package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PageTableauBord extends JPanel {

    private JLabel labelRecette, labelStockTotal, labelAlertes, labelReferences;
    private JPanel stockPanel, alertesPanel;

    public PageTableauBord() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titre = new JLabel("Tableau de bord");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titre.setForeground(new Color(30, 30, 30));
        add(titre, BorderLayout.NORTH);

        // Cartes du haut
        JPanel cartesPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cartesPanel.setBackground(new Color(245, 247, 250));
        cartesPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        labelRecette = new JLabel("0 Ar");
        labelStockTotal = new JLabel("0 L");
        labelAlertes = new JLabel("0");
        labelReferences = new JLabel("0");

        cartesPanel.add(creerCarte("Recette du jour", labelRecette, new Color(40, 80, 200)));
        cartesPanel.add(creerCarte("Stock total", labelStockTotal, new Color(40, 80, 200)));
        cartesPanel.add(creerCarte("Produits en alerte", labelAlertes, Color.RED));
        cartesPanel.add(creerCarte("Produits référencés", labelReferences, new Color(40, 80, 200)));

        add(cartesPanel, BorderLayout.CENTER);

        // Section du bas (Stock actuel + Alertes)
        JPanel basPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        basPanel.setBackground(new Color(245, 247, 250));

        stockPanel = new JPanel();
        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        stockPanel.setBackground(Color.WHITE);
        stockPanel.setBorder(BorderFactory.createTitledBorder(" Stock actuel "));

        alertesPanel = new JPanel();
        alertesPanel.setLayout(new BoxLayout(alertesPanel, BoxLayout.Y_AXIS));
        alertesPanel.setBackground(Color.WHITE);
        alertesPanel.setBorder(BorderFactory.createTitledBorder(" Alertes de stock "));

        basPanel.add(stockPanel);
        basPanel.add(alertesPanel);
        add(basPanel, BorderLayout.SOUTH);

        // Premier chargement
        rafraichir();
    }

    private JPanel creerCarte(String titre, JLabel valeurLabel, Color couleur) {
        JPanel carte = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
            }
        };
        carte.setOpaque(false);
        carte.setBorder(new EmptyBorder(10, 10, 10, 10));

        valeurLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valeurLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valeurLabel.setForeground(couleur);

        JLabel labelTitre = new JLabel(titre, SwingConstants.CENTER);
        labelTitre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelTitre.setForeground(Color.GRAY);

        carte.add(valeurLabel, BorderLayout.CENTER);
        carte.add(labelTitre, BorderLayout.SOUTH);
        return carte;
    }

    public void rafraichir() {
        // 1. Recette du jour
        labelRecette.setText(DonneesMemoire.recetteDuJour + " Ar");

        // 2. Stock total
        int stockTotal = 0;
        for (DonneesMemoire.Produit p : DonneesMemoire.chargerProduits()) stockTotal += p.stock;
        labelStockTotal.setText(stockTotal + " L");

        // 3. Produits en alerte
        int alerte = 0;
        for (DonneesMemoire.Produit p : DonneesMemoire.chargerProduits()) {
            if (p.stock < p.seuil) alerte++;
        }
        labelAlertes.setText(String.valueOf(alerte));

        // 4. Produits référencés
        labelReferences.setText(String.valueOf(DonneesMemoire.chargerProduits().size()));

        // 5. Mise à jour des listes du bas
        stockPanel.removeAll();
        alertesPanel.removeAll();

        // Liste des produits
        for (DonneesMemoire.Produit p : DonneesMemoire.chargerProduits()) {
            JPanel ligne = new JPanel(new BorderLayout());
            ligne.setBackground(Color.WHITE);
            ligne.setBorder(new EmptyBorder(5, 10, 5, 10));
            JLabel nom = new JLabel("🛢️ " + p.designation);
            nom.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JLabel qte = new JLabel(p.stock + " L");
            qte.setFont(new Font("Segoe UI", Font.BOLD, 14));
            qte.setForeground(new Color(46, 125, 50));
            ligne.add(nom, BorderLayout.WEST);
            ligne.add(qte, BorderLayout.EAST);
            stockPanel.add(ligne);
        }
        stockPanel.revalidate();
        stockPanel.repaint();

        // Liste des alertes
        boolean aDesAlertes = false;
        for (DonneesMemoire.Produit p : DonneesMemoire.chargerProduits()) {
            if (p.stock < p.seuil) {
                aDesAlertes = true;
                JLabel msg = new JLabel("⚠️ " + p.designation + " : " + p.stock + " L (Seuil : " + p.seuil + " L)");
                msg.setForeground(Color.RED);
                alertesPanel.add(msg);
            }
        }
        if (!aDesAlertes) {
            alertesPanel.add(new JLabel("✅ Aucune alerte, tout va bien !"));
        }
        alertesPanel.revalidate();
        alertesPanel.repaint();
    }
}