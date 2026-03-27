package main;

/**
 * @author Douglas
 */
public class Tabuleiro implements Cloneable {

    private char[][] matriz;
    private final int TAMANHO = 6;
    public static final int VAZIO = 0;
    public static final int BRANCA = 1;
    public static final int PRETA = 2;
    public static final int DAMA_BRANCA = 3;
    public static final int DAMA_PRETA = 4;

    private int turnoAtual = BRANCA;
    private boolean emCombo = false;
    private int comboL = -1, comboC = -1;
    private int contadorEmpateDamas = -1;

    private static final int[][] DECODER_POSICAO = {
            {0, 1}, {0, 3}, {0, 5}, // a, b, c
            {1, 0}, {1, 2}, {1, 4}, // d, e, f
            {2, 1}, {2, 3}, {2, 5}, // g, h, i
            {3, 0}, {3, 2}, {3, 4}, // j, k, l
            {4, 1}, {4, 3}, {4, 5}, // m, n, o
            {5, 0}, {5, 2}, {5, 4}  // p, q, r
    };

    private static final char[][] ENCODER_POSICAO = {
            {' ', 'a', ' ', 'b', ' ', 'c'},
            {'d', ' ', 'e', ' ', 'f', ' '},
            {' ', 'g', ' ', 'h', ' ', 'i'},
            {'j', ' ', 'k', ' ', 'l', ' '},
            {' ', 'm', ' ', 'n', ' ', 'o'},
            {'p', ' ', 'q', ' ', 'r', ' '}
    };

    public Tabuleiro() {
        this.matriz = new char[TAMANHO][TAMANHO];
        inicializar();
    }

