package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;

public class PageStock extends JPanel {
    private JComboBox<String> comboProduits;
    private JTextField champQuantite;
    private JLabel labelStockAvant, labelStockApres;
    private DefaultTableModel modeleHistorique;
    private JTable tableau;

    public PageStock() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Entrée de stock (CRUD)");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titre, BorderLayout.NORTH);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 20, 0));
        contenu.setBackground(new Color(245, 247, 250));

        // Formulaire
        JPanel formulaire = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        formulaire.setOpaque(false);
        formulaire.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formulaire.add(new JLabel("Produit :"), gbc);
        gbc.gridx = 1; comboProduits = new JComboBox<>(); remplirCombo(); formulaire.add(comboProduits, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formulaire.add(new JLabel("Quantité (L) :"), gbc);
        gbc.gridx = 1; champQuantite = new JTextField(10); formulaire.add(champQuantite, gbc);

        gbc.gridx = 0; gbc.gridy = 2; labelStockAvant = new JLabel("Stock avant : -"); formulaire.add(labelStockAvant, gbc);
        gbc.gridy = 3; labelStockApres = new JLabel("Stock après : -"); labelStockApres.setForeground(new Color(40, 80, 200)); formulaire.add(labelStockApres, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        JButton btnValider = new JButton("Valider l'entrée");
        btnValider.setBackground(new Color(40, 80, 200));
        btnValider.setForeground(Color.WHITE);
        btnValider.setFocusPainted(false);
        btnValider.setBorderPainted(false);
        btnValider.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnValider.setPreferredSize(new Dimension(150, 35));
        formulaire.add(btnValider, gbc);

        contenu.add(formulaire);

        // Historique
        JPanel histPanel = new JPanel(new BorderLayout());
        histPanel.setBackground(Color.WHITE);
        histPanel.setBorder(BorderFactory.createTitledBorder(" Historique des entrées "));
        String[] cols = {"PRODUIT", "QUANTITÉ", "DATE", "ACTIONS"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        tableau = new JTable(modeleHistorique);
        tableau.setRowHeight(40);
        tableau.getTableHeader().setBackground(new Color(40, 80, 200));
        tableau.getTableHeader().setForeground(Color.WHITE);
        histPanel.add(new JScrollPane(tableau), BorderLayout.CENTER);
        contenu.add(histPanel);

        add(contenu, BorderLayout.CENTER);

        // Actions
        comboProduits.addActionListener(e -> mettreAJourStockAvant());
        btnValider.addActionListener(e -> validerEntree());

        tableau.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tableau.rowAtPoint(evt.getPoint());
                int col = tableau.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 3) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Supprimer cette entrée ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DonneesMemoire.historiqueEntrees.remove(row);
                        modeleHistorique.removeRow(row);
                    }
                }
            }
        });
    }

    // --- MÉTHODE PUBLIQUE POUR METTRE À JOUR LA LISTE DÉROULANTE ---
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
            labelStockApres.setText("Stock après : " + stock + " L");
        }
    }

    private void validerEntree() {
        try {
            int idx = comboProduits.getSelectedIndex();
            if (idx == -1) { JOptionPane.showMessageDialog(this, "Aucun produit disponible !"); return; }
            int qte = Integer.parseInt(champQuantite.getText());
            DonneesMemoire.Produit p = DonneesMemoire.chargerProduits().get(idx);
            int avant = p.stock;
            p.stock += qte;
            labelStockApres.setText("Stock après : " + p.stock + " L");
            DonneesMemoire.historiqueEntrees.add(new DonneesMemoire.Entree(p.designation, qte, LocalDate.now().toString()));
            modeleHistorique.addRow(new Object[]{p.designation, qte, LocalDate.now().toString(), "Supprimer"});
            JOptionPane.showMessageDialog(this, qte + " L ajoutés !");
            champQuantite.setText("");
            remplirCombo();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur de saisie !"); }
    }
}