package br.com.mvbos.lgj.invader;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import br.com.mvbos.lgj.base.Elemento;

public class Tiro extends Elemento {

    private boolean inimigo;

    public Tiro() {
        setLargura(5);
        setAltura(5);
    }

    public Tiro(boolean inimigo) {
        this();
        this.inimigo = inimigo;
    }

    @Override
    public void atualiza() {
    }

    @Override
    public void desenha(Canvas g, Paint p) {
        if (!isAtivo())
            return;

        p.setColor(inimigo ? Color.RED : Color.WHITE);

        g.drawRect(convertPxy(getPx(), getPy(), getLargura(), getAltura()), p);
    }

}
