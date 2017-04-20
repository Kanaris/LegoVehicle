package info.privateblog.legovision.bluetooth.exception;

/**
 * Created by Siarhei_Charkes on 2/24/2017.
 */

public class BluetoothException extends Exception {
    public BluetoothException(Throwable throwable) {
        super(throwable);
    }
    public BluetoothException(String message) {
        super(message);
    }
    public BluetoothException() {
    }
}
