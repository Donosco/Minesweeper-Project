package JK;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @version 1.0
 * @author khal0j
 */

/**
 * Classe qui permet de gerer l'interface du jeu
 */
public class GUI extends JPanel {
    JPanel grid = new JPanel(new GridLayout(3, 3));
    JPanel InfoPane = new JPanel(new GridLayout(1, 2));

    JLabel scoreLab = new JLabel("Score");
    JLabel diffculityLab = new JLabel("Difficulty");
    JLabel timerLab = new JLabel("Timer");
    JLabel userName = new JLabel("Username");

    JButton newGame = new JButton("New Game");

    JMenuBar menuBar = new JMenuBar();
    JMenu options = new JMenu("Options");
    JMenu diffSubMenu = new JMenu("Difficulty");
    JMenuItem mQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
    JMenuItem joinServer = new JMenuItem("Join new server", KeyEvent.VK_J);
    JMenuItem createServer = new JMenuItem("Create server", KeyEvent.VK_C);
    JMenuItem disconnect = new JMenuItem("Disconnect", KeyEvent.VK_D);

    private int nbMinesTrouvees = 0;
    private int seconds;
    private int minutes;
    public int casesOpened = 0;

    private Timer timer;

    public Matrix mineMatrix;
    private Main main;
    private Level level;
    private Map<Level, Integer> levelScores = new HashMap<Level, Integer>();
    private Case[][] tabCases;
    private Thread levelSaveThread = new Thread(() -> {
        saveLastLevel();
    });

    private boolean isFirstTest = true;
    private boolean isMultiplayer = false;

    /**
     * Constructeur de la classe GUI
     * 
     * @param mineMatrix La matrice de mines
     * @param main       une instance la classe Main
     */
    GUI(Main main) {
        setLayout(new BorderLayout());

        this.main = main;

        initMatrix();
        initScores();
        initMenuBar();
        initGrid();
        initButtons();
        initTimer();

        timer.start();
    }

    /**
     * Fonction pour initialiser la matrice
     */

    void initMatrix() {
        readDifficulty();
        if (level.equals(Level.EASY)) {
            mineMatrix = new Matrix(10, 8);
        } else if (level.equals(Level.MEDIUM)) {
            mineMatrix = new Matrix(25, 12);
        } else if (level.equals(Level.HARD)) {
            mineMatrix = new Matrix(40, 15);
        } else if (level.equals(Level.CUSTOM)) {
            int dim = Integer.parseInt(JOptionPane.showInputDialog("Enter the dimension of the new custom grid", 3));
            mineMatrix = new Matrix((Integer) dim * dim / 4, dim);
        }
    }

    /**
     * Fonction pour initialiser la panel d'information
     */

    void initInfoPane() {
        scoreLab.setText("Score: " + levelScores.get(level));
        diffculityLab.setText("Difficulty: " + level.toString());
    }

    /**
     * Fonction pour initialiser la bar de menu
     */
    void initMenuBar() {
        InfoPane.add(scoreLab);
        InfoPane.add(diffculityLab);
        InfoPane.add(timerLab);
        InfoPane.add(userName);
        userName.setVisible(false);
        add(InfoPane, BorderLayout.NORTH);
        initInfoPane();
        try {
            ImageIcon quitIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/quit.png"))));
            mQuit.setIcon(quitIcon);

            ImageIcon diffIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/difficulty.png"))));
            diffSubMenu.setIcon(diffIcon);
            diffculityLab.setIcon(diffIcon);

            ImageIcon scoreIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/score.png"))));
            scoreLab.setIcon(scoreIcon);

            ImageIcon timerIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/timer.png"))));
            timerLab.setIcon(timerIcon);

            ImageIcon joinIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/joinServer.png"))));
            joinServer.setIcon(joinIcon);

            ImageIcon createIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/createServer.png"))));
            createServer.setIcon(createIcon);

            ImageIcon disconnectIcon = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(
                            getClass().getResourceAsStream("images/disconnect.png"))));
            disconnect.setIcon(disconnectIcon);

