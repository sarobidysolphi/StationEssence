package stationessenceswing;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(new JLabel("Immatriculation :"), gbc);
        gbc.gridx = 1; champVoiture = new JTextField(15); formCard.add(champVoiture, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formCard.add(new JLabel("Services :"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        checkPanel = new JPanel(new GridLayout(0, 1));
        checkPanel.setBackground(Theme.FOND_CARTE);
        checkPanel.setBorder(BorderFactory.createLineBorder(Theme.BORDURE));
        formCard.add(checkPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formCard.add(new JLabel("Total :"), gbc);
        gbc.gridx = 1; labelTotal = new JLabel("0 Ar"); labelTotal.setFont(Theme.POLICE_GRANDE); labelTotal.setForeground(Theme.BLEU_ACCENT); formCard.add(labelTotal, gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton btnActualiser = MacButton.ghost("Actualiser services");
        btnActualiser.addActionListener(e -> genererCheckBoxes());
        btnPanel.add(btnActualiser);
        JButton btnValider = MacButton.primary("Enregistrer");
        btnValider.addActionListener(e -> validerEntretien());
        btnPanel.add(btnValider);
        JButton btnNouveau = MacButton.ghost("Nouveau");
        btnNouveau.addActionListener(e -> reinitialiserFormulaire());
        btnPanel.add(btnNouveau);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formCard.add(btnPanel, gbc);

        contenu.add(formCard);

        JPanel droite = new JPanel(new BorderLayout(0, 12));
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
        recuCard.setPreferredSize(new Dimension(0, 300));

        JPanel recuHeader = new JPanel(new BorderLayout());
        recuHeader.setOpaque(false);
        recuHeader.setBorder(new EmptyBorder(8, 12, 4, 12));
        JLabel recuTitre = new JLabel("Recu d'entretien");
        recuTitre.setFont(Theme.POLICE_GRAS);
        recuHeader.add(recuTitre, BorderLayout.WEST);

        btnPDF = MacButton.primary("Generer le PDF");
        btnPDF.addActionListener(e -> genererPDFFromRecu());
        recuHeader.add(btnPDF, BorderLayout.EAST);
        recuCard.add(recuHeader, BorderLayout.NORTH);

        zoneRecu = new JTextArea();
        zoneRecu.setEditable(false);
        zoneRecu.setFont(new Font("Consolas", Font.PLAIN, 13));
        zoneRecu.setBackground(Theme.FOND_CARTE);
        zoneRecu.setForeground(Theme.TEXTE_FONCE);
        zoneRecu.setBorder(new EmptyBorder(8, 16, 8, 16));
        zoneRecu.setText("Remplissez le formulaire et cochez des services\npour voir le recu ici.");
        recuCard.add(new JScrollPane(zoneRecu), BorderLayout.CENTER);

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

        String[] cols = {"NUM", "SERVICE", "VOITURE", "CLIENT", "DATE", "ACTIONS"};
        modeleHistorique = new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        tableau = new StyledTable(modeleHistorique);
        tableau.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tableau.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        histCard.add(new JScrollPane(tableau), BorderLayout.CENTER);

        droite.add(recuCard, BorderLayout.NORTH);
        droite.add(histCard, BorderLayout.CENTER);
        contenu.add(droite);

        add(contenu, BorderLayout.CENTER);

        genererCheckBoxes();
        rafraichirHistorique();

        champNom.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
        });
        champVoiture.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculerTotal(); }
        });
    }

    public void genererCheckBoxes() {
        checkBoxServices.clear();
        checkPanel.removeAll();
        List<ServiceEnt> services = ServiceDAO.getAll();
        javax.swing.event.ChangeListener update = e -> calculerTotal();
        for (ServiceEnt s : services) {
            JCheckBox chk = new JCheckBox(s.getService() + " - " + String.format("%,d", s.getPrix()) + " Ar");
            chk.setFont(Theme.POLICE_NORMALE);
            chk.putClientProperty("service", s);
            chk.addChangeListener(update);
            checkBoxServices.add(chk);
            checkPanel.add(chk);
        }
        checkPanel.revalidate();
        checkPanel.repaint();
    }

    private void calculerTotal() {
        int total = 0;
        for (JCheckBox chk : checkBoxServices) {
            if (chk.isSelected()) {
                ServiceEnt s = (ServiceEnt) chk.getClientProperty("service");
                if (s != null) total += s.getPrix();
            }
        }
        labelTotal.setText(String.format("%,d", total) + " Ar");

        String nom = champNom.getText().trim();
        String voiture = champVoiture.getText().trim();

        StringBuilder recu = new StringBuilder();
        recu.append("Date : ").append(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n\n");
        recu.append("Client : ").append(nom.isEmpty() ? "---" : nom).append("\n");
        recu.append("Voiture : ").append(voiture.isEmpty() ? "---" : voiture).append("\n\n");

        String sep = "+----------------------+----------------------+\n";
        String sepDouble = "+======================+======================+\n";

        recu.append(sep);
        recu.append(String.format("| %-20s | %-20s |\n", "Service", "Montant"));
        recu.append(sep);

        for (JCheckBox chk : checkBoxServices) {
            if (chk.isSelected()) {
                ServiceEnt s = (ServiceEnt) chk.getClientProperty("service");
                if (s != null) {
                    recu.append(String.format("| %-20s | %,18d Ar |\n", s.getService(), s.getPrix()));
                }
            }
        }

        recu.append(sepDouble);
        recu.append(String.format("| %-20s | %,18d Ar |\n", "TOTAL", total));
        recu.append(sep);

        zoneRecu.setText(recu.toString());
    }

    private void validerEntretien() {
        String nom = champNom.getText().trim();
        String voiture = champVoiture.getText().trim();

        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nom de client !");
            return;
        }

        if (voiture.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer l'immatriculation !");
            return;
        }

        boolean hasSelection = false;
        for (JCheckBox chk : checkBoxServices) {
            if (chk.isSelected()) { hasSelection = true; break; }
        }
        if (!hasSelection) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner au moins un service !");
            return;
        }

        boolean succes = true;
        for (JCheckBox chk : checkBoxServices) {
            if (chk.isSelected()) {
                ServiceEnt s = (ServiceEnt) chk.getClientProperty("service");
                if (s != null) {
                    String numEntr = EntretienDAO.genererId();
                    String dateAujourdhui = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                    Entretien ent = new Entretien(numEntr, s.getService(), voiture, nom, dateAujourdhui);
                    if (!EntretienDAO.ajouter(ent, s.getNumServ())) {
                        succes = false;
                    }
                }
            }
        }

        rafraichirHistorique();

        if (succes) {
            JOptionPane.showMessageDialog(this, "Entretien enregistre avec succes !");
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement !");
        }
    }

    private void reinitialiserFormulaire() {
        champNom.setText("");
        champVoiture.setText("");
        for (JCheckBox chk : checkBoxServices) chk.setSelected(false);
        labelTotal.setText("0 Ar");
        zoneRecu.setText("Remplissez le formulaire et cochez des services\npour voir le recu ici.");
    }

    private void genererPDFFromRecu() {
        if (zoneRecu.getText().contains("Remplissez le formulaire")) {
            JOptionPane.showMessageDialog(this, "Enregistrez d'abord un entretien pour generer le PDF !");
            return;
        }
        try {
            String chemin = "C:\\Users\\Solphi\\OneDrive\\Desktop\\recu_entretien.pdf";
            PdfWriter writer = new PdfWriter(new FileOutputStream(chemin));
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.add(new Paragraph("STATION ESSENCE").setBold().setFontSize(16));
            doc.add(new Paragraph(zoneRecu.getText()).setFontSize(11));
            doc.add(new Paragraph("\nMerci pour votre visite !").setFontSize(11));
            doc.close();
            JOptionPane.showMessageDialog(this, "PDF genere sur le Bureau !");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur PDF : " + e.getMessage());
        }
    }

    public void rafraichirHistorique() {
        modeleHistorique.setRowCount(0);
        for (Entretien e : EntretienDAO.getAll()) {
            modeleHistorique.addRow(new Object[]{e.getNumEntr(), e.getNumServ(), e.getImmatriculation(), e.getNomClient(), e.getDateEntretien(), "Actions"});
        }
    }

    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton btnModifier, btnSupprimer;
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
            setBackground(Theme.FOND_CARTE);
            btnModifier = MacButton.ghost("Modifier");
            btnModifier.setPreferredSize(new Dimension(80, 28));
            btnModifier.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnSupprimer = MacButton.danger("Supprimer");
            btnSupprimer.setPreferredSize(new Dimension(80, 28));
            btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 11));
            add(btnModifier); add(btnSupprimer);
        }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnModifier, btnSupprimer;
        private int currentRow;
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            panel.setBackground(Theme.FOND_CARTE);
            btnModifier = MacButton.ghost("Modifier");
            btnModifier.setPreferredSize(new Dimension(80, 28));
            btnModifier.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnSupprimer = MacButton.danger("Supprimer");
            btnSupprimer.setPreferredSize(new Dimension(80, 28));
            btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 11));
            panel.add(btnModifier); panel.add(btnSupprimer);

            btnModifier.addActionListener(e -> {
                fireEditingStopped();
                String numEntr = (String) modeleHistorique.getValueAt(currentRow, 0);
                String serviceActuel = (String) modeleHistorique.getValueAt(currentRow, 1);
                String voitureActuelle = (String) modeleHistorique.getValueAt(currentRow, 2);
                String clientActuel = (String) modeleHistorique.getValueAt(currentRow, 3);

                JComboBox<String> comboService = new JComboBox<>();
                List<ServiceEnt> services = ServiceDAO.getAll();
                int selectIdx = 0;
                for (int i = 0; i < services.size(); i++) {
                    ServiceEnt s = services.get(i);
                    comboService.addItem(s.getService() + " - " + String.format("%,d", s.getPrix()) + " Ar");
                    if (s.getService().equals(serviceActuel)) selectIdx = i;
                }
                if (selectIdx < comboService.getItemCount()) comboService.setSelectedIndex(selectIdx);

                JTextField champClientModif = new JTextField(clientActuel, 15);
                JTextField champVoitureModif = new JTextField(voitureActuelle, 15);

                JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
                form.add(new JLabel("Client :")); form.add(champClientModif);
                form.add(new JLabel("Immatriculation :")); form.add(champVoitureModif);
                form.add(new JLabel("Service :")); form.add(comboService);

                int result = JOptionPane.showConfirmDialog(null, form, "Modifier l'entretien", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String newClient = champClientModif.getText().trim();
                    String newVoiture = champVoitureModif.getText().trim();
                    if (newClient.isEmpty() || newVoiture.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Tous les champs sont obligatoires !");
                        return;
                    }
                    int newIdx = comboService.getSelectedIndex();
                    if (newIdx < 0) return;
                    String newNumServ = services.get(newIdx).getNumServ();
                    EntretienDAO.modifier(numEntr, newNumServ, newVoiture, newClient);
                    rafraichirHistorique();
                }
            });

            btnSupprimer.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(null, "Supprimer cet entretien ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String numEntr = (String) modeleHistorique.getValueAt(currentRow, 0);
                    EntretienDAO.supprimer(numEntr);
                    rafraichirHistorique();
                }
            });
        }
        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        @Override public Object getCellEditorValue() { return "Actions"; }
    }
}
