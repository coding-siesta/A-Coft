package a_coft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class View_EditReservation extends JFrame implements ActionListener {


    // COLORS
    static Color espresso = new Color(54, 38, 37);
    static Color cream = new Color(245, 245, 220);
    static Color goldAccent = new Color(193, 154, 107);
    static Color textLight = new Color(255, 255, 255);

    JButton btnCheck, btnSave, btnManage, btnFood, btnExit;

    public View_EditReservation() {

        setTitle("A-COFT Restaurant");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(espresso);
        headerPanel.setPreferredSize(new Dimension(400, 70));
        headerPanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("A-COFT RESTAURANT");
        titleLabel.setForeground(goldAccent);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        headerPanel.add(titleLabel);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(cream);
        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;


        // MENU
        JLabel welcomeLabel = new JLabel("Welcome Back! Please choose an option", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        welcomeLabel.setForeground(espresso);
        gbc.gridy = 0;
        contentPanel.add(welcomeLabel, gbc);

        Dimension btnSize = new Dimension(400, 45);
        Font btnFont = new Font("SansSerif", Font.BOLD, 14);

        btnCheck = createStyledButton("Check Table Availability", btnSize, btnFont);
        btnSave = createStyledButton("Save Reservation", btnSize, btnFont);
        btnManage = createStyledButton("View Reservations", btnSize, btnFont);
        btnFood = createStyledButton("Order Food", btnSize, btnFont);
        btnExit = createStyledButton("Exit", btnSize, btnFont);

        btnCheck.addActionListener(this);
        btnSave.addActionListener(this);
        btnManage.addActionListener(this);
        btnFood.addActionListener(this);
        btnExit.addActionListener(this);

        gbc.gridy = 1; contentPanel.add(btnCheck, gbc);
        gbc.gridy = 2; contentPanel.add(btnSave, gbc);
        gbc.gridy = 3; contentPanel.add(btnManage, gbc);
        gbc.gridy = 4; contentPanel.add(btnFood, gbc);
        gbc.gridy = 5; contentPanel.add(btnExit, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text, Dimension size, Font font) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(size);
        btn.setFont(font);
        btn.setBackground(espresso);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(goldAccent, 1));
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCheck) {
            checkAvailability();
        } else if (e.getSource() == btnSave) {
            saveReservation();
        } else if (e.getSource() == btnManage) {
            viewReservations();
        } else if (e.getSource() == btnFood) {
            JOptionPane.showMessageDialog(this,"Opening Food Menu...");
            new FoodOrder();
            this.dispose();
        } else if (e.getSource() == btnExit) {
            JOptionPane.showMessageDialog(this,"Grazie!, Closing Application...");
            System.exit(0);
        }
    }


    public void checkAvailability() {
        String input = JOptionPane.showInputDialog(this, "How many people (1-6)?");
        if (input == null) return;

        try {
            int pax = Integer.parseInt(input);
            StringBuilder result = new StringBuilder("Available Tables:\n\n");

            Connection con = DBConnection.getConnection();

            for (int i = 1; i <= 6; i++) {

                int cap = (i <= 2) ? 2 : (i <= 4) ? 4 : 6;

                if (pax <= cap) {
                    PreparedStatement pst = con.prepareStatement(
                            "SELECT COUNT(*) FROM reservations WHERE table_number=?"
                    );
                    pst.setInt(1, i);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next() && rs.getInt(1) == 0) {
                        result.append("Table ").append(i)
                                .append(" (Capacity ").append(cap).append(")\n");
                    }
                }
            }

            JOptionPane.showMessageDialog(this, result.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveReservation() {
        JTextField nameF = new JTextField();
        JTextField paxF = new JTextField();
        JTextField tableF = new JTextField();

        Object[] fields = {
                "Name:", nameF,
                "People:", paxF,
                "Table (1-6):", tableF
        };

        int opt = JOptionPane.showConfirmDialog(this, fields, "New Reservation", JOptionPane.OK_CANCEL_OPTION);

        if (opt == JOptionPane.OK_OPTION) {
            try {
                String name = nameF.getText();
                int pax = Integer.parseInt(paxF.getText());
                int table = Integer.parseInt(tableF.getText());

                int cap = (table <= 2) ? 2 : (table <= 4) ? 4 : 6;

                if (pax > cap) {
                    JOptionPane.showMessageDialog(this, "Table too small.");
                    return;
                }

                Connection con = DBConnection.getConnection();

                PreparedStatement pst = con.prepareStatement(
                        "INSERT INTO reservations(customer_name, table_number, number_of_people) VALUES (?, ?, ?)"
                );

                pst.setString(1, name);
                pst.setInt(2, table);
                pst.setInt(3, pax);

                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Reservation saved!");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void viewReservations() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM reservations");
            ResultSet rs = pst.executeQuery();

            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                        .append(" | Name: ").append(rs.getString("customer_name"))
                        .append(" | Table: ").append(rs.getInt("table_number"))
                        .append(" | Pax: ").append(rs.getInt("number_of_people"))
                        .append("\n");
            }

            JOptionPane.showMessageDialog(this, sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // TEST RUN
    public static void main(String[] args) {
        new View_EditReservation().setVisible(true);
    }
}