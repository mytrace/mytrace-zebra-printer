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

public class EtiquetaBtags implements ExecutableProgram {

	private static final String NOME_PROGRAMA = "etiqueta_btags";

	public static Logger LOG = LoggerFactory.getLogger(EtiquetaBtags.class);

	public void execute(String data, PrintService printService) {

		String[] args = data.split("\\|");
		if (args.length < 13) {
			throw new RuntimeException("Solicitacao invalida. Checar os parametros.");
		}

		String vqrcode = args[0];
		String vNmProduto = args[1];
		String vDtManipulacao = args[2];
		String vDtValidade = args[3];
		String vNmResponsavel = args[4];
		String vNmFornecedor = args[5];
		String vNrLote = args[6];
		String vDsMetConservacao = args[7];
		String vNrSif = args[8];
		String vIdEtiqueta = args[9];
		String vNmEmpresa = args[10];
		String vDsEnderecoEmpresa = args[11];
		String vDsCidadeEstado = args[12];

		StringBuilder sb = new StringBuilder();

		String pathToFile = null;
		try {
			File file = new File(
					EtiquetaBtags.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

			pathToFile = file.getParent() + "/";
		} catch (Exception e) {
			pathToFile = "./";
		}

		try (BufferedReader br = new BufferedReader(new FileReader(pathToFile + NOME_PROGRAMA))) {
			UTF8ToZPLHexConverter zpl = new UTF8ToZPLHexConverter();

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("vqrcode")) {
					line = line.replace("vqrcode", vqrcode);
				}
				if (line.contains("vNmProduto")) {
					line = line.replace("vNmProduto", zpl.convert(vNmProduto));
				}
				if (line.contains("vDtManipulacao")) {
					line = line.replace("vDtManipulacao", vDtManipulacao);
				}
				if (line.contains("vDtValidade")) {
					line = line.replace("vDtValidade", vDtValidade);
				}
				if (line.contains("vNmResponsavel")) {
					line = line.replace("vNmResponsavel", zpl.convert(vNmResponsavel));
				}
				if (line.contains("vNmResponsavel")) {
					line = line.replace("vNmResponsavel", zpl.convert(vNmResponsavel));
				}
				if (line.contains("vNmFornecedor")) {
					line = line.replace("vNmFornecedor", zpl.convert(vNmFornecedor));
				}
				if (line.contains("vNrLote")) {
					line = line.replace("vNrLote", zpl.convert(vNrLote));
				}
				if (line.contains("vDsMetConservacao")) {
					line = line.replace("vDsMetConservacao", zpl.convert(vDsMetConservacao));
				}
				if (line.contains("vNrSif")) {
					line = line.replace("vNrSif", zpl.convert(vNrSif));
				}
				if (line.contains("vIdEtiqueta")) {
					line = line.replace("vIdEtiqueta", zpl.convert(vIdEtiqueta));
				}
				if (line.contains("vNmEmpresa")) {

					if (vNmEmpresa.equals("-")) {
						vNmEmpresa = " ";
					}

					line = line.replace("vNmEmpresa", zpl.convert(vNmEmpresa));
				}
				if (line.contains("vDsEnderecoEmpresa")) {

					if (vDsEnderecoEmpresa.equals("-")) {
						vDsEnderecoEmpresa = " ";
					}

					line = line.replace("vDsEnderecoEmpresa", zpl.convert(vDsEnderecoEmpresa));
				}
				if (line.contains("vDsCidadeEstado")) {

					if (vDsCidadeEstado.equals("-")) {
						vDsCidadeEstado = " ";
					}

					line = line.replace("vDsCidadeEstado", zpl.convert(vDsCidadeEstado));
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
