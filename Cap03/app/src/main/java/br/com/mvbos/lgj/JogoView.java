package br.com.mvbos.lgj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.pingpong.InicioCenario;
import br.com.mvbos.lgj.pingpong.JogoCenario;


public class JogoView extends View {

    public enum Tecla {
        BA, BB
    }

    //Controle do loop do jogo
    private long prxAtualizacao;

    private int larguraTela;

    private int alturaTela;

    private CenarioPadrao cenario;

    private Paint paint = new Paint();

    private static final int FPS = 1000 / 20;

    public static boolean pausado;

    public static int velocidade;

    public static boolean modoNormal;

    public static boolean mudarCena;

    public static boolean[] controleTecla = new boolean[Tecla.values().length];

    public static void liberaTeclas() {
        for (int i = 0; i < controleTecla.length; i++) {
            controleTecla[i] = false;
        }
    }

    public JogoView(Context context, int larguraTela, int alturaTela) {
        super(context);
        this.larguraTela = larguraTela;
        this.alturaTela = alturaTela;
    }

    public void carregarJogo() {
        cenario = new InicioCenario(larguraTela, alturaTela);
        cenario.carregar();
    }

    public boolean onSair() {
        if (cenario instanceof InicioCenario) {
            return true;
        }

        JogoView.mudarCena = true;
        return false;
    }

    public void onTouch(MotionEvent event) {
        if (cenario != null) {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    cenario.onToque(event.getX(), event.getY());
                    break;
                case android.view.MotionEvent.ACTION_UP:
                    cenario.onToqueLiberar(event.getX(), event.getY());
                    break;
                case android.view.MotionEvent.ACTION_MOVE:
                    cenario.onMovimentar(event.getX(), event.getY());
                    break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //Inicio da atualizacao do jogo dentro do FPS definido
        if (System.currentTimeMillis() >= prxAtualizacao) {

            if (cenario != null) {
                if (JogoView.mudarCena) {
                    JogoView.mudarCena = false;

                    // Pressionou menuIniciar ou menuVoltar
                    cenario.descarregar();
                    if (cenario instanceof InicioCenario)
                        cenario = new JogoCenario(larguraTela, alturaTela);
                    else if (cenario instanceof JogoCenario)
                        cenario = new InicioCenario(larguraTela, alturaTela);

                    cenario.carregar();
                }

                cenario.atualizar();
            }

            //liberaTeclas();
            prxAtualizacao = System.currentTimeMillis() + FPS;
        }

        //Hora de desenhar
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, larguraTela, alturaTela, paint);

        if (cenario == null) {
            paint.setColor(Color.WHITE);
            canvas.drawText("Carregando...", 20, 20, paint);

        } else {
            cenario.desenhar(canvas, paint);
        }

        invalidate();
    }
}
