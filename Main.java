//Hop To It - ICS4U FSE
//By: Rahma Gillan and Reem Boudali
//June 15, 2018

//A rabbit runs through trying to navigate through a maze and killing his enemies to reach the rabbit hole
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.*;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

//Main class - primary function is to switch between and generate panels in the game
//where the container of the panels (JFrame) is initiated
public class Main extends JFrame implements ActionListener {
	
	//panels
	private JPanel cards, gameOver, youWin;
	private GamePanel game = new GamePanel();
	private MenuPanel menu = new MenuPanel();
	private InstructionsPanel instructions = new InstructionsPanel();
	private CreditsPanel credits = new CreditsPanel();
	private SettingsPanel settings = new SettingsPanel();
	private LeaderboardPanel leaderboard = new LeaderboardPanel();
	
	//layout used for the menu and to switch between panels
	private CardLayout cLayout = new CardLayout();
	
	private Timer myTimer; //timer for frames
	
	//creating a carrot cursor (for most panels) and a blank one (for GamePanel)
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private Image cursor = toolkit.getImage("carrot.png");
	private Image blankCursor = toolkit.getImage("blank.png").getScaledInstance(16, 16, Image.SCALE_FAST); //make cursor invisible on game screen
	
	private boolean gameStart = false; //flag keep track of if the game has begun
	private boolean nameNotRecieved = true; //flag used for popUp name entering box at the end of the game
	
	//class constructor
	public Main() throws IOException, FontFormatException {
		super("Hop to It");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		setSize(1200,738);//account for offset of the frame to panel
		setLocation(0,0); //locate on the top left of the monitor
		setIconImage(new ImageIcon("RabbitIcon.png").getImage()); //set the image in the top right and on the taskbar
		
		//Final JPanels for winning or loosing
		gameOver = new JPanel();
		youWin = new JPanel();
		JLabel goPicLabel = new JLabel(new ImageIcon("gameOver.png"));
		JLabel ywPicLabel = new JLabel(new ImageIcon("youWin.png")); 
		gameOver.add(goPicLabel);
		youWin.add(ywPicLabel);
		
		//set cursor for all panels accessible via the MenuPanel into the carrot image
		menu.setCursor(toolkit.createCustomCursor(cursor,new Point(0,0),"carrot"));
		instructions.setCursor(toolkit.createCustomCursor(cursor,new Point(0,0),"carrot"));
		credits.setCursor(toolkit.createCustomCursor(cursor,new Point(0,0),"carrot"));
		settings.setCursor(toolkit.createCustomCursor(cursor,new Point(0,0),"carrot"));
		leaderboard.setCursor(toolkit.createCustomCursor(cursor,new Point(0,0),"carrot"));
		//GamePanel invisible cursor
		game.setCursor(toolkit.createCustomCursor(blankCursor, new Point(0,0), "blank"));
		
		//add all JPanels to the layout JPanel cards
		cards = new JPanel(cLayout);
		cards.add(menu, "menu");
		cards.add(game, "game");
		cards.add(instructions, "instructions");
		cards.add(credits, "credits");
		cards.add(settings,"settings");
		cards.add(leaderboard,"leaderboard");
		cards.add(gameOver, "game over");
		cards.add(youWin, "you win");
		getContentPane().add(cards); //add to JFrame
		
		//do not let user adjust the screen size
		setResizable(false);
		setVisible(true);
		
		//start the timer 
		myTimer = new Timer(100,this);
		myTimer.start();
	}
	
