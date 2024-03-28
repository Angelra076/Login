import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static com.mysql.cj.conf.PropertyKey.PASSWORD;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginForm() {
        setTitle("Login Form");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        add(usernameLabel);

        usernameField = new JTextField();
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        add(passwordLabel);

        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        add(loginButton);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton = new JButton("Register");
        add(registerButton);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterForm();
            }
        });

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Verificar si los campos están llenos
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar su usuario y contraseña.");
            return;
        }

        // Verificar la autenticación del usuario en la base de datos
        try {
            Connection connection = DriverManager.getConnection(DatabaseConnection.JDBC_URL, DatabaseConnection.USERNAME, String.valueOf(PASSWORD));
            String query = "SELECT * FROM logi WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Si hay una coincidencia en la base de datos, el usuario se autentica correctamente
                // Abre la nueva ventana aquí
                openNewWindow();
                // Cierra la ventana actual de inicio de sesión
                dispose();
                JOptionPane.showMessageDialog(this, "Login exitoso!");
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.");
            }

            connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al autenticar usuario: " + e.getMessage());
        }
    }

    private void openNewWindow() {
        // Crea una instancia del GUI principal
        GUI gui = new GUI();
        // Muestra el GUI principal
        gui.iniciar();
        // Cierra la ventana actual de inicio de sesión
        setVisible(false); // O dispose(), dependiendo de tus necesidades
    }

    private void openRegisterForm() {
        RegisterForm registerForm = new RegisterForm(this);
        registerForm.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}


class RegisterForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField lastNameField;
    private JTextField phoneNumberField;
    private JTextField emailField;
    private JButton registerButton;

    public RegisterForm(JFrame parent) {
        setTitle("Register Form");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(7, 2));

        JLabel usernameLabel = new JLabel("Username:");
        add(usernameLabel);

        usernameField = new JTextField();
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        add(passwordLabel);

        passwordField = new JPasswordField();
        add(passwordField);

        JLabel nameLabel = new JLabel("Name:");
        add(nameLabel);

        nameField = new JTextField();
        add(nameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        add(lastNameLabel);

        lastNameField = new JTextField();
        add(lastNameField);

        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        add(phoneNumberLabel);

        phoneNumberField = new JTextField();
        add(phoneNumberField);

        JLabel emailLabel = new JLabel("Email:");
        add(emailLabel);

        emailField = new JTextField();
        add(emailField);

        registerButton = new JButton("Register");
        add(registerButton);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        // Centrar la ventana de registro en relación con la ventana principal de inicio de sesión
        setLocationRelativeTo(parent);
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText();
        String lastName = lastNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();

        // Verificar si todos los campos están llenos
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        // Verificar si la contraseña y la confirmación coinciden
        if (!password.equals(passwordField.getText())) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
            return;
        }

        // Realizar la inserción en la base de datos
        try {
            Connection connection = DriverManager.getConnection(DatabaseConnection.JDBC_URL, DatabaseConnection.USERNAME, String.valueOf(PASSWORD));
            String query = "INSERT INTO logi (username, password, name, lastname, number, email) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, name);
            statement.setString(4, lastName);
            statement.setString(5, phoneNumber);
            statement.setString(6, email);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");
            }

            connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + e.getMessage());
        }
    }

}


