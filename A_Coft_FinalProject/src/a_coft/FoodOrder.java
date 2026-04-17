package a_coft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class FoodOrder extends JFrame implements ActionListener {

    // COLORS
    static Color espresso = new Color(54, 38, 37);
    static Color cream = new Color(245, 245, 220);
    static Color goldAccent = new Color(193, 154, 107);

    // MENU
    String[][] menuItems = {
            {"Main Course", "Chicken Parmigiana", "Breaded chicken, marinara sauce & melted mozzarella with vegetables", "320"},
            {"Main Course", "Osso Buco", "Slow-braised beef shank with herbs & rich sauce", "480"},
            {"Main Course", "Margherita Pizza", "Tomato sauce, fresh mozzarella & basil", "260"},
            {"Pastas", "Spaghetti Carbonara", "Creamy pasta with eggs, cheese, pancetta & black pepper", "220"},
            {"Pastas", "Fettuccine Alfredo", "Pasta tossed in rich, creamy parmesan sauce", "200"},
            {"Pastas", "Penne Arrabbiata", "Spicy tomato sauce with garlic & chili", "190"},
            {"Beverages", "Italian Soda", "Sparkling drink with flavored syrup", "90"},
            {"Beverages", "Espresso", "Strong and rich Italian coffee", "80"},
            {"Beverages", "Limoncello", "Sweet lemon-flavored Italian drink", "110"},
    };

    JButton btnOrder, btnUpdate, btnBack;
    Map<String, Integer> cart = new LinkedHashMap<>();

    public FoodOrder() {

        setTitle("A-COFT Food Order");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cream);

        // HEADER
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(espresso);
        headerPanel.setPreferredSize(new Dimension(600, 35));

        JLabel title = new JLabel("A-COFT Restaurant Food Order");
        title.setFont(new Font("Serif", Font.BOLD, 26));
        title.setForeground(goldAccent);

        headerPanel.add(title);
        panel.add(headerPanel);

        // BODY
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(cream);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 70));

        btnOrder = makeBtn("Add Order");
        btnUpdate = makeBtn("Update Order");
        JButton btnPayout = makeBtn("Proceed to Payment");
        btnBack = makeBtn("Back");

        btnOrder.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnBack.addActionListener(this);
        btnPayout.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No order yet! Please add an order first.");
            } else {
                new Payout(cart);
                this.dispose();
            }
        });

        // BUTTONS
        bodyPanel.add(btnOrder);
        bodyPanel.add(Box.createVerticalStrut(12));
        bodyPanel.add(btnUpdate);
        bodyPanel.add(Box.createVerticalStrut(12));
        bodyPanel.add(btnPayout);
        bodyPanel.add(Box.createVerticalStrut(12));
        bodyPanel.add(btnBack);

        panel.add(bodyPanel);

        add(panel);
        setVisible(true);
    }

    // BUTTON STYLE
    JButton makeBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setBackground(espresso);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);

        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(320, 48));
        b.setPreferredSize(new Dimension(320, 48));

        b.setBorder(BorderFactory.createLineBorder(goldAccent, 1));
        return b;
    }

    JButton makeQtyBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(espresso);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(26, 26));
        b.setMinimumSize(new Dimension(26, 26));
        b.setMaximumSize(new Dimension(26, 26));
        b.setMargin(new Insets(0, 0, 0, 0));
        return b;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOrder) {
            openOrderWindow(false);
        } else if (e.getSource() == btnUpdate) {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No order yet! Please add an order first.");
            } else {
                openOrderWindow(true);
            }
        } else if (e.getSource() == btnBack) {
            new View_EditReservation().setVisible(true);
            this.dispose();
        }
    }

    // ORDER WINDOW
    void openOrderWindow(boolean isUpdate) {

        Map<String, Integer> tempCart = new LinkedHashMap<>();
        if (isUpdate) tempCart.putAll(cart);

        JDialog dialog = new JDialog(this,
                isUpdate ? "Update Order" : "Add Order",
                true);

        dialog.setSize(750, 580);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(cream);

        // HEADER
        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(espresso);
        dialogHeader.setPreferredSize(new Dimension(750, 35));

        JLabel header = new JLabel("A-COFT MENU", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 20));
        header.setForeground(goldAccent);

        dialogHeader.add(header, BorderLayout.CENTER);
        mainPanel.add(dialogHeader, BorderLayout.NORTH);

        // LIST AREA
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(cream);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel cartLabel = new JLabel("Cart: 0 item(s)  |  Total: P0");
        cartLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        cartLabel.setForeground(espresso);

        Runnable updateCart = () -> {
            int total = 0;
            int count = 0;

            for (Map.Entry<String, Integer> entry : tempCart.entrySet()) {
                count += entry.getValue();
                for (String[] item : menuItems) {
                    if (item[1].equals(entry.getKey())) {
                        total += entry.getValue() * Integer.parseInt(item[3]);
                    }
                }
            }
            cartLabel.setText("Cart: " + count + " item(s)  |  Total: P" + total);
        };

        for (String[] item : menuItems) {

            String category = item[0];
            String name = item[1];
            String desc = item[2];
            int price = Integer.parseInt(item[3]);

            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(cream);
            row.setBorder(BorderFactory.createEmptyBorder(6, 5, 6, 5));

            JPanel left = new JPanel(new GridLayout(2, 1));
            left.setBackground(cream);

            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            nameLabel.setForeground(espresso);

            JLabel descLabel = new JLabel(desc);
            descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            descLabel.setForeground(Color.DARK_GRAY);

            left.add(nameLabel);
            left.add(descLabel);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 10));
            right.setBackground(cream);

            JLabel priceLabel = new JLabel("P" + price);
            priceLabel.setForeground(goldAccent);

            int startQty = tempCart.getOrDefault(name, 0);
            JLabel qtyLabel = new JLabel(String.valueOf(startQty));

            JButton minus = makeQtyBtn("-");
            JButton plus = makeQtyBtn("+");

            minus.addActionListener(ev -> {
                int q = tempCart.getOrDefault(name, 0);
                if (q > 0) {
                    q--;
                    if (q == 0) tempCart.remove(name);
                    else tempCart.put(name, q);
                    qtyLabel.setText(String.valueOf(q));
                    updateCart.run();
                }
            });

            plus.addActionListener(ev -> {
                int q = tempCart.getOrDefault(name, 0) + 1;
                tempCart.put(name, q);
                qtyLabel.setText(String.valueOf(q));
                updateCart.run();
            });

            right.add(priceLabel);
            right.add(minus);
            right.add(qtyLabel);
            right.add(plus);

            row.add(left, BorderLayout.CENTER);
            row.add(right, BorderLayout.EAST);

            listPanel.add(row);
        }

        updateCart.run();

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // FOOTER
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(espresso);
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JButton back = new JButton("Back");
        back.setBackground(goldAccent);
        back.setForeground(espresso);

        JButton confirm = new JButton("Confirm");
        confirm.setBackground(goldAccent);
        confirm.setForeground(espresso);

        back.addActionListener(ev -> dialog.dispose());

        confirm.addActionListener(ev -> {
            if (tempCart.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No items selected! Please add items to your order.");
                return;
            }
            cart.clear();
            cart.putAll(tempCart);
            JOptionPane.showMessageDialog(dialog, "Order saved successfully!");
            dialog.dispose();
        });

        bottom.add(back, BorderLayout.WEST);
        bottom.add(cartLabel, BorderLayout.CENTER);
        bottom.add(confirm, BorderLayout.EAST);

        mainPanel.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    // TEST RUN
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FoodOrder());
    }
}