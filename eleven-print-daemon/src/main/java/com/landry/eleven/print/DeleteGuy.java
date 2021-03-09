package com.landry.eleven.print;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.landry.eleven.print.dto.PrintJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DeleteGuy {

	Logger logger = LoggerFactory.getLogger(DeleteGuy.class);

	@Value("${delete.print.job.uri:/api/print-jobs}")
	private String uri;

	@Autowired
	RestCaller restCaller;

	@Autowired
	PrintDaemon printDaemon;

	public DeleteGuy() {
		super();
	}

	public void doDeleteWork( PrintJob[] printJobs) {

		for (PrintJob pj : printJobs) {
			logger.info("Deleting print job with id: " + pj.getId());
			logger.debug("The uri: " + printDaemon.getUrlHeader() + uri + "/" + pj.getId());
			restCaller.delete(printDaemon.getUrlHeader() + uri + "/" + pj.getId());
		}

	}

}
