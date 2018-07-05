import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

import java.awt.*;


public class Maze {
	//fields
	private int map_height,map_width;
	
	private int[][] map = new int[14][24];
	private ArrayList<Enemy> enemies;
	private ArrayList<Block> blockList = new ArrayList<Block>();
	private ArrayList<Powerup> powerups = new ArrayList<Powerup>();
	private Image sand;
	private Image hole;
	//private Block endBlock;
	private Rectangle endRect;
	private int endIndex; 
	
	//constructor
	public Maze(int level) throws IOException{
		map_height = map.length;
		map_width = map[0].length; //all lists are the same length
	
		//load map from corresponding txt file
		Scanner mapFile = new Scanner(new File("maze"+(level-1)+".txt"));
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				map[i][j] = mapFile.nextInt();
			}
		}
		mapFile.close();
		
		int counter = 0;
		for (int a=0;a<map_height;a++){
			for (int b=0;b<map_width;b++){
				
				if (map[a][b] != 0) {
					if (map[a][b] == 3) {
						//the block at the end
						blockList.add(new Block(b,a));
						endRect = new Rectangle(b*400,a*400-200,400,400); //rectangle that the player is at
						endIndex = counter; //position in map list
					}
					else {
						blockList.add(new Block(b,a));
					}
					counter++;
				}
			}
		}
		
		sand = ImageIO.read(new File("sand.png"));
		hole = ImageIO.read(new File("rabbithole.png")).getScaledInstance(400, 400, Image.SCALE_DEFAULT);
		
		
		//generate enemies in the maze
	
		int numEnemies = (int) Math.pow(level+1, 2); //number of enemies grows as level increases
		
		enemies = new ArrayList<Enemy>();
		for (int i = 0; i < numEnemies; i++) {
			enemies.add(new Enemy(map));
		}
		
		int numPU = (int) Math.pow(level+1, 2); //number of powerups invreases at same rate as enemies
		for (int i = 0; i < numPU; i++) {
			powerups.add(new Powerup(map));
		}
	
	}

	//getters
	public int[][] getMap() {
		return map;
	}
	public int getHeight() {
		return map_height;
	}
	public int getWidth() {
		return map_width;
	}
	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}
	public Rectangle get_endRect() {
		return endRect;
	}
	public ArrayList<Powerup> getPowerups() {
		return powerups;
	}
	
	//setters
	public void removePU(int a){
		powerups.remove(a);
	}
	
	//methods
	public void draw (Graphics g) {
		int counter = 0;
		//System.out.println("****"+Arrays.toString(enemies[0].get_dBoolean()));
		for (Block b: blockList){ //draws stone texture on all blocks
			if (endIndex == counter) {
				g.drawImage(hole,b.getX(),b.getY(),null); //draw end hole
			}
			else {
				g.drawImage(sand,b.getX(),b.getY(),null); 
			}
			counter++;
		}
		
		//draw powerups
		for (int i = 0; i < powerups.size(); i ++) {
			powerups.get(i).draw(g);
		}
	}
	
	public void moveBlocks(int x, int y){ //move endRect too
		for (Block b: blockList){
			//moves all blocks by the same amount
			b.move(x,y);
		}
		endRect.x = (int)endRect.getX() + x;
		endRect.y = (int)endRect.getY() + y;
	}
	
	public boolean checkCollide(int dx,int dy){
		//checks collisions for all blocks
		for (Block b: blockList){
			if (b.collide(dx,dy)){
				return false;///////////////////////////
			}
		}
		return false;
	}



}

class Block{
	int bx, by, length, height;
	
	public Block(int indX, int indY){
		bx = indX*400;
		by = indY*400-200; //moves it up so the player doesn't start on the wall
		length = 400;
		height = 400;
	}
	
	public int getX(){
		return bx;
	}
	
	public int getY(){
		return by;
	}
	
	public int getLength(){
		return length;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void move(int x, int y){
		bx+=x;
		by+=y;
	}
	
	public boolean collide(int dx, int dy){
		if ((bx+dx<525+150 && bx+dx>525-400 && by+dy<275+150 && by+dy>275-400)) //using the worse possible scenarios, checks where the blocks collide with the player image
			return true;
		else
			return false;
	}
}
