package filmorate;

import java.sql.Connection;
import java.sql.DriverManager;

public class H2Test {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:h2:file:C:/Users/lexaz/dev/hw10/db/filmorate;AUTO_SERVER=TRUE";
        String user = "sa";
        String password = "";

        // Принудительно грузим драйвер
        Class.forName("org.h2.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Connected successfully to H2 database!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
