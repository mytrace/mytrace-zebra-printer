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

public class EtiquetaPecaPedido implements ExecutableProgram {

	private static final String NOME_PROGRAMA = "etiqueta_peca_pedido";

	public void execute(String data, PrintService printService) {

		String[] args = data.split("\\|");
		if (args.length != 11) {
			throw new RuntimeException(
					"Num. de parametros de entrada invalido.");
		}

		String numPedido = args[0];
		String dtAtual = args[1];
		String codPeca = args[2];
		String nmCliente = args[3];
		String modelo = args[4];
		String lote = args[5];
		String nrSerie = args[6];
		String dsProduto = args[7];
		String dsRebaixo = args[8];
		String dsDegrade = args[9];
		String dsOpcao = args[10];
		
		
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
				if (line.contains("vlote")) {
					line = line.replace("vlote", lote);
				}
				if (line.contains("vnrSerie")) {
					line = line.replace("vnrSerie", nrSerie);
				}
				if (line.contains("vdsProduto")) {
					line = line.replace("vdsProduto", dsProduto);
				}
				if (line.contains("vdsRebaixo")) {
					line = line.replace("vdsRebaixo", dsRebaixo);
				}
				if (line.contains("vdsBanda")) {
					line = line.replace("vdsDegrade", dsDegrade);
				}
				if (line.contains("vdsOpcao")) {
					line = line.replace("vdsOpcao", dsOpcao);
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
