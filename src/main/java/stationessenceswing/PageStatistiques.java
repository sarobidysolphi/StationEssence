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
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Statistiques");
        titre.setFont(Theme.POLICE_TITRE);
        JLabel sousTitre = new JLabel("Recettes des 5 derniers mois");
        sousTitre.setFont(Theme.POLICE_SOUS_TITRE);
        sousTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel chartCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        chartCard.setOpaque(false);

        graphiquePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerGraphique(g);
            }
        };
        graphiquePanel.setOpaque(false);
        graphiquePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        chartCard.add(graphiquePanel, BorderLayout.CENTER);

        add(chartCard, BorderLayout.CENTER);
    }

    private void dessinerGraphique(Graphics g) {
        int largeur = getWidth() - 100;
        int hauteur = getHeight() - 100;
        int xStart = 80;
        int yStart = 40;

        int[] donnees = DonneesMemoire.getRecettes5DerniersMois();
        int maxVal = 1;
        for (int val : donnees) if (val > maxVal) maxVal = val;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Theme.BORDURE_CLAIRE);
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(xStart, yStart, xStart, hauteur + yStart);
        g2.drawLine(xStart, hauteur + yStart, largeur + xStart, hauteur + yStart);

        int largeurBarre = 60;
        int espace = 40;
        int debutX = xStart + 40;

        LocalDate dateCourante = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yy", new Locale("fr", "FR"));

        for (int i = 0; i < donnees.length; i++) {
            int val = donnees[i];
            int hauteurBarre = (int)((double)val / maxVal * (hauteur - 40));
            if (hauteurBarre == 0) hauteurBarre = 10;

            int x = debutX + i * (largeurBarre + espace);
            int y = hauteur + yStart - hauteurBarre;

            // Ombre douce
            g2.setColor(new Color(0, 122, 255, 20));
            g2.fillRoundRect(x + 3, y + 3, largeurBarre, hauteurBarre, 12, 12);

            // Barre avec degradé bleu macOS
            GradientPaint gradient = new GradientPaint(x, y, Theme.BLEU_ACCENT, x, hauteur + yStart, new Color(0, 90, 210));
            g2.setPaint(gradient);
            g2.fillRoundRect(x, y, largeurBarre, hauteurBarre, 12, 12);

            // Valeur
            g2.setColor(Theme.TEXTE_FONCE);
            g2.setFont(Theme.POLICE_GRAS);
            g2.drawString(String.format("%,d", val) + " Ar", x + 5, y - 10);

            // Mois
            LocalDate moisDate = dateCourante.minusMonths(4 - i);
            String nomMois = moisDate.format(formatter);
            g2.setColor(Theme.TEXTE_SECONDAIRE);
            g2.setFont(Theme.POLICE_PETITE);
            g2.drawString(nomMois, x + 10, hauteur + yStart + 20);
        }
    }

    public void rafraichir() {
        graphiquePanel.repaint();
    }
}
