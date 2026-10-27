package main.vue;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AccueilPage  extends JFrame{
    private static final Color SIDEBAR_ACTIVE = new Color(0x3B, 0x82, 0xF6); // bleu actif
    private static final Color SIDEBAR_ACTIVEB = new Color(59, 130, 246, 136); // bleu actif

    private static final Color TEXT_DARK = new Color(0x1F, 0x29, 0x37);
    private static final Color TEXT_GRAY = new Color(0x6B, 0x72, 0x80);
    private final List<JButton> menuButtons = new ArrayList<>();
    private JButton activeButton;
    private final JPanel  contentArea;
    private final  CardLayout cardLayout;

    public AccueilPage() {
        setTitle("SecureBank");
        setSize(1000,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);

        add(createSidebar(),BorderLayout.WEST);
        add(contentArea,BorderLayout.CENTER);


    }

    private JPanel createSidebar(){
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200,0));
        sidebar.setBackground(Color.white);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));


        sidebar.add(createMenuItem("Overview","overview",true));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("Accounts","accounts",false));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("Transfer","transfer",false));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("Transactions","transactions",false));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("Profile","profile",false));
        sidebar.add(Box.createVerticalGlue());
        return  sidebar;

    }

    private JButton createMenuItem(String text, String pageKey, boolean active){
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_DARK);
        btn.setBackground(Color.white);
        btn.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setRolloverEnabled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        menuButtons.add(btn);
        if (active) activeButton = btn;
            btn.addActionListener(e->{
                setActiveButton(btn);
            });
            return  btn;
    }
    private void setActiveButton(JButton button) {
        // Si on clique sur le bouton déjà actif, on ne fait rien
        if (button == activeButton) {
            return;
        }

        // 1. Remettre l'ancien bouton à son état normal (fond blanc, texte gris)
        if (activeButton != null) {
            activeButton.setBackground(Color.WHITE);
            activeButton.setForeground(TEXT_GRAY);
        }

        // 2. Mettre le nouveau bouton en état actif (fond bleu, texte blanc)
        button.setBackground(SIDEBAR_ACTIVEB);
        button.setForeground(SIDEBAR_ACTIVE); // ⚠️ Modifié ici : Blanc au lieu de TEXT_DARK

        activeButton = button;
    }
}
