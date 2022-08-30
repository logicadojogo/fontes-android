package br.com.mvbos.lgj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.pingpong.InicioCenario;
import br.com.mvbos.lgj.pingpong.JogoCenario;

public class JogoView extends View {

    public enum Tecla {
        BA, BB
    }

    public enum ModoEscala {
        ORIGINAL, PROPORCIONAL, TELA_CHEIA
    }

    public PointF escala = new PointF(1, 1);
    public ModoEscala modoEscala = ModoEscala.TELA_CHEIA;

    //Controle do loop do jogo
    private long prxAtualizacao;

    private final int larguraTela;

    private final int alturaTela;

    private final int larguraCena;

    private final int alturaCena;

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
        /* jogo feito para uma tela de  480x320 */
        this.larguraCena = 480;
        this.alturaCena = 320;
        this.larguraTela = larguraTela;
        this.alturaTela = alturaTela;
        modoEscala = ModoEscala.TELA_CHEIA;
        this.configurarEscala();
    }

    public void configurarEscala() {
        if (ModoEscala.ORIGINAL == modoEscala) {
            // Resetar valor padrao
            escala.x = 1;
            escala.y = 1;
        } else {
            //PROPORCIONAL ou TELA_CHEIA
            escala.x = larguraTela / (float) larguraCena;
            escala.y = alturaTela / (float) alturaCena;

            if (ModoEscala.PROPORCIONAL == modoEscala) {
                if (escala.x > escala.y)
                    escala.x = escala.y;
                else
                    escala.y = escala.x;
            }
        }
    }

    public void carregarJogo() {
        cenario = new InicioCenario(larguraCena, alturaCena);
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
        if (cenario == null)
            return;

        for (int i = 0; i < event.getPointerCount(); ++i) {
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            //Corrige a posicao do toque
            float x = event.getX(pointerIndex) / escala.x;
            float y = event.getY(pointerIndex) / escala.y;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    cenario.onPressionar(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    cenario.onLiberar(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    cenario.onMovimentar(i, x, y);
                    break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Inicio da atualizacao do jogo dentro do FPS definido
        if (System.currentTimeMillis() >= prxAtualizacao) {

            if (cenario != null) {
                if (JogoView.mudarCena) {
                    // Pressionou menuIniciar ou botão voltar do aparelho
                    JogoView.mudarCena = false;
                    liberaTeclas();
                    cenario.descarregar();

                    if (cenario instanceof InicioCenario)
                        cenario = new JogoCenario(larguraCena, alturaCena);
                    else
                        cenario = new InicioCenario(larguraCena, alturaCena);

                    cenario.carregar();
                }

                cenario.atualizar();
            }

            prxAtualizacao = System.currentTimeMillis() + FPS;
        }

        //Hora de desenhar
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, larguraTela, alturaTela, paint);

        canvas.scale(escala.x, escala.y);

        //Debug tamaho cenario
        //paint.setColor(Color.MAGENTA);
        //canvas.drawRect(0, 0, larguraCena, alturaCena, paint);

        if (cenario == null) {
            paint.setColor(Color.WHITE);
            canvas.drawText("Carregando...", 20, 20, paint);

        } else {
            cenario.desenhar(canvas, paint);
        }

        invalidate();
    }
}
