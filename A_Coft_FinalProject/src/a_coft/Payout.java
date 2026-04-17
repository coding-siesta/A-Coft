package a_coft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class Payout extends JFrame implements ActionListener {

    static Color espresso = new Color(54, 38, 37);
    static Color cream = new Color(245, 245, 220);
    static Color goldAccent = new Color(193, 154, 107);

    Map<String, Integer> cart;

    String[][] menuItems = {
            {"Main Course", "Chicken Parmigiana", "320"},
            {"Main Course", "Osso Buco", "480"},
            {"Main Course", "Margherita Pizza", "260"},
            {"Pastas", "Spaghetti Carbonara", "220"},
            {"Pastas", "Fettuccine Alfredo", "200"},
            {"Pastas", "Penne Arrabbiata", "190"},
            {"Beverages", "Italian Soda", "90"},
            {"Beverages", "Espresso", "80"},
            {"Beverages", "Limoncello", "110"},
    };

    String selectedPayment = null;

    JButton btnGCash, btnPayInStore, btnConfirm, btnCancel;
    JPanel summaryPanel;
    JLabel totalLabel;

    public Payout(Map<String, Integer> cart) {
        this.cart = cart;

        setTitle("A-COFT Payout");
        setSize(600, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(cream);

        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(espresso);
        headerPanel.setPreferredSize(new Dimension(600, 35));

        JLabel titleLabel = new JLabel("A-COFT Restaurant — Payout");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(goldAccent);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ================= BODY =================
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(cream);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 40, 18, 40));

        // --- ORDER SUMMARY SECTION ---
        JLabel summaryTitle = new JLabel("Order Summary");
        summaryTitle.setFont(new Font("Serif", Font.BOLD, 17));
        summaryTitle.setForeground(espresso);
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(summaryTitle);
        bodyPanel.add(Box.createVerticalStrut(8));

        // Summary list panel
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(new Color(235, 230, 210));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(goldAccent, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        populateSummary();

        JScrollPane summaryScroll = new JScrollPane(summaryPanel);
        summaryScroll.setBorder(null);
        summaryScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        summaryScroll.setPreferredSize(new Dimension(500, 160));
        bodyPanel.add(summaryScroll);

        bodyPanel.add(Box.createVerticalStrut(10));

        // Total
        int total = computeTotal();
        totalLabel = new JLabel("Total:  P" + total);
        totalLabel.setFont(new Font("Serif", Font.BOLD, 16));
        totalLabel.setForeground(espresso);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(totalLabel);

        bodyPanel.add(Box.createVerticalStrut(20));

        // --- PAYMENT OPTIONS SECTION ---
        JSeparator sep = new JSeparator();
        sep.setForeground(goldAccent);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(sep);
        bodyPanel.add(Box.createVerticalStrut(14));

        JLabel paymentTitle = new JLabel("Select Payment Method");
        paymentTitle.setFont(new Font("Serif", Font.BOLD, 17));
        paymentTitle.setForeground(espresso);
        paymentTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(paymentTitle);
        bodyPanel.add(Box.createVerticalStrut(10));

        JPanel paymentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        paymentRow.setBackground(cream);
        paymentRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnGCash = makePayBtn("GCash");
        btnPayInStore = makePayBtn("Pay at the Restaurant");

        btnGCash.addActionListener(this);
        btnPayInStore.addActionListener(this);

        paymentRow.add(btnGCash);
        paymentRow.add(btnPayInStore);
        bodyPanel.add(paymentRow);

        bodyPanel.add(Box.createVerticalStrut(24));

        // --- CONFIRM / CANCEL BUTTONS ---
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(goldAccent);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(sep2);
        bodyPanel.add(Box.createVerticalStrut(16));

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionRow.setBackground(cream);
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnConfirm = makeBtn("Confirm Order");
        btnCancel = makeBtn("Cancel");

        btnConfirm.addActionListener(this);
        btnCancel.addActionListener(this);

        actionRow.add(btnConfirm);
        actionRow.add(btnCancel);
        bodyPanel.add(actionRow);

        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    void populateSummary() {
        summaryPanel.removeAll();

        if (cart == null || cart.isEmpty()) {
            JLabel empty = new JLabel("No items in order.");
            empty.setFont(new Font("SansSerif", Font.ITALIC, 12));
            empty.setForeground(Color.GRAY);
            summaryPanel.add(empty);
            return;
        }

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue();
            int price = getPriceFor(name);

            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(new Color(235, 230, 210));
            row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

            JLabel itemLabel = new JLabel(qty + "x  " + name);
            itemLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            itemLabel.setForeground(espresso);

            JLabel priceLabel = new JLabel("P" + (qty * price));
            priceLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            priceLabel.setForeground(goldAccent);

            row.add(itemLabel, BorderLayout.WEST);
            row.add(priceLabel, BorderLayout.EAST);
            summaryPanel.add(row);
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    int getPriceFor(String name) {
        for (String[] item : menuItems) {
            if (item[1].equals(name)) return Integer.parseInt(item[2]);
        }
        return 0;
    }

    int computeTotal() {
        int total = 0;
        if (cart == null) return 0;
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            total += entry.getValue() * getPriceFor(entry.getKey());
        }
        return total;
    }

    // ================= BUTTON STYLES =================
    JButton makeBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(espresso);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(180, 42));
        b.setBorder(BorderFactory.createLineBorder(goldAccent, 1));
        return b;
    }

    JButton makePayBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(cream);
        b.setForeground(espresso);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(200, 40));
        b.setBorder(BorderFactory.createLineBorder(espresso, 1));
        return b;
    }

    void setPaymentSelected(JButton selected, JButton other) {
        selected.setBackground(espresso);
        selected.setForeground(Color.WHITE);
        selected.setBorder(BorderFactory.createLineBorder(goldAccent, 2));
        other.setBackground(cream);
        other.setForeground(espresso);
        other.setBorder(BorderFactory.createLineBorder(espresso, 1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnGCash) {
            selectedPayment = "GCash";
            setPaymentSelected(btnGCash, btnPayInStore);

        } else if (e.getSource() == btnPayInStore) {
            selectedPayment = "Pay at the Restaurant";
            setPaymentSelected(btnPayInStore, btnGCash);

        } else if (e.getSource() == btnConfirm) {
            if (cart == null || cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your order is empty. Please add items first.");
                return;
            }
            if (selectedPayment == null) {
                JOptionPane.showMessageDialog(this, "Please select a payment method.");
                return;
            }

            // Build receipt message
            StringBuilder sb = new StringBuilder();
            sb.append("Order confirmed!\n\n");
            sb.append("Payment Method: ").append(selectedPayment).append("\n\n");
            sb.append("Items:\n");
            for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                int price = getPriceFor(entry.getKey());
                sb.append("  ").append(entry.getValue()).append("x ")
                        .append(entry.getKey()).append("  —  P")
                        .append(entry.getValue() * price).append("\n");
            }
            sb.append("\nTotal:  P").append(computeTotal());

            if (selectedPayment.equals("GCash")) {
                sb.append("\n\nYou will receive a GCash payment request shortly.");
            } else {
                sb.append("\n\nPlease proceed to the cashier to complete your payment.");
            }

            JOptionPane.showMessageDialog(this, sb.toString(), "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);

            // Clear cart after confirm
            cart.clear();
            this.dispose();

        } else if (e.getSource() == btnCancel) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cancel your order?",
                    "Cancel Order",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Returning to Food Menu...");
                this.dispose();
                new FoodOrder().setVisible(true);
            }
        }
    }

    // TEST RUN with Items
    public static void main(String[] args) {
        Map<String, Integer> testCart = new LinkedHashMap<>();
        testCart.put("Chicken Parmigiana", 2);
        testCart.put("Spaghetti Carbonara", 1);
        testCart.put("Espresso", 3);
        SwingUtilities.invokeLater(() -> new Payout(testCart));
    }
}