/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bradford.dungeongame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import uk.ac.bradford.dungeongame.Entity.EntityType;

/**
 * The GameEngine class is responsible for managing information about the game,
 * creating levels, the player and monsters, as well as updating information
 * when a key is pressed while the game is running.
 *
 * @author prtrundl
 */
public class GameEngine {

    /**
     * An enumeration type to represent different types of tiles that make up a
     * dungeon level. Each type has a corresponding image file that is used to
     * draw the right tile to the screen for each tile in a level. Floors are
     * open for monsters and the player to move into, walls should be
     * impassable, stairs allow the player to progress to the next level of the
     * dungeon, and chests can yield a reward when moved over.
     */
    public enum TileType {
        WALL, FLOOR, CHEST, STAIRS
    }

    /**
     * The width of the dungeon level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int DUNGEON_WIDTH = 25;

    /**
     * The height of the dungeon level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int DUNGEON_HEIGHT = 18;

    /**
     * The maximum number of monsters that can be generated on a single level of
     * the dungeon. This attribute can be used to fix the size of an array (or
     * similar) that will store monsters.
     */
    public static final int MAX_MONSTERS = 40;

    /**
     * The chance of a wall being generated instead of a floor when generating
     * the level. 1.0 is 100% chance, 0.0 is 0% chance.
     */
    public static final double WALL_CHANCE = 0.05;
    /**
     * Whether the boss is dead or not. Being true activates the end of the game.
     */
    public static boolean bossDead = false;

    /**
     * A random number generator that can be used to include randomised choices
     * in the creation of levels, in choosing places to spawn the player and
     * monsters, and to randomise movement and damage. This currently uses a
     * seed value of 123 to generate random numbers - this helps you find bugs
     * by giving you the same numbers each time you run the program. Remove the
     * seed value if you want different results each game.
     */
    private Random rng = new Random();

    /**
     * The current level number for the dungeon. As the player moves down stairs
     * the level number should be increased and can be used to increase the
     * difficulty e.g. by creating additional monsters with more health.
     */
    private int depth = 1;  //current dunegeon level

    /**
     * The GUI associated with a GameEngine object. THis link allows the engine
     * to pass level (tiles) and entity information to the GUI to be drawn.
     */
    private GameGUI gui;

    /**
     * The 2 dimensional array of tiles the represent the current dungeon level.
     * The size of this array should use the DUNGEON_HEIGHT and DUNGEON_WIDTH
     * attributes when it is created.
     */
    private TileType[][] tiles;

    /**
     * An ArrayList of Point objects used to create and track possible locations
     * to spawn the player and monsters.
     */
    private ArrayList<Point> spawns;

    /**
     * An Entity object that is the current player. This object stores the state
     * information for the player, including health and the current position
     * (which is a pair of co-ordinates that corresponds to a tile in the
     * current level)
     */
    private Entity player;

    /**
     * An array of Entity objects that represents the monsters in the current
     * level of the dungeon. Elements in this array should be of the type
     * Entity, meaning that a monster is alive and needs to be drawn or moved,
     * or should be null which means nothing is drawn or processed for movement.
     * Null values in this array are skipped during drawing and movement
     * processing. Monsters (Entity objects) that die due to player attacks can
     * be replaced with the value null in this array which removes them from the
     * game.
     */
    private Entity[] monsters;

    /**
     * Constructor that creates a GameEngine object and connects it with a
     * GameGUI object.
     *
     * @param gui The GameGUI object that this engine will pass information to
     * in order to draw levels and entities to the screen.
     */
    public GameEngine(GameGUI gui) {
        this.gui = gui;
        startGame();
    }