	//methods
	@Override
	public void actionPerformed(ActionEvent e) { //method is primarily used to check for switching JPanels
		if (gameStart)  {	//when the game begins
			game.requestFocus(); //to implement KeyListener properly focus must be on this Panel
			game.repaint(); //refresh graphics
			game.move(); 
			game.reset(); //method checks for important collisions in the game
			if (game.getUser().getGameOver()) { //when the user looses all their lives
				gameStart = false;
				cLayout.show(cards, "game over"); 
				if (nameNotRecieved) { 
					String name = JOptionPane.showInputDialog("Name: "); //Dialog box used to add name and score onto the Leaderboard.txt
					nameNotRecieved = false;
				}
				System.exit(0); //end program
			}
			else if(game.getUser().getWin()) { //when the user reaches the end of the last level (4)
				gameStart = false;
				cLayout.show(cards, "you win");
				if (nameNotRecieved) {
					String name = JOptionPane.showInputDialog("Name: "); //Dialog box used to add name and score onto the Leaderboard.txt
					nameNotRecieved = false;
				}
				System.exit(0); //end program 
			}
		}
		else {
			menu.setVolume(settings.getGain()); //get the volume
			if (menu.getStart()) {
				cLayout.show(cards, "game");
				//change the music
				menu.stopClip();
				game.setVolume(settings.getGain());
				game.playClip();
				gameStart = true;
			}
			//show the instructions panel
			else if (menu.getInstructions()) {
				cLayout.show(cards, "instructions");
				if (instructions.getMenu()) {
					cLayout.first(cards);
					menu.setInstructions(false);
					instructions.setMenu(false);
				}
			}
			//show the credits panel
			else if (menu.getCredits()) {
				cLayout.show(cards, "credits");
				if (credits.getMenu()) {
					cLayout.first(cards);
					menu.setCredits(false);
					credits.setMenu(false);
				}
			}
			//show the settings panel
			else if (menu.getSettings()) {
				cLayout.show(cards, "settings");
				if (settings.getMenu()) {
					cLayout.first(cards);
					menu.setSettings(false);
					settings.setMenu(false);
				}
			}
			//show the leaderboard panel
			else if (menu.getLeaderboard()) {
				cLayout.show(cards, "leaderboard");
				if (leaderboard.getMenu()) {
					cLayout.first(cards);
					menu.setLeaderboard(false);
					leaderboard.setMenu(false);
				}
			}
			//end program
			else if (menu.getQuit()) {
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			}
		}
	}

	public static void main(String[] args) throws IOException, FontFormatException {
		Main frame = new Main();
	}
}

//GamePanel - where all objects in the game come together
//All game logic not within Object methods can be found in this class
class GamePanel extends JPanel implements KeyListener {

	//fields
	private Player user = new Player(525,275,3,1); //start in the center of the screen with 3 lives at level 1
	private boolean[] keys; //for KeyListener
	
	//for the map generation / drawing
	private Maze t_map;
	private int boxx,boxy;
	private Image mapPic;
	
	private boolean bullet = false; //flag used to see if a bullet is currently being shot (only one bullet at a time)
	private Bullet shoot_bullet;
	
	private int level = 1; //the level the game is currently at
	private int score = 0; 
	private JLabel displayScore = new JLabel(); //JLabel used to display the current score
	
	//fields used for the music
	private Clip clip;
	private BooleanControl muteControl;
	private FloatControl gainControl;

	//constructor
	public GamePanel() throws IOException {
		keys = new boolean[KeyEvent.KEY_LAST+1];		
		
		addKeyListener(this);
		setFocusable(true); //to focus on the JPanel so KeyListener works
		
		//score label - set to top center of the screen by default
		displayScore.setSize(new Dimension(200,50));
		displayScore.setFont(user.getFont());//set label font
		displayScore.setForeground(new Color(255,255,255));
		add(displayScore);

		t_map = new Maze(1); //generate level 1 maze to start
		try {
			mapPic = ImageIO.read(new File("map.png"));
		} catch (IOException e) {}	
				
		//music: 
		//https://stackoverflow.com/questions/25171205/playing-sound-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa	
		try {
	         // Open an audio input stream
	          File soundFile = new File("gameMusic.wav"); //you could also get the sound file with an URL
	          AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
	         // Get a sound clip resource
	         clip = AudioSystem.getClip();
	         // Open audio clip and load samples from the audio input stream
	         clip.open(audioIn);
	      } catch (UnsupportedAudioFileException e) {
	      } catch (IOException e) {
	      } catch (LineUnavailableException e) {
	      }
		gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
	}
	
	//for implementing KeyListener
	public void addNotify() {
		super.addNotify();
		requestFocus();
	}
	
