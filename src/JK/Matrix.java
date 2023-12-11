package JK;

import java.util.Random;

/**
 * Classe correspondante à la matrice de mines
 */
public class Matrix {
    private boolean[][] cases;

    private final static int DIM = 5;
    private final static int NBMINES = 7;

    private int nbMines;
    private int dimSquare;

    /**
     * Constructeur sans arguments
     */
    Matrix() {
        this(NBMINES);
    }

    /**
     * Constructeur avec nombres de mines
     * 
     * @param nbMines qui est le nombre de mines dans la matrice
     */

    Matrix(int nbMines) {
        this(nbMines, DIM);
    }

    /**
     * Constructeur avec nombre de mines et dimension de la matrice de mines
     * 
     * @param nbMines   qui est le nombre de mines dans la matrice
     * @param dimSquare qui est la dimension de la matrice
     */
    Matrix(int nbMines, int dimSquare) {
        this.nbMines = nbMines;
        this.dimSquare = dimSquare;
        this.cases = new boolean[this.dimSquare][this.dimSquare];
        placeMines();
    }

    /**
     * Constructeur avec niveau de difficulté
     * 
     * @param level qui est le niveau de difficulté
     */
    Matrix(Level level, int dim) {
        if (level == Level.EASY) {
            this.nbMines = 10;
            this.dimSquare = 8;
        } else if (level == Level.MEDIUM) {
            this.nbMines = 25;
            this.dimSquare = 12;
        } else if (level == Level.HARD) {
            this.nbMines = 40;
            this.dimSquare = 15;
        } else {
            if (dim != 0) {
                this.dimSquare = dim;
                this.nbMines = dim * dim / 4;
            }
        }
        this.cases = new boolean[this.dimSquare][this.dimSquare];
        placeMines();

    }

    /**
     * Place les mines aleatoirement dans la matrice
     */
    void placeMines() {
        Random rand = new Random();
        for (int i = 0; i < cases.length; i++) {
            for (int j = 0; j < cases[0].length; j++) {
                this.cases[i][j] = false;
            }
        }
        int noMines = this.nbMines;
        while (noMines > 0) {
            int x = rand.nextInt(this.dimSquare);
            int y = rand.nextInt(this.dimSquare);
            if (!this.cases[x][y]) {
                this.cases[x][y] = true;
                noMines--;
            }
        }
    }

    /**
     * Affiche la matrice
     */
    void displayMatrix() {
        for (int i = 0; i < cases.length; i++) {
            System.out.print("|");
            for (int j = 0; j < cases[0].length; j++) {
                if (this.cases[i][j]) {
                    System.out.print("X");
                } else {
                    System.out.print(" ");
                }
                System.out.print("|");
            }
            System.out.println();
        }
    }

    /**
     * Trouver le nombre de mines autour d'une case
     * 
     * @param i indice de ligne de la case
     * @param j indice de colonne de la case
     */

    int findMinesAround(int i, int j) {
        int noMines;
        noMines = 0;
        for (int x = i - 1; x < i + 2; x++) {
            for (int y = j - 1; y < j + 2; y++) {
                if (x >= 0 && x < cases.length && y >= 0 && y < cases[0].length && !(x == i && y == j)) {
                    if (this.cases[x][y]) {
                        noMines++;
                    }
                }
            }
        }
        return noMines;
    }

    /**
     * Affiche le nombre de mines autour de chaque case
     */
    void displayMinesAround() {
        for (int i = 0; i < cases.length; i++) {
            System.out.print("|");
            for (int j = 0; j < cases[0].length; j++) {
                if (cases[i][j]) {
                    System.out.print("X");
                } else {
                    System.out.print(findMinesAround(i, j));
                }
                System.out.print("|");
            }
            System.out.println();
        }
    }

    /**
     * Getter pour la dimension de la matrice
     */
    int getDim() {
        return this.dimSquare;
    }

    /**
     * Getter pour la case - utilisé dans GUI
     * 
     * @param i indice de ligne de la case
     * @param j indice de colonne de la case
     * @return
     */

    boolean getCase(int i, int j) {
        return this.cases[i][j];
    }

    /**
     * Getter pour le nombre de mines
     */
    int getNoMines() {
        return this.nbMines;
    }

    /**
     * Setter pour la dimension de la matrice
     * 
     * @param dim dimension de la matrice de mines
     */
    void setDim(int dim) {
        this.dimSquare = dim;
    }

    /**
     * Setter pour le nombre de mines
     * 
     * @param n nombre de mines dans la matrice
     */
    void setNoMines(int n) {
        this.nbMines = n;
    }

    /**
     * Fonction qui met à jour la matrice des cases
     */
    void updateCases() {
        this.cases = new boolean[this.dimSquare][this.dimSquare];
    }
}
