package com.landry.eleven.print.packrat;

import java.util.List;
//OBSOLETE not using this. We tried to implement the http invoker...
public interface  PrintJobApi {
	public List<PrintJobDto> findPrintJobsByPrinterId(Integer printerId);
}
