import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface ServiceRMI extends Remote {

    String getCoordonnees() throws RemoteException;

    String reserverTable(int idRestau, String date, String periode, int nbrPersonnes, String prenom, String nom, String telephone) throws RemoteException, SQLException;
}
