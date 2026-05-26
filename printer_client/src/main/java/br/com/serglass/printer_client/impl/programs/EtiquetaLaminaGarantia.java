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

public class EtiquetaLaminaGarantia implements ExecutableProgram {

	private static final String NOME_PROGRAMA = "etiqueta_lamina_garantia";

	public void execute(String data, PrintService printService) {

		String[] args = data.split("\\|");
//		if (args.length != 8) {
//			throw new RuntimeException(
//					"Num. de parametros de entrada invalido.");
//		}

		String numPedido = args[0];
		String dtAtual = args[1];
		String codLamina = args[2];
		String numLote = args[3];
		String nmCliente = args[4];
		String modelo = args[5];
		String nrSeqMaqCorte = args[6];
		String nrSerie = "-";
		if(args[7] != null)
			nrSerie = args[7];

		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(
				NOME_PROGRAMA))) {
			UTF8ToZPLHexConverter zpl = new UTF8ToZPLHexConverter();

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("vpedido")) {
					line = line.replace("vpedido", numPedido);
				}
				if (line.contains("vdata")) {
					line = line.replace("vdata", dtAtual);
				}
				if (line.contains("vcodLamina")) {
					line = line.replace("vcodLamina", codLamina);
				}
				if (line.contains("vlote")) {
					line = line.replace("vlote", numLote);
				}
				if (line.contains("vcliente")) {
					line = line.replace("vcliente", zpl.convert(nmCliente));
				}
				if (line.contains("vmodelo")) {
					line = line.replace("vmodelo", modelo);
				}
				if (line.contains("vseqMaqCorte")) {
					line = line.replace("vseqMaqCorte", nrSeqMaqCorte);
				}
				if (line.contains("vnrSerie")) {
					line = line.replace("vnrSerie", nrSerie);
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
