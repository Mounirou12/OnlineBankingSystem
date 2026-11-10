package main.vue.page;

import main.controller.Accounts;
import main.controller.Session;
import main.controller.Transactions;
import main.model.AccountActive;
import main.vue.LoginPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ClientTransfertPage extends JPanel {
    // --- Palette de couleurs ---
    private final Color TITLE_BG = new Color(0x00, 0x3B, 0x8A);
    private final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x1A);
    private final Color FIELD_BG = new Color(0xEA, 0xF0, 0xFA);
    private final Color BORDER_COLOR = new Color(0xB0, 0xC4, 0xDE);
    private final Color BTN_PRIMARY = new Color(0x00, 0x88, 0xFF);
    private final Color BTN_DANGER = new Color(0xDC, 0x35, 0x45);

    // Champs du formulaire (déclarés en global pour y accéder dans le bouton)
    private JComboBox<AccountActive> cbFromAccount;
    private PlaceholderTextField txtRecipient;
    private PlaceholderTextField txtAmount;

    public ClientTransfertPage() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF4, 0xF7, 0xFB));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createMainHeader(), BorderLayout.NORTH);
        add(createDetailsCard(), BorderLayout.CENTER);
    }

    private JPanel createMainHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void printComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TITLE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 15, 15);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel iconLabel = new JLabel("💸");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        iconLabel.setForeground(Color.WHITE);

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(new Color(255, 255, 255, 100));
        separator.setPreferredSize(new Dimension(1, 40));

        JLabel title = new JLabel("Effectuer un transfert");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.BLACK); // Corrigé : le texte doit être blanc sur fond bleu

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel);
        leftPanel.add(separator);
        leftPanel.add(title);

        header.add(leftPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createDetailsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 0, 0, 0),
                BorderFactory.createLineBorder(new Color(0xD0, 0xE0, 0xF0), 1, true)));

        // --- Titre de la carte ---
        JPanel cardTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TITLE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 15, 15);
                g2.dispose();
            }
        };
        cardTitlePanel.setOpaque(false);

        JLabel infoIcon = new JLabel("ℹ️");
        infoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        infoIcon.setForeground(Color.WHITE);

        JLabel cardTitle = new JLabel("Nouveau Virement");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(Color.WHITE);

        cardTitlePanel.add(infoIcon);
        cardTitlePanel.add(cardTitle);
        card.add(cardTitlePanel, BorderLayout.NORTH);

        // --- Contenu (Formulaire) ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Compte Source
        int currentUserId = Session.getCurrentUserId();
        List<AccountActive> userAccounts = Accounts.getAllAccountsByUserIdAndType(currentUserId);

        cbFromAccount = new JComboBox<>();
        styleComboBox(cbFromAccount);
        cbFromAccount.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AccountActive) {
                    AccountActive acc = (AccountActive) value;
                    String rawNumber = acc.getAccountName(); // Adaptez selon votre méthode
                    String maskedNumber = "•••• " + rawNumber.substring(Math.max(0, rawNumber.length() - 4));
                    setText("Compte Principal (" + maskedNumber + ")");
                }
                return this;
            }
        });

        if (userAccounts != null && !userAccounts.isEmpty()) {
            for (AccountActive acc : userAccounts) {
                cbFromAccount.addItem(acc);
            }
        } else {
            cbFromAccount.addItem(null); // Géré dans l'action
        }

        addFormRow(gridPanel, gbc, 0, "💳", "Compte d'envoi :", cbFromAccount);

        // 2. Destinataire
        txtRecipient = new PlaceholderTextField("Entrez le numéro de compte");
        styleTextField(txtRecipient);
        addFormRow(gridPanel, gbc, 1, "👤", "Destinataire :", txtRecipient);

        // 3. Montant
        txtAmount = new PlaceholderTextField("Entrez le montant (ex: 150.50)");
        styleTextField(txtAmount);
        addFormRow(gridPanel, gbc, 2, "💰", "Montant :", txtAmount);

        contentPanel.add(gridPanel, BorderLayout.CENTER);

        // --- Boutons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(5, 30, 20, 30));

        JButton btnSend = createStyledButton("Envoyer le transfert", "✈️", BTN_PRIMARY);
        btnSend.addActionListener(e -> effectuerTransfert());

        buttonPanel.add(btnSend);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    // --- LOGIQUE MÉTIER DU BOUTON ---
    private void effectuerTransfert() {
        AccountActive selectedAccount = (AccountActive) cbFromAccount.getSelectedItem();
        if (selectedAccount == null) {
            JOptionPane.showMessageDialog(this, "Aucun compte source sélectionné.", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sourceAccountNumber = selectedAccount.getAccountName();
        String targetAccountNumber = txtRecipient.getText().trim();
        String amountStr = txtAmount.getText().trim();

        if (targetAccountNumber.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Montant invalide.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Appel de votre méthode métier
        boolean success = Transactions.transferMoney(sourceAccountNumber, targetAccountNumber, amount);

        if (success) {
            JOptionPane.showMessageDialog(this, "Transfert effectué avec succès !", "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            txtRecipient.setText("");
            txtAmount.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Échec du transfert. Vérifiez le compte destinataire et votre solde.",
                    "Échec", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MÉTHODES UTILITAIRES DE STYLE ---
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String icon, String labelText,
            JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLabel.setForeground(TITLE_BG);
        panel.add(iconLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        panel.add(label, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void styleTextField(PlaceholderTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_DARK);
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(TEXT_DARK);
        combo.setBackground(FIELD_BG);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private JButton createStyledButton(String text, String icon, Color bgColor) {
        JButton button = new JButton(icon + " " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
                button.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.repaint();
            }
        });
        return button;
    }
}