	//methods to playing and stopping the music
	public void playClip() {
		clip.start();
	    clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	public void stopClip() {
		clip.stop();
	}
	//for adjusting the volume
	public void setVolume(int g){
		if (g==-6){
			muteControl.setValue(true);
		}
		else{
			muteControl.setValue(false);
			gainControl.setValue((float)g);
		}
	}
	
	//getters
	public Player getUser() {
		return user;
	}
	public int getScore() {
		return score;
	}
	
	//setter
	public void adjustScore(int add) { //parameter is amount to add to score - thus negative add amount will subtract
		if (score + add >= 0) { //do not allow for negative scores
			score += add;
		}
		else {
			score = 0;
		}
	}
	
	//methods
	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}
	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
	//method constantly checks for a variety of collisions between objects and the user death
	public void reset() {
		//check for user collision with bullets
		for (int i = 0; i < t_map.getEnemies().size(); i++) {
			if (t_map.getEnemies().get(i).getBullets().size()>0) {
				user.collide(t_map.getEnemies().get(i).getBullets().get(0));
			}
		}
		//check for the enemy colliding with the user's bullets
		for (int i = 0; i < t_map.getEnemies().size(); i++) {	
			if (bullet) {	
				t_map.getEnemies().get(i).collide(shoot_bullet);//check Bullet collide with all enemies
			}
			user.collide(t_map.getEnemies().get(i));//check Player collide with Enemy for body damage
		}
		//if player dies
		if (user.getDead()) {
			user = new Player(525,275,user.getLives()-1,user.getLevel()); //reset player with one less life
			adjustScore(-150); //remove 150 points of score upon each death
		}
		//check for users collision with the Rectangle at the end of the maze on top of the rabbit hole
		if (user.collide(t_map.get_endRect())) {
			adjustScore(level*500 + user.getLives()*100); //add score proportional to the level
			level++;
			levelChange(); //method used to reset when level changes
		}
	}
	
	//method resets things when the level changes
	public void levelChange() {
		user = new Player(525,275,user.getLives(),user.getLevel()+1); //generate a new user at next level - therefore regenerating health with more maximum health as well (see Player class)
		try {
			t_map = new Maze(level); //generate new Maze according to new level - different map/number of enemies/number of powerups per level
		} catch (IOException e) {}
	}
	
	//method used to move everything in the GamePanel
	public void move() {
		if (keys[KeyEvent.VK_RIGHT]){
			user.rightCount();
			reset(); //allow for collisions while player is moving
			if (t_map.checkCollide(-10,0)==false){ //checks if moving to the right will collide with the blocks
				if (boxx>-(2800-1200)) //bounds for the image to prevent it from moving offscreen
					boxx-=20; //ground moves in opposite direction
				t_map.moveBlocks(-20,0); //blocks move in opposite direction to give movement effect
				user.setPPos(20,0); //sends player positions for map
				for (int i = 0; i < t_map.getEnemies().size(); i++) {
					t_map.getEnemies().get(i).align(-20, 0); //aligns the enemies as user moves
					for (int j = 0; j < t_map.getEnemies().get(i).getBullets().size(); j++) {
						t_map.getEnemies().get(i).getBullets().get(j).align(-20, 0); //aligns the enemies' bullets as user moves
					}
				}
				runPowerups(-20,0); //used to align the powerups as user moves
				if (bullet) {
					shoot_bullet.align(-20, 0); //align the users bullets as the user moves
				}
			}
		}
		//same necessary alignments made when moving left, up and down
		if (keys[KeyEvent.VK_LEFT]){
			user.leftCount();
			reset();
			if (t_map.checkCollide(10,0)==false){
				if (boxx<0)
					boxx+=20;
				t_map.moveBlocks(20,0);
				user.setPPos(-20,0);
				for (int i = 0; i < t_map.getEnemies().size(); i++) {
					t_map.getEnemies().get(i).align(20, 0);
					for (int j = 0; j < t_map.getEnemies().get(i).getBullets().size(); j++) {
						t_map.getEnemies().get(i).getBullets().get(j).align(20, 0);
					}
				}
				runPowerups(20,0);
				if (bullet) {
					shoot_bullet.align(20, 0);
				}
			}	
		}
		if (keys[KeyEvent.VK_UP]){
			user.upCount();
			reset();
			if (t_map.checkCollide(0,10)==false){
				if (boxy<0)
					boxy+=20;
				t_map.moveBlocks(0,20);
				user.setPPos(0,-20);
				for (int i = 0; i < t_map.getEnemies().size(); i++) {
					t_map.getEnemies().get(i).align(0,20);
					for (int j = 0; j < t_map.getEnemies().get(i).getBullets().size(); j++) {
						t_map.getEnemies().get(i).getBullets().get(j).align(0, 20);
					}
				}
				runPowerups(0,20);
				if (bullet) {
					shoot_bullet.align(0, 20);
				}
			}
			
		}
		if (keys[KeyEvent.VK_DOWN]){
			user.downCount();
			reset();
			//user.nextLevel();////////////////////////// testing lvl change 
			if (t_map.checkCollide(0,-10)==false){
				if (boxy>-(2000-700))
					boxy-=20;
				t_map.moveBlocks(0,-20);
				user.setPPos(0,20);
				for (int i = 0; i < t_map.getEnemies().size(); i++) {
					t_map.getEnemies().get(i).align(0,-20);
					for (int j = 0; j < t_map.getEnemies().get(i).getBullets().size(); j++) {
						t_map.getEnemies().get(i).getBullets().get(j).align(0, -20);
					} 
				}
				runPowerups(0,-20);
				if (bullet) {
					shoot_bullet.align(0, -20);
				}
			}	
		}
		//to attack
		if (keys[KeyEvent.VK_SPACE]) {	
			reset(); //constantly check collisions
			shoot_bullet = new Bullet(user.get_movingDirection()); //create a new bullet in the diretion the user is currently facing
			bullet = true;
		}
		if (bullet && shoot_bullet.getDead()) { //to stop drawing bullet when it reaches its maximum range
			bullet = false;
		}
	}
	
