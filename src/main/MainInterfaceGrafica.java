package main;

import javax.swing.*;
import java.awt.*;

/**
 * @author Douglas
 */
public final class MainInterfaceGrafica extends JFrame {

    private final int TAMANHO = 6;
    private final CasaBotao[][] tabuleiroInterface = new CasaBotao[TAMANHO][TAMANHO];
    private final Tabuleiro tabuleiroLogico;
    private int linhaOrigem = -1, colOrigem = -1;

    public MainInterfaceGrafica() {
        tabuleiroLogico = new Tabuleiro();

        setTitle("DISCIPLINA - IA - MINI JOGO DE DAMA");
        setSize(800, 800);
        setLayout(new GridLayout(TAMANHO, TAMANHO));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializarComponentes();
        sincronizarInterface();

        setVisible(true);
    }

    private void inicializarComponentes() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                tabuleiroInterface[i][j] = new CasaBotao();

                if ((i + j) % 2 == 0) {
                    tabuleiroInterface[i][j].setBackground(new Color(235, 235, 208)); // Bege
                } else {
                    tabuleiroInterface[i][j].setBackground(new Color(119, 149, 86));  // Verde
                }

                int linha = i;
                int coluna = j;
                tabuleiroInterface[i][j].addActionListener(e -> tratarClique(linha, coluna));
                add(tabuleiroInterface[i][j]);
            }
        }
    }

    private void atualizarTitulo() {
        String jogador = (tabuleiroLogico.getTurnoAtual() == Tabuleiro.BRANCA) ? "BRANCAS" : "PRETAS";
        setTitle("DISCIPLINA - IA - VEZ DAS " + jogador);
    }

    private void tratarClique(int linha, int col) {
        // Caso 1: Selecionando a peça
        if (linhaOrigem == -1) {
            if (tabuleiroLogico.TurnoCorreto(linha, col)) {
                linhaOrigem = linha;
                colOrigem = col;
                tabuleiroInterface[linha][col].setBackground(Color.YELLOW);
                atualizarTitulo();
            }
        }
        // Caso 2: Movimentando a peça
        else {
            char idOrigem = tabuleiroLogico.getId(linhaOrigem, colOrigem);
            char idDestino = tabuleiroLogico.getId(linha, col);

            if (linhaOrigem == linha && colOrigem == col) {
                cancelarSelecao();
                return;
            }

            boolean sucesso = moverPecaLogica(idOrigem, idDestino);

            if (sucesso) {
                // Só alterna o turno se não estiver em sequência de capturas
                if (!tabuleiroLogico.isEmCombo()) {
                    tabuleiroLogico.alternarTurno();
                }

                cancelarSelecao();
                sincronizarInterface();
                atualizarTitulo();

                // Verificação de vitória ou empate
                int estado = tabuleiroLogico.verificarEstadoJogo();
                if (estado != 0) {
                    String mensagem = "";
                    if (estado == Tabuleiro.BRANCA) mensagem = "FIM DE JOGO! VITÓRIA DAS BRANCAS!";
                    else if (estado == Tabuleiro.PRETA) mensagem = "FIM DE JOGO! VITÓRIA DAS PRETAS!";
                    else if (estado == 3) mensagem = "EMPATE! (SOMENTE 2 DAMAS)";

                    JOptionPane.showMessageDialog(this, mensagem);
                    System.exit(0);
                }

            } else {
                cancelarSelecao();
            }
        }
    }

    private void cancelarSelecao() {
        if (linhaOrigem != -1) {
            tabuleiroInterface[linhaOrigem][colOrigem].setBackground(new Color(119, 149, 86));
        }
        linhaOrigem = -1;
        colOrigem = -1;
    }

    private boolean moverPecaLogica(char id1, char id2) {
        int[] origem = tabuleiroLogico.getCoordenadas(id1);
        int[] destino = tabuleiroLogico.getCoordenadas(id2);
        return tabuleiroLogico.verificaMovimento(origem[0], origem[1], destino[0], destino[1]);
    }

    public void sincronizarInterface() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                int peca = tabuleiroLogico.getMatriz()[i][j];
                tabuleiroInterface[i][j].setTipoPeca(peca);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainInterfaceGrafica::new);
    }

    private class CasaBotao extends JButton {
        private int tipoPeca = 0;

        public void setTipoPeca(int tipo) {
            this.tipoPeca = tipo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int margem = 10;
            // Peças comuns (1 e 2) ou Damas (3 e 4)
            if (tipoPeca == 1 || tipoPeca == 3) { // Brancas
                g2.setColor(Color.WHITE);
                g2.fillOval(margem, margem, getWidth() - 2 * margem, getHeight() - 2 * margem);
                g2.setColor(Color.BLACK);
                g2.drawOval(margem, margem, getWidth() - 2 * margem, getHeight() - 2 * margem);
            } else if (tipoPeca == 2 || tipoPeca == 4) { // Pretas
                g2.setColor(Color.BLACK);
                g2.fillOval(margem, margem, getWidth() - 2 * margem, getHeight() - 2 * margem);
            }

            // Representação de Dama
            if (tipoPeca > 2) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(margem + 5, margem + 5, getWidth() - 2 * margem - 10, getHeight() - 2 * margem - 10);
            }
        }
    }
}