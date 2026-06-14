import com.google.gson.Gson;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCentral implements ServiceRMI {

    @Override
    public String getCoordonnees() throws RemoteException {
        try {
            Connection connection = DBConnection.getConnection();
            Statement statm = connection.createStatement();
            statm.executeQuery("SELECT * FROM RESTAURANT");
            ResultSet rs = statm.getResultSet();
            List<Restaurant> liste = new ArrayList<>();
            while (rs.next()) {
                double longitude = rs.getDouble("LONGITUDE");
                double latitude = rs.getDouble("LATITUDE");
                int id = rs.getInt("ID_RESTAURANT");
                String nom = rs.getString("NOM");
                String adresse = rs.getString("ADRESSE");
                liste.add(new Restaurant(id, nom, adresse, latitude, longitude));
            }
            statm.close();
            Gson gson = new Gson();
            return gson.toJson(liste.toArray(new Restaurant[0]));
        } catch (SQLException e) {
            throw new RemoteException("Erreur BDD getCoordonnees : " + e.getMessage());
        }
    }

    @Override
    public String reserverTable(int idRestau, String date, String periode, int nbrPersonnes, String prenom, String nom, String telephone) throws RemoteException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            PreparedStatement statm = connection.prepareStatement("""
                SELECT ID_TABLE
                FROM TABLE_RESTO
                WHERE ID_RESTAURANT = ? AND CAPACITE_MAX >= ?
                MINUS
                SELECT ID_TABLE
                FROM RESERVATION
                WHERE DATE_HEURE_RESERVATION = TO_DATE(?,'DD-MM-YYYY') AND PERIODE = ?
            """);
            statm.setInt(1, idRestau);
            statm.setInt(2, nbrPersonnes);
            statm.setString(3, date);
            statm.setString(4, periode);
            statm.execute();
            ResultSet rs = statm.getResultSet();

            String json;
            if (rs.next()) {
                int idTable = rs.getInt(1);
                PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO RESERVATION (ID_TABLE, NOM_CLIENT, PRENOM_CLIENT, TELEPHONE, DATE_HEURE_RESERVATION, NB_CONVIVES, PERIODE)
                    VALUES (?, ?, ?, ?, TO_DATE(?, 'DD-MM-YYYY'), ?, ?)
                """);
                ps.setInt(1, idTable);
                ps.setString(2, nom);
                ps.setString(3, prenom);
                ps.setString(4, telephone);
                ps.setString(5, date);
                ps.setInt(6, nbrPersonnes);
                ps.setString(7, periode);
                ps.execute();
                connection.commit();
                statm.close();
                ps.close();
                json = "{\"response\":\"OK\"}";
            } else {
                connection.commit();
                statm.close();
                json = "{\"response\":\"FAILURE\"}";
            }
            return json;
        } catch (SQLException e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ignored) {}
            throw new RemoteException("Erreur BDD reserverTable : " + e.getMessage());
        }
    }
}
