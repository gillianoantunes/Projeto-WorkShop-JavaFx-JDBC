package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

//controlador da tela SellerForm
//para usar o tab e percorrer os campos da tela, basta colocar um debaixo do outro no SceneBuilder
public class SellerFormController implements Initializable {

	// dependencia do departamento
	private Seller entity;

	// dependencia de departmentservice
	private SellerService service;

	private DepartmentService departmentService;

	// guarda uma lista de objetos que quiera receber o evento de atualizacao da
	// lista
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	// declarar os componentes da tela
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	// para datas usa o datepicker
	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	// combobox cujo os objetos serão do tipo Department
	@FXML
	private ComboBox<Department> comboBoxDepartment;

	// label com mensagem de erro
	@FXML
	private Label labelErrorName;

	// label com mensagem de erro
	@FXML
	private Label labelErrorEmail;

	// label com mensagem de erro
	@FXML
	private Label labelErrorBirthDate;

	// label com mensagem de erro
	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	// lista departamento do banco de dados com observableList
	private ObservableList<Department> obsLista;

	// metodo para implemtar set do entity criado em cima do department
	// criou a instacia contrutor do departamento no controlador
	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	// metodoset do Services injeta 2 dependencia services do seller e department
	// (construtores)
	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	// metodo para inscrever na lista atualizada listener
	// todo objeto que implementar a interface DataChangeListener pode increver para
	// receber este evento que atualiza depois de salvar,alterar ou excluir
	// se salvar com sucesso no botao salvar alterar la
	public void subscribeDataChangeListener(DataChangeListener listeners) {
		dataChangeListeners.add(listeners);
	}

	// metodos dos eventos do botoes
	// salvar departamento no banco de dados
	// passar o parametro da janela atual para ser fechada depois que salvar
	@FXML
	public void onBtSaveAction(ActionEvent event) {

		// se entity estiver valendo nullo
		if (service == null) {
			throw new IllegalStateException("Service está nulo");
		}
		// se o service estiver nullo
		if (entity == null) {
			throw new IllegalStateException("Entidade está nula");
		}
		// colocar todas operações com o banco dentro do try
		try {
			// getformdata metodo que criei para pegar dados nas caixinhas e instanciar
			// departamento
			entity = getFormData();
			// chama o metodo saveorUpdate em SellerService para salvar no banco
			service.saveOrUpdate(entity);
			// se salvar com sucesso chamar o metodo para atualizar lista
			notifyDataChangeListerners();
			// depois que salvar fechar a janela atual pegando o paramentro da janela atual
			Utils.currentStage(event).close();
		}
		// tratar o validationException
		catch (ValidationException e) {
			// chama o metodo abaixo setErrorMessage que escreve o erro no label da tela
			// passando e.getErros que é nome do meu map que vai ser aquela coleção de erros
			// na classe ValidtionException
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro em salvar", null, e.getMessage(), AlertType.ERROR);
		}
	}

	// executar o metodo onDataChanged na interface DataChangeListener para
	// atualizar
	// o listeners emite o metodo o observer na claase SellerList irá escutar o
	// metodo Listener
	private void notifyDataChangeListerners() {
		// para cada datachangeListeners pertencente a minha lista dataChangeListeners
		// vou fazer
		// um listener.onDataChanged
		for (DataChangeListener listener : dataChangeListeners) {
			// este metodo estara implementado em departmenteListController
			// ele atualiza os dados da tabela
			listener.onDataChanged();
		}
	}

	// pega os dados do formulario e instancia em departamento
	private Seller getFormData() {
		Seller obj = new Seller();

		// verificar se tem erro primeiro instanciar o validationException
		ValidationException exception = new ValidationException("Validação de erro");
		// utils.tryParseToInt criei metodo para converter para inteiro
		obj.setId((Utils.tryParseToInt(txtId.getText())));

		// verificar se txt name esta vazio
				// se igual a null ou igual a string vazio equals
				if (txtName.getText() == null || txtName.getText().trim().equals("")) {
					// adicona erro na exceçaõ para guardar no map na classe ValidationException
					// exeption é o nome da instancia que fiz acima
					// adicona o nome do campo e a mensagem
					exception.addError("name", "O campo não pode ser vazio");
				}
				obj.setName(txtName.getText());

				
				
				// verificar se txt email esta vazio
				// se igual a null ou igual a string vazio equals
				if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
					// adicona erro na exceçaõ para guardar no map na classe ValidationException
					// exeption é o nome da instancia que fiz acima
					// adicona o nome do campo e a mensagem
					exception.addError("email", "O campo não pode ser vazio");
				}
				obj.setEmail(txtEmail.getText());
				
				//se o valor do datepicker for nulo significa que nao foi selecionado..gerar exceção
				if(dpBirthDate.getValue()==null) {
					exception.addError("birthDate", "O campo não pode ser vazio");
				}
				else {
				//para pegar o valor do datepicker
				//instant recebe o conteudo do datepicker
				//atStartOfDay converte a data escolhida no horario do computadpr do usuario
				Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
				//o meu obejto espera um tipo date converte instant para date date.from(instant)
				obj.setBirthDate(Date.from(instant));
				}
				
				
				//para pegar salario
				
