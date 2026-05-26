package br.com.serglass.printer_client;


public interface Printer {

	enum Status {
		UNKNOW_DRIVER, NOT_ACCEPTING_JOBS, ACCEPTING_JOBS, UNKNOW
	}

	Status getStatus(String printerId);

	int executeCommand(String deviceId, String programName, String data);
}
