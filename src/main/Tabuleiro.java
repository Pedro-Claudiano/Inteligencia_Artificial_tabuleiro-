package main;

/**
 * @author Douglas
 */
public class Tabuleiro implements Cloneable {

    private char [][] matriz;
    private final int TAMANHO = 6;
    public static final int VAZIO = 0;
    public static final int BRANCA = 1;
    public static final int PRETA = 2;
    public static final int DAMA_BRANCA = 3;
    public static final int DAMA_PRETA = 4;

    private static final int[][] DECODER_POSICAO = {
            {0,1}, {0,3}, {0,5}, // a, b, c
            {1,0}, {1,2}, {1,4}, // d, e, f
            {2,1}, {2,3}, {2,5}, // g, h, i
            {3,0}, {3,2}, {3,4}, // j, k, l
            {4,1}, {4,3}, {4,5}, // m, n, o
            {5,0}, {5,2}, {5,4}  // p, q, r
    };

    private static final char[][] ENCODER_POSICAO = {
            {' ', 'a', ' ', 'b', ' ', 'c'},
            {'d', ' ', 'e', ' ', 'f', ' '},
            {' ', 'g', ' ', 'h', ' ', 'i'},
            {'j', ' ', 'k', ' ', 'l', ' '},
            {' ', 'm', ' ', 'n', ' ', 'o'},
            {'p', ' ', 'q', ' ', 'r', ' '}
    };

    public int[] getCoordenadas(char id) {
        return DECODER_POSICAO[id - 'a'];
    }

    public char getId(int l, int c) {
        return ENCODER_POSICAO[l][c];
    }


    public Tabuleiro() {
        this.matriz = new char[TAMANHO][TAMANHO];
        inicializar();
    }

    private void inicializar() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if ((i + j) % 2 != 0) {
                    if (i < 2) {
                        matriz[i][j] = PRETA; // Pretas
                    }else if (i > 3) {
                        matriz[i][j] = BRANCA; // Brancas
                    }else{
                        matriz[i][j] = VAZIO;
                    }
                }else{
                    matriz[i][j] = VAZIO;
                }
            }
        }
    }

    private int turnoAtual=BRANCA;

    public int getTurnoAtual() {
        return turnoAtual;
    }

    public boolean TurnoCorreto(int l,int c){
        char peca = matriz[l][c];
        if(turnoAtual==BRANCA){
            return(peca==BRANCA || peca==DAMA_BRANCA);
        }else{
            return(peca==PRETA || peca==DAMA_PRETA);
        }
    }

    public void alternarTurno() {
        this.turnoAtual = (this.turnoAtual == BRANCA) ? PRETA : BRANCA;
    }

    @Override
    public Tabuleiro clone() {
        try {
            Tabuleiro clone = (Tabuleiro) super.clone();
            clone.matriz = new char[TAMANHO][];
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

        if(turnoAtual==BRANCA && lNova > lAntiga){
            return false;
        }
        if(turnoAtual==PRETA && lNova < lAntiga){
            return false;

        }

        //criar um metodo que verifica se pode comer uma peca e reutiliza-lo para verificar se posso fazer uma nova jogada para otimizar o codigo
        //atribuir a cada posicao do tabuleiro um char e criar uma hask para decodificar a possição da tabela
        //Verificar o ganhador tem que ser feito de maneira rapida e sempre verificar de maneira otimizada
        //lembrar de heuristica de podas na arvores
        //se existir somente 2 damas e elas nao puderem comer EMPATE
        matriz[lNova][cNova] = matriz[lAntiga][cAntiga];
        matriz[lAntiga][cAntiga] = 0;

        if (matriz[lNova][cNova] == BRANCA && lNova == 0) {
            matriz[lNova][cNova] = DAMA_BRANCA;
        }
        else if (matriz[lNova][cNova] == PRETA && lNova == 5) {
            matriz[lNova][cNova] = DAMA_PRETA;
        }

        return true;
    }


    public char[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(char[][] matriz) {
        this.matriz = matriz;
    }
}
