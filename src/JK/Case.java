package JK;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @version 1.0
 * @author khal0j
 */

/**
 * Classe case qui représente une case dans le démineur
 */
public class Case extends JPanel implements MouseListener {
    private String value;
    private final static int DIM = 50;
    private GUI gui;
    private int state = 0; // chaque case a 3 etats : 0 = non ouverte, 1 = ouverte, 2 = drapeau
    private int X; // coordonnee X de la case
    private int Y; // coordonnee Y de la case
    private Color color;
    private boolean isClickable = true;

    /**
     * Constructeur de la classe Case
     * 
     * @param val un String qui indique la valeur dans la case
     * @param gui un GUI qui est l'interface graphique du jeu
     */
    public Case(String val, GUI gui, int dimX, int dimY) {
        setPreferredSize(new Dimension(DIM, DIM));
        addMouseListener(this);
        this.value = val;
        this.gui = gui;
        this.X = dimX;
        this.Y = dimY;
        this.state = 0;
    }

    /**
     * Fonction qui permet de dessiner la case
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // ADDING THE IMAGE
        if (state == 0) {
            try {
                Image image = ImageIO
                        .read(Objects.requireNonNull(getClass().getResourceAsStream("images/case.png")));
                g.drawImage(image, 0, 0, gui);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (state == 1) {
            if (this.value.equals("X")) {
                try {
                    Image image = ImageIO
                            .read(Objects.requireNonNull(getClass().getResourceAsStream("images/mine.png")));
                    g.drawImage(image, 0, 0, gui);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, DIM / 2));
                if (gui.isMultiplayer())
                    g.setColor(color);
                else if (value.equals("0")) {
                    g.setColor(Color.PINK);
                } else if (value.equals("1")) {
                    g.setColor(Color.GREEN);
                } else if (value.equals("2")) {
                    g.setColor(Color.BLUE);
                } else if (value.equals("3")) {
                    g.setColor(Color.RED);
                } else if (value.equals("4")) {
                    g.setColor(Color.CYAN);
                } else if (value.equals("5")) {
                    g.setColor(Color.MAGENTA);
                } else if (value.equals("6")) {
                    g.setColor(Color.ORANGE);
                } else if (value.equals("7")) {
                    g.setColor(Color.DARK_GRAY);
                } else if (value.equals("8")) {
                    g.setColor(Color.BLACK);
                }
                g.drawString(value, 18, 30);
                gui.incrementCasesOpened();
            }
        } else {
            try {
                Image image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("images/flag.png")));
                g.drawImage(image, 0, 0, gui);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * mouseClick listener, change le state de la case selon le bouton de la souris
     * (gauche ou droite) et gere le comportement de la case selon MouseEvent
     * 
     * @param e un MouseEvent qui est l'evenement de la souris
     */

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isClickable)
            return;
        else if (gui.isMultiplayer() && state == 0) {
            gui.sendCoordinates(X, Y);
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (state == 0) {
                if (!this.value.equals("X")) {
                    gui.initCascade(X, Y);
                } else {
                    openCase();
                    gui.gameLost();
                }
                state = 1;
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (state == 0) {
                if (this.value.equals("X")) {
                    gui.incrementMinesTrouvees();
                    gui.incrementCasesOpened();
                }
                state = 2;
                repaint();
            } else if (state == 2) {
                if (this.value.equals("X")) {
                    gui.decrementMinesTrouvees();
                    gui.decrementCasesOpened();
                }
                state = 0;
                repaint();
            }
        }
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
    }

    /**
     * Fonction qui nous permet d'ouvrir la case
     */
    void openCase() {
        state = 1;
        repaint();
    }

    /**
     * Fonction getter pour l'etat de la case
     */
    int getState() {
        return state;
    }

    /**
     * Fonction getter pour la valeur de la case
     */
    String getValue() {
        return this.value;
    }

    /**
     * Fonction setter pour rendre la case cliquable ou non
     * 
     * @param flag un boolean qui indique si la case est cliquable ou non
     */
    void setClickable(boolean flag) {
        isClickable = flag;
    }

    /**
     * Fonction qui permet d'ouvrir la case en mode multijoueur
     * 
     * @param value la valeur de la case
     * @param color la couleur de la case
     */
    void openCaseMultiplayer(String value, String color) {
        this.state = 1;
        this.value = value;
        this.color = Color.decode(color);
        repaint();
    }
}
