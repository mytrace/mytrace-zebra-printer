package br.com.mytrace.printer_client.core;

public class ConsultaDTO {

	private String id;
	private String chave;

	public ConsultaDTO(String id, String chave) {
		this.id = id;
		this.chave = chave;
	}

	public ConsultaDTO() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

}
