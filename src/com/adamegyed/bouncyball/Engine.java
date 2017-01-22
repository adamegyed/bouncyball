package com.adamegyed.bouncyball;

import com.adamegyed.bouncyball.component.MovingComponent;
import com.adamegyed.bouncyball.window.HelpWindow;
import com.adamegyed.bouncyball.window.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by Adam on 2/22/16.
 * Main Engine for the BouncyBall Program
 * Will start the program after construction
 */
public class Engine implements ActionListener{

    // Main Window for Displaying balls
    MainWindow mainWindow;

    // Help window for explaining controls
    HelpWindow helpWindow;

    // List holding all ablls
    LinkedList<MovingComponent> movingList;

    //Timer for updating the screen 4o times a second
    Timer refreshTimer;

    //TImer that update 5 times a second and has a chance to change some of the ai's movements
    Timer npcUpdateTimer;

    // Size of frame to display
    private int frameSize = 600;

    // Stepper int used for counting ticks on the npc update timer
    int npcTimerTick = 0;

    // Random Number Generator
    private Random randGen;

    // Efficiency of collisions against the wall, ball-to-ball collisions are perfectly elastic
    private double bounceEfficiency = 0.75;


    double ballRadius;
    double ballDiameter;

    // Boolean for whether npc ai logic is enabled
    boolean npcEnabled = true;





    public Engine() {

        // Construct windows
        mainWindow = new MainWindow(this, frameSize);
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        helpWindow = new HelpWindow();
        helpWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        helpWindow.setLocationRelativeTo(null);
        helpWindow.setVisible(true);

        // Construct linkedList
        movingList = new LinkedList<MovingComponent>();


        // Construct Timers
        refreshTimer = new Timer(25,this);
        npcUpdateTimer = new Timer(200,this);

        // Construct Random Generator
        randGen = new Random();


        // Adds 3 player controlled balls at the start of the program

        MovingComponent movingComponent;

        for (int i = 0; i < 3; i++) {
            movingComponent = new MovingComponent(mainWindow.getFrameSize(),randGen.nextInt(mainWindow.getFrameSize()),
                randGen.nextInt(mainWindow.getFrameSize()));
            movingComponent.setPlayerControlled(true);
            addMoving(movingComponent);
        }



        // begin timers
        refreshTimer.start();
        npcUpdateTimer.start();

        // Set the ballradius and diameter variables to the same value as what each ball is initialized to
        ballRadius = frameSize / 30;
        ballDiameter = 2 * ballRadius;


        // Construct and add the KeyListener
        MovementListener mL = new MovementListener();
        mainWindow.addKeyListener(mL);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Responds to the two timers and performs the respective methods for each

        if (e.getSource()==refreshTimer) {
            refresh();
        }
        else if (e.getSource()==npcUpdateTimer) {
            if (npcEnabled) updateNPC();
        }

    }



    private void refresh() {
        for (MovingComponent mC : movingList) {
            mC.moveTick(); // Performs stepping of location based on velocity
            mC.repaint(); // Repaint the individual ball components

            // Defines bounds for where each ball should bounce

            int lowerXBound = mC.getHalfxSize();
            int upperXBound = mainWindow.getFrameSize() - mC.getHalfxSize();
            int lowerYBound = mC.getHalfySize();
            int upperYBound = mainWindow.getFrameSize() - mC.getHalfySize();


            // if past the bounce border, move them back on the border and change their velocity accordingly
            if (mC.getxPos() < lowerXBound) {
                mC.setDx(-mC.getDx() * bounceEfficiency);
                mC.setxPos(lowerXBound);
            }
            else if (mC.getxPos() > upperXBound) {
                mC.setDx(-mC.getDx()* bounceEfficiency);
                mC.setxPos(upperXBound);
            }
            if (mC.getyPos() < lowerYBound) {

                mC.setDy(-mC.getDy() * bounceEfficiency);
                mC.setyPos(lowerYBound);

            }
            else if (mC.getyPos() > upperYBound) {
                mC.setDy(-mC.getDy() * bounceEfficiency);
                mC.setyPos(upperYBound);
            }



        }

        //Check for any collisions between balls
        checkForCollisions();
    }


