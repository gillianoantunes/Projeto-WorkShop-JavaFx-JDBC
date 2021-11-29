package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException{


	private static final long serialVersionUID = 1;
	
	//para guardar os erros de cada campo do formulario
	//para o campo nome o erro é este, para o campo email o erro é esse e assim por diante
	//usei map para guardar pares, primeiro string indica o nome do campo
	//o segundo string do mao indica o nome do erro
	private Map<String, String> errors = new  HashMap<>();
	
	
// classe que mostra erro na tela se houver erro nos txts
	public ValidationException(String msg) {
		super(msg);
	}
	
	//metodo get dos erros 
	public Map<String, String>  getErrors(){
		return errors;
	}
	
	//adiciona erros no map informando o fildName que é o nome do campo
	// e a mensagem de erro
	public void addError(String fieldName, String errorMessage) {
		//para inserir no map
		errors.put(fieldName, errorMessage);
	}
	
}
