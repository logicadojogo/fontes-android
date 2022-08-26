package br.com.mvbos.lgj.pingpong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import br.com.mvbos.lgj.JogoView;
import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.base.Menu;
import br.com.mvbos.lgj.base.Util;

public class InicioCenario extends CenarioPadrao {

    public InicioCenario(int largura, int altura) {
        super(largura, altura);
    }

    private Bola bola;

    private Menu menuModo;

    private Menu menuVeloc;

    private Menu menuIniciar;

    @Override
    public void carregar() {
        bola = new Bola();

        menuModo = new Menu("Modo");
        menuModo.setCor(Color.WHITE);
        menuModo.setSelecionado(true);
        menuModo.addOpcoes("Normal", "Em casa");

        menuVeloc = new Menu("Vel.");
        menuVeloc.setCor(Color.WHITE);
        menuVeloc.addOpcoes("Normal", "Rápido", "Lento");

        menuIniciar = new Menu("");
        menuIniciar.setCor(Color.WHITE);
        menuIniciar.addOpcoes("Jogar");
        menuIniciar.setLargura(60);

        Util.centraliza(bola, largura, altura);
        Util.centraliza(menuModo, largura, altura);
        Util.centraliza(menuVeloc, largura, altura);
        Util.centraliza(menuIniciar, largura, altura);

        menuModo.setPy(menuModo.getPy() + 20);
        menuVeloc.setPy(menuModo.getPy() + menuModo.getAltura());
        menuIniciar.setPy(menuVeloc.getPy() + menuVeloc.getAltura());

        bola.setAtivo(true);
    }

    @Override
    public void onLiberar(float x, float y) {
        JogoView.controleTecla[JogoView.Tecla.BA.ordinal()] = true;
    }

    @Override
    public void descarregar() {
        JogoView.velocidade = bola.getVel();
        JogoView.modoNormal = menuModo.getOpcaoId() == 0;
    }

    @Override
    public void atualizar() {
        if (JogoView.controleTecla[JogoView.Tecla.BA.ordinal()]) {

            if (Util.colide(elToque, menuModo)) {
                menuModo.setSelecionado(true);
                menuVeloc.setSelecionado(false);
                menuModo.trocaOpcao(true);

            } else if (Util.colide(elToque, menuVeloc)) {
                menuModo.setSelecionado(false);
                menuVeloc.setSelecionado(true);
                menuVeloc.trocaOpcao(true);

                if (menuVeloc.getOpcaoId() == 0) {
                    bola.setVel(Bola.VEL_INICIAL);

                } else if (menuVeloc.getOpcaoId() == 1) {
                    bola.setVel(Bola.VEL_INICIAL * 2);

                } else {
                    bola.setVel(Bola.VEL_INICIAL / 2);
                }
            } else if (Util.colide(elToque, menuIniciar)) {
                JogoView.mudarCena = true;
            }

            JogoView.liberaTeclas();
        }

        // Controle da bola
        bola.incPx();
        bola.incPy();

        if (Util.colide(menuModo, bola) || Util.colide(menuVeloc, bola)) {
            bola.inverteX();
            bola.inverteY();
        }

        if (bola.getPx() < 0 || bola.getPx() + bola.getLargura() > largura) {
            // Colisao nas laterais da tela
            bola.inverteX();

        } else if (bola.getPy() <= 0 || bola.getPy() + bola.getAltura() >= altura) {
            // Colisao no topo ou base da tela
            bola.inverteY();
        }

        if (bola.getPy() < 0)
            bola.setPy(0);
        else if (bola.getPy() + bola.getAltura() > altura)
            bola.setPy(altura - bola.getAltura());
    }

    @Override
    public void desenhar(Canvas g, Paint p) {
        bola.desenha(g, p);
        menuModo.desenha(g, p);
        menuVeloc.desenha(g, p);
        menuIniciar.desenha(g, p);
    }

}
