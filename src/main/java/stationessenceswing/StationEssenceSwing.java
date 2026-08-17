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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            fenetre = new JFrame();
            fenetre.setSize(1150, 750);
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setLayout(new BorderLayout());

            fenetre.setUndecorated(true);
            fenetre.setBackground(new Color(0, 0, 0, 0));

            JPanel barreTitre = new JPanel(new BorderLayout());
            barreTitre.setOpaque(false);
            barreTitre.setPreferredSize(new Dimension(1150, 55));
            barreTitre.setBorder(new EmptyBorder(0, 25, 0, 20));

            barreTitre.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    mouseX = e.getX();
                    mouseY = e.getY();
                }
            });
            barreTitre.addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    int x = e.getXOnScreen() - mouseX;
                    int y = e.getYOnScreen() - mouseY;
                    fenetre.setLocation(x, y);
                }
            });

            JLabel titreApp = new JLabel("Application Desktop");
            titreApp.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titreApp.setForeground(Color.WHITE);

            JPanel boutonsControle = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
            boutonsControle.setOpaque(false);

            JButton btnMin = new JButton() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 204, 0));
                    g2.fill(new Ellipse2D.Double(0, 0, 28, 28));
                    g2.setColor(Color.BLACK);
                    g2.drawString("—", 8, 18);
                }
            };
            btnMin.setPreferredSize(new Dimension(28, 28));
            btnMin.addActionListener(e -> fenetre.setState(JFrame.ICONIFIED));

            JButton btnMax = new JButton() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0, 204, 0));
                    g2.fill(new Ellipse2D.Double(0, 0, 28, 28));
                    g2.setColor(Color.BLACK);
                    g2.drawString("☐", 7, 18);
                }
            };
            btnMax.setPreferredSize(new Dimension(28, 28));
            btnMax.addActionListener(e -> {
                if (fenetre.getExtendedState() == JFrame.MAXIMIZED_BOTH) fenetre.setExtendedState(JFrame.NORMAL);
                else fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
            });

            JButton btnClose = new JButton() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 60, 60));
                    g2.fill(new Ellipse2D.Double(0, 0, 28, 28));
                    g2.setColor(Color.WHITE);
                    g2.drawString("✕", 8, 18);
                }
            };
            btnClose.setPreferredSize(new Dimension(28, 28));
            btnClose.addActionListener(e -> System.exit(0));

            boutonsControle.add(btnMin);
            boutonsControle.add(btnMax);
            boutonsControle.add(btnClose);

            barreTitre.add(titreApp, BorderLayout.WEST);
            barreTitre.add(boutonsControle, BorderLayout.EAST);

            JPanel menuPanel = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(40, 80, 200));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
                }
            };
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            menuPanel.setOpaque(false);
            menuPanel.setPreferredSize(new Dimension(240, 750));
            menuPanel.setBorder(new EmptyBorder(60, 20, 20, 20));

            JLabel titreMenu = new JLabel("<html><center><font color='white' size='4'>⛽<br><b>Application<br>Desktop</b></font></center></html>");
            titreMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
            menuPanel.add(titreMenu);
            menuPanel.add(Box.createVerticalStrut(40));

            String[] nomsPages = {
                "Tableau de bord", "Produits", "Entrées de stock",
                "Vente carburant", "Services", "Entretiens",
                "Statistiques", "Recettes"
            };

            contentPanel = new JPanel(new CardLayout());
            contentPanel.setBackground(new Color(245, 247, 250));
            contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

            contentPanel.add(new PageTableauBord(), "Tableau de bord");
            contentPanel.add(new PageProduits(), "Produits");
            contentPanel.add(new PageStock(), "Entrées de stock");
            contentPanel.add(new PageVente(), "Vente carburant");
            contentPanel.add(new PageServices(), "Services");
            contentPanel.add(new PageEntretiens(), "Entretiens");
            contentPanel.add(new PageStatistiques(), "Statistiques");
            contentPanel.add(new PageRecettes(), "Recettes");

            for (String nom : nomsPages) {
                JButton btn = creerBoutonMenuStylise(nom);
                btn.addActionListener(e -> {
                    CardLayout cl = (CardLayout)(contentPanel.getLayout());
                    cl.show(contentPanel, nom);
                });
                menuPanel.add(btn);
                menuPanel.add(Box.createVerticalStrut(10));
            }

            JPanel mainPanel = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(40, 80, 200));
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

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(60, 110, 255));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(40, 80, 200));
            }
        });
        return btn;
    }
}