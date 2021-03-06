package com.tannerlittle.uno.view;

import com.tannerlittle.uno.enums.Suit;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class HiddenCardPanel extends JPanel {

    private int width;
    private int height;

    private String text;

    private final Border border;
    private final Border focused;

    public HiddenCardPanel(int width, int height, String text, boolean borders) {
        this.border = BorderFactory.createEtchedBorder(WHEN_FOCUSED, Color.WHITE, Color.BLACK);
        this.focused = BorderFactory.createEtchedBorder(WHEN_FOCUSED, Color.WHITE, Color.WHITE);

        this.width = width;
        this.height = height;

        this.text = text;

        this.setOpaque(false);
        this.setPreferredSize(new Dimension(width, height));

        if (borders) {
            this.setBorder(border);

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    HiddenCardPanel.this.setBorder(focused);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    HiddenCardPanel.this.setBorder(border);
                }
            });
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        int margin = 5;
        g2.setColor(Color.BLACK);
        g2.fillRect(margin, margin, width-2*margin, height-2*margin);

        g2.setColor(Suit.RED.getColor());
        AffineTransform org = g2.getTransform();
        g2.rotate(0.5, (width / 8 * 7), (height));

        g2.fillOval(-(width / 5), height / 4, (width / 5 * 4), (height / 12 * 11));
        g2.setTransform(org);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.fillRect(0, 0, margin, height);
        g2.fillRect(width-margin, 0, margin, height);

        this.drawTextShadow(g2, Font.BOLD, (width / 3 + 5), (width/2), (height/2));
    }

    private void drawTextShadow(Graphics graphics, int style, int size, int x, int y) {
        Font font = new Font("Helvetica", style, size);
        Graphics2D g2 = (Graphics2D) graphics;

        FontMetrics metrics = this.getFontMetrics(font);
        x -= (metrics.stringWidth(text) / 2);
        y += (font.getSize() / 3);

        TextLayout layout = new TextLayout(text, font, g2.getFontRenderContext());
        g2.setPaint(Color.BLACK);
        layout.draw(g2, x - 3, y + 3);

        g2.setPaint(Color.WHITE);
        layout.draw(g2, x, y);
    }
}