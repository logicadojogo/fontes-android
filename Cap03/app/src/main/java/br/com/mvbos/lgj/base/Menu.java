package br.com.mvbos.lgj.base;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Menu extends Texto {

    private short idx;
    private String rotulo;
    private String[] opcoes;
    private boolean selecionado;

    public Menu(String rotulo) {
        super();

        this.rotulo = rotulo;
        setLargura(120);
        setAltura(20);
        setAtivo(true);
    }

    public void addOpcoes(String... opcao) {
        opcoes = opcao;
    }

    @Override
    public void desenha(Canvas g, Paint p) {
        if (opcoes == null)
            return;

        p.setColor(getCor());
        p.setTextSize(getTamanhoFonte());

        if (rotulo != null && !rotulo.isEmpty()) {
            super.desenha(g, p, String.format("%s: <%s>", rotulo, opcoes[idx]), getPx(), getPy() + getAltura());
        } else {
            super.desenha(g, p, String.format("<%s>", opcoes[idx]), getPx(), getPy() + getAltura());
        }

        if (selecionado)
            g.drawLine(getPx(), getPy() + getAltura() + 5, getPx() + getLargura(), getPy() + getAltura() + 5, p);
    }

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        this.rotulo = rotulo;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public int getOpcaoId() {
        return idx;
    }

    public String getOpcaoTexto() {
        return opcoes[idx];
    }

    public void trocaOpcao(boolean esquerda) {
        if (!isSelecionado() || !isAtivo())
            return;

        idx += esquerda ? -1 : 1;

        if (idx < 0)
            idx = (short) (opcoes.length - 1);
        else if (idx == opcoes.length)
            idx = 0;
    }

}
