package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PageAchat extends JPanel {
    private JComboBox<String> comboProduit;
    private JTextField champClient, champLitres;
    private JLabel labelTotal;
    private DefaultTableModel modeleHistorique;
    private StyledTable tableau;
    private PlaceholderTextField champRecherche;

    public PageAchat() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Vente de carburant");
        titre.setFont(Theme.POLICE_TITRE);
        JLabel sousTitre = new JLabel("Verification du stock et calcul automatique");
        sousTitre.setFont(Theme.POLICE_SOUS_TITRE);
        sousTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 16, 0));
        contenu.setBackground(Theme.FOND_CLAIR);

        JPanel formCard = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        formCard.setOpaque(false);
        formCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(new JLabel("Carburant :"), gbc);
        gbc.gridx = 1; comboProduit = new JComboBox<>(); formCard.add(comboProduit, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(new JLabel("Client :"), gbc);
        gbc.gridx = 1; champClient = new JTextField(15); formCard.add(champClient, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formCard.add(new JLabel("Litres :"), gbc);
        gbc.gridx = 1; champLitres = new JTextField(10); formCard.add(champLitres, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formCard.add(new JLabel("Total :"), gbc);
        gbc.gridx = 1; labelTotal = new JLabel("0 FCFA"); labelTotal.setFont(Theme.POLICE_GRANDE); labelTotal.setForeground(Theme.BLEU_ACCENT); formCard.add(labelTotal, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton btnValider = MacButton.primary("Valider la vente");
        formCard.add(btnValider, gbc);

        contenu.add(formCard);

        JPanel histCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        histCard.setOpaque(false);

        JPanel histToolbar = new JPanel(new BorderLayout());
        histToolbar.setOpaque(false);
        JLabel histTitre = new JLabel("   Historique des ventes");
        histTitre.setFont(Theme.POLICE_GRAS);
        histTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        histToolbar.add(histTitre, BorderLayout.WEST);

        champRecherche = new PlaceholderTextField("Rechercher client...");
        champRecherche.setPreferredSize(new Dimension(180, 30));
        champRecherche.setFont(Theme.POLICE_PETITE);
        champRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { rafraichirHistorique(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { rafraichirHistorique(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { rafraichirHistorique(); }
        });
        histToolbar.add(champRecherche, BorderLayout.EAST);
        histCard.add(histToolbar, BorderLayout.NORTH);

        String[] cols = {"NUM", "PRODUIT", "CLIENT", "LITRES", "DATE"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tableau = new StyledTable(modeleHistorique);
        histCard.add(new JScrollPane(tableau), BorderLayout.CENTER);

        contenu.add(histCard);
        add(contenu, BorderLayout.CENTER);

        champLitres.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
        });
        btnValider.addActionListener(e -> validerVente());

        remplirCombo();
    }

    private void remplirCombo() {
        comboProduit.removeAllItems();
        for (Produit p : ProduitDAO.getAll()) {
            comboProduit.addItem(p.getNumProd() + " - " + p.getDesignation() + " (" + p.getStock() + " L)");
        }
        rafraichirHistorique();
    }

    private void rafraichirHistorique() {
        modeleHistorique.setRowCount(0);
        List<Achat> achats;
        String recherche = champRecherche != null ? champRecherche.getText().trim() : "";
        if (!recherche.isEmpty()) {
            achats = AchatDAO.rechercherParClient(recherche);
        } else {
            achats = AchatDAO.getAll();
        }
        for (Achat a : achats) {
            modeleHistorique.addRow(new Object[]{a.getNumAchat(), a.getNumProd(), a.getNomClient(), a.getNbrLitre(), a.getDateAchat()});
        }
    }

    private void calculerTotal() {
        try {
            int index = comboProduit.getSelectedIndex();
            if (index >= 0 && !champLitres.getText().isEmpty()) {
                int litres = Integer.parseInt(champLitres.getText());
                labelTotal.setText(String.format("%,d", litres * 5200) + " FCFA");
            }
        } catch (Exception e) { labelTotal.setText("0 FCFA"); }
    }

    private void validerVente() {
        try {
            int index = comboProduit.getSelectedIndex();
            String client = champClient.getText().trim();
            String litresStr = champLitres.getText().trim();

            if (client.isEmpty()) { JOptionPane.showMessageDialog(this, "Veuillez entrer un nom de client !"); return; }
            if (litresStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Veuillez entrer une quantite de litres !"); return; }

            int litres = Integer.parseInt(litresStr);
            if (litres <= 0) { JOptionPane.showMessageDialog(this, "La quantite doit etre superieure a 0 !"); return; }

            List<Produit> produits = ProduitDAO.getAll();
            Produit p = produits.get(index);
            if (p.getStock() < litres) {
                JOptionPane.showMessageDialog(this, "Stock insuffisant ! Il reste " + p.getStock() + " L.");
                return;
            }

            String numAchat = AchatDAO.genererId();
            String dateAujourdhui = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            Achat achat = new Achat(numAchat, p.getNumProd(), client, litres, dateAujourdhui);

            if (AchatDAO.ajouter(achat, p.getNumProd())) {
                rafraichirHistorique();
                JOptionPane.showMessageDialog(this, "Vente enregistree !");
                champClient.setText(""); champLitres.setText(""); labelTotal.setText("0 FCFA");
                remplirCombo();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement !");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide pour les litres !");
        }
    }
}
