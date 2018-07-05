//Player class - the player object represents the user as a bunny that moves - only 1 present in the game at any given time
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Player {
	
	//fields
	private int playerx, playery;
	private int left_count=0;
	private int right_count=0;
	private int up_count=0;
	private int	down_count=0;
	private Image currImage;
	//lists of images
	private ArrayList<Image> hopUpSprite = new ArrayList<Image>();
	private ArrayList<Image> hopDownSprite = new ArrayList<Image>();
	private ArrayList<Image> hopRightSprite = new ArrayList<Image>();
	private ArrayList<Image> hopLeftSprite = new ArrayList<Image>();
	//right, left, up, down
	private boolean[] movingDirection = {false, false, false, true}; //array keeps track of current direction user is moving
	
	private int px,py; //relative position
	
	private int max_health;
	private int health;
	private int level;
	private int lives;
	private boolean dead = false;
	private Font myFont;
	
	//keep track of result at the end of the game
	private boolean gameOver = false;
	private boolean win = false;
	
	private Image heartPic; //used for demonstrating lives
	private HealthBar pHealthBar; //users health bar
	private Rectangle pRect; //Rectangle used for collisions
	private int adjustScore = 0; //used to adjust the score in the GamePanel class
	
	//constructor
	public Player(int x, int y, int lives, int level) {
		playerx = x;
		playery = y;
		
		max_health = 500 + level*200;
		health = 500 + level*200;
		this.lives = lives;
		this.level = level;
		
		try {
			for (int i=1;i<4;i++){
			 hopUpSprite.add(ImageIO.read(new File("HopSprite/Forward"+i+".png")).getScaledInstance(150, 150, Image.SCALE_SMOOTH));
			 hopDownSprite.add(ImageIO.read(new File("HopSprite/Back"+i+".png")).getScaledInstance(150, 150, Image.SCALE_SMOOTH));
			 hopLeftSprite.add(ImageIO.read(new File("HopSprite/Left"+i+".png")).getScaledInstance(150, 150, Image.SCALE_SMOOTH));
			 hopRightSprite.add(ImageIO.read(new File("HopSprite/Right"+i+".png")).getScaledInstance(150, 150, Image.SCALE_SMOOTH));
			  
			}
			heartPic = ImageIO.read(new File("heart.png")).getScaledInstance(40, 40, Image.SCALE_SMOOTH);
			//load font
			myFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("Alice_in_Wonderland.ttf"))).deriveFont(Font.BOLD,30);
		} catch (IOException | FontFormatException e) {}
		currImage = hopUpSprite.get(0);
		pHealthBar = new HealthBar(playerx,playery,health,max_health,new Color(60,200,80));
		pRect = new Rectangle(x,y,150,150); //playerRect stays the same since the player is always in the center of the screen
	}
	
	//getters
	public Font getFont() {
		return myFont;
	}
	public String get_movingDirection() {
		if (movingDirection[0] == true) 
			return "right";
		else if(movingDirection[1] == true)
			return "left";
		else if (movingDirection[2] == true) 
			return "up";
		else
			return "down";
	}
	public int getLevel() {
		return level;
	}
	public boolean getWin() {
		return win;
	}
	public int get_px() {
		return px;
	}
	public int get_py() {
		return py;
	}
	public Rectangle getRect() {
		return pRect;
	}
	public boolean getDead() {
		return dead;
	}
	public int getLives() {
		return lives;
	}
	public boolean getGameOver() {
		return gameOver;
	}
	public int get_scoreAdjustment() {
		int s = adjustScore;
		adjustScore = 0; //reset variable
		return s;
	}
	
	//setters
	public void setHealth(int change) {
		//only change health if it is valid
		if (health + change <= max_health && health + change >= 0) //health can not exceed the maximum or be below 0
			health += change; 
		else if (health + change < 0) {
			health = 0;
			//if lives = 0 then the game is over
			if (lives!=0){
				lives--;
				setHealth();
			}
			else{
				setGameOver(true);
			}
		}
	}
	public void setGameOver(boolean b) {
		gameOver = b;
	}
	public void nextLevel() {
		if (level < 9) //max level
			level++;
		setHealth();
	}
	public void setHealth() {
		max_health = 500 + level*200;
		health = 500 + level*200;
	}
	public void setPPos(int x,int y){
		px+= x;
		py+= y;
	}
	public void setLives(int add) {
		if (lives + add <= 4) { //maximum amount of lives is 4
			lives += add;
		}
	}
	
	//methods
	//to animate carrot attack
	public void resetMove() {
		for (int i = 0; i < movingDirection.length; i++) {
			movingDirection[i] = false;
		}
	}
	//method sets the direction the user is moving according to the array
	public void setDirection(String direction) {
		resetMove();
		if (direction.equals("right")) 
			movingDirection[0] = true;
		else if(direction.equals("left"))
			movingDirection[1] = true;
		else if (direction.equals("up")) 
			movingDirection[2] = true;
		else if(direction.equals("down"))
			movingDirection[3] = true;
	}
	
	//method checks for the user to collide with the EnemyBullet and it adjusts the health of the Player accordingly
	public void collide(EnemyBullet eBullet) {
		if (pRect.intersects(eBullet.getRect())) {
			if (eBullet.getDamaged() == false) {	
				setHealth(eBullet.getDamage()); 
				adjustScore -= 25; //subtract 25 from score
				eBullet.setDamaged(true); //only let bullet damage player once
				eBullet.setDead(true);
			}
		}
	}
	
	//method checks if user collides with enemy and adjusts - continual damage to the user
	public void collide(Enemy e) {
		if (pRect.intersects(e.getRect())) {
			setHealth(e.getBodyDmg());
			adjustScore -= 10;
		}
	}
	
	//Method checks if the user collides with any Rectangle - used primarily to check for collisions with the final rectangle before swtiching levels
	public boolean collide(Rectangle rect) {
		if (pRect.intersects(rect)) {
			if (level == 4) { //max level currently 
				win  = true;
			}
			return true;
		}
		return false;
	}
	//since each direction has three sprites, each sprite will be shown every three movements
	public void leftCount(){
		left_count+=1;
		currImage = hopLeftSprite.get(left_count%3);
		setDirection("left");
	}
	public void rightCount(){
		right_count+=1;
		currImage = hopRightSprite.get(right_count%3);
		setDirection("right");
	}
	public void upCount(){
		up_count+=1;
		currImage = hopDownSprite.get(up_count%3);
		setDirection("up");
	}
	public void downCount(){
		down_count+=1;
		currImage = hopUpSprite.get(down_count%3);
		setDirection("down");
	}
	
	//method draws player
	public void draw (Graphics g) {
		g.drawImage(currImage,playerx,playery,null);
		
		pHealthBar = new HealthBar(playerx,playery,health,max_health,new Color(60,200,80));
		pHealthBar.draw(g);
		
		//draw level box
		g.setColor(Color.BLACK);
		g.fillRect(playerx-17, playery-17, 27, 27);
		g.setColor(Color.WHITE);
		g.setFont(myFont);
	
		g.drawString(""+level, playerx-11, playery+7);
		
		//draw lives
		for (int i = 0; i < lives; i++) {
			//960,560,240,140 <-- minimap rect; draw lives above this
			g.drawImage(heartPic, 960 + i*46, 510, null);
		}	
	}	
}

