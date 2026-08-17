package stationessenceswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.EmptyBorder;

public class StationEssenceSwing {

    private static JPanel contentPanel;
    private static JFrame fenetre;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            fenetre = new JFrame();
            fenetre.setSize(1150, 750);
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setLayout(new BorderLayout());

            // --- ACTIVATION DE LA TRANSPARENCE POUR LES COINS RONDS ---
            fenetre.setUndecorated(true);
            fenetre.setBackground(new Color(0, 0, 0, 0));

            // --- BARRE DE TITRE PERSONNALISÉE (AVEC LES 3 BOUTONS RONDS) ---
            JPanel barreTitre = new JPanel(new BorderLayout());
            barreTitre.setOpaque(false);
            barreTitre.setPreferredSize(new Dimension(1150, 50));
            barreTitre.setBorder(new EmptyBorder(0, 20, 0, 20));

            JLabel titreApp = new JLabel("Station Essence");
            titreApp.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titreApp.setForeground(Color.WHITE);

            // --- PANNEAU DES 3 BOUTONS DE CONTRÔLE (Ronds et Modernes) ---
            JPanel boutonsControle = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
            boutonsControle.setOpaque(false);

            // 1. Réduire (Jaune)
            JButton btnMin = new JButton("—");
            btnMin.setPreferredSize(new Dimension(28, 28));
            btnMin.setBackground(new Color(255, 204, 0));
            btnMin.setBorderPainted(false);
            btnMin.setFocusPainted(false);
            btnMin.setForeground(Color.BLACK);
            btnMin.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnMin.addActionListener(e -> fenetre.setState(JFrame.ICONIFIED));

            // 2. Agrandir (Vert)
            JButton btnMax = new JButton("☐");
            btnMax.setPreferredSize(new Dimension(28, 28));
            btnMax.setBackground(new Color(0, 204, 0));
            btnMax.setBorderPainted(false);
            btnMax.setFocusPainted(false);
            btnMax.setForeground(Color.BLACK);
            btnMax.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnMax.addActionListener(e -> {
                if (fenetre.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    fenetre.setExtendedState(JFrame.NORMAL);
                } else {
                    fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });

            // 3. Fermer (Rouge)
            JButton btnClose = new JButton("✕");
            btnClose.setPreferredSize(new Dimension(28, 28));
            btnClose.setBackground(new Color(255, 60, 60));
            btnClose.setBorderPainted(false);
            btnClose.setFocusPainted(false);
            btnClose.setForeground(Color.WHITE);
            btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnClose.addActionListener(e -> System.exit(0));

            boutonsControle.add(btnMin);
            boutonsControle.add(btnMax);
            boutonsControle.add(btnClose);

            barreTitre.add(titreApp, BorderLayout.WEST);
            barreTitre.add(boutonsControle, BorderLayout.EAST);

            // --- MENU LATÉRAL BLEU AVEC COINS ARRONDIS ---
            JPanel menuPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(40, 80, 200)); // Bleu vif
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
                }
            };
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            menuPanel.setOpaque(false);
            menuPanel.setPreferredSize(new Dimension(230, 750));
            menuPanel.setBorder(new EmptyBorder(40, 20, 20, 20));

            JLabel titreMenu = new JLabel("<html><center><font color='white' size='5'>⛽<br>STATION<br>ESSENCE</font></center></html>");
            titreMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuPanel.add(titreMenu);
            menuPanel.add(Box.createVerticalStrut(30));

            String[] nomsPages = {
                "Tableau de bord", "Produits", "Entrées de stock",
                "Vente carburant", "Services", "Entretiens",
                "Statistiques", "Recettes"
            };

            // --- CONTENU DROITE AVEC COINS RONDS ---
            contentPanel = new JPanel(new CardLayout());
            contentPanel.setBackground(new Color(245, 247, 250)); // Fond clair
            contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

            // AJOUT DES PAGES
            contentPanel.add(new PageTableauBord(), "Tableau de bord");
            contentPanel.add(new PageProduits(), "Produits");
            contentPanel.add(new PageStock(), "Entrées de stock");
            contentPanel.add(new PageVente(), "Vente carburant");
            contentPanel.add(new PageServices(), "Services");
            contentPanel.add(new PageEntretiens(), "Entretiens");
            contentPanel.add(new PageStatistiques(), "Statistiques");
            contentPanel.add(new PageRecettes(), "Recettes");

            // --- CRÉATION DES BOUTONS DU MENU (DYNAMIQUES ET MODERNES) ---
            for (String nom : nomsPages) {
                JButton btn = creerBoutonMenuStylise(nom);
                btn.addActionListener(e -> {
                    CardLayout cl = (CardLayout)(contentPanel.getLayout());
                    cl.show(contentPanel, nom);
                });
                menuPanel.add(btn);
                menuPanel.add(Box.createVerticalStrut(8));
            }

            // --- PANNEAU PRINCIPAL AVEC FOND TRANSPARENT ET COINS RONDS ---
            JPanel mainPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(40, 80, 200)); // Couleur du fond global
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 35, 35));
                }
            };
            mainPanel.setOpaque(false);
            mainPanel.add(barreTitre, BorderLayout.NORTH);
            mainPanel.add(menuPanel, BorderLayout.WEST);
            mainPanel.add(contentPanel, BorderLayout.CENTER);

            fenetre.add(mainPanel);
            fenetre.setVisible(true);
        });
    }

    // --- MÉTHODE POUR CRÉER UN BOUTON DYNAMIQUE ET STYLÉ ---
    private static JButton creerBoutonMenuStylise(String texte) {
        JButton btn = new JButton(texte);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 80, 200));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 15, 5, 15));

        // EFFET DE SURVOL DYNAMIQUE
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(60, 110, 255)); // Bleu plus clair au survol
                btn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(40, 80, 200)); // Retour au bleu normal
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }
}