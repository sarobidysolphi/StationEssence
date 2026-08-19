package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PageTableauBord extends JPanel {

    private JLabel labelRecette, labelStockTotal, labelAlertes, labelReferences;
    private JPanel stockPanel, alertesPanel;

    public PageTableauBord() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Tableau de bord");
        titre.setFont(Theme.POLICE_TITRE);
        titre.setForeground(Theme.TEXTE_FONCE);
        JLabel sousTitre = new JLabel("Vue d'ensemble de la station");
        sousTitre.setFont(Theme.POLICE_SOUS_TITRE);
        sousTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel cartesPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        cartesPanel.setBackground(Theme.FOND_CLAIR);
        cartesPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        labelRecette = new JLabel("0 Ar");
        labelStockTotal = new JLabel("0 L");
        labelAlertes = new JLabel("0");
        labelReferences = new JLabel("0");

        cartesPanel.add(creerCarteApple("Recette totale", labelRecette, Theme.BLEU_ACCENT, "\uD83D\uDCB0"));
        cartesPanel.add(creerCarteApple("Stock total", labelStockTotal, Theme.VERT_ACCENT, "\u26FD"));
        cartesPanel.add(creerCarteApple("En alerte", labelAlertes, Theme.ROUGE_ACCENT, "\u26A0\uFE0F"));
        cartesPanel.add(creerCarteApple("References", labelReferences, Theme.VIOLET_ACCENT, "\uD83D\uDCCB"));

        add(cartesPanel, BorderLayout.CENTER);

        JPanel basPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        basPanel.setBackground(Theme.FOND_CLAIR);

        stockPanel = new JPanel();
        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        stockPanel.setBackground(Theme.FOND_CARTE);
        stockPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDURE_CLAIRE),
            new EmptyBorder(12, 16, 12, 16)
        ));

        alertesPanel = new JPanel();
        alertesPanel.setLayout(new BoxLayout(alertesPanel, BoxLayout.Y_AXIS));
        alertesPanel.setBackground(Theme.FOND_CARTE);
        alertesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDURE_CLAIRE),
            new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel stockWrapper = new JPanel(new BorderLayout());
        stockWrapper.setBackground(Theme.FOND_CARTE);
        stockWrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDURE_CLAIRE));
        JLabel stockTitre = new JLabel("   Stock actuel");
        stockTitre.setFont(Theme.POLICE_GRAS);
        stockTitre.setForeground(Theme.TEXTE_FONCE);
        stockTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        stockWrapper.add(stockTitre, BorderLayout.NORTH);
        stockWrapper.add(stockPanel, BorderLayout.CENTER);

        JPanel alertesWrapper = new JPanel(new BorderLayout());
        alertesWrapper.setBackground(Theme.FOND_CARTE);
        alertesWrapper.setBorder(BorderFactory.createLineBorder(Theme.BORDURE_CLAIRE));
        JLabel alertesTitre = new JLabel("   Alertes de stock (< 10 L)");
        alertesTitre.setFont(Theme.POLICE_GRAS);
        alertesTitre.setForeground(Theme.TEXTE_FONCE);
        alertesTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        alertesWrapper.add(alertesTitre, BorderLayout.NORTH);
        alertesWrapper.add(alertesPanel, BorderLayout.CENTER);

        basPanel.add(stockWrapper);
        basPanel.add(alertesWrapper);

        add(basPanel, BorderLayout.SOUTH);

        rafraichir();
    }

    private JPanel creerCarteApple(String titre, JLabel valeurLabel, Color couleur, String emoji) {
        JPanel carte = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.setColor(couleur);
                g2.fillRoundRect(0, 0, 4, getHeight() - 2, 4, 4);
            }
        };
        carte.setOpaque(false);
        carte.setBorder(new EmptyBorder(14, 18, 14, 14));

        JPanel centrePanel = new JPanel(new BorderLayout());
        centrePanel.setOpaque(false);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        centrePanel.add(emojiLabel, BorderLayout.WEST);

        valeurLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        valeurLabel.setFont(Theme.POLICE_GRANDE);
        valeurLabel.setForeground(couleur);
        centrePanel.add(valeurLabel, BorderLayout.CENTER);

        carte.add(centrePanel, BorderLayout.CENTER);

        JLabel labelTitre = new JLabel(titre);
        labelTitre.setFont(Theme.POLICE_PETITE);
        labelTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        carte.add(labelTitre, BorderLayout.SOUTH);
        return carte;
    }

    public void rafraichir() {
        int totalRecette = AchatDAO.getRecetteTotale();
        labelRecette.setText(String.format("%,d", totalRecette) + " FCFA");

        List<Produit> produits = ProduitDAO.getAll();
        int stockTotal = 0;
        for (Produit p : produits) stockTotal += p.getStock();
        labelStockTotal.setText(stockTotal + " L");

        int alerte = 0;
        for (Produit p : produits) {
            if (p.getStock() < 10) alerte++;
        }
        labelAlertes.setText(String.valueOf(alerte));
        labelReferences.setText(String.valueOf(produits.size()));

        stockPanel.removeAll();
        for (Produit p : produits) {
            JPanel ligne = new JPanel(new BorderLayout());
            ligne.setBackground(Theme.FOND_CARTE);
            ligne.setBorder(new EmptyBorder(6, 4, 6, 4));
            JLabel nom = new JLabel(p.getDesignation());
            nom.setFont(Theme.POLICE_NORMALE);
            nom.setForeground(Theme.TEXTE_FONCE);
            JLabel qte = new JLabel(p.getStock() + " L");
            qte.setFont(Theme.POLICE_GRAS);

            if (p.getStock() < 10) qte.setForeground(Theme.ROUGE_ACCENT);
            else qte.setForeground(Theme.VERT_ACCENT);

            ligne.add(nom, BorderLayout.WEST);
            ligne.add(qte, BorderLayout.EAST);
            stockPanel.add(ligne);
            stockPanel.add(Box.createVerticalStrut(2));
        }
        stockPanel.revalidate();
        stockPanel.repaint();

        alertesPanel.removeAll();
        boolean aDesAlertes = false;
        for (Produit p : produits) {
            if (p.getStock() < 10) {
                aDesAlertes = true;
                String statutText = p.getStock() <= 0 ? "RUPTURE" : "Stock faible";
                JLabel msg = new JLabel("  " + statutText + " : " + p.getDesignation() + " (" + p.getStock() + " L)");
                msg.setFont(Theme.POLICE_NORMALE);
                msg.setOpaque(true);
                msg.setBackground(p.getStock() <= 0 ? new Color(255, 59, 48, 15) : new Color(255, 149, 0, 15));
                msg.setForeground(p.getStock() <= 0 ? Theme.ROUGE_ACCENT : Theme.ORANGE_ACCENT);
                msg.setBorder(new EmptyBorder(4, 4, 4, 4));
                alertesPanel.add(msg);
                alertesPanel.add(Box.createVerticalStrut(2));
            }
        }
        if (!aDesAlertes) {
            JLabel ok = new JLabel("  Aucune alerte, tout va bien !");
            ok.setFont(Theme.POLICE_NORMALE);
            ok.setForeground(Theme.VERT_ACCENT);
            alertesPanel.add(ok);
        }
        alertesPanel.revalidate();
        alertesPanel.repaint();
    }
}
