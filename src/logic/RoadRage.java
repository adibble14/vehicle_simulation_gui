package logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Direction;
import model.Light;
import model.Terrain;
import model.Vehicle;

/**
 * Implementation the behaviors of the RoadRage simulation. 
 * 
 * @author Charles Bryan
 * @version 1 OCT 2018
 */
public class RoadRage implements PropertyChangeEnabledRoadRageControls {
    
    /**
     * The number of clock ticks between light changes.
     */
    private static final int LIGHT_CHANGE_TICKS = 15;
    
    /**
     * The terrain grid for the simulation.
     */
    private final Terrain[][] myGrid;
    
    /**
     * The current timestep of the simulation.
     */
    private long myTimestep;
    
    /**
     * The current status of lights.
     */
    private Light myLight;
    
    /**
     * The vehicles to move and display. 
     */
    private final List<Vehicle> myVehicles;
    
    /**
     * Manager for Propery Change Listeners. 
     */
    private final PropertyChangeSupport myPcs;
    
    /**
     * 
     * @param theGrid the 2D grid of Terrain that defines the map
     * @param theVehicles the Vehicles on the map
     */
    public RoadRage(final Terrain[][] theGrid, final List<Vehicle> theVehicles) {
        myVehicles = new ArrayList<Vehicle>(theVehicles);
        myGrid = theGrid.clone();
        myPcs = new PropertyChangeSupport(this);            
    }
    
    @Override
    public void advance() {
        for (final Vehicle v : myVehicles) {
            final Map<Direction, Terrain> neighbors = generateNeighbors(v);

            // move the vehicle
            if (v.isAlive()) {
                final Direction newDirection = v.chooseDirection(neighbors);
                v.setDirection(newDirection);

                // move one square in current direction, if it's okay to do so
                if (v.canPass(neighbors.get(newDirection), myLight)) {
                    v.setX(v.getX() + newDirection.dx());
                    v.setY(v.getY() + newDirection.dy());
                }
            } else {
                // become one move closer to revival
                v.poke();
            }

            // look for collisions
            for (final Vehicle other : myVehicles) {
                if (v.equals(other)) { // use of == is intentional - checking for same object
                    // don't collide with self
                    continue;
                }

                if (v.getX() == other.getX() && v.getY() == other.getY()) {
                    // tell both vehicles they have collided
                    v.collide(other);
                    other.collide(v);
                }
            }
        }
        advanceTimeStep();
        if (myTimestep % LIGHT_CHANGE_TICKS == 0) {
            setLightColor(myLight.advance());
        }
        fireVehicleChange();
    }
    
    @Override
    public void start() {
        reset();
    }
    
    @Override
    public void reset() {
        resetVehicles();
        setLightColor(Light.GREEN);
        setTimeStep(0);
        fireGridChange();
        fireVehicleChange();
    }
    
    @Override
    public int getHeight() {
        return myGrid.length;
    }

    @Override
    public int getWidth() {
        return myGrid[0].length;
    }
    
 
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(theListener);
    }
    

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(theListener);
    }
    
    @Override
    public void addPropertyChangeListener(final String thePropertyName,
                                          final PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(thePropertyName, theListener);
        
    }

    @Override
    public void removePropertyChangeListener(final String thePropertyName,
                                             final PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(thePropertyName, theListener);
        
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
    
    /**
     * Generates a read-only neighbors map for the specified vehicle.
     * 
     * @param theMover The vehicle.
     * @return The neighbors map.
     */
    private Map<Direction, Terrain> generateNeighbors(final Vehicle theMover) {
        final int x = theMover.getX();
        final int y = theMover.getY();
        final Map<Direction, Terrain> result = new HashMap<Direction, Terrain>();

        for (final Direction dir : Direction.values()) {
            if (isValidIndex(y + dir.dy(), x + dir.dx())) {
                result.put(dir, myGrid[y + dir.dy()][x + dir.dx()]);
            }
        }
        return Collections.unmodifiableMap(result);
    }
    
    /**
     * Sets the paint color appropriately for the current lights.
     * 
     * @param theLight The Light to base the color on.
     */
    private void setLightColor(final Light theLight) {
        final Light old = myLight;
        myLight = theLight;
        myPcs.firePropertyChange(PROPERTY_LIGHT, old, myLight); 
    }
    
    /**
     * Sets the time step for the simulation. 
     * 
     * @param theTimestep the time to set. 
     */
    private void setTimeStep(final long theTimestep) {
        final long old = myTimestep;
        myTimestep = theTimestep;
        myPcs.firePropertyChange(PROPERTY_TIME, old, myTimestep);
    }
    
    /**
     * Advances the time step by 1 for the simulation. 
     */
    private void advanceTimeStep() {
        setTimeStep(myTimestep + 1);
    }
    
    /**
     * Inform PropertyChagneListeners of the current state of vehicles.
     */
    private void fireVehicleChange() {
        myPcs.firePropertyChange(PROPERTY_VEHICLES, null, new ArrayList<>(myVehicles));
    }
    
    /**
     * Inform PropertyChagneListeners of the current 2D Terrain grid state.
     */
    private void fireGridChange() {
        myPcs.firePropertyChange(PROPERTY_GRID, null, myGrid.clone());
    }
    
    /**
     * Reset all of the vehicles to their original state. 
     */
    private void resetVehicles() {
        for (final Vehicle mov : myVehicles) {
            mov.reset();
        }
    }

}
