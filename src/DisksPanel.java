import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class DisksPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 400;
	public static final int HEIGHT = 300;
	private static final int TICKS = 50;
	
	private Timer timer;
	private ArrayList<Disk> disk;
	private int numberOfDisks;
	private Point tracked;
	private Point clicked;
	private Random rand;
	private Disk ghostDisk;
	private Disk newDisk;
	private int ticks;
	private int gamemode;
	private FileReader filereader;
	private BufferedReader reader;
	private FileWriter filewriter;
	private BufferedWriter writer;
	private int[] scoreboard;
	private int gamemodeRecall;
	
	public DisksPanel() {
		
		// constructors/initialization:
		disk = new ArrayList<Disk>();
		numberOfDisks = 0;
		rand = new Random();
		
		tracked = new Point();
		clicked = new Point();
		
		ticks = TICKS;
		gamemode = 0;
		// GAMEMODE legend:
		// 		0: game start
		// 		1: peaceful
		// 		2: regular
		// 		3: panic
		// 		4: game over
		
		// check the scoreboard
		scoreboard = new int[3];
		File f = new File("scoreboard.txt");
		// if the scoreboard file is missing, try to create a new one.
		if (f.exists() == false) {
			try {
				f.createNewFile();
			// if file creation fails, initialize the scoreboard as all zeroes.
			} catch (IOException e) {
				for (int i = 0; i < 3; i++) {
					scoreboard[i] = 0;
				}
			}
		}
		else {
			for (int i = 0; i < 3; i++) {
				try {
					filereader = new FileReader("scoreboard.txt");
					reader = new BufferedReader(filereader);
					try {
						// get the ith highscore and add it to the scoreboard
						for (int j = 0; j <= i; j++) {
							scoreboard[i] = Integer.valueOf(reader.readLine());
						}
					} finally {
						reader.close();
					}
				// if a read exception occurs, initialize the scoreboard as all zeroes.
				} catch (FileNotFoundException e) {
					scoreboard[i] = 0;
				} catch (IOException e) {
					scoreboard[i] = 0;
				} catch (NullPointerException e) {
					scoreboard[i] = 0;
				} catch (NumberFormatException e) {
					scoreboard[i] = 0;
				} 
			}
		}
		
		
		// create a random ghost disk
		DiskColor newDiskColor = DiskColor.values()[rand.nextInt(15)];
		ghostDisk = new Disk(tracked.x, tracked.y, rand.nextInt(65) + 5, newDiskColor);
		
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.GRAY);
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseClick(e);
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				handleMouseMove(e);
			}
		});
		
		// Schedule timer events to fire every 100 milliseconds (0.1 seconds)
		this.timer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleTimerEvent(e);
			}
		});
	}

	// You can call this method to convert a DiskColor value into
	// a java.awt.Color object.
	public Color colorOf(DiskColor dc) {
		return new Color(dc.red(), dc.green(), dc.blue());
	}

	// This method is called whenever the mouse is moved
	protected void handleMouseMove(MouseEvent e) {
		tracked.x = e.getX();
		tracked.y = e.getY();
		repaint();
	}
	
	// This method is called whenever the mouse is clicked
	protected void handleMouseClick(MouseEvent e) {
		clicked.x = e.getX();
		clicked.y = e.getY();
		repaint();
		
		// player select difficulty screen
		if (gamemode == 0) {
			if (((90 < tracked.x) && (tracked.x < 155)) && ((115 < tracked.y) && (tracked.y < 150))) {
				ticks = 0;
				gamemode = 1;				// change gamemode to "Zen"; timer is zero
				gamemodeRecall = gamemode;
			} else if (((90 < tracked.x) && (tracked.x < 200)) && ((155 < tracked.y) && (tracked.y < 190))) {
				ticks = TICKS;
				gamemode = 2;				// change gamemode to "Normal"; reset timer
				gamemodeRecall = gamemode;
			} else if (((90 < tracked.x) && (tracked.x < 180)) && ((195 < tracked.y) && (tracked.y < 230))) {
				ticks = TICKS;
				gamemode = 3;				// change gamemode to "Panic"; reset timer
				gamemodeRecall = gamemode;
			}
		}
		
		// play game!
		else if (gamemode != 0 && gamemode != 4){
			// pause the timer before running tests...
			timer.stop();
			
			// try to create a new disk at the ghost's location
			newDisk = new Disk(clicked.x, clicked.y, ghostDisk.getRadius(), ghostDisk.getColor()); 
			
			// out of bounds
			if (newDisk.isOutOfBounds(WIDTH, HEIGHT) == true) {
				gamemode = 4;
			} 
			// overlaps
			for (int i = 0; i < numberOfDisks; i++) {
				if (newDisk.overlaps(disk.get(i))) {
					gamemode = 4;
				}
			}
			
			// move is legal
			if (gamemode != 4) {
				// add the new (legal) disk to the array
				disk.add(numberOfDisks, newDisk);
				numberOfDisks++;
				
				// randomize the new ghost disk
				DiskColor newDiskColor = DiskColor.values()[rand.nextInt(15)];
				ghostDisk = new Disk(tracked.x, tracked.y, rand.nextInt(65) + 5, newDiskColor);
				
				// start the timer
				if (gamemode != 1) {
					// normal mode
					if (gamemode == 2) {
						ticks = TICKS;
					
					// panic mode (gets faster, consistent when down to 2 sec)
					} else if (gamemode == 3) {
						if ((TICKS - 20) < numberOfDisks) {
							ticks = 20;
						} else {
							ticks = TICKS - numberOfDisks;
						}
					}
					timer.start();
				}
			}
		}
		
		// illegal move. game over!
		else if (gamemode == 4) {
			// new high score?
			boolean highscore = false;
			// "gamemode recall" remembers the last gamemode before the "game over" (gamemode 4) screen.
			if (scoreboard[gamemodeRecall - 1] < numberOfDisks) {
				highscore = true;
				scoreboard[gamemodeRecall - 1] = numberOfDisks;
			}
			
			File f = new File("scoreboard.txt");
			// if the scoreboard exists, try to write the new high score to it
			if (highscore && f.exists() == true) {
				try {
					try {
						filewriter = new FileWriter("scoreboard.txt");
						writer = new BufferedWriter(filewriter);
						for (int i = 0; i < 3; i++) {
							writer.write(String.valueOf(scoreboard[i]));
							writer.newLine();
						}
					} finally {
						writer.close();
					}
				// if an exception occurs, don't save the new high score and move on.
				} catch (FileNotFoundException ex) {
				} catch (IOException ex) {
				} catch (NullPointerException ex) {
				}
			}
			// if player selects "try again," clear score, remove disks and restart game
			if (((110 < clicked.x) && (clicked.x < 275)) && ((160 < clicked.y) && (clicked.y < 205))) {
				timer.stop();
				
				ArrayList<Disk> toRemove = new ArrayList<Disk>();
				for (int i = 0; i < numberOfDisks; i++) {
					toRemove.add(disk.get(i));
				}
				disk.removeAll(toRemove);
				numberOfDisks = 0;
				
				ticks = TICKS;
				highscore = false;
				gamemode = 0;
			}
		}	
	}
	
	// This method is called whenever a timer event fires
	protected void handleTimerEvent(ActionEvent e) {
		// timer runs out
		if (ticks == 0 && gamemode != 1) {
			gamemode = 4;
		} else {
			ticks--;
		}
		repaint();
	}
	
	private static final Font FONT = new Font("Dialog", Font.BOLD, 40);
	private static final Font FONTSMALL = new Font("Dialog", Font.BOLD, 25);
	private static final Font FONTREALLYSMALL = new Font("Dialog", Font.BOLD, 15);

	// This method is called automatically whenever the contents of
	// the window need to be redrawn.
	@Override
	public void paintComponent(Graphics g) {
		// Paint the window background
		super.paintComponent(g);
		g.setFont(FONT);
		
		// player select difficulty screen
		if (gamemode == 0) {
			g.setFont(FONTSMALL);
			g.setColor(Color.BLACK);
			g.drawString("Select a Difficulty:", 80, 80);
			g.drawString("Zen", 100, 140);
			g.drawString("Normal", 100, 180);
			g.drawString("Panic!", 100, 220);
			if (((90 < tracked.x) && (tracked.x < 155)) && ((115 < tracked.y) && (tracked.y < 150))) {
				g.setColor(Color.RED);
				g.drawString("Zen", 100, 140);			// highlight "Zen" + display highscore
				g.setFont(FONTREALLYSMALL);
				g.setColor(Color.BLACK);
				g.drawString("Highscore:  " + String.valueOf(scoreboard[0]), 225, 250);
			} else if (((90 < tracked.x) && (tracked.x < 200)) && ((155 < tracked.y) && (tracked.y < 190))) {
				g.setColor(Color.RED);
				g.drawString("Normal", 100, 180);		// highlight "Normal" + display highscore
				g.setFont(FONTREALLYSMALL);
				g.setColor(Color.BLACK);
				g.drawString("Highscore:  " + String.valueOf(scoreboard[1]), 225, 250);
			} else if (((90 < tracked.x) && (tracked.x < 180)) && ((195 < tracked.y) && (tracked.y < 230))) {
				g.setColor(Color.RED);
				g.drawString("Panic!", 100, 220);		// highlight "Panic" + display highscore
				g.setFont(FONTREALLYSMALL);
				g.setColor(Color.BLACK);
				g.drawString("Highscore:  " + String.valueOf(scoreboard[2]), 225, 250);
			}
		} else {
			for (int i = 0; i < numberOfDisks; i++) {
				// paint existing disks:
				Color color = colorOf(disk.get(i).getColor());
				g.setColor(color);
				int ghostX = (int) (disk.get(i).getX()) - (int) ((disk.get(i).getRadius()) / 2); 
				int ghostY = (int) (disk.get(i).getY()) - (int) ((disk.get(i).getRadius()) / 2); 
				g.fillOval(ghostX, ghostY, (int) disk.get(i).getRadius(), (int) disk.get(i).getRadius());
				
				//paint counter:
				g.setColor(Color.BLACK);
				String s = String.valueOf(numberOfDisks);
				g.drawString(s, 300, 250);
			}
			
			// paint timer bar:
			int barLength;
			if (gamemode == 2) {
				// on normal mode, speed is consistent
				barLength = (int) (380 * ((double) ticks / TICKS));
			} else {
				// on panic mode, bar speeds up
				if ((TICKS - 20) < numberOfDisks) {
					barLength = (int) (380 * ((double) ticks / 20));
				} else {
					barLength = (int) (380 * ((double) ticks / (TICKS - numberOfDisks)));
				}
			}
			// bar changes color! would implement this commented-out feature but it slows down the game too much.
			//Color barColor = new Color((int) ( 255 * (double) ((380. - barLength) / 380.)), (int) (	255 * (double) (barLength / 380.)), 0, 50);
			Color barColor = new Color(255, 10, 10, 50);
			g.setColor(barColor);
			g.fillRect(10, 270, barLength, 20);
			
			// paint ghost (new) disk:
			if (gamemode != 4) {
				g.setColor(Color.BLACK);
				int ghostX = (tracked.x) - (int) ((ghostDisk.getRadius() / 2)); 
				int ghostY = (tracked.y) - (int) ((ghostDisk.getRadius() / 2)); 
				g.drawOval(ghostX, ghostY, (int) ghostDisk.getRadius(), (int) ghostDisk.getRadius());
			}
			
			// GAME OVER screen
			else {
				g.setColor(Color.BLACK);
				g.drawString("Game Over!", 75, 150);
				g.setFont(FONTSMALL);
				g.drawString("Play Again?", 120, 190);
				if (((110 < tracked.x) && (tracked.x < 275)) && ((160 < tracked.y) && (tracked.y < 205))) {
					g.setColor(Color.RED);
					g.drawString("Play Again?", 120, 190);		// highlight "Try Again?"
				}
			}
		}
	}
}
