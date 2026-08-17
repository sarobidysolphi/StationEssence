package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PageTableauBord extends JPanel {
    public PageTableauBord() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Tableau de bord");
        titre.setFont(Theme.POLICE_TITRE);
        titre.setForeground(Theme.TEXTE_FONCE);
        add(titre, BorderLayout.NORTH);

        JPanel cartes = new JPanel(new GridLayout(1, 4, 20, 0));
        cartes.setBackground(Theme.FOND_CLAIR);
        cartes.setBorder(new EmptyBorder(20, 0, 20, 0));

        cartes.add(creerCarteArrondie("0 Ar", "Recette du jour", Theme.BLEU_ACCENT));
        cartes.add(creerCarteArrondie("865 L", "Stock total", Theme.BLEU_ACCENT));
        cartes.add(creerCarteArrondie("0", "Produits en alerte", Theme.BLEU_ACCENT));
        cartes.add(creerCarteArrondie("3", "Produits référencés", Theme.BLEU_ACCENT));
        add(cartes, BorderLayout.CENTER);
    }

    // Nouvelle méthode pour créer des cartes avec coins arrondis
    private JPanel creerCarteArrondie(String valeur, String description, Color accent) {
        JPanel carte = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        carte.setLayout(new BoxLayout(carte, BoxLayout.Y_AXIS));
        carte.setOpaque(false);
        carte.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel val = new JLabel(valeur);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(accent);
        JLabel desc = new JLabel(description);
        desc.setFont(Theme.POLICE_NORMALE);
        desc.setForeground(Theme.TEXTE_SECONDAIRE);
        
        carte.add(val);
        carte.add(Box.createVerticalStrut(5));
        carte.add(desc);
        return carte;
    }
}