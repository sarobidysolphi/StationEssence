package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;

public class PageVente extends JPanel {
    private JComboBox<String> comboProduit;
    private JTextField champClient, champLitres;
    private JLabel labelTotal;
    private DefaultTableModel modeleHistorique;
    private JTable tableau;

    public PageVente() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Vente carburant (CRUD)");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titre.setForeground(new Color(30, 30, 30));
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

        gbc.gridx = 0; gbc.gridy = 0; formulaire.add(new JLabel("Carburant :"), gbc);
        gbc.gridx = 1; comboProduit = new JComboBox<>(); remplirCombo(); formulaire.add(comboProduit, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formulaire.add(new JLabel("Client :"), gbc);
        gbc.gridx = 1; champClient = new JTextField(15); formulaire.add(champClient, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formulaire.add(new JLabel("Litres :"), gbc);
        gbc.gridx = 1; champLitres = new JTextField(10); formulaire.add(champLitres, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formulaire.add(new JLabel("Total :"), gbc);
        gbc.gridx = 1; labelTotal = new JLabel("0 Ar"); labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 16)); labelTotal.setForeground(new Color(40, 80, 200)); formulaire.add(labelTotal, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        JButton btnValider = new JButton("Valider la vente");
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
        histPanel.setBorder(BorderFactory.createTitledBorder(" Historique des ventes "));
        String[] cols = {"PRODUIT", "CLIENT", "LITRES", "MONTANT", "DATE", "ACTIONS"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        tableau = new JTable(modeleHistorique);
        tableau.setRowHeight(40);
        tableau.getTableHeader().setBackground(new Color(40, 80, 200));
        tableau.getTableHeader().setForeground(Color.WHITE);
        histPanel.add(new JScrollPane(tableau), BorderLayout.CENTER);
        contenu.add(histPanel);

        add(contenu, BorderLayout.CENTER);

        champLitres.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
        });
        btnValider.addActionListener(e -> validerVente());

        // Écouteur de suppression
        tableau.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tableau.rowAtPoint(evt.getPoint());
                int col = tableau.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 5) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Supprimer cette vente ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DonneesMemoire.historiqueVentes.remove(row);
                        modeleHistorique.removeRow(row);
                    }
                }
            }
        });
    }

    private void remplirCombo() {
        comboProduit.removeAllItems();
        for (var p : DonneesMemoire.chargerProduits()) {
            comboProduit.addItem(p.designation + " (" + p.stock + " L)");
        }
    }

    private void calculerTotal() {
        try {
            int index = comboProduit.getSelectedIndex();
            if (index >= 0 && !champLitres.getText().isEmpty()) {
                int litres = Integer.parseInt(champLitres.getText());
                int prix = DonneesMemoire.chargerProduits().get(index).prixParLitre * litres;
                labelTotal.setText(prix + " Ar");
            }
        } catch (Exception e) { labelTotal.setText("0 Ar"); }
    }

    private void validerVente() {
        try {
            int index = comboProduit.getSelectedIndex();
            int litres = Integer.parseInt(champLitres.getText());
            var p = DonneesMemoire.chargerProduits().get(index);
            if (p.stock >= litres) {
                p.stock -= litres;
                int total = litres * p.prixParLitre;
                DonneesMemoire.recetteDuJour += total;
                DonneesMemoire.Vente vente = new DonneesMemoire.Vente(champClient.getText(), p.designation, litres, total);
                DonneesMemoire.historiqueVentes.add(vente);
                modeleHistorique.addRow(new Object[]{p.designation, champClient.getText(), litres, total + " Ar", LocalDate.now().toString(), "Supprimer"});
                JOptionPane.showMessageDialog(this, "Vente validée : " + total + " Ar");
                champClient.setText(""); champLitres.setText(""); labelTotal.setText("0 Ar");
                remplirCombo();
            } else {
                JOptionPane.showMessageDialog(this, "Stock insuffisant !");
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur !"); }
    }
}