package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PageTableauBord extends JPanel {
    public PageTableauBord() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Tableau de bord");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titre.setForeground(new Color(30, 30, 30));
        add(titre, BorderLayout.NORTH);

        JPanel cartes = new JPanel(new GridLayout(1, 4, 20, 0));
        cartes.setBackground(new Color(245, 247, 250));
        cartes.setBorder(new EmptyBorder(20, 0, 20, 0));

        cartes.add(creerCarteArrondie("0 Ar", "Recette du jour", new Color(40, 80, 200)));
        cartes.add(creerCarteArrondie("865 L", "Stock total", new Color(40, 80, 200)));
        cartes.add(creerCarteArrondie("0", "Produits en alerte", new Color(40, 80, 200)));
        cartes.add(creerCarteArrondie("3", "Produits référencés", new Color(40, 80, 200)));
        add(cartes, BorderLayout.CENTER);
    }

    private JPanel creerCarteArrondie(String valeur, String description, Color accent) {
        JPanel carte = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        carte.setLayout(new BoxLayout(carte, BoxLayout.Y_AXIS));
        carte.setOpaque(false);
        carte.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel val = new JLabel(valeur);
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        val.setForeground(accent);
        JLabel desc = new JLabel(description);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(100, 100, 100));

        carte.add(val);
        carte.add(Box.createVerticalStrut(5));
        carte.add(desc);
        return carte;
    }
}