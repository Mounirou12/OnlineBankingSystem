package main.vue.page;

import main.controller.Users;
import main.vue.AccueilPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CreateUserDialog extends JDialog {

    // --- Palette cohérente avec l'application ---
    private final Color TITLE_BG = new Color(0x00, 0x3B, 0x8A);
    private final Color FIELD_BG = new Color(0xEA, 0xF0, 0xFA);
    private final Color BORDER_CLR = new Color(0xB0, 0xC4, 0xDE);
    private final Color TEXT_DARK = new Color(0x1A, 0x1A, 0x1A);
    private final Color BTN_PRIMARY = new Color(0x00, 0x88, 0xFF);
    private final Color BTN_CANCEL = new Color(0x6C, 0x75, 0x7D);

    // --- Champs ---
    private final JTextField firstNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmField = new JPasswordField();
    private final JTextField phoneField = new JTextField();
    private final JTextField dateField = new JTextField(); // format yyyy-MM-dd
    private final JComboBox<String> sexeCombo = new JComboBox<>(new String[] { "Homme", "Femme", "Autre" });
    private final JTextField adresseField = new JTextField();

    private boolean userCreated = false;

    public CreateUserDialog(Window parent) {
        super(parent, "Créer un nouvel utilisateur", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 720);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // --- En-tête ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12)) {
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
        JLabel icon = new JLabel("👥");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        icon.setForeground(Color.WHITE);
        JLabel title = new JLabel("Créer un nouvel utilisateur");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        root.add(header, BorderLayout.NORTH);

        // --- Formulaire (avec scroll si nécessaire) ---
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 25, 10, 25));

        form.add(createField("Prénom *", firstNameField));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Nom *", lastNameField));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Email *", emailField));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Mot de passe *", passwordField));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Confirmer le mot de passe *", confirmField));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Téléphone", phoneField));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Date de naissance (yyyy-MM-dd)", dateField));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Sexe", sexeCombo));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Adresse", adresseField));

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(Color.WHITE);
        root.add(scroll, BorderLayout.CENTER);

        // --- Boutons ---
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(new EmptyBorder(0, 20, 10, 20));

        JButton btnCancel = createStyledButton("Annuler", BTN_CANCEL);
        btnCancel.addActionListener(e -> dispose());

        JButton btnCreate = createStyledButton("Créer l'utilisateur", BTN_PRIMARY);
        btnCreate.addActionListener(e -> handleSubmit());

        buttons.add(btnCancel);
        buttons.add(btnCreate);
        root.add(buttons, BorderLayout.SOUTH);

        // Style pour la combo sexe
        styleCombo(sexeCombo);
    }

    public boolean isUserCreated() {
        return userCreated;
    }

    // --- Création d'un bloc label + champ ---
    private JPanel createField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);

        if (field instanceof JTextField) {
            JTextField tf = (JTextField) field;
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tf.setForeground(TEXT_DARK);
            tf.setBackground(FIELD_BG);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                    new EmptyBorder(8, 10, 8, 10)));
            if (tf instanceof JPasswordField) {
                ((JPasswordField) tf).setEchoChar('●');
            }
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(TEXT_DARK);
        combo.setBackground(FIELD_BG);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text) {
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
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(bg.darker());
                b.repaint();
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
                b.repaint();
            }
        });
        return b;
    }

    // --- Logique de validation et d'insertion ---
    private void handleSubmit() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String phone = phoneField.getText().trim();
        String dateStr = dateField.getText().trim();
        String sexe = (String) sexeCombo.getSelectedItem();
        String adresse = adresseField.getText().trim();

        // 1. Champs obligatoires
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirm.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires (*).");
            return;
        }

        // 2. Email valide (simple)
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$")) {
            showError("Le format de l'email est invalide.");
            emailField.requestFocus();
            return;
        }

        // 3. Mots de passe identiques + longueur
        if (!password.equals(confirm)) {
            showError("Les deux mots de passe ne correspondent pas.");
            confirmField.setText("");
            confirmField.requestFocus();
            return;
        }
        if (password.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        // 4. Date de naissance optionnelle mais valide si fournie
        LocalDate dateNaissance = null;
        if (!dateStr.isEmpty()) {
            try {
                dateNaissance = LocalDate.parse(dateStr);

                // 1. Vérifier que la date n'est pas dans le futur
                if (dateNaissance.isAfter(LocalDate.now())) {
                    showError("La date de naissance ne peut pas être dans le futur.");
                    return;
                }

                // 2. Vérifier que la personne a au moins 18 ans
                LocalDate dateLimite18Ans = LocalDate.now().minusYears(18);
                if (dateNaissance.isAfter(dateLimite18Ans)) {
                    showError("Vous devez avoir au moins 18 ans pour créer un compte.");
                    dateField.requestFocus();
                    return;
                }

            } catch (DateTimeParseException ex) {
                showError("Date invalide. Format attendu : yyyy-MM-dd (ex : 1990-05-21).");
                dateField.requestFocus();
                return;
            }
        }

        // 5. Appel métier : l'insertion se fait dans Users.insertUser
        // qui applique déjà BCrypt.hashpw sur le mot de passe
        boolean ok = Users.insertUser(
                firstName, lastName, email, password, phone,
                dateNaissance, sexe, adresse);

        if (ok) {
            userCreated = true;
            JOptionPane.showMessageDialog(this,
                    "Utilisateur créé avec succès.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new AccueilPage().setVisible(true);
        } else {
            showError("Impossible de créer l'utilisateur.\n"
                    + "L'email est peut-être déjà utilisé, ou la base est inaccessible.");
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}