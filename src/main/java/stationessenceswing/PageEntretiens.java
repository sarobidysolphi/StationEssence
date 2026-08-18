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
import java.util.ArrayList;
import java.util.List;

public class PageEntretiens extends JPanel {
    private JTextField champNom, champVoiture;
    private JLabel labelTotal;
    private JTextArea zoneRecu;
    private DefaultTableModel modeleHistorique;
    private StyledTable tableau;
    private JButton btnPDF;
    private List<JCheckBox> checkBoxServices = new ArrayList<>();
    private JPanel checkPanel;

    public PageEntretiens() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Fiche d'entretien");
        titre.setFont(Theme.POLICE_TITRE);
        JLabel sousTitre = new JLabel("Selectionner les services et enregistrer");
        sousTitre.setFont(Theme.POLICE_SOUS_TITRE);
        sousTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contenu = new JPanel(new GridLayout(1, 2, 16, 0));
        contenu.setBackground(Theme.FOND_CLAIR);

        // Formulaire
        JPanel formCard = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        formCard.setOpaque(false);
        formCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(new JLabel("Nom du client :"), gbc);
        gbc.gridx = 1; champNom = new JTextField(15); formCard.add(champNom, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(new JLabel("Voiture :"), gbc);
        gbc.gridx = 1; champVoiture = new JTextField(15); formCard.add(champVoiture, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formCard.add(new JLabel("Services :"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        checkPanel = new JPanel(new GridLayout(0, 1));
        checkPanel.setBackground(Color.WHITE);
        genererCheckBoxes();
        formCard.add(checkPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formCard.add(new JLabel("Total :"), gbc);
        gbc.gridx = 1; labelTotal = new JLabel("0 Ar"); labelTotal.setFont(Theme.POLICE_GRANDE); labelTotal.setForeground(Theme.BLEU_ACCENT); formCard.add(labelTotal, gbc);

        contenu.add(formCard);

        // Droite
        JPanel droite = new JPanel(new BorderLayout());
        droite.setOpaque(false);

        JPanel recuCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        recuCard.setOpaque(false);
        JLabel recuTitre = new JLabel("   Recu d'entretien");
        recuTitre.setFont(Theme.POLICE_GRAS);
        recuTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        recuCard.add(recuTitre, BorderLayout.NORTH);

        zoneRecu = new JTextArea();
        zoneRecu.setEditable(false);
        zoneRecu.setFont(new Font("Consolas", Font.PLAIN, 13));
        zoneRecu.setBorder(new EmptyBorder(8, 16, 8, 16));
        zoneRecu.setText("Remplissez le formulaire et cochez des services.");
        recuCard.add(new JScrollPane(zoneRecu), BorderLayout.CENTER);

        JPanel basRecu = new JPanel();
        basRecu.setOpaque(false);
        btnPDF = MacButton.primary("Enregistrer et imprimer");
        btnPDF.setEnabled(false);
        btnPDF.addActionListener(e -> validerEtImprimer());
        basRecu.add(btnPDF);
        recuCard.add(basRecu, BorderLayout.SOUTH);

        JPanel histCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        histCard.setOpaque(false);
        JLabel histTitre = new JLabel("   Historique des entretiens");
        histTitre.setFont(Theme.POLICE_GRAS);
        histTitre.setBorder(new EmptyBorder(8, 4, 8, 0));
        histCard.add(histTitre, BorderLayout.NORTH);

        String[] cols = {"CLIENT", "VOITURE", "SERVICES", "TOTAL", "DATE", "ACTIONS"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        tableau = new StyledTable(modeleHistorique);
        histCard.add(new JScrollPane(tableau), BorderLayout.CENTER);

        droite.add(recuCard, BorderLayout.CENTER);
        droite.add(histCard, BorderLayout.SOUTH);
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
                if (row >= 0 && col == 5) {
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
            JCheckBox chk = new JCheckBox(s.nom + " - " + String.format("%,d", s.prix) + " Ar");
            chk.setFont(Theme.POLICE_NORMALE);
            checkBoxServices.add(chk);
            checkPanel.add(chk);
        }
        checkPanel.revalidate();
        checkPanel.repaint();
    }

    private void calculerTotal() {
        int total = 0;
        for (int i = 0; i < checkBoxServices.size(); i++) {
            if (checkBoxServices.get(i).isSelected()) {
                total += DonneesMemoire.chargerServices().get(i).prix;
            }
        }
        labelTotal.setText(String.format("%,d", total) + " Ar");

        String nom = champNom.getText().trim();
        String voiture = champVoiture.getText().trim();
        String recu = "****************************\n";
        recu += "      STATION ESSENCE\n";
        recu += "****************************\n\n";
        recu += "Date    : " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n";
        recu += "Client  : " + (nom.isEmpty() ? "---" : nom) + "\n";
        if (!voiture.isEmpty()) recu += "Voiture : " + voiture + "\n";
        recu += "----------------------------\n";
        for (int i = 0; i < checkBoxServices.size(); i++) {
            if (checkBoxServices.get(i).isSelected()) {
                DonneesMemoire.Service s = DonneesMemoire.chargerServices().get(i);
                recu += String.format("%-18s %,10d\n", s.nom, s.prix);
            }
        }
        recu += "----------------------------\n";
        recu += String.format("%-18s %,10d Ar\n", "TOTAL", total);
        recu += "----------------------------\n\n";
        recu += "   Merci de votre visite";
        zoneRecu.setText(recu);
        btnPDF.setEnabled(total > 0 && !nom.isEmpty());
    }

    private void validerEtImprimer() {
        int total = 0;
        List<String> servicesSelectionnes = new ArrayList<>();
        for (int i = 0; i < checkBoxServices.size(); i++) {
            if (checkBoxServices.get(i).isSelected()) {
                DonneesMemoire.Service s = DonneesMemoire.chargerServices().get(i);
                total += s.prix;
                servicesSelectionnes.add(s.nom);
            }
        }

        String nom = champNom.getText().trim();
        String voiture = champVoiture.getText().trim();
        String servicesStr = String.join(", ", servicesSelectionnes);

        DonneesMemoire.historiqueEntretiens.add(new DonneesMemoire.Entretien(nom, voiture, servicesStr, total, DonneesMemoire.aujourdHui()));
        modeleHistorique.addRow(new Object[]{nom, voiture, servicesStr, String.format("%,d", total) + " Ar", DonneesMemoire.aujourdHui(), "Supprimer"});

        try {
            String chemin = System.getProperty("user.home") + "\\Desktop\\recu_entretien.pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(chemin));
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.add(new Paragraph(zoneRecu.getText()));
            doc.close();
            JOptionPane.showMessageDialog(this, "PDF genere sur le bureau !");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur PDF : " + e.getMessage());
        }

        champNom.setText(""); champVoiture.setText("");
        for (JCheckBox chk : checkBoxServices) chk.setSelected(false);
        labelTotal.setText("0 Ar");
        zoneRecu.setText("Remplissez le formulaire et cochez des services.");
        btnPDF.setEnabled(false);
    }
}
