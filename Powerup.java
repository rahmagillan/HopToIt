import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Powerup {
    private Image heartPic;
	private int x,y;
	private Rectangle pRect;
	
    public Powerup(int[][] map) throws IOException{
    	int blankSpots = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == 0) {
					blankSpots++;					
				}
			}
		}
				
		int powerupPos = (int)(Math.random()*blankSpots);
		
		int counter = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == 0)
					counter++;
				if (counter == powerupPos) {
					x = j*400+600; //account for offset
					y = i*400;
				}
			}
		}
    	try {
			heartPic = ImageIO.read(new File("heart.png")).getScaledInstance(50,50, Image.SCALE_SMOOTH);
		} catch (IOException e) {}
		
		pRect = new Rectangle(x,y,50,50);
    }
 	  
    public int getX(){
    	return x;
    }
    public int getY(){
    	return y;
    }
    public Rectangle getRect() {
		return pRect;
	}
    public boolean collide(){
    	if ((x+50)>525 && x<(525+150) && (y+50)>275 && y<(275+150)){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    public void align(int x, int y) {
		//adjust x and y positions when the blocks are shifted in the main class
		this.x += x;
		this.y += y;
		pRect = new Rectangle(x,y,50,50);
		//don't adjust static values used for minimap
	}
	public void draw(Graphics g) {
		g.drawImage(heartPic, x, y, null);
	}
}

    