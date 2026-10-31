package main.vue.page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlaceholderTextField extends JTextField implements FocusListener {

    private String placeholder;
    private boolean showingPlaceholder;
    private Color placeholderColor = new Color(0x99, 0x99, 0x99); // Gris par défaut
    private Color normalTextColor;

    public PlaceholderTextField() {
        super();
        init();
    }

    public PlaceholderTextField(String placeholder) {
        super();
        this.placeholder = placeholder;
        init();
    }

    private void init() {
        this.normalTextColor = getForeground();
        this.showingPlaceholder = true;
        addFocusListener(this);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        if (showingPlaceholder) {
            repaint();
        }
    }

    @Override
    public String getText() {
        // Si on affiche le placeholder, on retourne une chaîne vide pour la logique métier
        return showingPlaceholder ? "" : super.getText();
    }

    @Override
    public void setText(String text) {
        if (text == null || text.isEmpty()) {
            showingPlaceholder = true;
            super.setText(placeholder);
            setForeground(placeholderColor);
        } else {
            showingPlaceholder = false;
            super.setText(text);
            setForeground(normalTextColor);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // On dessine le placeholder par-dessus le champ vide
        if (showingPlaceholder && placeholder != null && !placeholder.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(placeholderColor);
            g2.setFont(getFont());

            // Aligner le texte verticalement et horizontalement avec une petite marge
            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int x = insets.left;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (showingPlaceholder) {
            showingPlaceholder = false;
            super.setText("");
            setForeground(normalTextColor);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (super.getText().isEmpty()) {
            showingPlaceholder = true;
            super.setText(placeholder);
            setForeground(placeholderColor);
        }
    }
}
