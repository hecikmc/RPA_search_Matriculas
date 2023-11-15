package com.example.task;

import java.util.List;

import javax.inject.Inject;

import com.workfusion.odf2.compiler.BotTask;
import com.workfusion.odf2.core.cdi.Injector;
import com.workfusion.odf2.core.task.AdHocTask;
import com.workfusion.odf2.core.task.TaskInput;
import com.workfusion.odf2.core.task.output.TaskRunnerOutput;
import com.workfusion.odf2.core.webharvest.rpa.RpaDriver;
import com.workfusion.odf2.core.webharvest.rpa.RpaFactory;
import com.workfusion.odf2.core.webharvest.rpa.RpaRunner;
import com.workfusion.rpa.helpers.Excel;
import com.workfusion.rpa.helpers.ExcelCellPosition;

@BotTask(requireRpa = true)
public class Dispatcher implements AdHocTask{
	
	private RpaRunner rpaRunner;
	
	 private final String file= "C:\\SRC_EjercicioMatriculas\\ProcesoMatriculas.xlsx";	
	
	private final String inputColumn = "A";
	private final String reportColumn = "C";
	private final String matriculaColumn = "B";
	
	private int cont = 1;
	
	@Inject
	public Dispatcher(Injector injector) {
		RpaFactory rpaFactory = injector.instance(RpaFactory.class);
		rpaRunner = rpaFactory.builder(RpaDriver.DESKTOP).build();
	}
	
	// Metodo que devuelve una lista de datos(string) de un fichero excel de la columna que se le indique por parametros.
	private void builtReport() {
		//abrimos excel
		Excel.openExcel(file);
		//creamos lista con los datos
		List<String> datas;
		//obtenemos las columna de las matriculas
		datas = Excel.getColumn(file, inputColumn);
		// Añade 2 columnas nuevas al fichero excel con el estado de procesamiento y el numero de intentos de ejecucion inicializados a 0.
		
		
		Excel.setCell(file, reportColumn + cont, "Status");
		Excel.setCell(file, ExcelCellPosition.CELL_TO_THE_RIGHT, "Reintento");
		cont++;
		datas.forEach(item -> {
			if (!item.equals("Matricula")) {
				Excel.setCell(file, matriculaColumn+cont, "");
				Excel.setCell(file, reportColumn + cont, "Pending");
				Excel.setCell(file, ExcelCellPosition.CELL_TO_THE_RIGHT, "0");
				cont++;
			}
		});
		Excel.saveExcel(file);
		Excel.closeExcel(file);
	}
	
	@Override
	public TaskRunnerOutput run(TaskInput taskInput) {
		rpaRunner.execute(salida -> {
			builtReport();
		});
		return taskInput.asResult()
                .withColumn("example_bot_task_output", "completed_successfully");
	}
}
