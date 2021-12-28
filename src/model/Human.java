package model;

import java.util.Map;

public class Human extends AbstractVehicle{

    private static final int deathTime = 45;
    private static final String IMAGE_ALIVE = "human.gif";
    private static final String IMAGE_DEAD = "human_dead.gif";

    /**
     * the constructor for the Human class, calls the super constructor in AbstractVehicle
     * @param theX the current x coordinate
     * @param theY the current y coordinate
     * @param theDir the current direction
     */
    public Human(int theX, int theY, Direction theDir){
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
        return switch (theTerrain) {
            case GRASS -> true;
            case CROSSWALK -> theLight != Light.GREEN;
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

        if(theNeighbors.get(getDirection()) == Terrain.CROSSWALK)   //preferring to go on a crosswalk
            return getDirection();
        else if(theNeighbors.get(getDirection().left()) == Terrain.CROSSWALK)
            return getDirection().left();
        else if(theNeighbors.get(getDirection().right()) == Terrain.CROSSWALK)
            return getDirection().right();

        switch (random) {
            case 1:  //checking if it can turn left
                if (theNeighbors.get(getDirection().left()) == Terrain.GRASS) {
                    return getDirection().left();
                }
            case 2: //checking if it can go straight
                if (theNeighbors.get(getDirection()) == Terrain.GRASS) {
                    return getDirection();
                }
            case 3: //checking if it can turn right
                if (theNeighbors.get(getDirection().right()) == Terrain.GRASS) {
                    return getDirection().right();
                }
            default:
                if (theNeighbors.get(getDirection()) != Terrain.GRASS && theNeighbors.get(getDirection().left()) != Terrain.GRASS && theNeighbors.get(getDirection().right()) != Terrain.GRASS) {
                    return getDirection().reverse();   //if no other way, turn around
                } else
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
        if((otherName.equals("truck") || otherName.equals("car") || otherName.equals("taxi") || otherName.equals("atv") || otherName.equals("bicycle")) && theOther.isAlive()){
            die();
        }
    }
}
