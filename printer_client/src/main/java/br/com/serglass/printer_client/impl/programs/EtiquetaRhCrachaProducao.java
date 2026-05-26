package br.com.serglass.printer_client.impl.programs;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.SimpleDoc;

import br.com.mytrace.core.util.UTF8ToZPLHexConverter;
import br.com.serglass.printer_client.ExecutableProgram;

public class EtiquetaRhCrachaProducao implements ExecutableProgram {

	private static final String NOME_PROGRAMA = "etiqueta_rh_cracha_producao";

	public void execute(String data, PrintService printService) {

		String[] args = data.split("\\|");
		if (args.length != 3) {
			throw new RuntimeException(
					"Num. de parametros de entrada invalido.");
		}

		String autenticacao = args[0];
		String nome = args[1];
		String dpto = args[2];

		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(
				NOME_PROGRAMA))) {
			UTF8ToZPLHexConverter zpl = new UTF8ToZPLHexConverter();

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("vautenticacao")) {
					line = line.replace("vautenticacao", autenticacao);
				}
				if (line.contains("vnome")) {
					line = line.replace("vnome", zpl.convert(nome));
				}
				if (line.contains("vdpto")) {
					line = line.replace("vdpto", zpl.convert(dpto));
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

	public static void main(String[] args) {

		String nome = "Produção";

		System.out.println(new UTF8ToZPLHexConverter().convert(nome));
	}

}
