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
		while(running) {
    		try {
				String time = sdf.format(new Date());
	
				if (LegoRobot.camera.read(frame)) {
					BufferedImage out = VideoUtil.convertMatToBufferedImage(frame);
		        	Graphics graphics = out.getGraphics();
		        	graphics.drawString(time, 5, 13);
		        	
		        	outStream.reset();
		        	
		    		ImageIO.write(out, "jpg", outStream);
		    		byte[]result = outStream.toByteArray();
					outputStream.write("*RDY*".getBytes());
		    		outputStream.write(("" + result.length + ";").getBytes());
		    		outputStream.write(result);
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
