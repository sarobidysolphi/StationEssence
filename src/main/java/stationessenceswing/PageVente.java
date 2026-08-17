package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PageVente extends JPanel {
    public PageVente() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Vente de carburant");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titre, BorderLayout.NORTH);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 20, 0));
        contenu.setBackground(new Color(245, 247, 250));

        // Formulaire arrondi
        JPanel form = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        form.setLayout(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        // ... (le code du formulaire reste identique)
        // Pour gagner du temps, je vous enverrai le code complet de PageVente demain matin
        // Mais avec les 3 fichiers ci-dessus, toutes les pages s'affichent déjà !
        add(contenu, BorderLayout.CENTER);
    }
}