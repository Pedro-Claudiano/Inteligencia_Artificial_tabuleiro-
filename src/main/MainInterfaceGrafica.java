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
    private JComboBox<String> comboDificuldade;

    private int ultimoL1IA = -1, ultimoC1IA = -1;
    private int ultimoL2IA = -1, ultimoC2IA = -1;

    public MainInterfaceGrafica() {
        tabuleiroLogico = new Tabuleiro();

        setTitle("DISCIPLINA - IA - MINI JOGO DE DAMA");
        setSize(800, 850);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializarComponentes();
        sincronizarInterface();

        setVisible(true);
    }

    private void inicializarComponentes() {
        JPanel painelSuperior = new JPanel();
        String[] niveis = {"Fácil (P2)", "Médio (P4)", "Difícil (P6)", "Mestre (P10)"};
        comboDificuldade = new JComboBox<>(niveis);
        comboDificuldade.setSelectedIndex(1);
        painelSuperior.add(new JLabel("Dificuldade IA: "));
        painelSuperior.add(comboDificuldade);
        add(painelSuperior, BorderLayout.NORTH);

        JPanel painelTabuleiro = new JPanel(new GridLayout(TAMANHO, TAMANHO));
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                tabuleiroInterface[i][j] = new CasaBotao();
                int linha = i;
                int coluna = j;
                tabuleiroInterface[i][j].addActionListener(e -> tratarClique(linha, coluna));
                painelTabuleiro.add(tabuleiroInterface[i][j]);
            }
        }
        add(painelTabuleiro, BorderLayout.CENTER);
    }

    private void atualizarTitulo() {
        String jogador = (tabuleiroLogico.getTurnoAtual() == Tabuleiro.BRANCA) ? "BRANCAS" : "PRETAS";
        setTitle("DISCIPLINA - IA - VEZ DAS " + jogador);
    }

    private void tratarClique(int linha, int col) {
        if (tabuleiroLogico.getTurnoAtual() == Tabuleiro.PRETA) return;

        if (linhaOrigem == -1) {
            if (tabuleiroLogico.TurnoCorreto(linha, col)) {
                linhaOrigem = linha;
                colOrigem = col;
                tabuleiroInterface[linha][col].setBackground(Color.YELLOW);
                atualizarTitulo();
            }
        } else {
            char idOrigem = tabuleiroLogico.getId(linhaOrigem, colOrigem);
            char idDestino = tabuleiroLogico.getId(linha, col);

            if (linhaOrigem == linha && colOrigem == col) {
                cancelarSelecao();
                return;
            }

            boolean sucesso = moverPecaLogica(idOrigem, idDestino);

            if (sucesso) {
                if (comboDificuldade.isEnabled()) comboDificuldade.setEnabled(false);

                if (!tabuleiroLogico.isEmCombo()) {
                    tabuleiroLogico.alternarTurno();
                }

                cancelarSelecao();
                sincronizarInterface();
                atualizarTitulo();

                if (checarFimDeJogo()) return;

                if (tabuleiroLogico.getTurnoAtual() == Tabuleiro.PRETA) {
                    dispararJogadaIA();
                }
            } else {
                cancelarSelecao();
            }
        }
    }

    private void dispararJogadaIA() {
        Timer timerIA = new Timer(600, e -> {
            int profundidade = switch (comboDificuldade.getSelectedIndex()) {
                case 0 -> 2;
                case 2 -> 6;
                case 3 -> 10;
                default -> 4;
            };

            MotorIA motor = new MotorIA(profundidade);
            Movimento melhor = motor.buscarMelhorJogada(tabuleiroLogico);

            if (melhor != null) {

                ultimoL1IA = melhor.lOrigem; ultimoC1IA = melhor.cOrigem;
                ultimoL2IA = melhor.lDestino; ultimoC2IA = melhor.cDestino;

                tabuleiroLogico.verificaMovimento(melhor.lOrigem, melhor.cOrigem, melhor.lDestino, melhor.cDestino);

                if (!tabuleiroLogico.isEmCombo()) {
                    tabuleiroLogico.alternarTurno();
                } else {
                    sincronizarInterface();
                    dispararJogadaIA();
                    return;
                }

                sincronizarInterface();
                atualizarTitulo();
                checarFimDeJogo();
            }
        });
        timerIA.setRepeats(false);
        timerIA.start();
    }

    private boolean checarFimDeJogo() {
        int estado = tabuleiroLogico.verificarEstadoJogo();
        if (estado != 0) {
            String mensagem = switch (estado) {
                case Tabuleiro.BRANCA -> "FIM DE JOGO! VITÓRIA DAS BRANCAS!";
                case Tabuleiro.PRETA -> "FIM DE JOGO! VITÓRIA DAS PRETAS!";
                case 3 -> "EMPATE POR FALTA DE CAPTURAS!";
                default -> "FIM DE JOGO!";
            };
            JOptionPane.showMessageDialog(this, mensagem);
            System.exit(0);
            return true;
        }
        return false;
    }

    private void cancelarSelecao() {
        if (linhaOrigem != -1) {
            sincronizarInterface();
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
                if ((i + j) % 2 == 0) tabuleiroInterface[i][j].setBackground(new Color(235, 235, 208));
                else tabuleiroInterface[i][j].setBackground(new Color(119, 149, 86));

                if ((i == ultimoL1IA && j == ultimoC1IA) || (i == ultimoL2IA && j == ultimoC2IA)) {
                    tabuleiroInterface[i][j].setBackground(new Color(173, 216, 230));
                }

                tabuleiroInterface[i][j].setTipoPeca(tabuleiroLogico.getMatriz()[i][j]);
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
            if (tipoPeca == 1 || tipoPeca == 3) {
                g2.setColor(Color.WHITE);
                g2.fillOval(margem, margem, getWidth() - 2 * margem, getHeight() - 2 * margem);
                g2.setColor(Color.BLACK);
                g2.drawOval(margem, margem, getWidth() - 2 * margem, getHeight() - 2 * margem);
            } else if (tipoPeca == 2 || tipoPeca == 4) {
                g2.setColor(Color.BLACK);
                g2.fillOval(margem, margem, getWidth() - 2 * margem, getHeight() - 2 * margem);
            }

            if (tipoPeca > 2) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(margem + 5, margem + 5, getWidth() - 2 * margem - 10, getHeight() - 2 * margem - 10);
            }
        }
    }
}