            ImageIcon usernameIcon = new ImageIcon(
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("images/username.png"))));
            userName.setIcon(usernameIcon);

        } catch (IOException e) {
            e.printStackTrace();
        }

        mQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        diffSubMenu.setMnemonic(KeyEvent.VK_D);
        options.setMnemonic(KeyEvent.VK_O);

        joinServer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
        joinServer.addActionListener(e -> {
            main.JoinServer();
        });
        options.add(joinServer);

        createServer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        createServer.addActionListener(e -> {
            main.createServer();
        });
        options.add(createServer);

        disconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        disconnect.addActionListener(e -> {
            disconnect();
        });
        options.add(disconnect);
        disconnect.setEnabled(false);
        // Setting up the values for the difficulty selector
        for (Level level : Level.values()) {
            try {
                ImageIcon icon = new ImageIcon(ImageIO
                        .read(Objects.requireNonNull(
                                getClass().getResourceAsStream("images/" + level.toString() + ".png"))));
                JMenuItem levelMenuItem = new JMenuItem(level.toString(), icon);
                diffSubMenu.add(levelMenuItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        diffSubMenu.getItem(0).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        diffSubMenu.getItem(1).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        diffSubMenu.getItem(2).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        diffSubMenu.getItem(3).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

        options.add(diffSubMenu);
        mQuit.addActionListener(e -> manageQuit());
        options.addSeparator();
        options.add(mQuit);
        menuBar.add(options);
        main.setJMenuBar(menuBar);

        diffSubMenu.getItem(0).addActionListener(e -> {
            easyLevel();
        });

        diffSubMenu.getItem(1).addActionListener(e -> {
            mediumLevel();
        });

        diffSubMenu.getItem(2).addActionListener(e -> {
            hardLevel();
        });

        diffSubMenu.getItem(3).addActionListener(e -> {
            customLevel();
        });
    }

    /**
     * Fonction utilisée pour initialiser la grille
     */
    void initGrid() {
        populateGrid();
        add(newGame, BorderLayout.SOUTH);
    }

    /**
     * Fonction utilisée pour remplir la grille
     */
    void populateGrid() {
        tabCases = new Case[mineMatrix.getDim()][mineMatrix.getDim()];
        remove(grid);
        grid = new JPanel();
        grid.setLayout(new GridLayout(mineMatrix.getDim(), mineMatrix.getDim()));

        for (int i = 0; i < mineMatrix.getDim(); i++) {
            for (int j = 0; j < mineMatrix.getDim(); j++) {
                if (mineMatrix.getCase(i, j)) {
                    tabCases[i][j] = new Case("X", this, i, j);
                    grid.add(tabCases[i][j]);
                } else {
                    tabCases[i][j] = new Case(Integer.toString(mineMatrix.findMinesAround(i, j)), this, i, j);
                    grid.add(tabCases[i][j]);
                }
            }
        }
        mineMatrix.displayMinesAround();
        add(grid, BorderLayout.CENTER);
    }

    /**
     * Fonction pour mettre a jour la grille
     */
    void updateGrid() {
        populateGrid();
        mineMatrix.displayMinesAround();
        timer.start();
        resetTimer();
    }

    /**
     * Fonction pour initialiser les boutons
     */
    void initButtons() {
        try {
            ImageIcon newgame = new ImageIcon(ImageIO
                    .read(Objects.requireNonNull(getClass().getResourceAsStream("images/newgame.png"))));
            newGame.setIcon(newgame);
        } catch (IOException e) {
            e.printStackTrace();
        }
        newGame.addActionListener(e -> {
            newGame();
        });
    }

    /**
     * Fontion pour incrementer le nombre de mines trouvees
     */
    void incrementMinesTrouvees() {
        nbMinesTrouvees++;
    }

    /**
     * Fonction pour decrementer le nombre de mines trouvees
     */
    void decrementMinesTrouvees() {
        nbMinesTrouvees--;
    }

    /**
     * Fonction qui affiche une fenetre de victoire
     */
    void gameWon() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Félicitations, vous avez gagné !!!!!");
        levelScores.replace(level, levelScores.get(level) + 1);
        manageGameOver();
    }

    /**
     * Fonction qui affiche une fenêtre d'echec
     */
    void gameLost() {
        timer.stop();
        for (int i = 0; i < mineMatrix.getDim(); i++) {
            for (int j = 0; j < mineMatrix.getDim(); j++) {
                if (mineMatrix.getCase(i, j)) {
                    tabCases[i][j].openCase();
                }
            }
        }
        JOptionPane.showMessageDialog(this, " :(( VOUS AVEZ PERDU!!!  )):");
        manageGameOver();
    }

    /**
     * Fonction qui gère le comportement de l'appli quand on gagne ou perd une
     * partie
     */
    void manageGameOver() {
        int result = JOptionPane.showConfirmDialog(this, "Lancez un nouveau jeu?", "New game",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            newGame();
        } else {
            setCasesClickable(false);
        }
    }

    void setCasesClickable(boolean flag) {
        for (int i = 0; i < mineMatrix.getDim(); i++) {
            for (int j = 0; j < mineMatrix.getDim(); j++) {
                tabCases[i][j].setClickable(flag);
            }
        }
        return;
    }

    /**
     * Fonction qui lance un nouveau jeu
     */
    void newGame() {
        casesOpened = 0;
        nbMinesTrouvees = 0;
        mineMatrix.updateCases();
        mineMatrix.placeMines();
        updateGrid();
        main.pack();
        timer.start();
        resetTimer();
    }

    /**
     * Fonction qui initialise le timer
     */
    void initTimer() {
        resetTimer();
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                }
                timerLab.setText(String.format("Timer: %02d:%02d", minutes, seconds - 1));
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
     * Fonction pour sauvegarder le dernier niveau joué
     */
    private void saveLastLevel() {
        byte[] tabBytes = level.toString().getBytes();
        Path path = Paths.get("levelSave.txt");
        try {
            Files.write(path, tabBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction pour lire le niveau de difficulté
     */
    void readDifficulty() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("levelSave.txt"));
            String result = br.readLine();
            level = Level.valueOf(result);
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction pour gérer le comportement de l'appli quand on quitte
     */
    void manageQuit() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you wanna quit? :(", "NOOOOO",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            if (isMultiplayer) {
                disconnect();
            }
            levelSaveThread.start();
            System.exit(0);
        } else
            return;
    }

    /**
     * Fonction qui initialise les scores a zero
     */
    void initScores() {
        for (Level level : Level.values()) {
            levelScores.put(level, 0);
        }
    }

    /**
     * Fonction qui nous permet d'initialiser la cascade
     * 
     * @param i numero de ligne de la case cliquee
     * @param j numero de colonne de la case cliquee
     */
    void initCascade(int i, int j) {
        isFirstTest = true;
        cascadeReveal(i, j, this.tabCases[i][j].getValue());
    }

    /**
     * Fonction qui nous permet d'effectuer l'ouverture des cases en cascade
     * 
     * @param i         numero de ligne de la case cliquee
     * @param j         numero de colonne de la case cliquee
     * @param prevValue valeur de la case precedente
     */
    void cascadeReveal(int i, int j, String prevValue) {
        String value = tabCases[i][j].getValue();
        int state = tabCases[i][j].getState();

        if (value.equals("0") || (!value.equals("X") && isFirstTest) && state != 2) {
            isFirstTest = false;
            tabCases[i][j].openCase();

            if (i - 1 >= 0 && state == 0) {
                cascadeReveal(i - 1, j, value);
            }
            if (j - 1 >= 0 && state == 0) {
                cascadeReveal(i, j - 1, value);
            }
            if (j + 1 < mineMatrix.getDim() && state == 0) {
                cascadeReveal(i, j + 1, value);
            }
            if (i + 1 < mineMatrix.getDim() && state == 0) {
                cascadeReveal(i + 1, j, value);
            }

        } else if (!value.equals("X") && prevValue.equals("0")) {
            tabCases[i][j].openCase();
            // stop revealing on first number (when it is no longer the first test)
        }
    }

    /**
     * Fonction qui gere le niveau easy
     */
    void easyLevel() {
        casesOpened = 0;
        nbMinesTrouvees = 0;
        level = Level.EASY;
        mineMatrix.setDim(8);
        mineMatrix.setNoMines(10);
        mineMatrix.updateCases();
        mineMatrix.placeMines();
        initInfoPane();
        updateGrid();
        main.pack();
    }

    /**
     * Fonction qui gere le niveau medium
     */
    void mediumLevel() {
        casesOpened = 0;
        nbMinesTrouvees = 0;
        level = Level.MEDIUM;
        mineMatrix.setDim(12);
        mineMatrix.setNoMines(25);
        mineMatrix.updateCases();
        mineMatrix.placeMines();
        initInfoPane();
        updateGrid();
        main.pack();
    }

    /**
     * Fonction qui gere le niveau hard
     */
    void hardLevel() {
        casesOpened = 0;
        nbMinesTrouvees = 0;
        level = Level.HARD;
        mineMatrix.setDim(15);
        mineMatrix.setNoMines(40);
        mineMatrix.updateCases();
        mineMatrix.placeMines();
        initInfoPane();
        updateGrid();
        main.pack();
    }

    /**
     * Fonction qui gere le niveau custom
     */
    void customLevel() {
        casesOpened = 0;
        nbMinesTrouvees = 0;
        level = Level.CUSTOM;
        int dim = Integer.parseInt(JOptionPane.showInputDialog("Enter the dimension of the new custom grid", 3));
        mineMatrix.setDim(dim);
        mineMatrix.setNoMines(dim * dim / 4);
        mineMatrix.updateCases();
        mineMatrix.placeMines();
        initInfoPane();
        updateGrid();
        main.pack();
    }

    void customLevel(int dim) {
        casesOpened = 0;
        nbMinesTrouvees = 0;
        level = Level.CUSTOM;
        mineMatrix.setDim(dim);
        mineMatrix.setNoMines(dim * dim / 4);
        mineMatrix.updateCases();
        mineMatrix.placeMines();
        initInfoPane();
        updateGrid();
        main.pack();
    }

    /**
     * Fonction pour savoir si on est en mode multijoueur
     */
    boolean isMultiplayer() {
        return isMultiplayer;
    }

    /**
     * Fonction pour changer le mode multijoueur
     * 
     * @param isMultiplayer un boolean qui indique si on est en mode multijoueur ou
     */
    void setMultiplayer(boolean flag) {
        setCasesClickable(!flag);
        this.isMultiplayer = flag;
        disconnect.setEnabled(flag);
        joinServer.setEnabled(!flag);
        createServer.setEnabled(!flag);
        newGame.setEnabled(!flag);
        diffSubMenu.setEnabled(!flag);
    }

    /**
     * Fonction pour changer le nom d'utilisateur
     * 
     * @param name nom d'utilisateur
     * @param c    couleur du nom d'utilisateur
     */
    void setUserName(String name, String c) {
        userName.setText(name);
        userName.setForeground(Color.decode(c));
        userName.setVisible(true);
    }

    /**
     * Fonction pour envoyer les coordonnees au serveur
     * 
     * @param x coordonnee x de la case cliquee
     * @param y coordonnee y de la case cliquee
     */
    void sendCoordinates(int x, int y) {
        main.sendCoordinates(x, y);
    }

    /**
     * Fonction pour ouvrir la case en mode multijoueur
     * 
     * @param x     coordonnee x de la case cliquee
     * @param y     coordonnee y de la case cliquee
     * @param value valeur de la case
     * @param color couleur du joueur ayant cliquer la case
     */
    public void openCase(int x, int y, String value, String color) {
        tabCases[x][y].openCaseMultiplayer(value, color);
    }

    /**
     * Fonction pour incrementer le nombre de cases ouvertes
     */
    public void incrementCasesOpened() {
        casesOpened++;
        if (casesOpened == mineMatrix.getDim() * mineMatrix.getDim() && nbMinesTrouvees == mineMatrix.getNoMines()) {
            gameWon();
        }
    }

    /**
     * Fonction pour decrementer le nombre de cases ouvertes
     */
    public void decrementCasesOpened() {
        casesOpened--;
    }

    /**
     * Fonction pour se deconnecter du serveur
     */
    public void disconnect() {
        setCasesClickable(true);
        setMultiplayer(false);
        main.disconnect();
        userName.setVisible(false);
        easyLevel();
    }

    /**
     * Fonction pour gerer la fin de jeu en mode multijoueur
     * 
     * @param username nom d'utilisateur du gagnant
     * @param score    score du gagnant
     */
    public void GameOverMultiplayer(String username, String score) {
        Object[] options = { "Stay on server",
                "Quit" };
        if (username.equals(this.userName.getText())) {
            int n = JOptionPane.showOptionDialog(this, "You won with a score of " + score, "Game Over!",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == 0) {
                return;
            } else {
                disconnect();
            }

        } else {
            int n = JOptionPane.showOptionDialog(this, "You lost! " + username + " won with a score of " + score,
                    "Game Over!",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == 0) {
                return;
            } else {
                disconnect();
            }
        }
    }
}