package br.com.mvbos.lgj.invader;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import br.com.mvbos.lgj.base.Elemento;

public class Tanque extends Elemento {

    private final int cano = 8;

    private final int escotilha = 10;

    public Tanque() {
        setLargura(30);
        setAltura(15);
    }

    @Override
    public void atualiza() {
    }

    @Override
    public void desenha(Canvas g, Paint p) {
        p.setColor(Color.GREEN);
        g.drawRect(convertPxy(getPx() + getLargura() / 2 - cano / 2, getPy() - cano, cano, cano), p);

        g.drawRect(convertPxy(getPx(), getPy(), getLargura(), getAltura()), p);

        p.setColor(Color.YELLOW);
        g.drawOval(convertPxy(getPx() + getLargura() / 2 - escotilha / 2, getPy() + getAltura() / 2 - escotilha / 2, escotilha, escotilha), p);
    }

}
