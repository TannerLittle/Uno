package com.tannerlittle.uno.view.menu;

import com.tannerlittle.uno.Main;

import javax.swing.*;
import java.awt.*;

public class MenuFrame extends JFrame {

    private Main main;

    public MenuFrame(Main main) {
        this.main = main;

        this.setTitle("Uno");
        this.setPreferredSize(new Dimension(250, 250));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(new PlayerPanel(main, this));

        this.pack();
        this.setVisible(true);
    }
}
