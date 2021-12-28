package model;

import java.util.Map;

public class ATV extends AbstractVehicle{

    private static final int deathTime = 25;
    private static final String IMAGE_ALIVE = "atv.gif";
    private static final String IMAGE_DEAD = "atv_dead.gif";

    /**
     * the constructor for the ATV class, calls the super constructor in AbstractVehicle
     * @param theX the current x coordinate
     * @param theY the current y coordinate
     * @param theDir the current direction
     */
    public ATV(int theX, int theY, Direction theDir){
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
        return theTerrain != Terrain.WALL;
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
                        theNeighbors.get(getDirection().left()) == Terrain.CROSSWALK || theNeighbors.get(getDirection().left()) == Terrain.GRASS
                || theNeighbors.get(getDirection().left()) == Terrain.TRAIL){
                    return getDirection().left();
                }
            case 2: //checking if it can go straight
                if(theNeighbors.get(getDirection()) == Terrain.STREET || theNeighbors.get(getDirection()) == Terrain.LIGHT ||
                        theNeighbors.get(getDirection()) == Terrain.CROSSWALK || theNeighbors.get(getDirection()) == Terrain.GRASS
                || theNeighbors.get(getDirection()) == Terrain.TRAIL){
                    return getDirection();
                }
            case 3: //checking if it can turn right
                if(theNeighbors.get(getDirection().right()) == Terrain.STREET || theNeighbors.get(getDirection().right()) == Terrain.LIGHT ||
                        theNeighbors.get(getDirection().right()) == Terrain.CROSSWALK || theNeighbors.get(getDirection().right()) == Terrain.GRASS
                || theNeighbors.get(getDirection().right()) == Terrain.TRAIL){
                    return getDirection().right();
                }
            default:
                return chooseDirection(theNeighbors);  //if not, then call the method again to find the right way to go
        }
    }

    /**
     * method that decides what happen when a collision occurs
     * @param theOther The other object.
     */
    @Override
    public void collide(Vehicle theOther) {
        String otherName = theOther.getClass().getSimpleName().toLowerCase();
        if((otherName.equals("truck") || otherName.equals("car") || otherName.equals("taxi")) && theOther.isAlive()){
            die();
        }
    }
}
