package info.privateblog.bluetooth;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.microedition.io.StreamConnection;

import info.privateblog.LegoRobot;
import info.privateblog.gpio.GPIOUtil;
import info.privateblog.video.VideoGetterThread;

public class ProcessConnectionThread implements Runnable {
	private static int TIMEOUT_SHORT = 100;
	private static int TIMEOUT_LONG = 300;
	
	private StreamConnection mConnection = null;
	private VideoGetterThread videoThread = null;
	
    // Constant that indicate command from devices
    private static final int EXIT_CMD = -1;

    public ProcessConnectionThread(StreamConnection connection) {
        mConnection = connection;
    }

    @Override
    public void run() {
        try {
            // prepare to receive data
            InputStream inputStream = mConnection.openInputStream();
            OutputStream outputStream = mConnection.openOutputStream();

            System.out.println("waiting for input");

            while (true) {
                int command = inputStream.read();

                if (command == EXIT_CMD) {
                    System.out.println("finish process");
                    break;
                }
                processCommand(command, outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the command from client
     * @param command the command code
     */
    private void processCommand(int command,OutputStream outputStream) {
        try {
            switch (command) {
	            case 'L':
	                System.out.println("Left");
	                GPIOUtil.left(TIMEOUT_SHORT);
	                break;            
	            case 'R':
	                System.out.println("Right");
	                GPIOUtil.right(TIMEOUT_SHORT);
	                break;            
	            case 'F':
                    System.out.println("Forward");
                    GPIOUtil.forward(TIMEOUT_LONG);
                    break;
                case 'B':
                    System.out.println("Backward");
                    GPIOUtil.backward(TIMEOUT_LONG);
                    break;
                case 'I':
                	System.out.println("Image - started");
                	if (videoThread != null) {
                		videoThread.setRunning(false);
                		videoThread = null;
                	}
                	videoThread = new VideoGetterThread(outputStream);
                	videoThread.start();

            		break;
                case 'S':
                	System.out.println("Image - End");
                	if (videoThread != null) {
                		videoThread.setRunning(false);
                		videoThread = null;
                	}
            		break;
                case 'E':
                    System.exit(1);
                    break;
                default:
                    System.out.println("Bad command: " + command);
                    break;                	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
