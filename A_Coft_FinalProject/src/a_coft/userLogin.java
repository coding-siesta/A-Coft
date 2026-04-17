package a_coft;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class userLogin {


    public static void loginModule() {


        JFrame frame = new JFrame("A-COFT Restaurant");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());


        // COLORS
        Color espresso = new Color(54, 38, 37);
        Color cream = new Color(245, 245, 220);
        Color goldAccent = new Color(193, 154, 107);
        Color textLight = new Color(255, 255, 255);


        // TOP PANEL
        JPanel topPanel = new JPanel();
        topPanel.setBackground(espresso);
        topPanel.setPreferredSize(new Dimension(600, 100));
        topPanel.setLayout(new GridBagLayout());


        JLabel title = new JLabel("A-COFT RESTAURANT");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(goldAccent);


        topPanel.add(title);


        // CENTER PANEL
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 1, 15, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 120, 30, 120));
        centerPanel.setBackground(cream);


        JLabel subtitle = new JLabel("Welcome! Please choose an option", JLabel.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(espresso);


        JButton registerBtn = createStyledButton("Register", espresso, textLight);
        JButton loginBtn = createStyledButton("Login", espresso, textLight);
        JButton exitBtn = createStyledButton("Exit", espresso, textLight);


        centerPanel.add(subtitle);
        centerPanel.add(registerBtn);
        centerPanel.add(loginBtn);
        centerPanel.add(exitBtn);


        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);


        frame.setVisible(true);


        // REGISTER
        registerBtn.addActionListener(e -> {


            JTextField emailField = new JTextField();
            JPasswordField passField = new JPasswordField();
            JTextField confirmEmailField = new JTextField();
            JPasswordField confirmPassField = new JPasswordField();


            Object[] message = {
                    "Enter Email:", emailField,
                    "Enter Password:", passField,
                    "Confirm Email:", confirmEmailField,
                    "Confirm Password:", confirmPassField
            };


            int option = JOptionPane.showConfirmDialog(frame, message, "Register", JOptionPane.OK_CANCEL_OPTION);


            if (option == JOptionPane.OK_OPTION) {
                String email = emailField.getText();
                String pass = new String(passField.getPassword());
                String confirmEmail = confirmEmailField.getText();
                String confirmPass = new String(confirmPassField.getPassword());

                if (email.equals(confirmEmail) && pass.equals(confirmPass)) {

                    try {
                        Connection con = DBConnection.getConnection();

                        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
                        PreparedStatement pst = con.prepareStatement(sql);
                        pst.setString(1, email);
                        pst.setString(2, pass);

                        pst.executeUpdate();

                        JOptionPane.showMessageDialog(frame, "Account Created!");

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else {
                    JOptionPane.showMessageDialog(frame, "Not matched! Try again.");
                }
            }
        });


        // LOGIN
        loginBtn.addActionListener(e -> {

            JTextField emailField = new JTextField();
            JPasswordField passField = new JPasswordField();

            Object[] message = {
                    "Enter Email:", emailField,
                    "Enter Password:", passField
            };

            int option = JOptionPane.showConfirmDialog(frame, message, "Login", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String email = emailField.getText();
                String pass = new String(passField.getPassword());

                try {
                    Connection con = DBConnection.getConnection();

                    String sql = "SELECT * FROM users WHERE email=? AND password=?";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setString(1, email);
                    pst.setString(2, pass);

                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(frame, "Login Successful!");
                        JOptionPane.showMessageDialog(frame, "Proceeding to the Main Menu...");
                        frame.dispose();
                        new View_EditReservation().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Wrong username or password.");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        // EXIT
        exitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Grazie!, Closing Application...");
            frame.dispose();
        });
    }


    private static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    public static void main(String[] args) {
        loginModule();
    }
}



