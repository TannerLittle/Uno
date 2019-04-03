package com.tannerlittle.uno.view;

import com.tannerlittle.uno.model.Card;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class CardPanel extends JPanel {

    private final Card card;

    private final Border border = BorderFactory.createEtchedBorder(WHEN_FOCUSED, Color.WHITE, Color.GRAY);
    private final Border focused = BorderFactory.createEtchedBorder(WHEN_FOCUSED, Color.BLACK, Color.GRAY);

    public CardPanel(Card card) {
        this.card = card;

        this.setPreferredSize(new Dimension(100,150));
        this.setBorder(border);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                CardPanel.this.setBorder(focused);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                CardPanel.this.setBorder(border);
            }
        });
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int width = 100;
        int height = 150;

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        int margin = 5;
        g2.setColor(card.getSuit().getColor());
        g2.fillRect(margin, margin, width-2*margin, height-2*margin);

        AffineTransform org = g2.getTransform();

        g2.setColor(card.getSuit().getColor());
        g2.rotate(0.5, (width / 8 * 7), height);

        g2.fillOval(-(width / 5), height / 4, (width / 5 * 4), (height / 12 * 11));
        g2.setTransform(org);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.rotate(0.5, (width / 8 * 7), height);

        g2.drawOval(-(width / 5), height / 4, (width / 5 * 4), (height / 12 * 11));
        g2.setTransform(org);

        this.drawTextShadow(g2, Font.BOLD, (width / 2 + 5), (width/2), (height/2));
        this.drawTextShadow(g2, Font.BOLD, (width / 4), 20, 20);
        this.drawTextShadow(g2, Font.BOLD, -(width / 4), (width - 20), (height - 20));
    }

    private void drawTextShadow(Graphics graphics, int style, int size, int x, int y) {
        Font font = new Font("Helvetica", style, size);
        Graphics2D g2 = (Graphics2D) graphics;

        FontMetrics metrics = this.getFontMetrics(font);
        x -= (metrics.stringWidth(card.getRank().getValue()) / 2);
        y += (font.getSize() / 3);

        TextLayout layout = new TextLayout(card.getRank().getValue(), font, g2.getFontRenderContext());
        g2.setPaint(Color.BLACK);
        layout.draw(g2, x - 3, y + 3);

        g2.setPaint(Color.WHITE);
        layout.draw(g2, x, y);
    }
}