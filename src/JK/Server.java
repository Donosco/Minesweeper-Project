package JK;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @version 1.0
 * @author khal0j
 */

/**
 * Classe qui permet de gerer un serveur
 */
public class Server extends JFrame implements Runnable {
    final static int PORT = 6060;

    private volatile ArrayList<Socket> userSockets = new ArrayList<Socket>();
    private volatile ArrayList<Thread> clientHandlers = new ArrayList<Thread>();

    private ServerSocket gestSock;

    private static ServerGUI panel;
    private Level level = Level.EASY;
    private Matrix matrix;
    private int nbOpenedCases = 0;
    private boolean isGameStarted = false;
    private Thread serverThread;
    private int dim = 0;

    /**
     * Constructeur de la classe Server
     * 
     * @throws IOException
     */
    Server() throws IOException {
        panel = new ServerGUI(this);
        matrix = new Matrix(level, dim);

        setContentPane(panel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        startServer(PORT);
    }

    /**
     * Constructeur de la classe Server avec numero de port en parametre
     * 
     * @param port
     */
    Server(int port) {
        panel = new ServerGUI(this);
        matrix = new Matrix(level, dim);

        setContentPane(panel);
        pack();
        setVisible(true);
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        manageServerquit();
                    }
                });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        startServer(port);
    }

    /**
     * Methode main de la classe Server
     * 
     * @param args not used
     */
    public static void main(String args[]) {
        new Server(6767);
    }

    /**
     * Fonction qui permet de gerer la fermeture du serveur
     * 
     */
    public void manageServerquit() {
        ClientHandler.broadCast("DISCONNECT");
        dispose();
    }

    /**
     * Fonction qui permet de demarrer le serveur
     * 
     * @param port
     */
    public void startServer(int port) {
        try {
            gestSock = new ServerSocket(port);
            System.out.println("Server started");
            serverThread = new Thread(() -> {
                while (!gestSock.isClosed() && !isGameStarted && ClientHandler.clientHandlers.size() < 7) {
                    Socket client;
                    try {
                        client = gestSock.accept();
                        userSockets.add(client);
                        Thread cThread = new Thread(new ClientHandler(client, matrix, this));
                        clientHandlers.add(cThread);
                        cThread.start();
                        System.out.println("Client joined");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            serverThread.start();
        } catch (Exception e) {
            closeSocket();
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui permet de fermer le socket
     * 
     */
    void closeSocket() {
        try {
            if (gestSock != null) {
                serverThread.interrupt();
                gestSock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui permet de lancer le serveur dans un thread
     */
    @Override
    public void run() {
        System.out.println("Server run");
    }

    /**
     * Fonction qui permet de changer la difficulte du jeu
     * 
     * @param l niveau de difficulte
     */
    public void setDifficulty(Level l, boolean flag) {
        level = l;
        if (l == Level.CUSTOM) {
            if (isGameStarted && dim != 0) {
                ClientHandler.broadCast("csLevel:" + dim);
            } else if (dim == 0) {
                dim = Integer.parseInt(JOptionPane.showInputDialog("Enter the dimension of the new custom grid", 3));
                ClientHandler.broadCast("cLevel:" + dim);
            }
        } else if (isGameStarted) {
            dim = 0;
            ClientHandler.broadCast("sLevel:" + level.toString());
        } else {
            dim = 0;
            ClientHandler.broadCast("Level :" + level.toString());
        }
        matrix = new Matrix(level, dim);
        for (ClientHandler c : ClientHandler.clientHandlers) {
            c.updateMatrix(matrix);
        }
        nbOpenedCases = 0;
    }

    /**
     * Fonction qui permet d'envoyer un message a tous les clients
     * 
     * @param message a envoyer
     */

    /**
     * Fonction qui permet de mettre a jour la grille du serveur
     * 
     * @param username nom du client
     * @param color    couleur associee au client
     * @param score    score du client
     */
    public void refreshGrid(String username, String color, int score) {
        panel.refreshGrid(username, color, score);

    }

    /**
     * Fonction qui permet de mettre a jour le score d'un client
     * 
     * @param username nom du client
     * @param score    score du client
     */
    public void updateNbOpenedCases(String username, int score) {
        nbOpenedCases++;

        if (nbOpenedCases == matrix.getDim() * matrix.getDim()) {
            dim = 0;
            int maxScore = 0;
            String winnerName = "";
            for (ClientHandler c : ClientHandler.clientHandlers) {
                if (c.getScore() > maxScore) {
                    maxScore = c.getScore();
                    winnerName = c.getUsername();
                }
            }
            ClientHandler.broadCast("GameOver:" + winnerName + "," + maxScore);
            isGameStarted = false;
            panel.initScores();
            panel.stopTimer();
        }
    }

    /**
     * Fonction qui permet de supprimer un client
     * 
     * @param index    index du client dans la liste des sockets
     * @param username nom du client
     */
    public void remove(int index, String username) {
        panel.removeUsername(index, username);
        userSockets.remove(index);
        clientHandlers.get(index).interrupt();
        clientHandlers.remove(index);
    }

    /**
     * Fonction qui permet de demarrer le jeu
     */
    public void startGame() {
        isGameStarted = true;
        setDifficulty(level, isGameStarted);
        panel.startTimer();
    }

    /**
     * Fonction qui retourne isGameStarted
     */
    public boolean getGameStarted() {
        return isGameStarted;
    }

}
