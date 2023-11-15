package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import com.example.models.RegistroExcel;
import com.example.task.PerformerMatriculas;
import com.workfusion.rpa.helpers.Excel;
import com.workfusion.rpa.helpers.ExcelColumnRowPosition;
import com.workfusion.rpa.helpers.RPA;
import static com.example.utils.Constants.SYSTEM_DRIVER;
import static com.example.utils.Constants.noSuchElementException;
import static com.example.utils.Constants.url;

import static com.example.utils.Constants.filePath;
import static com.example.utils.Constants.SYSTEM_PROPERTY_URL;
import static com.example.utils.Constants.COOKIES_BUTTON;
import static com.example.utils.Constants.MATRICULA_ELEMENT;
import static com.example.utils.Constants.MATRICULA_BUTTON;
import static com.example.utils.Constants.ANO_MATRICULA_ELEMENT;

import static com.example.utils.Constants.INPROGRESS;
import static com.example.utils.Constants.PENDING;
import static com.example.utils.Constants.BUSINESS_EXCEPTION;
import static com.example.utils.Constants.SUCCESSFUL;
import static com.example.utils.Constants.SYSTEM_EXCEPTION;

import static com.example.utils.Constants.columnaEstado;
import static com.example.utils.Constants.columnaMatricula;
import static com.example.utils.Constants.columnaAno;
import static com.example.utils.Constants.columnaReintento;

public class MatriculasService{
	private WebDriver driver;
	private List<RegistroExcel> registrosExcel = new ArrayList<>();
	private List<RegistroExcel> registrosExcelCompletados = new ArrayList<>();
	
	private static final Logger logger = Logger.getLogger(PerformerMatriculas.class.getName());
	
    //private final String filePath = "C:\\Neoris\\matriculas.xlsx";

	
	
	public void openURL() {
		 try {
			 //cambiamos el driver a chrome
			 //RPA.driver().switchDriver("chrome");
			 //definimos el WebDriver
			 //driver = RPA.driver();
			 System.setProperty(SYSTEM_DRIVER, SYSTEM_PROPERTY_URL);
			 driver = new ChromeDriver();
			 driver.manage().window().maximize();
			 driver.get(url);
			 WebElement cookiesButton = driver.findElement(By.xpath(COOKIES_BUTTON));
			 cookiesButton.click();
		 } catch (WebDriverException se) {
			 openURL();
		 }
	 }
	 
	 public List<RegistroExcel> getInputData() {
		    //abrimos excel
	        Excel.openExcel(filePath);	
	        
	        //almacenamos las lineas procesadas con las matriculas
	        List<RegistroExcel> listaRegistros = new ArrayList<>();
	        
	        //obtenemos la primera columna del excel con las matriculas
	        List<String> listaMatriculas = Excel.getColumn(filePath, ExcelColumnRowPosition.FIRST);
	        
	        //eliminamos la cabecera
	        listaMatriculas.remove("Matricula");
	        //eliminamos vacios
	        listaMatriculas.removeIf(elemento -> elemento.trim().isEmpty());
	        
	        //empezamos en la segunda linea de excel
	        int fila = 2;
	        
	        //recorremos las matriculas
	        for(String matricula: listaMatriculas) {
	        	//creamos el objeto RegistroExcel añadiendo los campos matriculas,año,estado,reintento
	        	RegistroExcel registroExcel = new RegistroExcel(Excel.getCell(filePath, columnaMatricula+fila).toString(),
	        	Excel.getCell(filePath, columnaAno+fila).toString(), 
	        	Excel.getCell(filePath, columnaEstado+fila).toString(), 
	        	Integer.valueOf(Excel.getCell(filePath, columnaReintento+fila).toString()), 
	        	fila++);

	        	//añadimos el objeto a la lista
	        	listaRegistros.add(registroExcel);
	        }
	        
	        //cerramos el excel
	        Excel.closeExcel(filePath);
	        
	        //listamos los registro
	        for(RegistroExcel registro: listaRegistros)
	        	logger.info("Lista de Registros leidos de Excel: " + registro.toString());
	        
	        //devolvemos los registros
	        return listaRegistros;
	 }
	 