class Bullet {
	
	//fields
	private int x,y,size,dmg,velocity;
	private int distanceCounter = 0;
	private int max_distance = 400; //size of 1 block
	private Image[] attackPics = new Image[4];
	private Image currBullet;
	private boolean right = false;
	private boolean left = false;
	private boolean up = false;
	private boolean down = false;
	private Rectangle bulletRect;
	private boolean damaged = false; //flag tracks if the bullet has already done damage
	private boolean dead = false;
	
	//constructor
	public Bullet(String direction) {
		
		//location of the player on the screen at all times
		x = 575; //middle + half of 150
		y = 325;
		size = 50;
		dmg = -200;
		velocity = 30;
		//load bullet images in different orientations
		try {
			 attackPics[0] = ImageIO.read(new File("carrotRight.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT);
			 attackPics[1] = ImageIO.read(new File("carrotLeft.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT);
			 attackPics[2] = ImageIO.read(new File("carrotUp.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT);
			 attackPics[3] = ImageIO.read(new File("carrotDown.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT);
		}
		catch (IOException e) {}
		
		if (direction.equals("right")) { 
			currBullet = attackPics[0];
			right = true;
		}
		else if (direction.equals("left")) { 
			currBullet = attackPics[1];
			left = true;
		}
		else if (direction.equals("up")) { 
			currBullet = attackPics[2];
			up = true;
		}
		if (direction.equals("down")) { 
			currBullet = attackPics[3];
			down = true;
		}
		
		bulletRect = new Rectangle(x,y-25,size,size);
	}
	
	//getter
	public Rectangle getRect() {
		return bulletRect;
	}
	public int getDamage() {
		return dmg;
	}
	public boolean getDamaged() {
		return damaged;
	}
	public boolean getDead() {
		return dead;
	}
	
	//setter
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
		bulletRect = new Rectangle(x,y-25,size,size); //adjust rectangle used for collide
	}
	
	
	//method moves the bullet
	public void move() {
		distanceCounter++;
		if (right)
			x+=velocity;
		else if (left)
			x-=velocity;
		else if (up)
			y-=velocity;
		else if (down)
			y+=velocity;
		bulletRect = new Rectangle(x,y-25,size,size);
	}
	
	public void draw(Graphics g) {
		if (dead == false) {
			move();
			if (distanceCounter <= max_distance) {
				g.drawImage(currBullet, x, y, null);
				distanceCounter+=velocity;
			}
			else {
				dead = true;
			}
		}
	}
}
