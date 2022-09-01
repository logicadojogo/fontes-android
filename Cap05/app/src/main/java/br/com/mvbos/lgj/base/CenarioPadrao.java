package br.com.mvbos.lgj.base;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class CenarioPadrao {

    public static final int MAX_MOVIMENTOS = 2;
    protected int altura, largura;

    protected Elemento elToque;

    protected PointF movimento[] = new PointF[MAX_MOVIMENTOS];

    public CenarioPadrao(int largura, int altura) {
        this.altura = altura;
        this.largura = largura;
        elToque = new Elemento(0, 0, 5, 5);
        elToque.setCor(Color.GRAY);
        elToque.setAtivo(true);

        for (int i = 0; i < movimento.length; ++i)
            movimento[i] = new PointF(-Float.MAX_VALUE, -Float.MAX_VALUE);
    }

    public abstract void carregar();

    public abstract void descarregar();

    public abstract void atualizar();

    public abstract void desenhar(Canvas g, Paint p);

    public void onPressionar(float x, float y) {
        elToque.setPx((int) x);
        elToque.setPy((int) y);
    }

    public void onPressionarLongo(float x, float y) {
        elToque.setPx((int) x);
        elToque.setPy((int) y);
    }

    public void onLiberar(float x, float y) {
        elToque.setPx((int) x);
        elToque.setPy((int) y);
    }

    public void onMovimentar(int index, float x, float y) {
        if (index > movimento.length)
            return;

        movimento[index].x = x;
        movimento[index].y = y;
    }

}
