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
                        matriz[i][j] = PRETA;
                    } else if (i > 3) {
                        matriz[i][j] = BRANCA;
                    } else {
                        matriz[i][j] = VAZIO;
                    }
                } else {
                    matriz[i][j] = VAZIO;
                }
            }
        }
    }

    private int turnoAtual = BRANCA;

    public int getTurnoAtual() {
        return turnoAtual;
    }

    public boolean TurnoCorreto(int l, int c) {
        char peca = matriz[l][c];
        if (turnoAtual == BRANCA) {
            return (peca == BRANCA || peca == DAMA_BRANCA);
        } else {
            return (peca == PRETA || peca == DAMA_PRETA);
        }
    }

    public void alternarTurno() {
        this.turnoAtual = (this.turnoAtual == BRANCA) ? PRETA : BRANCA;
    }

    private void checarPromocao(int l, int c) {
        if (matriz[l][c] == BRANCA && l == 0) {
            matriz[l][c] = DAMA_BRANCA;
        }
        else if (matriz[l][c] == PRETA && l == 5){
            matriz[l][c] = DAMA_PRETA;
        }
    }

    private boolean emCombo = false;
    private int comboL = -1, comboC = -1;

    public boolean podeCapturar(int l, int c, boolean souCombo) {
        char peca = matriz[l][c];
        if (peca == VAZIO) return false;

        // Dama
        if (peca == DAMA_BRANCA || peca == DAMA_PRETA) {
            int[] dl = {-1, -1, 1, 1};
            int[] dc = {-1, 1, -1, 1};

            for (int i = 0; i < 4; i++) {
                for (int dist = 1; dist < TAMANHO; dist++) {
                    int lInimigo = l + (dist * dl[i]);
                    int cInimigo = c + (dist * dc[i]);
                    int lFinal = lInimigo + dl[i];
                    int cFinal = cInimigo + dc[i];

                    if (lFinal >= 0 && lFinal < TAMANHO && cFinal >= 0 && cFinal < TAMANHO) {
                        char pCaminho = matriz[lInimigo][cInimigo];
                        if (pCaminho != VAZIO) {
                            if (!MesmoTime(peca, pCaminho) && matriz[lFinal][cFinal] == VAZIO) {
                                return true;
                            }
                            break;
                        }
                    } else break;
                }
            }
            return false;
        }

        // Peca Comum
        int[] dl = {-2, -2, 2, 2};
        int[] dc = {-2, 2, -2, 2};

        for (int i = 0; i < 4; i++) {
            int lFinal = l + dl[i];
            int cFinal = c + dc[i];
            int lMeio = l + dl[i] / 2;
            int cMeio = c + dc[i] / 2;

            if (lFinal >= 0 && lFinal < TAMANHO && cFinal >= 0 && cFinal < TAMANHO) {
                if (matriz[lFinal][cFinal] == VAZIO && matriz[lMeio][cMeio] != VAZIO) {
                    if (getTimeDaPeca(lMeio, cMeio) != getTurnoAtual()) {
                        if (!souCombo && (peca == BRANCA || peca == PRETA)) {
                            boolean paraFrente = (peca == BRANCA) ? (dl[i] < 0) : (dl[i] > 0);
                            if (!paraFrente) continue;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean existeCapturaObrigatoria() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (getTimeDaPeca(i, j) == getTurnoAtual()) {
                    if (podeCapturar(i, j, false)) return true;
                }
            }
        }
        return false;
    }

    public int getTimeDaPeca(int l, int c) {
        char p = matriz[l][c];
        if (p == BRANCA || p == DAMA_BRANCA) return BRANCA;
        if (p == PRETA || p == DAMA_PRETA) return PRETA;
        return VAZIO;
    }

    private boolean MesmoTime(char p1, char p2) {
        int timeP1 = (p1 == BRANCA || p1 == DAMA_BRANCA) ? 1 : 2;
        int timeP2 = (p2 == BRANCA || p2 == DAMA_BRANCA) ? 1 : 2;
        return timeP1 == timeP2;
    }

    public boolean verificaMovimento(int lAntiga, int cAntiga, int lNova, int cNova) {
        if (lNova < 0 || lNova >= TAMANHO || cNova < 0 || cNova >= TAMANHO) return false;
        if ((lNova + cNova) % 2 == 0 || matriz[lNova][cNova] != VAZIO) return false;
        if (emCombo && (lAntiga != comboL || cAntiga != comboC)) return false;

        int deltaLinha = Math.abs(lNova - lAntiga);
        int deltaColuna = Math.abs(cNova - cAntiga);
        if (deltaLinha != deltaColuna) return false;

        char pecaOrigem = matriz[lAntiga][cAntiga];

        // Dama
        if (pecaOrigem == DAMA_BRANCA || pecaOrigem == DAMA_PRETA) {
            int stepL = (lNova > lAntiga) ? 1 : -1;
            int stepC = (cNova > cAntiga) ? 1 : -1;
            int pecasNoCaminho = 0;
            int lInimigo = -1, cInimigo = -1;

            for (int i = 1; i < deltaLinha; i++) {
                int lAtual = lAntiga + (i * stepL);
                int cAtual = cAntiga + (i * stepC);
                char pCaminho = matriz[lAtual][cAtual];
                if (pCaminho != VAZIO) {
                    if (MesmoTime(pecaOrigem, pCaminho)) return false;
                    pecasNoCaminho++;
                    lInimigo = lAtual;
                    cInimigo = cAtual;
                }
            }

            if (pecasNoCaminho == 0 && !emCombo) {
                if (existeCapturaObrigatoria()) return false;
                matriz[lNova][cNova] = pecaOrigem;
                matriz[lAntiga][cAntiga] = VAZIO;
                return true;
            }

            if (pecasNoCaminho == 1 && (lInimigo + stepL == lNova && cInimigo + stepC == cNova)) {
                matriz[lNova][cNova] = pecaOrigem;
                matriz[lAntiga][cAntiga] = VAZIO;
                matriz[lInimigo][cInimigo] = VAZIO;
                if (podeCapturar(lNova, cNova, true)) {
                    emCombo = true;
                    comboL = lNova; comboC = cNova;
                } else emCombo = false;
                return true;
            }
            return false;
        }

        // Peca Comum - Captura
        if (deltaLinha == 2) {
            int lM = (lAntiga + lNova) / 2;
            int cM = (cAntiga + cNova) / 2;
            if (matriz[lM][cM] != VAZIO && !MesmoTime(pecaOrigem, matriz[lM][cM])) {
                if (!emCombo && (pecaOrigem == BRANCA || pecaOrigem == PRETA)) {
                    boolean frente = (pecaOrigem == BRANCA) ? (lNova < lAntiga) : (lNova > lAntiga);
                    if (!frente) return false;
                }
                matriz[lNova][cNova] = pecaOrigem;
                matriz[lAntiga][cAntiga] = VAZIO;
                matriz[lM][cM] = VAZIO;

                char pAntes = matriz[lNova][cNova];
                checarPromocao(lNova, cNova);
                if (pAntes != matriz[lNova][cNova]) {
                    emCombo = false; return true;
                }

                if (podeCapturar(lNova, cNova, true)) {
                    emCombo = true;
                    comboL = lNova; comboC = cNova;
                } else emCombo = false;
                return true;
            }
        }

        // Peca Comum - Movimento Simples
        if (deltaLinha == 1 && !emCombo) {
            if (existeCapturaObrigatoria()) return false;
            if (pecaOrigem == BRANCA && lNova > lAntiga) return false;
            if (pecaOrigem == PRETA && lNova < lAntiga) return false;

            matriz[lNova][cNova] = pecaOrigem;
            matriz[lAntiga][cAntiga] = VAZIO;
            checarPromocao(lNova, cNova);
            return true;
        }
        return false;
    }

    @Override
    public Tabuleiro clone() {
        try {
            Tabuleiro clone = (Tabuleiro) super.clone();
            clone.matriz = new char[TAMANHO][];
            for (int i = 0; i < TAMANHO; i++) clone.matriz[i] = this.matriz[i].clone();
            return clone;
        } catch (CloneNotSupportedException e) { return null; }
    }

    public char[][] getMatriz() { return matriz; }
    public void setMatriz(char[][] matriz) { this.matriz = matriz; }
    public boolean isEmCombo() { return emCombo; }
}