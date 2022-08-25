package br.com.mvbos.lgj.pingpong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.Random;

import br.com.mvbos.lgj.JogoView;
import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.base.Elemento;
import br.com.mvbos.lgj.base.Texto;
import br.com.mvbos.lgj.base.Util;

public class JogoCenario extends CenarioPadrao {

    private float inc = 0.5f;

    private Ponto pontoA, pontoB;

    private Bola bola;

    private Elemento esquerda;

    private Elemento direita;

    private Elemento areaPausa;

    private boolean reiniciarJogada;

    private final Texto textoPausa;

    // Modo em casa
    private int idx;

    private Bola[] bolaArr = new Bola[0];

    private Random rand;

    private final int TAMANHO_FONTE = 60;

    private final Typeface FONTE = Typeface.create("Consolas", Typeface.NORMAL);

    public JogoCenario(int largura, int altura) {
        super(largura, altura);

        bola = new Bola();
        esquerda = new Elemento();
        direita = new Elemento();

        pontoA = new Ponto(FONTE, TAMANHO_FONTE);
        pontoB = new Ponto(FONTE, TAMANHO_FONTE);

        areaPausa = new Elemento();
        textoPausa = new Texto(FONTE, TAMANHO_FONTE);
    }

    @Override
    public void carregar() {
        bola.setVel(JogoView.velocidade);

        pontoA.setPx(largura / 2 - 120);
        pontoA.setPy(TAMANHO_FONTE);

        pontoB.setPx(largura / 2 + 120 - TAMANHO_FONTE / 2);
        pontoB.setPy(TAMANHO_FONTE);

        esquerda.setVel(5);
        esquerda.setAltura(70);
        esquerda.setLargura(10);
        esquerda.setCor(Color.WHITE);

        direita.setVel(esquerda.getVel());
        direita.setAltura(esquerda.getAltura());
        direita.setLargura(esquerda.getLargura());
        direita.setCor(esquerda.getCor());
        direita.setPx(largura - direita.getLargura());

        areaPausa.setAltura(altura);
        areaPausa.setLargura(largura - 80);

        textoPausa.setCor(Color.WHITE);

        Util.centraliza(bola, largura, altura);
        Util.centraliza(direita, 0, altura);
        Util.centraliza(esquerda, 0, altura);
        Util.centraliza(areaPausa, largura, altura);

        bola.setAtivo(true);
        direita.setAtivo(true);
        esquerda.setAtivo(true);
        areaPausa.setAtivo(true);

        if (!JogoView.modoNormal) {
            rand = new Random();
            bolaArr = new Bola[30];

            for (int i = 0; i < bolaArr.length; i++) {
                int v = rand.nextInt(3) + 1;

                bolaArr[i] = new Bola();
                bolaArr[i].setDirX(i % 2 == 0 ? -1 : 1);

                bolaArr[i].setVel(Bola.VEL_INICIAL * v);
                bolaArr[i].setAltura(bola.getAltura() * v);
                bolaArr[i].setLargura(bola.getLargura() * v);

                Util.centraliza(bolaArr[i], largura, altura);
            }
        }

        JogoView.pausado = false;
    }

    @Override
    public void onToque(float x, float y) {
        super.onToque(x, y);
        if (Util.colide(elToque, areaPausa)) {
            // Toque no centro pausa o jogo
            JogoView.pausado = !JogoView.pausado;
        } else if (Util.colide(elToque, esquerda)) {
            // Toque no jogador da esquerda ativoa botao A
            JogoView.controleTecla[JogoView.Tecla.BA.ordinal()] = true;
        } else if (Util.colide(elToque, direita)) {
            // Toque no jogador da direita ativoa botao B
            JogoView.controleTecla[JogoView.Tecla.BB.ordinal()] = true;
        }
    }

    @Override
    public void onToqueLiberar(float x, float y) {
        JogoView.liberaTeclas();
    }

    @Override
    public void descarregar() {
    }

