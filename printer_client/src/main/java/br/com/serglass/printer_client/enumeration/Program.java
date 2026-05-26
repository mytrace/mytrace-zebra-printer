package br.com.serglass.printer_client.enumeration;

import br.com.serglass.printer_client.ExecutableProgram;
import br.com.serglass.printer_client.impl.programs.EtiquetaInspecaoGarantia;
import br.com.serglass.printer_client.impl.programs.EtiquetaInspecaoPedido;
import br.com.serglass.printer_client.impl.programs.EtiquetaLaminaGarantia;
import br.com.serglass.printer_client.impl.programs.EtiquetaLaminaPedido;
import br.com.serglass.printer_client.impl.programs.EtiquetaLaminacao;
import br.com.serglass.printer_client.impl.programs.EtiquetaPecaGarantia;
import br.com.serglass.printer_client.impl.programs.EtiquetaPecaPedido;
import br.com.serglass.printer_client.impl.programs.EtiquetaProducaoCarbon;
import br.com.serglass.printer_client.impl.programs.EtiquetaRhCrachaProducao;

public enum Program {

	 PRINT_ETIQUETA_LAMINA_PEDIDO(1, "etiqueta_lamina_pedido",
			new EtiquetaLaminaPedido()),
	 PRINT_ETIQUETA_LAMINA_GARANTIA(2, "etiqueta_lamina_garantia", new EtiquetaLaminaGarantia()),
	 PRINT_ETIQUETA_LAMINACAO(2, "etiqueta_laminacao", new EtiquetaLaminacao()),
	 PRINT_ETIQUETA_PECA_PEDIDO(3, "etiqueta_peca_pedido", new EtiquetaPecaPedido()),
	 PRINT_ETIQUETA_PECA_GARANTIA(4, "etiqueta_peca_garantia", new EtiquetaPecaGarantia()), 
	 PRINT_ETIQUETA_INSPECAO_PEDIDO(5, "etiqueta_inspecao_pedido", new EtiquetaInspecaoPedido()),
	 PRINT_ETIQUETA_INSPECAO_GARANTIA(6, "etiqueta_inspecao_garantia", new EtiquetaInspecaoGarantia()), 
	 PRINT_ETIQUETA_RH_CRACHA_PRODUCAO(7, "etiqueta_rh_cracha_producao", new EtiquetaRhCrachaProducao()), 
	 PRINT_ETIQUETA_PRODUCAO_CARBON(8, "etiqueta_producao_carbon", new EtiquetaProducaoCarbon()), 
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
