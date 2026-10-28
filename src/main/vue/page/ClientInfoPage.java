package main.vue.page;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClientInfoPage extends JPanel {
    // --- Palette de couleurs basée sur l'image ---
    private final Color HEADER_BG_1 = new Color(0x00, 0x55, 0xD0); // Bleu foncé
    private final Color HEADER_BG_2 = new Color(0x00, 0x88, 0xFF); // Bleu clair
    private final Color TITLE_BG = new Color(0x00, 0x3B, 0x8A);    // Bleu très foncé (titre détails)
    private final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x1A);
    private final Color FIELD_BG = new Color(0xEA, 0xF0, 0xFA);    // Bleu très très clair
    private final Color BORDER_COLOR = new Color(0xB0, 0xC4, 0xDE);

    public ClientInfoPage(){
        setLayout(new BorderLayout());
        setBackground(new Color(0xF4, 0xF7, 0xFB)); // Fond gris/bleu très clair de la fenêtre
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. En-tête principal (Informations du Client)
        add(createMainHeader(), BorderLayout.NORTH);

        // 2. Carte centrale (Détails)
        add(createDetailsCard(), BorderLayout.CENTER);
    }

    private JPanel createMainHeader(){
        JPanel header = new JPanel(new BorderLayout()){
            @Override
            protected void printComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TITLE_BG);
                // Coins arrondis uniquement en haut
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 15, 15);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Icône Utilisateur (simulée avec une étiquette)
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        iconLabel.setForeground(Color.WHITE);

        // Ligne verticale séparatrice
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(new Color(255, 255, 255, 100));
        separator.setPreferredSize(new Dimension(1, 40));

        // Titre
        JLabel title = new JLabel("Informations du Client");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel);
        leftPanel.add(separator);
        leftPanel.add(title);

        header.add(leftPanel, BorderLayout.WEST);
        return header;
            }

    // --- CARTE DES DÉTAILS ---
    private JPanel createDetailsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 0, 0, 0), // Marge par rapport à l'en-tête
                BorderFactory.createLineBorder(new Color(0xD0, 0xE0, 0xF0), 1, true)
        ));

        // Titre de la carte (Détails du Client)
        JPanel cardTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TITLE_BG);
                // Coins arrondis uniquement en haut
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 15, 15);
                g2.dispose();
            }
        };
        cardTitlePanel.setOpaque(false);

        JLabel infoIcon = new JLabel("ℹ️");
        infoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        infoIcon.setForeground(Color.WHITE);

        JLabel cardTitle = new JLabel("Détails du Client");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(Color.WHITE);

        cardTitlePanel.add(infoIcon);
        cardTitlePanel.add(cardTitle);
        card.add(cardTitlePanel, BorderLayout.NORTH);

        // Grille des informations
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(20, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Lignes d'informations (Icon, Label, Champ)
        addRow(gridPanel, gbc, 0, "👤", "Nom Complet :", "Mounirou Fagbemy");
        addRow(gridPanel, gbc, 1, "📅", "Date de Naissance :", "15 mars 2002");
        addRow(gridPanel, gbc, 2, "♂", "Sexe :", "Masculin");
        addRow(gridPanel, gbc, 3, "📞", "Téléphone :", "+229 01 97 12 34 56");
        addRow(gridPanel, gbc, 4, "✉️", "Email :", "mounirou@gmail.com");
        addRow(gridPanel, gbc, 5, "🏠", "Adresse :", "Cotonou, Bénin");
        addRow(gridPanel, gbc, 6, "🗓️", "Date d'Inscription :", "10 janvier 2025");

        card.add(gridPanel, BorderLayout.CENTER);
        return card;
    }
    // Méthode utilitaire pour ajouter une ligne proprement
    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String icon, String labelText, String valueText) {
        // 1. L'icône
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLabel.setForeground(new Color(0x0A, 0x3B, 0x7C)); // Bleu foncé
        panel.add(iconLabel, gbc);

        // 2. Le label (Nom complet, etc.)
        gbc.gridx = 1; gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        panel.add(label, gbc);

        // 3. Le champ de valeur
        gbc.gridx = 2; gbc.weightx = 0.7;
        JTextField valueField = new JTextField(valueText);
        valueField.setEditable(false); // Lecture seule
        valueField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueField.setForeground(TEXT_DARK);
        valueField.setBackground(FIELD_BG);
        valueField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        panel.add(valueField, gbc);
    }


}


