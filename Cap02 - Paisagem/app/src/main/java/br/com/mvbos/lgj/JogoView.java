package br.com.mvbos.lgj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

import br.com.mvbos.lgj.base.Elemento;
import br.com.mvbos.lgj.base.Texto;
import br.com.mvbos.lgj.base.Util;
import br.com.mvbos.lgj.invader.Botao;
import br.com.mvbos.lgj.invader.Invader;
import br.com.mvbos.lgj.invader.Tanque;
import br.com.mvbos.lgj.invader.Tiro;


class JogoView extends View {

    private static final int FPS = 1000 / 20;

    // Elementos do jogo
    private int vidas = 3;

    // Desenharemos mais dois tanques na base da tela
    private Elemento vida = new Tanque();

    private Elemento tiroTanque;

    private Elemento tiroChefe;

    private Elemento[] tiros = new Tiro[3];

    private Texto texto = new Texto();

    private Invader chefe;

    private Elemento tanque;

    private Invader[][] invasores = new Invader[11][3];

    private Invader.Tipos[] tipoPorLinha = {Invader.Tipos.PEQUENO, Invader.Tipos.MEDIO, Invader.Tipos.GRANDE};

    // Linha para simular o chao
    private int linhaBase = 60;

    // Controle do espacamento entre os inimigos e outros elementos
    private int espacamento = 15;

    // Contador de inimigos destruidos
    private int destruidos = 0;

    private int dir;

    private int totalInimigos;

    private int contadorEspera;

    boolean novaLinha;

    boolean moverInimigos;

    private int contador;

    private int pontos;

    private int level = 1;

    private Random rand = new Random();

    private Paint paint = new Paint();

    private int larguraTela;

    private int alturaTela;

    private boolean[] controleTecla = new boolean[5];

    private Elemento[] setas = new Botao[2];

    private Elemento elClique;

    public JogoView(Context context, int larguraTela, int alturaTela) {
        super(context);
        this.larguraTela = larguraTela;
        this.alturaTela = alturaTela;
    }

