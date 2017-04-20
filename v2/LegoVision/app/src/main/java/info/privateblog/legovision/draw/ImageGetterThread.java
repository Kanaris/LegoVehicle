package info.privateblog.legovision.draw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import info.privateblog.legovision.bluetooth.BluetoothConnector;
import info.privateblog.legovision.bluetooth.exception.BluetoothException;

/**
 * Created by Merl1n on 01.03.2017.
 */
public class ImageGetterThread extends Thread {
    private static final  char[]COMMAND = {'*', 'R', 'D', 'Y', '*'};
    private static final int WIDTH = 320; //640;
    private static final int HEIGHT = 240; //480;

    private ImageHandler imageHandler = null;
    private BluetoothConnector connector = null;

    private boolean running = true;

    public ImageGetterThread(ImageHandler imageHandler, BluetoothConnector connector) {
        super();
        this.imageHandler = imageHandler;
        this.connector = connector;
    }

    @Override
    public void run() {
        byte[]inputArray = new byte[10000];

        try {
            while (connector.isConnected() && running) {
                while (!isImageStart(0)) {};

                int byte3 = 0xFF&connector.read();
                int byte2 = 0xFF&connector.read();
                int byte1 = 0xFF&connector.read();

                int size = (byte3<<16) + (byte2<<8) + byte1;

                for (int i = 0; i < size; i++) {
                    inputArray[i] = (byte)connector.read();
                }

                ByteArrayInputStream inStream = new ByteArrayInputStream(inputArray, 0, size);
                Bitmap biImage = BitmapFactory.decodeStream(inStream);

                imageHandler.setCurrentImage(biImage);
            }
        } catch (Exception e) {}
    }

    private boolean isImageStart(int index) throws BluetoothException, IOException {
        if (index < COMMAND.length) {
            if (COMMAND[index] == connector.read()) {
                return isImageStart(++index);
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
