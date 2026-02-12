package main;

/**
 * @author Douglas
 */
public class Tabuleiro implements Cloneable {

    private int[][] matriz;
    private final int TAMANHO = 6;

    public Tabuleiro() {
        this.matriz = new int[TAMANHO][TAMANHO];
        inicializar();
    }

    private void inicializar() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if ((i + j) % 2 != 0) {
                    if (i < 2) {
                        matriz[i][j] = 2; // Pretas
                    } else if (i > 3) {
                        matriz[i][j] = 1; // Brancas
                    }
                }
            }
        }
    }

    @Override
    public Tabuleiro clone() {
        try {
            Tabuleiro clone = (Tabuleiro) super.clone();
            clone.matriz = new int[TAMANHO][];
            for (int i = 0; i < TAMANHO; i++) {
                clone.matriz[i] = this.matriz[i].clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    /*
        Implementação dos métodos - getMovimentosPossiveis(), fazerMovimento(), etc
    */
    public boolean verificaMovimento (int lAntiga,int cAntiga,int lNova,int cNova) {
        if (lNova < 0 || lNova >= TAMANHO || cNova < 0 || cNova >= TAMANHO) {
            return false;
        }
        if ((lNova + cNova) % 2 == 0) {
            return false;
        }

        int deltaLinha = Math.abs(lNova - lAntiga);
        int deltaColuna = Math.abs(cNova - cAntiga);

        if (deltaLinha != 1 || deltaColuna != 1) {
            return false;
        }

        if (matriz[lNova][cNova] != 0) {
            return false;
        }
        matriz[lNova][cNova] = matriz[lAntiga][cAntiga];
        matriz[lAntiga][cAntiga] = 0;

        return true;
    }
    

    public int[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(int[][] matriz) {
        this.matriz = matriz;
    }
}
