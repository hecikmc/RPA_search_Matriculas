package com.example.models;

public class RegistroExcel {
	
	private String matricula;
	private String anoMatriculacion;
	private String estado;
	private int reintento;
	private int fila;

	public RegistroExcel() {}
	
	public RegistroExcel(String matricula, String anoMatriculacion, String estado, int reintento, int fila) {
		this.matricula = matricula;
		this.anoMatriculacion = anoMatriculacion;
		this.estado = estado;
		this.reintento = reintento;
		this.fila = fila;
	}
	
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getAnoMatriculacion() {
		return anoMatriculacion;
	}
	public void setAnoMatriculacion(String anoMatriculacion) {
		this.anoMatriculacion = anoMatriculacion;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public int getReintento() {
		return reintento;
	}
	public void setReintento(int reintento) {
		this.reintento = reintento;
	}
	public int getFila() {
		return fila;
	}
	public void setFila(int fila) {
		this.fila = fila;
	}
	
	public void incReintento() {
		this.reintento++;
	}

	@Override
	public String toString() {
		return "RegistroExcel [matricula=" + matricula + ", anoMatriculacion=" + anoMatriculacion + ", estado=" + estado
				+ ", reintento=" + reintento + ", fila=" + fila + "]";
	}
	
	

}
