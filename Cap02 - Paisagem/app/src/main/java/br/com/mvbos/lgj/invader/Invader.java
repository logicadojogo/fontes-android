package br.com.mvbos.lgj.invader;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import br.com.mvbos.lgj.base.Elemento;

public class Invader extends Elemento {

    public enum Tipos {
        PEQUENO, MEDIO, GRANDE, CHEFE
    }

    private Tipos tipo;
    private boolean aberto;

    public Invader(Tipos t) {
        this.tipo = t;

        setLargura(20);
        setAltura(20);
    }

    @Override
    public void atualiza() {
        aberto = !aberto;
    }

    @Override
    public void desenha(Canvas g, Paint p) {

        if (!isAtivo())
            return;

        int larg = getLargura();

        if (tipo == Tipos.PEQUENO) {

            larg = larg - 2;

            p.setColor(Color.BLUE);

            if (aberto) {
                // Desenha um circulo azul com quadrados ao redor
                g.drawOval(convertPxy(getPx(), getPy(), larg, getAltura()), p);

                g.drawRect(convertPxy(getPx() - 5, getPy() - 5, 5, 5), p);
                g.drawRect(convertPxy(getPx() + larg, getPy() - 5, 5, 5), p);

                g.drawRect(convertPxy(getPx() - 5, getPy() + getLargura(), 5, 5), p);
                g.drawRect(convertPxy(getPx() + larg, getPy() + larg, 5, 5), p);

            } else {
                // Desenha um quadrado azul
                g.drawRect(convertPxy(getPx(), getPy(), larg, getAltura()), p);
            }

        } else if (tipo == Tipos.MEDIO) {
            p.setColor(Color.YELLOW);

            if (aberto) {
                // Desenha um quadrado vazio bordas na cor laranja
                p.setStyle(Paint.Style.STROKE);
                g.drawRect(convertPxy(getPx(), getPy(), larg, getAltura()), p);
                p.setStyle(Paint.Style.FILL_AND_STROKE);
            } else {
                // Desenha um quadrado preenchido na cor laranja
                g.drawRect(convertPxy(getPx(), getPy(), larg, getAltura()), p);
            }

        } else if (tipo == Tipos.GRANDE) {

            larg = larg + 4;

            if (aberto) {
                // Desenha um retangulo em p� na cor cinza escuro
                p.setColor(Color.GRAY);
                g.drawRect(convertPxy(getPx(), getPy(), getAltura(), larg), p);

            } else {
                // Desenha um retangulo deitado na cor cinza
                p.setColor(Color.GRAY);
                g.drawRect(convertPxy(getPx(), getPy(), larg, getAltura()), p);
            }

        } else {
            // Tenta desenhar algo parecido com um disco voador com luzes
            // piscantes
            larg = larg + 10;

            p.setColor(Color.RED);
            g.drawOval(convertPxy(getPx(), getPy(), larg, getAltura()), p);

            if (aberto) {
                // Tres quadrados brancos
                p.setColor(Color.WHITE);

                g.drawRect(convertPxy(getPx() + 7, getPy() + getAltura() / 2 - 2, 4, 4), p);
                g.drawRect(convertPxy(getPx() + 13, getPy() + getAltura() / 2 - 2, 4, 4), p);
                g.drawRect(convertPxy(getPx() + 19, getPy() + getAltura() / 2 - 2, 4, 4), p);
            }
        }
    }

    public int getPremio() {
        switch (tipo) {
            case PEQUENO:
                return 300;

            case MEDIO:
                return 200;

            case GRANDE:
                return 100;

            default:
                return 1000;
        }
    }
}
