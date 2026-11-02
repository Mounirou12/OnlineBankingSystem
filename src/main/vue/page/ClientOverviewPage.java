package main.vue.page;

import main.controller.Accounts;
import main.controller.Session;
import main.controller.Transactions;
import main.controller.Users;
import main.model.AccountActive;

import javax.swing.*;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;


public class ClientOverviewPage extends JPanel {


    // Couleurs de la palette
    private final Color BG_COLOR = new Color(0xF4, 0xF7, 0xFB);
    private final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x1A);
    private final Color TEXT_GRAY = new Color(0x6C, 0x75, 0x7D);
    private final Color PRIMARY_BLUE = new Color(0x00, 0x88, 0xFF);
    private final Color GREEN_SUCCESS = new Color(0x28, 0xA7, 0x45);

    public ClientOverviewPage() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre principal
        JLabel title = new JLabel("Welcome back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // Conteneur pour les 3 panels (utilise GridBagLayout pour donner des tailles précises)
        JPanel panelsContainer = new JPanel(new GridBagLayout());
        panelsContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 15); // Espace entre les panels

        // --- PANEL 1 : Solde et Transactions (Prend le plus de place) ---
        gbc.gridx = 0;
        gbc.weightx = 0.55; // 55% de la largeur
        gbc.weighty = 1.0;
        panelsContainer.add(createMainAccountPanel(), gbc);

        // --- PANEL 2 : Quick Transfer ---
        gbc.gridx = 1;
        gbc.weightx = 0.45; // 45% de la largeur
        gbc.insets = new Insets(0, 0, 0, 0); // Pas de marge à droite pour le dernier
        panelsContainer.add(createQuickTransferPanel(), gbc);

        add(panelsContainer, BorderLayout.CENTER);
    }

    // =========================================================
    // PANEL 1 : Solde + Transactions
    // =========================================================
    private JPanel createMainAccountPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // --- Carte Solde ---
        JPanel balanceCard = new JPanel(new BorderLayout());
        balanceCard.setBackground(Color.WHITE);
        balanceCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0, 0xE0, 0xE0), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // En-tête de la carte Solde
        JPanel balanceHeader = new JPanel(new BorderLayout());
        balanceHeader.setOpaque(false);
        JLabel lblAccount = new JLabel("Primary Account");
        lblAccount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAccount.setForeground(TEXT_DARK);

        JLabel lblActive = new JLabel("● Active");
        lblActive.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblActive.setForeground(GREEN_SUCCESS);

        balanceHeader.add(lblAccount, BorderLayout.WEST);
        balanceHeader.add(lblActive, BorderLayout.EAST);

        // Montant et numéro de compte
        JPanel amountPanel = new JPanel();
        amountPanel.setLayout(new BoxLayout(amountPanel, BoxLayout.Y_AXIS));
        amountPanel.setOpaque(false);
        amountPanel.setBorder(new EmptyBorder(10, 0, 15, 0));

        JLabel lblAmount = new JLabel("48,250.00");
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblAmount.setForeground(TEXT_DARK);

        JLabel lblAccNum = new JLabel("•••• 4821");
        lblAccNum.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAccNum.setForeground(TEXT_GRAY);

        amountPanel.add(lblAmount);
        amountPanel.add(lblAccNum);

        // Boutons d'action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnTransfer = createStyledButton("↗ Transfer Money", PRIMARY_BLUE, Color.WHITE);
        JButton btnStatement = createStyledButton("📄 View Statement", new Color(0xEB, 0xF4, 0xFF), PRIMARY_BLUE);

        actionPanel.add(btnTransfer);
        actionPanel.add(btnStatement);

        balanceCard.add(balanceHeader, BorderLayout.NORTH);
        balanceCard.add(amountPanel, BorderLayout.CENTER);
        balanceCard.add(actionPanel, BorderLayout.SOUTH);

        // --- Carte Transactions Récentes ---
        JPanel recentTransCard = new JPanel();
        recentTransCard.setLayout(new BoxLayout(recentTransCard, BoxLayout.Y_AXIS));
        recentTransCard.setBackground(Color.WHITE);
        recentTransCard.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 0, 0, 0), // Marge par rapport à la carte du haut
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0xE0, 0xE0, 0xE0), 1, true),
                        new EmptyBorder(15, 15, 15, 15)
                )
        ));

        JLabel lblRecent = new JLabel("Recent Transactions");
        lblRecent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRecent.setForeground(TEXT_DARK);
        lblRecent.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentTransCard.add(lblRecent);
        recentTransCard.add(Box.createVerticalStrut(15));

        // Lignes de transactions
        recentTransCard.add(createTransactionRow("AJ", "Alex Johnson", "2025-05-12", "$1,200.00", "Completed", new Color(0xEB, 0xF4, 0xFF)));
        recentTransCard.add(Box.createVerticalStrut(10));
        recentTransCard.add(createTransactionRow("TS", "Taylor Smith", "2025-05-10", "$850.50", "Completed", new Color(0xEB, 0xF4, 0xFF)));
        recentTransCard.add(Box.createVerticalStrut(10));
        recentTransCard.add(createTransactionRow("JL", "Jordan Lee", "2025-05-08", "$2,500.00", "Completed", new Color(0xEB, 0xF4, 0xFF)));

        panel.add(balanceCard);
        panel.add(recentTransCard);
        return panel;
    }

    // =========================================================
    // PANEL 2 : Quick Transfer
    // =========================================================
    private JPanel createQuickTransferPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0, 0xE0, 0xE0), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel("Quick Transfer");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(20));

        // Champ From Account
        panel.add(createLabel("From Account"));

