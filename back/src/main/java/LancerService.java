import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerService {

    public static void main(String[] args) throws RemoteException {
        ServiceCentral service = new ServiceCentral();
        ServiceRMI rmi = (ServiceRMI) UnicastRemoteObject.exportObject(service,0);
        Registry reg = LocateRegistry.createRegistry(1099);
        reg.rebind("BDDRestaurant",rmi);
        System.out.println("Service activé");
    }
}
