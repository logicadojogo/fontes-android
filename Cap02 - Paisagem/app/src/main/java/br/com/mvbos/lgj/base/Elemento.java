package br.com.mvbos.lgj.base;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Elemento {

    private int px;
    private int py;
    private int largura;
    private int altura;
    private int vel;
    private boolean ativo;
    private int cor;

    private final RectF r = new RectF();

    public Elemento() {
    }

    public Elemento(int px, int py, int largura, int altura) {
        this.px = px;
        this.py = py;
        this.largura = largura;
        this.altura = altura;
    }

    public void atualiza() {
    }

    public void desenha(Canvas g, Paint p) {
        g.drawRect(convertPxy(), p);
    }

    public int getLargura() {
        return largura;
    }

    public void setLargura(int largura) {
        this.largura = largura;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getPx() {
        return px;
    }

    public void setPx(int px) {
        this.px = px;
    }

    public int getPy() {
        return py;
    }

    public void setPy(int py) {
        this.py = py;
    }

    public int getVel() {
        return vel;
    }

    public void setVel(int vel) {
        this.vel = vel;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }

    public void incPx(int x) {
        px = px + x;
    }

    public void incPy(int y) {
        py = py + y;
    }

    public RectF convertPxy() {
        r.set(px, py, px + largura, py + altura);
        return r;
    }

    public RectF convertPxy(float px, float py, float largura, float altura) {
        r.set(px, py, px + largura, py + altura);
        return r;
    }

}
