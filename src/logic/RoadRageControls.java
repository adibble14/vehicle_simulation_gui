
package logic;

/**
 * Define the behaviors of the Road Rage simulation. 
 * 
 * @author Charles Bryan
 * @version 1 OCT 2018
 */
public interface RoadRageControls {

    /**
     * Advances the simulation by one frame of animation, moving each vehicle
     * once and checking collisions.
     */
    void advance();

    /**
     * Put the simulation in the starting state. 
     */
    void start();
    
    /**
     * Reset the simulation to the starting state.
     */
    void reset();
    
    /**
     * Access the height of the 2D grid of Terrain that forms the map. 
     * @return the height of the 2D grid
     */
    int getHeight();
    
    /**
     * Access the width of the 2D grid of Terrain that forms the map. 
     * @return the width of the 2D grid
     */
    int getWidth();

}
