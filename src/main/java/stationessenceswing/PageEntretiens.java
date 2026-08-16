package stationessenceswing;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileOutputStream;
import java.time.LocalDate;

public class PageEntretiens extends JPanel {
    private JTextField champNom, champVoiture;
    private JCheckBox chkLavage, chkGonflage, chkVidange, chkGraissage;
    private JLabel labelTotal;

    public PageEntretiens() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titre = new JLabel("Fiche d'entretien");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(titre, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(" Informations client "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Nom du client :"), gbc);
        gbc.gridx = 1; champNom = new JTextField(20); form.add(champNom, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Voiture (immatriculation) :"), gbc);
        gbc.gridx = 1; champVoiture = new JTextField(20); form.add(champVoiture, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Services :"), gbc);

        JPanel checkPanel = new JPanel(new GridLayout(4, 1));
        checkPanel.setBackground(Color.WHITE);
        chkLavage = new JCheckBox("Lavage — 20 000 Ar");
        chkGonflage = new JCheckBox("Gonflage — 2 000 Ar");
        chkVidange = new JCheckBox("Vidange — 35 000 Ar");
        chkGraissage = new JCheckBox("Graissage — 10 000 Ar");
        checkPanel.add(chkLavage); checkPanel.add(chkGonflage); checkPanel.add(chkVidange); checkPanel.add(chkGraissage);

        javax.swing.event.ChangeListener listener = e -> calculerTotal();
        chkLavage.addChangeListener(listener);
        chkGonflage.addChangeListener(listener);
        chkVidange.addChangeListener(listener);
        chkGraissage.addChangeListener(listener);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridheight = 4; form.add(checkPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridheight = 1; form.add(new JLabel("Total :"), gbc);
        gbc.gridx = 1; labelTotal = new JLabel("0 Ar"); labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 20)); labelTotal.setForeground(new Color(46, 125, 50)); form.add(labelTotal, gbc);

        gbc.gridx = 1; gbc.gridy = 7; 
        JButton btnValider = new JButton("📄 Générer le PDF");
        btnValider.setBackground(new Color(46, 125, 50)); btnValider.setForeground(Color.WHITE); btnValider.setFocusPainted(false); btnValider.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnValider.addActionListener(e -> genererPDF());
        form.add(btnValider, gbc);

        add(form, BorderLayout.CENTER);
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
        if (nom.isEmpty() || voiture.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir le nom et la voiture !");
            return;
        }
        int total = Integer.parseInt(labelTotal.getText().replace(" Ar", ""));
        if (total == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez cocher au moins un service !");
            return;
        }

        DonneesMemoire.recetteDuJour += total;

        try {
            // Chemin sécurisé vers le bureau
          String chemin = "C:\\Users\\Solphi\\OneDrive\\Desktop\\recu_entretien.pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(chemin));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("STATION ESSENCE").setBold().setFontSize(20));
            document.add(new Paragraph("Reçu d'entretien").setFontSize(16));
            document.add(new Paragraph("Date : " + LocalDate.now()));
            document.add(new Paragraph("Client : " + nom));
            document.add(new Paragraph("Voiture : " + voiture));

            document.add(new Paragraph("\n"));
            Table table = new Table(UnitValue.createPercentArray(new float[]{400, 150}));
            table.addCell("Service");
            table.addCell("Montant");

            if (chkLavage.isSelected()) { table.addCell("Lavage"); table.addCell("20 000 Ar"); }
            if (chkGonflage.isSelected()) { table.addCell("Gonflage"); table.addCell("2 000 Ar"); }
            if (chkVidange.isSelected()) { table.addCell("Vidange"); table.addCell("35 000 Ar"); }
            if (chkGraissage.isSelected()) { table.addCell("Graissage"); table.addCell("10 000 Ar"); }

            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("TOTAL : " + total + " Ar").setBold().setFontSize(16));

            document.close();

            JOptionPane.showMessageDialog(this, "✅ PDF généré sur votre Bureau !\nFichier : recu_entretien.pdf");

            champNom.setText(""); champVoiture.setText("");
            chkLavage.setSelected(false); chkGonflage.setSelected(false); chkVidange.setSelected(false); chkGraissage.setSelected(false);
            labelTotal.setText("0 Ar");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}