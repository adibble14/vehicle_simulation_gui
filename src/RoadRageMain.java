/*
 * TCSS 305 - Road Rage
 */

//package view;

import view.RoadRageGUI;

import java.awt.*;

/**
 * Runs the Road Rage program.
 * 
 * @author Marty Stepp
 * @author Daniel M. Zimmerman
 * @author Alan Fowler (acfowler@u.washington.edu)
 * @version 1.1
 */

public final class RoadRageMain {
    
    /**
     * Private constructor to prevent construction of instances.
     */
    private RoadRageMain() {
        // do nothing
    }

    /**
     * Constructs the main GUI window frame.
     * 
     * @param theArgs Command line arguments (ignored).
     */
    public static void main(final String... theArgs) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RoadRageGUI();
            }
        });
    }
}
