package JK;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/**
 * @version 1.0
 * @author khal0j
 */

/**
 * Classe qui permet de gerer l'interface du serveur
 */

public class ServerGUI extends JPanel {
    private JMenuBar menuBar = new JMenuBar();
    private JMenu options = new JMenu("Options");
    private JMenu diffSubMenu = new JMenu("Difficulty");
    JMenuItem quit = new JMenuItem("Quit", KeyEvent.VK_Q);

    private JPanel grid = new JPanel(new GridLayout(1, 1));

    private Server server;

    private JLabel usernameLab;
    private JLabel scoreLab;
    private JLabel timerLab = new JLabel("00:00");

    private JButton start = new JButton("Start game");

    private Timer timer;
    private int seconds = 0;
    private int minutes = 0;

    private ArrayList<String> usernames = new ArrayList<String>();
    private ArrayList<String> colors = new ArrayList<String>();
    private ArrayList<Integer> scores = new ArrayList<Integer>();

    /**
     * Constructeur de la classe ServerGUI
     * 
     * @param s le serveur associe a cette interface
     */
    public ServerGUI(Server s) {
        server = s;
        setLayout(new BorderLayout());
        initMenuBar();
        initButtons();
        initGrid();
        initTimer();

        timer.start();
    }

    /**
     * Methode qui initialise la barre de menu
     */
    private void initMenuBar() {
        for (Level level : Level.values()) {
            try {
                ImageIcon icon = new ImageIcon(ImageIO.read(
                        Objects.requireNonNull(getClass().getResourceAsStream("images/" + level.toString() + ".png"))));
                JMenuItem levelMenuItem = new JMenuItem(level.toString(), icon);
                // L'accelerateir est le premier chiffre du niveau
                levelMenuItem.addActionListener(e -> server.setDifficulty(level, false));
                diffSubMenu.add(levelMenuItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ImageIcon quitIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/quit.png"))));
            quit.setIcon(quitIcon);

            ImageIcon diffIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/difficulty.png"))));
            diffSubMenu.setIcon(diffIcon);

            ImageIcon timerIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/timer.png"))));
            timerLab.setIcon(timerIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        diffSubMenu.setMnemonic(KeyEvent.VK_D);
        diffSubMenu.getItem(0).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        diffSubMenu.getItem(1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        diffSubMenu.getItem(2).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        diffSubMenu.getItem(3).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

        options.add(diffSubMenu);
        options.addSeparator();
        options.setMnemonic(KeyEvent.VK_O);
        options.add(quit);

        quit.addActionListener(e -> server.manageServerquit());
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        menuBar.add(options);
        server.setJMenuBar(menuBar);
    }

    /**
     * Methode qui initialise les boutons
     */
    private void initButtons() {
        start.addActionListener(e -> {
            server.startGame();
        });
        add(start, BorderLayout.SOUTH);
    }

    /**
     * Methode qui initialise la grille
     */
    private void initGrid() {
        JLabel username = new JLabel("User 1");
        username.setForeground(Color.RED);
        grid.add(username);
        add(grid, BorderLayout.CENTER);
    }

    /**
     * Methode qui permet de rafraichir la grille
     * 
     * @param username le nom de l'utilisateur
     * @param color    la couleur de l'utilisateur
     * @param score    le score de l'utilisateur
     */
    public void refreshGrid(String username, String color, int score) {
        if (score == -1 && !usernames.contains(username)) {
            usernames.add(username);
            colors.add(color);
            scores.add(0);
        } else if (usernames.contains(username)) {
            scores.set(usernames.indexOf(username), score);
        } else {
            usernames.add(username);
            colors.add(color);
            scores.add(score);
        }
        reloadGrid();
    }

    /**
     * Methode qui permet de recharger la grille
     */
    private void reloadGrid() {
        grid.removeAll();
        grid.setLayout(new GridLayout(usernames.size(), 2));
        ImageIcon usernameIcon = new ImageIcon();

        try {
            usernameIcon = new ImageIcon(
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("images/username.png"))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < usernames.size(); i++) {
            usernameLab = new JLabel(usernames.get(i));
            usernameLab.setForeground(Color.decode(colors.get(i)));
            usernameLab.setIcon(usernameIcon);
            grid.add(usernameLab);
            scoreLab = new JLabel(Integer.toString(scores.get(i)));
            scoreLab.setForeground(Color.decode(colors.get(i)));
            grid.add(scoreLab);
        }
        grid.revalidate();
        grid.repaint();
    }

    /**
     * Methode qui permet de supprimer un utilisateur de la grille
     * 
     * @param index    l'index de l'utilisateur
     * @param username le nom de l'utilisateur
     */
    public void removeUsername(int index, String username) {
        colors.remove(usernames.indexOf(username));
        scores.remove(usernames.indexOf(username));
        usernames.remove(username);
        reloadGrid();
    }

    /**
     * Fonction qui initialise le timer
     */
    void initTimer() {
        add(timerLab, BorderLayout.NORTH);
        resetTimer();
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                }
                if (minutes == 60) {
                    minutes = 0;
                }
                timerLab.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds - 1));
            }
        });
    }

    /**
     * Fonction qui reset le timer
     */
    void resetTimer() {
        seconds = 0;
        minutes = 0;
    }

    /**
     * Fonction qui permet d'arreter le timer
     */
    void stopTimer() {
        timer.stop();
    }

    /**
     * Fonction qui permet de demarrer le timer
     */
    void startTimer() {
        resetTimer();
        timer.start();
    }

    /**
     * Fonction pour annuler les scores
     */
    public void initScores() {
        // Mettre les scores a 0
        for (int i = 0; i < scores.size(); i++) {
            scores.set(i, 0);
        }
        reloadGrid();
    }
}
