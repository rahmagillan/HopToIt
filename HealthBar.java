import java.awt.Color;
import java.awt.Graphics;

public class HealthBar {
	
	//fields
	private Color color;
	private int x,y,health,num,max_health;
	private int length = 140;
	
	//constructors
	public HealthBar(int x, int y, int health, int max_health, Color color) {
		this.x = x;
		this.y = y;
		this.health = health;
		this.max_health = max_health;
		this.color = color;
	}
	
	//methods
	public void draw(Graphics g) {
		//draw health bar and level
		g.setColor(Color.BLACK);
		g.fillRect(x+13, y-12, 144, 19);
		g.setColor(color);
		

		if (health > 0) {
			g.fillRect(x+15, y-10, (int)((double)health/(double)max_health*length), 15);
		}
	
	}
	
	
}
