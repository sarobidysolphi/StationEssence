package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PageProduits extends JPanel {
    private DefaultTableModel modele;
    private StyledTable tableau;
    private PlaceholderTextField champRecherche;
    private JComboBox<String> comboFiltreType;

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

        comboFiltreType = new JComboBox<>(new String[]{"Tous les types", "Essence", "Gasoil", "Petrole"});
        comboFiltreType.setPreferredSize(new Dimension(130, 34));
        comboFiltreType.setFont(Theme.POLICE_NORMALE);

        JButton btnNouveau = MacButton.primary("+ Nouveau");
        btnNouveau.addActionListener(e -> ajouterProduit());

        outilPanel.add(champRecherche);
        outilPanel.add(comboFiltreType);
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
        tableCard.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(outilPanel, BorderLayout.CENTER);
        tableCard.add(topPanel, BorderLayout.NORTH);

        String[] colonnes = {"Designation", "Type", "Stock (L)", "Seuil", "Prix/L", "Statut", "ACTIONS"};
        modele = new DefaultTableModel(new Object[][]{}, colonnes) {
            @Override public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        tableau = new StyledTable(modele);
        tableau.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        tableau.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

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
        comboFiltreType.addActionListener(e -> rafraichirTableau());
    }

    private void ajouterProduit() {
        String nom = JOptionPane.showInputDialog(this, "Nom du nouveau produit :");
        if (nom == null || nom.trim().isEmpty()) return;

        String[] types = {"Essence", "Gasoil", "Petrole"};
        String type = (String) JOptionPane.showInputDialog(this, "Type :", "Type",
                JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
        if (type == null) return;

        try {
            int stock = Integer.parseInt(JOptionPane.showInputDialog(this, "Stock (L) :", "0"));
            int seuil = Integer.parseInt(JOptionPane.showInputDialog(this, "Seuil d'alerte (L) :", "50"));
            int prix = Integer.parseInt(JOptionPane.showInputDialog(this, "Prix / L (Ar) :", "0"));

            int nouvelId = DonneesMemoire.listeProduits.size() + 1;
            DonneesMemoire.listeProduits.add(new DonneesMemoire.Produit(nouvelId, nom.trim(), type, stock, seuil, prix));
            rafraichirTableau();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valeurs invalides !");
        }
    }

    public void rafraichirTableau() {
        modele.setRowCount(0);
        String recherche = champRecherche.getText().toLowerCase();
        String typeFiltre = (String) comboFiltreType.getSelectedItem();

        for (DonneesMemoire.Produit p : DonneesMemoire.chargerProduits()) {
            if (!recherche.isEmpty() && !p.designation.toLowerCase().contains(recherche)) continue;
            if (!"Tous les types".equals(typeFiltre) && !p.type.equals(typeFiltre)) continue;
            modele.addRow(new Object[]{p.designation, p.type, p.stock, p.seuil, p.prixParLitre, p.getStatut(), "Actions"});
        }

        for (int i = 0; i < tableau.getRowCount(); i++) {
            String statut = (String) modele.getValueAt(i, 5);
            final Color c;
            switch (statut) {
                case "RUPTURE": c = Theme.ROUGE_ACCENT; break;
                case "FAIBLE": c = Theme.ORANGE_ACCENT; break;
                default: c = Theme.VERT_ACCENT; break;
            }
            tableau.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
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
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
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
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
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
                DonneesMemoire.Produit p = DonneesMemoire.listeProduits.get(currentRow);
                String newName = JOptionPane.showInputDialog("Nouveau nom :", p.designation);
                if (newName != null && !newName.trim().isEmpty()) {
                    String[] types = {"Essence", "Gasoil", "Petrole"};
                    String newType = (String) JOptionPane.showInputDialog(null, "Type :", "Type",
                            JOptionPane.QUESTION_MESSAGE, null, types, p.type);
                    if (newType != null) {
                        try {
                            int newStock = Integer.parseInt(JOptionPane.showInputDialog("Stock (L) :", p.stock));
                            int newSeuil = Integer.parseInt(JOptionPane.showInputDialog("Seuil (L) :", p.seuil));
                            int newPrix = Integer.parseInt(JOptionPane.showInputDialog("Prix/L (Ar) :", p.prixParLitre));
                            p.designation = newName.trim();
                            p.type = newType;
                            p.stock = newStock;
                            p.seuil = newSeuil;
                            p.prixParLitre = newPrix;
                            rafraichirTableau();
                        } catch (Exception ex) {}
                    }
                }
            });

            btnSupprimer.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(null, "Supprimer ce produit ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    DonneesMemoire.listeProduits.remove(currentRow);
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
