/*
 * TCSS 305 - Road Rage
 */

package view;

import static logic.PropertyChangeEnabledRoadRageControls.PROPERTY_GRID;
import static logic.PropertyChangeEnabledRoadRageControls.PROPERTY_LIGHT;
import static logic.PropertyChangeEnabledRoadRageControls.PROPERTY_VEHICLES;
import static logic.PropertyChangeEnabledRoadRageControls.PROPERTY_TIME;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import model.Direction;
import model.Light;
import model.Terrain;
import model.Vehicle;

/**
 * A drawing panel for the map.
 * 
 * @version 05 OCT 2018
 * @author Marty Stepp
 * @author Daniel M. Zimmerman
 * @author Alan Fowler (acfowler@u.washington.edu)
 * @author Charles Bryan
 * 
 */
public class RoadRagePanel extends JPanel implements PropertyChangeListener, ChangeListener {

    /**
     * The UID of this class (to avoid warnings).
     */
    private static final long serialVersionUID = 4269666L;

    /**
     * The font used by this panel.
     */
    private static final Font FONT = new Font("SansSerif", Font.BOLD, 9);
    
    /**
     * The stroke used for painting.
     */
    private static final BasicStroke STROKE = new BasicStroke(3, BasicStroke.CAP_BUTT,
                                                              BasicStroke.JOIN_MITER, 2,
                                                              new float[] {2, 2, 2, 2}, 0);

    /**
     * The offset to use to position cross walk lines and to use for cross walk light size.
     */
    private static final double CROSSWALK_SCALE = .25;
    
    /**
     * The size in pixels of the directional markers drawn on vehicles in debug
     * mode.
     */
    private static final int MARKER_SIZE = 10;
    
    /**
     * The offset in pixels of the debug messages drawn for each square.
     */
    private static final int DEBUG_OFFSET = 10;
    
    /**
     * The size in pixels of a side of one "square" on the grid.
     */
    private static final int SQUARE_SIZE = 40;
    

    // Instance Fields
    
    /**
     * The terrain grid for the simulation.
     */
    private Terrain[][] myGrid;
    
    /**
     * A flag indicating whether or not we are running in debug mode.
     */
    private boolean myDebugFlag;

    /**
     * The current timestep of the simulation.
     */
    private long myTimestep;
    
    /**
     * The current color of lights.
     */
    private Color myLightColor;
        
    /**
     * The vehicles to move and display. 
     */
    private List<Vehicle> myVehicles;

    // Constructor

    /**
     * Construct a new Panel.
     * @param theWidth width of the 2D grid of Terrain that defines the map
     * @param theHeight height of the 2D grid of Terrain that defines the map
     */
    public RoadRagePanel(final int theWidth, final int theHeight) {
        super();

        myVehicles = new ArrayList<Vehicle>();
        myGrid = new Terrain[0][0];
        setLightColor(Light.GREEN);
        setPreferredSize(new Dimension(theWidth * SQUARE_SIZE,
                                       theHeight * SQUARE_SIZE));
        setBackground(Color.GREEN);
        setFont(FONT);
    }

    // Instance Methods

    /**
     * Paints this panel on the screen with the specified Graphics object.
     * 
     * @param theGraphics The Graphics object.
     */
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2 = (Graphics2D) theGraphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(STROKE);

        // draw city map

        drawMap(g2);

        // draw vehicles
        for (final Vehicle v : myVehicles) {
            final String imageFilename = "icons//" + v.getImageFileName();
            //final String imageFilename = v.getImageFileName();
            ImageIcon imgIcon = new ImageIcon(imageFilename);

            if (imgIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                imgIcon = new ImageIcon(getClass().getResource(imageFilename));
            }

            final Image img = imgIcon.getImage();
            g2.drawImage(img, v.getX() * SQUARE_SIZE, v.getY() * SQUARE_SIZE,
                         SQUARE_SIZE, SQUARE_SIZE, this);

            if (myDebugFlag) {
                drawDebugInfo(g2, v);
            }
        }

