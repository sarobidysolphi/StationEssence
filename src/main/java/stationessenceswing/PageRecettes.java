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
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titre = new JLabel("Recettes & Statistiques");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(titre, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(new Color(245, 245, 245));

        // -- GAUCHE : Recette totale --
        JPanel recettePanel = new JPanel(new BorderLayout());
        recettePanel.setBackground(Color.WHITE);
        recettePanel.setBorder(BorderFactory.createTitledBorder(" Recette totale "));
        
        labelTotal = new JLabel("0 Ar", SwingConstants.CENTER);
        labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 30));
        labelTotal.setForeground(new Color(46, 125, 50));
        recettePanel.add(labelTotal, BorderLayout.CENTER);
        content.add(recettePanel);

        // -- DROITE : Top 5 meilleurs clients --
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder(" Top 5 meilleurs clients "));
        
        String[] cols = {"RANG", "NOM", "DÉPENSES"};
        modelTop = new DefaultTableModel(new Object[][]{}, cols);
        JTable tableTop = new JTable(modelTop);
        tableTop.setRowHeight(35);
        topPanel.add(new JScrollPane(tableTop), BorderLayout.CENTER);
        content.add(topPanel);

        add(content, BorderLayout.CENTER);
        
        // À chaque fois que la page est créée ou réaffichée, on met à jour !
        rafraichir();
    }
    
    // --- MÉTHODE DE RAFRAÎCHISSEMENT (L'appeler à chaque clic) ---
    public void rafraichir() {
        System.out.println("Rafraîchissement de la page Recettes..."); // Pour vérifier dans la console
        
        // 1. Mise à jour du montant total
        labelTotal.setText(DonneesMemoire.recetteDuJour + " Ar");

        // 2. Mise à jour du Top 5
        modelTop.setRowCount(0); // On vide le tableau
        
        Map<String, Integer> mapClients = new HashMap<>();
        for (DonneesMemoire.Vente v : DonneesMemoire.historiqueVentes) {
            mapClients.put(v.nomClient, mapClients.getOrDefault(v.nomClient, 0) + v.montant);
        }
        
        List<Map.Entry<String, Integer>> list = new ArrayList<>(mapClients.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue())); // Tri décroissant

        for (int i = 0; i < Math.min(5, list.size()); i++) {
            var entry = list.get(i);
            modelTop.addRow(new Object[]{i + 1, entry.getKey(), entry.getValue() + " Ar"});
        }
    }
}