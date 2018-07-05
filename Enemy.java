import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Enemy {
	
	//fields
	private int x,y; //relative position of the enemy
	private int staticx,staticy; //for mini map
	private int length = 300; //sizes for enemy
	private int width = 300;
	private int velocity = 10;
	//keep track of direction enemy is moving in
	private String[] dKey = {"r","l","u","d"};
	private boolean[] dBoolean = {false,false,false,true}; 
	private int distanceLimit = 400-length; //size of one block - length for enemy path
	private int distanceMoved = 0; //can only move max 400 pixels
	private boolean change = false;
	private int attackRange= 600; //max distance enemies can shoot
	private ArrayList<EnemyBullet> bullets = new ArrayList<EnemyBullet>(); //list of the enemies bullets
	
	//sprites
	private Image currImage;
	private ArrayList<Image> upSprite = new ArrayList<Image>();
	private ArrayList<Image> downSprite = new ArrayList<Image>();
	private ArrayList<Image> rightSprite = new ArrayList<Image>();
	private ArrayList<Image> leftSprite = new ArrayList<Image>();
	private ArrayList<Image> deadSprite = new ArrayList<Image>();
	//for blitting sprites
	private int upCount = 0;
	private int downCount = 0;
	private int rightCount = 0;
	private int leftCount = 0;
	
	private int health = 1000;
	private int max_health = 1000;
	private HealthBar eHealthBar;
	
	private int bodyDmg; //damage enemy deals to user when the user runs into the Enemy
	private Rectangle eRect; //used for collisions with the Player/Player's bullets
	
	private boolean dead = false;
	//for animating the death
	private int interval = 0;
	private int spriteCounter = 0;
	
	//constructor
	public Enemy(int[][] map) throws IOException{	
		bodyDmg = -100;
		//count the number of 0's in the map
		int blankSpots = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == 0) {
					blankSpots++;					
				}
			}
		}	
		int enemyPos = 0;
		while (enemyPos == 0) { //do not let the enemy position = 0 as that is where the user spawns
			enemyPos = (int)(Math.random()*blankSpots);
		}
		//find the random position - of the empty square - to generate the enemy on
		int counter = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == 0)
					counter++;
				if (counter == enemyPos) {
					x = j*400+400;
					y = i*400-200-velocity;//account for offset and velocity(only y values since it starts off moving downward)
					staticx = j*400+400;
					staticy = i*400-200-velocity;
				}
			}
		}
			
		//load sprites
		try {
			for (int i = 1; i < 4; i++) {
				 upSprite.add(ImageIO.read(new File("EnemySprites/up"+i+".png")).getScaledInstance(length, width, Image.SCALE_SMOOTH));
				 downSprite.add(ImageIO.read(new File("EnemySprites/down"+i+".png")).getScaledInstance(length, width, Image.SCALE_SMOOTH));
				 leftSprite.add(ImageIO.read(new File("EnemySprites/left"+i+".png")).getScaledInstance(length, width, Image.SCALE_SMOOTH));
				 rightSprite.add(ImageIO.read(new File("EnemySprites/right"+i+".png")).getScaledInstance(length, width, Image.SCALE_SMOOTH));
			}
			for (int i = 0; i < 6; i++) {
				deadSprite.add(ImageIO.read(new File("EnemySprites/die"+i+".png")).getScaledInstance(length, width, Image.SCALE_REPLICATE));
			}
		} catch (IOException e) {}
		
		eRect = new Rectangle(x+74,y+68,146,172); //align the rectangle since the sprite image has a lot of whitespace		
	}

	//getters
	public int get_staticX() {
		return staticx-400; //account for mini map offset
	}
	public int get_staticY() {
		return staticy-400;
	}
	public boolean[] get_dBoolean() {
		return dBoolean;
	}
	public ArrayList<EnemyBullet> getBullets() {
		return bullets;
	}
	public Rectangle getRect() {
		return eRect;
	}
	public int getBodyDmg() {
		return bodyDmg;
	}
	public boolean getDead() {
		return dead;
	}
	
	//setters
	public void setHealth(int dmg) {
		if (health + dmg <= 0) { //do not let the health do below 0
			health = 0;
		}
		else {
			health += dmg;
		}
	}
	
	//methods
	public void align(int x, int y) {
		//adjust x and y positions when the blocks are shifted in the main class
		this.x += x;
		this.y += y;
		eRect = new Rectangle(x+74,y+68,146,172);
		//don't adjust static values used for minimap
	}
	
	//method continually moves the Enemy in circles around its square
	public void move() {
		//first determine the currDirection using the boolean[]
		String currDirection = "";
		for (int i = 0; i < dBoolean.length; i++) {
			if (dBoolean[i] == true) 
				currDirection = dKey[i];
		}
		if (change) { //when the enemy reaches the corner of its square and needs to change directions
			changeDirection(currDirection);
			change = false;
		}
				
		//adjusting the x and y according to direction
		if (currDirection.equals("r")) {
			if (distanceMoved + velocity <= distanceLimit) {
				x += velocity;
				staticx += velocity;
				distanceMoved += velocity;
				//sprites
				currImage = rightSprite.get(rightCount%3);
				rightCount++;
			}
			else {
				change = true;
				distanceMoved = 0;
			}
		}
		else if (currDirection.equals("l")) { //change the direction to down
			if (distanceMoved + velocity <= distanceLimit) { 
				x -= velocity;
				staticx -= velocity;
				distanceMoved += velocity;
				//sprites
				currImage = leftSprite.get(leftCount%3);
				leftCount++;
			}
			else {
				change = true;
				distanceMoved = 0;
			}
		}
		else if (currDirection.equals("u")) { //change the direction to left
			if (distanceMoved + velocity <= distanceLimit) {
				y -= velocity;
				staticy -= velocity;
				distanceMoved += velocity;
				//sprites
				currImage = upSprite.get(upCount%3);
				upCount++;
			}
			else {
				change = true;
				distanceMoved = 0;
			}
		}
		else if (currDirection.equals("d")) { //change the direction to right
			if (distanceMoved + velocity <= distanceLimit) { //-length
				y += velocity;
				staticy += velocity;
				distanceMoved += velocity;
				//sprites
				currImage = downSprite.get(downCount%3);
				downCount++;
			}
			else {
				change = true;
				distanceMoved = 0;
			}
		}
		eRect = new Rectangle(x+74,y+68,146,172); //reset the enemy Rectangle so it is constantly accurate for collisions
	}
	
	public void changeDirection(String currDirection) { //method used to change the enemies direction when it hits the limit
		if (currDirection.equals("r")) { //change the direction to up
			dBoolean[0] = false;
			dBoolean[2] = true;
		}
		else if (currDirection.equals("l")) { //change the direction to down
			dBoolean[1] = false;
			dBoolean[3] = true;
		}
		else if (currDirection.equals("u")) { //change the direction to left
			dBoolean[2] = false;
			dBoolean[1] = true;
		}
		else if (currDirection.equals("d")) { //change the direction to right
			dBoolean[3] = false;
			dBoolean[0] = true;
		}	
	}
	
	//make the enemy attack
	public void inRange() { //method check if player is in range of the enemy to add a bullet 
		//player is always in the center of the screen, therefore playerx = 525, and playery = 275
		if ((findDistance(x,y,600,350) <= attackRange) && bullets.size() == 0) {
			bullets.add(new EnemyBullet(x+width/2,y+length/2,600,350));
		}

		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).move();
			if (bullets.get(i).getDead())
				bullets.remove(i);
		}
	}
	
	//finds the distance between two points
	public int findDistance(int startx, int starty, int endx, int endy) {
		return (int)Math.abs(Math.sqrt((Math.pow(startx-endx,2))+(Math.pow(starty-endy,2))));
	}
	
	//method checks for the Enemy to collide with the Player's bullets and it adjusts the health of the Player accordingly
	public void collide(Bullet b) {
		if (eRect.intersects(b.getRect())) {
			if (b.getDamaged() == false) {	
				setHealth(b.getDamage()); 
				b.setDamaged(true); //only let bullet damage player once
				b.setDead(true);
			}
		}
	}
	//method used for graphics
	public void draw(Graphics g) {
		g.drawImage(currImage, x, y, null);
		
		//draw the death animation on the enemy
		if (health <=0 && spriteCounter < deadSprite.size()) {
			if (interval % 3 == 0) { //the number interval is % by determines the speed of the animation
				currImage = deadSprite.get(spriteCounter);
				spriteCounter++;
			}
			interval++;
		}	
		else if (health > 0) {	//normal drawing when the enemy is alive
			inRange();
			move(); //only move if enemies alive
			for (int i = 0; i < bullets.size(); i++) {
				bullets.get(i).draw(g);
			}
			eHealthBar = new HealthBar(x+50,y+30,health,max_health,Color.RED);
			eHealthBar.draw(g);
		}
		else { //when death animation is complete set dead flag to true so it can be removed from ArrayList
			dead = true;
		}
	}
}

