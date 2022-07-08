package br.com.mytrace.printer_client.impl;

import br.com.mytrace.printer_client.Printer;

public class MockPrinter implements Printer {

	public Status getStatus(String printerId) {
		return Status.ACCEPTING_JOBS;
	}

	public int executeCommand(String deviceId, String programName, String data) {
		return 1;
	}

}
