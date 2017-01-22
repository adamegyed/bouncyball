package com.adamegyed.bouncyball.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Created by Adam on 2/22/16.
 * The component for each ball
 */
public class MovingComponent extends JComponent {

    // Dimensions
    private int xSize;
    private int ySize;

    // Precalculated for better efficiency
    private int halfxSize;
    private int halfySize;

    // Position
    private double xPos;
    private double yPos;

    // Handle on size of the window
    private int frameSize;

    // Color of the ball
    private Color color;

    // RNG
    private Random randGen;

    // Velocities
    private double dx;
    private double dy;

    // Flag for whether it is player 1 controlled or not
    boolean playerControlled = false;

    // Flags for current acceleration
    boolean acceleratingUp = false;
    boolean acceleratingDown = false;
    boolean acceleratingLeft = false;
    boolean acceleratingRight = false;

    // Rate of change of speed
    private double acceleration = 1;

    // Rate of gradual decrease in speed
    private double friction = 0.1;

    private int radius;

    // Final fields for directions
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    //Starting trigger point for updating npc state
    private int npcUpdateTrigger = 4;

    public MovingComponent(int frameSize, int x, int y) {
        super();

        this.frameSize = frameSize;

        xSize = this.frameSize / 15;
        ySize = this.frameSize / 15;


        halfxSize = xSize / 2;
        halfySize = ySize / 2;

        radius = xSize / 2;

        xPos = x;
        yPos = y;

        randGen = new Random();

        color = new Color(randGen.nextInt(256),randGen.nextInt(256),randGen.nextInt(256));

        // No starting velocity
        dx = 0;
        dy = 0;

        acceleration = 0.75 + (randGen.nextDouble() / 2);

        npcUpdateTrigger = 2 + randGen.nextInt(4);


    }

    // Overriden paintComponent method
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Ellipse2D.Double body = new Ellipse2D.Double(xPos-halfxSize,yPos-halfySize,xSize,ySize);

        g2.setColor(color);

        g2.fill(body);

        g2.setStroke(new BasicStroke(4));
        g2.setColor(Color.black);

        g2.draw(body);




    }



    // Move the ball base don its speed
    public void moveTick() {

        xPos += dx;
        yPos += dy;

        if (acceleratingRight) dx += acceleration;
        if (acceleratingLeft) dx -= acceleration;
        if (acceleratingUp) dy -= acceleration;
        if (acceleratingDown) dy += acceleration;

        // Redce velocity due to friction
        if (dx < 0) {
            dx += friction;
            if (dx > 0) dx = 0;
        }
        else {
            dx -= friction;
            if (dx < 0) dx = 0;
        }
        if (dy < 0) {
            dy += friction;
            if (dy > 0) dy = 0;
        }
        else {
            dy -= friction;
            if (dy < 0) dy = 0;
        }







    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void setPlayerControlled(boolean playerControlled) {
        this.playerControlled = playerControlled;
    }

    public boolean isPlayerControlled() {
        return playerControlled;
    }


    public double getxPos() {
        return xPos;
    }

    public double getyPos() {
        return yPos;
    }

    // Randomizes color of the ball
    public void newRandomColor() {

        color = new Color(randGen.nextInt(256),randGen.nextInt(256),randGen.nextInt(256));

    }

    public int getHalfxSize() {
        return halfxSize;
    }

    public int getHalfySize() {
        return halfySize;
    }

    // Start acceleration in a dirrection
    public void startAccelerating(int direction) {

        if (direction==UP) acceleratingUp = true;
        else if (direction==DOWN) acceleratingDown = true;
        else if (direction==LEFT) acceleratingLeft = true;
        else if (direction==RIGHT) acceleratingRight = true;

    }

    // Stop acceleration in a given direction
    public void stopAccelerating(int direction) {

        if (direction==UP) acceleratingUp = false;
        else if (direction==DOWN) acceleratingDown = false;
        else if (direction==LEFT) acceleratingLeft = false;
        else if (direction==RIGHT) acceleratingRight = false;

    }

    // Stop any acceleration currently in progress
    public void stopAllAcceleration() {
        acceleratingUp = false;
        acceleratingDown = false;
        acceleratingLeft = false;
        acceleratingRight = false;
    }

    // Change the constant of acceleration
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public int getNpcUpdateTrigger() {
        return npcUpdateTrigger;
    }

    // Start accelerating in random directions
    public void randomizeAcceleration() {

        acceleratingUp = randGen.nextBoolean();
        acceleratingDown = randGen.nextBoolean();
        acceleratingLeft = randGen.nextBoolean();
        acceleratingRight = randGen.nextBoolean();

    }

    // Radnomize ticks needed to change movement
    public void randomizeNpcTrigger() {
        npcUpdateTrigger = 2 + randGen.nextInt(4);
    }

    public int getRadius() {
        return radius;
    }
}
