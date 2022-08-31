package br.com.mvbos.lgj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.cobrinha.InicioCenario;
import br.com.mvbos.lgj.cobrinha.JogoCenario;
import br.com.mvbos.lgj.cobrinha.JogoCenarioDoRusso;
import br.com.mvbos.lgj.cobrinha.Nivel;

public class JogoView extends View {

    private static final float SCROLL_PRECISION = 50;

    public enum Tecla {
        CIMA, BAIXO, ESQUERDA, DIREITA, BA, BB
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

    public static int nivel;

    public static int velocidade;

    public static boolean pausado;

    public static boolean mudarCena;

    public static boolean[] controleTecla = new boolean[Tecla.values().length];

    public static void liberaTeclas() {
        for (int i = 0; i < controleTecla.length; i++) {
            controleTecla[i] = false;
        }
    }

    public JogoView(Context context, int larguraTela, int alturaTela) {
        super(context);
        /* jogo feito para uma tela de  480x640 */
        this.larguraCena = 480;
        this.alturaCena = 640;
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

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1) {
        if (movimentoNaoLido()) {
            return true;
        }

        if (motionEvent.getX() - motionEvent1.getX() > SCROLL_PRECISION) {
            JogoView.controleTecla[Tecla.ESQUERDA.ordinal()] = true;
        } else if (motionEvent1.getX() - motionEvent.getX() > SCROLL_PRECISION) {
            JogoView.controleTecla[Tecla.DIREITA.ordinal()] = true;
        }

        if (motionEvent.getY() - motionEvent1.getY() > SCROLL_PRECISION) {
            JogoView.controleTecla[Tecla.CIMA.ordinal()] = true;
        } else if (motionEvent1.getY() - motionEvent.getY() > SCROLL_PRECISION) {
            JogoView.controleTecla[Tecla.BAIXO.ordinal()] = true;
        }

        return true;
    }

    private boolean movimentoNaoLido() {
        return JogoView.controleTecla[Tecla.ESQUERDA.ordinal()] ||
                JogoView.controleTecla[Tecla.DIREITA.ordinal()] ||
                JogoView.controleTecla[Tecla.CIMA.ordinal()] ||
                JogoView.controleTecla[Tecla.BAIXO.ordinal()];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Inicio da atualizacao do jogo dentro do FPS definido
        if (System.currentTimeMillis() >= prxAtualizacao) {

            if (cenario != null) {
                if (JogoView.mudarCena) {
                    // Pressionou menuIniciar ou botão voltar do aparelho

                    JogoView.mudarCena = false;
                    liberaTeclas();
                    cenario.descarregar();

                    if (cenario instanceof InicioCenario) {
                        cenario = null;
                        if (JogoView.nivel < Nivel.niveis.length)
                            cenario = new JogoCenario(larguraCena, alturaCena);
                        else
                            cenario = new JogoCenarioDoRusso(larguraCena, alturaCena);
                    } else {
                        cenario = null;
                        cenario = new InicioCenario(larguraCena, alturaCena);
                    }

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
