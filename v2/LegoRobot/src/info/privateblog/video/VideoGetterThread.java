package info.privateblog.video;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import info.privateblog.LegoRobot;
import info.privateblog.gpio.GPIOUtil;
import info.privateblog.util.VideoUtil;

public class VideoGetterThread extends Thread {
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	private boolean running = true;
	private OutputStream outputStream = null;
	private	ByteArrayOutputStream outStream = new ByteArrayOutputStream();

	private Mat frame = new Mat(LegoRobot.HEIGHT, LegoRobot.WIDTH, CvType.CV_8UC3);
	
	public VideoGetterThread(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	@Override
	public void run() {
		int byteArraySize = 500;
		
		while(running) {
    		try {
				String time = sdf.format(new Date());
	
				if (LegoRobot.camera.read(frame)) {
					BufferedImage out = VideoUtil.convertMatToBufferedImage(frame);
		        	Graphics graphics = out.getGraphics();
		        	graphics.drawString(time, 5, 13);

		        	int distance = GPIOUtil.getDistance();
		        	if (distance == 0) {
			        	graphics.drawString("Distance: uknown", 5, 25);
		        	} else {
			        	graphics.drawString("Distance: " + distance + " cm", 5, 25);
		        	}
		        	/*graphics.drawString("Z: " + axis[1], 5, 40);
		        	graphics.drawString("Y: " + axis[2], 5, 55);
		        	
		        	double angle = Math.atan((double)axis[2]/axis[0]) * 180/Math.PI + 180;
		        	graphics.drawString("Angle: " + angle, 5, 70);
		        	*/
		        	

		        	outStream.reset();
		    		ImageIO.write(out, "jpg", outStream);
		    		byte[]result = outStream.toByteArray();
					outputStream.write("*RDY*".getBytes());
					int byte1 = (0xFF&result.length);
					int byte2 = (0xFF&(result.length>>8));
					int byte3 = (0xFF&(result.length>>16));
					
		    		outputStream.write(byte3);
					outputStream.write(byte2);
		    		outputStream.write(byte1);
		    		
		    		outputStream.write(result);
		    		
/*		    		int i = 0;
		    		for (i = 0; i < result.length - byteArraySize; i += byteArraySize) {
			    		outputStream.write(result, i, byteArraySize);
		    		}
		    		outputStream.write(result, i*byteArraySize, result.length - i*byteArraySize);
*/		    		
		    		
		    		outputStream.flush();
				}
			} catch (IOException e) {
				System.out.println("Image getting failed: " + e.getMessage());
				return;
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
