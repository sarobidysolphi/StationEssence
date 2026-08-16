package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PageProduits extends JPanel {
    private DefaultTableModel modele;
    private JTable tableau;
    private JTextField champRecherche;

    public PageProduits() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Titre
        JLabel titre = new JLabel("Gestion des produits");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(titre, BorderLayout.NORTH);

        // --- BARRE D'OUTILS (Recherche + Nouveau) ---
        JPanel outilPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        outilPanel.setBackground(new Color(245, 245, 245));

        // 1. Le champ de recherche (C'est ici que la magie opère pour le LIKE % %)
        champRecherche = new JTextField("Rechercher un produit...", 20);
        champRecherche.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Ajout d'un écouteur qui déclenche la recherche à chaque frappe clavier
        champRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrerTableau(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrerTableau(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrerTableau(); }
        });

        // 2. Le bouton Nouveau
        JButton btnNouveau = new JButton("+ Nouveau");
        btnNouveau.setBackground(new Color(30, 45, 40));
        btnNouveau.setForeground(Color.WHITE);
        btnNouveau.setFocusPainted(false);
        btnNouveau.addActionListener(e -> ajouterProduit());

        outilPanel.add(champRecherche);
        outilPanel.add(btnNouveau);
        add(outilPanel, BorderLayout.CENTER);

        // --- TABLEAU ---
        String[] colonnes = {"Numéro", "Désignation", "Stock (L)"};
        modele = new DefaultTableModel(new Object[][]{}, colonnes) {
            @Override 
            public boolean isCellEditable(int row, int col) { 
                return false; 
            }
        };
        tableau = new JTable(modele);
        tableau.setRowHeight(30);
        tableau.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tableau);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.SOUTH);

        // Chargement initial
        rafraichirTableau();
    }

    // --- AJOUTER UN PRODUIT ---
    private void ajouterProduit() {
        String nom = JOptionPane.showInputDialog(this, "Nom du nouveau produit :");
        if (nom == null || nom.trim().isEmpty()) return;

        int nouvelId = DonneesMemoire.listeProduits.size() + 1;
        DonneesMemoire.listeProduits.add(new DonneesMemoire.Produit(nouvelId, nom, 0, 10, 1000));
        
        JOptionPane.showMessageDialog(this, "Produit '" + nom + "' ajouté avec succès !");
        rafraichirTableau();
    }

    // --- RAFRAÎCHIR LE TABLEAU (Affiche tout) ---
    private void rafraichirTableau() {
        modele.setRowCount(0);
        for (DonneesMemoire.Produit p : DonneesMemoire.chargerProduits()) {
            modele.addRow(new Object[]{p.numProd, p.designation, p.stock});
        }
    }

    // --- FILTRER LE TABLEAU (La fameuse méthode LIKE % %) ---
    private void filtrerTableau() {
        String recherche = champRecherche.getText().trim().toLowerCase();
        modele.setRowCount(0); // On vide le tableau

        // Si la recherche est vide ou contient le texte par défaut, on affiche tout
        if (recherche.isEmpty() || recherche.equals("rechercher un produit...")) {
            rafraichirTableau();
            return;
        }

        // On parcourt la liste des produits en mémoire
        for (DonneesMemoire.Produit p : DonneesMemoire.chargerProduits()) {
            // Ici, on simule le "LIKE %mot%". 
            // Si le nom du produit CONTIENT le texte tapé (même en minuscule), on l'affiche.
            if (p.designation.toLowerCase().contains(recherche)) {
                modele.addRow(new Object[]{p.numProd, p.designation, p.stock});
            }
        }
    }
}