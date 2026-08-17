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
    private DefaultTableModel modelTop;

    public PageRecettes() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("📊 Recettes & Performances");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titre.setForeground(new Color(30, 30, 30));
        add(titre, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(new Color(245, 247, 250));

        JPanel totalPanel = new JPanel(new GridBagLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createTitledBorder(" Recette totale "));
        labelTotal = new JLabel("0 Ar", SwingConstants.CENTER);
        labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 40));
        labelTotal.setForeground(new Color(40, 80, 200));
        totalPanel.add(labelTotal);
        content.add(totalPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder(" 🏆 Top 5 meilleurs clients "));
        String[] cols = {"RANG", "NOM", "DÉPENSES"};
        modelTop = new DefaultTableModel(new Object[][]{}, cols);
        JTable table = new JTable(modelTop);
        table.setRowHeight(35);
        table.getTableHeader().setBackground(new Color(40, 80, 200));
        table.getTableHeader().setForeground(Color.WHITE);
        topPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(topPanel);

        add(content, BorderLayout.CENTER);
        rafraichir();
    }

    public void rafraichir() {
        labelTotal.setText(DonneesMemoire.recetteDuJour + " Ar");
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