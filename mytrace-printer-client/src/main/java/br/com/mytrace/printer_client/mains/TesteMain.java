package br.com.mytrace.printer_client.mains;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterName;

import br.com.mytrace.printer_client.impl.UsbPrinter;
import br.com.mytrace.printer_client.impl.programs.EtiquetaTeste;

public class TesteMain {

	public static void main(String[] args) {

		String param = args[0];

		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

		PrintService deviceService = null;
		for (PrintService printService : services) {
			PrintServiceAttribute attr = printService.getAttribute(PrinterName.class);
			String device = ((PrinterName) attr).getValue();

			if (device.equals(param)) {
				deviceService = printService;
			}

		}

		if (deviceService == null) {
			throw new RuntimeException("Dispositivo nao encontrado.");
		} else {

			new UsbPrinter().getStatus(param);
			new EtiquetaTeste().execute(null, deviceService);
		}

	}
}
