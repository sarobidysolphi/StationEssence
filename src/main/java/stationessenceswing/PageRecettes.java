package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRecettes extends JPanel {
    private JLabel labelTotal;
    private DefaultTableModel modelDetail, modelTop;

    public PageRecettes() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Recettes");
        titre.setFont(Theme.POLICE_TITRE);
        JLabel sousTitre = new JLabel("Detail des recettes et top clients");
        sousTitre.setFont(Theme.POLICE_SOUS_TITRE);
        sousTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setBackground(Theme.FOND_CLAIR);

        // Detail
        JPanel detailCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        detailCard.setOpaque(false);

        JLabel detailTitre = new JLabel("   Detail des recettes");
        detailTitre.setFont(Theme.POLICE_GRAS);
        detailTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        detailCard.add(detailTitre, BorderLayout.NORTH);

        String[] cols1 = {"DATE", "TYPE", "DETAIL", "MONTANT"};
        modelDetail = new DefaultTableModel(new Object[][]{}, cols1);
        JTable table1 = new StyledTable(modelDetail);
        detailCard.add(new JScrollPane(table1), BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Theme.FOND_CARTE);
        totalPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        labelTotal = new JLabel("0 Ar", SwingConstants.CENTER);
        labelTotal.setFont(Theme.POLICE_GRANDE);
        labelTotal.setForeground(Theme.BLEU_ACCENT);
        totalPanel.add(labelTotal, BorderLayout.CENTER);
        detailCard.add(totalPanel, BorderLayout.SOUTH);

        content.add(detailCard);

        // Top 5
        JPanel topCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        topCard.setOpaque(false);

        JLabel topTitre = new JLabel("   Top 5 meilleurs clients");
        topTitre.setFont(Theme.POLICE_GRAS);
        topTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        topCard.add(topTitre, BorderLayout.NORTH);

        String[] cols2 = {"RANG", "NOM", "DEPENSES"};
        modelTop = new DefaultTableModel(new Object[][]{}, cols2);
        JTable table2 = new StyledTable(modelTop);
        topCard.add(new JScrollPane(table2), BorderLayout.CENTER);

        content.add(topCard);
        add(content, BorderLayout.CENTER);

        rafraichir();
    }

    public void rafraichir() {
        modelDetail.setRowCount(0);
        int totalGeneral = 0;

        for (DonneesMemoire.Vente v : DonneesMemoire.historiqueVentes) {
            modelDetail.addRow(new Object[]{v.date, "Carburant", v.nomClient + " - " + v.produit + " (" + v.litres + " L)", String.format("%,d", v.montant) + " Ar"});
            totalGeneral += v.montant;
        }

        for (DonneesMemoire.Entretien e : DonneesMemoire.historiqueEntretiens) {
            modelDetail.addRow(new Object[]{e.date, "Service", e.nomClient + (e.voiture.isEmpty() ? "" : " (" + e.voiture + ")"), String.format("%,d", e.total) + " Ar"});
            totalGeneral += e.total;
        }

        if (labelTotal != null) {
            labelTotal.setText(String.format("%,d", totalGeneral) + " Ar");
        }

        modelTop.setRowCount(0);
        Map<String, Integer> map = new HashMap<>();
        for (DonneesMemoire.Vente v : DonneesMemoire.historiqueVentes) {
            map.put(v.nomClient, map.getOrDefault(v.nomClient, 0) + v.montant);
        }
        for (DonneesMemoire.Entretien e : DonneesMemoire.historiqueEntretiens) {
            map.put(e.nomClient, map.getOrDefault(e.nomClient, 0) + e.total);
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        for (int i = 0; i < Math.min(5, list.size()); i++) {
            var entry = list.get(i);
            modelTop.addRow(new Object[]{i + 1, entry.getKey(), String.format("%,d", entry.getValue()) + " Ar"});
        }
    }
}
