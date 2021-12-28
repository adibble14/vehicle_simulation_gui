package model;
import java.util.Random;

/**
 * the AbstractVehicle class is the parent class for all vehicles. It contains many methods that all the vehicles must implement that are defined in the
 * Vehicle interface. It contains one constructor that all the vehicles call.
 */
public abstract class AbstractVehicle implements Vehicle {  //not instantiable because its abstract

    private final Random randomNum;
    private Direction theDir;
    private int theX;
    private int theY;
    private int deathTime;
    private final String imageAlive;
    private final String imageDead;
    private final int  originalX;
    private final int originalY;
    private final Direction originalDir;
    private boolean isAlive = true;

    /**
     * the constructor for AbstractVehicle
     * @param theX the current x coordinate
     * @param theY the current y coordinate
     * @param theDir the current direction
     * @param deathTime the death time for this vehicle
     * @param imageAlive the image file name for when the vehicle is alive
     * @param imageDead the image file name for when the vehicle is dead
     */
    protected AbstractVehicle(int theX, int theY, Direction theDir, int deathTime, String imageAlive, String imageDead){
        randomNum = new Random();
        this.theDir = theDir;
        originalDir = theDir;
        this.theX= theX;
        originalX = theX;
        this.theY = theY;
        originalY = theY;
        this.deathTime = deathTime;
        this.imageAlive = imageAlive;
        this.imageDead = imageDead;

    }

    /**
     * method to return the death time
     * @return deathTime an integer of the death time for the vehicle
     */
    @Override
    public int getDeathTime() {
        return deathTime;
    }

    /**
     * method to return the image file name, called by the GUI
     * @return image file name as a String - imageAlive or imageDead depending on if the vehicle is alive
     */
    @Override
    public String getImageFileName() {
        if(isAlive())
            return imageAlive;
        else
            return imageDead;
    }

    /**
     * method to return the direction of the vehicle
     * @return theDir Direction
     */
    @Override
    public Direction getDirection() {
        return theDir;
    }

    /**
     * method to return current x coordinate
     * @return theX int
     */
    @Override
    public int getX() {
        return theX;
    }

    /**
     * method to return the current y coordinate
     * @return theY int
     */
    @Override
    public int getY() {
        return theY;
    }

    /**
     * method to return if the vehicle is alive
     * @return isAlive boolean
     */
    @Override
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * method to set a vehicle's alive status
     * @param alive boolean that is either true/false depending on if vehicle should be set to alive/dead
     */
    protected void setAlive(boolean alive){
        isAlive = alive;
    }

    /**
     * method to kill the vehicle because it did not survive a collision
     */
    protected void die(){
        if(isAlive())
            setAlive(false);
    }

    /**
     * method that the GUI calls to decrement a dead vehicle's death time so that it will eventually revive itself
     */
    @Override
    public void poke() {
        deathTime--;
        if(deathTime == 0) {
            theDir = Direction.random();
            setAlive(true);
        }
    }

    /**
     * method that the GUI calls when the reset button is pushed, puts all vehicles back in original starting positions
     */
    @Override
    public void reset() {
        theDir = originalDir;
        theX = originalX;
        theY = originalY;
        setAlive(true);
        deathTime=0;
    }

    /**
     * method to set the direction of a vehicle
     * @param theDir The new direction.
     */
    @Override
    public void setDirection(Direction theDir) {
        this.theDir = theDir;
    }

    /**
     * method to change the x coordinate
     * @param theX The new x-coordinate.
     */
    @Override
    public void setX(int theX) {
        this.theX = theX;
    }

    /**
     * method to change the y coordinate
     * @param theY The new y-coordinate.
     */
    @Override
    public void setY(int theY) {
        this.theY = theY;
    }

    /**
     * method that overrides the toString method in the Object class, returns a simple name of the vehicles
     * @return vehicleName and if it is alive or dead
     */
    @Override
    public String toString(){
        String vehicleName = getClass().getSimpleName().toLowerCase();  //returns a string of the class calling the method
        if(isAlive())
            return vehicleName;
        else
            return "Dead " + vehicleName;
    }

    /**
     * method to return randomNum
     * @return randomNum
     */
    protected Random getRandomNum(){
        return randomNum;
    }
}
