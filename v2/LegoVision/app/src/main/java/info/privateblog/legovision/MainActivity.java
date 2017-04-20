package info.privateblog.legovision;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import info.privateblog.legovision.bluetooth.BluetoothConnector;
import info.privateblog.legovision.bluetooth.exception.BluetoothException;
import info.privateblog.legovision.draw.ImageGetterThread;
import info.privateblog.legovision.draw.ImageHandler;
import info.privateblog.legovision.draw.SurfaceDrawer;
import info.privateblog.legovision.draw.SyncThread;
import info.privateblog.legovision.model.AddressModel;

public class MainActivity extends AppCompatActivity {
    private BluetoothConnector connector = new BluetoothConnector();
    private LinkedList<String> logs = new LinkedList<String>();

    private List<View> controlElements = new ArrayList<View>();

    private ImageHandler imageHandler = new ImageHandler();
    private SurfaceDrawer surfaceDrawer = new SurfaceDrawer(imageHandler);

    private static final int MAX_LOGS = 10;

    private final static String CONNECT_TEXT = "Connect";
    private final static String DISCONNECT_TEXT = "Disconnect";

    private SyncThread syncThread = null;

    private ImageGetterThread imageThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configLayout();

        List<AddressModel> deviceList = new ArrayList<AddressModel>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Buttons
        Button connectButton = (Button)findViewById(R.id.connectButton);
        Spinner bluetoothAdapters = (Spinner)findViewById(R.id.bluetoothAdapters);

        connectButton.setOnClickListener(clickListener);
        connectButton.setText(CONNECT_TEXT);

        //Other control elements
        ((Switch)findViewById(R.id.lightSwitch)).setOnCheckedChangeListener(changeLister);
        ((Switch)findViewById(R.id.cameraSwitch)).setOnCheckedChangeListener(changeLister);

        controlElements.add(findViewById(R.id.downButton));
        controlElements.add(findViewById(R.id.upButton));
        controlElements.add(findViewById(R.id.leftButton));
        controlElements.add(findViewById(R.id.rightButton));
        controlElements.add(findViewById(R.id.lightSwitch));
        controlElements.add(findViewById(R.id.cameraSwitch));

        for (View control: controlElements) {
            control.setEnabled(false);
            control.setOnClickListener(clickListener);
        }

        //Bluetooth
        try {
            if (!BluetoothConnector.isSupported()) {
                logMessage("Error: Bluetooth is not supported");
                connectButton.setEnabled(false);
                bluetoothAdapters.setEnabled(false);
                return;
            } else {
                logMessage("Bluetooth is supported");
            }
            if (!BluetoothConnector.isEnabled()) {
                logMessage("Bluetooth is not enabled");
                connectButton.setEnabled(false);
                bluetoothAdapters.setEnabled(false);
                return;
            } else {
                logMessage("Bluetooth is enabled");
                logMessage("Name: " + BluetoothConnector.getAdapterName());
            }

            Map<String, String> mPairedDevicesArrayAdapter = BluetoothConnector.getBondedDevices();
            if (mPairedDevicesArrayAdapter.size() > 0) {
                Iterator<String> it = mPairedDevicesArrayAdapter.keySet().iterator();
                while(it.hasNext()) {
                    String key = it.next();

                    deviceList.add(new AddressModel(key, mPairedDevicesArrayAdapter.get(key)));
                }
            } else {
                logMessage("No paired device");
            }
        } catch (BluetoothException ex) {
            logMessage("Creation Error: " + ex.getMessage());
            connectButton.setEnabled(false);
            bluetoothAdapters.setEnabled(false);
            return;
        }

        //Fill spinner
        ArrayAdapter<AddressModel> dataAdapter = new ArrayAdapter<AddressModel>(this,
                android.R.layout.simple_spinner_item, deviceList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bluetoothAdapters.setAdapter(dataAdapter);

        //Surface
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceDrawer);

        //Sync Thread
        if (syncThread == null) {
            syncThread = new SyncThread(connector);
            syncThread.start();
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            View button = v;
            try {
                switch(button.getId()) {
                    //connection
                    case R.id.connectButton:
                        if (!connector.isConnected()) {
                            Spinner adapters = (Spinner)findViewById(R.id.bluetoothAdapters);
                            AddressModel addressModel = (AddressModel)adapters.getSelectedItem();

                            connector.connect(addressModel.getAddress());

                            for (View control: controlElements) {
                                control.setEnabled(true);
                            }
                            ((Switch)findViewById(R.id.cameraSwitch)).setChecked(true);

                            connector.write('I');
                            if (imageThread == null) {
                               imageThread = new ImageGetterThread(imageHandler, connector);
                               imageThread.start();
                            } else {
                                logMessage("Cannot start image thread");
                            }
                        } else {
                            connector.write('S');//stop image
                            connector.write('N');//stop light
                            if (imageThread != null) {
                                imageThread.setRunning(false);
                                imageThread = null;
                            } else {
                                logMessage("Image thread is not started");
                            }
                            connector.disconnect();
                        }
                        if(connector.isConnected()) {
                            logMessage("Connected");
                            findViewById(R.id.bluetoothAdapters).setEnabled(false);
                            ((Button)button).setText(DISCONNECT_TEXT);
                        } else {
                            logMessage("Not connected");
                            findViewById(R.id.bluetoothAdapters).setEnabled(true);
                            ((Button)button).setText(CONNECT_TEXT);
                        }
                        break;
                    case R.id.leftButton:
                        connector.write('L');
                        break;
                    case R.id.rightButton:
                        connector.write('R');
                        break;
                    case R.id.upButton:
                        connector.write('F');
                        break;
                    case R.id.downButton:
                        connector.write('B');
                        break;
                }
            } catch (Exception e) {
                logMessage("Error: " + e.getMessage());
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener changeLister = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                switch (buttonView.getId()) {
                    case  R.id.lightSwitch:
                        if (isChecked) {
                            connector.write('Y');
                        } else {
                            connector.write('N');
                        }
                       break;
                    case  R.id.cameraSwitch:
                        if (isChecked) {
                            connector.write('I');
                        } else {
                            connector.write('S');
                        }
                        break;
                }
            } catch (Exception e) {
                logMessage("Error: " + e.getMessage());
            }
        }
    };

    public void configLayout() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void logMessage(String message) {
        logs.add(message);
        if (logs.size() > MAX_LOGS) {
            logs.removeFirst();
        }

        TextView loggerTextViewer = (TextView) findViewById(R.id.logText);
        loggerTextViewer.setText("");

        for (String log: logs) {
            loggerTextViewer.append(log + "\n");
        }
    }
}
