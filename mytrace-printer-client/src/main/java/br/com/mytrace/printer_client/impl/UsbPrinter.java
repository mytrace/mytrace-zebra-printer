package br.com.mytrace.printer_client.impl;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.mytrace.printer_client.ExecutableProgram;
import br.com.mytrace.printer_client.Printer;
import br.com.mytrace.printer_client.enumeration.Program;

public class UsbPrinter implements Printer {

	public static Logger LOG = LoggerFactory.getLogger(UsbPrinter.class);

	private static int INVALID_PROGRAM = 2;
	private static int EXECUCAO_OK = 1;
	private static int COMMAND_NOK = 0;

	public Status getStatus(String printerId) {
		PrintService service = checkPrinterStatus(printerId, true);
		Status state = null;
		if (service == null) {
			return Status.UNKNOW_DRIVER;
		} else {
			PrintServiceAttribute attr = service.getAttribute(PrinterIsAcceptingJobs.class);

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
				LOG.error("Programa invalido.");
				return INVALID_PROGRAM;
			}
		} else {
			return COMMAND_NOK;
		}
	}

	private PrintService checkPrinterStatus(String printerId, boolean printStatus) {
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

		if (printStatus) {
			LOG.info(String.format("Verificando dispositivos USB conectados. Buscando [%s].", printerId));
		}

		String printerName = null;
		for (PrintService printService : services) {
			PrintServiceAttribute attr = printService.getAttribute(PrinterName.class);
			printerName = ((PrinterName) attr).getValue();

			if (printStatus) {
				LOG.info("Dispositivo: " + printerName);
			}
			if (printerName != null && printerName.toLowerCase().contains(printerId.toLowerCase())) {
				if (printStatus) {
					LOG.info("Detectado! - " + printerName);
				}
				return printService;
			} else {
				if (printStatus) {
					LOG.info("Dispositivo Invalido. Prosseguindo Execucao...");
				}
				continue;
			}
		}

		if (printStatus) {
			LOG.info("Impressora Nao Encontrada - OFFLINE");
		}
		return null;
	}

}
