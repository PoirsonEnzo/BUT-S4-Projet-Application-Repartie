package RMI;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceCentral implements ServiceRMI {

    List<String> listeClients = new ArrayList<>();

    @Override
    public void enregisterClient() throws RemoteException {

    }

    @Override
    public String getCoordonnees() throws RemoteException {
        try {
            Connection connection = DBConnection.getConnection();
            connection.createStatement().execute("""
        SELECT * FROM restaurants 
""");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";

    }

    @Override
    public void reserverTable(int id, String prenom, String nom, int nbrPersonnes, String telephone) throws RemoteException {
        try {
            Connection connection = DBConnection.getConnection();
            connection.prepareStatement("""
            INSERT INTO reservation VALUES ()
""");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
