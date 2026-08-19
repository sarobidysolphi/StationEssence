package stationessenceswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.EmptyBorder;

public class StationEssenceSwing {

    private static JPanel contentPanel;
    private static JFrame fenetre;
    private static int mouseX, mouseY;
    private static JButton dernierBoutonActif = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("Panel.background", Theme.FOND_CLAIR);
            UIManager.put("Label.foreground", Theme.TEXTE_FONCE);
            UIManager.put("TextField.background", Theme.FOND_CARTE);
            UIManager.put("TextField.foreground", Theme.TEXTE_FONCE);
            UIManager.put("TextField.caretForeground", Theme.BLEU_ACCENT);
            UIManager.put("TextField.border", new javax.swing.border.LineBorder(Theme.BORDURE, 1));
            UIManager.put("ComboBox.background", Theme.FOND_CARTE);
            UIManager.put("ComboBox.foreground", Theme.TEXTE_FONCE);
            UIManager.put("ComboBox.selectionBackground", Theme.BLEU_ACCENT);
            UIManager.put("ComboBox.selectionForeground", Theme.TEXTE_FONCE);
            UIManager.put("List.background", Theme.FOND_CARTE);
            UIManager.put("List.foreground", Theme.TEXTE_FONCE);
            UIManager.put("List.selectionBackground", Theme.BLEU_ACCENT);
            UIManager.put("List.selectionForeground", Theme.TEXTE_FONCE);
            UIManager.put("OptionPane.background", Theme.FOND_CLAIR);
            UIManager.put("OptionPane.messageForeground", Theme.TEXTE_FONCE);
            UIManager.put("OptionPane.messageFont", Theme.POLICE_NORMALE);
            UIManager.put("CheckBox.foreground", Theme.TEXTE_FONCE);
            UIManager.put("ScrollPane.background", Theme.FOND_CLAIR);
            UIManager.put("Viewport.background", Theme.FOND_CARTE);

            fenetre = new JFrame();
            fenetre.setSize(1150, 750);
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setLayout(new BorderLayout());
            fenetre.setUndecorated(true);
            fenetre.setBackground(new Color(0, 0, 0, 0));

            JPanel barreTitre = new JPanel(new BorderLayout());
            barreTitre.setOpaque(false);
            barreTitre.setPreferredSize(new Dimension(1150, 38));
            barreTitre.setBorder(new EmptyBorder(0, 16, 0, 12));