	//method checks for collisions with Powerups and aligns the Powerups on the map
	public void runPowerups(int x, int y) {
		for (int i = 0; i < t_map.getPowerups().size(); i++) {
			t_map.getPowerups().get(i).align(x, y);
			if (t_map.getPowerups().get(i).collide()) {
				user.setLives(1); //add one life to user
				t_map.removePU(i); 
			}
		}
	}
	
	//graphics
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(mapPic,boxx,boxy,this); //draw the background
		t_map.draw(g); //draw the map
		user.draw(g); //draw the user
		if (bullet) { //draw the users bullets and move them
			shoot_bullet.draw(g);
			shoot_bullet.move();
		}
		//draw minimap
		g.setColor(Color.BLACK); //map rectangle
		g.fillRect(960,560,240,140);
        g.setColor(Color.RED); //red dot indicating player's position relative to the map (divides the pos on the map to get a smaller area)
        g.fillOval(960+user.get_px()/35,560+user.get_py()/35,10,10); //draw user on minimap
        //blue dots on the map represent the enemies
        for (int i = 0; i < t_map.getEnemies().size(); i++) {	
            g.setColor(Color.BLUE);
            //divides the enemy position with the same ratio as the users for accurate minimap
        	g.fillOval(960+t_map.getEnemies().get(i).get_staticX()/35, 560+t_map.getEnemies().get(i).get_staticY()/35, 10, 10);
        	//draw enemies
        	t_map.getEnemies().get(i).draw(g);
        	//remove enemies when they die
        	if (t_map.getEnemies().get(i).getDead()) {
        		//adjust score when enemy killed
        		adjustScore(250);
        		t_map.getEnemies().remove(i);
        	}
        }
        //update label displaying score
        adjustScore(user.get_scoreAdjustment());
        displayScore.setText("Score: "+score);
	}	
}

//MenuPanel class - used to navigate between different panels with the use of JButtons
class MenuPanel extends JPanel implements ActionListener, MouseListener {
	
	//fields	
	private Image background;
	private Image curr_hoverPic;
	private Image button_hover;
	private Image hover_settings;
	private Image hover_quit;
	private Image settingsPic, quitPic;
	private Font asianSkylineFont;
	
	//start, instructions, leaderboard, credits, settings, quit
	private JButton[] buttons = new JButton[]{new JButton("play"),new JButton("instructions"),new JButton("leaderboard"),new JButton("credits"),new JButton(),new JButton()};
		
