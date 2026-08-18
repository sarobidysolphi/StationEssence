package stationessenceswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class MacButton extends JButton {
    private Color baseColor;
    private Color hoverColor;
    private Color currentColor;
    private Timer animTimer;

    public MacButton(String text, Color base) {
        this(text, base, base.brighter());
    }

    public MacButton(String text, Color base, Color hover) {
        super(text);
        this.baseColor = base;
        this.hoverColor = hover;
        this.currentColor = base;

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(Theme.POLICE_GRAS);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(getPreferredSize().width + 20, 34));
        setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startAnimation(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startAnimation(baseColor);
            }
        });
    }

    private void startAnimation(Color target) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
        animTimer = new Timer(16, e -> {
            Color from = currentColor;
            int r = lerp(from.getRed(), target.getRed(), 0.2f);
            int g = lerp(from.getGreen(), target.getGreen(), 0.2f);
            int b = lerp(from.getBlue(), target.getBlue(), 0.2f);
            currentColor = new Color(r, g, b);
            repaint();

            if (Math.abs(currentColor.getRed() - target.getRed()) < 2 &&
                Math.abs(currentColor.getGreen() - target.getGreen()) < 2 &&
                Math.abs(currentColor.getBlue() - target.getBlue()) < 2) {
                currentColor = target;
                animTimer.stop();
                repaint();
            }
        });
        animTimer.start();
    }

    private int lerp(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = h;

        if (isEnabled()) {
            g2.setColor(currentColor);
        } else {
            g2.setColor(Theme.BORDURE);
        }
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

        super.paintComponent(g);
        g2.dispose();
    }

    public static MacButton primary(String text) {
        return new MacButton(text, Theme.BLEU_ACCENT, Theme.BLEU_HOVER);
    }

    public static MacButton danger(String text) {
        return new MacButton(text, Theme.ROUGE_ACCENT, new Color(255, 80, 70));
    }

    public static MacButton success(String text) {
        return new MacButton(text, Theme.VERT_ACCENT, new Color(70, 210, 105));
    }

    public static MacButton ghost(String text) {
        MacButton btn = new MacButton(text, new Color(240, 240, 245), new Color(225, 225, 230));
        btn.setForeground(Theme.TEXTE_FONCE);
        return btn;
    }
}
