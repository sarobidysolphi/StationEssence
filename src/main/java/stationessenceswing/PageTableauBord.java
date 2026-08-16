package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PageTableauBord extends JPanel {
    public PageTableauBord() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CARTE);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titre = new JLabel("Tableau de bord");
        titre.setFont(Theme.POLICE_TITRE);
        titre.setForeground(Theme.TEXTE_PRINCIPAL);
        add(titre, BorderLayout.NORTH);

        JPanel cartes = new JPanel(new GridLayout(1, 4, 20, 0));
        cartes.setBackground(Theme.FOND_CARTE);
        cartes.setBorder(new EmptyBorder(20, 0, 20, 0));
        cartes.add(creerCarte("0 Ar", "Recette du jour", Theme.ACCENT_VERT, Color.GREEN));
        cartes.add(creerCarte("865 L", "Stock total", Theme.ACCENT_VERT, Color.GREEN));
        cartes.add(creerCarte("0", "Produits en alerte", Theme.TEXTE_PRINCIPAL, Color.GREEN));
        cartes.add(creerCarte("3", "Produits référencés", Theme.TEXTE_PRINCIPAL, new Color(255, 152, 0)));
        add(cartes, BorderLayout.CENTER);

        JPanel stocks = new JPanel();
        stocks.setLayout(new BoxLayout(stocks, BoxLayout.Y_AXIS));
        stocks.setBackground(Theme.FOND_PRINCIPAL);
        stocks.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.BORDURE_VERRE), " Stock actuel "));
        stocks.add(creerLigneStock("Essence", "530 L"));
        stocks.add(creerLigneStock("Gasoil", "245 L"));
        stocks.add(creerLigneStock("Pétrole", "90 L"));
        add(stocks, BorderLayout.SOUTH);
    }
    
    private JPanel creerCarte(String v, String d, Color c, Color b) {
        JPanel carte = new JPanel(); 
        carte.setLayout(new BoxLayout(carte, BoxLayout.Y_AXIS));
        carte.setBackground(Theme.FOND_PRINCIPAL);
        carte.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10,10,10,10), BorderFactory.createLineBorder(b, 1)));
        JLabel val = new JLabel(v); val.setFont(new Font("Segoe UI", Font.BOLD, 22)); val.setForeground(c);
        JLabel desc = new JLabel(d); desc.setFont(new Font("Segoe UI", Font.PLAIN, 14)); desc.setForeground(Theme.TEXTE_SECONDAIRE);
        carte.add(val); carte.add(Box.createVerticalStrut(5)); carte.add(desc); return carte;
    }
    
    private JPanel creerLigneStock(String n, String q) {
        JPanel ligne = new JPanel(new BorderLayout()); 
        ligne.setBackground(Theme.FOND_PRINCIPAL);
        ligne.setBorder(new EmptyBorder(8, 15, 8, 15));
        JLabel nom = new JLabel("🛢️ " + n); 
        nom.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        nom.setForeground(Theme.TEXTE_PRINCIPAL);
        JLabel qte = new JLabel(q); 
        qte.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        qte.setForeground(Theme.ACCENT_VERT);
        ligne.add(nom, BorderLayout.WEST); 
        ligne.add(qte, BorderLayout.EAST); 
        return ligne;
    }
}