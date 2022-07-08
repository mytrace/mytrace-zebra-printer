package br.com.mytrace.printer_client.core;

public class SolicitacaoImpressaoDTO {

	private Integer id;
	private String data;
	private String programa;

	public String getPrograma() {
		return programa;
	}

	public void setPrograma(String programa) {
		this.programa = programa;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
