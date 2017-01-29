package info.privateblog.view;

import info.privateblog.uart.UARTThread;
import info.privateblog.uart.UARTUtil;
import info.privateblog.view.ui.CameraView;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class LegoWindow extends JFrame 
	implements ActionListener {
	private static Logger logger = Logger.getLogger(LegoWindow.class.getName());

	
	private CameraView cameraView = new CameraView();
	private JLabel uartPortLabel = new JLabel("UART Port");
	private JComboBox<String> uartPortComboBox = new JComboBox<String>();
	private JButton toggleButton = new JButton("Start/stop");
	private JButton leftButton = new JButton("Left");
	private JButton rightButton = new JButton("Right");
	private JButton forwardButton = new JButton("Forward");
	private JButton backwardButton = new JButton("Backward");
	private JButton imageButtonStart = new JButton("Image start");
	private JButton imageButtonStop = new JButton("Image stop");
	
	private SerialPort serialPort = null;
	private InputStream inputStream  = null; 
	private OutputStream outputStream  = null; 
	private UARTThread thread = null;
	
	public LegoWindow() {
		super("Lego viewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.setProperty("swing.defaultlaf", "javax.swing.plaf.metal.MetalLookAndFeel");
		setSize(800, 800);
		
		init();
		setUpLayout();
	}
	
	private void init() {
		//ComboBox
		uartPortComboBox.addItem("");
		for (String item: UARTUtil.getCommPortNames()) {
			uartPortComboBox.addItem(item);			
		}
		
		//Button
		toggleButton.addActionListener(this);
		leftButton.addActionListener(this);
		rightButton.addActionListener(this);
		forwardButton.addActionListener(this);
		backwardButton.addActionListener(this);
		imageButtonStart.addActionListener(this);
		imageButtonStop.addActionListener(this);
	}
	
	private void setUpLayout() {
		Container container = this.getContentPane();
		container.setLayout(new GridBagLayout());
		container.add(uartPortLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(5, 5,5, 5), 0, 0));
		container.add(uartPortComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,5, 5), 0, 0));
		container.add(toggleButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,5, 5), 0, 0));
		
		container.add(leftButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,5, 5), 0, 0));
		container.add(forwardButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,5, 5), 0, 0));
		container.add(rightButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,5, 5), 0, 0));
		container.add(backwardButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,5, 5), 0, 0));
		
		container.add(imageButtonStart, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,5, 5), 0, 0));
		container.add(imageButtonStop, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,5, 5), 0, 0));
				
		container.add(cameraView, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,5, 5), 0, 0));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.log(Level.INFO, "Button clicked");

		String comboBoxSelection = uartPortComboBox.getSelectedItem().toString();
		CommPortIdentifier comPort = UARTUtil.getCommPortIdentifiers().get(comboBoxSelection);

		if (e.getSource().equals(toggleButton)) { 
			if (comPort != null && serialPort == null) {
				logger.log(Level.INFO, "Found serial port: " + comPort.getName());
				try {
						logger.log(Level.INFO, "Toggle clicked");

						serialPort = (SerialPort) comPort.open("SimpleReadApp", 1000);
						inputStream = serialPort.getInputStream();
						outputStream = serialPort.getOutputStream();
	
			            serialPort.setSerialPortParams(921600, //(460800,
			                SerialPort.DATABITS_8,
			                SerialPort.STOPBITS_1,
			                SerialPort.PARITY_NONE);
	
			            
			            thread = new UARTThread();
			            thread.setInputStream(inputStream);
			            thread.addImageLoadedEvents(cameraView);
			            thread.start();
				} catch (PortInUseException | UnsupportedCommOperationException | IOException ex) {
					ex.printStackTrace();
					if (serialPort != null) {
						serialPort.close();
					}
					try {
						if (inputStream != null) {
							inputStream.close();
						}
					} catch (IOException e1) {}
					if (thread != null) {
						thread.setInputStream(null);
					}
					serialPort = null;
					inputStream = null;
				} 
			} else {
				if (thread != null) {
					thread.setInputStream(null);
					thread.setRunning(false);
				}
				if (serialPort != null) {
					serialPort.close();
				}
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e1) {}
				serialPort = null;
				inputStream = null;		
			}
		} else {
			try {
				if (e.getSource().equals(forwardButton)) {
					logger.log(Level.INFO, "Forward clicked");
					outputStream.write('F');
				} else if (e.getSource().equals(backwardButton)) {
					logger.log(Level.INFO, "Backward clicked");
					outputStream.write('B');
				} else if (e.getSource().equals(leftButton)) {
					logger.log(Level.INFO, "Left clicked");
					outputStream.write('L');
				} else if (e.getSource().equals(rightButton)) {
					logger.log(Level.INFO, "Right clicked");
					outputStream.write('R');
				} else if (e.getSource().equals(imageButtonStart)) {
					logger.log(Level.INFO, "Image started");
					outputStream.write('I');
				} else if (e.getSource().equals(imageButtonStop)) {
					logger.log(Level.INFO, "Image stoped");
					outputStream.write('S');
				}		
			} catch (IOException e1) {}
		}
	}
}
