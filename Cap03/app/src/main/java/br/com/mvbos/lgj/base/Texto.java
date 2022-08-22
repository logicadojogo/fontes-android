package br.com.mvbos.lgj.base;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

public class Texto extends Elemento {

    private Typeface fonte;
    private int tamanhoFonte;

    public Texto() {
        fonte = Typeface.create("Tahoma", Typeface.NORMAL);
        tamanhoFonte = 16;
    }

    public Texto(Typeface fonte, int tamanhoFonte) {
        this.fonte = fonte;
        this.tamanhoFonte = tamanhoFonte;
    }

    public void desenha(Canvas g, Paint p, String texto) {
        desenha(g, p, texto, getPx(), getPy());
    }

    public void desenha(Canvas g, Paint p, String texto, int px, int py) {
        p.setColor(getCor());
        p.setTypeface(fonte);
        p.setTextSize(tamanhoFonte);
        g.drawText(texto, px, py, p);
    }

    public Typeface getFonte() {
        return fonte;
    }

    public void setFonte(Typeface fonte) {
        this.fonte = fonte;
    }

    public int getTamanhoFonte() {
        return tamanhoFonte;
    }

    public void setTamanhoFonte(int tamanhoFonte) {
        this.tamanhoFonte = tamanhoFonte;
    }
}
