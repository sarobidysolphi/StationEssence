package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PageServices extends JPanel {
    private DefaultTableModel modele;
    private StyledTable tableau;
    private PlaceholderTextField champRecherche;

    public PageServices() {
        setLayout(new BorderLayout());
        setBackground(Theme.FOND_CLAIR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.FOND_CLAIR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel titre = new JLabel("Gestion des services");
        titre.setFont(Theme.POLICE_TITRE);
        titre.setForeground(Theme.TEXTE_FONCE);
        JLabel sousTitre = new JLabel("Ajouter, modifier et supprimer des services");
        sousTitre.setFont(Theme.POLICE_SOUS_TITRE);
        sousTitre.setForeground(Theme.TEXTE_SECONDAIRE);
        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(sousTitre, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel outilPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        outilPanel.setBackground(Theme.FOND_CLAIR);

        champRecherche = new PlaceholderTextField("Rechercher...");
        champRecherche.setPreferredSize(new Dimension(220, 34));
        champRecherche.setFont(Theme.POLICE_NORMALE);

        JButton btnNouveau = MacButton.primary("+ Nouveau service");
        btnNouveau.addActionListener(e -> ajouterService());

        outilPanel.add(champRecherche);
        outilPanel.add(btnNouveau);

        JPanel tableCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(Theme.FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
            }
        };
        tableCard.setOpaque(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(outilPanel, BorderLayout.CENTER);
        tableCard.add(topPanel, BorderLayout.NORTH);

        String[] colonnes = {"ID", "SERVICE", "PRIX (Ar)", "ACTIONS"};
        modele = new DefaultTableModel(new Object[][]{}, colonnes) {
            @Override public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        tableau = new StyledTable(modele);
        tableau.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        tableau.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scroll = new JScrollPane(tableau);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Theme.FOND_CARTE);
        tableCard.add(scroll, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);

        rafraichirTableau();

        champRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { rafraichirTableau(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { rafraichirTableau(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { rafraichirTableau(); }
        });
    }

    private void ajouterService() {
        JTextField champId = new JTextField(10);
        JTextField champNom = new JTextField(15);
        JTextField champPrix = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Num Service :")); form.add(champId);
        form.add(new JLabel("Nom :")); form.add(champNom);
        form.add(new JLabel("Prix (Ar) :")); form.add(champPrix);

        int result = JOptionPane.showConfirmDialog(this, form, "Nouveau service", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = champId.getText().trim();
                String nom = champNom.getText().trim();
                int prix = Integer.parseInt(champPrix.getText().trim());
                if (id.isEmpty() || nom.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Remplissez tous les champs !");
                    return;
                }
                if (ServiceDAO.ajouter(new ServiceEnt(id, nom, prix))) {
                    rafraichirTableau();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout !\n" + ServiceDAO.lastErreur);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Prix invalide !");
            }
        }
    }

    public void rafraichirTableau() {
        modele.setRowCount(0);
        String recherche = champRecherche.getText().toLowerCase();
        for (ServiceEnt s : ServiceDAO.getAll()) {
            if (!recherche.isEmpty() && !s.getService().toLowerCase().contains(recherche)) continue;
            modele.addRow(new Object[]{s.getNumServ(), s.getService(), String.format("%,d", s.getPrix()), "Actions"});
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
                List<ServiceEnt> services = ServiceDAO.getAll();
                if (currentRow >= services.size()) return;
                ServiceEnt s = services.get(currentRow);

                JTextField champNom = new JTextField(s.getService(), 15);
                JTextField champPrix = new JTextField(String.valueOf(s.getPrix()), 10);

                JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
                form.add(new JLabel("Nom :")); form.add(champNom);
                form.add(new JLabel("Prix (Ar) :")); form.add(champPrix);

                int result = JOptionPane.showConfirmDialog(null, form, "Modifier service", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        s = new ServiceEnt(s.getNumServ(), champNom.getText().trim(),
                                Integer.parseInt(champPrix.getText().trim()));
                        ServiceDAO.modifier(s.getNumServ(), s);
                        rafraichirTableau();
                    } catch (Exception ex) {}
                }
            });

            btnSupprimer.addActionListener(e -> {
                fireEditingStopped();
                List<ServiceEnt> services = ServiceDAO.getAll();
                if (currentRow >= services.size()) return;
                int confirm = JOptionPane.showConfirmDialog(null, "Supprimer ce service ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ServiceDAO.supprimer(services.get(currentRow).getNumServ());
                    rafraichirTableau();
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
