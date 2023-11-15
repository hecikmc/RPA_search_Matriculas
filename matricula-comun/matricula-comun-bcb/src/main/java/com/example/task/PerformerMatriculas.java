package com.example.task;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.inject.Inject;

import com.example.service.MatriculasService;
import com.workfusion.odf2.compiler.BotTask;
import com.workfusion.odf2.core.cdi.Injector;
import com.workfusion.odf2.core.task.AdHocTask;
import com.workfusion.odf2.core.task.TaskInput;
import com.workfusion.odf2.core.task.output.TaskRunnerOutput;
import com.workfusion.odf2.core.webharvest.rpa.RpaDriver;
import com.workfusion.odf2.core.webharvest.rpa.RpaFactory;
import com.workfusion.odf2.core.webharvest.rpa.RpaRunner;

@BotTask(requireRpa = true)
public class PerformerMatriculas implements AdHocTask {
	
	private static final Logger logger = Logger.getLogger(PerformerMatriculas.class.getName());
	private RpaRunner rpaRunner;
	private MatriculasService matriculasService;
    
	
	
    public PerformerMatriculas() {
		 ConsoleHandler consoleHandler = new ConsoleHandler();
		 consoleHandler.setLevel(Level.ALL);
		 consoleHandler.setFormatter(new SimpleFormatter());
		 logger.addHandler(consoleHandler);
		 logger.setLevel(Level.ALL);
	 }
    
	
	 @Inject
	 public PerformerMatriculas(Injector injector) {
	        RpaFactory rpaFactory = injector.instance(RpaFactory.class);
	        rpaRunner = rpaFactory.builder(RpaDriver.DESKTOP).build();
	        matriculasService=new MatriculasService();
	 }
	 
	
	 
	 @Override
	 public TaskRunnerOutput run(TaskInput taskInput) {
	    	logger.info("------------------------- Performer Matriculas -----------------------------");
	    	
	    	rpaRunner.execute(salida -> {
	    		 matriculasService.openURL();
	    		 matriculasService.processMatriculas();
	    		 matriculasService.completarExcel();
	             //matriculasService.closeDriver(driver);
	    	});
	    	
	        return taskInput.asResult()
	                .withColumn("example_bot_task_output", "completed_successfully");
	 }
}


