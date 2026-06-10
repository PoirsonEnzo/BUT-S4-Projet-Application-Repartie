import com.google.gson.Gson;

import java.rmi.RemoteException;
import java.sql.*;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class ServiceCentral implements ServiceRMI {


    @Override
    public String getCoordonnees() throws RemoteException {
        StringBuilder json = new StringBuilder("{restaurants:[");
        try {
            Connection connection = DBConnection.getConnection();
            Statement statm = connection.createStatement();
            statm.executeQuery("""
            SELECT * FROM RESTAURANT 
                """);
            ResultSet rs = statm.getResultSet();
            List<Restaurant> liste = new ArrayList<>();


            while (rs.next()){

                double longitude = rs.getDouble("LONGITUDE");
                double latitude = rs.getDouble("LATITUDE");
                int id = rs.getInt("ID_RESTAURANT");
                String nom = rs.getString("NOM");
                String adresse = rs.getString("ADRESSE");
                liste.add(new Restaurant(id,nom,adresse,latitude,longitude));
            }
            statm.close();
            Restaurant[] restaurants = liste.toArray(new Restaurant[0]);
            Gson gson = new Gson();

            return gson.toJson(restaurants);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String reserverTable(int idRestau, String date, String periode, int nbrPersonnes, String prenom, String nom, String telephone) throws RemoteException, SQLException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();

            // Recherche des tables disponibles pour un restaurant, un nombre de personnes et un créneau donné
            PreparedStatement statm = connection.prepareStatement("""
            SELECT ID_TABLE 
            FROM TABLE_RESTO
            WHERE ID_RESTAURANT = ? AND CAPACITE_MAX <= ?
            ORDER BY CAPACITE_MAX
            MINUS 
            SELECT ID_TABLE
            FROM RESERVATION
            WHERE DATE_RESERVATION = TO_DATE(?,'DD-MM-YYYY') AND PERIODE = ?
            FOR UPDATE
""");
            statm.setInt(1,idRestau);
            statm.setInt(2,nbrPersonnes);
            statm.setString(3,date);
            statm.setString(4,periode);

            statm.execute();
            ResultSet rs = statm.getResultSet();

            String json;
            // Ligne présente = Réservation disponible
            if (rs.next()){
                int idTable = rs.getInt(1);

                // Ajout de la réservation
                PreparedStatement ps = connection.prepareStatement("""
                INSERT INTO RESERVATION (ID_TABLE, NOM_CLIENT, PRENOM_CLIENT, TELEPHONE, DATE_RESERVATION,NB_CONVIVES,EST_MIDI) 
                VALUES (?,?,?,?,TO_DATE(?, 'DD-MM-YYYY'),?,?)
                """);
                ps.setInt(1,idTable);
                ps.setString(2,nom);
                ps.setString(3,prenom);
                ps.setString(4,telephone);
                ps.setString(5,date);
                ps.setInt(6,nbrPersonnes);
                ps.setString(7,periode);
                ps.execute();

                connection.commit();

                statm.close();

                json = "{'response' : 'OK'}";
            } else {
                connection.commit();
                statm.close();
                json = "{'response' : 'FAILURE'}";

            }
            return json;

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw new RuntimeException(e);
        }
    }

}