    // Adds a ball to the window and list
    public void addMoving(MovingComponent mC) {

        mainWindow.add(mC,0);
        movingList.add(mC);
        mC.setBounds(0,0,mainWindow.getFrameSize(),mainWindow.getFrameSize());

    }


    // Start acclelration on player controlled balls
    private void startMovePlayerControlled(int direction) {

        for (MovingComponent mC : movingList) {
            if (mC.isPlayerControlled()) {
                mC.startAccelerating(direction);
            }
        }



    }

    // Stop given acceleration on player controlled balls
    private void stopMovePlayerControlled(int direction) {

        for (MovingComponent mC : movingList) {
            if (mC.isPlayerControlled()) {
                mC.stopAccelerating(direction);
            }
        }



    }

    // Start acceleration on player 2 controlled balls
    private void startMovePlayer2Controlled(int direction) {

        for (MovingComponent mC : movingList) {
            if (!mC.isPlayerControlled()) {
                mC.startAccelerating(direction);
            }
        }



    }

    // Stop acceleration on player 2 controlled balls
    private void stopMovePlayer2Controlled(int direction) {

        for (MovingComponent mC : movingList) {
            if (!mC.isPlayerControlled()) {
                mC.stopAccelerating(direction);
            }
        }



    }


    // Run once each time the npc timer fires
    private void updateNPC() {

        // Generalized for loop
        for (MovingComponent mC : movingList) {

            if (!mC.isPlayerControlled() && (npcTimerTick==mC.getNpcUpdateTrigger())) {

                mC.randomizeAcceleration(); // Random direction for it to head in
                mC.randomizeNpcTrigger(); // Randomize the time needed to change acceleration



            }

        }

        // Increment the ticker
        npcTimerTick++;

        // Reset the ticker should it get too high
        if (npcTimerTick==6) npcTimerTick = 0;

    }

    // Checks for any ball collisions in the movingList
    private void checkForCollisions() {


        // Creates the first iterator - goes over ever ball in the abll list
        Iterator<MovingComponent> movingIterator = movingList.iterator();

        // Declare a second iterator
        Iterator<MovingComponent> movingIterator2;

        // Number of balls already checked by iterator 1
        int numBallsAlreadyChecked = 0;

        // First Ball
        MovingComponent mC1;

        // ooping through the first iterator
        while (movingIterator.hasNext()) {

            mC1 = movingIterator.next(); // Get the next ball

            // Reconstruct the second iterator, starting at the number of balls already checked plus one, so no ball will collide with itself
            // This will effectively pace through the first few items in the iterator until it reaches the parameter
            // This is redone with each ball in the first iterator
            movingIterator2 = movingList.listIterator(numBallsAlreadyChecked+1);

            // Declare second ball
            MovingComponent mC2;


            // Loop through the second iterator
            while (movingIterator2.hasNext()) {

                // Get the second ball
                mC2 = movingIterator2.next();


                //Creates a bounding box - don't bother checking for collisions unless already inside the box
                if (Math.abs(mC2.getxPos()-mC1.getxPos()) < ballDiameter + 1) {

                    if (Math.abs(mC2.getyPos()-mC1.getyPos()) < ballDiameter + 1) {

                        // Calculates distance between two balls

                        double distance = distanceBetween(mC1.getxPos(),mC1.getyPos(),mC2.getxPos(),mC2.getyPos());

                        // If they are within one diameter of each other's center
                        if (distance < ballDiameter) {

                            // Collide the balls
                            collideBalls(mC1, mC2, distance);

                        }

                    }

                }


            }





            // Increment the number of balls already checked for the next iteration
            numBallsAlreadyChecked++;

        }

    }

