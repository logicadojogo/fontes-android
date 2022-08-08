package br.com.mvbos.lgj;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class UmJogoActivity extends Activity {

    class Elemento {
        public int x, y, largura, altura;
        public float velocidade;

        public Elemento(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.largura = width;
            this.altura = height;
        }
    }

    class JogoView extends View {

        private boolean jogando = true;
        private boolean fimDeJogo = false;

        private Elemento tiro;
        private Elemento jogador;
        private Elemento[] blocos;

        private Point size = new Point(320, 480);

        private int pontos;
        private int larg = 50; // Largura padrao
        private int linhaLimite = 350;
        private java.util.Random r = new java.util.Random();

        private Paint p = new Paint();

        private final int larguraTela = 320;
        private final int alturaTela = 480;

        public JogoView(Context context) {
            super(context);

            tiro = new Elemento(0, 0, 1, 0);
            jogador = new Elemento(0, 0, larg, larg);

            jogador.velocidade = 5;
            jogador.x = larguraTela / 2 - jogador.largura / 2;
            jogador.y = alturaTela - jogador.altura;

            tiro.altura = alturaTela - jogador.altura;

            blocos = new Elemento[5];
            for (int i = 0; i < blocos.length; i++) {
                int espaco = i * larg + 10 * (i + 1);
                blocos[i] = new Elemento(espaco, 0, larg, larg);
                blocos[i].velocidade = 1;
            }
        }

        private void atualizaJogo() {
            if (fimDeJogo)
                return;

            if (jogador.x < 0)
                jogador.x = larguraTela - jogador.largura;

            if (jogador.x + jogador.largura > larguraTela)
                jogador.x = 0;

            tiro.y = 0;
            tiro.x = jogador.x + jogador.largura / 2;

            for (Elemento bloco : blocos) {

                if (bloco.y > linhaLimite) {
                    fimDeJogo = true;
                    break;
                }

                if (colide(bloco, tiro) && bloco.y > 0) {
                    bloco.y -= bloco.velocidade * 2;
                    tiro.y = bloco.y;

                } else {
                    int sorte = r.nextInt(10);
                    if (sorte == 0)
                        bloco.y += bloco.velocidade + 1;
                    else if (sorte == 5)
                        bloco.y -= bloco.velocidade;
                    else
                        bloco.y += bloco.velocidade;
                }
            }

            pontos = pontos + blocos.length;
        }

        private boolean colide(Elemento a, Elemento b) {
            if (a.x + a.largura >= b.x && a.x <= b.x + b.largura) {
                return true;
            }

            return false;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            p.setColor(Color.WHITE);
            canvas.drawRect(0, 0, larguraTela, alturaTela, p);

            p.setColor(Color.RED);
            canvas.drawRect(tiro.x, tiro.y, tiro.x + tiro.largura, tiro.y + tiro.altura, p);

            p.setColor(Color.GREEN);
            canvas.drawRect(jogador.x, jogador.y, jogador.x + jogador.largura, jogador.y + jogador.altura, p);

            p.setColor(Color.BLUE);
            for (Elemento bloco : blocos) {
                canvas.drawRect(bloco.x, bloco.y, bloco.x + bloco.largura, bloco.y + bloco.altura, p);
            }

            p.setColor(Color.GRAY);
            canvas.drawLine(0, linhaLimite, larguraTela, linhaLimite, p);

            canvas.drawText("Pontos: " + pontos, 0, 10, p);

            atualizaJogo();
            invalidate();
        }

        public void onTouch(MotionEvent event) {
            int action = event.getActionMasked();

            if (action == MotionEvent.ACTION_UP) {
                jogador.x = (int) event.getX();
            }
        }
    }

    private JogoView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definir orientacao padrao
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Window window = getWindow();
        // Habilitar fullscreen
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.requestFeature(Window.FEATURE_NO_TITLE);

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        // Manter tela ligada
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        view = new JogoView(this);
        setContentView(view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        view.onTouch(event);
        return true;
    }
}