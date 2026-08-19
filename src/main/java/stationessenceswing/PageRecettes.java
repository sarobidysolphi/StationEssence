package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PageRecettes extends JPanel {
    private JLabel labelTotal;
    private DefaultTableModel modelDetail, modelTop;
    private PlaceholderTextField champRecherche;

    public PageRecettes() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Recettes");
        titre.setFont(Theme.POLICE_TITRE);
        JLabel sousTitre = new JLabel("Detail des recettes et top 5 clients");
        sousTitre.setFont(Theme.POLICE_SOUS_TITRE);
        sousTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        toolbar.setBackground(Theme.FOND_CLAIR);

        champRecherche = new PlaceholderTextField("Rechercher un client...");
        champRecherche.setPreferredSize(new Dimension(250, 34));
        champRecherche.setFont(Theme.POLICE_NORMALE);

        JButton btnRechercher = MacButton.primary("Rechercher");
        btnRechercher.addActionListener(e -> rafraichir());

        JButton btnReset = MacButton.ghost("Afficher tout");
        btnReset.addActionListener(e -> {
            champRecherche.setText("");
            rafraichir();
        });

        toolbar.add(champRecherche);
        toolbar.add(btnRechercher);
        toolbar.add(btnReset);
        toolbar.add(Box.createHorizontalGlue());
        add(toolbar, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setBackground(Theme.FOND_CLAIR);

        JPanel detailCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        detailCard.setOpaque(false);

        JLabel detailTitre = new JLabel("   Detail des recettes (achats)");
        detailTitre.setFont(Theme.POLICE_GRAS);
        detailTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        detailCard.add(detailTitre, BorderLayout.NORTH);

        String[] cols1 = {"NUM", "PRODUIT", "CLIENT", "LITRES", "DATE"};
        modelDetail = new DefaultTableModel(new Object[][]{}, cols1) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table1 = new StyledTable(modelDetail);
        detailCard.add(new JScrollPane(table1), BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Theme.FOND_CARTE);
        totalPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        labelTotal = new JLabel("0 Ar", SwingConstants.CENTER);
        labelTotal.setFont(Theme.POLICE_GRANDE);
        labelTotal.setForeground(Theme.BLEU_ACCENT);
        totalPanel.add(labelTotal, BorderLayout.CENTER);
        detailCard.add(totalPanel, BorderLayout.SOUTH);

        content.add(detailCard);

        JPanel topCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        topCard.setOpaque(false);

        JLabel topTitre = new JLabel("   Top 5 meilleurs clients");
        topTitre.setFont(Theme.POLICE_GRAS);
        topTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        topCard.add(topTitre, BorderLayout.NORTH);

        String[] cols2 = {"RANG", "NOM", "DEPENSES (Ar)"};
        modelTop = new DefaultTableModel(new Object[][]{}, cols2) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table2 = new StyledTable(modelTop);
        topCard.add(new JScrollPane(table2), BorderLayout.CENTER);

        content.add(topCard);
        add(content, BorderLayout.CENTER);

        rafraichir();
    }

    public void rafraichir() {
        modelDetail.setRowCount(0);
        int totalGeneral = 0;

        String recherche = champRecherche != null ? champRecherche.getText().trim() : "";
        List<Achat> achats;
        if (!recherche.isEmpty()) {
            achats = AchatDAO.rechercherParClient(recherche);
        } else {
            achats = AchatDAO.getAll();
        }

        for (Achat a : achats) {
            modelDetail.addRow(new Object[]{a.getNumAchat(), a.getNumProd(), a.getNomClient(), a.getNbrLitre(), a.getDateAchat()});
            totalGeneral += a.getNbrLitre() * 5200;
        }

        if (labelTotal != null) {
            labelTotal.setText(String.format("%,d", totalGeneral) + " Ar");
        }

        modelTop.setRowCount(0);
        List<String[]> topClients = AchatDAO.getTop5Clients();
        int rang = 1;
        for (String[] client : topClients) {
            modelTop.addRow(new Object[]{rang++, client[0], String.format("%,d", Integer.parseInt(client[1])) + " Ar"});
        }
    }
}
