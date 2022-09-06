package br.com.mvbos.lgj.base;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

public class SomUtil {

    private Context context;
    private SoundPool soundPool;

    public SomUtil(Context context) {
        this.context = context;
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .build();
    }

    public int carregar(int idRecurso) {
        return soundPool.load(context, idRecurso, 1);
    }

    public boolean descarregar(int idSom) {
        return soundPool.unload(idSom);
    }

    public int carregar(String nomeArquivo) {
        try {
            return soundPool.load(context.getAssets().openFd(nomeArquivo), 1);
        } catch (IOException e) {
            Log.e(SomUtil.class.getName(), e.getMessage());
            return -1;
        }
    }

    public void tocar(int idSom, float volumeEsq, float volumeDir, int loop) {
        soundPool.play(idSom, volumeEsq, volumeDir, 0, loop, 1);
    }

}
