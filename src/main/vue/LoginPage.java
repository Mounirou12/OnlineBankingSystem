package main.vue;

import main.controller.Session;
import main.controller.Users;
import main.vue.page.CreateUserDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPage extends JFrame {
    private JTextField identifiantField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private final Color PRIMARY_COLOR = new Color(0x3B, 0x82, 0xF6);
    private final Color HOVER_COLOR = new Color(0x25, 0x63, 0xEB);
    private final Color TEXT_DARK = new Color(0x1F, 0x29, 0x37);
    private final Color TEXT_GRAY = new Color(0x6B, 0x72, 0x80);
    private final Color BORDER_COLOR = new Color(0xD1, 0xD5, 0xDB);

    public LoginPage() {
        setTitle("SecureBank - Connexion");
        setSize(520, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(0xEB, 0xF4, 0xFF);
                Color color2 = new Color(0xFF, 0xFF, 0xFF);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setPreferredSize(new Dimension(440, 480));

        JLabel title = new JLabel("SecureBank");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Bienvenue ! Connectez-vous.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(35));

        JLabel idLabel = new JLabel("Email ou Prénom");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        idLabel.setForeground(TEXT_DARK);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        identifiantField = new JTextField();
        styliserChamp(identifiantField);

        card.add(idLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(identifiantField);
        card.add(Box.createVerticalStrut(20));

        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(TEXT_DARK);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        styliserChamp(passwordField);

        card.add(passLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(25)); // Espace avant la ligne des boutons

        // ============================================================
        // 1. Création du bouton "Nouvel utilisateur"
        // ============================================================
        JButton btnAddUser = new JButton("Nouvel utilisateur");
        btnAddUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAddUser.setForeground(PRIMARY_COLOR);
        btnAddUser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddUser.setContentAreaFilled(false);
        btnAddUser.setBorderPainted(false);
        btnAddUser.setFocusPainted(false);
        btnAddUser.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            CreateUserDialog dialog = new CreateUserDialog(parent);
            dialog.setVisible(true);
            if (dialog.isUserCreated()) {
                // chargerUtilisateurs();
            }
        });

        // ============================================================
        // 2. Création du bouton "Se connecter"
        // ============================================================
        loginButton = new JButton("Se connecter") {
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

        loginButton.setPreferredSize(new Dimension(140, 42)); // Largeur augmentée à 150
        loginButton.setMaximumSize(new Dimension(140, 42));
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { loginButton.setBackground(HOVER_COLOR); loginButton.repaint(); }
            @Override
            public void mouseExited (MouseEvent e) { loginButton.setBackground(PRIMARY_COLOR); loginButton.repaint(); }
        });

        loginButton.addActionListener(e -> manageConnexion());

        // ============================================================
        // 3. Panneau horizontal : les deux boutons sur la même ligne
        // ============================================================
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Évite que BoxLayout l'étire

        // À gauche : "Nouvel utilisateur"
        headerRow.add(btnAddUser, BorderLayout.WEST);

        // À droite : "Se connecter"
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(loginButton);
        headerRow.add(rightPanel, BorderLayout.EAST);

        // ============================================================
        // 4. Ajout UNIQUE de la ligne dans la carte
        // ============================================================
        card.add(headerRow);

        mainPanel.add(card);
        setContentPane(mainPanel);
    }
    // ⚠️ Correction : on retire le paramètre placeholder inutile et on corrige les couleurs
    private void styliserChamp(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // ✅ Typo corrigée
        // field.setBackground(TEXT_DARK); // ❌ Supprimé (texte noir sur fond noir)

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(4, 9, 4, 9)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }

    private void manageConnexion() {
        String identifiant = identifiantField.getText().trim();
        String motDePasse = new String(passwordField.getPassword());

        if (identifiant.isEmpty() || motDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs.",
                    "Champs vides",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ⚠️ Vérifie que la classe Users existe et a bien une méthode connexion
        if (Users.connexion(identifiant, motDePasse)) {
//            JOptionPane.showMessageDialog(this, "Bienvenue "+ Session.getCurrentUserFirstName() + "!");
            this.dispose();
            new AccueilPage().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Email ou mot de passe incorrect.",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        // Pour que l'interface utilise le style natif du système (plus beau sur Windows/Mac)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}