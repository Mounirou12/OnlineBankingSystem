package main.vue.page;

import main.controller.Session;
import main.vue.LoginPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class ClientInfoPage extends JPanel {
    // --- Palette de couleurs basée sur l'image ---
    private final Color HEADER_BG_1 = new Color(0x00, 0x55, 0xD0); // Bleu foncé
    private final Color HEADER_BG_2 = new Color(0x00, 0x88, 0xFF); // Bleu clair
    private final Color TITLE_BG = new Color(0x00, 0x3B, 0x8A);    // Bleu très foncé (titre détails)
    private final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x1A);
    private final Color FIELD_BG = new Color(0xEA, 0xF0, 0xFA);    // Bleu très très clair
    private final Color BORDER_COLOR = new Color(0xB0, 0xC4, 0xDE);

    // Couleurs pour les boutons
    private final Color BTN_PRIMARY = new Color(0x00, 0x88, 0xFF);   // Bleu pour mettre à jour
    private final Color BTN_DANGER = new Color(0xDC, 0x35, 0x45);    // Rouge pour déconnexion
    private final Color BTN_WARNING = new Color(0xFF, 0x8C, 0x00);   // Orange pour mot de passe
    private final Color BTN_HOVER = new Color(0x00, 0x6B, 0xCC);     // Bleu foncé survol

    public ClientInfoPage(){
        setLayout(new BorderLayout());
        setBackground(new Color(0xF4, 0xF7, 0xFB)); // Fond gris/bleu très clair de la fenêtre
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. En-tête principal (Informations du Client)
        add(createMainHeader(), BorderLayout.NORTH);

        // 2. Carte centrale (Détails)
        add(createDetailsCard(), BorderLayout.CENTER);
    }
    private String formatDateFr(LocalDate date) {
        if (date == null) return "Non renseignée";

        // Tableau des mois en français
        String[] mois = {
                "janvier", "février", "mars", "avril", "mai", "juin",
                "juillet", "août", "septembre", "octobre", "novembre", "décembre"
        };

        // Formatage : jour + mois + année
        return date.getDayOfMonth() + " " + mois[date.getMonthValue() - 1] + " " + date.getYear();
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
        title.setForeground(TEXT_DARK);

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

        // Conteneur principal pour la grille + boutons
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Grille des informations
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(20, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        LocalDate dateNaiss = Session.getCurrentDateNaissance();
        String textedateNaissance = formatDateFr(dateNaiss);
        String texteDate = (dateNaiss != null) ? dateNaiss.toString() : "Non renseignée";
        // Lignes d'informations (Icon, Label, Champ)
        addRow(gridPanel, gbc, 0, "👤", "Nom Complet :", Session.getCurrentUserFirstName() + " "+Session.getCurrentUserLastName());
        addRow(gridPanel, gbc, 1, "📅", "Date de Naissance :", textedateNaissance);
        addRow(gridPanel, gbc, 2, "♂", "Sexe :", Session.getCurrentUserSexe());
        addRow(gridPanel, gbc, 3, "📞", "Téléphone :", Session.getCurrentUserPhone());
        addRow(gridPanel, gbc, 4, "✉️", "Email :", Session.getCurrentUserEmail());
        addRow(gridPanel, gbc, 5, "🏠", "Adresse :", Session.getCurrentUserAdresse());
        addRow(gridPanel, gbc, 6, "🗓️", "Date d'Inscription :", "10 janvier 2025");

        contentPanel.add(gridPanel, BorderLayout.CENTER);

        // Panneau des boutons en bas
        JPanel buttonPanel = createButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    // --- PANNEAU DES BOUTONS ---
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(5, 30, 20, 30));



        // Bouton Changer de mot de passe
        JButton btnChangePassword = createStyledButton("Changer de mot de passe", "🔒", BTN_WARNING);
        btnChangePassword.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(ClientInfoPage.this);
            ChangePasswordDialog dialog = new ChangePasswordDialog(parentWindow);
            dialog.setVisible(true);   // bloque jusqu'à fermeture (APPLICATION_MODAL)

            // Si le mot de passe a été changé → reconnexion obligatoire
            if (dialog.isPasswordChanged()) {
                JOptionPane.showMessageDialog(
                        ClientInfoPage.this,
                        "Mot de passe modifié avec succès.\nVeuillez vous reconnecter.",
                        "Reconnexion requise",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // 1. Nettoyer la session
                Session.clear();

                // 2. Ouvrir la page de connexion
                SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));

                // 3. Fermer la fenêtre actuelle
                Window current = SwingUtilities.getWindowAncestor(ClientInfoPage.this);
                if (current != null) {
                    current.dispose();
                }
            }
        });

        // Bouton Déconnexion
        JButton btnLogout = createStyledButton("Déconnexion", "🚪", BTN_DANGER);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment vous déconnecter ?",
                    "Confirmation de déconnexion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                // Action à implémenter : déconnexion
                Session.clear();
                SwingUtilities.invokeLater(()-> new LoginPage().setVisible(true));

                Window window = SwingUtilities.getWindowAncestor(ClientInfoPage.this);
                if(window != null){
                    window.dispose();
                }
            }
        });

        buttonPanel.add(btnChangePassword);
        buttonPanel.add(btnLogout);

        return buttonPanel;
    }

    // --- MÉTHODE UTILITAIRE POUR CRÉER UN BOUTON STYLISÉ ---
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

        // Effet de survol
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