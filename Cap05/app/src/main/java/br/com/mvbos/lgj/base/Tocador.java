package br.com.mvbos.lgj.base;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class Tocador {
    private Context context;
    private MediaPlayer mediaPlayer;

    public Tocador(Context context) {
        this.context = context;
    }

    public void carregar(String nomeArquivo) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(nomeArquivo);

            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            else
                mediaPlayer.reset();

            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            Log.e(Tocador.class.getName(), e.getMessage());
        }
    }

    public void tocar(float volumeEsq, float volumeDir, boolean loop) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.prepare();
                mediaPlayer.setLooping(loop);
                mediaPlayer.setVolume(volumeEsq, volumeDir);

                mediaPlayer.start();

            } catch (IOException e) {
                Log.e(Tocador.class.getName(), e.getMessage());
            }
        }
    }

    public void pausar() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void continuar() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void parar() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