    // Calculates distance between two coordinates
    private double distanceBetween(double x1, double y1, double x2, double y2) {

        //Math.pow is not used because on very small powers, it is vastly inefficient

        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));


    }

    // Collides two balls given in the parameters
    private void collideBalls(MovingComponent mC1, MovingComponent mC2, double distance) {

        // Super duper complicated formula I found
        // the velocity is not changed immediately because it would effect the outcome of the consecutive calculations
        double vx1 =  mC1.getDx() -
                (mC1.getDx() - mC2.getDx())
                        * (mC2.getxPos() - mC1.getxPos()) * Math.signum(mC1.getDx()-mC2.getDx())/ (ballDiameter);

        double vy1 =  mC1.getDy() - (mC1.getDy() - mC2.getDy()) * (mC2.getyPos() - mC1.getyPos()) * Math.signum(mC1.getDy()-mC2.getDy())/ (ballDiameter);

        // Net change in velocity - used to calculate velocity of second ball much more quickly
        double delta_vx_1 = vx1 - mC1.getDx();
        double delta_vy_1 = vy1 - mC1.getDy();


        //Corrections on circle's locations if they are already phazed within each other
        double step = (ballDiameter - distance) / 2;

        double scale = (distance + step) / distance;

        double deltaX = mC2.getxPos() - mC1.getxPos();
        double deltaY = mC2.getyPos() - mC1.getyPos();

        double newX2 = mC1.getxPos() + scale * deltaX;
        double newY2 = mC1.getyPos() + scale * deltaY;

        double newX1 = mC2.getxPos() - scale * deltaX;
        double newY1 = mC2.getyPos() - scale * deltaY;

        mC1.setxPos(newX1);
        mC1.setyPos(newY1);

        mC2.setxPos(newX2);
        mC2.setyPos(newY2);


        // Set the new velocities of the second ball to the current velocity minus the net change in the first ball
        // You can do this because the balls have equal radius and mass
        double vx2 = mC2.getDx() - delta_vx_1;
        double vy2 = mC2.getDy() - delta_vy_1;

        /*
        System.out.println("Colliding " + mC1.getDx() + " " + mC1.getDy() + " " + mC2.getDx() + " " + mC2.getDy() + " dvs:"
        + delta_vx_1 + " " + delta_vy_1 + " dxs: " + (mC2.getxPos()-mC1.getxPos()) + " " + (mC2.getyPos()-mC1.getyPos())
        + " d="+ballDiameter);
        */

        //refreshTimer.stop();



        // Actually make the adjustments on velocity
        mC1.setDx(vx1);

        mC1.setDy(vy1);

        mC2.setDx(vx2);

        mC2.setDy(vy2);



    }


    public void toggleHelp() {

        //Makes the help window toggle its visibility

        helpWindow.setVisible(!helpWindow.isVisible());

    }

    // Keylistener used in the program
    private class MovementListener implements KeyListener
    {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

            // Movement for player 1
            if (e.getKeyCode()==KeyEvent.VK_W) {

                startMovePlayerControlled(MovingComponent.UP);

            }
            else if (e.getKeyCode()==KeyEvent.VK_S) {

                startMovePlayerControlled(MovingComponent.DOWN);

            }
            else if (e.getKeyCode()==KeyEvent.VK_D) {

                startMovePlayerControlled(MovingComponent.RIGHT);

            }
            else if (e.getKeyCode()==KeyEvent.VK_A) {

                startMovePlayerControlled(MovingComponent.LEFT);

            }
            // Randomize color player 1
            else if (e.getKeyCode()==KeyEvent.VK_X) {
                for (MovingComponent mC : movingList) {
                    if (mC.isPlayerControlled()) mC.newRandomColor();
                }
            }
            // Delete all balls player 1
            else if (e.getKeyCode()==KeyEvent.VK_Z) {

                ListIterator<MovingComponent> lE= movingList.listIterator();
                while(lE.hasNext()) {
                    MovingComponent movingComponent = lE.next();
                    if (movingComponent.isPlayerControlled()) {
                        lE.remove();
                        mainWindow.remove(movingComponent);
                        movingComponent.repaint();
                        mainWindow.repaintBG();
                    }
                }

            }
            // Create new ball player 1
            else if (e.getKeyCode()==KeyEvent.VK_C) {

                MovingComponent movingComponent = new MovingComponent(mainWindow.getFrameSize(),randGen.nextInt(mainWindow.getFrameSize()),randGen.nextInt(mainWindow.getFrameSize()));
                movingComponent.setPlayerControlled(true);
                addMoving(movingComponent);
            }
            // create new ball player 2
            else if (e.getKeyCode()==KeyEvent.VK_F) {
                MovingComponent movingComponent = new MovingComponent(mainWindow.getFrameSize(),randGen.nextInt(mainWindow.getFrameSize()),randGen.nextInt(mainWindow.getFrameSize()));
                movingComponent.setPlayerControlled(false);
                addMoving(movingComponent);
            }
            // Randomize color player 2
            else if (e.getKeyCode()==KeyEvent.VK_R) {
                for (MovingComponent mC : movingList) {
                    if (!mC.isPlayerControlled()) mC.newRandomColor();
                }
            }
            // Delete all balls player 2
            else if (e.getKeyCode()==KeyEvent.VK_V) {

                ListIterator<MovingComponent> lE= movingList.listIterator();
                while(lE.hasNext()) {
                    MovingComponent movingComponent = lE.next();
                    if (!movingComponent.isPlayerControlled()) {
                        lE.remove();
                        mainWindow.remove(movingComponent);
                        movingComponent.repaint();
                        mainWindow.repaintBG();
                    }
                }

            }

            // Movement player 2
            if (e.getKeyCode()==KeyEvent.VK_I) {

                startMovePlayer2Controlled(MovingComponent.UP);

            }
            else if (e.getKeyCode()==KeyEvent.VK_K) {

                startMovePlayer2Controlled(MovingComponent.DOWN);

            }
            else if (e.getKeyCode()==KeyEvent.VK_L) {

                startMovePlayer2Controlled(MovingComponent.RIGHT);

            }
            else if (e.getKeyCode()==KeyEvent.VK_J) {

                startMovePlayer2Controlled(MovingComponent.LEFT);

            }
            else if (e.getKeyCode()==KeyEvent.VK_B) {

                refreshTimer.restart();

            }
            // Toggle ai for player 2
            else if (e.getKeyCode()==KeyEvent.VK_G) {

                npcEnabled = !npcEnabled;
                for (MovingComponent mC : movingList) {
                    if (!mC.isPlayerControlled()) mC.stopAllAcceleration();
                }

            }
            // Toggle help display
            else if (e.getKeyCode()==KeyEvent.VK_H) {
                toggleHelp();
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {

            // Ends the acceleration on each key release for the respective balls

            if (e.getKeyCode()==KeyEvent.VK_W) {

                stopMovePlayerControlled(MovingComponent.UP);

            }
            else if (e.getKeyCode()==KeyEvent.VK_S) {

                stopMovePlayerControlled(MovingComponent.DOWN);

            }
            else if (e.getKeyCode()==KeyEvent.VK_D) {

                stopMovePlayerControlled(MovingComponent.RIGHT);

            }
            else if (e.getKeyCode()==KeyEvent.VK_A) {

                stopMovePlayerControlled(MovingComponent.LEFT);

            }
            else if (e.getKeyCode()==KeyEvent.VK_I) {

                stopMovePlayer2Controlled(MovingComponent.UP);

            }
            else if (e.getKeyCode()==KeyEvent.VK_K) {

                stopMovePlayer2Controlled(MovingComponent.DOWN);

            }
            else if (e.getKeyCode()==KeyEvent.VK_L) {

                stopMovePlayer2Controlled(MovingComponent.RIGHT);

            }
            else if (e.getKeyCode()==KeyEvent.VK_J) {

                stopMovePlayer2Controlled(MovingComponent.LEFT);

            }


        }


    }




}
