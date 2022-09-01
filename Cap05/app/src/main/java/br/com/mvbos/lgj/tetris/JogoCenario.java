package br.com.mvbos.lgj.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import br.com.mvbos.lgj.JogoView;
import br.com.mvbos.lgj.R;
import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.base.SomUtil;
import br.com.mvbos.lgj.base.Texto;
import br.com.mvbos.lgj.base.Tocador;


public class JogoCenario extends CenarioPadrao {

    enum Estado {
        JOGANDO, GANHOU, PERDEU
    }

    private static final int ESPACAMENTO = 2;

    private static final int ESPACO_VAZIO = -1;

    private static final int LINHA_COMPLETA = -2;

    private int largBloco, altBloco; // largura bloco e altura bloco

    private int ppx, ppy; // Posicao peca x e y

    private final int[][] grade = new int[10][16];

    private int temporizador = 0;

    private Texto texto;
    private Texto textoPausa;
    private Texto textoPlacar;

    private Random rand = new Random();

    private int idPeca = -1;
    private int idPrxPeca = -1;
    private int corPeca;
    private int[][] peca;

    private int nivel = JogoView.nivel;
    private int pontos;
    private int linhasFeistas;

    private boolean animar;
    private boolean depurar;
    private boolean pressionado;

    private Estado estado = Estado.JOGANDO;

    // Efeitos sonoros
    private int somMarcarLinha;
    private int somAdicionarPeca;
    private SomUtil som = JogoView.getSomUtil();

    // Musica
    private Tocador tocador = JogoView.getTocador();

    public JogoCenario(int largura, int altura) {
        super(largura, altura);
    }

    @Override
    public void carregar() {

        largBloco = largura / grade.length;
        altBloco = altura / grade[0].length;

        for (int i = 0; i < grade.length; i++) {
            for (int j = 0; j < grade[0].length; j++) {
                grade[i][j] = ESPACO_VAZIO;
            }
        }

        texto = new Texto(25);
        texto.setCor(Color.RED);

        textoPlacar = new Texto(20);
        textoPlacar.setCor(Color.WHITE);

        textoPausa = new Texto(40);
        textoPausa.setCor(Color.WHITE);

        /*
         * Opcoes de audio
         * 1 = ligado
         * 2 = efeitos
         * 3 = desligado
         * */

        somAdicionarPeca = som.carregar(R.raw.adiciona_peca);
        somMarcarLinha = som.carregar(R.raw._109662_grunz_success);

        if (JogoView.opcaoAudio == 1) {
            tocador.carregar("sounds/piano_quebrado.wav");
            tocador.tocar(1, 1, true);
        }

        adicionaPeca();
    }

    @Override
    public void descarregar() {

        som.descarregar(somAdicionarPeca);
        som.descarregar(somMarcarLinha);
        tocador.parar();
    }

    @Override
    public void atualizar() {

        if (estado != Estado.JOGANDO) {
            return;
        }

        if (JogoView.controleTecla[JogoView.Tecla.ESQUERDA.ordinal()]) {
            if (validaMovimento(peca, ppx - 1, ppy))
                ppx--;

        } else if (JogoView.controleTecla[JogoView.Tecla.DIREITA.ordinal()]) {
            if (validaMovimento(peca, ppx + 1, ppy))
                ppx++;
        }

        if (JogoView.controleTecla[JogoView.Tecla.CIMA.ordinal()]) {
            girarReposicionarPeca(false);

        } else if (pressionado) {
            if (validaMovimento(peca, ppx, ppy + 1))
                ppy++;
        }

        if (depurar && JogoView.controleTecla[JogoView.Tecla.BB.ordinal()]) {
            if (++idPeca == Peca.PECAS.length)
                idPeca = 0;

            peca = Peca.PECAS[idPeca];
            corPeca = Peca.Cores[idPeca];
        }

        JogoView.liberaTeclas();

        if (animar && temporizador >= 5) {
            animar = false;

            descerColunas();
            adicionaPeca();

        } else if (temporizador >= 20) {
            temporizador = 0;

            if (colidiu(ppx, ppy + 1)) {

                pressionado = false;
                tocarSom(somAdicionarPeca);

                if (!parouForaDaGrade()) {
                    adicionarPecaNaGrade();
                    animar = marcarLinha();

                    peca = null;

                    if (!animar)
                        adicionaPeca();

                } else {
                    estado = Estado.PERDEU;
                }

            } else
                ppy++;

        } else
            temporizador += nivel;
    }

    @Override
    public void onPressionarLongo(float x, float y) {
        super.onPressionarLongo(x, y);
        pressionado = true;
    }

    @Override
    public void onPressionar(float x, float y) {
        super.onPressionar(x, y);
        pressionado = false;
    }

    @Override
    public void onLiberar(float x, float y) {
        JogoView.controleTecla[JogoView.Tecla.CIMA.ordinal()] = true;
    }