// 1. Création du JComboBox typé avec AccountActive
        JComboBox<AccountActive> cbFromAccount = new JComboBox<>();
        styleComboBox(cbFromAccount);

// On indique au ComboBox comment afficher un AccountActive (avec les étoiles)
        cbFromAccount.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AccountActive) {
                    AccountActive acc = (AccountActive) value;
                    // Adaptez getAccountNumber() selon le nom exact de votre méthode dans AccountActive
                    String rawNumber = acc.getAccountName();
                    String maskedNumber = "•••• " + rawNumber.substring(Math.max(0, rawNumber.length() - 4));
                    setText("Primary Account (" + maskedNumber + ")");
                }
                return this;
            }
        });

// 2. Récupération de l'ID de l'utilisateur
        int currentUserId = Session.getCurrentUserId();

// 3. Appel de votre méthode
        List<AccountActive> userAccounts = Accounts.getAllAccountsByUserIdAndType(currentUserId);

// 4. Remplissage (on ajoute l'objet complet, pas juste le texte)
        if (userAccounts != null && !userAccounts.isEmpty()) {
            for (AccountActive account : userAccounts) {
                cbFromAccount.addItem(account); // On ajoute l'objet !
            }
        } else {
            // Si aucun compte, on laisse le combo vide, ou on gère ça autrement
        }

// 5. Ajout au panel
        panel.add(cbFromAccount);
        panel.add(Box.createVerticalStrut(15));

        // Champ Recipient
        panel.add(createLabel("Recipient"));
        PlaceholderTextField txtRecipient = new PlaceholderTextField("Enter recipient name");
        styleTextField(txtRecipient);
        panel.add(txtRecipient);
        panel.add(Box.createVerticalStrut(15));

        // Champ Amount
        panel.add(createLabel("Amount"));
        PlaceholderTextField txtAmount = new PlaceholderTextField("Enter recipient name");
        styleTextField(txtAmount);
        panel.add(txtAmount);
        panel.add(Box.createVerticalStrut(25));

        // Bouton Send Money
        JButton btnSend = createStyledButton("➤ Send Money", PRIMARY_BLUE, Color.WHITE);
        btnSend.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSend.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnSend.addActionListener(e->{
            AccountActive selectedAccount =(AccountActive) cbFromAccount.getSelectedItem();

            if (selectedAccount == null){
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un compte.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }


            // On extrait le vrai numéro de compte (sans les étoiles)
            String sourceAccountNumber = selectedAccount.getAccountName();

            // On récupère le destinataire et le montant
            String targetAccountNumber = txtRecipient.getText().trim();
            double amount;

            try {
                amount = Double.parseDouble(txtAmount.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Montant invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Appel de votre méthode mise à jour
            boolean success = Transactions.transferMoney(sourceAccountNumber,targetAccountNumber,amount);
            if (success){
                JOptionPane.showMessageDialog(this,"Transfert reussi!");
                txtRecipient.setText("");
                txtAmount.setText("");

            }else{
                JOptionPane.showMessageDialog(this, "Échec du transfert.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(btnSend);

        return panel;
    }

    // =========================================================
    // MÉTHODES UTILITAIRES (Design)
    // =========================================================

    // Créer une ligne de transaction
    private JPanel createTransactionRow(String initials, String name, String date, String amount, String status, Color avatarBg) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Gauche : Avatar + Nom
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JLabel avatar = new JLabel(initials, SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        avatar.setForeground(PRIMARY_BLUE);
        avatar.setBackground(avatarBg);
        avatar.setOpaque(true);
        avatar.setPreferredSize(new Dimension(35, 35));
        // Arrondir l'avatar (pseudo-code visuel, on utilise une bordure ronde)
        avatar.setBorder(BorderFactory.createLineBorder(avatarBg, 1, true));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(TEXT_DARK);

        JLabel lblDate = new JLabel(date);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDate.setForeground(TEXT_GRAY);

        namePanel.add(lblName);
        namePanel.add(lblDate);

        leftPanel.add(avatar);
        leftPanel.add(namePanel);

        // Droite : Montant + Statut
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JLabel lblAmount = new JLabel(amount);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAmount.setForeground(TEXT_DARK);
        lblAmount.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblStatus = new JLabel(status);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(GREEN_SUCCESS);
        lblStatus.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(lblAmount);
        rightPanel.add(lblStatus);

        row.add(leftPanel, BorderLayout.WEST);
        row.add(rightPanel, BorderLayout.EAST);
        return row;
    }

    // Style pour les labels de formulaire
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        return label;
    }

    // Style pour les champs de texte
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD0, 0xD0, 0xD0), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // Style pour la ComboBox
    private void styleComboBox(JComboBox<AccountActive> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD0, 0xD0, 0xD0), 1, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // Style pour les boutons
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));

        // Effet de survol
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
                btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
                btn.repaint();
            }
        });
        return btn;
    }
}
