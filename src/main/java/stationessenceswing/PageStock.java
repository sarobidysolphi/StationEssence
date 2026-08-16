package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PageStock extends JPanel {

    private JComboBox<String> comboProduits;
    private JTextField champQuantite;
    private JLabel labelStockAvant, labelStockApres;
    private JButton btnValider;

    // Couleurs du design moderne (Dark Glass)
    private final Color COULEUR_FOND = new Color(18, 25, 42);      // Bleu nuit profond
    private final Color COULEUR_CARTE = new Color(30, 41, 59, 220); // Bleu ardoise semi-transparent
    private final Color COULEUR_BORDURE = new Color(255, 255, 255, 30);
    private final Color VERT_ACCENT = new Color(46, 125, 50);      // Vert forêt moderne
    private final Color VERT_VIF = new Color(76, 175, 80);         // Vert vif pour le bouton

    public PageStock() {
        setLayout(new BorderLayout());
        setBackground(COULEUR_FOND);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        // --- TITRE DE LA PAGE ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titre = new JLabel("Entrée de stock");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titre.setForeground(Color.WHITE);
        
        JLabel sousTitre = new JLabel("Le stock est mis à jour automatiquement");
        sousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sousTitre.setForeground(new Color(255, 255, 255, 180));
        sousTitre.setBorder(new EmptyBorder(5, 0, 20, 0));
        
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- CARTE CENTRALE (LE FORMULAIRE) ---
        JPanel carteCentrale = new JPanel();
        carteCentrale.setLayout(new GridBagLayout());
        carteCentrale.setBackground(COULEUR_CARTE);
        carteCentrale.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COULEUR_BORDURE, 1, true),
            new EmptyBorder(30, 30, 30, 30)
        ));
        // Ombre portée (effet glassmorphism)
        carteCentrale.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            carteCentrale.getBorder()
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- Ligne 1 : Produit (avec design moderne) ---
        JLabel labelProduit = new JLabel("Produit");
        labelProduit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelProduit.setForeground(new Color(255, 255, 255, 200));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        carteCentrale.add(labelProduit, gbc);

        comboProduits = new JComboBox<>();
        comboProduits.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboProduits.setBackground(Color.WHITE);
        comboProduits.setPreferredSize(new Dimension(0, 40));
        gbc.gridy = 1;
        carteCentrale.add(comboProduits, gbc);

        // --- Ligne 2 : Quantité ---
        JLabel labelQuantite = new JLabel("Quantité (L)");
        labelQuantite.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelQuantite.setForeground(new Color(255, 255, 255, 200));
        gbc.gridy = 2;
        carteCentrale.add(labelQuantite, gbc);

        champQuantite = new JTextField();
        champQuantite.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        champQuantite.setPreferredSize(new Dimension(0, 40));
        gbc.gridy = 3;
        carteCentrale.add(champQuantite, gbc);

        // --- Ligne 3 : Affichage Stock Avant / Après ---
        JPanel stockPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        stockPanel.setOpaque(false);
        stockPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        labelStockAvant = new JLabel("Stock avant : -");
        labelStockAvant.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelStockAvant.setForeground(new Color(255, 255, 255, 180));

        labelStockApres = new JLabel("Stock après : -");
        labelStockApres.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelStockApres.setForeground(VERT_VIF);

        stockPanel.add(labelStockAvant);
        stockPanel.add(labelStockApres);
        
        gbc.gridy = 4;
        carteCentrale.add(stockPanel, gbc);

        // --- Ligne 4 : Bouton Valider (Design Ultra Moderne) ---
        btnValider = new JButton("✅ Valider l'entrée");
        btnValider.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnValider.setBackground(VERT_VIF);
        btnValider.setForeground(Color.WHITE);
        btnValider.setFocusPainted(false);
        btnValider.setBorderPainted(false);
        btnValider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnValider.setPreferredSize(new Dimension(0, 50));

        // Effet de survol pour le bouton
        btnValider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnValider.setBackground(VERT_VIF.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnValider.setBackground(VERT_VIF);
            }
        });

        gbc.gridy = 5;
        carteCentrale.add(btnValider, gbc);

        // --- Ajouter la carte centrée ---
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(carteCentrale);
        add(wrapper, BorderLayout.CENTER);

        // --- Charger les produits au démarrage ---
        mettreAJourCombo();
        
        // --- Détection des changements dans le combo pour afficher le stock actuel ---
        comboProduits.addActionListener(e -> mettreAJourStockAvant());

        // --- ACTION DU BOUTON VALIDER ---
        btnValider.addActionListener(e -> validerEntreeStock());
    }

    // --- Méthode pour mettre à jour la liste déroulante ---
    private void mettreAJourCombo() {
        comboProduits.removeAllItems();
        for (DonneesMemoire.Produit p : DonneesMemoire.chargerProduits()) {
            comboProduits.addItem(p.designation + " (" + p.stock + " L)");
        }
        mettreAJourStockAvant();
    }

    // --- Méthode pour afficher le stock avant ---
    private void mettreAJourStockAvant() {
        if (comboProduits.getSelectedIndex() >= 0 && comboProduits.getItemCount() > 0) {
            int index = comboProduits.getSelectedIndex();
            int stock = DonneesMemoire.chargerProduits().get(index).stock;
            labelStockAvant.setText("Stock avant : " + stock + " L");
            labelStockApres.setText("Stock après : " + stock + " L");
        }
    }

    // --- Méthode pour valider l'entrée de stock ---
    private void validerEntreeStock() {
        try {
            if (comboProduits.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit !");
                return;
            }
            
            int index = comboProduits.getSelectedIndex();
            String texteQte = champQuantite.getText().trim();
            if (texteQte.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une quantité !");
                return;
            }

            int quantite = Integer.parseInt(texteQte);
            if (quantite <= 0) {
                JOptionPane.showMessageDialog(this, "La quantité doit être positive !");
                return;
            }

            // Mise à jour du stock
            DonneesMemoire.Produit p = DonneesMemoire.chargerProduits().get(index);
            int ancienStock = p.stock;
            p.stock += quantite;

            // Mise à jour de l'interface
            labelStockApres.setText("Stock après : " + p.stock + " L");
            JOptionPane.showMessageDialog(this, 
                "✅ " + quantite + " L de " + p.designation + " ajoutés !\n" +
                "Stock initial : " + ancienStock + " L\n" +
                "Nouveau stock : " + p.stock + " L",
                "Succès", JOptionPane.INFORMATION_MESSAGE);

            champQuantite.setText("");
            mettreAJourCombo();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide !");
        }
    }
}