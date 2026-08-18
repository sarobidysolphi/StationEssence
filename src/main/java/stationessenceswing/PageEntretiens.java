package stationessenceswing;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PageEntretiens extends JPanel {
    private JTextField champNom, champVoiture;
    private JLabel labelTotal;
    private JTextArea zoneReçu;
    private DefaultTableModel modeleHistorique;
    private JTable tableau;
    private JButton btnPDF;
    
    private List<JCheckBox> checkBoxServices = new ArrayList<>();
    private JPanel checkPanel;

    public PageEntretiens() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Fiche d'entretien (CRUD)");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titre, BorderLayout.NORTH);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 20, 0));
        contenu.setBackground(new Color(245, 247, 250));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(" Informations client "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Nom du client :"), gbc);
        gbc.gridx = 1; champNom = new JTextField(15); form.add(champNom, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Voiture :"), gbc);
        gbc.gridx = 1; champVoiture = new JTextField(15); form.add(champVoiture, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Services :"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        
        checkPanel = new JPanel(new GridLayout(0, 1));
        checkPanel.setBackground(Color.WHITE);
        genererCheckBoxes();
        form.add(checkPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Total :"), gbc);
        gbc.gridx = 1; labelTotal = new JLabel("0 Ar"); labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 16)); form.add(labelTotal, gbc);

        contenu.add(form);

        JPanel droite = new JPanel(new BorderLayout());
        
        JPanel recuPanel = new JPanel(new BorderLayout());
        recuPanel.setBackground(Color.WHITE);
        recuPanel.setBorder(BorderFactory.createTitledBorder(" Reçu d'entretien "));
        zoneReçu = new JTextArea();
        zoneReçu.setEditable(false);
        zoneReçu.setFont(new Font("Monospaced", Font.PLAIN, 14));
        zoneReçu.setText("Remplissez le formulaire et cochez des services.");
        recuPanel.add(new JScrollPane(zoneReçu), BorderLayout.CENTER);

        JPanel basRecu = new JPanel();
        btnPDF = new JButton("📄 Générer le PDF");
        btnPDF.setBackground(new Color(40, 80, 200));
        btnPDF.setForeground(Color.WHITE);
        btnPDF.setFocusPainted(false);
        btnPDF.setBorderPainted(false);
        btnPDF.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPDF.setEnabled(false);
        btnPDF.addActionListener(e -> genererPDF());
        basRecu.add(btnPDF);
        recuPanel.add(basRecu, BorderLayout.SOUTH);

        JPanel histPanel = new JPanel(new BorderLayout());
        histPanel.setBackground(Color.WHITE);
        histPanel.setBorder(BorderFactory.createTitledBorder(" Historique des entretiens "));
        String[] cols = {"CLIENT", "VOITURE", "TOTAL", "DATE", "ACTIONS"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        tableau = new JTable(modeleHistorique);
        tableau.setRowHeight(35);
        tableau.getTableHeader().setBackground(new Color(40, 80, 200));
        tableau.getTableHeader().setForeground(Color.WHITE);
        histPanel.add(new JScrollPane(tableau), BorderLayout.CENTER);

        droite.add(recuPanel, BorderLayout.CENTER);
        droite.add(histPanel, BorderLayout.SOUTH);
        contenu.add(droite);

        add(contenu, BorderLayout.CENTER);

        javax.swing.event.ChangeListener update = e -> calculerTotal();
        for (JCheckBox chk : checkBoxServices) {
            chk.addChangeListener(update);
        }

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

    private void genererCheckBoxes() {
        checkBoxServices.clear();
        checkPanel.removeAll();
        for (DonneesMemoire.Service s : DonneesMemoire.chargerServices()) {
            JCheckBox chk = new JCheckBox(s.nom + " — " + s.prix + " Ar");
            checkBoxServices.add(chk);
            checkPanel.add(chk);
        }
        checkPanel.revalidate();
        checkPanel.repaint();
    }

    private void calculerTotal() {
        int total = 0;
        for (int i = 0; i < checkBoxServices.size(); i++) {
            JCheckBox chk = checkBoxServices.get(i);
            if (chk.isSelected()) {
                DonneesMemoire.Service s = DonneesMemoire.chargerServices().get(i);
                total += s.prix;
            }
        }
        labelTotal.setText(total + " Ar");

        String nom = champNom.getText().trim();
        String voiture = champVoiture.getText().trim();
        String recu = "*********************************\n";
        recu += "       STATION ESSENCE\n";
        recu += "*********************************\n";
        recu += "Date    : " + LocalDate.now() + "\n";
        recu += "Client  : " + (nom.isEmpty() ? "---" : nom) + "\n";
        recu += "Voiture : " + (voiture.isEmpty() ? "---" : voiture) + "\n";
        recu += "---------------------------------\n";
        for (int i = 0; i < checkBoxServices.size(); i++) {
            JCheckBox chk = checkBoxServices.get(i);
            if (chk.isSelected()) {
                DonneesMemoire.Service s = DonneesMemoire.chargerServices().get(i);
                recu += s.nom + "         " + s.prix + " Ar\n";
            }
        }
        recu += "---------------------------------\n";
        recu += "TOTAL   : " + total + " Ar\n";
        recu += "*********************************\n";
        recu += "Merci de votre visite !";
        zoneReçu.setText(recu);

        btnPDF.setEnabled(total > 0 && !nom.isEmpty());
    }

    private void genererPDF() {
        String texteRecu = zoneReçu.getText();
        try {
            String chemin = System.getProperty("user.home") + "\\Desktop\\recu_entretien.pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(chemin));
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.add(new Paragraph(texteRecu));
            doc.close();
            JOptionPane.showMessageDialog(this, "PDF généré sur le bureau !");

            int total = Integer.parseInt(labelTotal.getText().replace(" Ar", ""));
            DonneesMemoire.historiqueEntretiens.add(new DonneesMemoire.Entretien(champNom.getText().trim(), champVoiture.getText().trim(), total, LocalDate.now().toString()));
            modeleHistorique.addRow(new Object[]{champNom.getText().trim(), champVoiture.getText().trim(), total + " Ar", LocalDate.now().toString(), "Supprimer"});
            champNom.setText(""); champVoiture.setText("");
            for (JCheckBox chk : checkBoxServices) chk.setSelected(false);
            labelTotal.setText("0 Ar");
            zoneReçu.setText("Remplissez le formulaire et cochez des services.");
            btnPDF.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur PDF : " + e.getMessage());
        }
    }
}