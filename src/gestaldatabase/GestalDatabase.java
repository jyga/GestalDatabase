package gestaldatabase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class GestalDatabase {

    public final String dbDir = (System.getProperty("user.dir") + File.separator + "GestalDB");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new GestalDatabase();
    }

    public GestalDatabase() {
        try {
            //invoke driver instance
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();

            //create database connection, providing path to database
            //here it is constructed based on program execution location
            //default Gestal installation database is in folder:
            //C:\Program Files (x86)\JYGA Technologies\Gestal\GestalDB
            //then is added the database name "GestalSS" and user name "guest"
            Connection connection = DriverManager.getConnection(
                    "jdbc:derby:" + dbDir + File.separator + "GestalSS;user=guest");

            //create a statement object to execute SQL commands
            Statement statement = connection.createStatement();

            //obtain a result set from a SQL command
            ResultSet resultSet = statement.executeQuery("SELECT * FROM APP.TRUIE");

            //parse the result set and fetch data
            while (resultSet.next()) {
                System.out.println("Truie: " + resultSet.getString("NomTruie"));
            }

            //close the result set
            resultSet.close();
            
            //example retrieving all table names
            resultSet = connection.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
            while (resultSet.next()) {
                System.out.println("existing table: " + resultSet.getString("TABLE_NAME"));
            }

            resultSet.close();

            //this command would fail du to read-only user rights
            //statement.execute("UPDATE APP.TRUIE SET NomTruie='Sow' WHERE ID=1");
            
            //close statement
            statement.close();

            //close connection
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
