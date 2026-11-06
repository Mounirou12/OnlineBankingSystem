package main.vue.page;

import main.controller.Accounts;
import main.controller.Session;
import main.model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ClientAccountsPage extends JPanel {

    // --- Palette de couleurs ---
    private final Color BG_COLOR = new Color(0xF4, 0xF7, 0xFB);
    private final Color TITLE_BG = new Color(0x00, 0x3B, 0x8A);
    private final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x1A);
    private final Color TEXT_GRAY = new Color(0x6C, 0x75, 0x7D);
    private final Color GREEN_SUCCESS = new Color(0x28, 0xA7, 0x45);
    private final Color RED_DANGER = new Color(0xDC, 0x35, 0x45);
    private final Color BORDER_COLOR = new Color(0xE0, 0xE0, 0xE0);
    private final Color PRIMARY_BLUE = new Color(0x00, 0x88, 0xFF);
    private final Color BTN_SUCCESS = new Color(0x28, 0xA7, 0x45);

    public ClientAccountsPage() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. En-tête de la page (avec le bouton de création)
        add(createHeader(), BorderLayout.NORTH);

        // 2. Contenu (Liste des comptes)
        add(createAccountsListPanel(), BorderLayout.CENTER);
    }

    // =========================================================
    // EN-TÊTE AVEC BOUTON
    // =========================================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TITLE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 15, 15);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        // --- Partie Gauche (Icône + Titre) ---
        JLabel iconLabel = new JLabel("💳");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        iconLabel.setForeground(Color.WHITE);

        JLabel title = new JLabel("Mes Comptes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel);
        leftPanel.add(title);

        // --- Partie Droite (Bouton Créer) ---
        JButton btnCreate = createStyledButton("Créer un compte", "➕", BTN_SUCCESS);
        btnCreate.addActionListener(e -> ouvrirDialogueCreationCompte());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(btnCreate);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // =========================================================
    // LISTE DES COMPTES
    // =========================================================
    private JScrollPane createAccountsListPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        int userId = Session.getCurrentUserId();
        List<Account> accounts = Accounts.getAllAccountsByUserId(userId);

        if (accounts == null || accounts.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucun compte trouvé pour cet utilisateur.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(TEXT_GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(emptyLabel);
        } else {
            for (Account account : accounts) {
                contentPanel.add(createAccountCard(account));
                contentPanel.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        return scrollPane;
    }

    // =========================================================
    // CARTE D'UN COMPTE
    // =========================================================
    private JPanel createAccountCard(Account account) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel lblType = new JLabel(account.getAccountType().toUpperCase());
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblType.setForeground(PRIMARY_BLUE);
        lblType.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNumber = new JLabel(account.getAccountNumber());
        lblNumber.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNumber.setForeground(TEXT_DARK);
        lblNumber.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblType);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(lblNumber);
        leftPanel.add(Box.createVerticalStrut(15));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        String balanceStr = String.format("%,.2f", account.getBalance()).replace(',', ' ');
        JLabel lblBalance = new JLabel(balanceStr + " FCFA");
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBalance.setForeground(TEXT_DARK);
        lblBalance.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String statusText = account.getStatus().toUpperCase();
        Color statusColor = statusText.equals("ACTIF") ? GREEN_SUCCESS : RED_DANGER;

        JLabel lblStatus = new JLabel("● " + (statusText.equals("ACTIF") ? "Actif" : "Bloqué"));
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(statusColor);
        lblStatus.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(lblBalance);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(lblStatus);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_BLUE, 1, true),
                        new EmptyBorder(20, 25, 20, 25)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(20, 25, 20, 25)
                ));
            }
        });

        return card;
    }

    // =========================================================
    // DIALOGUE DE CRÉATION DE COMPTE
    // =========================================================
    private void ouvrirDialogueCreationCompte() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouveau Compte", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // --- Titre ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        headerPanel.setBackground(TITLE_BG);
        JLabel lblTitle = new JLabel("➕ Ouvrir un nouveau compte");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // --- Formulaire ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Type de compte
        JLabel lblType = new JLabel("Type de compte :");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblType.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cbType = new JComboBox<>(new String[]{"COURANT", "EPARGNE"});
        cbType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cbType.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Solde initial
        JLabel lblBalance = new JLabel("Solde initial (FCFA) :");
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBalance.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblBalance.setBorder(new EmptyBorder(15, 0, 0, 0));

        JTextField txtBalance = new JTextField("0");
        txtBalance.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBalance.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtBalance.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(lblType);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(cbType);
        formPanel.add(lblBalance);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtBalance);

        dialog.add(formPanel, BorderLayout.CENTER);

        // --- Boutons ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnValider = createStyledButton("Créer", "", BTN_SUCCESS);
        btnValider.addActionListener(e -> {
            try {
                String type = (String) cbType.getSelectedItem();
                long balance = Long.parseLong(txtBalance.getText().trim());

                // Appel de votre méthode (le numéro et le statut sont gérés par la méthode)
                boolean success = Accounts.insertAccount(Session.getCurrentUserId(), null, type, balance, "ACTIF");

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Compte créé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    // Rafraîchir la page pour voir le nouveau compte
                    removeAll();
                    add(createHeader(), BorderLayout.NORTH);
                    add(createAccountsListPanel(), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Impossible de créer le compte.\nVous avez peut-être atteint la limite de comptes.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Veuillez entrer un solde valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnValider);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // =========================================================
    // MÉTHODE UTILITAIRE : BOUTON STYLISÉ
    // =========================================================
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