            barreTitre.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
            });
            barreTitre.addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    fenetre.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
                }
            });

            JLabel titreApp = new JLabel("  Station Essence");
            titreApp.setFont(new Font("Segoe UI", Font.BOLD, 13));
            titreApp.setForeground(Theme.BLEU_ACCENT);

            JPanel boutonsControle = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
            boutonsControle.setOpaque(false);

            JButton btnClose = creerBoutonFenetre(new Color(255, 95, 86), "\u2715");
            btnClose.addActionListener(e -> System.exit(0));

            JButton btnMin = creerBoutonFenetre(new Color(255, 189, 46), "\u2014");
            btnMin.addActionListener(e -> fenetre.setState(JFrame.ICONIFIED));

            JButton btnMax = creerBoutonFenetre(new Color(39, 201, 63), "\u25A1");
            btnMax.addActionListener(e -> {
                if (fenetre.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    fenetre.setExtendedState(JFrame.NORMAL);
                } else {
                    fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });

            boutonsControle.add(btnClose);
            boutonsControle.add(btnMin);
            boutonsControle.add(btnMax);

            barreTitre.add(titreApp, BorderLayout.WEST);
            barreTitre.add(boutonsControle, BorderLayout.EAST);

            JPanel menuPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.SIDEBAR_FOND);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                }
            };
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            menuPanel.setOpaque(false);
            menuPanel.setPreferredSize(new Dimension(220, 750));
            menuPanel.setBorder(new EmptyBorder(50, 12, 12, 12));

            JLabel logoLabel = new JLabel("\u26FD  Station Essence");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            logoLabel.setForeground(Theme.BLEU_ACCENT);
            logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            logoLabel.setBorder(new EmptyBorder(0, 8, 4, 0));
            menuPanel.add(logoLabel);

            JLabel sousLogo = new JLabel("  Systeme de gestion");
            sousLogo.setFont(Theme.POLICE_PETITE);
            sousLogo.setForeground(Theme.TEXTE_TERTIAIRE);
            sousLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
            sousLogo.setBorder(new EmptyBorder(0, 8, 16, 0));
            menuPanel.add(sousLogo);

            menuPanel.add(creerSeparateurSidebar());

            JLabel clockLabel = new JLabel(" ", SwingConstants.CENTER);
            clockLabel.setForeground(Theme.TEXTE_TERTIAIRE);
            clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            clockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            Timer clockTimer = new Timer(1000, e -> {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                clockLabel.setText(String.format("%02d:%02d:%02d", now.getHour(), now.getMinute(), now.getSecond()));
            });
            clockTimer.setInitialDelay(0);
            clockTimer.start();

            String[] emojis = {"\u2302", "\u26FD", "\u2B07\uFE0F", "\uD83D\uDCB3", "\u2699\uFE0F", "\uD83D\uDE97", "\uD83D\uDCCA", "\uD83D\uDCB0"};
            String[] nomsPages = {
                "Tableau de bord", "Produits", "Entrees de stock",
                "Vente carburant", "Services", "Entretiens",
                "Statistiques", "Recettes"
            };

            contentPanel = new JPanel(new CardLayout());
            contentPanel.setBackground(Theme.FOND_CLAIR);
            contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            contentPanel.add(new PageTableauBord(), "Tableau de bord");
            contentPanel.add(new PageProduits(), "Produits");
            contentPanel.add(new PageStock(), "Entrees de stock");
            contentPanel.add(new PageAchat(), "Vente carburant");
            contentPanel.add(new PageServices(), "Services");
            contentPanel.add(new PageEntretiens(), "Entretiens");
            contentPanel.add(new PageStatistiques(), "Statistiques");
            contentPanel.add(new PageRecettes(), "Recettes");

            for (int i = 0; i < nomsPages.length; i++) {
                String nom = nomsPages[i];
                String emoji = emojis[i];
                JButton btn = creerBoutonSidebar(emoji + "   " + nom);
                btn.addActionListener(e -> {
                    if (dernierBoutonActif != null) {
                        dernierBoutonActif.setBackground(new Color(0, 0, 0, 0));
                        dernierBoutonActif.setForeground(Theme.TEXTE_TERTIAIRE);
                    }
                    btn.setBackground(Theme.SIDEBAR_ACTIF);
                    btn.setForeground(Theme.TEXTE_FONCE);
                    dernierBoutonActif = btn;

                    CardLayout cl = (CardLayout)(contentPanel.getLayout());
                    cl.show(contentPanel, nom);

                    for (java.awt.Component c : contentPanel.getComponents()) {
                        if (c instanceof PageTableauBord && nom.equals("Tableau de bord"))
                            ((PageTableauBord) c).rafraichir();
                        if (c instanceof PageStock && nom.equals("Entrees de stock"))
                            ((PageStock) c).remplirCombo();
                        if (c instanceof PageProduits && nom.equals("Produits"))
                            ((PageProduits) c).rafraichirTableau();
                        if (c instanceof PageServices && nom.equals("Services"))
                            ((PageServices) c).rafraichirTableau();
                        if (c instanceof PageAchat && nom.equals("Vente carburant"))
                            ((PageAchat) c).remplirCombo();
                        if (c instanceof PageEntretiens && nom.equals("Entretiens")) {
                            ((PageEntretiens) c).genererCheckBoxes();
                            ((PageEntretiens) c).rafraichirHistorique();
                        }
                        if (c instanceof PageRecettes && nom.equals("Recettes"))
                            ((PageRecettes) c).rafraichir();
                        if (c instanceof PageStatistiques && nom.equals("Statistiques"))
                            ((PageStatistiques) c).rafraichir();
                    }
                });
                menuPanel.add(btn);
                menuPanel.add(Box.createVerticalStrut(2));
            }

            menuPanel.add(Box.createVerticalGlue());
            menuPanel.add(creerSeparateurSidebar());
            menuPanel.add(Box.createVerticalStrut(4));
            menuPanel.add(clockLabel);
            menuPanel.add(Box.createVerticalStrut(4));

            JPanel mainPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.FOND_CLAIR);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            mainPanel.setOpaque(false);
            mainPanel.add(barreTitre, BorderLayout.NORTH);
            mainPanel.add(menuPanel, BorderLayout.WEST);
            mainPanel.add(contentPanel, BorderLayout.CENTER);

            fenetre.add(mainPanel);
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
        });
    }

    private static JButton creerBoutonFenetre(Color couleur, String symbole) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(couleur);
                g2.fill(new Ellipse2D.Double(0, 0, 14, 14));
                g2.setColor(new Color(0, 0, 0, 100));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                FontMetrics fm = g2.getFontMetrics();
                int x = (14 - fm.stringWidth(symbole)) / 2;
                int y = (14 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(symbole, x, y);
            }
        };
        btn.setPreferredSize(new Dimension(16, 16));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static JButton creerBoutonSidebar(String texte) {
        JButton btn = new JButton(texte);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 38));
        btn.setPreferredSize(new Dimension(200, 38));
        btn.setForeground(Theme.TEXTE_TERTIAIRE);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != dernierBoutonActif) {
                    btn.setBackground(Theme.SIDEBAR_HOVER);
                    btn.setForeground(Theme.TEXTE_FONCE);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != dernierBoutonActif) {
                    btn.setBackground(new Color(0, 0, 0, 0));
                    btn.setForeground(Theme.TEXTE_TERTIAIRE);
                }
            }
        });
        return btn;
    }

    private static Component creerSeparateurSidebar() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(180, 1));
        sep.setForeground(new Color(255, 255, 255, 30));
        return sep;
    }
}
