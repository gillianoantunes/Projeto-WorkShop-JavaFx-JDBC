package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.services.DepartmentService;

//controlador da tela DepartmentForm
public class DepartmentFormController implements Initializable{
	
	//dependencia do departamento
	private Department entity;
	
	//dependencia de departmentservice
	private DepartmentService service;

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
	
	//metodoset do DepartmentService criando instancia construtor
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	//metodos dos eventos do botoes
	//salvar departamento no banco de dados
	//passar o parametro da janela atual para ser fechada depois que salvar
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		
		//se entity estiver valendo nullo
		if(service == null) {
			throw new IllegalStateException("Service está nulo");
		}
		//se o service estiver nullo
		if(entity == null) {
			throw new IllegalStateException("Entidade está nula");
		}
		//colocar todas operações com o banco dentro do try
		try {
		//getformdata metodo que criei para pegar dados nas caixinhas e instanciar departamento
		entity = getFormData();
		//chama o metodo saveorUpdate em DepartmentService para salvar no banco
		service.saveOrUpdate(entity);
		//depois que salvar fechar a janela atual pegando o paramentro da janela atual
		Utils.cuurentStage(event).close();
		}
		catch(DbException e) {
			Alerts.showAlert("Erro em salvar", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	//pega os dados do formulario e instancia em departamento
	private Department getFormData() {
		Department obj = new Department();
		//utils.tryParseToInt criei metodo para converter para inteiro
		obj.setId((Utils.tryParseToInt(txtId.getText())));
		obj.setName(txtName.getText());
		
		return obj;
	}

	
	//para cancelar fechando a janela passar evento da janela atual
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.cuurentStage(event).close();
	}
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//chama o método abaixo assim que inicilizar o formulario
		inicializeNodes();
		
	}

	//metodos para tratar inicio das caixinhas de texto
	private void inicializeNodes() {
		//só aceita inteiros na caixa ID
		Constraints.setTextFieldInteger(txtId);
		//só aceita 30 caracteres no nome do departamento
		Constraints.setTextFieldMaxLength(txtName, 30);
		
	}
	
	//pega os dados do departamento e joga no formulário
	public void UpdateFormData() {
		//testar se o entity esta nulo lança excecao
		if((entity == null)) {
			throw new IllegalStateException("Entidade está nula");
		}
		//valueof converte o id para inteiro pois a caixinha de texto é string
		txtId.setText(String.valueOf(entity.getId()));
		txtId.setText(entity.getName());
	}
}
