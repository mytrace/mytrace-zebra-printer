package br.com.mytrace.printer_client.impl.programs;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.SimpleDoc;

import br.com.mytrace.printer_client.ExecutableProgram;
import br.com.mytrace.printer_client.util.UTF8ToZPLHexConverter;

public class Apontamento implements ExecutableProgram {

	private static final String NOME_PROGRAMA = "apontamento";

	public void execute(String data, PrintService printService) {

		String[] args = data.split("\\|");
		if (args.length != 7) {
			throw new RuntimeException("Solicitacao invalida.");
		}

		String nrContratoBlindagem = args[0];
		String nmModelo = args[1];
		String nmFuncionario = args[2];
		String nmEtapa = args[3];
		String dtInicio = args[4];
		String dtFim = args[5];
		String dtEmissao = args[6];

		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(NOME_PROGRAMA))) {
			UTF8ToZPLHexConverter zpl = new UTF8ToZPLHexConverter();

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("vnrContratoBlindagem")) {
					line = line.replace("vnrContratoBlindagem", nrContratoBlindagem);
				}
				if (line.contains("vnmModelo")) {
					line = line.replace("vnmModelo", zpl.convert(nmModelo));
				}
				if (line.contains("vnmFuncionario")) {
					line = line.replace("vnmFuncionario",zpl.convert(nmFuncionario));
				}
				if (line.contains("vnmEtapa")) {
					line = line.replace("vnmEtapa", zpl.convert(nmEtapa));
				}
				if (line.contains("vdtInicio")) {
					line = line.replace("vdtInicio", dtInicio);
				}
				if (line.contains("vdtIFim")) {
					line = line.replace("vdtIFim", dtFim);
				}
				if (line.contains("vdtEmissao")) {
					line = line.replace("vdtEmissao", dtEmissao);
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
