package br.com.mvbos.lgj.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ImagemUtil {

    private Context context;

    public ImagemUtil(Context context) {
        this.context = context;
    }

    public Bitmap carregar(int idRecurso) {

        try {
            InputStream is = context.getResources().openRawResource(idRecurso);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;

        } catch (IOException e) {
            Log.e(ImagemUtil.class.getName(), e.getMessage());
        }

        return null;
    }

    public Bitmap carregar(String nomeArquivo) {
        try {
            InputStream is = context.getAssets().open(nomeArquivo);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;

        } catch (IOException e) {
            Log.e(ImagemUtil.class.getName(), e.getMessage());
        }

        return null;
    }
}
