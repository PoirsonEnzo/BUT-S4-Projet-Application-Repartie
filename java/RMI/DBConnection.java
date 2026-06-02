package RMI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnection {

    private static Connection instance;

    private DBConnection(String user, String mdp) throws SQLException {

        // creation de la connection
        Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", mdp);
        String urlDB = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
        DBConnection.instance = DriverManager.getConnection(urlDB, connectionProps);
        //instance.setAutoCommit(false);
    }

    public static synchronized Connection getConnection() throws SQLException {
        return DBConnection.getConnection("","");
    }

    public static synchronized Connection getConnection(String user, String mdp) throws SQLException {
        if (DBConnection.instance == null){
            new DBConnection(user,mdp);
        }
        return DBConnection.instance;
    }






}
