package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceRMI extends Remote {

    void enregisterClient() throws RemoteException;

    String getCoordonnees() throws RemoteException;

    void reserverTable(int idTable, String prenom, String nom, int nbrPersonnes, String telephone) throws RemoteException;
}
