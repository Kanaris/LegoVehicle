package info.privateblog.legovision.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * Created by Merl1n on 01.03.2017.
 */

public class DrawThread extends Thread {
    private boolean runFlag = false;

    private ImageHandler imageHandler;
    private SurfaceHolder holder;

    private Rect srcRect;
    private Rect destRect;

    private Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DrawThread(SurfaceHolder holder, ImageHandler imageHandler) {
        this.imageHandler = imageHandler;
        this.holder = holder;
    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    public void run() {
        Canvas canvas = null;

        while (runFlag) {
            Bitmap currentImage = imageHandler.getCurrentImage();
            canvas = null;
            try {
                if (currentImage != null) {
                    canvas = holder.lockCanvas();

                    if (canvas != null) {
                        if (destRect == null) {
                            int canvasWidth = canvas.getWidth();
                            int canvasHeight = canvas.getHeight();

                            double scale = 1.0*canvasHeight/currentImage.getHeight();

                            srcRect = new Rect(0,0, currentImage.getWidth()-1, currentImage.getHeight()-1);
                            destRect = new Rect(0,0, (int)(currentImage.getWidth()*scale), (int)(currentImage.getHeight()*scale));
                        }

                        canvas.drawBitmap(currentImage, srcRect, destRect, bitmapPaint);
                        //canvas.drawBitmap(currentImage, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.currentThread().sleep(100);
            } catch (Exception e) {}
        }
    }
}