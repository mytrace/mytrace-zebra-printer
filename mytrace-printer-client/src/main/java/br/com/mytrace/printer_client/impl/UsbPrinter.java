package br.com.mytrace.printer_client.impl;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterName;

import br.com.mytrace.printer_client.ExecutableProgram;
import br.com.mytrace.printer_client.Printer;
import br.com.mytrace.printer_client.enumeration.Program;

public class UsbPrinter implements Printer {

	private static int INVALID_PROGRAM = 2;
	private static int EXECUCAO_OK = 1;
	private static int COMMAND_NOK = 0;

	public Status getStatus(String printerId) {
		PrintService service = checkPrinterStatus(printerId, true);
		Status state = null;
		if (service == null) {
			return Status.UNKNOW_DRIVER;
		} else {
			PrintServiceAttribute attr = service
					.getAttribute(PrinterIsAcceptingJobs.class);

			int code = ((PrinterIsAcceptingJobs) attr).getValue();
			switch (code) {
			case 0:
				state = Status.NOT_ACCEPTING_JOBS;
				break;
			case 1:
				state = Status.ACCEPTING_JOBS;
				break;
			default:
				state = Status.UNKNOW;
				break;
			}

			return state;
		}
	}

	public int executeCommand(String deviceId, String programName, String data) {

		PrintService service = checkPrinterStatus(deviceId, false);
		if (service != null) {
			Program program = Program.getByName(programName);

			if (program != null) {
				ExecutableProgram executable = program.getExecutable();
				executable.execute(data, service);

				return EXECUCAO_OK;
			} else {
				System.out.println("Programa invalido.");
				return INVALID_PROGRAM;
			}
		} else {
			return COMMAND_NOK;
		}
	}

	private PrintService checkPrinterStatus(String printerId,
			boolean printStatus) {
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null,
				null);

		if (printStatus)
			System.out.println(String.format(
					"Verificando dispositivos USB conectados. Buscando [%s].",
					printerId));

		String printerName = null;
		for (PrintService printService : services) {
			PrintServiceAttribute attr = printService
					.getAttribute(PrinterName.class);
			printerName = ((PrinterName) attr).getValue();

			if (printStatus)
				System.out.println("Dispositivo: " + printerName);
			if (printerName != null
					&& printerName.toLowerCase().contains(
							printerId.toLowerCase())) {
				if (printStatus)
					System.out.println("Detectado! - " + printerName);
				return printService;
			} else {
				if (printStatus)
					System.out
							.println("Dispositivo Invalido. Prosseguindo Execucao...");
				continue;
			}
		}

		if (printStatus)
			System.out.println("Impressora Nao Encontrada - OFFLINE");
		return null;
	}

	// private void executarImpressaoQrComLabel(String texto,
	// PrintService printService) throws PrintException {
	// String command = "";
	// command += "^XA";
	//
	// command += "^FO310,80";// posicao inicial 310, 30
	// command += "^BQN,2,7";// densidate mode 2, correcion level 7
	// command += "^FDQA,";
	// command += texto;
	// command += "^FS";
	//
	// command += "^FO250,20";
	// command += "^A0N,32,25";
	// command += "^FD";
	// command += texto;
	// command += "^FS";
	//
	// command += "^XZ";
	//
	// byte[] rawCommand = command.getBytes();
	// DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
	// Doc doc = new SimpleDoc(rawCommand, flavor, null);
	//
	// DocPrintJob job = printService.createPrintJob();
	// job.print(doc, null);
	// }
}