    private void tocarSom(int idSom) {
        if (JogoView.opcaoAudio < 3)
            som.tocar(idSom, 1, 1, 0);
    }

    private void adicionaPeca() {

        ppy = -2;
        ppx = grade.length / 2 - 1;

        // Primeira chamada
        if (idPeca == -1)
            idPeca = rand.nextInt(Peca.PECAS.length);
        else
            idPeca = idPrxPeca;
        // idPeca=6;
        idPrxPeca = rand.nextInt(Peca.PECAS.length);

        // Isso acontece muito
        if (idPeca == idPrxPeca)
            idPrxPeca = rand.nextInt(Peca.PECAS.length);

        peca = Peca.PECAS[idPeca];
        corPeca = Peca.Cores[idPeca];
    }

    private void adicionarPecaNaGrade() {

        for (int col = 0; col < peca.length; col++) {
            for (int lin = 0; lin < peca[col].length; lin++) {

                if (peca[lin][col] != 0) {

                    grade[col + ppx][lin + ppy] = idPeca;

                }
            }
        }
    }

    private boolean validaMovimento(int[][] peca, int px, int py) {

        if (peca == null)
            return false;

        for (int col = 0; col < peca.length; col++) {
            for (int lin = 0; lin < peca[col].length; lin++) {
                if (peca[lin][col] == 0)
                    continue;

                int prxPx = col + px; // Proxima posicao peca x
                int prxPy = lin + py; // Proxima posicao peca y

                if (prxPx < 0 || prxPx >= grade.length)
                    return false;

                if (prxPy >= grade[0].length)
                    return false;

                if (prxPy < 0)
                    continue;

                // Colidiu com uma peca na grade
                if (grade[prxPx][prxPy] > ESPACO_VAZIO)
                    return false;

            }
        }

        return true;
    }

    private boolean parouForaDaGrade() {

        if (peca == null)
            return false;

        for (int lin = 0; lin < peca.length; lin++) {
            for (int col = 0; col < peca[lin].length; col++) {
                if (peca[lin][col] == 0)
                    continue;
                // Fora da grade
                if (lin + ppy < 0)
                    return true;
            }
        }

        return false;
    }

    private boolean colidiu(int px, int py) {

        if (peca == null)
            return false;

        for (int col = 0; col < peca.length; col++) {
            for (int lin = 0; lin < peca[col].length; lin++) {
                if (peca[lin][col] == 0)
                    continue;

                int prxPx = col + px;
                int prxPy = lin + py;

                if (depurar) {
                    if (prxPx < 0 || prxPx >= grade.length)
                        return false;
                }
                // Chegou na base da grade
                if (prxPy == grade[0].length)
                    return true;

                // Fora da grade
                if (prxPy < 0)
                    continue;

                // Colidiu com uma peca na grade
                if (grade[prxPx][prxPy] > ESPACO_VAZIO)
                    return true;
            }
        }

        return false;
    }

    private boolean marcarLinha() {
        int multPontos = 0;

        for (int lin = grade[0].length - 1; lin >= 0; lin--) {
            boolean linhaCompleta = true;

            for (int col = grade.length - 1; col >= 0; col--) {
                if (grade[col][lin] == ESPACO_VAZIO) {
                    linhaCompleta = false;
                    break;
                }
            }

            if (linhaCompleta) {
                multPontos++;
                for (int col = grade.length - 1; col >= 0; col--) {
                    grade[col][lin] = LINHA_COMPLETA;
                }
            }
        }

        pontos += multPontos * multPontos;
        linhasFeistas += multPontos;

        if (nivel == 9 && linhasFeistas >= 9) {
            estado = Estado.GANHOU;

        } else if (linhasFeistas >= 9) {
            nivel++;
            linhasFeistas = 0;
        }

        return multPontos > 0;
    }

    private void descerColunas() {
        for (int col = 0; col < grade.length; col++) {
            for (int lin = grade[0].length - 1; lin >= 0; lin--) {

                if (grade[col][lin] == LINHA_COMPLETA) {
                    int moverPara = lin;
                    int prxLinha = lin - 1;

                    for (; prxLinha > -1; prxLinha--) {
                        if (grade[col][prxLinha] == LINHA_COMPLETA)
                            continue;
                        else
                            break;

                    }

                    for (; moverPara > -1; moverPara--, prxLinha--) {

                        if (prxLinha > -1)
                            grade[col][moverPara] = grade[col][prxLinha];
                        else
                            grade[col][moverPara] = ESPACO_VAZIO;

                    }
                }
            }
        }

        tocarSom(somMarcarLinha);
    }

    protected void girarPeca(boolean sentidoHorario) {
        if (peca == null)
            return;

        final int[][] temp = new int[peca.length][peca.length];

        for (int i = 0; i < peca.length; i++) {
            for (int j = 0; j < peca.length; j++) {
                if (sentidoHorario)
                    temp[j][peca.length - i - 1] = peca[i][j];
                else
                    temp[peca.length - j - 1][i] = peca[i][j];
            }
        }

        System.out.println("Antes:");
        imprimirArray(peca);
        System.out.println("Depois:");
        imprimirArray(temp);

        if (validaMovimento(temp, ppx, ppy)) {
            peca = temp;
        }
    }