	//flags keeping track of which button is pressed
	private boolean start = false;
	private boolean instructions = false;
	private boolean leaderboard = false;
	private boolean credits = false;
	private boolean settings = false;
	private boolean quit = false;
	
	//fields below used for effect of flower upon hovering on some of the JButtons
	private boolean hover = false;
	private int hoverx,hovery;
	private int[] hoverAlign = {130,10,20,90};
	private int[] btnSizes = {120,363,343,202};
	private int currButton; //keep track of current button being hovered over

	//for controlling the music
	private Clip clip;
	private BooleanControl muteControl;
	private FloatControl gainControl;
	
	//constructor
	public MenuPanel() throws IOException, FontFormatException{		
		setLayout(null);
		
		//load image, font, button_hover
		try {
			background = ImageIO.read(new File("Background.png"));
			button_hover = ImageIO.read(new File("hovertri.png")).getScaledInstance(70, 70, Image.SCALE_DEFAULT);
			hover_settings = ImageIO.read(new File("settingsTop.png"));
			hover_quit = ImageIO.read(new File("exitTop.png"));
			settingsPic = ImageIO.read(new File("settingsPic.png"));
			quitPic = ImageIO.read(new File("exitPic.png"));
			asianSkylineFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("Alice_in_Wonderland.ttf"))).deriveFont(Font.PLAIN,95);
		} catch (IOException e) {}	
		
		//apply formatting that applies to all buttons
		for (JButton button : buttons) {
			button.addMouseListener(this);
			button.setBackground(new Color(255,0,0));
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			button.setFocusPainted(false);
			add(button);
		}
		//for the play, instructions, leaderboard, and credits buttons
		for (int i = 0; i < 4; i++) {
			buttons[i].setFont(asianSkylineFont);
			buttons[i].setForeground(Color.WHITE);
			buttons[i].setSize(new Dimension(400,70));
			buttons[i].setLocation(410,350+i*85);
			buttons[i].setBounds(350, 350+i*85, 520, 70);
		}
		//for the settings and quit button
		for (int i = 4; i < buttons.length; i++) {
			buttons[i].setSize(new Dimension(47,47));
			buttons[i].setLocation(1057+(i-4)*58,628);
		}
				
		//music: 
		//https://stackoverflow.com/questions/25171205/playing-sound-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
		try {
	         // Open an audio input stream.           
	          File soundFile = new File("menuMusic.wav"); //you could also get the sound file with an URL
	          AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
	         // Get a sound clip resource.
	         clip = AudioSystem.getClip();
	         // Open audio clip and load samples from the audio input stream.
	         clip.open(audioIn);
	         clip.start();
	      } catch (UnsupportedAudioFileException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (LineUnavailableException e) {
	         e.printStackTrace();
	      }
		
		gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
		
	}
	
	//getters - used in Main class ActionPerformed method
	public boolean getStart() {
		return start;
	}
	public boolean getInstructions() {
		return instructions;
	}
	public boolean getLeaderboard() {
		return leaderboard;
	}
	public boolean getCredits() {
		return credits;
	}
	public boolean getSettings() {
		return settings;
	}
	public boolean getQuit() {
		return quit;
	}
	
	//setters
	public void stopClip() { //for controlling the music
		clip.stop();
	}
	public void setVolume(int g){
		if (g==-6){
			muteControl.setValue(true);
		}
		else{
			muteControl.setValue(false);
			gainControl.setValue((float)g);
		}
	}
	//used in Main class in ActionPerformed method
	public void setInstructions(boolean b) {
		instructions = b;
	}
	public void setCredits(boolean b) {
		credits = b;
	}
	public void setSettings(boolean b) {
		settings = b;
	}
	public void setLeaderboard(boolean b) {
		leaderboard = b;
	}
	//ActionListener methods
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	//MouseListener methods
	@Override
	public void mouseClicked(MouseEvent e) {
		Object source = e.getSource();
		if (source == buttons[0]) {
			start = true;
		}
		else if (source == buttons[1]) {
			instructions = true;
		}
		else if (source == buttons[2]) {
			leaderboard = true;
		}
		else if (source == buttons[3]) {
			credits = true;
		}
		else if (source == buttons[4]) {
			settings = true;
		}
		else if (source == buttons[5]) {
			quit = true;
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		Object source = e.getSource();
		
		//change the color of the buttons when the mouse hovers on them
		if (source == buttons[0]) {
			buttons[0].setForeground(new Color(255,255,0));
			hover = true;
			curr_hoverPic = button_hover;
			hoverx = (int) buttons[0].getLocation().getX()+hoverAlign[0];
			hovery = (int) buttons[0].getLocation().getY();
			currButton = 0;
		}
		else if (source == buttons[1]) {
			buttons[1].setForeground(new Color(255,255,0));
			hover = true;
			curr_hoverPic = button_hover;
			hoverx = (int) buttons[1].getLocation().getX()+hoverAlign[1];
			hovery = (int) buttons[1].getLocation().getY();
			currButton = 1;
		}
		else if (source == buttons[2]) {
			buttons[2].setForeground(new Color(255,255,0));
			hover = true;
			curr_hoverPic = button_hover;
			hoverx = (int) buttons[2].getLocation().getX()+hoverAlign[2];
			hovery = (int) buttons[2].getLocation().getY();
			currButton = 2;
		}
		else if (source == buttons[3]) {
			buttons[3].setForeground(new Color(255,255,0));
			hover = true;
			curr_hoverPic = button_hover;
			hoverx = (int) buttons[3].getLocation().getX()+hoverAlign[3];
			hovery = (int) buttons[3].getLocation().getY();
			currButton = 3;
		}
		else if (source == buttons[4]) {
			hover = true;
			curr_hoverPic = hover_settings;
			hoverx = (int) buttons[4].getLocation().getX();
			hovery = (int) buttons[4].getLocation().getY();
			currButton = 4;
		}
		else if (source == buttons[5]) {
			hover = true;
			curr_hoverPic = hover_quit;
			hoverx = (int) buttons[5].getLocation().getX();
			hovery = (int) buttons[5].getLocation().getY();
			currButton = 5;
		}
		
	}
	@Override
	//change the color of the buttons back to normal when they are not mouse hovers on them
	public void mouseExited(MouseEvent e) {
		Object source = e.getSource();
		if (source == buttons[0]) {
			buttons[0].setForeground(new Color(255,255,255));
			hover = false;
		}
		else if (source == buttons[1]) {
			buttons[1].setForeground(new Color(255,255,255));
			hover = false;
		}
		else if (source == buttons[2]) {
			buttons[2].setForeground(new Color(255,255,255));
			hover = false;
		}
		else if (source == buttons[3]) {
			buttons[3].setForeground(new Color(255,255,255));
			hover = false;
		}
		else if (source == buttons[4]) {
			hover = false;
		}
		else if (source == buttons[5]) {
			hover = false;
		}
	
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	//draw menu
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background,0,0,null);
		g.drawImage(settingsPic,1057,628,null);
		g.drawImage(quitPic,1057+58,628,null);
		if (hover) { //draw necessary hover animations only when a hover occurs
			g.drawImage(curr_hoverPic,hoverx,hovery,null);
			if (currButton < 4)
				g.drawImage(curr_hoverPic,hoverx+btnSizes[currButton]+70,hovery,null);
		}
	}
}

