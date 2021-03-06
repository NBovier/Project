package projet2k18;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

public class DotUnlockPanel extends JPanel implements Runnable {
	
	// D�claration des constantes
	private final Color INK = new Color(255, 255, 255);
	private final Color DOT = Color.WHITE;

	// D�claration des tableaux/Listes
	private boolean trues[] = new boolean[9];
	private Rectangle rect[] = new Rectangle[9];
	private int pattern[] = new int[9];
	private List<Line2D.Double> lines = new ArrayList<>();

	// D�claration des variables
	private Thread th;
	private Graphics2D g;
	private int startx, starty, endx, endy, enddx, enddy;
	private int end, start, index = 1, stroke = 2, time = 5;
	private int incw = 10, oncw;
	private boolean drawing = false;
	private String patt = "";
	private Timer timer;
	private JLabel output;
	private String finalPattern = "";
	
	public class RoundButton extends JButton {
		 
		  public RoundButton(String text) {
			super(text);
		    setFocusPainted(false);
		    setBackground(Color.BLACK);
		  }
		 
		  /**
		   * d�termine si le point (x, y) est � l'int�rieur de l'ic�ne circulaire
		   */
		  public boolean contains(int x, int y) {
			return drawing;
		    
		  }
		}
	
	public DotUnlockPanel() {
		
		
		try {
			oncw = incw + 40;
			th = new Thread(this);
			setOpaque(false);
			
			RoundButton btnNewButton = new RoundButton("hihihi");
			btnNewButton.setBackground(Color.CYAN);
			add(btnNewButton);
			
			ActionListener al = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					if (time == 0) {
						//check si le mod�le dessin� correspond au mod�le enregistr�
						for(int numDot : pattern ) {
							if(numDot > 0) {
								finalPattern += Integer.toString(numDot);
							}
						}
						//Test conditionnel si le schema dessin� correspond au sch�ma sauvegard�
						if(compareSchemeCode()) {
							//TODO :
							//	switcher vers le JPanel HomeScreenPanel
							//JFrame topFrame = (JFrame) getParent();
							//topFrame.setVisible(false);
							//topFrame.add(new HomeScreenPanel());
							PhoneFrame pf = new PhoneFrame();
							pf.setPanel(new HomeScreenPanel());
						}
						else {
							//Si le code ne correspond pas on reset l'�cran
							resetScreen();
						}
						
						timer.stop();
					}
					--time;
				}
			};

			timer = new Timer(1000, al);
			// Listener starts
			MouseListener ml = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent me) {
					resetScreen();
					index = 1;
					startx = me.getX();
					starty = me.getY();
					for (int i = 0; i < rect.length; ++i) {
						if (rect[i].contains(me.getPoint())) {
							startx = (int) rect[i].getCenterX();
							starty = (int) rect[i].getCenterY();
							endx = startx;
							endy = starty;
							trues[i] = true;
							pattern[0] = i+1;
							start = i;
							drawing = true;
							break;
						}
					}
				}

				@Override
				public void mouseReleased(MouseEvent me) {
					drawing = false;
					printPattern();
					time = 2;
					timer.start();
				}
			};

			MouseMotionListener mll = new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent me) {
					if (drawing == true) {
						endx = me.getX();
						endy = me.getY();
						for (int i = 0; i < rect.length; ++i) {
							if (trues[i] != true) {
								if (rect[i].contains(me.getPoint())) {
									index++;
									
									//cr�er la ligne de centre du point vers le centre du point suivant
									enddx = (int) rect[i].getCenterX();
									enddy = (int) rect[i].getCenterY();
									lines.add(new Line2D.Double(startx, starty, enddx, enddy));
									
									// la prochaine ligne commence au centre du point actuel
									startx = enddx;
									starty = enddy;
									
									
									end = i;

									start = i;
									trues[i] = true;
									pattern[index] = i+1;
									break;
								}
							}
						}
					}
				}
			};

			addMouseListener(ml);
			addMouseMotionListener(mll);
			// Listener ends
			th.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void paintComponent(Graphics g) {
		
		/*
		try {
			Image img = ImageIO.read(new File("wallpaper.jpg"));
			g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	public void paint(Graphics g2) {
		// Graphics2D est un enfant de Graphics qui permet de dessiner mieux
		g = (Graphics2D) g2;
		// Am�liore la qualit� de l'affichage
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		super.paint(g);

		int ind = 0;

		// Cr�ation du trait
		g.setColor(INK); // Couleur
		g.setStroke(new BasicStroke(incw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // Forme du trait

		// Dessine les lignes entre le point et le curseur
		if (drawing == true) {
			g.drawLine(startx, starty, endx, endy);
		}

		// Dessine les lignes entre les points
		for (int i = 0; i < lines.size(); ++i) {
			g.draw(lines.get(i));
		}

		// Cr�ation du rond autour de point
		g.setColor(DOT); // Couleur
		g.setStroke(new BasicStroke(stroke)); // taille

		// Affichage centr� des points
		for (int i = (getWidth() / 3) / 2; i < getWidth(); i += getWidth() / 3) {
			for (int j = (getHeight() / 3) / 2; j < getHeight(); j += getHeight() / 3) {

				// Dessine les points
				g.fillOval(i - incw / 2, j - incw / 2, incw, incw); // i-incw / 2 permet de centrer

				// Si le curseur est dans la zone du point on dessine un rond autour
				if (trues[ind] == true) {
					g.drawOval(i - oncw / 2, j - oncw / 2, oncw, oncw);
				}

				rect[ind] = new Rectangle();
				rect[ind].setLocation(i - (oncw) / 2, j - (oncw) / 2);
				rect[ind].setSize(oncw + stroke / 2, oncw + stroke / 2);
				ind++;
			}
		}
	}
	
	private Boolean compareSchemeCode() {
		File file = new File("scheme");
		FileReader fr;
		String str = "";
		
		try {
			fr = new FileReader(file);
			int i = 0;
			
			while((i = fr.read()) != -1) {
				str += (char)i;
			}
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		if(str.equals(finalPattern)) {
			return true;
		}
		return false;
	}

	private void resetScreen() {
		if (timer.isRunning()) {
			timer.stop();
		}
		
		
		
		clearPattern();
		lines.clear();
		makeFalse();
	}

	private void makeFalse() {
		for (int i = 0; i < trues.length; ++i) {
			trues[i] = false;
		}
	}

	private void printPattern() {
		String s = "";
		for (int i = 0; i < pattern.length; ++i) {
			s += "," + pattern[i];
		}
		patt = s.substring(1);
		//System.out.println("Pattern = " + patt);
		//output.setText("Pattern = " + patt);
	}

	public void setOutputComponent(JLabel l) {
		output = l;
	}

	private void clearPattern() {
		for (int i = 0; i < pattern.length; ++i) {
			pattern[i] = 0;
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(10);
				repaint();
			}
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
	
}

