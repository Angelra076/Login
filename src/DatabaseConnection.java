import java.sql.*;

public class DatabaseConnection {
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/log";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";
    public static Connection connection = null;

    private DatabaseConnection() {
        // Constructor privado para evitar instanciación directa
    }

    public static Connection getInstance() {
        if (connection == null) {
            try {
                // Establecer la conexión si aún no existe
                connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                System.out.println("Conexión exitosa a la base de datos.");
            } catch (SQLException e) {
                System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            }
        }
        return connection;
    }

    // Método para cerrar la conexión
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