//InstructionsPanel - simply displays instructions with a back button used to return back to the MenuPanel
class InstructionsPanel extends JPanel implements ActionListener,MouseListener {

	//fields
	private Image background;
	private Font myFont;
	private JButton back;
	private boolean menu = false;
	
	//constructor
	public InstructionsPanel() throws FileNotFoundException, FontFormatException, IOException {
		setLayout(null);
		
		//load files
		background = ImageIO.read(new File("instructions.png"));		
		myFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("Alice_in_Wonderland.ttf"))).deriveFont(Font.PLAIN,95);
		
		//add and format back button
		back = new JButton("back");
		back.addMouseListener(this);
		back.setBackground(new Color(0,0,0,0));
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		back.setFocusPainted(false);
		back.setFont(myFont);
		back.setForeground(new Color(0,51,102));
		back.setSize(new Dimension(200,70));
		back.setLocation(0,630);
		add(back);
	}
	
	//getter
	public boolean getMenu() {
		return menu;
	}
	//setter
	public void setMenu(boolean b) {
		menu = b;
	}
	
	//methods
	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back)
			menu = true;
	}
	@Override
	public void mouseEntered(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back) 
			back.setForeground(new Color(0,0,0)); //for hover effect
	}
	@Override
	public void mouseExited(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back) 
			back.setForeground(new Color(0,51,102)); //for hover effect
	}
	@Override
	public void mousePressed(MouseEvent arg0) {	
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {		
	}
	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, null); //draw image with instructions on it
	}
}
//CreditsPanel - simply displays credits with a back button used to return back to the MenuPanel
class CreditsPanel extends JPanel implements ActionListener,MouseListener {

