package main;

import java.util.List;

public class MotorIA {
    private final int PROFUNDIDADE_MAX;

    public MotorIA(int profundidade) {
        this.PROFUNDIDADE_MAX = profundidade;
    }

    public Movimento buscarMelhorJogada(Tabuleiro tabuleiro) {
        Movimento melhorMovimento = null;
        int melhorValor = Integer.MIN_VALUE;
        int turnoIA = tabuleiro.getTurnoAtual();

        List<Movimento> movimentos = tabuleiro.gerarMovimentosPossiveis();

        for (Movimento m : movimentos) {
            Tabuleiro simulado = tabuleiro.clone();
            simulado.verificaMovimento(m.lOrigem, m.cOrigem, m.lDestino, m.cDestino);


            if (!simulado.isEmCombo()) {
                simulado.alternarTurno();
            }

            int valor = minimax(simulado, PROFUNDIDADE_MAX - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, turnoIA);

            if (valor > melhorValor) {
                melhorValor = valor;
                melhorMovimento = m;
            }
        }
        return melhorMovimento;
    }

    private int minimax(Tabuleiro nodo, int prof, int alpha, int beta, boolean maximizando, int corIA) {
        int resultado = nodo.verificarEstadoJogo();
        if (resultado != 0 || prof == 0) {

            return (corIA == Tabuleiro.BRANCA) ? nodo.avaliar() : -nodo.avaliar();
        }

        List<Movimento> movimentos = nodo.gerarMovimentosPossiveis();

        if (maximizando) {
            int maxEval = Integer.MIN_VALUE;
            for (Movimento m : movimentos) {
                Tabuleiro filho = nodo.clone();
                filho.verificaMovimento(m.lOrigem, m.cOrigem, m.lDestino, m.cDestino);
                if (!filho.isEmCombo()) filho.alternarTurno();

                int eval = minimax(filho, prof - 1, alpha, beta, false, corIA);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Poda
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Movimento m : movimentos) {
                Tabuleiro filho = nodo.clone();
                filho.verificaMovimento(m.lOrigem, m.cOrigem, m.lDestino, m.cDestino);
                if (!filho.isEmCombo()) filho.alternarTurno();

                int eval = minimax(filho, prof - 1, alpha, beta, true, corIA);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Poda
            }
            return minEval;
        }
    }
}