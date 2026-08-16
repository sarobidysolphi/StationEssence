package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StationEssenceSwing {

    private static JPanel contentPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame fenetre = new JFrame("Station Essence - Système de gestion");
            fenetre.setSize(1200, 750);
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setLayout(new BorderLayout());

            // --- MENU GAUCHE ---
            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            menuPanel.setBackground(new Color(18, 25, 22));
            menuPanel.setPreferredSize(new Dimension(240, 750));
            menuPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

            JLabel titreMenu = new JLabel("<html><center><font color='white' size='5'>⛽ STATION<br>ESSENCE</font></center></html>");
            titreMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuPanel.add(titreMenu);
            menuPanel.add(Box.createVerticalStrut(40));

            String[] nomsPages = {
                "Tableau de bord", "Produits", "Entrées de stock", 
                "Vente carburant", "Services", "Entretiens", 
                "Statistiques", "Recettes"
            };

            // --- CONTENU DROITE (CardLayout) ---
            contentPanel = new JPanel(new CardLayout());
            contentPanel.setBackground(new Color(245, 245, 245));

            // AJOUT DES PAGES
            contentPanel.add(new PageTableauBord(), "Tableau de bord");
            contentPanel.add(new PageProduits(), "Produits");
            contentPanel.add(new PageStock(), "Entrées de stock");
            contentPanel.add(new PageVente(), "Vente carburant");
            contentPanel.add(new PageServices(), "Services");
            contentPanel.add(new PageEntretiens(), "Entretiens");
            contentPanel.add(new PageStatistiques(), "Statistiques");
            contentPanel.add(new PageRecettes(), "Recettes");

            // --- CRÉATION DES BOUTONS ---
            for (String nom : nomsPages) {
                JButton btn = creerBoutonMenu(nom);
                btn.addActionListener(e -> {
                    CardLayout cl = (CardLayout)(contentPanel.getLayout());
                    cl.show(contentPanel, nom);
                });
                menuPanel.add(btn);
                menuPanel.add(Box.createVerticalStrut(10));
            }

            fenetre.add(menuPanel, BorderLayout.WEST);
            fenetre.add(contentPanel, BorderLayout.CENTER);
            fenetre.setVisible(true);
        });
    }

    private static JButton creerBoutonMenu(String texte) {
        JButton btn = new JButton(texte);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(18, 25, 22));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(46, 125, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(18, 25, 22));
            }
        });
        return btn;
    }
}