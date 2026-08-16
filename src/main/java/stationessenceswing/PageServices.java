package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PageServices extends JPanel {
    private DefaultTableModel modele;
    private JTable tableau;

    public PageServices() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titre = new JLabel("Gestion des services");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(titre, BorderLayout.NORTH);

        // Bouton Nouveau service
        JButton btnNouveau = new JButton("+ Nouveau service");
        btnNouveau.setBackground(new Color(30, 45, 40));
        btnNouveau.setForeground(Color.WHITE);
        btnNouveau.setFocusPainted(false);
        btnNouveau.addActionListener(e -> ajouterService());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(new Color(245, 245, 245));
        actionPanel.add(btnNouveau);
        add(actionPanel, BorderLayout.CENTER);

        // Tableau des services
        String[] colonnes = {"SERVICE", "PRIX (AR)", "MODIFIER", "SUPPRIMER"};
        modele = new DefaultTableModel(new Object[][]{}, colonnes) {
            @Override public boolean isCellEditable(int row, int col) {
                return col >= 2; // Seulement les boutons sont cliquables
            }
        };
        tableau = new JTable(modele);
        tableau.setRowHeight(45);

        // --- CORRECTION ICI : On ajoute le listener APRES avoir créé le tableau ---
        tableau.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tableau.rowAtPoint(evt.getPoint());
                int col = tableau.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 2) {
                    DonneesMemoire.Service s = DonneesMemoire.listeServices.get(row);
                    if (col == 2) { // Modifier
                        String newNom = JOptionPane.showInputDialog("Nouveau nom :", s.nom);
                        if (newNom != null && !newNom.trim().isEmpty()) {
                            String newPrixStr = JOptionPane.showInputDialog("Nouveau prix :", s.prix);
                            try {
                                s.nom = newNom;
                                s.prix = Integer.parseInt(newPrixStr);
                                rafraichirTableau();
                            } catch (Exception e) { /* ignore */ }
                        }
                    } else if (col == 3) { // Supprimer
                        int confirm = JOptionPane.showConfirmDialog(null, "Supprimer " + s.nom + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            DonneesMemoire.listeServices.remove(row);
                            rafraichirTableau();
                        }
                    }
                }
            }
        });
        // --- FIN DE LA CORRECTION ---

        // Remplissage initial
        rafraichirTableau();

        JScrollPane scroll = new JScrollPane(tableau);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.SOUTH);
    }

    private void ajouterService() {
        String nom = JOptionPane.showInputDialog(this, "Nom du nouveau service :");
        if (nom == null || nom.trim().isEmpty()) return;
        try {
            String prixStr = JOptionPane.showInputDialog(this, "Prix (Ar) :");
            int prix = Integer.parseInt(prixStr);
            int newId = DonneesMemoire.listeServices.size() + 1;
            DonneesMemoire.listeServices.add(new DonneesMemoire.Service(newId, nom, prix));
            rafraichirTableau();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Prix invalide !");
        }
    }

    private void rafraichirTableau() {
        modele.setRowCount(0);
        for (DonneesMemoire.Service s : DonneesMemoire.listeServices) {
            modele.addRow(new Object[]{s.nom, s.prix, "Modifier", "Suppr."});
        }
    }
}