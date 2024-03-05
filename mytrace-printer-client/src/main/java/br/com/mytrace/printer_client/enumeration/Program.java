package br.com.mytrace.printer_client.enumeration;

import br.com.mytrace.printer_client.ExecutableProgram;
import br.com.mytrace.printer_client.impl.programs.EtiquetaAtivo;
import br.com.mytrace.printer_client.impl.programs.EtiquetaTeste;

public enum Program {

	 PRINT_ETIQUETA_TESTE(1, "teste_basico",new EtiquetaTeste()),
	 PRINT_ATIVO_V1(2, "etiqueta_ativo",new EtiquetaAtivo())
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
