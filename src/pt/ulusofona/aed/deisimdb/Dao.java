package pt.ulusofona.aed.deisimdb;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.print.DocFlavor.STRING;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;


public class Dao {
    String connectionUrl; 
    String sqlserver = "//localhost:1433";
    String databaseName="deisIMDB";
    String user="sa";
    String password="YourStrong!Passw0rd";//"ClientS@fe!Passw0rd";
    String encrypt="true";
    String trustServerCertificate="true";

    public Dao() {
        connectionUrl = "jdbc:sqlserver:" + sqlserver + 
                        ";databaseName=" + databaseName + 
                        ";user=" + user + 
                        ";password=" + password + 
                        ";encrypt=" + encrypt + 
                        ";trustServerCertificate=" + trustServerCertificate + ";";
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }    

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }
    
    @Override
    public String toString() {
        return  connectionUrl.toString();
    }

}
