package info.privateblog.gpio;

import com.pi4j.wiringpi.Serial;

public class UARTThread extends Thread {
	private boolean running = true;
	
	private int serialPort = 0;
	
	public UARTThread() {
		serialPort = Serial.serialOpen(Serial.DEFAULT_COM_PORT, 9600);
	}
	
	public void run() {
		byte[]buffer = new byte[10];
		int index = 0;
		boolean isDistance = false;
		
		try {
			while (running) {
				if (Serial.serialDataAvail(serialPort) > 0) {
					byte[]charSymbols = Serial.serialGetBytes(serialPort, 1);
					byte charSymbol = charSymbols[0];
					
					if (charSymbol == 'D' && !isDistance) {
						isDistance = true;
						continue;
					} else if (isDistance) {
						if (charSymbol == '\n' || charSymbol == '\r') {
							if (index > 0) {
								String value = new String(buffer, 0, index);
								int result = 0;
								try {
									result = Integer.parseInt(value);
									GPIOUtil.setDistance(result);
								} catch (NumberFormatException ex){ 
								} finally {
									isDistance = false;
									index = 0;
								}
							}						
						} else {
							if (index < 10) {
								buffer[index++] = charSymbol;
							} else {
								isDistance = false;
								index = 0;
							}
						}
					}
				} else {
					Thread.sleep(100);
				}
			}
		} catch (Exception e1) {
			System.out.println("Failed to read uart data: " + e1.getMessage());
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
