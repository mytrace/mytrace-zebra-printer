package br.com.mytrace.printer_client.impl.programs;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.SimpleDoc;

import br.com.mytrace.printer_client.ExecutableProgram;

public class EtiquetaProducao implements ExecutableProgram {

	private static final String NOME_PROGRAMA = "etiqueta_producao";

	public void execute(String data, PrintService printService) {

		String[] args = data.split("\\|");
		if (args.length != 2) {
			throw new RuntimeException("Num. de parametros de entrada invalido.");
		}

		String cdContrato = args[0];
		String dtAtual = args[1];

		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(NOME_PROGRAMA))) {

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("vos")) {
					line = line.replace("vos", cdContrato);
				}
				if (line.contains("vdata")) {
					line = line.replace("vdata", dtAtual);
				}
				sb.append(line);
			}

			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			Doc doc = new SimpleDoc(sb.toString().getBytes(), flavor, null);

			DocPrintJob job = printService.createPrintJob();
			job.print(doc, null);

		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Erro ao ler arquivo de execucao do programa [%s].", NOME_PROGRAMA), e);
		}

	}
}
