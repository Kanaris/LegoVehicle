package info.privateblog.legovision.bluetooth.exception;

/**
 * Created by Siarhei_Charkes on 2/24/2017.
 */

public class ConnectionBluetoothException extends BluetoothException {
    public ConnectionBluetoothException(Throwable throwable) {
        super(throwable);
    }
    public ConnectionBluetoothException(String message) {
        super(message);
    }
}