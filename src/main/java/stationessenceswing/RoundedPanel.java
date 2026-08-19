package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int rayon;
    private Color ombreCouleur;

    public RoundedPanel(int rayon) {
        this(rayon, Theme.FOND_CARTE, new Color(0, 0, 0, 40));
    }

    public RoundedPanel(int rayon, Color fond, Color ombre) {
        this.rayon = rayon;
        this.ombreCouleur = ombre;
        setOpaque(false);
        setBackground(fond);
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(ombreCouleur);
        g2.fillRoundRect(3, 3, w - 4, h - 4, rayon, rayon);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w - 2, h - 2, rayon, rayon);

        g2.dispose();
    }
}
