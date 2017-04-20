package info.privateblog.view.ui;

import info.privateblog.uart.ImageLoadedEvent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class CameraView extends JComponent implements ImageLoadedEvent {
	private Image image = null;
	
	public CameraView() {
		setPreferredSize(new Dimension(640, 480));
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		if (image == null) {
			graphics.setColor(Color.blue);
		    graphics.fillRect(0, 0, this.getWidth(), this.getHeight());			
		} else {
			int original = image.getWidth(null);
			int needed = this.getWidth();
			
			double scale = 1.0*needed/original;
			
			synchronized (this) {
				graphics.drawImage(image, 0, 0, (int)(image.getWidth(null)*scale), (int)(image.getHeight(null)*scale), null);
			}
		}
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public void loadedImage(Image image) {
		synchronized (this) {
			this.image = image;
		}
		System.out.println("Repainting");
		repaint();
	}
}
