package br.com.mvbos.lgj.pacman;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import br.com.mvbos.lgj.JogoView;
import br.com.mvbos.lgj.base.Elemento;
import br.com.mvbos.lgj.pacman.JogoCenario.Direcao;

public class Pizza extends Elemento {

    private Direcao direcao = Direcao.OESTE;

    private int linha;
    private int coluna;

    private Rect origemImg = new Rect();
    private Rect destinoImg = new Rect();

    public Pizza() {
        super(0, 0, 16, 16);
        setImagem(JogoView.getImagemUtil().carregar("imagens/sprite_pizza.png"));
    }

    @Override
    public void atualiza() {
        incPx(getVel() * getDx());
        incPy(getVel() * getDy());

        if (getDx() == 1)
            linha = 0;
        else if (getDx() == -1)
            linha = 1;
        else if (getDy() == -1)
            linha = 2;
        else if (getDy() == 1)
            linha = 3;

        if (getDx() + getDy() != 0)
            coluna++;

        if (coluna > 3)
            coluna = 0;
    }

    @Override
    public void desenha(Canvas g, Paint p) {

        int pX = getPx() - 6;
        int pY = getPy() + JogoCenario.ESPACO_TOPO - 6;

        // Largura e altura da moldura
        int largMoldura = getImagem().getWidth() / 4;
        int altMoldura = getImagem().getHeight() / 4;

        // Largura e altura do recorte da imagem
        int largImg = largMoldura * coluna;
        int altImg = altMoldura * linha;

        destinoImg.set(pX, pY, pX + largMoldura, pY + altMoldura);
        origemImg.set(largImg, altImg, largImg + largMoldura, altImg + altMoldura);

        g.drawBitmap(getImagem(), origemImg, destinoImg, p);
    }

    public Direcao getDirecao() {
        return direcao;
    }

    public void setDirecao(Direcao direcao) {
        this.direcao = direcao;
    }
}
