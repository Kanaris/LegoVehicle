package info.privateblog.legovision.draw;

import android.view.SurfaceHolder;

/**
 * Created by Merl1n on 01.03.2017.
 */
public class SurfaceDrawer implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private ImageHandler imageHandler;

    public SurfaceDrawer(ImageHandler imageHandler) {
        this.imageHandler = imageHandler;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(holder, imageHandler);
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        // завершаем работу потока
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // если не получилось, то будем пытаться еще и еще
            }
        }
    }
}