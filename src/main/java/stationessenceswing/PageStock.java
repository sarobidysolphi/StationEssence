package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

        String[] cols = {"NUM", "PRODUIT", "QUANTITE", "DATE", "ACTIONS"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        tableau = new StyledTable(modeleHistorique);
        tableau.getColumnModel().getColumn(4).setPreferredWidth(200);
        tableau.getColumnModel().getColumn(4).setMinWidth(200);
        tableau.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        tableau.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        histCard.add(new JScrollPane(tableau), BorderLayout.CENTER);

        contenu.add(histCard);
        add(contenu, BorderLayout.CENTER);

        comboProduits.addActionListener(e -> mettreAJourStockAvant());
        btnValider.addActionListener(e -> validerEntree());

        remplirCombo();
    }

    public void remplirCombo() {
        comboProduits.removeAllItems();
        List<Produit> produits = ProduitDAO.getAll();
        for (Produit p : produits) {
            comboProduits.addItem(p.getNumProd() + " - " + p.getDesignation() + " (" + p.getStock() + " L)");
        }
        modeleHistorique.setRowCount(0);
        for (Entree e : EntreeDAO.getAll()) {
            modeleHistorique.addRow(new Object[]{e.getNumEntree(), e.getNumProd(), e.getStockEntree(), e.getDateEntree(), "Actions"});
        }
        mettreAJourStockAvant();
    }

    private void mettreAJourStockAvant() {
        int idx = comboProduits.getSelectedIndex();
        if (idx >= 0) {
            List<Produit> produits = ProduitDAO.getAll();
            if (idx < produits.size()) {
                int stock = produits.get(idx).getStock();
                labelStockAvant.setText("Stock avant : " + stock + " L");
                labelStockApres.setText("Stock apres : " + stock + " L");
            }
        }
    }

    private void validerEntree() {
        try {
            int idx = comboProduits.getSelectedIndex();
            if (idx == -1) { JOptionPane.showMessageDialog(this, "Aucun produit disponible !"); return; }
            int qte = Integer.parseInt(champQuantite.getText());
            if (qte <= 0) { JOptionPane.showMessageDialog(this, "La quantite doit etre superieure a 0 !"); return; }

            List<Produit> produits = ProduitDAO.getAll();
            Produit p = produits.get(idx);
            String numProd = p.getNumProd();
            String idEntree = EntreeDAO.genererId();
            String dateAujourdhui = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

            Entree entree = new Entree(idEntree, p.getDesignation(), qte, dateAujourdhui);
            if (EntreeDAO.ajouter(entree, numProd)) {
                labelStockApres.setText("Stock apres : " + (p.getStock() + qte) + " L");
                JOptionPane.showMessageDialog(this, qte + " L ajoutes au stock !");
                champQuantite.setText("");
                remplirCombo();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'entree !");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erreur de saisie !");
        }
    }

    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton btnModifier, btnSupprimer;
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));
            setBackground(Color.WHITE);
            btnModifier = MacButton.ghost("Modifier");
            btnModifier.setPreferredSize(new Dimension(70, 28));
            btnModifier.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnSupprimer = MacButton.danger("Supprimer");
            btnSupprimer.setPreferredSize(new Dimension(70, 28));
            btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 11));
            add(btnModifier); add(btnSupprimer);
        }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnModifier, btnSupprimer;
        private int currentRow;
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            panel.setBackground(Color.WHITE);
            btnModifier = MacButton.ghost("Modifier");
            btnModifier.setPreferredSize(new Dimension(70, 28));
            btnModifier.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnSupprimer = MacButton.danger("Supprimer");
            btnSupprimer.setPreferredSize(new Dimension(70, 28));
            btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 11));
            panel.add(btnModifier); panel.add(btnSupprimer);

            btnModifier.addActionListener(e -> {
                fireEditingStopped();
                String numEntree = (String) modeleHistorique.getValueAt(currentRow, 0);
                String numProd = (String) modeleHistorique.getValueAt(currentRow, 1);
                int qteActuelle = (int) modeleHistorique.getValueAt(currentRow, 2);

                JTextField champQte = new JTextField(String.valueOf(qteActuelle), 10);
                JPanel form = new JPanel(new GridLayout(1, 2, 8, 8));
                form.add(new JLabel("Quantite (L) :")); form.add(champQte);

                int result = JOptionPane.showConfirmDialog(null, form, "Modifier l'entree", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int nouvelleQte = Integer.parseInt(champQte.getText().trim());
                        if (nouvelleQte <= 0) { JOptionPane.showMessageDialog(null, "La quantite doit etre superieure a 0 !"); return; }
                        Produit p = ProduitDAO.getById(numProd);
                        if (p != null) {
                            EntreeDAO.modifier(numEntree, p.getNumProd(), nouvelleQte);
                        }
                        remplirCombo();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Quantite invalide !");
                    }
                }
            });

            btnSupprimer.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(null, "Supprimer cette entree ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String numEntree = (String) modeleHistorique.getValueAt(currentRow, 0);
                    EntreeDAO.supprimer(numEntree);
                    remplirCombo();
                }
            });
        }
        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        @Override public Object getCellEditorValue() { return "Actions"; }
    }
}