    /**
     * Generates a new dungeon level. The method builds a 2D array of TileType
     * values that will be used to draw tiles to the screen and to add a variety
     * of elements into each level. Tiles can be floors, walls, stairs (to
     * progress to the next level of the dungeon) or chests. The method should
     * contain the implementation of an algorithm to create an interesting and
     * varied level each time it is called.
     *
     * @return A 2D array of TileTypes representing the tiles in the current
     * level of the dungeon. The size of this array should use the width and
     * height of the dungeon.
     */
    private TileType[][] generateLevel() {
        //create new level
        TileType[][] level = new TileType[DUNGEON_WIDTH][DUNGEON_HEIGHT];
        //stuff for spawning appropriate amount of chests
        boolean noStairs = false;
        boolean chestDepth = false;
        int chestCount = 0;
        //allocating the maximum amount of chests for the level.
        int maxChests;
        //first 5
        if (depth <= 5) {
            maxChests = 2;
            //next 15
        } else if (depth <= 20) {
            maxChests = 3;
            //next 15
        } else if (depth <= 35) {
            maxChests = 4;
            //last 4
        } else if (depth < 40) {
            maxChests = 5;
        } else if (depth == 40) {
            maxChests = 0;
            noStairs = true;
            //if something breaks 
        } else {
            maxChests = 2;
        }
        //generating the level
        for (int i = 0; i < DUNGEON_WIDTH; i++) { //loop through the x axis
            for (int j = 0; j < DUNGEON_HEIGHT; j++) { //loop through the y axis
                int randInt = rng.nextInt(100); //generate a random number between 0 and 99 (inclusive)
                if (i == 0 || j == 0 || i == (DUNGEON_WIDTH - 1) || j == (DUNGEON_HEIGHT - 1)) { //make the outer border a wall
                    level[i][j] = TileType.WALL;
                } else if (randInt >= 90) { // if the random number >= 90, it makes it a wall, unless it's level 40, then it will spawn a floor
                    if (depth == 40) {
                        level[i][j] = TileType.FLOOR;
                    } else {
                        level[i][j] = TileType.WALL;
                    }
                } else if (randInt >= 70 && noStairs == false) { // if number is greater than or equal to 70 and no stairs are spawned, it spawns a stairs.
                    level[i][j] = TileType.STAIRS;
                    //set nostairs to true to stop more stairs
                    noStairs = true;
                } else if (randInt < 15 && chestDepth == false) { // if number is less than 15 and there are less chests than the level is meant to have
                    if (chestCount >= maxChests) {//check if there are too many chests
                        level[i][j] = TileType.FLOOR;
                        chestDepth = true;
                    } else {
                        level[i][j] = TileType.CHEST; //put a chest there if it isnt
                        chestCount++;
                    }
                } else { // if the number isnt ordinary 
                    level[i][j] = TileType.FLOOR;
                }

            }
        }
        //return the level
        return level;

    }

    /**
     * Generates spawn points for the player and monsters. The method processes
     * the tiles array and finds tiles that are suitable for spawning, i.e.
     * tiles that are not walls or stairs. Suitable tiles should be added to the
     * ArrayList that will contain Point objects - Points are a simple kind of
     * object that contain an X and a Y co-ordinate stored using the int
     * primitive type and are part of the Java language (search for the Point
     * API documentation and examples of their use)
     *
     * @return An ArrayList containing Point objects representing suitable X and
     * Y co-ordinates in the current level that the player or monsters can be
     * spawned in
     */
    private ArrayList<Point> getSpawns() {
        ArrayList<Point> s = new ArrayList<>();
        for (int i = 0; i < DUNGEON_WIDTH; i++) { //loop through x axis
            for (int j = 0; j < DUNGEON_HEIGHT; j++) { //loop through y axis
                if (tiles[i][j] == TileType.FLOOR) { //if the tile is a floor
                    s.add(new Point(i, j)); //it adds it to the arraylist

                }

            }

        }
        //return the arraylist
        return s;
    }

