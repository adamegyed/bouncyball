package com.adamegyed.bouncyball.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by Adam on 2/22/16.
 *
 * Blank Canvas drawn in the background
 *
 */
public class BackgroundComponent extends JComponent {


    private int frameSize;

    public BackgroundComponent(int frameSize) {

        this.frameSize = frameSize;



    }

    public void paintComponent(Graphics g) {


        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.lightGray);
        g2.fill(new Rectangle2D.Double(0,0,frameSize,frameSize));


    }

}
