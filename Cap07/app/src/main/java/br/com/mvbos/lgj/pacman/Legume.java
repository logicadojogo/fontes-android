package br.com.mvbos.lgj.pacman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import br.com.mvbos.lgj.JogoView;
import br.com.mvbos.lgj.base.Elemento;
import br.com.mvbos.lgj.pacman.JogoCenario.Direcao;

public class Legume extends Elemento {

    public enum Modo {
        PRESO, ATIVO, INATIVO, FANTASMA, CACANDO, FUGINDO;
    }

    public enum Tipo {
        VERMELHO, ROXO, AMARELO, VERDE;
    }

    private Tipo tipo;
    private Modo modo = Modo.PRESO;
    private Direcao direcao = Direcao.OESTE;

    private int linha;
    private int coluna;
    private int lnOlhos; // Linha olhos

    private static Bitmap olhos;
    private static Bitmap sprite;

    private Rect origemImg = new Rect();
    private Rect destinoImg = new Rect();

    public Legume(Tipo tipo) {
        super(0, 0, 16, 16);
        this.tipo = tipo;
        this.coluna = tipo.ordinal();

        this.olhos = JogoView.getImagemUtil().carregar("imagens/olhos.png");
        this.sprite = JogoView.getImagemUtil().carregar("imagens/sprite_inimigos.png");
    }

    @Override
    public void atualiza() {
        incPx(getVel() * getDx());
        incPy(getVel() * getDy());

        if (getDx() == -1)
            lnOlhos = 0;
        else if (getDx() == 1)
            lnOlhos = 1;
        else if (getDy() == -1)
            lnOlhos = 2;
        else if (getDy() == 1)
            lnOlhos = 3;

        if (modo == Modo.FUGINDO)
            linha = 1;
        else
            linha = 0;
    }

    @Override
    public void desenha(Canvas g, Paint p) {
        int pX = getPx() - 6;
        int pY = getPy() + JogoCenario.ESPACO_TOPO - 6;

        int largMoldura = sprite.getWidth() / 4;
        int altMoldura = sprite.getHeight() / 2;

        int largImg = largMoldura * coluna;
        int altImg = altMoldura * linha;

        if (modo != Modo.FANTASMA) {
            destinoImg.set(pX, pY, pX + largMoldura, pY + altMoldura);
            origemImg.set(largImg, altImg, largImg + largMoldura, altImg + altMoldura);
            g.drawBitmap(sprite, origemImg, destinoImg, p);
        }

        int altOlho = olhos.getHeight() / 4;
        int direcaoOlho = altOlho * lnOlhos;

        origemImg.set(0, direcaoOlho, olhos.getWidth(), direcaoOlho + altOlho);
        destinoImg.set(pX, pY, pX + olhos.getWidth(), pY + altOlho);
        g.drawBitmap(olhos, origemImg, destinoImg, p);
    }

    public Direcao getDirecao() {
        return direcao;
    }

    public void setDirecao(Direcao direcao) {
        this.direcao = direcao;
    }

    public Modo getModo() {
        return modo;
    }

    public void setModo(Modo modo) {
        this.modo = modo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

}
