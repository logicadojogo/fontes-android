package br.com.mvbos.lgj.pingpong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import br.com.mvbos.lgj.base.Texto;

public class Ponto extends Texto {
    private short ponto;

    public Ponto(Typeface fonte, int tamanhoFonte) {
        super(fonte, tamanhoFonte);
        super.setCor(Color.WHITE);
    }

    public void add() {
        ponto++;
    }

    public short getPonto() {
        return ponto;
    }

    public void setPonto(short ponto) {
        this.ponto = ponto;
    }

    @Override
    public void desenha(Canvas g, Paint p) {
        p.setTextSize(getTamanhoFonte());
        super.desenha(g, p, Short.toString(ponto), getPx(), getPy());
    }
}
