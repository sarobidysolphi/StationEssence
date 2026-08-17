package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class StationEssenceSwing {

    private static JPanel contentPanel;
    private static JFrame fenetre;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            fenetre = new JFrame();
            fenetre.setSize(1200, 750);
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setLayout(new BorderLayout());
            fenetre.setUndecorated(true); // On cache la barre Windows

            // --- NOTRE PROPRE BARRE DE TITRE (Avec les 3 boutons ronds) ---
            JPanel barreTitre = new JPanel(new BorderLayout());
            barreTitre.setBackground(Theme.MENU_BLEU);
            barreTitre.setPreferredSize(new Dimension(1200, 45));
            barreTitre.setBorder(new EmptyBorder(0, 15, 0, 15));

            JLabel titreApp = new JLabel("Station Essence");
            titreApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titreApp.setForeground(Color.WHITE);

            JPanel boutonsControle = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
            boutonsControle.setOpaque(false);

            // Bouton Réduire (Rond jaune)
            JButton btnMinimize = new JButton("—");
            btnMinimize.setPreferredSize(new Dimension(28, 28));
            btnMinimize.setBackground(new Color(255, 204, 0));
            btnMinimize.setBorderPainted(false);
            btnMinimize.setFocusPainted(false);
            btnMinimize.setForeground(Color.BLACK);
            btnMinimize.addActionListener(e -> fenetre.setState(JFrame.ICONIFIED));

            // Bouton Agrandir (Rond vert)
            JButton btnMaximize = new JButton("☐");
            btnMaximize.setPreferredSize(new Dimension(28, 28));
            btnMaximize.setBackground(new Color(0, 204, 0));
            btnMaximize.setBorderPainted(false);
            btnMaximize.setFocusPainted(false);
            btnMaximize.setForeground(Color.BLACK);
            btnMaximize.addActionListener(e -> {
                if (fenetre.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    fenetre.setExtendedState(JFrame.NORMAL);
                } else {
                    fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });

            // Bouton Fermer (Rond rouge)
            JButton btnClose = new JButton("✕");
            btnClose.setPreferredSize(new Dimension(28, 28));
            btnClose.setBackground(new Color(255, 60, 60));
            btnClose.setBorderPainted(false);
            btnClose.setFocusPainted(false);
            btnClose.setForeground(Color.WHITE);
            btnClose.addActionListener(e -> System.exit(0));

            // Ajout des boutons au panneau
            boutonsControle.add(btnMinimize);
            boutonsControle.add(btnMaximize);
            boutonsControle.add(btnClose);

            barreTitre.add(titreApp, BorderLayout.WEST);
            barreTitre.add(boutonsControle, BorderLayout.EAST);

            // --- MENU GAUCHE ARRONDI ---
            JPanel menuPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.MENU_BLEU);
                    // Dessine un rectangle avec le coin supérieur droit et inférieur droit arrondis
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
                }
            };
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            menuPanel.setOpaque(false);
            menuPanel.setPreferredSize(new Dimension(240, 750));
            menuPanel.setBorder(new EmptyBorder(60, 20, 20, 20));

            // Titre du menu
            JLabel titreMenu = new JLabel("<html><center><font color='white' size='5'>⛽ STATION<br>ESSENCE</font></center></html>");
            titreMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuPanel.add(titreMenu);
            menuPanel.add(Box.createVerticalStrut(40));

            String[] nomsPages = {
                "Tableau de bord", "Produits", "Entrées de stock", 
                "Vente carburant", "Services", "Entretiens", 
                "Statistiques", "Recettes"
            };

            // --- CONTENU DROITE ---
            contentPanel = new JPanel(new CardLayout());
            contentPanel.setBackground(Theme.FOND_CLAIR);
            contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            // AJOUT DES PAGES
            contentPanel.add(new PageTableauBord(), "Tableau de bord");
            contentPanel.add(new PageProduits(), "Produits");
            contentPanel.add(new PageStock(), "Entrées de stock");
            contentPanel.add(new PageVente(), "Vente carburant");
            contentPanel.add(new PageServices(), "Services");
            contentPanel.add(new PageEntretiens(), "Entretiens");
            contentPanel.add(new PageStatistiques(), "Statistiques");
            contentPanel.add(new PageRecettes(), "Recettes");

            // --- CRÉATION DES BOUTONS DU MENU ---
            for (String nom : nomsPages) {
                JButton btn = creerBoutonMenu(nom);
                btn.addActionListener(e -> {
                    CardLayout cl = (CardLayout)(contentPanel.getLayout());
                    cl.show(contentPanel, nom);
                });
                menuPanel.add(btn);
                menuPanel.add(Box.createVerticalStrut(10));
            }

            fenetre.add(barreTitre, BorderLayout.NORTH);
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
        btn.setBackground(Theme.MENU_BLEU);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(Theme.BLEU_ACCENT.brighter());
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(Theme.MENU_BLEU);
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }
}