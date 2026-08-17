package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PageProduits extends JPanel {
    private DefaultTableModel modele;
    private JTable tableau;
    private JTextField champRecherche;

    public PageProduits() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titre = new JLabel("Gestion des produits");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titre.setForeground(new Color(30, 30, 30));
        add(titre, BorderLayout.NORTH);

        // Barre d'outils
        JPanel outilPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        outilPanel.setBackground(new Color(245, 247, 250));
        champRecherche = new JTextField("Rechercher un produit...", 20);
        champRecherche.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnNouveau = new JButton("+ Nouveau");
        btnNouveau.setBackground(new Color(40, 80, 200));
        btnNouveau.setForeground(Color.WHITE);
        btnNouveau.setFocusPainted(false);
        btnNouveau.addActionListener(e -> ajouterProduit());
        outilPanel.add(champRecherche);
        outilPanel.add(btnNouveau);
        add(outilPanel, BorderLayout.CENTER);

        // Tableau arrondi
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

        String[] colonnes = {"Numéro", "Désignation", "Stock (L)"};
        modele = new DefaultTableModel(new Object[][]{}, colonnes) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tableau = new JTable(modele);
        tableau.setRowHeight(35);
        tableau.setBackground(Color.WHITE);
        tableau.getTableHeader().setBackground(new Color(40, 80, 200));
        tableau.getTableHeader().setForeground(Color.WHITE);
        tableau.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(tableau);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scroll, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.SOUTH);

        rafraichirTableau();
    }

    private void ajouterProduit() {
        String nom = JOptionPane.showInputDialog(this, "Nom du nouveau produit :");
        if (nom == null || nom.trim().isEmpty()) return;
        int nouvelId = DonneesMemoire.listeProduits.size() + 1;
        DonneesMemoire.listeProduits.add(new DonneesMemoire.Produit(nouvelId, nom, 0, 10, 1000));
        rafraichirTableau();
    }

    private void rafraichirTableau() {
        modele.setRowCount(0);
        for (DonneesMemoire.Produit p : DonneesMemoire.chargerProduits()) {
            modele.addRow(new Object[]{p.numProd, p.designation, p.stock});
        }
    }
}