        if (myDebugFlag) {
            g2.setColor(Color.WHITE);
            g2.drawString("Update # " + myTimestep, DEBUG_OFFSET / 2,
                          FONT.getSize() + DEBUG_OFFSET / 2);
        }
    }
    

    /**
     * Draws the city map with the specified Graphics2D object.
     * 
     * @param theGraphics The Graphics2D object.
     */
    private void drawMap(final Graphics2D theGraphics) {
        for (int y = 0; y < myGrid.length; y++) {
            final int topy = y * SQUARE_SIZE;

            for (int x = 0; x < myGrid[y].length; x++) {
                final int leftx = x * SQUARE_SIZE;

                switch (myGrid[y][x]) {
                    case STREET:
                        theGraphics.setPaint(Color.LIGHT_GRAY);
                        theGraphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        drawStreetLines(theGraphics, x, y);
                        break;

                    case WALL:
                        theGraphics.setPaint(Color.BLACK);
                        theGraphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        break;

                    case TRAIL:
                        theGraphics.setPaint(Color.YELLOW.darker().darker());
                        theGraphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        break;

                    case LIGHT:
                        // draw a circle of appropriate color
                        theGraphics.setPaint(Color.LIGHT_GRAY);
                        theGraphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        theGraphics.setPaint(myLightColor);
                        theGraphics.fillOval(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        break;
                        
                    case CROSSWALK:
                        theGraphics.setPaint(Color.LIGHT_GRAY);
                        theGraphics.fillRect(leftx, topy, SQUARE_SIZE, SQUARE_SIZE);
                        
                        drawCrossWalkLines(theGraphics, x, y);
                        
                        // draw a small circle of appropriate color centered in the square
                        theGraphics.setPaint(myLightColor);
                        theGraphics.fillOval(leftx + (int)  (SQUARE_SIZE * CROSSWALK_SCALE),
                                             topy  + (int) (SQUARE_SIZE * CROSSWALK_SCALE),
                                             SQUARE_SIZE / 2, SQUARE_SIZE / 2);
                        break;

                    default:
                }

                drawDebugInfo(theGraphics, x, y);
            }
        }
    }
    
    /**
     * Draws debugging information, if necessary.
     * 
     * @param theGraphics The Graphics context to use for drawing.
     * @param theX The x-coordinate of the street.
     * @param theY The y-coordinate of the street.
     */
    private void drawDebugInfo(final Graphics2D theGraphics, final int theX, final int theY) {

        if (myDebugFlag) {
            // draw numbers for the row and column
            final Paint oldPaint = theGraphics.getPaint();
            theGraphics.setPaint(Color.BLACK);

            final int leftx = theX * SQUARE_SIZE;
            final int topy = theY * SQUARE_SIZE;
            theGraphics.drawString("(" + theX + ", " + theY + ")", leftx, topy + DEBUG_OFFSET);
            theGraphics.setPaint(oldPaint);
        }
    }
    
    /**
     * Draws the debug information for a single Vehicle.
     * 
     * @param theGraphics The graphic context.
     * @param theVehicle The Vehicle being drawn.
     */
    private void drawDebugInfo(final Graphics2D theGraphics, final Vehicle theVehicle) {
        int x = theVehicle.getX() * SQUARE_SIZE;
        int y = theVehicle.getY() * SQUARE_SIZE;

        // draw numbers on each vehicle
        theGraphics.setColor(Color.WHITE);
        theGraphics.drawString(theVehicle.toString(), x, y + SQUARE_SIZE - 1);
        theGraphics.setColor(Color.BLACK);
        theGraphics.drawString(theVehicle.toString(), x + 1, y + SQUARE_SIZE);

        // draw arrow on vehicle for its direction
        final Direction dir = theVehicle.getDirection();
        int dx = (SQUARE_SIZE - MARKER_SIZE) / 2;
        int dy = dx;

        switch (dir) {
            case WEST:
                dx = 0;
                break;

            case EAST:
                dx = SQUARE_SIZE - MARKER_SIZE;
                break;

            case NORTH:
                dy = 0;
                break;

            case SOUTH:
                dy = SQUARE_SIZE - MARKER_SIZE;
                break;

            default:
        }

        x = x + dx;
        y = y + dy;

        theGraphics.setColor(Color.YELLOW);
        theGraphics.fillOval(x, y, MARKER_SIZE, MARKER_SIZE);
    }

    /**
     * Sets the paint color appropriately for the current lights.
     * 
     * @param theLight The Light to base the color on.
     */
    private void setLightColor(final Light theLight) {
        switch (theLight) {
            case GREEN:
                myLightColor = Color.GREEN.darker();
                break;

            case RED:
                myLightColor = Color.RED.darker();
                break;

            case YELLOW:
                myLightColor = Color.YELLOW;
                break;

            default:
        }
    }
    
    /**
     * Draws dotted lines on cross walks.
     * 
     * @param theGraphics The Graphics context to use for drawing.
     * @param theX The x-coordinate of the cross walk.
     * @param theY The y-coordinate of the cross walk.
     */
    private void drawCrossWalkLines(final Graphics2D theGraphics,
                                 final int theX, final int theY) {
        
        final Paint oldPaint = theGraphics.getPaint();
        theGraphics.setPaint(Color.WHITE);

        final int leftx = theX * SQUARE_SIZE;
        final int topy = theY * SQUARE_SIZE;

        final int rightx = leftx + SQUARE_SIZE;
        final int bottomy = topy + SQUARE_SIZE;

        if (isValidIndex(theY - 1, theX) && myGrid[theY - 1][theX] == Terrain.GRASS) {
            // GRASS is above. Let's assume GRASS is also below.
            // DRAW lines on left and right of light
            theGraphics.drawLine(leftx  + (int) (SQUARE_SIZE * CROSSWALK_SCALE), topy,
                                 leftx  + (int) (SQUARE_SIZE * CROSSWALK_SCALE), bottomy);
            theGraphics.drawLine(rightx - (int) (SQUARE_SIZE * CROSSWALK_SCALE), topy,
                                 rightx - (int) (SQUARE_SIZE * CROSSWALK_SCALE), bottomy);
        }
        

        if (isValidIndex(theY, theX - 1) && myGrid[theY][theX - 1] == Terrain.GRASS) {
            // GRASS is left. Let's assume GRASS is also right.
            // DRAW lines above and below light
            theGraphics.drawLine(leftx, topy + (int) (SQUARE_SIZE * CROSSWALK_SCALE),
                                 rightx, topy + (int) (SQUARE_SIZE * CROSSWALK_SCALE));
            theGraphics.drawLine(leftx, bottomy - (int) (SQUARE_SIZE * CROSSWALK_SCALE),
                                 rightx, bottomy - (int) (SQUARE_SIZE * CROSSWALK_SCALE));
        }

        theGraphics.setPaint(oldPaint);
    }
    
    /**
     * Draws dotted lines on streets.
     * 
     * @param theGraphics The Graphics context to use for drawing.
     * @param theX The x-coordinate of the street.
     * @param theY The y-coordinate of the street.
     */
    private void drawStreetLines(final Graphics2D theGraphics,
                                 final int theX, final int theY) {
        
        final Paint oldPaint = theGraphics.getPaint();
        theGraphics.setPaint(Color.YELLOW);

        final int leftx = theX * SQUARE_SIZE;
        final int topy = theY * SQUARE_SIZE;
        final int centerx = leftx + SQUARE_SIZE / 2;
        final int centery = topy + SQUARE_SIZE / 2;
        final int rightx = leftx + SQUARE_SIZE;
        final int bottomy = topy + SQUARE_SIZE;

        if (isValidIndex(theY - 1, theX) && myGrid[theY - 1][theX] == Terrain.STREET) {
            theGraphics.drawLine(centerx, centery, centerx, topy); // above
        }
        if (isValidIndex(theY + 1, theX) && myGrid[theY + 1][theX] == Terrain.STREET) {
            theGraphics.drawLine(centerx, centery, centerx, bottomy); // below
        }
        if (isValidIndex(theY, theX - 1) && myGrid[theY][theX - 1] == Terrain.STREET) {
            theGraphics.drawLine(centerx, centery, leftx, centery); // left
        }
        if (isValidIndex(theY, theX + 1) && myGrid[theY][theX + 1] == Terrain.STREET) {
            theGraphics.drawLine(centerx, centery, rightx, centery); // right
        }

        theGraphics.setPaint(oldPaint);
    }
    
    /**
     * Tests whether the square at the given x/y position exists on the map.
     * 
     * @param theX The x position.
     * @param theY The y position.
     * @return true if the position exists on the map, false otherwise.
     */
    private boolean isValidIndex(final int theY, final int theX) {
        return 0 <= theY && theY < myGrid.length
            && 0 <= theX && theX < myGrid[theY].length;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {
        switch (theEvent.getPropertyName()) {
            case PROPERTY_GRID:
                myGrid = (Terrain[][]) theEvent.getNewValue();
                repaint();
                break;
            case PROPERTY_LIGHT:
                setLightColor((Light) theEvent.getNewValue());
                repaint();
                break;
            case PROPERTY_VEHICLES:
                @SuppressWarnings("unchecked") 
                final List<Vehicle> list = (List<Vehicle>) theEvent.getNewValue();
                myVehicles = new ArrayList<>(list);
                repaint();
                break;
            case PROPERTY_TIME:
                myTimestep = (Long) theEvent.getNewValue();
                repaint();
                break;
            default:
                break;  
            
        }
    }

    @Override
    public void stateChanged(final ChangeEvent theEvent) {
        myDebugFlag = ((JToggleButton) theEvent.getSource()).isSelected();
        repaint();
    }
    
} // end class RoadRagePanel

