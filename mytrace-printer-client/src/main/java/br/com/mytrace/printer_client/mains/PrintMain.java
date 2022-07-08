package br.com.mytrace.printer_client.mains;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import br.com.mytrace.printer_client.Printer;
import br.com.mytrace.printer_client.Printer.Status;
import br.com.mytrace.printer_client.core.SolicitacaoImpressaoDTO;
import br.com.mytrace.printer_client.impl.UsbPrinter;

public class PrintMain {

	public static void main(String[] args) throws InterruptedException {

		System.out.println("Iniciando setup impressora...");

		if (args.length != 3) {
			System.out.println("ERRO: NUMERO DE PARAMETROS INVALIDOS.");
			return;
		}

		String endpoint = args[0];
		String device = args[1];
		String estacao = args[2];

		Printer printer = new UsbPrinter();
		Status status = printer.getStatus(device);

		if (!Status.ACCEPTING_JOBS.equals(status)) {
			System.out.println("ERRO: IMPRESSORA NAO DISPONIVEL.");
			return;
		}

		System.out.println("Aguardando solicitacoes. Pressione 'CONTROL C' para interromper...");
		System.out.println("------------------------------------------------------------------");

		RestTemplate http = new RestTemplate();
		while (true) {

			List<SolicitacaoImpressaoDTO> solicitacoes = new ArrayList<>();
			try {
				solicitacoes = obtemSolicitacoes(http, endpoint + "/impressao/consultar?impressora=" + estacao);
			} catch (Exception e) {
				System.out.println("ERRO AO COMUNICAR COM SERVIDOR DE IMPRESSAO. COMUNICAR A EQUIPE DE SUPORTE:");
				System.out.println(ExceptionUtils.getStackTrace(e));
				Thread.sleep(10000);
				continue;
			}

			if (!CollectionUtils.isEmpty(solicitacoes)) {

				List<Integer> ids = new ArrayList<>();

				for (SolicitacaoImpressaoDTO sol : solicitacoes) {

					try {
						System.out.println(String.format("JOB [%s]: [%s]. Programa [%s].", sol.getId(), sol.getData(),
								sol.getPrograma()));
						printer.executeCommand(device, sol.getPrograma(), sol.getData());

						ids.add(sol.getId());
					} catch (Exception e) {
						System.out.println(String.format(
								"ERRO INESPERADO AO EXECUTAR JOB [%s]: [%s]. COMUNICAR A EQUIPE DE SUPORTE:",
								sol.getId(), sol.getData()));
						System.out.println(ExceptionUtils.getStackTrace(e));
					}
				}

				notificarSolicitacoesAtendidas(http, ids, endpoint + "/impressao/notificar");
				System.out.println("------------------------------------------------------------------");
			}
			
			Thread.sleep(2000);
		}
	}

	public static List<SolicitacaoImpressaoDTO> obtemSolicitacoes(RestTemplate http, String uri)
			throws InterruptedException {

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> entity = new HttpEntity<>(headers);

		ResponseEntity<JsonNode> response = http.exchange(uri, HttpMethod.GET, entity, JsonNode.class);

		JsonNode body = response.getBody();

		List<SolicitacaoImpressaoDTO> solicitacoes = new ArrayList<>();
		if (!body.get("hasError").asBoolean()) {

			ArrayNode nodes = (ArrayNode) body.get("data");

			for (JsonNode node : nodes) {
				SolicitacaoImpressaoDTO sol = new SolicitacaoImpressaoDTO();
				sol.setId(node.get("id").asInt());
				sol.setData(node.get("data").asText());
				sol.setPrograma(node.get("programa").asText());
				solicitacoes.add(sol);
			}
		}

		return solicitacoes;
	}

	public static void notificarSolicitacoesAtendidas(RestTemplate http, List<Integer> ids, String uri) {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> entity = new HttpEntity<>(headers);

		if (!CollectionUtils.isEmpty(ids)) {
			uri += "?jobs=";

			for (Integer id : ids) {
				uri += id + ",";
			}

			uri = uri.substring(0, uri.length() - 1);

			http.exchange(uri, HttpMethod.GET, entity, JsonNode.class);
		}
	}

	public static void logarSolicitacoes(List<SolicitacaoImpressaoDTO> solicitacoes) {
		for (SolicitacaoImpressaoDTO s : solicitacoes) {
			System.out.println(String.format("%s: %s", s.getId(), s.getData()));
		}
		System.out.println("------------------------------------------------------------------");
	}

}
