package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PageStock extends JPanel {
    private JComboBox<String> comboProduits;
    private JTextField champQuantite;
    private JLabel labelStockAvant, labelStockApres;
    private DefaultTableModel modeleHistorique;
    private StyledTable tableau;

    public PageStock() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Entree de stock");
        titre.setFont(Theme.POLICE_TITRE);
        JLabel sousTitre = new JLabel("Le stock est mis a jour automatiquement");
        sousTitre.setFont(Theme.POLICE_SOUS_TITRE);
        sousTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 16, 0));
        contenu.setBackground(Theme.FOND_CLAIR);

        // Formulaire card
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
        JLabel l1 = new JLabel("Produit :"); l1.setFont(Theme.POLICE_NORMALE); formCard.add(l1, gbc);
        gbc.gridx = 1; comboProduits = new JComboBox<>(); comboProduits.setFont(Theme.POLICE_NORMALE); formCard.add(comboProduits, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Quantite (L) :"); l2.setFont(Theme.POLICE_NORMALE); formCard.add(l2, gbc);
        gbc.gridx = 1; champQuantite = new JTextField(10); champQuantite.setFont(Theme.POLICE_NORMALE); formCard.add(champQuantite, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        labelStockAvant = new JLabel("Stock avant : -"); labelStockAvant.setFont(Theme.POLICE_NORMALE); formCard.add(labelStockAvant, gbc);
        gbc.gridy = 3;
        labelStockApres = new JLabel("Stock apres : -"); labelStockApres.setFont(Theme.POLICE_GRAS); labelStockApres.setForeground(Theme.BLEU_ACCENT); formCard.add(labelStockApres, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton btnValider = MacButton.primary("Valider l'entree");
        formCard.add(btnValider, gbc);

        contenu.add(formCard);

        // Historique card
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

        JLabel histTitre = new JLabel("   Historique des entrees");
        histTitre.setFont(Theme.POLICE_GRAS);
        histTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        histCard.add(histTitre, BorderLayout.NORTH);

        String[] cols = {"PRODUIT", "QUANTITE", "DATE", "ACTIONS"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        tableau = new StyledTable(modeleHistorique);
        histCard.add(new JScrollPane(tableau), BorderLayout.CENTER);

        contenu.add(histCard);
        add(contenu, BorderLayout.CENTER);

        comboProduits.addActionListener(e -> mettreAJourStockAvant());
        btnValider.addActionListener(e -> validerEntree());

        tableau.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tableau.rowAtPoint(evt.getPoint());
                int col = tableau.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 3) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Supprimer cette entree ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DonneesMemoire.historiqueEntrees.remove(row);
                        modeleHistorique.removeRow(row);
                    }
                }
            }
        });

        remplirCombo();
    }

    public void remplirCombo() {
        comboProduits.removeAllItems();
        for (var p : DonneesMemoire.chargerProduits()) {
            comboProduits.addItem(p.designation + " (" + p.stock + " L)");
        }
        mettreAJourStockAvant();
    }

    private void mettreAJourStockAvant() {
        int idx = comboProduits.getSelectedIndex();
        if (idx >= 0) {
            int stock = DonneesMemoire.chargerProduits().get(idx).stock;
            labelStockAvant.setText("Stock avant : " + stock + " L");
            labelStockApres.setText("Stock apres : " + stock + " L");
        }
    }

    private void validerEntree() {
        try {
            int idx = comboProduits.getSelectedIndex();
            if (idx == -1) { JOptionPane.showMessageDialog(this, "Aucun produit disponible !"); return; }
            int qte = Integer.parseInt(champQuantite.getText());
            if (qte <= 0) { JOptionPane.showMessageDialog(this, "La quantite doit etre superieure a 0 !"); return; }
            DonneesMemoire.Produit p = DonneesMemoire.chargerProduits().get(idx);
            p.stock += qte;
            labelStockApres.setText("Stock apres : " + p.stock + " L");
            DonneesMemoire.historiqueEntrees.add(new DonneesMemoire.Entree(p.designation, qte, DonneesMemoire.aujourdHui()));
            modeleHistorique.addRow(new Object[]{p.designation, qte, DonneesMemoire.aujourdHui(), "Supprimer"});
            JOptionPane.showMessageDialog(this, qte + " L ajoutes !");
            champQuantite.setText("");
            remplirCombo();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur de saisie !"); }
    }
}
