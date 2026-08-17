package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PageStock extends JPanel {
    public PageStock() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Entrée de stock");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titre.setForeground(new Color(30, 30, 30));
        add(titre, BorderLayout.NORTH);

        JPanel carteFormulaire = new JPanel() {
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
        carteFormulaire.setLayout(new GridBagLayout());
        carteFormulaire.setOpaque(false);
        carteFormulaire.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; 
        carteFormulaire.add(new JLabel("Produit :"), gbc);
        gbc.gridx = 1; 
        JComboBox<String> combo = new JComboBox<>();
        for(var p : DonneesMemoire.chargerProduits()) combo.addItem(p.designation + " (" + p.stock + " L)");
        carteFormulaire.add(combo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; 
        carteFormulaire.add(new JLabel("Quantité (L) :"), gbc);
        gbc.gridx = 1; 
        JTextField qte = new JTextField(10);
        carteFormulaire.add(qte, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        JButton btn = new JButton("Valider l'entrée");
        btn.setBackground(new Color(40, 80, 200));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        carteFormulaire.add(btn, gbc);

        add(carteFormulaire, BorderLayout.CENTER);
    }
}