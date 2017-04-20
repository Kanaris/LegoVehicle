package info.privateblog.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class GPIOUtil {
    private static final GpioController gpio = GpioFactory.getInstance();

    private static final GpioPinDigitalOutput rightBackward = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
    private static final GpioPinDigitalOutput rightForward = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
    private static final GpioPinDigitalOutput leftBackward = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
    private static final GpioPinDigitalOutput leftForward = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);
    private static final GpioPinDigitalOutput light = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);

    private static volatile int distance = 0;
    
    public static void forward(int timeout) {
        rightForward.high();
        leftForward.high();
        sleep(timeout);
        rightForward.low();
        leftForward.low();
    }
    
    public static void backward(int timeout) {
    	rightBackward.high();
    	leftBackward.high();
    	sleep(timeout);
        rightBackward.low();
        leftBackward.low();
    }
    
    public static void left(int timeout) {
        rightForward.high();
        leftBackward.high();
        sleep(timeout);
        rightForward.low();
        leftBackward.low();
    }

    public static void right(int timeout) {
        rightBackward.high();
        leftForward.high();
        sleep(timeout);
        rightBackward.low();
        leftForward.low();
    }

    public static void light(boolean onOff) {
    	if (onOff) {
    		light.high();
    	} else {
    		light.low();
    	}
    }
    
    public static void sleep(int timeout) {
        try {
			Thread.currentThread().sleep(timeout);
		} catch (InterruptedException e) {}    	
    }
    public static void sleep(int timeout, int nano) {
        try {
			Thread.currentThread().sleep(timeout, nano);
		} catch (InterruptedException e) {}    	
    }

	public static int getDistance() {
		return distance;
	}

	public static void setDistance(int distance) {
		GPIOUtil.distance = distance;
	}
}
