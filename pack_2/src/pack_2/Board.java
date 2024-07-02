package pack_2;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;


public class Board extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final int B_WIDTH = 500;
	private final int B_HEIGHT = 500;
	private final int DOT_SIZE = 10;
	private final int ALL_DOTS = 2400;

	private final int x[] = new int[ALL_DOTS];
	private final int y[] = new int[ALL_DOTS];

	private int dots;
	private int apple_x;
	private int apple_y;
	private int bad_x;
	private int bad_y;
	private int score = 0;
	private int level = 1;
	private int count = 0;

	private boolean leftDirection = false;
	private boolean rightDirection = true;
	private boolean upDirection = false;
	private boolean downDirection = false;
	private boolean inGame = true;
	private boolean isPaused = false;
	private JButton nextLevelButton;
	private float fadeOpacity = 1.0f;

	private PowerUp power;
	private Image body;
	private Image turn;
	private Image tail;
	private Image apple;
	private Image badApple;
	private Image head;


	public Board() {
		initBoard();
	}

	private void initBoard() {
		addKeyListener(new TAdapter());
		setBackground(Color.blue);
		setFocusable(true);

		setPreferredSize(new Dimension(B_WIDTH + 4, B_HEIGHT + 40));
		loadImages();
		initGame();
	}

	private void loadImages() {
		ImageIcon iid = new ImageIcon("src/resources/dot.png");
		body = iid.getImage();

		ImageIcon iia = new ImageIcon("src/resources/apple-pink-lady.png");
		apple = iia.getImage();

		ImageIcon iitn = new ImageIcon("src/resources/turn.png");
		turn = iitn.getImage();

		ImageIcon iit = new ImageIcon("src/resources/tail.png");
		tail = iit.getImage();

		ImageIcon iib = new ImageIcon("src/resources/bad-apple.png");
		badApple = iib.getImage();

		ImageIcon iih = new ImageIcon("src/resources/head.png");
		head = iih.getImage();
	}

	private void initGame() {
	    dots = 3;

	    for (int z = 0; z < dots; z++) {
	        x[z] = 50 - z * 10;
	        y[z] = 50;
	    }

	    locateApple();

	    power = new PowerUp(this);
	    power.startTimer();

	   

	    
	    
	    
	}

	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);

	    g.setColor(Color.black);
	    g.fillRect(2, 2, B_WIDTH, B_HEIGHT);

	    if (isPaused) {
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeOpacity));
	        g2d.setColor(Color.gray);
	        g2d.fillRect(2, 2, B_WIDTH - 5, B_HEIGHT - 5);
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	        pauseScreen(g); // Make sure this line is called when isPaused is true
	    } else {
	        doDrawing(g);
	    }
	}

	private boolean checkTurn(int z) {
		if (x[z - 1] == x[z + 1] || y[z - 1] == y[z + 1]) {
			return false;
		} else {
			return true;
		}
	}

	private void doDrawing(Graphics g) {
		if (inGame) {
			g.drawImage(apple, apple_x, apple_y, this);
			if(score >= 1000) {
				g.drawImage(badApple, bad_x, bad_y, this);
			}

			for (int z = 0; z < dots; z++) {
				if (z == 0) {
					drawRotatedImage(g, head, x[z], y[z], getDirection(z));
				} else if (z == dots - 1) {
					drawRotatedImage(g, tail, x[z], y[z], getDirection(z));
				} else if (z > 0 && z < dots - 1) {
					if (checkTurn(z)) {
						drawTurnImage(g, turn, x[z], y[z], getTurnAngle(z));
					} else {
						drawRotatedImage(g, body, x[z], y[z], getDirection(z));
					}
				} else {
					drawRotatedImage(g, body, x[z], y[z], getDirection(z));
				}
			}

			drawScore(g);
			drawLevel(g);

			Toolkit.getDefaultToolkit().sync();
			
		} else {
			gameOver(g);
		}
	}

	private void drawRotatedImage(Graphics g, Image img, int x, int y, int angle) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform original = g2d.getTransform();
		g2d.rotate(Math.toRadians(angle), x + DOT_SIZE / 2, y + DOT_SIZE / 2);
		g2d.drawImage(img, x, y, this);
		g2d.setTransform(original);
	}

	private int getTurnAngle(int z) {
		String pos1 = null;
		String pos2 = null;

		if (x[z - 1] == x[z] && y[z - 1] > y[z]) {
			pos1 = "bottom";
		} else if (x[z - 1] == x[z] && y[z - 1] < y[z]) {
			pos1 = "top";
		} else if (x[z - 1] > x[z] && y[z - 1] == y[z]) {
			pos1 = "right";
		} else if (x[z - 1] < x[z] && y[z - 1] == y[z]) {
			pos1 = "left";
		}

		if (x[z + 1] == x[z] && y[z + 1] > y[z]) {
			pos2 = "bottom";
		} else if (x[z + 1] == x[z] && y[z + 1] < y[z]) {
			pos2 = "top";
		} else if (x[z + 1] > x[z] && y[z + 1] == y[z]) {
			pos2 = "right";
		} else if (x[z + 1] < x[z] && y[z + 1] == y[z]) {
			pos2 = "left";
		}

		if ((pos1.equals("top") && pos2.equals("right")) || (pos1.equals("right") && pos2.equals("top"))) {
			return 270;
		}
		if ((pos1.equals("bottom") && pos2.equals("right")) || (pos1.equals("right") && pos2.equals("bottom"))) {
			return 0;
		}
		if ((pos1.equals("bottom") && pos2.equals("left")) || (pos1.equals("left") && pos2.equals("bottom"))) {
			return 90;
		}
		if ((pos1.equals("top") && pos2.equals("left")) || (pos1.equals("left") && pos2.equals("top"))) {
			return 180;
		}
		return 0;
	}

	private void drawTurnImage(Graphics g, Image img, int x, int y, int angle) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform original = g2d.getTransform();
		g2d.rotate(Math.toRadians(angle), x + DOT_SIZE / 2, y + DOT_SIZE / 2);
		g2d.drawImage(img, x, y, this);
		g2d.setTransform(original);
	}

	private int getDirection(int z) {
		if (z == 0) {
			if (leftDirection)
				return 270;
			if (rightDirection)
				return 90;
			if (upDirection)
				return 0;
			if (downDirection)
				return 180;
		} else {
			if (x[z] < x[z - 1])
				return 270;
			if (x[z] > x[z - 1])
				return 90;
			if (y[z] < y[z - 1])
				return 0;
			if (y[z] > y[z - 1])
				return 180;
		}
		return 0;
	}

	private void drawScore(Graphics g) {
		String s = "Score: " + score;
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);

		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(s, (B_WIDTH +1 - metr.stringWidth(s)) / 2, B_HEIGHT + 15);
	}

	private void drawLevel(Graphics g) {
		String l = "Level: " + level;
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);

		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(l, (B_WIDTH +1 - metr.stringWidth(l)) / 2, B_HEIGHT + 35);
	}
	
	private void pauseScreen(Graphics g) {
		String p = "Paused";
		String p2 = "Press 'P' to resume";
		Font mid = new Font("Helvetica", Font.BOLD, 18);
		Font small = new Font("Helvetica", Font.PLAIN, 14);
		FontMetrics metr1 = getFontMetrics(mid);
		FontMetrics metr2 = getFontMetrics(small);
		
		g.setColor(Color.white);
		g.setFont(mid);
		g.drawString(p, (B_WIDTH+1 - metr1.stringWidth(p)) / 2, B_HEIGHT/2-40);
		g.setFont(small);
		g.drawString(p2, (B_WIDTH+1 - metr2.stringWidth(p2)) / 2,B_HEIGHT/2);
		
	}

	private void gameOver(Graphics g) {
		String msg = "Game Over";
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);

		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
	}

	private void checkApple() {
		if ((x[0] == apple_x) && (y[0] == apple_y)) {
			dots++;
			score += 100;
			count++;
			if (count==10) {
				count = 0;
				nextLevel();
			} 
			locateApple();
			if(level>=2) {
				locateBadApple();
			}
		}
	}

	
	
	private int[] checkOverlap(int xValue, int yValue) {
		for(int z=0; z<x.length;z++) {
			if (x[z]==xValue&&y[z]==yValue) {
				int r = (int) (Math.random()* 49);
				xValue = ((r * DOT_SIZE));

				r = (int) (Math.random() * 49+1);
				yValue = ((r * DOT_SIZE));
			}
		}
		return new int[] {xValue,yValue};
	}

	private void move() {
		for (int z = dots; z > 0; z--) {
			x[z] = x[(z - 1)];
			y[z] = y[(z - 1)];
		}

		if (leftDirection) {
			x[0] -= DOT_SIZE;
		}

		if (rightDirection) {
			x[0] += DOT_SIZE;
		}

		if (upDirection) {
			y[0] -= DOT_SIZE;
		}

		if (downDirection) {
			y[0] += DOT_SIZE;
		}
	}

	private void checkCollision() {
		for (int z = dots; z > 0; z--) {
			if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
				inGame = false;
			}
		}
			if ((x[0] == bad_x) && (y[0] == bad_y)) {
			
			inGame=false;
		}

		if (y[0] >= B_HEIGHT+2) {
			inGame = false;
		}

		if (y[0] < 2) {
			inGame = false;
		}

		if (x[0] >= B_WIDTH+2) {
			inGame = false;
		}

		if (x[0] < 2) {
			inGame = false;
		}

		if (!inGame) {
			power.stopTimer();
		}
	}

	private void locateApple() {
		int r = (int) (Math.random()* 49);
		apple_x = ((r * DOT_SIZE));

		r = (int) (Math.random() * 49+1);
		apple_y = ((r * DOT_SIZE));
		
		int[]	coord=	checkOverlap(apple_x,apple_y);
		apple_x=coord[0];
		apple_y=coord[1];
		
		
	}

	

	private void locateBadApple() {
		int r = (int) (Math.random() * 49);
		bad_x = ((r * DOT_SIZE));

		r = (int) (Math.random() * 49+1);
		bad_y = ((r * DOT_SIZE));
		
	}

	private void pauseGame() {
	    isPaused = true;
	    power.stopTimer();
	    fadeOpacity = 0.5f;
	    
	}

	public void resumeGame() {
	    isPaused = false;
	    power.startTimer();
	    fadeOpacity = 1.0f;
	    
	    if (nextLevelButton != null && nextLevelButton.isVisible()) {
	        nextLevelButton.setVisible(false);
	        this.remove(nextLevelButton); // Remove the button from the panel
	    }
	}

	private void nextLevel() {
	    level++;
	    dots++;
	    
	    pauseGame();
	    nextLevelButton = power.nextButton(B_WIDTH, B_HEIGHT);
	    this.setLayout(null);
	    this.add(nextLevelButton);
	    nextLevelButton.setVisible(true); // Ensure button is visible
	    power.changeSpeed();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (inGame) {
			checkApple();
			
			
			checkCollision();
			move();
			
		}
		repaint();
	}

	private class TAdapter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
		    int key = e.getKeyCode();

		    if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
		        leftDirection = true;
		        upDirection = false;
		        downDirection = false;
		    }

		    if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
		        rightDirection = true;
		        upDirection = false;
		        downDirection = false;
		    }

		    if ((key == KeyEvent.VK_UP) && (!downDirection)) {
		        upDirection = true;
		        rightDirection = false;
		        leftDirection = false;
		    }

		    if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
		        downDirection = true;
		        rightDirection = false;
		        leftDirection = false;
		    }
		    if (key == KeyEvent.VK_P) {
		        isPaused = !isPaused;
		        if (isPaused) {
		            pauseGame();
		        } else {
		            resumeGame();
		        }
		        repaint(); // Add this line to trigger a repaint when P is pressed
		    }
		}
	}
}


