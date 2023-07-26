package br.com.mytrace.printer_client.mains;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.mytrace.printer_client.core.ConsultaDTO;
import br.com.mytrace.printer_client.core.ImpressoraDTO;

public class UpdateMain {

	public static Logger LOG = LoggerFactory.getLogger(UpdateMain.class);

	public static void main(String[] args) throws Exception {

		String endpoint = args[0];
		String idImpressora = args[1];

		if (args.length != 2) {
			throw new RuntimeException("Num. de parametros invalido.");
		}

		RestTemplate http = new RestTemplate();

		try {
			LOG.info("****************************************************");
			LOG.info("**********ROTINA DE ATUALIZACAO BTAGS***************");
			LOG.info("****************************************************");

			ImpressoraDTO impressora = consultaDadosImpressora(http, endpoint, idImpressora);

			LOG.info(String.format("Atualizando dados da impressora [id: %s, num_serie: %s, id_programa: %s]",
					impressora.getIdImpressora(), impressora.getDsNumeroSerie(), impressora.getIdPrograma()));

			atualizaProgramaZpl(http, endpoint, idImpressora);
			atualizaRunner(http, endpoint, idImpressora);

			LOG.info("Fim da execucao.");
		} catch (Exception e) {
			LOG.error("Erro.", e);
			throw e;
		}

	}

	public static ImpressoraDTO consultaDadosImpressora(RestTemplate http, String endpoint, String id)
			throws InterruptedException {
		LOG.info("Consultando dados impressora...");
		ImpressoraDTO impressora = new ImpressoraDTO();

		HttpEntity<ConsultaDTO> request = new HttpEntity<>(new HttpHeaders());

		ResponseEntity<JsonNode> response = http.exchange(endpoint + "/impressora/" + id, HttpMethod.GET, request,
				JsonNode.class);

		JsonNode body = response.getBody();

		if (!body.get("hasError").asBoolean()) {

			JsonNode node = (JsonNode) body.get("data");
			impressora.setIdImpressora(node.get("idImpressora").asInt());
			impressora.setDsNumeroSerie(node.get("dsNumeroSerie").asText());
			impressora.setIdPrograma(node.get("idPrograma").asInt());
			impressora.setDsPrograma(node.get("dsPrograma").asText());

		}

		return impressora;
	}

	public static void atualizaProgramaZpl(RestTemplate http, String endpoint, String idImpressora) throws IOException {
		try {
			LOG.info("Consultando arquivo de programa ZPL.");

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<byte[]> response = http.exchange(endpoint + "/programa/zpl/" + idImpressora, HttpMethod.GET,
					entity, byte[].class);
			File file = new File("./etiqueta_btags");

			if (file.exists()) {
				file.delete();
			}

			if (response.getBody().length > 0) {
				LOG.info("Atualizando arquivo [etiqueta_btags].");
				Files.write(Paths.get("./etiqueta_btags"), response.getBody());
				LOG.info("OK.");
			} else {
				LOG.error("Arquivo nao foi atualizado. Checar configuracoes");
			}

		} catch (IOException e) {
			LOG.error("Erro ao fazer o download do programa de impressao");
			throw e;
		}
	}

	public static void atualizaRunner(RestTemplate http, String endpoint, String idImpressora) throws IOException {
		try {
			LOG.info("Consultando arquivo do executavel [runner.jar].");

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<byte[]> response = http.exchange(endpoint + "/programa/runner/" + idImpressora,
					HttpMethod.GET, entity, byte[].class);
			File file = new File("./runner.jar");

			if (file.exists()) {
				file.delete();
			}

			if (response.getBody().length > 0) {
				LOG.info("Atualizando arquivo [runner.jar].");
				LOG.info("Aguarde download ...%...%...%...");
				Files.write(Paths.get("./runner.jar"), response.getBody());
				LOG.info("OK.");
			} else {
				LOG.error("Arquivo nao foi atualizado. Checar configuracoes");
			}

		} catch (IOException e) {
			LOG.error("Erro ao fazer o download do programa de impressao");
			throw e;
		}
	}

}