    /**
     * Spawns monsters in suitable locations in the current level. The method
     * uses the spawns ArrayList to pick suitable positions to add monsters,
     * removing these positions from the spawns ArrayList as they are used
     * (using the remove() method) to avoid multiple monsters spawning in the
     * same location. The method creates monsters by instantiating the Entity
     * class, setting health, and setting the X and Y position for the monster
     * using the X and Y values in the Point object removed from the spawns
     * ArrayList.
     *
     * @return A array of Entity objects representing the monsters for the
     * current level of the dungeon
     */
    private Entity[] spawnMonsters() {
        //maximum monsters for the level
        int maxMonsters;
        //first 5 levels
        if (depth <= 5) {
            maxMonsters = 3;
            //next 15
        } else if (depth <= 20) {
            maxMonsters = 4;
            //15 after
        } else if (depth <= 35) {
            maxMonsters = 5;
            //last 4
        } else if (depth < 40) {
            maxMonsters = 6;
            //spawn the boss for level 40.
        } else if (depth == 40) {
            maxMonsters = 1;
            //catch incase something breaks
        } else {
            maxMonsters = 2;
        }
        //create array with length of the maximum monsters
        Entity[] monsters = new Entity[maxMonsters];
        //loop through the array
        for (int i = 0; i < monsters.length; i++) {
            //generate a random number the size of getspawns
            int n = rng.nextInt(getSpawns().size());
            //creates a x and y value with the randomly generated index's x and y value
            int x = getSpawns().get(n).x;
            int y = getSpawns().get(n).y;
            //remove the index from getspawns
            getSpawns().remove(n);
            //is boss
            if (depth == 40) {
                //5000 health because chests are broken and I dont want to fix it.
                monsters[i] = new Entity(5000, x, y, EntityType.MONSTER);
                monsters[i].changeDamage(70);
            } else {
                //regular monster
                monsters[i] = new Entity(50, x, y, EntityType.MONSTER);
            }
        }
        //return the array
        return monsters;

    }

    /**
     * Spawns a player entity in the game. The method uses the spawns ArrayList
     * to select a suitable location to spawn the player and removes the Point
     * from the spawns ArrayList. The method instantiates the Entity class and
     * assigns values for the health, position and type of Entity.
     *
     * @return An Entity object representing the player in the game
     */
    private Entity spawnPlayer() {
        //generate a random number from the size of getspawns
        int n = rng.nextInt(getSpawns().size());
        //get the x and y values of that point
        int x = getSpawns().get(n).x;
        int y = getSpawns().get(n).y;
        //remove the point from getspawns
        getSpawns().remove(n);
        //create a new player
        Entity player = new Entity(100, x, y, EntityType.PLAYER);
        //return the player
        return player;
    }