    private void inicializar() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if ((i + j) % 2 != 0) {
                    if (i < 2) matriz[i][j] = PRETA;
                    else if (i > 3) matriz[i][j] = BRANCA;
                    else matriz[i][j] = VAZIO;
                } else {
                    matriz[i][j] = VAZIO;
                }
            }
        }
    }

    // Gerenciamento de Turno
    public int getTurnoAtual() { return turnoAtual; }

    public boolean TurnoCorreto(int l, int c) {
        char peca = matriz[l][c];
        if (turnoAtual == BRANCA) return (peca == BRANCA || peca == DAMA_BRANCA);
        else return (peca == PRETA || peca == DAMA_PRETA);
    }

    public void alternarTurno() {
        this.turnoAtual = (this.turnoAtual == BRANCA) ? PRETA : BRANCA;
    }

    // Auxiliares de Peca
    public int getTimeDaPeca(int l, int c) {
        char p = matriz[l][c];
        if (p == BRANCA || p == DAMA_BRANCA) return BRANCA;
        if (p == PRETA || p == DAMA_PRETA) return PRETA;
        return VAZIO;
    }

    private boolean MesmoTime(char p1, char p2) {
        return getTimeDaPeca(0, 0) == getTimeDaPeca(0, 0); // Placeholder para lógica de tempo
    }

    private boolean isMesmoTime(int l1, int c1, int l2, int c2) {
        return getTimeDaPeca(l1, c1) == getTimeDaPeca(l2, c2);
    }

    private void checarPromocao(int l, int c) {
        if (matriz[l][c] == BRANCA && l == 0) matriz[l][c] = DAMA_BRANCA;
        else if (matriz[l][c] == PRETA && l == 5) matriz[l][c] = DAMA_PRETA;
    }

    // Logica de Captura
    public boolean podeCapturar(int l, int c, boolean souCombo) {
        char peca = matriz[l][c];
        if (peca == VAZIO) return false;

        if (peca == DAMA_BRANCA || peca == DAMA_PRETA) {
            int[] dl = {-1, -1, 1, 1}, dc = {-1, 1, -1, 1};
            for (int i = 0; i < 4; i++) {
                for (int dist = 1; dist < TAMANHO; dist++) {
                    int lI = l + (dist * dl[i]), cI = c + (dist * dc[i]);
                    int lF = lI + dl[i], cF = cI + dc[i];
                    if (lF >= 0 && lF < TAMANHO && cF >= 0 && cF < TAMANHO) {
                        if (matriz[lI][cI] != VAZIO) {
                            if (!isMesmoTime(l, c, lI, cI) && matriz[lF][cF] == VAZIO) return true;
                            break;
                        }
                    } else break;
                }
            }
        } else {
            int[] dl = {-2, -2, 2, 2}, dc = {-2, 2, -2, 2};
            for (int i = 0; i < 4; i++) {
                int lF = l + dl[i], cF = c + dc[i];
                int lM = l + dl[i] / 2, cM = c + dc[i] / 2;
                if (lF >= 0 && lF < TAMANHO && cF >= 0 && cF < TAMANHO) {
                    if (matriz[lF][cF] == VAZIO && matriz[lM][cM] != VAZIO) {
                        if (!isMesmoTime(l, c, lM, cM)) {
                            if (!souCombo) {
                                boolean frente = (peca == BRANCA) ? (dl[i] < 0) : (dl[i] > 0);
                                if (!frente) continue;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean existeCapturaObrigatoria() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (getTimeDaPeca(i, j) == turnoAtual && podeCapturar(i, j, false)) return true;
            }
        }
        return false;
    }

    // Movimentacao Principal
    public boolean verificaMovimento(int lA, int cA, int lN, int cN) {
        if (lN < 0 || lN >= TAMANHO || cN < 0 || cN >= TAMANHO) return false;
        if ((lN + cN) % 2 == 0 || matriz[lN][cN] != VAZIO) return false;
        if (emCombo && (lA != comboL || cA != comboC)) return false;

        int dL = Math.abs(lN - lA), dC = Math.abs(cN - cA);
        if (dL != dC) return false;
        char peca = matriz[lA][cA];

        // Dama
        if (peca == DAMA_BRANCA || peca == DAMA_PRETA) {
            int sL = (lN > lA) ? 1 : -1, sC = (cN > cA) ? 1 : -1;
            int pecas = 0, lI = -1, cI = -1;
            for (int i = 1; i < dL; i++) {
                int lAt = lA + (i * sL), cAt = cA + (i * sC);
                if (matriz[lAt][cAt] != VAZIO) {
                    if (isMesmoTime(lA, cA, lAt, cAt)) return false;
                    pecas++; lI = lAt; cI = cAt;
                }
            }
            if (pecas == 0 && !emCombo) {
                if (existeCapturaObrigatoria()) return false;
                matriz[lN][cN] = peca; matriz[lA][cA] = VAZIO; return true;
            }
            if (pecas == 1 && (lI + sL == lN && cI + sC == cN)) {
                matriz[lN][cN] = peca; matriz[lA][cA] = VAZIO; matriz[lI][cI] = VAZIO;
                if (podeCapturar(lN, cN, true)) { emCombo = true; comboL = lN; comboC = cN; }
                else emCombo = false;
                return true;
            }
        }
        // Peca Comum
        else {
            if (dL == 2) {
                int lM = (lA + lN) / 2, cM = (cA + cN) / 2;
                if (matriz[lM][cM] != VAZIO && !isMesmoTime(lA, cA, lM, cM)) {
                    if (!emCombo) {
                        boolean f = (peca == BRANCA) ? (lN < lA) : (lN > lA);
                        if (!f) return false;
                    }
                    matriz[lN][cN] = peca; matriz[lA][cA] = VAZIO; matriz[lM][cM] = VAZIO;
                    char pAnt = matriz[lN][cN]; checarPromocao(lN, cN);
                    if (pAnt != matriz[lN][cN]) { emCombo = false; return true; }
                    if (podeCapturar(lN, cN, true)) { emCombo = true; comboL = lN; comboC = cN; }
                    else emCombo = false;
                    return true;
                }
            } else if (dL == 1 && !emCombo) {
                if (existeCapturaObrigatoria()) return false;
                if ((peca == BRANCA && lN > lA) || (peca == PRETA && lN < lA)) return false;
                matriz[lN][cN] = peca; matriz[lA][cA] = VAZIO; checarPromocao(lN, cN); return true;
            }
        }
        return false;
    }

    // Verificacao de Estado Final
    public int verificarEstadoJogo() {
        int b = 0, p = 0;
        boolean movB = false, movP = false;
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (matriz[i][j] == VAZIO) continue;
                if (getTimeDaPeca(i, j) == BRANCA) { b++; if (!movB) movB = temAlgumLance(i, j); }
                else { p++; if (!movP) movP = temAlgumLance(i, j); }
            }
        }
        if (b == 0 || (turnoAtual == BRANCA && !movB)) return PRETA;
        if (p == 0 || (turnoAtual == PRETA && !movP)) return BRANCA;
        if (b == 1 && p == 1 && apenasDamas()) {
            if (contadorEmpateDamas == -1) contadorEmpateDamas = 2;
            else if (--contadorEmpateDamas == 0) return 3; // Empate
        } else contadorEmpateDamas = -1;
        return 0;
    }

    private boolean temAlgumLance(int l, int c) {
        if (podeCapturar(l, c, false)) return true;
        int[] dl = {-1, -1, 1, 1}, dc = {-1, 1, -1, 1};
        for (int i = 0; i < 4; i++) {
            int nL = l + dl[i], nC = c + dc[i];
            if (nL >= 0 && nL < TAMANHO && nC >= 0 && nC < TAMANHO && matriz[nL][nC] == VAZIO) {
                char p = matriz[l][c];
                if (p > 2 || (p == BRANCA && nL < l) || (p == PRETA && nL > l)) return true;
            }
        }
        return false;
    }

    private boolean apenasDamas() {
        for (char[] linha : matriz) for (char p : linha) if (p == BRANCA || p == PRETA) return false;
        return true;
    }

    // Metodos do Sistema
    public int[] getCoordenadas(char id) { return DECODER_POSICAO[id - 'a']; }
    public char getId(int l, int c) { return ENCODER_POSICAO[l][c]; }
    public char[][] getMatriz() { return matriz; }
    public boolean isEmCombo() { return emCombo; }

    @Override
    public Tabuleiro clone() {
        try {
            Tabuleiro clone = (Tabuleiro) super.clone();
            clone.matriz = new char[TAMANHO][];
            for (int i = 0; i < TAMANHO; i++) clone.matriz[i] = this.matriz[i].clone();
            return clone;
        } catch (CloneNotSupportedException e) { return null; }
    }
}