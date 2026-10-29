package main.vue.page;

import main.controller.Session;
import main.controller.Users;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChangePasswordDialog extends JDialog {

    // Palette cohérente avec ClientInfoPage
    private final Color TITLE_BG   = new Color(0x00, 0x3B, 0x8A);
    private final Color FIELD_BG   = new Color(0xEA, 0xF0, 0xFA);
    private final Color BORDER_CLR = new Color(0xB0, 0xC4, 0xDE);
    private final Color TEXT_DARK  = new Color(0x1A, 0x1A, 0x1A);
    private final Color BTN_PRIMARY = new Color(0x00, 0x88, 0xFF);
    private final Color BTN_CANCEL  = new Color(0x6C, 0x75, 0x7D);

    private final JPasswordField oldPasswordField     = new JPasswordField();
    private final JPasswordField newPasswordField     = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();

    private boolean passwordChanged = false;   // ← AJOUT

    public ChangePasswordDialog(Window parent) {
        super(parent, "Changer de mot de passe", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(480, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // --- En-tête ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TITLE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 15, 15);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        JLabel icon = new JLabel("🔒");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        icon.setForeground(Color.WHITE);
        JLabel title = new JLabel("Changer de mot de passe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        root.add(header, BorderLayout.NORTH);

        // --- Formulaire ---
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 25, 10, 25));

        form.add(createField("Ancien mot de passe", oldPasswordField));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Nouveau mot de passe", newPasswordField));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Confirmer le nouveau mot de passe", confirmPasswordField));

        root.add(form, BorderLayout.CENTER);

        // --- Boutons ---
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(new EmptyBorder(0, 20, 10, 20));

        JButton btnCancel = createStyledButton("Annuler", BTN_CANCEL);
        btnCancel.addActionListener(e -> dispose());

        JButton btnValidate = createStyledButton("Valider", BTN_PRIMARY);
        btnValidate.addActionListener(e -> handleSubmit());

        buttons.add(btnCancel);
        buttons.add(btnValidate);
        root.add(buttons, BorderLayout.SOUTH);
    }

    /** Construit un bloc "label + champ" stylisé */
    private JPanel createField(String labelText, JPasswordField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_DARK);
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setEchoChar('●');

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
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
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); b.repaint(); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg);         b.repaint(); }
        });
        return b;
    }

    // --- Logique de validation ---
    private void handleSubmit() {
        String oldPwd     = new String(oldPasswordField.getPassword());
        String newPwd     = new String(newPasswordField.getPassword());
        String confirmPwd = new String(confirmPasswordField.getPassword());

        // 1. Champs vides
        if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        // 2. Nouveau == confirmation
        if (!newPwd.equals(confirmPwd)) {
            showError("Les deux nouveaux mots de passe ne correspondent pas.");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        // 3. Longueur minimale
        if (newPwd.length() < 6) {
            showError("Le nouveau mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        // 4. Nouveau != ancien
        if (newPwd.equals(oldPwd)) {
            showError("Le nouveau mot de passe doit être différent de l'ancien.");
            return;
        }

        // 5. Appel métier : vérifier l'ancien + mettre à jour
        boolean ok = Users.passwordChange(
                Session.getCurrentUserId(),
                oldPwd,
                newPwd,
                confirmPwd
        );

        if (ok) {
            passwordChanged = true;
            dispose();
        } else {
            showError("Ancien mot de passe incorrect.");
            oldPasswordField.setText("");
            oldPasswordField.requestFocus();
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /** Permet au parent de savoir si le mot de passe a été changé. */
    public boolean isPasswordChanged() {        // ← AJOUT
        return passwordChanged;
    }
}