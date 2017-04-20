package info.privateblog;

import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import info.privateblog.bluetooth.WaitThread;
import info.privateblog.gpio.GPIOUtil;
import info.privateblog.util.NativeUtils;

public class LegoRobot {
	public static int WIDTH = 320;
	public static int HEIGHT = 240;
	
	static {
		try {
	        System.loadLibrary("opencv_java249");
	    } catch (UnsatisfiedLinkError e) {
	        try {
	            NativeUtils.loadLibraryFromJar("/libopencv_java249.so");
	        } catch (IOException e1) {
	            throw new RuntimeException(e1);
	        }
	    }
	}

	public static VideoCapture camera = new VideoCapture(0);      

	static {
	    camera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, WIDTH);      
	    camera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, HEIGHT); 
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Lego Robot started");
        //testGPIO();
        bluetooth();
        System.out.println("Lego Robot ended");
	}
	
	private static void bluetooth() throws BluetoothStateException {
        System.out.println("bluetooth test - starting");
		System.out.println("Local address: " + LocalDevice.getLocalDevice().getBluetoothAddress());

        Thread waitThread = new Thread(new WaitThread());
        waitThread.start();
		
		System.out.println("bluetooth test - done");
	}
	
	private static void testGPIO() throws InterruptedException {
        System.out.println("gpio test - starting");

        GPIOUtil.forward(500);
        GPIOUtil.backward(500);
        
        System.out.println("gpio test - done");
	}
}
