package br.com.serglass.printer_client.impl.programs;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.SimpleDoc;

import br.com.serglass.printer_client.ExecutableProgram;

public class EtiquetaLaminacao implements ExecutableProgram {

	private static final String NOME_PROGRAMA = "etiqueta_laminacao";

	public void execute(String data, PrintService printService) {

		String[] args = data.split("\\|");
		if (args.length != 1) {
			throw new RuntimeException(
					"Num. de parametros de entrada invalido.");
		}

		String codPeca = args[0];

		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(
				NOME_PROGRAMA))) {

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("vdata")) {
					line = line.replace("vdata", codPeca);
				}

				sb.append(line);
			}

			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			Doc doc = new SimpleDoc(sb.toString().getBytes(), flavor, null);

			DocPrintJob job = printService.createPrintJob();
			job.print(doc, null);

		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"Erro ao ler arquivo de execucao do programa [%s].",
					NOME_PROGRAMA), e);
		}

	}
}