	//fields
	private Image background;
	private Font myFont;
	private JButton back;
	private boolean menu = false;
	
	//constructor
	public CreditsPanel() throws FileNotFoundException, FontFormatException, IOException {
		setLayout(null);
		
		//load files
		background = ImageIO.read(new File("credits.png"));		
		myFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("Alice_in_Wonderland.ttf"))).deriveFont(Font.PLAIN,95);
		
		//add and format back button
		back = new JButton("back");
		back.addMouseListener(this);
		back.setBackground(new Color(0,0,0,0));
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		back.setFocusPainted(false);
		back.setFont(myFont);
		back.setForeground(new Color(0,51,102));
		back.setSize(new Dimension(200,70));
		back.setLocation(0,630);
		add(back);
	}
	
	//getter
	public boolean getMenu() {
		return menu;
	}
	//setter
	public void setMenu(boolean b) {
		menu = b;
	}
	
	//methods
	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back)
			menu = true;
	}
	@Override
	public void mouseEntered(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back) 
			back.setForeground(new Color(0,0,0));
	}
	@Override
	public void mouseExited(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back) 
			back.setForeground(new Color(0,51,102));
	}
	@Override
	public void mousePressed(MouseEvent arg0) {	
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {		
	}
	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, null);
	}
}

//SettingPanel - used to adjust the volume of the music that plays with a back button
class SettingsPanel extends JPanel implements ActionListener,MouseListener {

	//fields
	private Image background;
	private Font myFont;
	private JButton back,upVol,downVol;
	private boolean menu = false;
    private int gain = 0;
	private boolean hover;
	private int hoverx;
	private Image currHoverPic, upVolPic,downVolPic, volUpHover,volDownHover;
	
	//constructor
	public SettingsPanel() throws FileNotFoundException, FontFormatException, IOException {
		
		setLayout(null);
		
		//load files
		background = ImageIO.read(new File("settings.png"));
		upVolPic = ImageIO.read(new File("volumeUp.png"));
		downVolPic = ImageIO.read(new File("volumeDown.png"));
		volUpHover = ImageIO.read(new File("volumeUpHover.png"));
		volDownHover = ImageIO.read(new File("volumeDownHover.png"));
				
		myFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("Alice_in_Wonderland.ttf"))).deriveFont(Font.PLAIN,95);
		
		//add and format back button
		back = new JButton("back");
		back.addMouseListener(this);
		back.setBackground(new Color(0,0,0,0));
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		back.setFocusPainted(false);
		back.setFont(myFont);
		back.setForeground(new Color(0,51,102));
		back.setSize(new Dimension(200,70));
		back.setLocation(0,630);
		add(back);
		
		upVol = new JButton();
		upVol.addMouseListener(this);
		upVol.setBackground(new Color(0,0,0,0));
		upVol.setBorderPainted(false);
		upVol.setContentAreaFilled(false);
		upVol.setFocusPainted(false);
		upVol.setSize(new Dimension(200,200));
		upVol.setLocation(150,300);
		add(upVol);
		
		downVol = new JButton();
		downVol.addMouseListener(this);
		downVol.setBackground(new Color(0,0,0,0));
		downVol.setBorderPainted(false);
		downVol.setContentAreaFilled(false);
		downVol.setFocusPainted(false);
		downVol.setSize(new Dimension(200,200));
		downVol.setLocation(850,300);
		add(downVol);
	}
	
	//getter
	public boolean getMenu() {
		return menu;
	}
	//setter
	public void setMenu(boolean b) {
		menu = b;
	}
	public int getGain(){
		return gain;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back)
			menu = true;
		else if (source==upVol){
			if (gain<6){
				gain++;
			}
			else{
				gain = 6;
			}
		}
		else if (source==downVol){
			if (gain>-6){
				gain--;
			}
			else{
				gain = -6;
			}
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back) 
			back.setForeground(new Color(0,0,0));
		else if (source==upVol){
			currHoverPic = volUpHover;
			hoverx = 150;
			hover = true;
		}
		else if (source == downVol){
			currHoverPic = volDownHover;
			hoverx = 850;
			hover = true;
		}
	}
	@Override
	public void mouseExited(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back) 
			back.setForeground(new Color(0,51,102));
		else if (source==upVol){
			hover = false;
		}
		else if (source == downVol){
			hover = false;
		}
	}
	@Override
	public void mousePressed(MouseEvent arg0) {	
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {		
	}
	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, null);
		g.drawImage(upVolPic,150,300,null);
		g.drawImage(downVolPic,850,300,null);
		if (hover){
			g.drawImage(currHoverPic,hoverx,300,null);
		}
	}
}

