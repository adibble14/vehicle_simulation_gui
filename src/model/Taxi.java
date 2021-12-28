package model;

import java.util.Map;

public class Taxi extends AbstractVehicle {

    private static final int deathTime = 15;
    private static final String IMAGE_ALIVE = "taxi.gif";
    private static final String IMAGE_DEAD = "taxi_dead.gif";
    private static int clockTicks = 0;

    /**
     * the constructor for the Taxi class, calls the super constructor in AbstractVehicle
     * @param theX the current x coordinate
     * @param theY the current y coordinate
     * @param theDir the current direction
     */
    public Taxi(int theX, int theY, Direction theDir) {
        super(theX, theY, theDir, deathTime, IMAGE_ALIVE, IMAGE_DEAD);
    }

    /**
     * method that checks if the vehicle can pass a certain terrain based on vehcile requirements
     * @param theTerrain The terrain.
     * @param theLight The light color.
     * @return true/false
     */
    @Override
    public boolean canPass(Terrain theTerrain, Light theLight) {
        switch (theTerrain) {
            case STREET:
                return true;
            case LIGHT:
                return theLight != Light.RED;
            case CROSSWALK: //if red, will only stop for 3 sec or till green
                if(theLight == Light.RED && clockTicks >= 3) {
                    clockTicks = 0;
                    return true;
                }else if(theLight == Light.RED){
                    clockTicks++;
                    return false;
                }
                else {
                    return true;
                }
            default:
                return false;
        }
    }

    /**
     * method that chooses the direction for the vehicle based on requirements
     * @param theNeighbors The map of neighboring terrain.
     * @return a Direction: either current (straight), left, right, reverse
     */
    @Override
    public Direction chooseDirection(Map<Direction, Terrain> theNeighbors) {
        if(theNeighbors.get(getDirection()) == Terrain.STREET || theNeighbors.get(getDirection()) == Terrain.LIGHT ||
                theNeighbors.get(getDirection()) == Terrain.CROSSWALK){
            return getDirection();
        }else if(theNeighbors.get(getDirection().left()) == Terrain.STREET || theNeighbors.get(getDirection().left()) == Terrain.LIGHT ||
                theNeighbors.get(getDirection().left()) == Terrain.CROSSWALK){
            return getDirection().left();
        }else if(theNeighbors.get(getDirection().right()) == Terrain.STREET || theNeighbors.get(getDirection().right()) == Terrain.LIGHT ||
                theNeighbors.get(getDirection().right()) == Terrain.CROSSWALK){
            return getDirection().right();
        }else{
            return getDirection().reverse();
        }
    }

    /**
     * method that decides what happen when a collision occurs
     * @param theOther The other object.
     */
    @Override
    public void collide(Vehicle theOther) {
        if(theOther.getClass().getSimpleName().equalsIgnoreCase("truck")  && theOther.isAlive()){
            die();
        }
    }
}