	 public void processMatriculas() {
		 //obtenemos los datos de entrada
		 registrosExcel = getInputData();
		 
		 //abrimos el excel y procesamos las matriculas
		 Excel.openExcel(filePath);
		 	while(!registrosExcel.isEmpty()) {
		 		//obtenemos el primer registroExcel
		 		RegistroExcel re = registrosExcel.get(0);

		 	         
	                try {
	                	//obtenemos el elemento para introducir la matricula
	    	            WebElement inputElement = driver.findElement(By.xpath(MATRICULA_ELEMENT));
	    	            	//inicializamos el texto
	    	            	inputElement.clear();
	    	            	//escribimos el texto
	    	                inputElement.sendKeys(re.getMatricula());	 
	                    //hacemos click en el boton para enviar la matricula
		                driver.findElement(By.xpath(MATRICULA_BUTTON)).click();
	                	//comprobamos si el estado es pending 
	                	if(re.getEstado().equals(PENDING)) {
	                		//ponemos el estado a inprogress
	                		re.setEstado(INPROGRESS);
	                		RPA.sleep(2000);
	                		//obtenemos la fecha de matriculacion
	                		WebElement dateLicensePlate = driver.findElement(By.xpath(ANO_MATRICULA_ELEMENT));
	                		//asignamos la fecha de matriculacion a nuestro objeto 
	                		re.setAnoMatriculacion(dateLicensePlate.getText().substring(6, 10));
	                		//asignamos el estado
	                		re.setEstado(SUCCESSFUL);
	                		//asignamos el numero de reintento
	                		//re.incReintento();
	                		//añadimos el registo a la lista registros completados
	                		registrosExcelCompletados.add(re);
	                		//eliminamos el registroExcel de la lista registrosExcel
	                		registrosExcel.remove(0);
	                	}

	                	//capturamos la excepcion en caso de que el elemento no se encuentre
	                } catch (NoSuchElementException exception) {
	                	//Asignamos matricula no encontrada
	                	re.setAnoMatriculacion(noSuchElementException);
	                	//Asignamos al estado business_exception
	                	re.setEstado(BUSINESS_EXCEPTION);
	                	//aumentamos el numero de reintento
	                	//re.incReintento();
	                	//añadimos a la lista registrosExcelCompletados el registro
	                	registrosExcelCompletados.add(re);
	                //eliminamos el primer elemento
               		registrosExcel.remove(0);
               		//capturamos la exception WebDriver       		
	                } catch (WebDriverException se) {
	                	//comprobamos el numero de reintento
	                	if (3 > re.getReintento()) {
	                		//asignamos el estado a pending
	                		re.setEstado(PENDING);
	                		//incrementamos el numero de reintento
	                		re.incReintento();
	                		logger.warning("Registro incrementa reintento " + re.getMatricula() + " - " + re.getReintento());
	                		this.closeDriver(driver);
	                	} else {
	                		//asignamos el estado a system exception
	                		re.setEstado(SYSTEM_EXCEPTION);	                		
	                		//incrementamos el numero de reintento
	                		//re.incReintento();
	                		logger.warning("Registro supera reintentos " + re.getMatricula() + " - " + re.getReintento());
	                		//añadimos el registro a la lista registroExcelCompletados
	                		registrosExcelCompletados.add(re);
	                		//eliminamos de la lista el primer registro
	                		registrosExcel.remove(0);
	                		this.closeDriver(driver);
	                	}
	                	//abrimos el navegador
	                	openURL();
	                }
		 	}
		 	for(RegistroExcel registro: registrosExcelCompletados)
	        	logger.info("Lista de Registros Completados: " + registro.toString());
	  }
	 
	 public void completarExcel() {
		 //abrimos el fichero excel
		 Excel.openExcel(filePath);
		 //recorremos las filas insertando los valores en las celdas
		 for(RegistroExcel registro: registrosExcelCompletados) {
			 Excel.setCell(filePath, columnaMatricula+registro.getFila(), registro.getMatricula());
			 Excel.setCell(filePath, columnaAno+registro.getFila(), registro.getAnoMatriculacion());
			 Excel.setCell(filePath, columnaEstado+registro.getFila(), registro.getEstado());
			 Excel.setCell(filePath, columnaReintento+registro.getFila(), String.valueOf(registro.getReintento()));
		 }
		 //guardamos excel
		 Excel.saveExcel(filePath);
		 //cerramos excel 
		 Excel.closeExcel(filePath);

		 //cerramos el webdriver
		 this.closeDriver(driver);
	 }
	 
	 public void closeDriver(WebDriver driver) {
	        driver.quit();
	 }
}
