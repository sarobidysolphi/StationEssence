package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PageVente extends JPanel {

    private JComboBox<String> comboCarburant;
    private JTextField champClient;
    private JTextField champLitres;
    private JLabel labelMontantTotal;
    private DefaultTableModel modeleHistorique;

    public PageVente() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- TITRE ---
        JLabel titre = new JLabel("Vente de carburant");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(titre, BorderLayout.NORTH);

        // --- PANNEAU CENTRAL (Formulaire + Tableau Historique) ---
        JPanel contenu = new JPanel(new GridLayout(1, 2, 30, 0));
        contenu.setBackground(new Color(245, 245, 245));

        // --- COLONNE GAUCHE : FORMULAIRE DE VENTE ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(" Nouvelle vente "),
            new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ligne 1 : Carburant
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Carburant :"), gbc);
        gbc.gridx = 1;
        comboCarburant = new JComboBox<>();
        mettreAJourComboCarburant();
        formPanel.add(comboCarburant, gbc);

        // Ligne 2 : Client
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Client :"), gbc);
        gbc.gridx = 1;
        champClient = new JTextField(15);
        formPanel.add(champClient, gbc);

        // Ligne 3 : Litres
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Litres :"), gbc);
        gbc.gridx = 1;
        champLitres = new JTextField(10);
        formPanel.add(champLitres, gbc);

        // Ligne 4 : Montant total (calculé automatiquement)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Total :"), gbc);
        gbc.gridx = 1;
        labelMontantTotal = new JLabel("0 Ar");
        labelMontantTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelMontantTotal.setForeground(new Color(46, 125, 50)); // Vert
        formPanel.add(labelMontantTotal, gbc);

        // Ligne 5 : Bouton Valider
        gbc.gridx = 1; gbc.gridy = 4;
        JButton btnValider = new JButton("Valider la vente");
        btnValider.setBackground(new Color(46, 125, 50));
        btnValider.setForeground(Color.WHITE);
        btnValider.setFocusPainted(false);
        btnValider.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(btnValider, gbc);

        // --- LOGIQUE : Calcul automatique du prix ---
        champLitres.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculerPrix(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculerPrix(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculerPrix(); }
        });
        
        // --- LOGIQUE : Le bouton Valider ---
        btnValider.addActionListener(e -> validerVente());

        // --- COLONNE DROITE : HISTORIQUE DES VENTES ---
        JPanel histPanel = new JPanel(new BorderLayout());
        histPanel.setBackground(Color.WHITE);
        histPanel.setBorder(BorderFactory.createTitledBorder(" Historique des ventes "));

        String[] colsHist = {"PRODUIT", "CLIENT", "LITRES", "MONTANT", "DATE"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, colsHist) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tableHist = new JTable(modeleHistorique);
        tableHist.setRowHeight(30);
        JScrollPane scrollHist = new JScrollPane(tableHist);
        histPanel.add(scrollHist, BorderLayout.CENTER);

        // --- ASSEMBLAGE ---
        contenu.add(formPanel);
        contenu.add(histPanel);
        add(contenu, BorderLayout.CENTER);

        // Charger l'historique existant au démarrage
        rafraichirHistorique();
    }

    // --- Méthode pour calculer le prix automatiquement ---
    private void calculerPrix() {
        try {
            if (comboCarburant.getSelectedIndex() == -1) return;
            int index = comboCarburant.getSelectedIndex();
            int litres = Integer.parseInt(champLitres.getText());
            int prixUnitaire = DonneesMemoire.listeProduits.get(index).prixParLitre;
            int total = litres * prixUnitaire;
            labelMontantTotal.setText(total + " Ar");
        } catch (Exception e) {
            labelMontantTotal.setText("0 Ar");
        }
    }

    // --- Méthode pour valider la vente ---
    private void validerVente() {
        try {
            int index = comboCarburant.getSelectedIndex();
            if (index == -1) { JOptionPane.showMessageDialog(this, "Veuillez sélectionner un carburant !"); return; }
            
            String client = champClient.getText().trim();
            if (client.isEmpty()) { JOptionPane.showMessageDialog(this, "Veuillez entrer le nom du client !"); return; }

            int litres = Integer.parseInt(champLitres.getText());
            if (litres <= 0) { JOptionPane.showMessageDialog(this, "La quantité doit être supérieure à 0 !"); return; }

            DonneesMemoire.Produit p = DonneesMemoire.listeProduits.get(index);

            // Vérification du stock
            if (p.stock < litres) {
                JOptionPane.showMessageDialog(this, "Stock insuffisant ! Il reste : " + p.stock + " L");
                return;
            }

            // Calcul du total
            int total = litres * p.prixParLitre;

            // Mise à jour du stock
            p.stock -= litres;

             DonneesMemoire.ajouterVente(new DonneesMemoire.Vente(client, p.designation, litres, total));
            // Feedback utilisateur
            JOptionPane.showMessageDialog(this, "Vente effectuée !\n" + client + " a acheté " + litres + " L de " + p.designation + " pour " + total + " Ar");

            // Réinitialiser les champs
            champClient.setText("");
            champLitres.setText("");
            labelMontantTotal.setText("0 Ar");
            mettreAJourComboCarburant();
            rafraichirHistorique();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide pour les litres !");
        }
    }

    // --- Méthode pour mettre à jour la liste déroulante des carburants ---
    private void mettreAJourComboCarburant() {
        comboCarburant.removeAllItems();
        for (DonneesMemoire.Produit p : DonneesMemoire.listeProduits) {
            comboCarburant.addItem(p.designation + " (" + p.stock + " L dispo)");
        }
    }

    // --- Méthode pour rafraîchir le tableau de l'historique ---
    private void rafraichirHistorique() {
        modeleHistorique.setRowCount(0);
        for (DonneesMemoire.Vente v : DonneesMemoire.historiqueVentes) {
            modeleHistorique.addRow(new Object[]{v.produit, v.nomClient, v.litres, v.montant + " Ar", v.date});
        }
    }
}