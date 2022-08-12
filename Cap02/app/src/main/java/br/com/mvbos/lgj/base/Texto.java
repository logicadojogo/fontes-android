package br.com.mvbos.lgj.base;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

public class Texto extends Elemento {

    private Typeface fonte;

    public Texto() {
        //fonte = new Font("Tahoma", Font.PLAIN, 16);
        fonte = Typeface.create("Tahoma", Typeface.NORMAL);
    }

    public Texto(Typeface fonte) {
        this.fonte = fonte;
    }

    public void desenha(Canvas g, Paint p, String texto) {
        desenha(g, p, texto, getPx(), getPy());
    }

    public void desenha(Canvas g, Paint p, String texto, int px, int py) {
        p.setColor(getCor());
        p.setTypeface(fonte);
        g.drawText(texto, px, py, p);
    }

    public Typeface getFonte() {
        return fonte;
    }

    public void setFonte(Typeface fonte) {
        this.fonte = fonte;
    }

}
