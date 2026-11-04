package main.vue.page;

import main.controller.Accounts;
import main.controller.Session;
import main.model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ClientAccountsPage extends JPanel {

    // --- Palette de couleurs (cohérente avec vos autres pages) ---
    private final Color BG_COLOR = new Color(0xF4, 0xF7, 0xFB);
    private final Color TITLE_BG = new Color(0x00, 0x3B, 0x8A);
    private final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x1A);
    private final Color TEXT_GRAY = new Color(0x6C, 0x75, 0x7D);
    private final Color GREEN_SUCCESS = new Color(0x28, 0xA7, 0x45);
    private final Color RED_DANGER = new Color(0xDC, 0x35, 0x45);
    private final Color BORDER_COLOR = new Color(0xE0, 0xE0, 0xE0);
    private final Color PRIMARY_BLUE = new Color(0x00, 0x88, 0xFF);

    public ClientAccountsPage() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. En-tête de la page
        add(createHeader(), BorderLayout.NORTH);

        // 2. Contenu (Liste des comptes)
        add(createAccountsListPanel(), BorderLayout.CENTER);
    }

    // =========================================================
    // EN-TÊTE
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

        header.add(leftPanel, BorderLayout.WEST);

        // Optionnel : Un petit texte à droite indiquant le nombre de comptes
        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(255, 255, 255, 200));
        header.add(countLabel, BorderLayout.EAST);

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
                contentPanel.add(Box.createVerticalStrut(15)); // Espace entre les cartes
            }
        }

        // On met tout dans un JScrollPane au cas où il y aurait beaucoup de comptes
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        return scrollPane;
    }

    // =========================================================
    // CARTE D'UN COMPTE INDIVIDUEL
    // =========================================================
    private JPanel createAccountCard(Account account) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // --- PARTIE GAUCHE : Infos principales ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        // Type de compte (ex: "Compte Courant")
        JLabel lblType = new JLabel(account.getAccountType().toUpperCase());
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblType.setForeground(PRIMARY_BLUE);
        lblType.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Numéro de compte (masqué par votre fonction SQL)
        JLabel lblNumber = new JLabel(account.getAccountNumber());
        lblNumber.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNumber.setForeground(TEXT_DARK);
        lblNumber.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Espace
        leftPanel.add(lblType);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(lblNumber);
        leftPanel.add(Box.createVerticalStrut(15));

        // --- PARTIE DROITE : Solde et Statut ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        // Formatage du solde
        String balanceStr = String.format("%,.2f", account.getBalance()).replace(',', ' ');
        JLabel lblBalance = new JLabel(balanceStr + " FCFA"); // Adaptez la devise
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBalance.setForeground(TEXT_DARK);
        lblBalance.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Statut (Actif / Bloqué)
        String statusText = account.getStatus().toUpperCase();
        Color statusColor = statusText.equals("ACTIF") ? GREEN_SUCCESS : RED_DANGER;

        JLabel lblStatus = new JLabel("● " + (statusText.equals("ACTIF") ? "Actif" : "Bloqué"));
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(statusColor);
        lblStatus.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(lblBalance);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(lblStatus);

        // --- ASSEMBLAGE ---
        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.EAST);

        // Effet de survol (optionnel, pour faire "pro")
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
}