//EnemyBullet class - used exclusively to generate the Enemies' bullets
class EnemyBullet {
	
	private int x,y,endx,endy,size,dmg;
	private int distance;
	private int rise,run,slope; //find slope of bullet path to move it
	private boolean dead = false; //flag to keep track of if the bullet is still being drawn
	private int dTravelled = 0; //counter to keep track of the amount the bullet has moved
	private int velocity = 20;
	private int b_speed = 20;
	
	private Image bulletPic;
	
	private Rectangle bRect;
	private boolean damaged = false; //keeps track of if bullet has already dealth damage
	//constructor
	public EnemyBullet(int startx, int starty, int endx, int endy) {
		x = startx-15; //offset
		y = starty-15;

		this.endx = endx;
		this.endy = endy;
		
		size = 30;
		dmg = -100;
		
		//calculate the distance using the distance formula
		distance = (int)Math.abs(Math.sqrt((Math.pow(startx-endx,2))+(Math.pow(starty-endy,2))))+150;//to the player plus 150 extra pixels
		
		if (endy-starty == 0) { //horizontal line
			run = (endx-startx)/b_speed;
			rise = 0;
		}
		else if (endx-startx == 0) { //vertical line
			run = 0;
			rise = (endy-starty)/b_speed;
		}
		else {
			run = (endx-startx)/b_speed;
			rise = (endy-starty)/b_speed;
		}
		
		//load image
		try {
			bulletPic = ImageIO.read(new File("EnemySprites/heartBulletRed.png")).getScaledInstance(size, size,Image.SCALE_SMOOTH);
		} catch (IOException e) { }
		
		bRect = new Rectangle(x,y,size,size);
		
	}
	
	//getters
	public boolean getDead() {
		return dead;
	}
	public Rectangle getRect() {
		return bRect;
	}
	//for tracing purposes
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getDamage() {
		return dmg;
	}
	public boolean getDamaged() {
		return damaged;
	}
	
	//setters
	public void setDamaged(boolean b) {
		damaged = b;
	}
	public void setDead(boolean b) {
		dead = b;
	}
	
	//methods
	public void align(int x, int y) {
		this.x += x;
		this.y += y;
		bRect = new Rectangle(x,y,size,size);
	}
	
	public void move() {
		if (dead == false) {
			if (dTravelled <= distance) {
				x += run;
				y += rise;
				dTravelled += velocity;
			}
			else {
				dead = true;
			}
		}
		bRect = new Rectangle(x,y,size,size);
	}
	
	public void draw(Graphics g) {
		//temporary draw bullet while its in range
		if (dead == false) {
			g.drawImage(bulletPic, x, y, null);		
		}
	}	
}