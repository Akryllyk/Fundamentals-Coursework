package uk.ac.bradford.dungeongame;

/**
 * The Entity class stores basic state information for both the Player and
 * Monster entity types in the game. The type of entity is stored as an
 * EntityType, an enumeration type local to this class.
 *
 * @author prtrundl
 */
public class Entity {

    /**
     * EntityType is an enumeration type with two possible values, representing
     * either a Monster or the Player in the game.
     */
    public enum EntityType {
        PLAYER, MONSTER
    }

    /**
     * maxHealth stores the maximum possible health for this entity
     */
    private int maxHealth;

    /**
     * health stores the current health for this entity
     */
    private int health;

    /**
     * xPos is the current x position in the game for this entity
     */
    private int xPos;

    /**
     * yPos is the current y position in the game for this entity
     */
    private int yPos;
    /**
     * damage is the amount of damage the entity does. The default for this will
     * be 10.
     */
    private int damage;
    /**
     * the armour means the player takes less damage if they get armour in a
     * chest
     */
    private int armour;
    /**
     * type is used to distinguish between the player and monsters in the game
     */

    private EntityType type;

    /**
     * This constructor is used to create an Entity object to use in the game
     * for both the player and monsters
     *
     * @param maxHealth the maximum health of this Entity, also used to set its
     * starting health value
     * @param x the X position of this Entity in the game
     * @param y the Y position of this Entity in the game
     * @param type They type of Entity, either EntityType.PLAYER or
     * EntityType.MONSTER sets damage to 10 as default sets armour to 0 as
     * default
     */
    public Entity(int maxHealth, int x, int y, EntityType type) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        xPos = x;
        yPos = y;
        damage = 10;
        armour = 0;
        this.type = type;
    }

    /**
     * This method returns the current X position for this entity in the game
     *
     * @return The X co-ordinate of this Entity in the game
     */
    public int getX() {
        return xPos;
    }

    /**
     * This method returns the current Y position for this entity in the game
     *
     * @return The Y co-ordinate of this Entity in the game
     */
    public int getY() {
        return yPos;
    }

    /**
     * Sets the position of the Entity in the game
     *
     * @param x The new X position for this Entity
     * @param y The new Y position for this Entity
     */
    public void setPosition(int x, int y) {
        xPos = x;
        yPos = y;
    }

    /**
     * Changes the current health value for this Entity, setting the health to
     * maxHealth if the change would cause the health attribute to exceed
     * maxHealth
     *
     * @param change An integer representing the change in health for this
     * Entity. Passing a positive value will increase the health, passing a
     * negative value will decrease the health.
     */
    public void changeHealth(int change) {
        health += change;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    /**
     * Changes the damage for this entity
     *
     * @param d the number the damage will change to
     */
    public void changeDamage(int d) {
        damage = d;
    }

    /**
     * Changes the armour value to the parameter
     *
     * @param a An integer showing what the armour changes to
     */
    public void changeArmour(int a) {
         armour += a;
         if (armour < 0) {
            armour = 0;
        } 

    }

    /**
     * Returns the current damage value for the Entity
     *
     * @return the value of the damage attribute for this entity
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Returns the current armour for this Entity
     *
     * @return the value of the armour attribute for this entity;
     */
    public int getArmour() {
        return armour;
    }

    /**
     * Returns the current health value for this Entity
     *
     * @return the value of the health attribute for this Entity
     */
    public int getHealth() {
        return health;
    }

    /**
     * Returns the maxHealth value for this Entity
     *
     * @return the value of the maxHealth attribute for this Entity
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Returns the type of this Entity, either EntityType.PLAYER or
     * EntityType.MONSTER
     *
     * @return the EntityType of this entity
     */
    public EntityType getType() {
        return type;
    }
}