    @Override
    public void atualizar() {

        if (JogoView.pausado)
            return;

        bola.incPx();
        bola.incPy();

        if (JogoView.controleTecla[JogoView.Tecla.BA.ordinal()]) {
            esquerda.setPy((int) movimento.y - esquerda.getAltura() / 2);
        } else if (JogoView.controleTecla[JogoView.Tecla.BB.ordinal()]) {
            direita.setPy((int) movimento.y - direita.getAltura() / 2);
        }

        validaPosicao(esquerda);
        validaPosicao(direita);

        if (reiniciarJogada) {
            reiniciarJogada = false;
            bola.inverteX();
            bola.setVel(JogoView.velocidade);
            Util.centraliza(bola, largura, altura);

        } else {
            reiniciarJogada = validaColisao(bola);
        }

        validaPosicao(bola);

        for (Bola b : bolaArr) {

            if (!b.isAtivo())
                continue;

            b.incPx();
            b.incPy();

            boolean saiu = validaColisao(b);
            if (saiu) {
                b.setAtivo(false);
                Util.centraliza(b, largura, altura);
            } else
                validaPosicao(b);
        }

    }

    @Override
    public void desenhar(Canvas g, Paint p) {
        // Desenha linha de fundo

        short linhaCentral = (short) (largura / 2 - 2);

        for (int i = 0; i < altura; i += 20) {
            p.setColor(Color.WHITE);
            g.drawRect(linhaCentral, i, linhaCentral + 4, i + 10, p);
        }

        pontoA.desenha(g, p);
        pontoB.desenha(g, p);

        // depurarColisao(esquerda, g);
        // depurarColisao(direita, g);

        bola.desenha(g, p);

        for (Bola b : bolaArr) {
            b.desenha(g, p);
        }

        esquerda.desenha(g, p);
        direita.desenha(g, p);

        if (JogoView.pausado) {
            int tempX = largura / 2 - TAMANHO_FONTE - TAMANHO_FONTE / 2;
            int tempY = altura / 2 + TAMANHO_FONTE / 2;
            textoPausa.desenha(g, p, "PAUSA", tempX, tempY);
        }

    }

    private boolean validaColisao(Bola b) {
        boolean saiu = false;

        if (Util.colide(esquerda, b)) {
            rebate(esquerda, b);

        } else if (Util.colide(direita, b)) {
            rebate(direita, b);

        } else if (b.getPx() < 0 || b.getPx() + b.getLargura() > largura) {

            saiu = true;

            if (b.getPx() < 0)
                pontoB.add();
            else
                pontoA.add();

        } else if (b.getPy() <= 0 || b.getPy() + b.getAltura() >= altura) {
            // Colisao no topo ou base da tela
            b.inverteY();
        }

        return saiu;
    }

    /*
    public void depurarColisao(Elemento el, Graphics2D g2d) {
        int p = el.getPx() == 0 ? 6 : -6;
        int x1 = el.getPx() + p;

        g2d.setColor(Color.RED);
        g2d.drawLine(x1, el.getPy(), el.getPx() + p, el.getPy() + el.getAltura() / 3);

        g2d.setColor(Color.GREEN);
        g2d.drawLine(x1, el.getPy() + el.getAltura() - el.getAltura() / 3, el.getPx() + p, el.getPy() + el.getAltura());
    }
    */

    public void rebate(Elemento raquete, Bola bola) {
        float vx = bola.getVelX();
        float vy = bola.getVelY();

        if (bola.getPy() < raquete.getPy() + raquete.getAltura() / 3) {
            bola.setDirY(-1);

            vx += inc;
            vy += inc;

            if (bola.getPy() < raquete.getPy()) {
                vy += inc;
            }

        } else if (bola.getPy() > raquete.getPy() + raquete.getAltura() - raquete.getAltura() / 3) {
            bola.setDirY(1);

            vx += inc;
            vy += inc;

            if (bola.getPy() + bola.getAltura() > raquete.getPy() + raquete.getAltura()) {
                vy += inc;
            }

        } else {
            vx += inc;
            vy = 1;
        }

        bola.inverteX();
        bola.incVel(vx, vy);

        if (bolaArr.length > 0) {
            if (idx < bolaArr.length) {
                bolaArr[idx++].setAtivo(true);

            } else {
                idx = 0;
            }
        }
    }

    private void validaPosicao(Elemento el) {
        if (el.getPy() < 0)
            el.setPy(0);
        else if (el.getPy() + el.getAltura() > altura)
            el.setPy(altura - el.getAltura());
    }

}
