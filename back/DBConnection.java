import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnection {

    private static Connection instance;

    private DBConnection() throws SQLException {

        // creation de la connection
        Properties connectionProps = new Properties();
        String fileName = "db.conf";

        try (FileInputStream fis = new FileInputStream(fileName)){
            connectionProps.load(fis);
        } catch (IOException e){
            System.out.println("Erreur de lecture du fichier de config");
            e.printStackTrace();;
        }
        /*
        connectionProps.put("user", user);
        connectionProps.put("password", mdp);
        String urlDB = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
         */
        DBConnection.instance = DriverManager.getConnection(connectionProps.getProperty("urlDB"), connectionProps);
        instance.setAutoCommit(false);
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (DBConnection.instance == null){
            new DBConnection();
        }
        return DBConnection.instance;
    }
}
