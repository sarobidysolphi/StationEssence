package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PageServices extends JPanel {
    private DefaultTableModel modele;
    private JTable tableau;
    private PlaceholderTextField champRecherche;

    public PageServices() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Gestion des services (CRUD)");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titre.setForeground(new Color(30, 30, 30));
        add(titre, BorderLayout.NORTH);

        JPanel outilPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        outilPanel.setBackground(new Color(245, 247, 250));

        champRecherche = new PlaceholderTextField("Rechercher un service...");
        champRecherche.setPreferredSize(new Dimension(200, 30));
        champRecherche.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnNouveau = new JButton("+ Nouveau service");
        btnNouveau.setBackground(new Color(40, 80, 200));
        btnNouveau.setForeground(Color.WHITE);
        btnNouveau.setFocusPainted(false);
        btnNouveau.setBorderPainted(false);
        btnNouveau.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNouveau.setPreferredSize(new Dimension(150, 30));
        btnNouveau.addActionListener(e -> ajouterService());

        outilPanel.add(champRecherche);
        outilPanel.add(btnNouveau);
        add(outilPanel, BorderLayout.CENTER);

        JPanel tableContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] colonnes = {"SERVICE", "PRIX (AR)", "ACTIONS"};
        modele = new DefaultTableModel(new Object[][]{}, colonnes) {
            @Override public boolean isCellEditable(int row, int col) { return col == 2; }
        };
        tableau = new JTable(modele);
        tableau.setRowHeight(50);
        tableau.setBackground(Color.WHITE);
        tableau.getTableHeader().setBackground(new Color(40, 80, 200));
        tableau.getTableHeader().setForeground(Color.WHITE);
        tableau.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        tableau.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        tableau.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scroll = new JScrollPane(tableau);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scroll, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.SOUTH);

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
            modele.addRow(new Object[]{s.nom, s.prix, "Actions"});
        }
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnModifier, btnSupprimer;
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setBackground(Color.WHITE);
            btnModifier = new JButton("Modifier");
            btnModifier.setBackground(new Color(40, 80, 200));
            btnModifier.setForeground(Color.WHITE);
            btnModifier.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnModifier.setBorderPainted(false);
            btnModifier.setPreferredSize(new Dimension(75, 25));
            btnSupprimer = new JButton("Supprimer");
            btnSupprimer.setBackground(new Color(200, 50, 50));
            btnSupprimer.setForeground(Color.WHITE);
            btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnSupprimer.setBorderPainted(false);
            btnSupprimer.setPreferredSize(new Dimension(80, 25));
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
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setBackground(Color.WHITE);
            btnModifier = new JButton("Modifier");
            btnModifier.setBackground(new Color(40, 80, 200));
            btnModifier.setForeground(Color.WHITE);
            btnModifier.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnModifier.setBorderPainted(false);
            btnModifier.setPreferredSize(new Dimension(75, 25));
            btnSupprimer = new JButton("Supprimer");
            btnSupprimer.setBackground(new Color(200, 50, 50));
            btnSupprimer.setForeground(Color.WHITE);
            btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnSupprimer.setBorderPainted(false);
            btnSupprimer.setPreferredSize(new Dimension(80, 25));
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