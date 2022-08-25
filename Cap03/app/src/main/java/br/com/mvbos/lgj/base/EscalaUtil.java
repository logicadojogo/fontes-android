package br.com.mvbos.lgj.base;

public class EscalaUtil {

    public enum Modo {
        PROPORCIONAL, TELA_CHEIA
    }

    public float xScale = 1;
    public float yScale = 1;
    public Modo modo = Modo.TELA_CHEIA;

    public void configure(float larguraTela, float alturaTela, float larguraCena, float alturaCena, Modo modo) {
        xScale = alturaTela / alturaCena;
        yScale = larguraTela / larguraCena;

        switch (modo) {
            case PROPORCIONAL:
                break;
            case TELA_CHEIA:
                //Expandir para ocupar tela inteira
                if (yScale > xScale) {
                    float temp = xScale;
                    xScale = yScale;
                    yScale = temp;
                }
                break;
        }
    }

    public void configure(float larguraTela, float alturaTela, float larguraCena, float alturaCena) {
        this.configure(larguraTela, alturaTela, larguraCena, alturaCena, Modo.TELA_CHEIA);
    }
}
