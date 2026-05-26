package br.com.serglass.printer_client.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import br.com.mytrace.core.domain.AjaxResult;
import br.com.mytrace.core.domain.printer.Job;
import br.com.mytrace.core.util.exception.ServiceException;

public class JobHelper {

	private String uri;
	private String dataBasePath;
	private String maquinaSolicitante;

	public JobHelper(String uri, String dataBasePath, String maquinaSolicitante)
			throws SQLException, ClassNotFoundException {
		this.uri = uri;
		this.dataBasePath = dataBasePath;
		this.maquinaSolicitante = maquinaSolicitante;

		/*
		 * Faz uma primeira conexao para validar caminho da base de dados
		 */
		Connection conn = null;
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(String.format(
					"jdbc:h2:file:%s;IFEXISTS=TRUE", dataBasePath), "admin",
					"admin");
		} catch (ClassNotFoundException e) {
			System.out.println("Erro ao inicializar base de dados.");
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			System.out.println("Erro ao inicializar base de dados.");
			e.printStackTrace();
			throw e;
		} finally {
			conn.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Job> obtemSolicitacoesDeImpressao() throws ServiceException {

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("impressora", maquinaSolicitante);

		RestTemplate restTemplate = new RestTemplate();

		String operacao = "/impressao/consultar";

		ResponseEntity<AjaxResult> response = restTemplate.getForEntity(String
				.format("%s%s?impressora=%s", uri, operacao,
						maquinaSolicitante), AjaxResult.class);

		AjaxResult results = null;
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new ServiceException(
					String.format(
							"Falha de comunicacao com o servidor de impressao. Status [%s].",
							response.getStatusCode().getReasonPhrase()));
		} else {
			results = response.getBody();
		}

		if (results == null) {
			throw new ServiceException(
					"Falha de comunicacao com o servidor de impressao");
		} else if (results.getHasError()) {
			throw new ServiceException(String.format("Detalhe [%s]",
					results.getMsg()));
		}

		Set<Integer> idsJobsJaSolicitados = consultaIdsJobsSolicitados();

		List<LinkedHashMap<String, Object>> jobsList = (List<LinkedHashMap<String, Object>>) results
				.getData();
		List<Job> listaDefinitiva = new ArrayList<Job>();
		for (LinkedHashMap<String, Object> mapItem : jobsList) {
			Integer id = (Integer) mapItem.get("id");

			if (!idsJobsJaSolicitados.contains(id)) {
				Job job = new Job(id, (String) mapItem.get("data"),
						(String) mapItem.get("programa"));
				listaDefinitiva.add(job);
			}
		}

		return listaDefinitiva;
	}

	public void registrarJobsSolicitados(List<Job> jobList)
			throws ServiceException {

		StringBuilder sb = new StringBuilder();
		for (Job job : jobList) {
			sb.append(job.getId());
			sb.append(";");
		}
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("jobs", sb.toString());

		RestTemplate restTemplate = new RestTemplate();

		String operacao = "/impressao/notificar";

		AjaxResult results = null;
		try {
			results = restTemplate.postForObject(uri + operacao, map,
					AjaxResult.class);
		} catch (Exception e) {
			salvarJobsAtendidosNaoNotificados(jobList);
			throw new ServiceException(String.format("Detalhe [%s]",
					results.getMsg()));
		}

		if (results == null || results.getHasError()) {
			salvarJobsAtendidosNaoNotificados(jobList);
			throw new ServiceException(String.format("Detalhe [%s]",
					results.getMsg()));
		}

	}

	public void notificarJobsAtendidos() {

		Set<Integer> jobsAtendidosNaoSolicitados = consultaIdsJobsSolicitados();

		if (!CollectionUtils.isEmpty(jobsAtendidosNaoSolicitados)) {

			StringBuilder sb = new StringBuilder();
			for (Integer id : jobsAtendidosNaoSolicitados) {
				sb.append(id);
				sb.append(";");
			}

			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("jobs", sb.toString());

			RestTemplate restTemplate = new RestTemplate();

			String operacao = "/impressao/notificar";

			AjaxResult results = restTemplate.postForObject(uri + operacao,
					map, AjaxResult.class);

			if (results != null && !results.getHasError()) {
				limpaListaJobsAtendidosNaoNotificados();
			}
		}

	}

	private void limpaListaJobsAtendidosNaoNotificados() {

		Connection conn = null;
		PreparedStatement stm = null;
		try {

			conn = getDBConnection();
			stm = conn.prepareStatement("delete from ATENDIDOS");
			stm.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
			try {
				stm.close();
			} catch (SQLException e) {
			}
		}
	}

	private void salvarJobsAtendidosNaoNotificados(List<Job> jobsAtendidosList) {

		for (Job job : jobsAtendidosList) {
			Connection conn = null;
			PreparedStatement stm = null;
			try {

				conn = getDBConnection();
				stm = conn
						.prepareStatement("insert into ATENDIDOS(ID, DATA) values(?, ?)");
				stm.setInt(1, job.getId());
				stm.setString(2, job.getData());

				stm.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
				continue;
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
				}
				try {
					stm.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	private Set<Integer> consultaIdsJobsSolicitados() {
		Set<Integer> jobSet = new HashSet<Integer>();

		Connection conn = null;
		PreparedStatement stm = null;
		try {
			conn = getDBConnection();
			stm = conn.prepareStatement("select ID, DATA from ATENDIDOS");
			ResultSet rs = stm.executeQuery();

			while (rs.next()) {
				jobSet.add(rs.getInt("ID"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}

			try {
				stm.close();
			} catch (SQLException e) {
			}
		}

		return jobSet;
	}

	private Connection getDBConnection() throws ClassNotFoundException,
			SQLException {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(String.format(
				"jdbc:h2:file:%s;IFEXISTS=TRUE;MVCC=TRUE", dataBasePath),
				"admin", "admin");
		return conn;
	}
}
