package JK;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;

/**
 * @version 1.0
 * @author khal0j
 */

/**
 * Classe qui permet de gerer un client
 */
public class Main extends JFrame implements Runnable {

    private GUI panel;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket cSock;
    private Thread userThread;
    private int portNo = 0;
    private String port;
    private String name;
    private boolean isGameStarted;

    /**
     * Constructeur de la classe Main
     */
    Main() {
        panel = new GUI(this);
        setContentPane(panel);

        pack();
        setVisible(true);
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        panel.manageQuit();
                    }
                });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    /**
     * Main method
     * 
     * @param args not used
     */
    public static void main(String[] args) {
        new Main();
    }

    /**
     * Fonction qui permet au client de se connecter a un serveur
     */
    public void JoinServer() {
        do {
            port = JOptionPane.showInputDialog("Enter server port (Must be > 1024)", 6767);
            if (port == null || port.equals(""))
                return;
            else
                portNo = Integer.parseInt(port);
        } while (portNo < 1024);

        do {
            name = JOptionPane.showInputDialog("Enter your name", "Player X");
        } while (name == null || name.equals(""));

        connectToServer();
    }

    /**
     * Fonction qui permet au client de se connecter a un serveur
     */
    private void connectToServer() {
        try {
            cSock = new Socket("localhost", portNo);
            in = new DataInputStream(cSock.getInputStream());
            out = new DataOutputStream(cSock.getOutputStream());
            out.writeUTF("Username:" + name);
            panel.setMultiplayer(true);
            String color = in.readUTF();
            panel.setUserName(name, color);

            userThread = new Thread(this);
            userThread.start();

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Fonction qui permet au client de creer un serveur
     */
    void createServer() {
        try {
            do {
                port = JOptionPane.showInputDialog("Enter server port (Must be > 1024)", 6767);
                if (port == null || port.equals(""))
                    return;
                else
                    portNo = Integer.parseInt(port);
            } while (portNo < 1024);

            do {
                name = JOptionPane.showInputDialog("Enter your name", "Player X");
            } while (name == null || name.equals(""));

            new Server(portNo);
            connectToServer();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Fonction qui permet d'envoyer les coordonnees au serveur
     */
    void sendCoordinates(int x, int y) {
        try {
            out.writeUTF("POS:" + x + "," + y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui permet lancer le thread du client pour communiquer avec le
     * serveur
     */
    @Override
    public void run() {
        try {
            while (!userThread.isInterrupted() && !cSock.isClosed()) {
                // Here we first check if the data input stream is available so we can execute
                // the code
                while (in.available() == 0) {
                    if (userThread.isInterrupted()) {
                        return;
                    }
                }
                String msg = in.readUTF();
                String[] split = msg.split(":");
                if (split[0].equals("Level ")) {
                    panel.setCasesClickable(false);
                    if (split[1].equals(Level.EASY.toString())) {
                        panel.easyLevel();
                    } else if (split[1].equals(Level.MEDIUM.toString())) {
                        panel.mediumLevel();
                    } else if (split[1].equals(Level.HARD.toString())) {
                        panel.hardLevel();
                    } else {
                        System.err.println("Error in the level");
                    }
                } else if (split[0].equals("cLevel")) {
                    panel.setCasesClickable(false);
                    panel.customLevel(Integer.parseInt(split[1]));
                } else if (split[0].equals("csLevel")) {
                    isGameStarted = true;
                    panel.customLevel(Integer.parseInt(split[1]));
                    panel.setCasesClickable(true);
                } else if (split[0].equals("sLevel")) {
                    isGameStarted = true;
                    panel.setCasesClickable(true);
                    if (split[1].equals(Level.EASY.toString())) {
                        panel.easyLevel();
                    } else if (split[1].equals(Level.MEDIUM.toString())) {
                        panel.mediumLevel();
                    } else if (split[1].equals(Level.HARD.toString())) {
                        panel.hardLevel();
                    } else {
                        System.err.println("Error in the level");
                    }

                } else if (split[0].equals("POS")) {
                    if (isGameStarted) {
                        String[] split2 = split[1].split(",");
                        panel.openCase(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]), split2[2], split2[3]);
                    }
                } else if (split[0].equals("GameOver")) {
                    String[] split2 = split[1].split(",");
                    panel.GameOverMultiplayer(split2[0], split2[1]);
                } else if (msg.equals("DISCONNECT")) {
                    panel.disconnect();
                } else if (split[0].equals("Score")) {
                    this.panel.scoreLab.setText("Score :" + split[1]);
                } else {
                    System.err.println("Error in the message");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui permet de deconnecter le client du serveur
     */
    void disconnect() {
        try {
            userThread.interrupt();
            out.writeUTF("Disconnect");
            in.close();
            out.close();
            cSock.close();
            isGameStarted = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
