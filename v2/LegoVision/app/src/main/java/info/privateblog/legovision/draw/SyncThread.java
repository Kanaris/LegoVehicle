package info.privateblog.legovision.draw;

import info.privateblog.legovision.bluetooth.BluetoothConnector;

/**
 * Created by Merl1n on 05.03.2017.
 */

public class SyncThread extends Thread {
    private boolean running = true;
    private BluetoothConnector connector;

    public SyncThread(BluetoothConnector connector) {
        this.connector = connector;
    }

    @Override
    public void run() {
        while (running) {
            if (connector.isConnected()) {
                try {
                    connector.write('|');
                    Thread.currentThread().sleep(1000);
                } catch (Exception e) {}
            }
        }
    }
}
