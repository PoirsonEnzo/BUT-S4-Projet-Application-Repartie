import java.rmi.RemoteException;
import java.sql.*;
import java.lang.String;

public class ServiceCentral implements ServiceRMI {

    @Override
    public void enregisterClient() throws RemoteException {

    }

    @Override
    public String getCoordonnees() throws RemoteException {
        StringBuilder json = new StringBuilder("{coords:[");
        try {
            Connection connection = DBConnection.getConnection();
            Statement statm = connection.createStatement();
            statm.execute("""
        SELECT coordX, coordY FROM restaurants 
            """);
            //TODO Rajouter nom et id
            ResultSet rs = statm.getResultSet();
            while (rs.next()){
                int x = rs.getInt(1);
                int y = rs.getInt(2);
                json.append("[").append(x).append(",").append(y).append("],");
            }
            json.deleteCharAt(json.length()-1);
            json.append("]}");
            return json.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void reserverTable(int id, String prenom, String nom, int nbrPersonnes, String telephone,String date) throws RemoteException {
        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("""
            INSERT INTO reservation VALUES (?,?,?,?,?,TO_DATE(?, 'DD-MM-YYYY'))
""");
            ps.setInt(1,id);
            ps.setString(2,prenom);
            ps.setString(3,nom);
            ps.setInt(4,nbrPersonnes);
            ps.setString(5,telephone);
            ps.setString(6,date);

            ps.execute();
            connection.commit();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTable(int id) throws RemoteException{
        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("""
            SELECT * FROM tables WHERE id = ?;
""");
            ps.setInt(1,id);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            StringBuilder json = new StringBuilder("{ table : {");
            while (rs.next()){
                //TODO CREER TABLE
            }
            json.append("}}");
            return json.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
