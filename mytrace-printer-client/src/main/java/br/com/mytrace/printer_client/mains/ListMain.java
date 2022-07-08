package br.com.mytrace.printer_client.mains;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterName;

import br.com.mytrace.printer_client.Printer;
import br.com.mytrace.printer_client.impl.UsbPrinter;

public class ListMain {

	public static void main(String[] args) {

		Printer usb = new UsbPrinter();

		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

		for (PrintService printService : services) {
			PrintServiceAttribute attr = printService.getAttribute(PrinterName.class);
			String device = ((PrinterName) attr).getValue();

			System.out.println(usb.getStatus(device));
		}

	}

}
