package com.adamegyed.bouncyball.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Adam on 3/2/16.
 * Window for displaying the helptext
 * Needed its own KeyListener to be able to hide itself
 */
public class HelpWindow extends JFrame {


    JTextArea helpText;

    JScrollPane scrollPane;

    JPanel helpPanel;

    public HelpWindow() {

        super("BouncyBall Help");
        init();
        this.setLocation(3,3);
        this.setResizable(false);
        this.pack();

        this.addKeyListener(new Listener());


        //this.setVisible(true); not made visible oon construction
    }

    //Create and draw all components
    private void init() {

        helpText = new JTextArea(25,55);
        helpText.setMargin(new Insets(3,3,3,3)); //Leave some room to the scrollbar won't clutter the reading area
        helpText.setEditable(false);
        helpText.setFont(new Font("Monaco", Font.PLAIN, 13));

        scrollPane = new JScrollPane(helpText,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        helpPanel = new JPanel();


        helpPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4)); //Space the text away from the border
        helpPanel.setBackground(Color.lightGray);
        helpPanel.add(scrollPane);

        this.setLayout(new BorderLayout());

        this.add(helpPanel,BorderLayout.CENTER);

        //The help text
        String help = "-----------------WELCOME TO BOUNCYBALL-----------------\n" +
                "\n" +
                "PRESS H TO HIDE OR SHOW THIS HELP DIALOG\n" +
                "\n" +
                "You are in control of two sets of bouncy balls - player\n" +
                "1 and player 2. Move the balls around and have them \n" +
                "collide!\n" +
                "\n" +
                "---Controls--------------------------------------------\n" +
                "The Key bindings are as follows:\n" +
                "\n" +
                "Player 1:\n" +
                "W/A/S/D - Move up, down, left, and right\n" +
                "C - Add a ball\n" +
                "X - Randomize ball colors\n" +
                "Z - Delete all balls\n" +
                "\n" +
                "Player 2:\n" +
                "I/J/K/L - Move up, down, left, and right\n" +
                "F - Add a ball\n" +
                "R - Randomize ball colors\n" +
                "V - Delete all balls\n" +
                "\n" +
                "General:\n" +
                "G - Toggle ai for player 2\n" +
                "H - Toggle this help menu\n" +
                "\n" +
                "-----------------Thanks, and have fun!-----------------";

        helpText.setText(help);
        helpText.setFocusable(false);



    }



    private class Listener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode()==KeyEvent.VK_H) {
                setVisible(false);
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }



}
