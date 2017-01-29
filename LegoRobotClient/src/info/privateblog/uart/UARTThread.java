package info.privateblog.uart;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class UARTThread extends Thread {
	private static Logger logger = Logger.getLogger(UARTThread.class.getName());
	private static final  char[]COMMAND = {'*', 'R', 'D', 'Y', '*'};

    private InputStream inputStream  = null;
    
    private List<ImageLoadedEvent> imageLoadedEvents = new ArrayList<ImageLoadedEvent>();

    private boolean running = true;
    
    @Override
	public void run() {
       	try {
	    	while(inputStream != null && running) {
	    		logger.log(Level.INFO, "Looking for image");
				while(!isImageStart(inputStream, 0)){}
	    		
	    		int value;
	    		String size = "";
	    		while((value = inputStream.read()) != ';') {
	    			size += (char)value;
	    		}
	    		
	    		size = size.replace("}", "");

	    		logger.log(Level.INFO, "Found image: " + size);

	    		byte[]inputArray = new byte[Integer.parseInt(size)];
	    		for (int i = 0; i < inputArray.length; i++) {
	    			inputArray[i] = (byte)inputStream.read();
	    		}

	    		ByteArrayInputStream inStream = new ByteArrayInputStream(inputArray);
	    		
	    		BufferedImage  biImage = ImageIO.read(inStream);
	    		//ImageIO.write(biImage, "jpg", new File("c:/test.jpg"));
	    		logger.log(Level.INFO, "Got image");
	    		for (ImageLoadedEvent imageLoadedEvent: imageLoadedEvents) {
	            	imageLoadedEvent.loadedImage(biImage);
	            }
	    	}
		} catch (IOException e) {
			running = false;
			e.printStackTrace();
		};
		
		logger.log(Level.INFO, "Thread ended");
    }

    private int read(InputStream inputStream) throws IOException {
    	int temp = (char) inputStream.read();
		if (temp == -1) {
			throw new IOException("Exit");
		}
		return temp;
    }
		
    private boolean isImageStart(InputStream inputStream, int index) throws IOException {
    	if (index < COMMAND.length) {
    		if (COMMAND[index] == read(inputStream)) {
    			return isImageStart(inputStream, ++index);
    		} else {
    			return false;
    		}
    	}
    	return true;
    }
    
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void removeImageLoadedEvents(ImageLoadedEvent imageLoadedEvents) {
		this.imageLoadedEvents.remove(imageLoadedEvents);
	}

	public void addImageLoadedEvents(ImageLoadedEvent imageLoadedEvents) {
		this.imageLoadedEvents.add(imageLoadedEvents);
	}
}
