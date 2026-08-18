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
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Statistiques");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titre, BorderLayout.NORTH);

        JLabel sousTitre = new JLabel("Recettes des 5 derniers mois");
        sousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sousTitre.setForeground(Color.GRAY);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 247, 250));
        header.add(titre, BorderLayout.NORTH);
        header.add(sousTitre, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        graphiquePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerGraphique(g);
            }
        };
        graphiquePanel.setBackground(Color.WHITE);
        graphiquePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(graphiquePanel, BorderLayout.CENTER);
    }

    private void dessinerGraphique(Graphics g) {
        int largeur = getWidth() - 100;
        int hauteur = getHeight() - 100;
        int xStart = 80;
        int yStart = 40;

        int[] donnees = DonneesMemoire.getRecettes5DerniersMois();
        int maxVal = 1;
        for (int val : donnees) if (val > maxVal) maxVal = val;

        g.setColor(new Color(200, 200, 200));
        g.drawLine(xStart, yStart, xStart, hauteur + yStart);
        g.drawLine(xStart, hauteur + yStart, largeur + xStart, hauteur + yStart);

        int largeurBarre = 60;
        int espace = 40;
        int debutX = xStart + 40;

        LocalDate dateCourante = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yy", new Locale("fr", "FR"));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        for (int i = 0; i < donnees.length; i++) {
            int val = donnees[i];
            int hauteurBarre = (int)((double)val / maxVal * (hauteur - 40));
            if (hauteurBarre == 0) hauteurBarre = 10;

            int x = debutX + i * (largeurBarre + espace);
            int y = hauteur + yStart - hauteurBarre;

            g.setColor(new Color(40, 80, 200));
            g.fillRoundRect(x, y, largeurBarre, hauteurBarre, 10, 10);

            g.setColor(Color.DARK_GRAY);
            g.drawString(val + " Ar", x + 10, y - 10);

            LocalDate moisDate = dateCourante.minusMonths(4 - i);
            String nomMois = moisDate.format(formatter);
            g.setColor(Color.GRAY);
            g.drawString(nomMois, x + 10, hauteur + yStart + 20);
        }
    }

    public void rafraichir() {
        graphiquePanel.repaint();
    }
}