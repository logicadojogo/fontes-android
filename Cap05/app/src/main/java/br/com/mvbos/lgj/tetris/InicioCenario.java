package br.com.mvbos.lgj.tetris;

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

    private Menu menuJogo;
    private Menu menuIniciar;

    @Override
    public void carregar() {
        int largPadraoMenu = 190;
        int fontePadraoMenu = 25;
        int espacamentoMenu = 18;

        menuJogo = new Menu("Nível");
        menuJogo.setAtivo(true);
        menuJogo.setCor(Color.WHITE);
        menuJogo.setSelecionado(true);
        menuJogo.setLargura(largPadraoMenu);
        menuJogo.setTamanhoFonte(fontePadraoMenu);
        menuJogo.addOpcoes("1", "2", "3", "4", "5", "6", "7", "8", "9");

        menuIniciar = new Menu(null);
        menuIniciar.setAtivo(true);
        menuIniciar.setCor(Color.WHITE);
        menuIniciar.setLargura(largPadraoMenu);
        menuIniciar.setTamanhoFonte(fontePadraoMenu);
        menuIniciar.addOpcoes("Jogar");
        menuIniciar.setLargura(90);

        Util.centraliza(menuJogo, largura, altura);
        Util.centraliza(menuIniciar, largura, altura);

        menuJogo.setPy(menuJogo.getPy() + menuJogo.getAltura());
        menuIniciar.setPy(menuJogo.getPy() + menuJogo.getAltura() + espacamentoMenu);
    }

    @Override
    public void onLiberar(float x, float y) {
        JogoView.controleTecla[JogoView.Tecla.BA.ordinal()] = true;
    }

    @Override
    public void descarregar() {
        JogoView.nivel = menuJogo.getOpcaoId() + 1;
    }

    @Override
    public void atualizar() {
        if (JogoView.controleTecla[JogoView.Tecla.BA.ordinal()]) {

            if (Util.colide(elToque, menuJogo)) {
                menuJogo.setSelecionado(true);
                menuJogo.trocaOpcao(false);

            } else if (Util.colide(elToque, menuIniciar)) {
                JogoView.mudarCena = true;
            }

            JogoView.liberaTeclas();
        }
    }

    @Override
    public void desenhar(Canvas g, Paint p) {
        menuJogo.desenha(g, p);
        menuIniciar.desenha(g, p);
    }

}
