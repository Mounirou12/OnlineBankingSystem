package main.vue.page;

import main.controller.Session;
import main.controller.Transactions; // Adaptez selon le nom de votre classe de contrôleur
import main.model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ClientTransactionsPage extends JPanel {

    // --- Palette de couleurs ---
    private final Color BG_COLOR = new Color(0xF4, 0xF7, 0xFB);
    private final Color TITLE_BG = new Color(0x00, 0x3B, 0x8A);
    private final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x1A);
    private final Color TEXT_GRAY = new Color(0x6C, 0x75, 0x7D);
    private final Color BORDER_COLOR = new Color(0xE0, 0xE0, 0xE0);
    private final Color GREEN_SUCCESS = new Color(0x28, 0xA7, 0x45);
    private final Color RED_DANGER = new Color(0xDC, 0x35, 0x45);
    private final Color PRIMARY_BLUE = new Color(0x00, 0x88, 0xFF);

    public ClientTransactionsPage() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. En-tête
        add(createHeader(), BorderLayout.NORTH);

        // 2. Liste des transactions
        add(createTransactionsListPanel(), BorderLayout.CENTER);
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

        JLabel iconLabel = new JLabel("📜");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        iconLabel.setForeground(Color.WHITE);

        JLabel title = new JLabel("Historique des Transactions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel);
        leftPanel.add(title);

        header.add(leftPanel, BorderLayout.WEST);
        return header;
    }

    // =========================================================
    // LISTE DES TRANSACTIONS
    // =========================================================
    private JScrollPane createTransactionsListPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        int userId = Session.getCurrentUserId();

        // ⚠️ IMPORTANT : Pour avoir TOUT l'historique, n'utilisez pas la méthode
        // "Limit".
        // Si vous n'avez que la méthode avec "LIMIT 3", créez une méthode sœur sans le
        // LIMIT
        // ou utilisez directement la méthode qui récupère toutes les transactions.
        List<Transaction> transactions = Transactions.getAllTransactionsByUserId(userId);

        if (transactions == null || transactions.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucune transaction trouvée.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(TEXT_GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(emptyLabel);
        } else {
            // On ajoute un en-tête de liste
            contentPanel.add(createListHeader());
            contentPanel.add(Box.createVerticalStrut(10));

            // On boucle sur les transactions
            for (Transaction tx : transactions) {
                contentPanel.add(createTransactionRow(tx));
                contentPanel.add(Box.createVerticalStrut(8)); // Espace entre les lignes
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
    // LIGNE D'UNE TRANSACTION
    // =========================================================
    private JPanel createTransactionRow(Transaction tx) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)));

        // --- GAUCHE : Icône + Détails ---
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        // Icône en fonction du type (Débit ou Crédit)
        JLabel iconLabel = new JLabel();
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setPreferredSize(new Dimension(40, 40));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setOpaque(true);

        boolean isDebit = "DEBIT".equalsIgnoreCase(tx.getType());

        if (isDebit) {
            iconLabel.setText("↗");
            iconLabel.setForeground(RED_DANGER);
            iconLabel.setBackground(new Color(0xFF, 0xEB, 0xEE)); // Fond rouge clair
        } else {
            iconLabel.setText("↙");
            iconLabel.setForeground(GREEN_SUCCESS);
            iconLabel.setBackground(new Color(0xEB, 0xF9, 0xEE)); // Fond vert clair
        }

        // Détails (Nom du destinataire/expéditeur et Date)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        String name = tx.getDestinataire() != null ? tx.getDestinataire() : "Inconnu";
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(TEXT_DARK);

        // Formatage de la date
        String dateStr = String.valueOf(tx.getTransactionDate());
        JLabel lblDate = new JLabel(dateStr + " • " + tx.getCompte());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(TEXT_GRAY);

        detailsPanel.add(lblName);
        detailsPanel.add(Box.createVerticalStrut(3));
        detailsPanel.add(lblDate);

        leftPanel.add(iconLabel);
        leftPanel.add(detailsPanel);

        // --- DROITE : Montant + Statut ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        // Formatage du montant (+ ou -)
        String amountStr = String.format("%,.2f", tx.getAmount()).replace(',', ' ');
        String prefix = isDebit ? "- " : "+ ";
        Color amountColor = isDebit ? TEXT_DARK : GREEN_SUCCESS;

        JLabel lblAmount = new JLabel(prefix + amountStr + " FCFA");
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAmount.setForeground(amountColor);
        lblAmount.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblStatus = new JLabel(tx.getStatus());
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(TEXT_GRAY);
        lblStatus.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(lblAmount);
        rightPanel.add(Box.createVerticalStrut(3));
        rightPanel.add(lblStatus);

        // --- ASSEMBLAGE ---
        row.add(leftPanel, BorderLayout.WEST);
        row.add(rightPanel, BorderLayout.EAST);

        return row;
    }

    // =========================================================
    // EN-TÊTE DE LA LISTE (Style tableau)
    // =========================================================
    private JPanel createListHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        header.setBorder(new EmptyBorder(0, 15, 0, 15));

        JLabel lblLeft = new JLabel("Détails de la transaction");
        lblLeft.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLeft.setForeground(TEXT_GRAY);

        JLabel lblRight = new JLabel("Montant");
        lblRight.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRight.setForeground(TEXT_GRAY);

        header.add(lblLeft, BorderLayout.WEST);
        header.add(lblRight, BorderLayout.EAST);
        return header;
    }
}