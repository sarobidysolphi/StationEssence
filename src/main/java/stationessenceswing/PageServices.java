package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PageServices extends JPanel {
    private DefaultTableModel modele;
    private StyledTable tableau;
    private PlaceholderTextField champRecherche;

    public PageServices() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Gestion des services");
        titre.setFont(Theme.POLICE_TITRE);
        titre.setForeground(Theme.TEXTE_FONCE);
        JLabel sousTitre = new JLabel("Ajouter, modifier et supprimer des services");
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

        JButton btnNouveau = MacButton.primary("+ Nouveau service");
        btnNouveau.addActionListener(e -> ajouterService());

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

        String[] colonnes = {"SERVICE", "PRIX (AR)", "ACTIONS"};
        modele = new DefaultTableModel(new Object[][]{}, colonnes) {
            @Override public boolean isCellEditable(int row, int col) { return col == 2; }
        };
        tableau = new StyledTable(modele);
        tableau.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        tableau.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));

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

    private void ajouterService() {
        String nom = JOptionPane.showInputDialog(this, "Nom du nouveau service :");
        if (nom == null || nom.trim().isEmpty()) return;
        try {
            String prixStr = JOptionPane.showInputDialog(this, "Prix (Ar) :");
            int prix = Integer.parseInt(prixStr);
            DonneesMemoire.listeServices.add(new DonneesMemoire.Service(DonneesMemoire.listeServices.size() + 1, nom, prix));
            rafraichirTableau();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Prix invalide !");
        }
    }

    public void rafraichirTableau() {
        modele.setRowCount(0);
        String recherche = champRecherche.getText().toLowerCase();
        for (DonneesMemoire.Service s : DonneesMemoire.listeServices) {
            if (!recherche.isEmpty() && !s.nom.toLowerCase().contains(recherche)) continue;
            modele.addRow(new Object[]{s.nom, String.format("%,d", s.prix), "Actions"});
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
                DonneesMemoire.Service s = DonneesMemoire.listeServices.get(currentRow);
                String newNom = JOptionPane.showInputDialog("Nouveau nom :", s.nom);
                if (newNom != null && !newNom.trim().isEmpty()) {
                    String newPrixStr = JOptionPane.showInputDialog("Nouveau prix :", s.prix);
                    try {
                        s.nom = newNom;
                        s.prix = Integer.parseInt(newPrixStr);
                        rafraichirTableau();
                    } catch (Exception ex) {}
                }
            });

            btnSupprimer.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(null, "Supprimer ce service ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    DonneesMemoire.listeServices.remove(currentRow);
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
