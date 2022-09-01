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
    private Menu menuAudio;
    private Menu menuIniciar;

    @Override
    public void carregar() {
        int espacamentoMenu = 18;

        menuJogo = new Menu("Nível");
        menuJogo.setSelecionado(true);
        configurarMenu(menuJogo, "1", "2", "3", "4", "5", "6", "7", "8", "9");

        menuAudio = new Menu("Áudio");
        configurarMenu(menuAudio, "Ligado", "Efeitos", "Desligado");
        menuAudio.setLargura(menuAudio.getLargura() * 2);

        menuIniciar = new Menu(null);
        configurarMenu(menuIniciar, "Jogar");

        menuJogo.setPy(menuJogo.getPy() + menuJogo.getAltura());
        menuAudio.setPy(menuJogo.getPy() + menuJogo.getAltura() + espacamentoMenu);
        menuIniciar.setPy(menuAudio.getPy() + menuAudio.getAltura() + espacamentoMenu);
    }

    private void configurarMenu(Menu menu, String... opcoes) {
        menu.setAtivo(true);
        menu.setCor(Color.WHITE);
        menu.setLargura(110);
        menu.setTamanhoFonte(25);
        menu.addOpcoes(opcoes);
        Util.centraliza(menu, super.largura, super.altura);
    }

    @Override
    public void onLiberar(float x, float y) {
        JogoView.controleTecla[JogoView.Tecla.BA.ordinal()] = true;
    }

    @Override
    public void descarregar() {
        JogoView.nivel = menuJogo.getOpcaoId() + 1;
        JogoView.opcaoAudio = menuAudio.getOpcaoId() + 1;
    }

    @Override
    public void atualizar() {
        if (JogoView.controleTecla[JogoView.Tecla.BA.ordinal()]) {

            if (Util.colide(elToque, menuJogo)) {
                menuAudio.setSelecionado(false);

                menuJogo.setSelecionado(true);
                menuJogo.trocaOpcao(false);

            } else if (Util.colide(elToque, menuAudio)) {
                menuJogo.setSelecionado(false);

                menuAudio.setSelecionado(true);
                menuAudio.trocaOpcao(false);

            } else if (Util.colide(elToque, menuIniciar)) {
                JogoView.mudarCena = true;
            }

            JogoView.liberaTeclas();
        }
    }

    @Override
    public void desenhar(Canvas g, Paint p) {
        menuJogo.desenha(g, p);
        menuAudio.desenha(g, p);
        menuIniciar.desenha(g, p);
    }

}
