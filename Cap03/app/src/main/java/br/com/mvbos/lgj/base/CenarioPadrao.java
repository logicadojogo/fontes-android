package br.com.mvbos.lgj.base;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class CenarioPadrao {

    protected int altura, largura;

    protected Elemento elClique;

    protected PointF movimento = new PointF();

    public CenarioPadrao(int largura, int altura) {
        this.altura = altura;
        this.largura = largura;
        elClique = new Elemento(0, 0, 5, 5);
        elClique.setAtivo(true);
    }

    public abstract void carregar();

    public abstract void descarregar();

    public abstract void atualizar();

    public abstract void desenhar(Canvas g, Paint p);

    public void onToque(float x, float y) {
        elClique.setPx((int) x);
        elClique.setPy((int) y);
    }

    public void onToqueLiberar(float x, float y) {
        elClique.setPx((int) x);
        elClique.setPy((int) y);
    }

    public void onMovimentar(float x, float y) {
        movimento.x = x;
        movimento.y = y;
    }
}
