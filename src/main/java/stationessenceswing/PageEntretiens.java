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

public class PageEntretiens extends JPanel {
    private JTextField champNom, champVoiture;
    private JCheckBox chkLavage, chkGonflage, chkVidange, chkGraissage;
    private JLabel labelTotal;
    private DefaultTableModel modeleHistorique;
    private JTable tableau;

    public PageEntretiens() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Fiche d'entretien (CRUD)");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titre, BorderLayout.NORTH);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 20, 0));
        contenu.setBackground(new Color(245, 247, 250));

        // Formulaire
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder(" Informations client "));
        form.add(new JLabel("Nom du client :")); form.add(champNom = new JTextField());
        form.add(new JLabel("Voiture :")); form.add(champVoiture = new JTextField());
        form.add(new JLabel("Services :"));
        JPanel checkPanel = new JPanel(new GridLayout(4, 1));
        chkLavage = new JCheckBox("Lavage 20 000 Ar");
        chkGonflage = new JCheckBox("Gonflage 2 000 Ar");
        chkVidange = new JCheckBox("Vidange 35 000 Ar");
        chkGraissage = new JCheckBox("Graissage 10 000 Ar");
        checkPanel.add(chkLavage); checkPanel.add(chkGonflage); checkPanel.add(chkVidange); checkPanel.add(chkGraissage);
        form.add(checkPanel);
        form.add(new JLabel("Total :")); form.add(labelTotal = new JLabel("0 Ar", SwingConstants.CENTER));
        contenu.add(form);

        // Historique
        JPanel histPanel = new JPanel(new BorderLayout());
        histPanel.setBackground(Color.WHITE);
        histPanel.setBorder(BorderFactory.createTitledBorder(" Historique des entretiens "));
        String[] cols = {"CLIENT", "VOITURE", "TOTAL", "DATE", "ACTIONS"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        tableau = new JTable(modeleHistorique);
        tableau.setRowHeight(40);
        tableau.getTableHeader().setBackground(new Color(40, 80, 200));
        tableau.getTableHeader().setForeground(Color.WHITE);
        histPanel.add(new JScrollPane(tableau), BorderLayout.CENTER);
        contenu.add(histPanel);

        add(contenu, BorderLayout.CENTER);

        JPanel bas = new JPanel();
        JButton btnPDF = new JButton("Générer le PDF");
        btnPDF.setBackground(new Color(40, 80, 200));
        btnPDF.setForeground(Color.WHITE);
        btnPDF.setFocusPainted(false);
        btnPDF.setBorderPainted(false);
        btnPDF.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPDF.setPreferredSize(new Dimension(160, 35));
        btnPDF.addActionListener(e -> genererPDF());
        bas.add(btnPDF);
        add(bas, BorderLayout.SOUTH);

        javax.swing.event.ChangeListener update = e -> calculerTotal();
        chkLavage.addChangeListener(update); chkGonflage.addChangeListener(update);
        chkVidange.addChangeListener(update); chkGraissage.addChangeListener(update);

        // Écouteur de suppression
        tableau.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tableau.rowAtPoint(evt.getPoint());
                int col = tableau.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 4) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Supprimer cet entretien ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DonneesMemoire.historiqueEntretiens.remove(row);
                        modeleHistorique.removeRow(row);
                    }
                }
            }
        });
    }

    private void calculerTotal() {
        int total = 0;
        if (chkLavage.isSelected()) total += 20000;
        if (chkGonflage.isSelected()) total += 2000;
        if (chkVidange.isSelected()) total += 35000;
        if (chkGraissage.isSelected()) total += 10000;
        labelTotal.setText(total + " Ar");
    }

    private void genererPDF() {
        String nom = champNom.getText().trim();
        String voiture = champVoiture.getText().trim();
        if (nom.isEmpty() || voiture.isEmpty()) { JOptionPane.showMessageDialog(this, "Remplissez le formulaire !"); return; }
        int total = Integer.parseInt(labelTotal.getText().replace(" Ar", ""));
        try {
            String chemin = System.getProperty("user.home") + "\\Desktop\\recu_entretien.pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(chemin));
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.add(new Paragraph("STATION ESSENCE").setBold());
            doc.add(new Paragraph("Reçu d'entretien\n"));
            doc.add(new Paragraph("Client : " + nom + "\nVoiture : " + voiture + "\n"));
            Table table = new Table(2);
            table.addCell("Service"); table.addCell("Montant");
            if (chkLavage.isSelected()) { table.addCell("Lavage"); table.addCell("20 000 Ar"); }
            if (chkGonflage.isSelected()) { table.addCell("Gonflage"); table.addCell("2 000 Ar"); }
            if (chkVidange.isSelected()) { table.addCell("Vidange"); table.addCell("35 000 Ar"); }
            if (chkGraissage.isSelected()) { table.addCell("Graissage"); table.addCell("10 000 Ar"); }
            doc.add(table); doc.add(new Paragraph("\nTOTAL : " + total + " Ar"));
            doc.close();
            JOptionPane.showMessageDialog(this, "PDF généré sur le bureau !");
            DonneesMemoire.historiqueEntretiens.add(new DonneesMemoire.Entretien(nom, voiture, total, LocalDate.now().toString()));
            modeleHistorique.addRow(new Object[]{nom, voiture, total + " Ar", LocalDate.now().toString(), "Supprimer"});
            champNom.setText(""); champVoiture.setText("");
            chkLavage.setSelected(false); chkGonflage.setSelected(false); chkVidange.setSelected(false); chkGraissage.setSelected(false);
            labelTotal.setText("0 Ar");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage()); }
    }
}