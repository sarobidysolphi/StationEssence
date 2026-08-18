package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRecettes extends JPanel {
    private JLabel labelTotal;
    private DefaultTableModel modelDetail, modelTop;

    public PageRecettes() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Recettes");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titre, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(new Color(245, 247, 250));

        // -- GAUCHE : Détail des recettes --
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createTitledBorder(" Détail des recettes "));
        String[] cols1 = {"DATE", "TYPE", "DÉTAIL", "MONTANT"};
        modelDetail = new DefaultTableModel(new Object[][]{}, cols1);
        JTable table1 = new JTable(modelDetail);
        table1.setRowHeight(30);
        table1.getTableHeader().setBackground(new Color(40, 80, 200));
        table1.getTableHeader().setForeground(Color.WHITE);
        detailPanel.add(new JScrollPane(table1), BorderLayout.CENTER);
        content.add(detailPanel);

        // -- DROITE : Top 5 clients --
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder(" Top 5 meilleurs clients "));
        String[] cols2 = {"RANG", "NOM", "DÉPENSES"};
        modelTop = new DefaultTableModel(new Object[][]{}, cols2);
        JTable table2 = new JTable(modelTop);
        table2.setRowHeight(30);
        table2.getTableHeader().setBackground(new Color(40, 80, 200));
        table2.getTableHeader().setForeground(Color.WHITE);
        topPanel.add(new JScrollPane(table2), BorderLayout.CENTER);
        content.add(topPanel);

        add(content, BorderLayout.CENTER);
        
        // --- CORRECTION : Initialisation de labelTotal avec GridBagLayout ---
        JPanel totalPanel = new JPanel(new GridBagLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createTitledBorder(" Recette totale "));
        labelTotal = new JLabel("0 Ar", SwingConstants.CENTER);
        labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 40));
        labelTotal.setForeground(new Color(40, 80, 200));
        totalPanel.add(labelTotal);
        add(totalPanel, BorderLayout.SOUTH);
        
        rafraichir();
    }

    public void rafraichir() {
        // Rafraichir la recette totale
        if (labelTotal != null) {
            labelTotal.setText(DonneesMemoire.recetteDuJour + " Ar");
        }

        // Rafraichir le détail
        modelDetail.setRowCount(0);
        for (DonneesMemoire.Vente v : DonneesMemoire.historiqueVentes) {
            modelDetail.addRow(new Object[]{v.date, "Carburant", v.nomClient + " (" + v.litres + " L)", v.montant + " Ar"});
        }

        // Rafraichir le Top 5
        modelTop.setRowCount(0);
        Map<String, Integer> map = new HashMap<>();
        for (DonneesMemoire.Vente v : DonneesMemoire.historiqueVentes) {
            map.put(v.nomClient, map.getOrDefault(v.nomClient, 0) + v.montant);
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        for (int i = 0; i < Math.min(5, list.size()); i++) {
            var e = list.get(i);
            modelTop.addRow(new Object[]{i + 1, e.getKey(), e.getValue() + " Ar"});
        }
    }
}