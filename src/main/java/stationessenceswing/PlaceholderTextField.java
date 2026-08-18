package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;

public class PlaceholderTextField extends JTextField implements FocusListener {
    private String placeholder;
    private boolean showingPlaceholder;

    public PlaceholderTextField(String placeholder) {
        super(placeholder);
        this.placeholder = placeholder;
        this.showingPlaceholder = true;
        this.setForeground(Theme.TEXTE_SECONDAIRE);
        this.setFont(Theme.POLICE_NORMALE);
        this.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, Theme.BORDURE),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText("");
            this.setForeground(Theme.TEXTE_FONCE);
            showingPlaceholder = false;
        }
        this.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, Theme.BLEU_ACCENT),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText(placeholder);
            this.setForeground(Theme.TEXTE_SECONDAIRE);
            showingPlaceholder = true;
        }
        this.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, Theme.BORDURE),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
    }

    @Override
    public String getText() {
        return showingPlaceholder ? "" : super.getText();
    }

    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