    private void imprimirArray(int[][] arr) {
        for (int lin = 0; lin < arr.length; lin++) {
            for (int col = 0; col < arr[lin].length; col++) {
                System.out.print(arr[lin][col] + "\t");
            }

            System.out.println();
        }
    }

    private void girarReposicionarPeca(boolean sentidoHorario) {
        if (peca == null)
            return;

        int tempPx = ppx;
        final int[][] tempPeca = new int[peca.length][peca.length];

        for (int i = 0; i < peca.length; i++) {
            for (int j = 0; j < peca.length; j++) {
                if (sentidoHorario)
                    tempPeca[j][peca.length - i - 1] = peca[i][j];
                else
                    tempPeca[peca.length - j - 1][i] = peca[i][j];
            }
        }

        // Reposiciona peca na tela
        for (int i = 0; i < tempPeca.length; i++) {
            for (int j = 0; j < tempPeca.length; j++) {
                if (tempPeca[j][i] == 0) {
                    continue;
                }

                int prxPx = i + tempPx;

                if (prxPx < 0)
                    tempPx = tempPx - prxPx;

                else if (prxPx == grade.length)
                    tempPx = tempPx - 1;

            }
        }

        if (validaMovimento(tempPeca, tempPx, ppy)) {
            peca = tempPeca;
            ppx = tempPx;
        }
    }

    @Override
    public void desenhar(Canvas g, Paint p) {

        for (int col = 0; col < grade.length; col++) {
            for (int lin = 0; lin < grade[0].length; lin++) {
                int valor = grade[col][lin];

                if (valor == ESPACO_VAZIO)
                    continue;

                if (valor == LINHA_COMPLETA)
                    p.setColor(Color.RED);
                else
                    p.setColor(Peca.Cores[valor]);

                int x = col * largBloco + ESPACAMENTO;
                int y = lin * altBloco + ESPACAMENTO;

                g.drawRect(x, y, x + largBloco - ESPACAMENTO, y + altBloco - ESPACAMENTO, p);
            }
        }

        if (peca != null) {
            p.setColor(corPeca);

            for (int col = 0; col < peca.length; col++) {
                for (int lin = 0; lin < peca[col].length; lin++) {
                    if (peca[lin][col] != 0) {

                        int x = (col + ppx) * largBloco + ESPACAMENTO;
                        int y = (lin + ppy) * altBloco + ESPACAMENTO;

                        g.drawRect(x, y, x + largBloco - ESPACAMENTO, y + altBloco - ESPACAMENTO, p);

                    } else if (depurar) {
                        p.setColor(Color.MAGENTA);
                        int x = (col + ppx) * largBloco + ESPACAMENTO;
                        int y = (lin + ppy) * altBloco + ESPACAMENTO;

                        g.drawRect(x, y, x + largBloco - ESPACAMENTO, y + altBloco - ESPACAMENTO, p);

                        p.setColor(corPeca);
                    }
                }
            }
        }

        int miniatura = largBloco / 4;
        int[][] prxPeca = Peca.PECAS[idPrxPeca];
        p.setColor(Peca.Cores[idPrxPeca]);

        int x, y;

        for (int col = 0; col < prxPeca.length; col++) {
            for (int lin = 0; lin < prxPeca[col].length; lin++) {
                if (prxPeca[lin][col] == 0)
                    continue;

                x = col * miniatura + ESPACAMENTO;
                y = lin * miniatura + ESPACAMENTO;

                g.drawRect(x, y, x + miniatura - ESPACAMENTO, y + miniatura - ESPACAMENTO, p);
            }
        }

        textoPlacar.desenha(g, p, "Level " + nivel + " - " + linhasFeistas, largura / 2 - 20, 20);
        textoPlacar.desenha(g, p, String.valueOf(pontos), largura - 50, 20);

        int tempY, tempX = 0;

        if (estado != Estado.JOGANDO) {
            tempY = altura / 2 + texto.getTamanhoFonte() / 2;
            tempX = largura / 2 - texto.getTamanhoFonte() - texto.getTamanhoFonte() / 2;

            if (estado == Estado.GANHOU)
                texto.desenha(g, p, "Finalmente!", tempX, tempY);
            else
                texto.desenha(g, p, "Deu ruim!", tempX, tempY);
        }

        if (JogoView.pausado) {
            tempY = altura / 2 + textoPausa.getTamanhoFonte() / 2;
            tempX = largura / 2 - textoPausa.getTamanhoFonte() - textoPausa.getTamanhoFonte() / 2;

            textoPausa.desenha(g, p, "PAUSA", tempX, tempY);
        }
    }
}
