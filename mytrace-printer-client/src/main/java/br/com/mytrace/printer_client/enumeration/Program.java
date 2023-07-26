package br.com.mytrace.printer_client.enumeration;

import br.com.mytrace.printer_client.ExecutableProgram;
import br.com.mytrace.printer_client.impl.programs.Apontamento;
import br.com.mytrace.printer_client.impl.programs.EtiquetaBtags;
import br.com.mytrace.printer_client.impl.programs.EtiquetaProducao;
import br.com.mytrace.printer_client.impl.programs.EtiquetaTeste;

public enum Program {

	 PRINT_ETIQUETA_TESTE(1, "teste_basico",new EtiquetaTeste()),
	 PRINT_ETIQUETA_LAMINA_PEDIDO(2, "apontamento",new Apontamento()),
	 PRINT_ETIQUETA_PRODUCAO(3, "etiqueta_producao",new EtiquetaProducao()),
	 PRINT_BTAGS_V1(4, "etiqueta_btags",new EtiquetaBtags())
	;

	private Program(int code, String name, ExecutableProgram executable) {
		this.code = code;
		this.name = name;
		this.executable = executable;

	}

	private int code;
	private String name;
	private ExecutableProgram executable;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExecutableProgram getExecutable() {
		return executable;
	}

	public void setExecutable(ExecutableProgram executable) {
		this.executable = executable;
	}

	public static Program getByName(String name) {
		for (Program program : Program.values()) {
			if (program.getName().equals(name)) {
				return program;
			}
		}
		return null;
	}

	public static Program getByCode(Integer code) {
		for (Program program : Program.values()) {
			if (program.getCode() == code) {
				return program;
			}
		}
		return null;
	}
}
