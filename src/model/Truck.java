package model;

import java.util.Map;

/**
 * The Truck class extends AbstractVehicle and creates a Truck object with all the specified attributes
 */
public class Truck extends AbstractVehicle {

    private final static int DEATH_TIME = 0;
    private final static String IMAGE_ALIVE = "truck.gif";
    private final static String IMAGE_DEAD = "truck_dead.gif";

    /**
     * the constructor for the Truck class, calls the super constructor in AbstractVehicle
     * @param theX the current x coordinate
     * @param theY the current y coordinate
     * @param theDir the current direction
     */
    public Truck(int theX, int theY, Direction theDir){
        super(theX, theY, theDir, DEATH_TIME, IMAGE_ALIVE, IMAGE_DEAD);
    }

    /**
     * method that checks if the vehicle can pass a certain terrain based on vehcile requirements
     * @param theTerrain The terrain.
     * @param theLight The light color.
     * @return true/false
     */
    @Override
    public boolean canPass(Terrain theTerrain, Light theLight) {
        return switch (theTerrain) {  //returns true if terrain is street or light or if crosswalk is G or Y
            case STREET, LIGHT -> true;
            case CROSSWALK -> theLight != Light.RED;
            default -> false;
        };

    }

    /**
     * method that chooses the direction for the vehicle based on requirements
     * @param theNeighbors The map of neighboring terrain.
     * @return a Direction: either current (straight), left, right, reverse
     */
    @Override
    public Direction chooseDirection(Map<Direction, Terrain> theNeighbors) {
        int random = super.getRandomNum().nextInt(3) + 1;  //getting a random num

        switch (random){
            case 1:  //checking if it can turn left
                if(theNeighbors.get(getDirection().left()) == Terrain.STREET || theNeighbors.get(getDirection().left()) == Terrain.LIGHT ||
                        theNeighbors.get(getDirection().left()) == Terrain.CROSSWALK){
                    return getDirection().left();
                }
            case 2: //checking if it can go straight
                if(theNeighbors.get(getDirection()) == Terrain.STREET || theNeighbors.get(getDirection()) == Terrain.LIGHT ||
                        theNeighbors.get(getDirection()) == Terrain.CROSSWALK){
                    return getDirection();
                }
            case 3: //checking if it can turn right
                if(theNeighbors.get(getDirection().right()) == Terrain.STREET || theNeighbors.get(getDirection().right()) == Terrain.LIGHT ||
                        theNeighbors.get(getDirection().right()) == Terrain.CROSSWALK){
                    return getDirection().right();
                }
            default:
                if((theNeighbors.get(getDirection().left()) == Terrain.GRASS || theNeighbors.get(getDirection().right()) == Terrain.GRASS || theNeighbors.get(getDirection()) == Terrain.GRASS)
                 && (theNeighbors.get(getDirection().left()) == Terrain.WALL || theNeighbors.get(getDirection().right()) == Terrain.WALL || theNeighbors.get(getDirection()) == Terrain.WALL)
                 && (theNeighbors.get(getDirection().left()) == Terrain.TRAIL || theNeighbors.get(getDirection().right()) == Terrain.TRAIL || theNeighbors.get(getDirection()) == Terrain.TRAIL)){
                    return getDirection().reverse();   //if no other way, turn around
                }else
                    return chooseDirection(theNeighbors);  //if not, then call the method again to find the right way to go
        }
    }

    /**
     * method that decides what happen when a collision occurs
     * @param theOther The other object.
     */
    @Override
    public void collide(Vehicle theOther) {  //truck always survives a collision

    }


}
