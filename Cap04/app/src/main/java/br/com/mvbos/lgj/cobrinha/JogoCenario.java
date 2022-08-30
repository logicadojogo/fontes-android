package br.com.mvbos.lgj.cobrinha;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import br.com.mvbos.lgj.JogoView;
import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.base.Elemento;
import br.com.mvbos.lgj.base.Texto;
import br.com.mvbos.lgj.base.Util;

public class JogoCenario extends CenarioPadrao {

    enum Estado {
        JOGANDO, GANHOU, PERDEU
    }

    private static final int RASTRO_INICIAL = 5;

    private int dx, dy;

    private boolean moveu;

    private int temporizador = 0;

    private int contadorRastro = RASTRO_INICIAL;

    private Elemento fruta;

    private Elemento serpente;

    private Elemento[] nivel;

    private Elemento[] rastros;

    private Texto texto;

    private Texto textoPausa;

    private Texto btnPausa;

    private Random rand = new Random();

    // Frutas para finalizar o level
    private int dificuldade = 10;

    private int contadorNivel = 0;

    private Estado estado = Estado.JOGANDO;

    private final int _ALT;

    private final int _LARG;

    public JogoCenario(int largura, int altura) {
        super(largura, altura);
        _ALT = Math.round(altura / (float) Nivel.niveis[0].length); //linhas
        _LARG = Math.round(largura / (float) Nivel.niveis[0][0].length); //colunas
    }

    @Override
    public void carregar() {
        texto = new Texto("Arial", 25);
        texto.setCor(Color.YELLOW);

        textoPausa = new Texto(40);
        textoPausa.setCor(Color.WHITE);

        btnPausa = new Texto(25);
        btnPausa.setAtivo(true);
        btnPausa.setCor(Color.WHITE);
        btnPausa.setAlturaLargura(20, 20);
        btnPausa.setPxy(largura - btnPausa.getLargura(), 5);

        // define direcao inicial
        dy = 1;
        rastros = new Elemento[dificuldade + RASTRO_INICIAL];

        fruta = new Elemento(0, 0, _LARG, _ALT);
        fruta.setCor(Color.RED);

        serpente = new Elemento(0, 0, _LARG, _ALT);
        serpente.setAtivo(true);
        serpente.setCor(Color.YELLOW);
        serpente.setVel(JogoView.velocidade);

        for (int i = 0; i < rastros.length; i++) {
            rastros[i] = new Elemento(serpente.getPx(), serpente.getPy(), _LARG, _ALT);
            rastros[i].setCor(Color.GREEN);
            rastros[i].setAtivo(true);
        }

        char[][] nivelSelecionado = Nivel.niveis[JogoView.nivel];
        nivel = new Elemento[nivelSelecionado.length * 2];

        for (int linha = 0; linha < nivelSelecionado.length; linha++) {
            for (int coluna = 1; coluna < nivelSelecionado[0].length; coluna++) {
                if (nivelSelecionado[linha][coluna] != ' ') {

                    Elemento e = new Elemento();
                    e.setAtivo(true);
                    e.setCor(Color.LTGRAY);

                    e.setPx(_LARG * coluna);
                    e.setPy(_ALT * linha);

                    e.setAltura(_ALT);
                    e.setLargura(_LARG);

                    nivel[contadorNivel++] = e;
                }
            }
        }
    }

    @Override
    public void onLiberar(float x, float y) {
        if (Util.colide(elToque, btnPausa)) {
            JogoView.pausado = !JogoView.pausado;
            JogoView.liberaTeclas();
        } else {
            JogoView.controleTecla[JogoView.Tecla.BA.ordinal()] = true;
        }
    }

    @Override
    public void descarregar() {
        fruta = null;
        rastros = null;
        serpente = null;
    }

