package com.tannerlittle.uno.view;

import com.tannerlittle.uno.enums.Suit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;

public class UnoButtonPanel extends JPanel {

    private final int width = 200;
    private final int height = 75;

    private Color color;

    public UnoButtonPanel() {
        super();

        this.setOpaque(false);

        this.color = Suit.RED.getColor();

        this.setPreferredSize(new Dimension(width, height));
        this.addMouseListener(new UnoButtonPanel.MouseHandler());
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(color);
        g2.fillOval(15, 5, width - 30, height - 10);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(15, 5, width - 30, height - 10);

        this.drawTextShadow(g2, Font.BOLD, (height / 2), (width/2), (height/2));
    }

    private void drawTextShadow(Graphics graphics, int style, int size, int x, int y) {
        Font font = new Font("Helvetica", style, size);
        Graphics2D g2 = (Graphics2D) graphics;

        FontMetrics metrics = this.getFontMetrics(font);
        x -= (metrics.stringWidth("Uno!") / 2);
        y += (font.getSize() / 3);

        TextLayout layout = new TextLayout("Uno!", font, g2.getFontRenderContext());
        g2.setPaint(Color.BLACK);
        layout.draw(g2, x - 3, y + 3);

        g2.setPaint(Color.WHITE);
        layout.draw(g2, x, y);
    }

    class MouseHandler extends MouseAdapter {
        public void mouseEntered(MouseEvent event) {
            UnoButtonPanel.this.color = Suit.RED.getFaded();
            UnoButtonPanel.this.repaint();
        }

        public void mouseExited(MouseEvent event){
            UnoButtonPanel.this.color = Suit.RED.getColor();
            UnoButtonPanel.this.repaint();
        }

        public void mouseClicked(MouseEvent event) {

        }
    }
}