				// verificar se txtBaseSalary esta vazio
				// se igual a null ou igual a string vazio equals
				if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
					// adicona erro na exceçaõ para guardar no map na classe ValidationException
					// exeption é o nome da instancia que fiz acima
					// adicona o nome do campo e a mensagem
					exception.addError("baseSalary", "O campo não pode ser vazio");
				}
				//chama o metodo em utils chamado  tryparseToDouble
				obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
				
				//associar o departamento, pega o valor da combobox e joga pro meu obj
				obj.setDepartment(comboBoxDepartment.getValue());
				
		// depois que passar disso mesmo vazio eu vou setar
		// se existir algum erro eu lanço a minha exceção
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
	}

	// para cancelar fechando a janela passar evento da janela atual
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// chama o método abaixo assim que inicilizar o formulario
		inicializeNodes();

	}

	// metodos para tratar inicio das caixinhas de texto
	private void inicializeNodes() {
		// só aceita inteiros na caixa ID
		Constraints.setTextFieldInteger(txtId);
		// só aceita 30 caracteres no nome do departamento
		Constraints.setTextFieldMaxLength(txtName, 30);
		// so aceita double na caixinha
		Constraints.setTextFieldDouble(txtBaseSalary);
		// tamanho maximo para email
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		// formato para data no DatePicker
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		//chama o metodo para iniciar o combobox com os departamentos
		initializeComboBoxDepartment();
	}

	// pega os dados do vendedor e joga no formulário
	public void UpdateFormData() {
		// testar se o entity esta nulo lança excecao
		if ((entity == null)) {
			throw new IllegalStateException("Entidade está nula");
		}
		// valueof converte o id para inteiro pois a caixinha de texto é string
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		// converter para string o salario para jogar no campo antes chama locale para
		// por ponto em vez de virgula
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		// mostra a data local baseado no computador dele usando o LocalDate no instante
		// que é ofInstant
		// .toinstant convert a data para instante
		// ZoneId é o fusohorario do computador local do usuario
		// if so converter data se ela não for nula
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		
		//preenche a combo com o departamento que está associado com o vendedor e mostra na tela
		//se o departamento é nulo este vendedor é novo
		if((entity.getDepartment() == null)) {
		// combobox esteja selecionado no primeiro elemento dele
			comboBoxDepartment.getSelectionModel().selectFirst();
		}
		//se nao for nulo eu seleciono na comboeste departamento que esta associado ao vendedor
		else {
			comboBoxDepartment.setValue(entity.getDepartment());	
		}
	}	

	// metodo chama o departamentservices e carrega os departamento do banco de
	// dados preenchendo a nossa lista com esses departamentos
	public void CarregarObjetosAssociados() {
		//se o departmentSevice estiver nulo lança exceção com uma msg pro usuario
		if(departmentService == null) {
			throw new IllegalStateException("DeparmentService estava nulo");
		}
		// carregamos os departamentos do banco de dados para a lista
		//findall busca os departamentos
		List<Department> list = departmentService.findAll();
		
		//jogar os departamento para observablelist
		//obslista é o nome da lista que criamos la em cima
		//FXCollection joga para obslista
		obsLista = FXCollections.observableArrayList(list);
		
		//seta lista com a lista do combobox
		comboBoxDepartment.setItems(obsLista);
	}

	// metodo para escrever no label na tela se houve algum erro
	// percorre o map com os erros
	private void setErrorMessages(Map<String, String> errors) {
		// uma coleçao recebe os erros
		Set<String> fields = errors.keySet();

		// testar se contem o valor name, se existir eu pego o label na tela e escrevo o
		// texto dele com a mensagem de erro
		// setar o label
		/*trocar pelo comando valido abaixo if (fields.contains("name")) {
			// joga para o label de erros la na tela
			labelErrorName.setText(errors.get("name"));
		}
		//apaga a label erro na tela 
		else {
			labelErrorName.setText("");
		}*/
		//trocar por
		//operador ternario se for verdadeiro coloca o errors.get("name") se for falso a label de erro recebe vazio""
		//quando for cadastradar um novo vendedor e esquecer de algum campo ele vai apresentar uma label de campo vazio
		//quando vc preencher alguns campos e deixarf outros vazios ele vai apagara alabel de rro que vc preencheu e deixar a label de rro somente do campo que falta preencher
		labelErrorName.setText((fields.contains("name")? errors.get("name") : ""));
		labelErrorEmail.setText((fields.contains("email")? errors.get("email") : ""));
		labelErrorBirthDate.setText((fields.contains("birthDate")? errors.get("birthDate") : ""));
		labelErrorBaseSalary.setText((fields.contains("baseSalary")? errors.get("baseSalary") : ""));
		

	}
	
	//inicializa o combobox com os departamentos
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
