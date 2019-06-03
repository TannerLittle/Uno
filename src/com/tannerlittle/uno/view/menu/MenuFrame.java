package com.tannerlittle.uno.view.menu;

import com.tannerlittle.uno.Main;
import com.tannerlittle.uno.view.BackgroundPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MenuFrame extends JFrame {

    private Main main;

    private Image background;

    public MenuFrame(Main main) {
        this.main = main;

        // Initialize background panel
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResource("/images/gaming-pattern.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.background = image;

        this.setTitle("Uno");
        this.setPreferredSize(new Dimension(250, 250));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(new PlayerPanel(main, this));

        this.pack();
        this.setVisible(true);
    }

    public Image getBackgroundImage() {
        return background;
    }
}
