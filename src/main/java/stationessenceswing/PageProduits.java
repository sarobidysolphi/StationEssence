package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PageProduits extends JPanel {
    private DefaultTableModel modele;
    private StyledTable tableau;
    private PlaceholderTextField champRecherche;

    public PageProduits() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Gestion des produits");
        titre.setFont(Theme.POLICE_TITRE);
        titre.setForeground(Theme.TEXTE_FONCE);
        JLabel sousTitre = new JLabel("Ajouter, modifier et supprimer des produits");
        sousTitre.setFont(Theme.POLICE_SOUS_TITRE);
        sousTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel outilPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        outilPanel.setBackground(Theme.FOND_CLAIR);

        champRecherche = new PlaceholderTextField("Rechercher...");
        champRecherche.setPreferredSize(new Dimension(220, 34));
        champRecherche.setFont(Theme.POLICE_NORMALE);

        JButton btnNouveau = MacButton.primary("+ Nouveau");
        btnNouveau.addActionListener(e -> ajouterProduit());

        outilPanel.add(champRecherche);
        outilPanel.add(btnNouveau);

        JPanel tableCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        tableCard.setOpaque(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(outilPanel, BorderLayout.CENTER);
        tableCard.add(topPanel, BorderLayout.NORTH);

        String[] colonnes = {"ID", "DESIGNATION", "STOCK (L)", "STATUT", "ACTIONS"};
        modele = new DefaultTableModel(new Object[][]{}, colonnes) {
            @Override public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        tableau = new StyledTable(modele);
        tableau.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        tableau.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scroll = new JScrollPane(tableau);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(scroll, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);

        rafraichirTableau();

        champRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { rafraichirTableau(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { rafraichirTableau(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { rafraichirTableau(); }
        });
    }

    private void ajouterProduit() {
        JTextField champId = new JTextField(10);
        JTextField champDesign = new JTextField(15);
        JTextField champStock = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Num Produit :")); form.add(champId);
        form.add(new JLabel("Designation :")); form.add(champDesign);
        form.add(new JLabel("Stock (L) :")); form.add(champStock);

        int result = JOptionPane.showConfirmDialog(this, form, "Nouveau produit", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = champId.getText().trim();
                String design = champDesign.getText().trim();
                int stock = Integer.parseInt(champStock.getText().trim());
                if (id.isEmpty() || design.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Remplissez tous les champs !");
                    return;
                }
                if (ProduitDAO.ajouter(new Produit(id, design, stock))) {
                    rafraichirTableau();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout !\n" + ProduitDAO.lastErreur);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Stock invalide !");
            }
        }
    }

    public void rafraichirTableau() {
        modele.setRowCount(0);
        String recherche = champRecherche.getText().toLowerCase();

        List<Produit> produits = ProduitDAO.getAll();
        for (Produit p : produits) {
            if (!recherche.isEmpty() && !p.getDesignation().toLowerCase().contains(recherche)) continue;
            String statut = p.getStock() < 10 ? (p.getStock() <= 0 ? "RUPTURE" : "FAIBLE") : "OK";
            modele.addRow(new Object[]{p.getNumProd(), p.getDesignation(), p.getStock(), statut, "Actions"});
        }

        for (int i = 0; i < tableau.getRowCount(); i++) {
            String statut = (String) modele.getValueAt(i, 3);
            final Color c;
            switch (statut) {
                case "RUPTURE": c = Theme.ROUGE_ACCENT; break;
                case "FAIBLE": c = Theme.ORANGE_ACCENT; break;
                default: c = Theme.VERT_ACCENT; break;
            }
            tableau.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setOpaque(true);
                    label.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : Theme.TABLE_LIGNE_ALTERNE));
                    label.setForeground(c);
                    label.setFont(Theme.POLICE_GRAS);
                    label.setText("  " + value + "  ");
                    return label;
                }
            });
        }
    }

    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton btnModifier, btnSupprimer;
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
            setBackground(Color.WHITE);
            btnModifier = MacButton.ghost("Modifier");
            btnModifier.setPreferredSize(new Dimension(80, 28));
            btnModifier.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnSupprimer = MacButton.danger("Supprimer");
            btnSupprimer.setPreferredSize(new Dimension(80, 28));
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
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            panel.setBackground(Color.WHITE);
            btnModifier = MacButton.ghost("Modifier");
            btnModifier.setPreferredSize(new Dimension(80, 28));
            btnModifier.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnSupprimer = MacButton.danger("Supprimer");
            btnSupprimer.setPreferredSize(new Dimension(80, 28));
            btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 11));
            panel.add(btnModifier); panel.add(btnSupprimer);

            btnModifier.addActionListener(e -> {
                fireEditingStopped();
                List<Produit> produits = ProduitDAO.getAll();
                if (currentRow >= produits.size()) return;
                Produit p = produits.get(currentRow);

                JTextField champDesign = new JTextField(p.getDesignation(), 15);
                JTextField champStock = new JTextField(String.valueOf(p.getStock()), 10);

                JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
                form.add(new JLabel("Designation :")); form.add(champDesign);
                form.add(new JLabel("Stock (L) :")); form.add(champStock);

                int result = JOptionPane.showConfirmDialog(null, form, "Modifier produit", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        p = new Produit(p.getNumProd(), champDesign.getText().trim(),
                                Integer.parseInt(champStock.getText().trim()));
                        ProduitDAO.modifier(p.getNumProd(), p);
                        rafraichirTableau();
                    } catch (Exception ex) {}
                }
            });

            btnSupprimer.addActionListener(e -> {
                fireEditingStopped();
                List<Produit> produits = ProduitDAO.getAll();
                if (currentRow >= produits.size()) return;
                int confirm = JOptionPane.showConfirmDialog(null, "Supprimer ce produit ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ProduitDAO.supprimer(produits.get(currentRow).getNumProd());
                    rafraichirTableau();
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
