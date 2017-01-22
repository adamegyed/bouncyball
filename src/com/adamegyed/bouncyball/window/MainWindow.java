package com.adamegyed.bouncyball.window;

import com.adamegyed.bouncyball.Engine;
import com.adamegyed.bouncyball.component.BackgroundComponent;
import com.adamegyed.bouncyball.component.MovingComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

/**
 * Created by Adam on 2/22/16.
 * Main Window for displaying ball positions and movement
 * Very minimalistic
 */
public class MainWindow extends JFrame {

    Engine engine;


    JLayeredPane viewLayerPane;

    BackgroundComponent bG;

    JMenuBar menuBar;

    private int frameSize;

    JPanel editPanel;

    JLabel frictionLabel;
    JTextPane editFriction;
    JButton updateFriction;


    public MainWindow(Engine e, int frameSize) {


        super("Bouncy Ball");

        this.engine = e;

        this.frameSize = frameSize;

        initWindow();



        this.pack();
        this.pack();
        this.setVisible(true);

    }

    private void initWindow() {

        viewLayerPane = new JLayeredPane();
        viewLayerPane.setPreferredSize(new Dimension(frameSize,frameSize));
        viewLayerPane.setMinimumSize(new Dimension(frameSize,frameSize));

        bG = new BackgroundComponent(frameSize);
        bG.setBounds(0,0,frameSize,frameSize);
        viewLayerPane.add(bG,0);


        // Unfinished work on dialog for editting friction and bounce efficiency
        editPanel = new JPanel(new GridLayout(1,3));

        frictionLabel = new JLabel("Friction: ");
        editFriction = new JTextPane();
        updateFriction = new JButton("Update Friction");

        editPanel.add(frictionLabel);
        editPanel.add(editFriction);
        editPanel.add(updateFriction);

        //Adding Menubar

        menuBar = new JMenuBar();

        JMenu help = new JMenu("Help");

        JMenuItem showHelp = new JMenuItem("Show Help");

        help.add(showHelp);

        menuBar.add(help);

        //this.setJMenuBar(menuBar);





        this.setLayout(new BorderLayout(0,0));
        this.add(viewLayerPane,BorderLayout.CENTER);
        //this.add(editPanel,BorderLayout.SOUTH);



    }


    public int getFrameSize() {
        return frameSize;
    }

    public void repaintBG() {
        bG.repaint();
    }



}