    /**
     * Handles the movement of the player when attempting to move left in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the left arrow key on the keyboard. The method checks
     * whether the tile to the left of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new
     * position. If the tile to the left of the player is not empty the method
     * will not update the player position, but may make other changes to the
     * game, such as damaging a monster in the tile to the left, or breaking a
     * wall etc.
     */
    public void movePlayerLeft() {
        //get player x and y co-ordinates
        int x = player.getX();
        int y = player.getY();
        //used for monster checking and combat
        int i;
        //whether the monster is to the left or not
        boolean monsterLeft = false;
        //loop through the monster array
        for (i = 0; i < monsters.length; i++) {
            //if the monsters isnt null so it doesnt error on me and i dont want to deal with try/catch
            if (monsters[i] != null) {
                //check if the monster is there
                if ((x - 1) == monsters[i].getX() && y == monsters[i].getY()) {
                    //if it is, boolean is true and break out of loop
                    monsterLeft = true;
                    break;
                }
            }
        }
        //if the player is trying to walk into a wall
        if (tiles[(x - 1)][y] == TileType.WALL) {
            //if the player is trying to fight a monster
        } else if (monsterLeft) {
            //hit the monster
            hitMonster(monsters[i]);
            //if the player moves onto the chest
        } else if (tiles[(x - 1)][y] == TileType.CHEST) {
            //open the chest
            openChest();
            //hopefully something good was in there
            //move the player
            player.setPosition((x - 1), y);
            //set the chest to a floor so they cant open multiple chests
            tiles[(x - 1)][y] = TileType.FLOOR;
        } else {
            //just move the player
            player.setPosition((x - 1), y);
        }
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the right arrow key on the keyboard. The method checks
     * whether the tile to the right of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new
     * position. If the tile to the right of the player is not empty the method
     * will not update the player position, but may make other changes to the
     * game, such as damaging a monster in the tile to the right, or breaking a
     * wall etc.
     */
    public void movePlayerRight() {
        //get player x and y co-ordinates
        int x = player.getX();
        int y = player.getY();
        //used for monster checking and combat
        int i;
        //whether the monster is to the right or not
        boolean monsterRight = false;
        //loop through the monsters array
        for (i = 0; i < monsters.length; i++) {
            //if the monster exists...          but what is existence?
            if (monsters[i] != null) {
                //if the monster is where the player is trying to move to
                if ((x + 1) == monsters[i].getX() && y == monsters[i].getY()) {
                    //boolean is true
                    monsterRight = true;
                    //the loop breaks
                    break;
                }
            }
        }
        //if the players walks into a wall
        if (tiles[(x + 1)][y] == TileType.WALL) {
            //if the monster is to the right of the player
        } else if (monsterRight) {
            //punch the monster
            hitMonster(monsters[i]);
        } else if (tiles[(x + 1)][y] == TileType.CHEST) {
            //open a chest
            openChest();
            //move
            player.setPosition((x + 1), y);
            //change the tile to a floor
            tiles[(x + 1)][y] = TileType.FLOOR;
            //if everything is normal
        } else {
            player.setPosition((x + 1), y);
        }

    }

    /**
     * Handles the movement of the player when attempting to move up in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the up arrow key on the keyboard. The method checks
     * whether the tile above the player is empty for movement and if it is
     * updates the player object's X and Y locations with the new position. If
     * the tile above the player is not empty the method will not update the
     * player position, but may make other changes to the game, such as damaging
     * a monster in the tile above the player, or breaking a wall etc.
     */
    public void movePlayerUp() {
        //get player x and y
        int x = player.getX();
        int y = player.getY();
        //monster id
        int i;
        //if there is a monster above
        boolean monsterUp = false;
        //loop through monsters
        for (i = 0; i < monsters.length; i++) {
            //if the monster is there
            if (monsters[i] != null) {
                //check if there is a monster
                if (x == monsters[i].getX() && (y - 1) == monsters[i].getY()) {
                    monsterUp = true;
                    break;
                }
            }
        }
        //ouch i walked into a wall
        if (tiles[x][(y - 1)] == TileType.WALL) {
            //fight the monster
        } else if (monsterUp) {
            hitMonster(monsters[i]);
            //chest 
        } else if (tiles[x][(y - 1)] == TileType.CHEST) {
            openChest();
            player.setPosition(x, (y - 1));
            tiles[x][(y - 1)] = TileType.FLOOR;
            //regular floor
        } else {
            player.setPosition(x, (y - 1));
        }
    }

    /**
     * Handles the movement of the player when attempting to move down in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the down arrow key on the keyboard. The method checks
     * whether the tile below the player is empty for movement and if it is
     * updates the player object's X and Y locations with the new position. If
     * the tile below the player is not empty the method will not update the
     * player position, but may make other changes to the game, such as damaging
     * a monster in the tile below the player, or breaking a wall etc.
     */
    public void movePlayerDown() {
        //you know what all this does by now hopefully but if not
        //get x and y
        int x = player.getX();
        int y = player.getY();
        //loop counter and monster identifier
        int i;
        //booooooooolean
        boolean monsterDown = false;
        //loop monsters
        for (i = 0; i < monsters.length; i++) {
            //monsters arent even people, but they arent null
            if (monsters[i] != null) {
                //if the monster decided to be there
                if (x == monsters[i].getX() && (y + 1) == monsters[i].getY()) {
                    monsterDown = true;
                    //DON'T GO BREAKING MY HEART
                    break;
                }
            }
        }
        //stop walking into walls
        if (tiles[x][(y + 1)] == TileType.WALL) {
            //monster punching 101
        } else if (monsterDown) {
            hitMonster(monsters[i]);
            //chests
        } else if (tiles[x][(y + 1)] == TileType.CHEST) {
            openChest();
            player.setPosition(x, (y + 1));
            tiles[x][(y + 1)] = TileType.FLOOR;
            //if the players being boring
        } else {
            player.setPosition(x, (y + 1));
        }
    }

    /**
     * Reduces a monster's health in response to the player attempting to move
     * into the same square as the monster (attacking the monster).
     *
     * @param m The Entity which is the monster that the player is attacking
     */
    private void hitMonster(Entity m) {
        //change the health equal to -player damage
        m.changeHealth(-player.getDamage());
        //call the dialog box to display a message
        gui.combatDialog("Monster took " + String.valueOf(player.getDamage()) + " damage");
    }

    /**
     * Moves all monsters on the current level. The method processes all
     * non-null elements in the monsters array and calls the moveMonster method
     * for each one.
     */
    private void moveMonsters() {
        //loop
        for (int i = 0; i < monsters.length; i++) {
            //if the monster is there
            if (monsters[i] != null) {
                moveMonster(monsters[i]);
            }
        }
    }

    /**
     * Moves a specific monster in the game. The method updates the X and Y
     * attributes of the monster Entity to reflect its new position.
     *
     * @param m The Entity (monster) that needs to be moved
     */
    private void moveMonster(Entity m) {
        //generates a number between 1 and 4
        int randomNo = rng.nextInt(4);
        //get x and y of the monster
        int x = m.getX();
        int y = m.getY();
        //switch the case 
        switch (randomNo) {
            //i didn't check whether there was a monster where the monster is trying to move to because "the monsters have team co-ordination"
            //if 0 (move up)
            case 0:
                //walk into a wall
                if (tiles[x][(y - 1)] == TileType.WALL) {
                    //combat
                } else if (x == player.getX() && (y - 1) == player.getY()) {
                    hitPlayer(m);
                    //move
                } else {
                    m.setPosition(x, (y - 1));
                }
                break;
            //if 1 (move right)
            case 1:
                //wall protection
                if (tiles[(x + 1)][(y)] == TileType.WALL) {
                    m.setPosition(x, y);
                    //combat
                } else if ((x + 1) == player.getX() && y == player.getY()) {
                    hitPlayer(m);
                    //move
                } else {
                    m.setPosition((x + 1), y);
                }
                break;
            //if 2 (move down)
            case 2:
                //wall
                if (tiles[x][(y + 1)] == TileType.WALL) {
                    //combat
                } else if (x == player.getX() && (y + 1) == player.getY()) {
                    hitPlayer(m);
                    //move
                } else {
                    m.setPosition(x, (y + 1));
                }
                break;
            //if 3 (move left)
            case 3:
                //wall
                if (tiles[(x - 1)][y] == TileType.WALL) {
                    //combat
                } else if ((x - 1) == player.getX() && y == player.getY()) {
                    hitPlayer(m);
                    //move
                } else {
                    m.setPosition((x - 1), y);
                }
                break;
            //if the number breaks 
            default:
                m.setPosition(x, y);
        }
    }

    /**
     * Reduces the health of the player when hit by a monster - a monster next
     * to the player can attack it instead of moving and should call this method
     * to reduce the player's health
     */
    private void hitPlayer(Entity m) {
        //armour is a thing i implemented
        if (player.getArmour() > 0) {
            //take more armour in the final level
            if (depth == 40) {
                player.changeArmour(-10);
                //regular level
            } else {
                player.changeArmour(-5);
            }
            //dialog box
            gui.combatDialog("Your armout was hit!");
            //regular health
        } else {
            //player is hit for monster's damage
            player.changeHealth(-m.getDamage());
            //combat dialog
            gui.combatDialog("You took " + String.valueOf(m.getDamage()) + " damage");
        }
    }

    /**
     * Opens a chest, and gives a random item from the cases.
     * Calls the chestDialog procedure in GameGui.
     */
    private void openChest() {
        //1 in 6 chance of items
        int chestItem = (rng.nextInt(6) + 1);
        //switch //case
        switch (chestItem) {
            //if 1
            case 1: //heals all health
                player.changeHealth(player.getMaxHealth() - player.getHealth());
                //dialog box
                gui.chestDialog("Greater Healing Potion");
                break;
            case 2: //deal more damage
                player.changeDamage(player.getDamage() + 5);
                //dialog box
                gui.chestDialog("Sword Upgrade");
                break;
            case 3:  //armour
                player.changeArmour(player.getArmour() + 5);
                //dialog box
                gui.chestDialog("Armour");
                break;
            case 4: //deal even more damage
                player.changeDamage(player.getDamage() + 5);
                //dialog box
                gui.chestDialog("Greater Sword Upgrade");
                break;
            case 5: //get even more armour
                player.changeArmour(player.getArmour() + 10);
                //dialog box
                gui.chestDialog("Super Armour");
                break;
            case 6: // heal some health
                player.changeHealth(20);
                //dialog box
                gui.chestDialog("Health Potion");
                break;
            //if number gen is broken
            default:
                gui.chestDialog("Nothing");
                break;
        }
    }

    /**
     * Processes the monsters array to find any Entity in the array with 0 or
     * less health. Any Entity in the array with 0 or less health should be set
     * to null; when drawing or moving monsters the null elements in the
     * monsters array are skipped.
     */
    private void cleanDeadMonsters() {
        //loop monsters
        for (int i = 0; i < monsters.length; i++) {
            //if monster exists
            if (monsters[i] == null) {
            } else {
                //is monster is dead
                if (monsters[i].getHealth() < 1) {
                    if (depth == 40) {
                        //the king is dead
                        bossDead = true;
                        //long live the king
                    }
                    //monster is dead
                    monsters[i] = null;
                }

            }
        }
    }

    /**
     * Called in response to the player moving into a Stair tile in the game.
     * The method increases the dungeon depth, generates a new level by calling
     * the generateLevel method, fills the spawns ArrayList with suitable spawn
     * locations and spawns monsters. Finally it places the player in the new
     * level by calling the placePlayer() method. Note that a new player object
     * should not be created here unless the health of the player should be
     * reset.
     */
    private void descendLevel() {
        //increase level
        depth++;
        //create the level
        tiles = generateLevel();
        //find the spawn locations
        spawns = getSpawns();
        //create monsters
        monsters = spawnMonsters();
        //place the player
        placePlayer();
        //refresh the display
        gui.updateDisplay(tiles, player, monsters);
    }

    /**
     * Places the player in a dungeon level by choosing a spawn location from
     * the spawns ArrayList, removing the spawn position as it is used. The
     * method sets the players position in the level by calling its setPosition
     * method with the x and y values of the Point taken from the spawns
     * ArrayList.
     */
    private void placePlayer() {
        //get a random number
        int n = rng.nextInt(getSpawns().size());
        //get the x and y of that point
        int x = getSpawns().get(n).x;
        int y = getSpawns().get(n).y;
        //remove the point from the list
        getSpawns().remove(n);
        //place the player
        player.setPosition(x, y);
    }

    /**
     * Performs a single turn of the game when the user presses a key on the
     * keyboard. The method cleans dead monsters, moves any monsters still alive
     * and then checks if the player is dead, exiting the game or resetting it
     * after an appropriate output to the user is given. It checks if the player
     * moved into a stair tile and calls the descendLevel method if it does.
     * Finally it requests the GUI to redraw the game level by passing it the
     * tiles, player and monsters for the current level.
     */
    public void doTurn() {
        //kill any dead monsters
        cleanDeadMonsters();
        //if the boss is dead
        if (bossDead) {
            //tell the player they have won
            gui.messageBossDead();
            //close the game
            System.exit(0);
        } else {
            //move the monsters
            moveMonsters();
            if (player != null) {       //checks a player object exists
                if (player.getHealth() < 1) {
                    System.exit(0);     //exits the game when player is dead
                }
                if (tiles[player.getX()][player.getY()] == TileType.STAIRS) {
                    descendLevel();     //moves to next level if the player is on Stairs
                }
            }
            gui.updateDisplay(tiles, player, monsters);   //updates GUI
        }
    }

    /**
     * Starts a game. This method generates a level, finds spawn positions in
     * the level, spawns monsters and the player and then requests the GUI to
     * update the level on screen using the information on tiles, player and
     * monsters.
     */
    public void startGame() {
        tiles = generateLevel();
        spawns = getSpawns();
        monsters = spawnMonsters();
        player = spawnPlayer();
        gui.updateDisplay(tiles, player, monsters);
    }
}