    public void onTouch(MotionEvent event) {
        elClique.setPx((int) event.getX());
        elClique.setPy((int) event.getY());

        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            if (Util.colide(elClique, setas[0])) {
                controleTecla[2] = !controleTecla[2];
                controleTecla[3] = false;
            } else if (Util.colide(elClique, setas[1])) {
                controleTecla[2] = false;
                controleTecla[3] = !controleTecla[3];
            } else {
                controleTecla[4] = true;
            }
        } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            controleTecla[4] = false;
        }
    }

    public void carregarJogo() {
        tanque = new Tanque();
        tanque.setVel(3);
        tanque.setAtivo(true);
        tanque.setPx(larguraTela / 2 - tanque.getLargura() / 2);
        tanque.setPy(alturaTela - tanque.getAltura() - linhaBase);

        tiroTanque = new Tiro();
        tiroTanque.setVel(-15);

        chefe = new Invader(Invader.Tipos.CHEFE);

        tiroChefe = new Tiro(true);
        tiroChefe.setVel(20);
        tiroChefe.setAltura(15);

        texto.setCor(Color.WHITE);

        elClique = new Elemento(0, 0, 10, 10);
        elClique.setAtivo(true);

        setas[0] = new Botao(larguraTela / 2 - 50, alturaTela - 50, 40, 40);
        setas[1] = new Botao(larguraTela / 2 + 10, alturaTela - 50, 40, 40);
        setas[0].setAtivo(true);
        setas[1].setAtivo(true);

        for (int i = 0; i < tiros.length; i++) {
            tiros[i] = new Tiro(true);
        }

        for (int i = 0; i < invasores.length; i++) {
            for (int j = 0; j < invasores[i].length; j++) {
                Invader e = new Invader(tipoPorLinha[j]);
                e.setAtivo(true);
                e.setPx(i * e.getLargura() + (i + 1) * espacamento);
                e.setPy(j * e.getAltura() + j * espacamento + espacamento);
                invasores[i][j] = e;
            }
        }

        dir = 1;
        totalInimigos = invasores.length * invasores[0].length;
        contadorEspera = totalInimigos / level;
    }

    //Iniciar jogo
    long prxAtualizacao = 0;

    @Override
    protected void onDraw(Canvas canvas) {

        //Inicio da atualizacao do jogo dentro do FPS definido
        if (System.currentTimeMillis() >= prxAtualizacao) {

            if (destruidos == totalInimigos) {
                destruidos = 0;
                level++;
                carregarJogo();

                return;
            }

            if (contador > contadorEspera) {
                moverInimigos = true;
                contador = 0;
                contadorEspera = totalInimigos - destruidos - level * level;

            } else {
                contador++;
            }

            if (tanque.isAtivo()) {
                if (controleTecla[2]) {
                    tanque.setPx(tanque.getPx() - tanque.getVel());

                } else if (controleTecla[3]) {
                    tanque.setPx(tanque.getPx() + tanque.getVel());
                }
            }

            // Tocou na tela, adiciona tiro
            if (controleTecla[4] && !tiroTanque.isAtivo()) {
                tiroTanque.setPx(tanque.getPx() + tanque.getLargura() / 2 - tiroTanque.getLargura() / 2);
                tiroTanque.setPy(tanque.getPy() - tiroTanque.getAltura());
                tiroTanque.setAtivo(true);
            }

            if (chefe.isAtivo()) {
                chefe.incPx(tanque.getVel() - 1);

                if (!tiroChefe.isAtivo() && Util.colideX(chefe, tanque)) {
                    addTiroInimigo(chefe, tiroChefe);
                }

                if (chefe.getPx() > larguraTela) {
                    chefe.setAtivo(false);
                }
            }

            boolean colideBordas = false;

            // Percorrendo primeiro as linhas, de baixo para cima
            for (int j = invasores[0].length - 1; j >= 0; j--) {

                // Depois as colunas
                for (int i = 0; i < invasores.length; i++) {

                    Invader inv = invasores[i][j];

                    if (!inv.isAtivo()) {
                        continue;
                    }

                    if (Util.colide(tiroTanque, inv)) {
                        inv.setAtivo(false);
                        tiroTanque.setAtivo(false);

                        destruidos++;
                        pontos = pontos + inv.getPremio() * level;

                        continue;
                    }

                    if (moverInimigos) {

                        inv.atualiza();

                        if (novaLinha) {
                            inv.setPy(inv.getPy() + inv.getAltura() + espacamento);
                        } else {
                            inv.incPx(espacamento * dir);
                        }

                        if (!novaLinha && !colideBordas) {
                            int pxEsq = inv.getPx() - espacamento;
                            int pxDir = inv.getPx() + inv.getLargura() + espacamento;

                            if (pxEsq <= 0 || pxDir >= larguraTela)
                                colideBordas = true;
                        }

                        if (!tiros[0].isAtivo() && inv.getPx() < tanque.getPx()) {
                            addTiroInimigo(inv, tiros[0]);
                        } else if (!tiros[1].isAtivo() && inv.getPx() > tanque.getPx() && inv.getPx() < tanque.getPx() + tanque.getLargura()) {
                            addTiroInimigo(inv, tiros[1]);
                        } else if (!tiros[2].isAtivo() && inv.getPx() > tanque.getPx()) {
                            addTiroInimigo(inv, tiros[2]);
                        }

                        if (!chefe.isAtivo() && rand.nextInt(500) == destruidos) {
                            chefe.setPx(0);
                            chefe.setAtivo(true);
                        }
                    }
                }
            }

            if (moverInimigos && novaLinha) {
                dir *= -1;
                novaLinha = false;

            } else if (moverInimigos && colideBordas) {
                novaLinha = true;
            }

            moverInimigos = false;

            if (tiroTanque.isAtivo()) {
                tiroTanque.incPy(tiroTanque.getVel());

                if (Util.colide(tiroTanque, chefe)) {
                    pontos = pontos + chefe.getPremio() * level;
                    chefe.setAtivo(false);
                    tiroTanque.setAtivo(false);

                } else if (tiroTanque.getPy() < 0) {
                    tiroTanque.setAtivo(false);
                }
            }

            if (tiroChefe.isAtivo()) {
                tiroChefe.incPy(tiroChefe.getVel());

                if (Util.colide(tiroChefe, tanque)) {
                    vidas--;
                    tiroChefe.setAtivo(false);

                } else if (tiroChefe.getPy() > alturaTela - linhaBase - tiroChefe.getAltura()) {
                    tiroChefe.setAtivo(false);
                }
            }

            for (int i = 0; i < tiros.length; i++) {
                if (tiros[i].isAtivo()) {
                    tiros[i].incPy(+10);

                    if (Util.colide(tiros[i], tanque)) {
                        vidas--;
                        tiros[i].setAtivo(false);

                    } else if (tiros[i].getPy() > alturaTela - linhaBase - tiros[i].getAltura())
                        tiros[i].setAtivo(false);
                }
            }

            tanque.atualiza();
            chefe.atualiza();
            prxAtualizacao = System.currentTimeMillis() + FPS;
        }
        //Fim da atualizacao do jogo dentro do FPS definido

        //Hora de desenhar
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, larguraTela, alturaTela, paint);

        if (tiroTanque.isAtivo())
            tiroTanque.desenha(canvas, paint);

        for (int i = 0; i < tiros.length; i++) {
            if (tiros[i].isAtivo()) {
                tiros[i].desenha(canvas, paint);
            }
        }

        if (tiroChefe.isAtivo())
            tiroChefe.desenha(canvas, paint);

        // Desenhe aqui para as naves ficarem acima dos tiros
        for (int i = 0; i < invasores.length; i++) {
            for (int j = 0; j < invasores[i].length; j++) {
                Invader e = invasores[i][j];
                e.desenha(canvas, paint);
            }
        }

        tanque.desenha(canvas, paint);
        chefe.desenha(canvas, paint);

        texto.desenha(canvas, paint, String.valueOf(pontos), 10, 20);
        texto.desenha(canvas, paint, "Level " + level, larguraTela - 100, 20);
        texto.desenha(canvas, paint, String.valueOf(vidas), 10, alturaTela - 10);

        // Linha base
        paint.setColor(Color.GREEN);
        canvas.drawLine(0, alturaTela - linhaBase, larguraTela, alturaTela - linhaBase, paint);

        for (int i = 1; i < vidas; i++) {
            vida.setPx(i * vida.getLargura() + i * espacamento);
            vida.setPy(alturaTela - vida.getAltura());

            vida.desenha(canvas, paint);
        }

        setas[0].desenha(canvas, paint);
        setas[1].desenha(canvas, paint);

        invalidate();
    }

    private void addTiroInimigo(Elemento inimigo, Elemento tiro) {
        //Diminuindo a quanidade de tiros
        if (rand.nextInt(10) > 8) {
            tiro.setAtivo(true);
            tiro.setPx(inimigo.getPx() + inimigo.getLargura() / 2 - tiro.getLargura() / 2);
            tiro.setPy(inimigo.getPy() + inimigo.getAltura());
        }
    }

}
