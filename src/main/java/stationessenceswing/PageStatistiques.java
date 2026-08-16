package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PageStatistiques extends JPanel {

    private JPanel graphiquePanel;

    public PageStatistiques() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Titre
        JLabel titre = new JLabel("Statistiques");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titre.setForeground(new Color(50, 50, 50));
        add(titre, BorderLayout.NORTH);

        // Sous-titre
        JLabel sousTitre = new JLabel("Recettes des 5 derniers mois");
        sousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sousTitre.setForeground(Color.GRAY);
        sousTitre.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 245, 245));
        header.add(titre, BorderLayout.NORTH);
        header.add(sousTitre, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Graphique
        graphiquePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerGraphique(g);
            }
        };
        graphiquePanel.setBackground(Color.WHITE);
        graphiquePanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(20, 20, 20, 20),
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        add(graphiquePanel, BorderLayout.CENTER);
    }

    // --- Méthode pour dessiner le graphique ---
    private void dessinerGraphique(Graphics g) {
        int largeur = getWidth() - 100; // Marges
        int hauteur = getHeight() - 100;
        int xStart = 80;
        int yStart = 40;

        // Récupération des données des 5 derniers mois
        int[] donnees = DonneesMemoire.getRecettes5DerniersMois();
        
        // Calcul du maximum pour adapter la hauteur des barres
        int maxVal = 1;
        for (int val : donnees) {
            if (val > maxVal) maxVal = val;
        }

        // 1. Dessiner les axes (Lignes grises)
        g.setColor(new Color(200, 200, 200));
        g.drawLine(xStart, yStart, xStart, hauteur + yStart); // Axe Y
        g.drawLine(xStart, hauteur + yStart, largeur + xStart, hauteur + yStart); // Axe X

        // 2. Dessiner les barres et les étiquettes
        int largeurBarre = 60;
        int espace = 40;
        int debutX = xStart + 40;

        // Noms des mois (format : "MMM yy" ex: "avr. 26")
        LocalDate dateCourante = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yy", new Locale("fr", "FR"));

        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        for (int i = 0; i < donnees.length; i++) {
            int val = donnees[i];
            
            // Hauteur de la barre (proportionnelle)
            int hauteurBarre = (int)((double)val / maxVal * (hauteur - 40));
            if (hauteurBarre == 0) hauteurBarre = 10; // Barre minimale visible

            int x = debutX + i * (largeurBarre + espace);
            int y = hauteur + yStart - hauteurBarre;

            // Dessiner la barre (Dégradé de couleur selon la valeur)
            int rouge = 46 + (int)((double)val / maxVal * 100); // De vert à orange
            int vert = 125 - (int)((double)val / maxVal * 50);
            g.setColor(new Color(rouge, vert, 50));
            g.fillRoundRect(x, y, largeurBarre, hauteurBarre, 10, 10);

            // Afficher le montant au-dessus de la barre
            g.setColor(Color.DARK_GRAY);
            g.drawString(val + " Ar", x + 10, y - 10);

            // Afficher le nom du mois sous la barre
            LocalDate moisDate = dateCourante.minusMonths(4 - i);
            String nomMois = moisDate.format(formatter);
            g.setColor(Color.GRAY);
            g.drawString(nomMois, x + 10, hauteur + yStart + 20);
        }
    }

    // --- Forcer le rafraîchissement quand on clique sur la page ---
    public void rafraichir() {
        graphiquePanel.repaint();
    }
}