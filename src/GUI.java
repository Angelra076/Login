import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static com.mysql.cj.conf.PropertyKey.PASSWORD;

public class GUI extends Component {
    private JFrame frame;
    private JTextField txtNombreUsuario;
    private JPasswordField txtContrasena;
    private JLabel lblMensaje;
    private Connection conexion;

    public GUI() {
        // Establecer la conexión a la base de datos
        conexion = DatabaseConnection.getInstance();

        // Crear la ventana principal
        frame = new JFrame("Login de Usuarios");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(0, 2));

        // Crear componentes de la GUI
        JLabel lblUsuario = new JLabel("Usuario:");
        txtNombreUsuario = new JTextField();
        JLabel lblContrasena = new JLabel("Contraseña:");
        txtContrasena = new JPasswordField();
        JButton btnIniciarSesion = new JButton("Iniciar Sesión");
        JButton btnRegistrarse = new JButton("Registrarse");
        JButton btnVerUsuarios = new JButton("Ver Usuarios");
        lblMensaje = new JLabel("");
        lblMensaje.setForeground(Color.RED);

        // Agregar componentes a la ventana
        frame.add(lblUsuario);
        frame.add(txtNombreUsuario);
        frame.add(lblContrasena);
        frame.add(txtContrasena);
        frame.add(btnIniciarSesion);
        frame.add(btnRegistrarse);
        frame.add(btnVerUsuarios);
        frame.add(lblMensaje);

        // Configurar eventos de botones
        btnIniciarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSesion();
            }
        });

        btnRegistrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarFormularioRegistro();
            }
        });

        btnVerUsuarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarListaUsuarios();
            }
        });
    }

    public void iniciar() {
        // Mostrar la ventana
        frame.setVisible(true);
    }

    private void iniciarSesion() {
        String nombreUsuario = txtNombreUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());

        if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Debe ingresar su usuario y contraseña.");
            return;
        }

        try {
            // Consultar la base de datos para verificar las credenciales
            PreparedStatement statement = conexion.prepareStatement("SELECT * FROM logi WHERE nombre_usuario = ? AND contrasena = ?");
            statement.setString(1, nombreUsuario);
            statement.setString(2, contrasena);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                lblMensaje.setText("");
                // Cerrar la ventana de login y mostrar el GUI principal
                frame.dispose();
                // Crear y mostrar el GUI principal
                GUI gui = new GUI();
                gui.iniciar();
            } else {
                lblMensaje.setText("Usuario o contraseña incorrectos.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblMensaje.setText("Error al iniciar sesión.");
        }
    }

    private void mostrarFormularioRegistro() {
        JFrame registroFrame = new JFrame("Registro de Usuario");
        registroFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registroFrame.setSize(400, 300);
        registroFrame.setLayout(new GridLayout(0, 2));

        // Implementa el formulario de registro aquí

        registroFrame.setVisible(true);
    }

    private void mostrarListaUsuarios() {
        // Crear un modelo de tabla para contener los datos
        DefaultTableModel model = new DefaultTableModel();
        // Añadir las columnas al modelo
        model.addColumn("Username");
        model.addColumn("Name");
        model.addColumn("Lastname");
        model.addColumn("Number");
        model.addColumn("Email");
        model.addColumn("Password");
        model.addColumn("Eliminar"); // Agregar columna para botón de eliminación

        try {
            // Consultar la base de datos para obtener los usuarios
            Connection connection = DriverManager.getConnection(DatabaseConnection.JDBC_URL, DatabaseConnection.USERNAME, String.valueOf(PASSWORD));
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT username, name, lastname, number, email, Password FROM logi");

            // Iterar sobre los resultados y agregarlos al modelo de tabla
            while (resultSet.next()) {
                // Crear un array para almacenar los datos de la fila
                Object[] row = new Object[]{
                        resultSet.getString("username"),
                        resultSet.getString("name"),
                        resultSet.getString("lastname"),
                        resultSet.getString("number"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        "Eliminar"
                };
                // Agregar la fila al modelo de tabla
                model.addRow(row);
            }

            // Cerrar la conexión y los recursos
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener la lista de usuarios: " + e.getMessage());
        }

        // Crear la tabla y establecer el modelo
        JTable table = new JTable(model);
// Agregar la columna con botones de eliminar
        new ButtonColumn(table, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int row = Integer.valueOf(e.getActionCommand());
                // Aquí colocas el código para eliminar el usuario de esa fila
            }
        }, 6);
        // Agregar el botón de eliminación a cada fila de la tabla
        ButtonColumn buttonColumn = new ButtonColumn(table, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.valueOf(e.getActionCommand());
                eliminarUsuario(row);
            }
        }, model.getColumnCount() - 1); // El botón se agregará en la última columna

        // Agregar la tabla a un JScrollPane para permitir el desplazamiento si hay muchos usuarios
        JScrollPane scrollPane = new JScrollPane(table);
        // Mostrar la tabla en un nuevo JFrame
        JFrame listaUsuariosFrame = new JFrame("Lista de Usuarios");
        listaUsuariosFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        listaUsuariosFrame.setSize(800, 600);
        listaUsuariosFrame.add(scrollPane);
        listaUsuariosFrame.setVisible(true);
    }

    private void eliminarUsuario(int row) {
        // Obtener el username de la fila seleccionada
        JTable table = new JTable();
        String username = (String) table.getValueAt(row, 0);
        // Realizar la eliminación en la base de datos
        try {
            Connection connection = DriverManager.getConnection(DatabaseConnection.JDBC_URL, DatabaseConnection.USERNAME, String.valueOf(PASSWORD));
            PreparedStatement statement = connection.prepareStatement("DELETE FROM logi WHERE username = ?");
            statement.setString(1, username);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
                mostrarListaUsuarios(); // Actualizar la lista de usuarios después de eliminar
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el usuario.");
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar el usuario: " + e.getMessage());
        }

    }




    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.iniciar();
    }
}
