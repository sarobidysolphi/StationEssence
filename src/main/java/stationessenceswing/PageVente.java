package stationessenceswing;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.FileOutputStream;
import java.time.LocalDate;

public class PageVente extends JPanel {
    private JComboBox<String> comboProduit;
    private JTextField champClient, champLitres;
    private JLabel labelTotal;
    private DefaultTableModel modeleHistorique;
    private JTable tableau;
    private JTextArea zoneReçu; // Pour afficher le reçu après la vente

    public PageVente() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Vente de carburant");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titre, BorderLayout.NORTH);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 20, 0));
        contenu.setBackground(new Color(245, 247, 250));

        // --- FORMULAIRE ---
        JPanel formulaire = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };
        formulaire.setOpaque(false);
        formulaire.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formulaire.add(new JLabel("Carburant :"), gbc);
        gbc.gridx = 1; comboProduit = new JComboBox<>(); remplirCombo(); formulaire.add(comboProduit, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formulaire.add(new JLabel("Client :"), gbc);
        gbc.gridx = 1; champClient = new JTextField(15); formulaire.add(champClient, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formulaire.add(new JLabel("Litres :"), gbc);
        gbc.gridx = 1; champLitres = new JTextField(10); formulaire.add(champLitres, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formulaire.add(new JLabel("Total :"), gbc);
        gbc.gridx = 1; labelTotal = new JLabel("0 Ar"); labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 16)); labelTotal.setForeground(new Color(40, 80, 200)); formulaire.add(labelTotal, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        JButton btnValider = new JButton("Valider la vente");
        btnValider.setBackground(new Color(40, 80, 200));
        btnValider.setForeground(Color.WHITE);
        btnValider.setFocusPainted(false);
        btnValider.setBorderPainted(false);
        btnValider.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnValider.setPreferredSize(new Dimension(150, 35));
        formulaire.add(btnValider, gbc);

        contenu.add(formulaire);

        // --- ZONE D'AFFICHAGE DU REÇU ---
        JPanel recuPanel = new JPanel(new BorderLayout());
        recuPanel.setBackground(Color.WHITE);
        recuPanel.setBorder(BorderFactory.createTitledBorder(" Reçu de vente "));
        zoneReçu = new JTextArea();
        zoneReçu.setEditable(false);
        zoneReçu.setFont(new Font("Monospaced", Font.PLAIN, 14));
        zoneReçu.setText("Aucune vente effectuée.");
        recuPanel.add(new JScrollPane(zoneReçu), BorderLayout.CENTER);

        // BOUTON PDF AJOUTÉ ICI
        JPanel basRecu = new JPanel();
        JButton btnPDF = new JButton("📄 Imprimer le reçu (PDF)");
        btnPDF.setBackground(new Color(40, 80, 200));
        btnPDF.setForeground(Color.WHITE);
        btnPDF.setFocusPainted(false);
        btnPDF.setBorderPainted(false);
        btnPDF.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPDF.setEnabled(false); // Désactivé tant qu'il n'y a pas de vente
        btnPDF.addActionListener(e -> genererPDF());
        basRecu.add(btnPDF);
        recuPanel.add(basRecu, BorderLayout.SOUTH);

        // --- HISTORIQUE DES VENTES ---
        JPanel histPanel = new JPanel(new BorderLayout());
        histPanel.setBackground(Color.WHITE);
        histPanel.setBorder(BorderFactory.createTitledBorder(" Historique des ventes "));
        String[] cols = {"PRODUIT", "CLIENT", "LITRES", "MONTANT", "DATE", "ACTIONS"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        tableau = new JTable(modeleHistorique);
        tableau.setRowHeight(40);
        tableau.getTableHeader().setBackground(new Color(40, 80, 200));
        tableau.getTableHeader().setForeground(Color.WHITE);
        histPanel.add(new JScrollPane(tableau), BorderLayout.CENTER);

        // --- ASSEMBLAGE FINAL ---
        JPanel droite = new JPanel(new BorderLayout());
        droite.add(recuPanel, BorderLayout.CENTER);
        droite.add(histPanel, BorderLayout.SOUTH);
        contenu.add(droite);

        add(contenu, BorderLayout.CENTER);

        champLitres.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
        });
        btnValider.addActionListener(e -> validerVente());

        tableau.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tableau.rowAtPoint(evt.getPoint());
                int col = tableau.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 5) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Supprimer cette vente ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DonneesMemoire.historiqueVentes.remove(row);
                        modeleHistorique.removeRow(row);
                    }
                }
            }
        });
    }

    private void remplirCombo() {
        comboProduit.removeAllItems();
        for (var p : DonneesMemoire.chargerProduits()) {
            comboProduit.addItem(p.designation + " (" + p.stock + " L)");
        }
    }

    private void calculerTotal() {
        try {
            int index = comboProduit.getSelectedIndex();
            if (index >= 0 && !champLitres.getText().isEmpty()) {
                int litres = Integer.parseInt(champLitres.getText());
                int prix = DonneesMemoire.chargerProduits().get(index).prixParLitre * litres;
                labelTotal.setText(prix + " Ar");
            }
        } catch (Exception e) { labelTotal.setText("0 Ar"); }
    }

    private void validerVente() {
        try {
            int index = comboProduit.getSelectedIndex();
            int litres = Integer.parseInt(champLitres.getText());
            var p = DonneesMemoire.chargerProduits().get(index);
            if (p.stock >= litres) {
                p.stock -= litres;
                int total = litres * p.prixParLitre;
                DonneesMemoire.recetteDuJour += total;
                DonneesMemoire.Vente vente = new DonneesMemoire.Vente(champClient.getText(), p.designation, litres, total);
                DonneesMemoire.historiqueVentes.add(vente);

                // --- AFFICHAGE DU REÇU DANS LA ZONE TEXTE ---
                String recu = "*********************************\n";
                recu += "       STATION ESSENCE\n";
                recu += "*********************************\n";
                recu += "Date    : " + LocalDate.now() + "\n";
                recu += "Client  : " + champClient.getText() + "\n";
                recu += "---------------------------------\n";
                recu += p.designation + "          " + total + " Ar\n";
                recu += "---------------------------------\n";
                recu += "TOTAL   : " + total + " Ar\n";
                recu += "*********************************\n";
                recu += "Merci de votre visite !";
                zoneReçu.setText(recu);

                // ACTIVER LE BOUTON PDF
                JPanel basRecu = (JPanel) ((JPanel) this.getComponent(1)).getComponent(1);
                JButton btnPDF = (JButton) basRecu.getComponent(0);
                btnPDF.setEnabled(true);

                modeleHistorique.addRow(new Object[]{p.designation, champClient.getText(), litres, total + " Ar", LocalDate.now().toString(), "Supprimer"});
                JOptionPane.showMessageDialog(this, "Vente validée !");
                champClient.setText(""); champLitres.setText(""); labelTotal.setText("0 Ar");
                remplirCombo();
            } else {
                JOptionPane.showMessageDialog(this, "Stock insuffisant !");
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur !"); }
    }

    private void genererPDF() {
        String texteRecu = zoneReçu.getText();
        try {
            String chemin = System.getProperty("user.home") + "\\Desktop\\recu_vente.pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(chemin));
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.add(new Paragraph(texteRecu.replace("\n", "\n")));
            doc.close();
            JOptionPane.showMessageDialog(this, "PDF généré sur le bureau !");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur PDF : " + e.getMessage());
        }
    }
}