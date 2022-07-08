package br.com.mytrace.printer_client.impl.programs;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.SimpleDoc;

import br.com.mytrace.printer_client.ExecutableProgram;

public class EtiquetaTeste implements ExecutableProgram {


	@Override
	public void execute(String data, PrintService printService) {
		try {
			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			Doc doc = new SimpleDoc("^XA^CF0,30^FO140,280^FDvdata^FS^XZ".getBytes(), flavor, null);

			DocPrintJob job = printService.createPrintJob();
			job.print(doc, null);

		} catch (Exception e) {
			throw new RuntimeException("Erro.", e);
		}

	}
}
