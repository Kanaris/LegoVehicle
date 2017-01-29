package info.privateblog.uart;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.comm.CommPortIdentifier;

public class UARTUtil {
	public static Map<String, CommPortIdentifier> getCommPortIdentifiers() {
		Map<String, CommPortIdentifier> identifiers = new HashMap<String, CommPortIdentifier>();
		Enumeration<CommPortIdentifier>portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				identifiers.put(portId.getName(), portId);
			}
        }
		return identifiers;
	}
	
	public static List<String> getCommPortNames() {
		List<String> identifiers = new ArrayList<String>();
		Enumeration<CommPortIdentifier>portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				identifiers.add(portId.getName());
			}
        }
		return identifiers;
	}
}