//LeaderboardPanel - reads from a txt file and displays the top scores
class LeaderboardPanel extends JPanel implements ActionListener,MouseListener {

	//fields
	private Image background;
	private Font myFont;
	private JButton back;
	private boolean menu = false;
	ArrayList<Integer> scores = new ArrayList<Integer>();
	ArrayList<String> names = new ArrayList<>();
	
	//constructor
	public LeaderboardPanel() throws FileNotFoundException, FontFormatException, IOException {
		setLayout(null);
		
		//load files
		background = ImageIO.read(new File("leaderboard.png"));		
		myFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("Alice_in_Wonderland.ttf"))).deriveFont(Font.PLAIN,95);
		
		//add and format back button
		back = new JButton("back");
		back.addMouseListener(this);
		back.setBackground(new Color(0,0,0,0));
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		back.setFocusPainted(false);
		back.setFont(myFont);
		back.setForeground(new Color(0,51,102));
		back.setSize(new Dimension(200,70));
		back.setLocation(0,630);
		add(back);
		
		TreeMap<Integer,String> leaderboardMap = new TreeMap<Integer,String>();
    	Scanner mapFile = new Scanner(new File("leaderboard.txt"));
   		while(mapFile.hasNextLine()){
   			leaderboardMap.put(mapFile.nextInt(),mapFile.next());
   		}
   		mapFile.close();
		//print all values using keyset
		
		for (Integer key:leaderboardMap.keySet()){
			names.add(leaderboardMap.get(key));
			scores.add(key);
		}
	}
	
	//getter
	public boolean getMenu() {
		return menu;
	}
	//setter
	public void setMenu(boolean b) {
		menu = b;
	}
	
	//methods
	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
	@Override
	public void mouseClicked(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back)
			menu = true;
	}
	@Override
	public void mouseEntered(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back) 
			back.setForeground(new Color(0,0,0));
	}
	@Override
	public void mouseExited(MouseEvent e) {	
		Object source = e.getSource();
		if (source == back) 
			back.setForeground(new Color(0,51,102));
	}
	@Override
	public void mousePressed(MouseEvent arg0) {	
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {		
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, null);
		g.setFont(myFont);
     	g.setColor(Color.BLACK);
     	//make sure there are at least 5 different scores in txt file, or else leaderboard will crash 
		g.drawString(("1: "+names.get(names.size()-1)+" , "+scores.get(scores.size()-1)),50,250);
		g.drawString(("2: "+names.get(names.size()-2)+" , "+scores.get(scores.size()-2)),50,340);
		g.drawString(("3: "+names.get(names.size()-3)+" , "+scores.get(scores.size()-3)),50,430);
		g.drawString(("4: "+names.get(names.size()-4)+" , "+scores.get(scores.size()-4)),50,520);
		g.drawString(("5: "+names.get(names.size()-5)+" , "+scores.get(scores.size()-5)),50,610);
	}
}