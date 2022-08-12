package br.com.mvbos.lgj.invader;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import br.com.mvbos.lgj.base.Elemento;

public class Botao extends Elemento {

    public Botao(int px, int py, int largura, int altura) {
        super(px, py, largura, altura);
    }

    @Override
    public void desenha(Canvas g, Paint p) {
        p.setColor(Color.GRAY);
        g.drawOval(convertPxy(), p);

        p.setColor(Color.RED);
        g.drawOval(convertPxy(getPx() + 2, getPy() + 2, getLargura() - 4, getAltura() - 4), p);
    }
}
