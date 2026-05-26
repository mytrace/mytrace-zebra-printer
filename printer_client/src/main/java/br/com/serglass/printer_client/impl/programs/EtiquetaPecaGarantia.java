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

public class EtiquetaPecaGarantia implements ExecutableProgram {

	private static final String NOME_PROGRAMA = "etiqueta_peca_garantia";

	public void execute(String data, PrintService printService) {

		String[] args = data.split("\\|");

		String numPedido = args[0];
		String dtAtual = args[1];
		String codPeca = args[2];
		String nmCliente = args[3];
		String modelo = args[4];
		String tipo = args[5];
		String nrSerie = "-";
		if(args[6] != null)
			nrSerie = args[6];

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
				if (line.contains("vcodPeca")) {
					line = line.replace("vcodPeca", codPeca);
				}
				if (line.contains("vcliente")) {
					line = line.replace("vcliente", zpl.convert(nmCliente));
				}
				if (line.contains("vmodelo")) {
					line = line.replace("vmodelo", modelo);
				}
				if (line.contains("vtipo")) {
					line = line.replace("vtipo", tipo);
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
