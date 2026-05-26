package br.com.serglass.printer_client;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import br.com.mytrace.core.domain.printer.Job;
import br.com.mytrace.core.util.exception.ServiceException;
import br.com.serglass.printer_client.Printer.Status;
import br.com.serglass.printer_client.impl.JobHelper;
import br.com.serglass.printer_client.impl.UsbPrinter;

public class Main {

	static String MSG_ENCERRANDO_EXECUCAO = "Encerrando execucao.";

	private static Map<String, Object> PRINTER_MAP = new HashMap<String, Object>();

	public static void main(String[] args) throws InterruptedException,
			ClassNotFoundException, SQLException {

		System.out.println("Iniciando client de impressao...");

		inicializaDevices();

		String deviceId = args != null && args.length > 0 ? (String) args[0]
				: null;
		if (deviceId == null || deviceId.isEmpty()) {
			System.out
					.println("ERRO: Parametro de entrada invalido. Obrigatorio informar o nome do dispositivo.");
			System.out.println(MSG_ENCERRANDO_EXECUCAO);
			return;
		}

		String uriImpressao = args != null && args.length > 1 ? (String) args[1]
				: null;
		if (uriImpressao == null || uriImpressao.isEmpty()) {
			System.out
					.println("ERRO: Parametro de entrada invalido. Obrigatorio informar a url do server de impressao.");
			System.out.println(MSG_ENCERRANDO_EXECUCAO);
			return;
		}

		String dataBasePath = args != null && args.length > 2 ? (String) args[2]
				: null;
		if (dataBasePath == null || dataBasePath.isEmpty()) {
			System.out
					.println("ERRO: Parametro de entrada invalido. Obrigatorio informar a url da base de dados local.");
			System.out.println(MSG_ENCERRANDO_EXECUCAO);
			return;
		}

		String maquinaSolicitante = args != null && args.length > 3 ? (String) args[3]
				: null;
		if (maquinaSolicitante == null || maquinaSolicitante.isEmpty()) {
			System.out
					.println("ERRO: Parametro de entrada invalido. Obrigatorio informar a maquina solicitante.");
			System.out.println(MSG_ENCERRANDO_EXECUCAO);
			return;
		}

		deviceId = deviceId.toLowerCase();
		Printer printer = (Printer) PRINTER_MAP.get(deviceId);
		if (printer == null) {
			System.out
					.println(String
							.format("ERRO: NAO existem configuracoes validas para o dispositivo [%s].",
									deviceId));
		}

		checkStatusSpooler(printer, deviceId);

		JobHelper printerService = new JobHelper(uriImpressao, dataBasePath,
				maquinaSolicitante);

		System.out
				.println("Executando. Pressione 'CONTROL C' para interromper...");

		while (true) {
			printerService.notificarJobsAtendidos();

			List<Job> jobsPendentesList = null;

			try {
				jobsPendentesList = printerService
						.obtemSolicitacoesDeImpressao();
			} catch (ServiceException e) {
				System.out
						.println(String
								.format("Erro: %s. Uma nova tentativa sera realizada em alguns segundos...",
										e.getMessage()));
				Thread.sleep(10000);
				continue;
			} catch (Exception e) {
				System.out
						.println("Ocorreu uma falha ao comunicar com o servidor de impressao. Uma nova tentativa sera realizada em alguns segundos...");
				e.printStackTrace();
				Thread.sleep(10000);
				continue;
			}

			if (!CollectionUtils.isEmpty(jobsPendentesList)) {
				System.out.println("Jobs Pendentes");
			} else {
				Thread.sleep(10000);
				continue;
			}

			for (Job job : jobsPendentesList) {
				System.out.println(String.format("%s - %s. Executando [%s]",
						job.getId(), job.getData(), job.getPrograma()));
			}

			System.out.println("Imprimindo..");

			List<Job> jobsAtendidos = new ArrayList<Job>();
			for (Job job : jobsPendentesList) {
				if (printer.executeCommand(deviceId, job.getPrograma(),
						job.getData()) != 1) {
					System.out.println(String.format(
							"Falha ao imprimir job [%s, %s].", job.getId(),
							job.getData()));
				} else {
					System.out.println(String.format(
							"Label [%s] enviado ao Spooler de impressao.",
							job.getData()));

					jobsAtendidos.add(job);
				}
			}

			try {
				printerService.registrarJobsSolicitados(jobsAtendidos);
			} catch (ServiceException e) {
				System.out
						.println("Houve uma falha ao notificar o servidor de impressao sobre os Jobs atendidos. Os dados foram salvos localmente.");
			}

			Thread.sleep(1000);
			System.out.println("----------");
		}
	}

	public static void inicializaDevices() {
		System.out.println("Inicializando configuracoes de dispositivo...");
		PRINTER_MAP.put("gc420t", new UsbPrinter());
		//PRINTER_MAP.put("gc420t", new MockPrinter());
		System.out.println("ok!");
	}

	public static Status checkStatusSpooler(Printer printer, String deviceId)
			throws InterruptedException {
		Status status;
		while (true) {
			System.out.println("Obtendo Status do Spooler de impressao...");
			status = printer.getStatus(deviceId);
			System.out.println("STATUS: " + status);
			if (status == Status.ACCEPTING_JOBS) {
				return status;
			}
			System.out.println("Aguardando status valido.");
			Thread.sleep(4000);
		}
	}

}
