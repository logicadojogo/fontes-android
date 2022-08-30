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

public class JogoCenarioDoRusso extends CenarioPadrao {

    enum Estado {
        JOGANDO, GANHOU, PERDEU
    }

    private final int _LARG = 80;

    private int dx, dy;

    private boolean moveu;

    private Elemento fruta;

    private Elemento serpente;

    private Elemento[] rastros;

    private int blocoPorTela;

    private int temporizador = 0;

    private int contadorRastro = 5;

    private Texto texto = new Texto(25);

    private Texto textoPausa = new Texto(40);

    private Random rand = new Random();

    private Estado estado = Estado.JOGANDO;

    public JogoCenarioDoRusso(int largura, int altura) {
        super(largura, altura);
    }

    @Override
    public void carregar() {
        blocoPorTela = (largura / _LARG) * (altura / _LARG) - 1;

        rastros = new Elemento[blocoPorTela + contadorRastro];

        fruta = new Elemento(0, 0, _LARG, _LARG);
        fruta.setCor(Color.RED);

        serpente = new Elemento(0, 0, _LARG, _LARG);
        serpente.setAtivo(true);
        serpente.setCor(Color.YELLOW);

        Util.centraliza(serpente, largura, altura);

        // define direcao inicial
        dy = -1;

        serpente.setVel(JogoView.velocidade);

        for (int i = 0; i < rastros.length; i++) {
            rastros[i] = new Elemento(serpente.getPx(), serpente.getPy(), _LARG, _LARG);
            rastros[i].setCor(Color.GREEN);
            rastros[i].setAtivo(true);
        }
    }

    @Override
    public void descarregar() {
        serpente = null;
        rastros = null;
        fruta = null;
    }

    @Override
    public void atualizar() {

        if (estado != Estado.JOGANDO) {
            return;
        }

        if (!moveu) {
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
        }

        if (temporizador >= 20) {
            temporizador = 0;
            moveu = false;

            int x = serpente.getPx();
            int y = serpente.getPy();

            // Somando mais um em _LARG para espacamento
            serpente.setPx(serpente.getPx() + (_LARG + 1) * dx);
            serpente.setPy(serpente.getPy() + (_LARG + 1) * dy);

            if (Util.saiu(serpente, largura, altura)) {
                serpente.setAtivo(false);
                estado = Estado.PERDEU;

            } else {

                for (int i = 0; i < contadorRastro; i++) {
                    if (Util.colide(serpente, rastros[i])) {
                        serpente.setAtivo(false);
                        estado = Estado.PERDEU;
                        break;
                    }
                }
            }

            if (Util.colide(fruta, serpente)) {
                contadorRastro++;
                fruta.setAtivo(false);

                if (blocoPorTela - contadorRastro == 0) {
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

        } else
            temporizador += serpente.getVel();

        if (estado == Estado.JOGANDO && blocoPorTela - contadorRastro > 0) {
            while (!adicionaProximaFruta())
                ;
        }

    }

    private boolean adicionaProximaFruta() {

        // Adicionando frutas
        if (!fruta.isAtivo()) {
            int x = rand.nextInt(largura - _LARG);
            int y = rand.nextInt(altura - _LARG);

            fruta.setPx(x);
            fruta.setPy(y);

            fruta.setAtivo(true);

            if (Util.colide(fruta, serpente)) {
                fruta.setAtivo(false);

            } else {
                for (int i = 0; i < contadorRastro; i++) {
                    if (Util.colide(fruta, rastros[i])) {
                        fruta.setAtivo(false);
                        break;
                    }
                }
            }

        }

        return fruta.isAtivo();
    }

    @Override
    public void desenhar(Canvas g, Paint p) {

        if (fruta.isAtivo()) {
            fruta.desenha(g, p);
        }

        for (int i = 0; i < contadorRastro; i++) {
            Elemento rastro = rastros[i];
            rastro.desenha(g, p);
        }

        serpente.desenha(g, p);

        texto.setCor(Color.RED);
        texto.desenha(g, p, String.valueOf(blocoPorTela - contadorRastro), largura - 30, altura);


        if (estado != Estado.JOGANDO) {
            texto.setCor(Color.WHITE);
            int tempX = largura / 2 - texto.getTamanhoFonte() - texto.getTamanhoFonte() / 2;
            int tempY = altura / 2 + texto.getTamanhoFonte() / 2;

            if (estado == Estado.GANHOU)
                texto.desenha(g, p, "Ganhou!", tempX, tempY);
            else
                texto.desenha(g, p, "V i x e !", tempX, tempY);

        } else if (JogoView.pausado) {
            int tempX = largura / 2 - textoPausa.getTamanhoFonte() - textoPausa.getTamanhoFonte() / 2;
            int tempY = altura / 2 + textoPausa.getTamanhoFonte() / 2;
            textoPausa.desenha(g, p, "PAUSA", tempX, tempY);
        }
    }

}
