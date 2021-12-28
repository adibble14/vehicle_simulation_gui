/*
 * TCSS 305 - Road Rage
 */

package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import logic.PropertyChangeEnabledRoadRageControls;
import view.util.FileLoader;

/**
 * The graphical user interface for the Road Rage program.
 * 
 * @author Marty Stepp
 * @author Daniel M. Zimmerman
 * @author Alan Fowler (acfowler@u.washington.edu)
 * @author Charles Bryan  (Decomposed into separate classes)
 * @version Autumn 2018
 */

public class RoadRageGUI extends JFrame implements ActionListener {
    
    // static final fields (class constants)

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 0;

    /**
     * The window title.
     */
    private static final String TITLE = "Road Rage";
    
    // constants to capture screen dimensions
    /** A ToolKit. */
    private static final Toolkit KIT = Toolkit.getDefaultToolkit();
    
    /** The Dimension of the screen. */
    private static final Dimension SCREEN_SIZE = KIT.getScreenSize();

    /**
     * The Start command.
     */
    private static final String START_COMMAND = "Start";

    /**
     * The Stop command.
     */
    private static final String STOP_COMMAND = "Stop";

    /**
     * The Step command.
     */
    private static final String STEP_COMMAND = "Step";

    /**
     * The Reset command.
     */
    private static final String RESET_COMMAND = "Reset";

    /**
     * The initial frames per second at which the simulation will run.
     */
    private static final int INITIAL_FRAMES_PER_SECOND = 10;

    /**
     * The maximum frames per second at which the simulation will run.
     */
    private static final int MAX_FRAMES_PER_SECOND = 60;

    /**
     * The numerator for delay calculations.
     */
    private static final int MY_DELAY_NUMERATOR = 1000;

    /**
     * The minor tick spacing for the FPS slider.
     */
    private static final int MINOR_TICK_SPACING = 1;

    /**
     * The major tick spacing for the FPS slider.
     */
    private static final int MAJOR_TICK_SPACING = 10;


    // Instance Fields
    
    /**
     * The delay between updates, based on the frames per second setting.
     */
    private int myDelay;

    /**
     * A timer used to update the state of the simulation.
     */
    private final Timer myTimer;

    /**
     * The slider for "frames per second".
     */
    private JSlider mySlider;
    
    /**
     * The logic for the simulation. 
     */
    private final PropertyChangeEnabledRoadRageControls myRoadRage;
    
    // Constructor

    /**
     * Constructs a new RoadRageGUI, using the files in the current working
     * directory.
     */
    public RoadRageGUI() {
        super(TITLE);
        // initialize instance fields
        
        myDelay = MY_DELAY_NUMERATOR / INITIAL_FRAMES_PER_SECOND;
        myTimer = new Timer(myDelay, this);        

        myRoadRage = FileLoader.readCity(this);
        
        initGUI();
        
        myRoadRage.start();
        setVisible(true);
    }
    
    // Instance Methods
    
    /**
     * Sets up the GUI.
     */
    private void initGUI() {
        
        // set up graphical components
        
        final RoadRagePanel panel = 
                        new RoadRagePanel(myRoadRage.getWidth(), myRoadRage.getHeight()); 
        myRoadRage.addPropertyChangeListener(panel);
  
        mySlider = new JSlider(SwingConstants.HORIZONTAL, 0, MAX_FRAMES_PER_SECOND,
                               INITIAL_FRAMES_PER_SECOND);
        mySlider.setMajorTickSpacing(MAJOR_TICK_SPACING);
        mySlider.setMinorTickSpacing(MINOR_TICK_SPACING);
        mySlider.setPaintLabels(true);
        mySlider.setPaintTicks(true);
        mySlider.addChangeListener(new ChangeListener() {
            /** Called in response to slider events in this window. */
            @Override
            public void stateChanged(final ChangeEvent theEvent) {
                final int value = mySlider.getValue();
                if (value > 0) {
                    myDelay = MY_DELAY_NUMERATOR / value;
                    myTimer.setDelay(myDelay);
                }
            }
        });

        final JCheckBox box = new JCheckBox("Debug Mode");
        box.addChangeListener(panel);

        // layout
        final Container northPanel = new JPanel(new FlowLayout());
        northPanel.add(makeButton(START_COMMAND));
        northPanel.add(makeButton(STOP_COMMAND));
        northPanel.add(makeButton(STEP_COMMAND));
        northPanel.add(makeButton(RESET_COMMAND));

        final Container southPanel = new JPanel(new FlowLayout());
        southPanel.add(new JLabel("FPS: "));
        southPanel.add(mySlider);
        southPanel.add(box);

        final Container masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(panel, BorderLayout.CENTER);
        masterPanel.add(northPanel, BorderLayout.NORTH);
        masterPanel.add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(masterPanel);
        pack();
        // position the frame in the center of the screen
        setLocation(SCREEN_SIZE.width / 2 - getWidth() / 2,
                    SCREEN_SIZE.height / 2 - getHeight() / 2);
    }
    
    /**
     * Returns a new JButton with the specified text.
     * 
     * @param theText The text.
     * @return a new JButton.
     */
    private JButton makeButton(final String theText) {
        final JButton button = new JButton(theText);
        button.addActionListener(this);
        return button;
    }

    /**
     * A notification method called in response to action events in this window.
     * 
     * @param theEvent The action event that triggered the call.
     */
    @Override
    public void actionPerformed(final ActionEvent theEvent) {
        final Object source = theEvent.getSource();
        if (source.equals(myTimer)) {
            // event came from the timer
            myRoadRage.advance();
        } else {
            // event came from one of the buttons
            final String command = theEvent.getActionCommand().intern();
            if (command.equals(START_COMMAND)) {
                myTimer.start();
            } else if (command.equals(STOP_COMMAND)) {
                myTimer.stop();
            } else if (command.equals(STEP_COMMAND)) {
                myRoadRage.advance();
            } else if (command.equals(RESET_COMMAND)) {
                reset();
            }
        }
    }

    /**
     * Resets all the vehicles to their initial locations, resets the tick
     * counter, and stops the simulation.
     */
    private void reset() {
        myTimer.stop();
        myRoadRage.reset();
    }
        
 // end class RoadRageGUI
}