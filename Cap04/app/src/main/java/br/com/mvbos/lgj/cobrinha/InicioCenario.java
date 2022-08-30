package br.com.mvbos.lgj.cobrinha;

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
    private Menu menuVelInicial;
    private Menu menuIniciar;

    @Override
    public void carregar() {
        int largPadraoMenu = 190;
        int fontePadraoMenu = 25;
        int espacamentoMenu = 18;

        menuJogo = new Menu("Fase");
        menuJogo.setCor(Color.WHITE);
        menuJogo.setSelecionado(true);
        menuJogo.setLargura(largPadraoMenu);
        menuJogo.setTamanhoFonte(fontePadraoMenu);

        String[] opcoes = new String[Nivel.niveis.length + 1];
        for (int i = 0; i < opcoes.length - 1; i++) {
            opcoes[i] = "Nível " + i;
        }

        opcoes[opcoes.length - 1] = "Do Russo";
        menuJogo.addOpcoes(opcoes);

        menuVelInicial = new Menu("Vel.");
        menuVelInicial.setCor(Color.WHITE);
        menuVelInicial.setLargura(largPadraoMenu);
        menuVelInicial.setTamanhoFonte(fontePadraoMenu);
        menuVelInicial.addOpcoes("Normal", "Rápido", "Lento");

        menuIniciar = new Menu(null);
        menuIniciar.setCor(Color.WHITE);
        menuIniciar.setLargura(largPadraoMenu);
        menuIniciar.setTamanhoFonte(fontePadraoMenu);
        menuIniciar.addOpcoes("Jogar");
        menuIniciar.setLargura(60);

        Util.centraliza(menuJogo, largura, altura);
        Util.centraliza(menuVelInicial, largura, altura);
        Util.centraliza(menuIniciar, largura, altura);

        menuVelInicial.setPy(menuJogo.getPy() + menuJogo.getAltura() + espacamentoMenu);
        menuIniciar.setPy(menuVelInicial.getPy() + menuVelInicial.getAltura() + espacamentoMenu);

        menuJogo.setAtivo(true);
        menuJogo.setSelecionado(true);
        menuVelInicial.setAtivo(true);
    }

    @Override
    public void onLiberar(float x, float y) {
        JogoView.controleTecla[JogoView.Tecla.BA.ordinal()] = true;
    }

    @Override
    public void descarregar() {
        JogoView.nivel = menuJogo.getOpcaoId();

        switch (menuVelInicial.getOpcaoId()) {
            case 0:
                JogoView.velocidade = 4;
                break;
            case 1:
                JogoView.velocidade = 8;
                break;
            case 2:
                JogoView.velocidade = 2;
        }

    }

    @Override
    public void atualizar() {
        if (JogoView.controleTecla[JogoView.Tecla.BA.ordinal()]) {

            if (Util.colide(elToque, menuJogo)) {
                menuJogo.setSelecionado(true);
                menuVelInicial.setSelecionado(false);
                menuJogo.trocaOpcao(false);

            } else if (Util.colide(elToque, menuVelInicial)) {
                menuJogo.setSelecionado(false);
                menuVelInicial.setSelecionado(true);
                menuVelInicial.trocaOpcao(true);

            } else if (Util.colide(elToque, menuIniciar)) {
                JogoView.mudarCena = true;
            }

            JogoView.liberaTeclas();
        }
    }

    @Override
    public void desenhar(Canvas g, Paint p) {
        menuJogo.desenha(g, p);
        menuVelInicial.desenha(g, p);
        menuIniciar.desenha(g, p);
    }

}
