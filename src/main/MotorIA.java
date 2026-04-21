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

        ordenarMovimentos(movimentos);

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
        ordenarMovimentos(movimentos);

        if (maximizando) {
            int maxEval = Integer.MIN_VALUE;
            for (Movimento m : movimentos) {
                Tabuleiro filho = nodo.clone();
                filho.verificaMovimento(m.lOrigem, m.cOrigem, m.lDestino, m.cDestino);

                if (!filho.isEmCombo()) filho.alternarTurno();

                int eval = minimax(filho, prof - 1, alpha, beta, false, corIA);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
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
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private void ordenarMovimentos(List<Movimento> movimentos) {
        movimentos.sort((m1, m2) -> {
            if (m1.ehCaptura && !m2.ehCaptura) return -1;
            if (!m1.ehCaptura && m2.ehCaptura) return 1;

            boolean m1Promove = (m1.lDestino == 0 || m1.lDestino == 5);
            boolean m2Promove = (m2.lDestino == 0 || m2.lDestino == 5);
            return Boolean.compare(m2Promove, m1Promove);
        });
    }
}