    @Override
    public void atualizar() {

        if (estado != Estado.JOGANDO || JogoView.pausado) {
            return;
        }

        if (!moveu && JogoView.controleTecla[JogoView.Tecla.BA.ordinal()]) {
            if (dy != 0) {
                if (JogoView.controleTecla[JogoView.Tecla.ESQUERDA.ordinal()]) {
                    dx = -1;
                } else if (JogoView.controleTecla[JogoView.Tecla.DIREITA.ordinal()]) {
                    dx = 1;
                }

                if (dx != 0) {
                    dy = 0;
                    moveu = true;
                }

            } else if (dx != 0) {
                if (JogoView.controleTecla[JogoView.Tecla.CIMA.ordinal()]) {
                    dy = -1;
                } else if (JogoView.controleTecla[JogoView.Tecla.BAIXO.ordinal()]) {
                    dy = 1;
                }

                if (dy != 0) {
                    dx = 0;
                    moveu = true;
                }
            }

            JogoView.liberaTeclas();
        }

        if (temporizador >= 20) {
            temporizador = 0;
            moveu = false;

            int x = serpente.getPx();
            int y = serpente.getPy();

            serpente.setPx(serpente.getPx() + _LARG * dx);
            serpente.setPy(serpente.getPy() + _ALT * dy);

            if (Util.saiu(serpente, largura, altura)) {
                serpente.setAtivo(false);
                estado = Estado.PERDEU;
            } else {
                // colisao com cenario
                for (int i = 0; i < contadorNivel; i++) {
                    if (Util.colide(serpente, nivel[i])) {
                        serpente.setAtivo(false);
                        estado = Estado.PERDEU;
                        break;
                    }
                }

                // colisao com o rastro
                for (int i = 0; i < contadorRastro; i++) {
                    if (Util.colide(serpente, rastros[i])) {
                        serpente.setAtivo(false);
                        estado = Estado.PERDEU;
                        break;
                    }
                }
            }

            if (Util.colide(fruta, serpente)) {
                // Adiciona uma pausa
                temporizador = -10;
                contadorRastro++;
                fruta.setAtivo(false);

                if (contadorRastro == rastros.length) {
                    serpente.setAtivo(false);
                    estado = Estado.GANHOU;
                }

            }

            for (int i = 0; i < contadorRastro; i++) {
                Elemento rastro = rastros[i];
                int tx = rastro.getPx();
                int ty = rastro.getPy();

                rastro.setPx(x);
                rastro.setPy(y);

                x = tx;
                y = ty;
            }

        } else {
            temporizador += serpente.getVel();
        }

        // Adicionando frutas
        if (estado == Estado.JOGANDO && !fruta.isAtivo()) {
            int x = rand.nextInt(Math.round(largura / (float) _LARG));
            int y = rand.nextInt(Math.round(altura / (float) _ALT));

            fruta.setPx(x * _LARG);
            fruta.setPy(y * _ALT);
            fruta.setAtivo(true);

            // colisao com a serpente
            if (Util.colide(fruta, serpente)) {
                fruta.setAtivo(false);
                return;
            }

            // colisao com rastro
            for (int i = 0; i < contadorRastro; i++) {
                if (Util.colide(fruta, rastros[i])) {
                    fruta.setAtivo(false);
                    return;
                }
            }

            // colisao com cenario
            for (int i = 0; i < contadorNivel; i++) {
                if (Util.colide(fruta, nivel[i])) {
                    fruta.setAtivo(false);
                    return;
                }
            }

        }
    }

    @Override
    public void desenhar(Canvas g, Paint p) {
        int tempY, tempX = 0;

        if (fruta.isAtivo()) {
            fruta.desenha(g, p);
        }

        for (Elemento e : nivel) {
            if (e == null)
                break;

            e.desenha(g, p);
        }

        for (int i = 0; i < contadorRastro; i++) {
            rastros[i].desenha(g, p);
        }

        serpente.desenha(g, p);
        btnPausa.desenha(g, p, "| |", btnPausa.getPx() - 5, btnPausa.getPy() + btnPausa.getTamanhoFonte());
        texto.desenha(g, p, String.valueOf(rastros.length - contadorRastro), largura - 35, altura - 5);

        if (estado != Estado.JOGANDO) {
            tempY = altura / 2 + texto.getTamanhoFonte() / 2;
            tempX = largura / 2 - texto.getTamanhoFonte() - texto.getTamanhoFonte() / 2;

            if (estado == Estado.GANHOU)
                texto.desenha(g, p, "Ganhou!", tempX, tempY);
            else
                texto.desenha(g, p, "V i x e !", tempX, tempY);
        }

        if (JogoView.pausado) {
            tempY = altura / 2 + textoPausa.getTamanhoFonte() / 2;
            tempX = largura / 2 - textoPausa.getTamanhoFonte() - textoPausa.getTamanhoFonte() / 2;

            textoPausa.desenha(g, p, "PAUSA", tempX, tempY);
        }
    }
}
