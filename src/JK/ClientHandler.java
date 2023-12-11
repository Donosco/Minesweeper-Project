package JK;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @version 1.0
 * @author khal0j
 */

/**
 * Classe qui permet de gerer un client connecte au serveur
 */
public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private String userColor;
    private Matrix matrix;
    private Server server;
    private int score = 0;

    private final String[] hexColorValues = {
            "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#00FFFF", "#000000", "#FFFFFF", "#FF00FF",
    };

    /**
     * Constructeur de la classe ClientHandler
     * 
     * @param clientSocket
     * @param m
     * @param s
     */
    ClientHandler(Socket clientSocket, Matrix m, Server s) {
        try {
            this.matrix = m;
            this.clientSocket = clientSocket;
            this.server = s;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            String msg = in.readUTF();
            String[] split = msg.split(":");
            username = split[1];
            userColor = hexColorValues[clientHandlers.size()];
            server.refreshGrid(username, userColor, 0);
            out.writeUTF(userColor.toString());
            clientHandlers.add(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui gere la communication entre le client et le serveur
     */
    @Override
    public void run() {
        String msg;
        while (!clientSocket.isClosed()) {
            try {
                msg = in.readUTF();
                String[] splitMsg = msg.split(":");
                if (splitMsg[0].equals("POS")) {
                    String[] split = splitMsg[1].split(",");
                    // Now we convert to iny
                    int x = Integer.parseInt(split[0]);
                    int y = Integer.parseInt(split[1]);
                    String value = matrix.getCase(x, y) ? "X" : Integer.toString(matrix.findMinesAround(x, y));
                    if (value.equals("X")) {
                        score -= 4;
                    } else {
                        score += Integer.parseInt(value);
                    }
                    if (server.getGameStarted()) {
                        server.refreshGrid(username, userColor, score);
                        server.updateNbOpenedCases(username, score);
                        out.writeUTF("Score:" + score);
                    } else {
                        server.refreshGrid(username, userColor, -1);
                    }
                    broadCast("POS:" + split[0] + "," + split[1] + "," + value + "," + userColor);
                } else if (splitMsg[0].equals("Username")) {
                    this.username = splitMsg[1];
                    broadCast(this.username + " joined the game");
                } else if (msg.equals("Disconnect")) {
                    closeAll();
                } else {
                    System.err.println("Error in the message");
                }
            } catch (IOException e) {
                closeAll();
                e.printStackTrace();
            }
        }
    }

    /**
     * Fonction qui permet d'envoyer un message a tous les clients
     * 
     * @param message
     */
    public static void broadCast(String message) {
        try {
            for (ClientHandler c : clientHandlers) {
                DataOutputStream output = new DataOutputStream(c.clientSocket.getOutputStream());
                output.writeUTF(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui permet de fermer tous les flux et le socket
     */
    public void closeAll() {
        removeClientHandler();
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui permet de supprimer le clientHandler de la liste
     */
    public void removeClientHandler() {
        server.remove(clientHandlers.indexOf(this), username);
        clientHandlers.remove(this);
        broadCast(this.username + " disconnected from server");
    }

    /**
     * Fonction qui permet de mettre a jour la matrice
     * 
     * @param m
     */
    public void updateMatrix(Matrix m) {
        this.matrix = m;
    }

    /**
     * Fonction qui permet de recuperer le score d'un client
     * 
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * Fonction qui permet de recuperer le nom d'un client
     * 
     * @return
     */
    public String getUsername() {
        return username;
    }
}
