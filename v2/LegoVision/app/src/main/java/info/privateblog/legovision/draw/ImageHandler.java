package info.privateblog.legovision.draw;

import android.graphics.Bitmap;

/**
 * Created by Merl1n on 01.03.2017.
 */

public class ImageHandler {
    private static final int HEIGHT = 320;
    private static final int WIDTH = 240;

    private Bitmap currentImage = null; //height:width

    public ImageHandler(){}

    public Bitmap getCurrentImage() {
        return currentImage;
    }

    public synchronized void setCurrentImage(Bitmap currentImage) {
        this.currentImage = currentImage;
    }
}
