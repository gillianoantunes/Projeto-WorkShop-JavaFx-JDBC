package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

//controlador da tela DepartmentForm
public class DepartmentFormController implements Initializable{
	
	//dependencia do departamento
	private Department entity;

	//declarar os componentes da tela
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	
	//label com mensagem de erro
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	//metodo para implemtar set do entity criado em cima do department
	//criou a instacia contrutor do departamento no controlador
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	

	
	//metodos dos eventos do botoes
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("Salvar");
	}
	
	@FXML
	public void onBtCancelAction() {
		System.out.println("Cancelar");
	}
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//chama o m�todo abaixo assim que inicilizar o formulario
		inicializeNodes();
		
	}

	//metodos para tratar inicio das caixinhas de texto
	private void inicializeNodes() {
		//s� aceita inteiros na caixa ID
		Constraints.setTextFieldInteger(txtId);
		//s� aceita 30 caracteres no nome do departamento
		Constraints.setTextFieldMaxLength(txtName, 30);
		
	}
	
	//pega os dados do departamento e joga no formul�rio
	public void UpdateFormData() {
		//testar se o entity esta nulo lan�a excecao
		if((entity == null)) {
			throw new IllegalStateException("Entidade est� nula");
		}
		//valueof converte o id para inteiro pois a caixinha de texto � string
		txtId.setText(String.valueOf(entity.getId()));
		txtId.setText(entity.getName());
	}
}
