package info.privateblog;

import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import info.privateblog.bluetooth.WaitThread;
import info.privateblog.gpio.GPIOUtil;
import info.privateblog.gpio.UARTThread;
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
	//private static final Serial serial = SerialFactory.createInstance();

	static {
	    camera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, WIDTH);      
	    camera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, HEIGHT); 
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Lego Robot started");
        bluetooth();
       // uart();
        System.out.println("Lego Robot ended");
	}
	
	private static void bluetooth() throws BluetoothStateException {
        System.out.println("Starting bluetooth");
		System.out.println("Local address: " + LocalDevice.getLocalDevice().getBluetoothAddress());
		GPIOUtil.light(false);
		
        Thread waitThread = new Thread(new WaitThread());
        waitThread.start();
        
        Thread distanceThread = new UARTThread();
        distanceThread.start();
	}
	
	/*private static void uart() {
        System.out.println("Starting uart");

		serial.addListener(new SerialDataEventListener(){
			@Override
			public void dataReceived(SerialDataEvent  event){
				if (!serial.isOpen()) {
					return;
				}
				
				byte[] wert = null;
				try {
					wert = event.getBytes();
				} catch (IOException e) {
					System.out.println("Cannot read data: " + e.getMessage());
				}
				System.out.println("Serial: " + new String(wert));
			}
		});

		try {
			serial.open(Serial.DEFAULT_COM_PORT, 9600);
		} catch (IOException e) {
			System.out.println("Failed to start uart thread: " + e.getMessage());
			return;
		}
	}*/
}
