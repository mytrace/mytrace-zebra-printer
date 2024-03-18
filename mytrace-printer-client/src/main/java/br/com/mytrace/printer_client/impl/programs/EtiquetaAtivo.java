package br.com.mytrace.printer_client.impl.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.SimpleDoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.mytrace.printer_client.ExecutableProgram;
import br.com.mytrace.printer_client.util.UTF8ToZPLHexConverter;

public class EtiquetaAtivo implements ExecutableProgram {

	private static final String NOME_PROGRAMA = "etiqueta_ativo";

	public static Logger LOG = LoggerFactory.getLogger(EtiquetaAtivo.class);

	public void execute(String data, PrintService printService) {

		String[] args = data.split("\\|");
		if (args.length < 4) {
			throw new RuntimeException("Solicitacao invalida. Checar os parametros.");
		}

		String dsCodAtivo = args[0];
		String dsGtin = args[1];
		String dsDescricao = args[2];
		String dsProprietario = args[3];

		StringBuilder sb = new StringBuilder();

		String pathToFile = null;
		try {
			File file = new File(
					EtiquetaAtivo.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

			pathToFile = file.getParent() + "/";
		} catch (Exception e) {
			pathToFile = "./";
		}

		try (BufferedReader br = new BufferedReader(new FileReader(pathToFile + NOME_PROGRAMA))) {
			UTF8ToZPLHexConverter zpl = new UTF8ToZPLHexConverter();

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("ds_cod_ativo")) {
					line = line.replace("ds_cod_ativo", dsCodAtivo);
				}
				if (line.contains("ds_gtin")) {
					line = line.replace("ds_gtin", zpl.convert(dsGtin));
				}
				if (line.contains("ds_descricao")) {
					line = line.replace("ds_descricao", dsDescricao);
				}
				if (line.contains("ds_proprietario")) {
					line = line.replace("ds_proprietario", dsProprietario);
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
