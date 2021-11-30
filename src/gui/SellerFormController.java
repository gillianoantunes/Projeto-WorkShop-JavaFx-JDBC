package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

//controlador da tela SellerForm
public class SellerFormController implements Initializable{
	
	//dependencia do departamento
	private Seller entity;
	
	//dependencia de departmentservice
	private SellerService service;

	// guarda uma lista de objetos que quiera receber o evento de atualizacao da lista
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	
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
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	//metodoset do SellerService criando instancia construtor
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	//metodo para inscrever na lista atualizada listener
	//todo objeto que implementar a interface DataChangeListener pode increver para receber este evento que atualiza depois de salvar,alterar ou excluir
	//se salvar com sucesso no botao salvar alterar la
	public void subscribeDataChangeListener(DataChangeListener listeners) {
		dataChangeListeners.add(listeners);
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
		//chama o metodo saveorUpdate em SellerService para salvar no banco
		service.saveOrUpdate(entity);
		//se salvar com sucesso chamar o metodo para atualizar lista
		notifyDataChangeListerners();
		//depois que salvar fechar a janela atual pegando o paramentro da janela atual
		Utils.currentStage(event).close();
		}
		//tratar o validationException 
		catch(ValidationException e) {
			//chama o metodo abaixo setErrorMessage que escreve o erro no label da tela
			//passando e.getErros que é nome do meu map que vai ser aquela coleção de erros na classe ValidtionException
			setErrorMessages(e.getErrors());
		}
		catch(DbException e) {
			Alerts.showAlert("Erro em salvar", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	// executar o metodo onDataChanged na interface DataChangeListener para atualizar
	//o listeners emite o metodo o observer na claase SellerList irá escutar o metodo Listener
	private void notifyDataChangeListerners() {
		//para cada datachangeListeners pertencente a minha lista dataChangeListeners vou fazer
		//um listener.onDataChanged
		for (DataChangeListener listener : dataChangeListeners) {
			//este metodo estara implementado em departmenteListController
			//ele atualiza os dados da tabela
			listener.onDataChanged();
		}
	}

	//pega os dados do formulario e instancia em departamento
	private Seller getFormData() {
		Seller obj = new Seller();
		
		//verificar se tem erro primeiro instanciar o validationException
		ValidationException exception = new ValidationException("Validação de erro");
		//utils.tryParseToInt criei metodo para converter para inteiro
		obj.setId((Utils.tryParseToInt(txtId.getText())));
		
		//verificar se txt name esta vazio
		//se igual a null ou igual a string vazio equals
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			//adicona erro na exceçaõ para guardar no map na classe ValidationException
			//exeption é o nome da instancia que fiz acima
			//adicona o nome do campo e a mensagem
			exception.addError("name","O campo não pode ser vazio");
		}
		obj.setName(txtName.getText());
		
		//depois que passar disso mesmo vazio eu vou setar
		//se existir algum erro eu lanço a minha exceção
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
	}

	
	//para cancelar fechando a janela passar evento da janela atual
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
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
		txtName.setText(entity.getName());
	}
	
	//metodo para escrever no label na tela se houve algum erro
	//percorre o map com os erros
	private void setErrorMessages(Map<String, String> errors) {
		//uma coleçao recebe os erros
		Set<String> fields = errors.keySet();
		
		//testar se contem o valor name, se existir eu pego o label na tela e escrevo o texto dele com a mensagem de erro
		//setar o label
		if(fields.contains("name")) {	
			// joga para o label de erros la na tela
			labelErrorName.setText(errors.get("name"));
		}
		
	}
}
