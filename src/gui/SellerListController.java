package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	// cria varivel do tipoSellerService para chamar o método findALL
	private SellerService servico;

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnName;

	@FXML
	private TableColumn<Seller, String> tableColumnEmail;

	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;

	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;

	// criou uma coluna para criar em cada linha um botão para editar e atualizar
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	// criou uma coluna para criar em cada linha um botão para deletar
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Seller> obsLista;

	// clique do botao
	@FXML
	public void onBTNewAction(ActionEvent event) {
		// chama a função em utils que pega a janela atual o stage atual
		Stage parentStage = Utils.currentStage(event);
		// instanciar departamento vazio e injetar no controlador
		Seller obj = new Seller();
		// chama a função createDialogForm que cria o formulario de cadastro de
		// departamento
		// o segundo argumento é a janela pai
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	public void setSellerService(SellerService servico) {
		this.servico = servico;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// criar um metodo para iniciar
		IniciarNode();

	}

	// executa quando inicializa a tela
	private void IniciarNode() {
		// no final é o mesmo nome do atributo na classe Seller no model.entities
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		// para formatar data chamar o metodo na classe Utils.java passando parametro o
		// nome da table e o formato
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		// formatar ponto flutuante com 2 casas decimais passando a table e o numero de
		// casas decimais
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

		// ajustar a tableview até o fim da janela
		Stage stage = (Stage) Main.getmainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());

	}

	// se for igual nulo o serviço
	// funçao que atualiza os dados da tabela tableview
	public void updateTableView() {
		if (servico == null) {
			throw new IllegalStateException("Serviço estava nulo");
		}

		// lista receb os dados
		List<Seller> lista = servico.findAll();
		// obs recebe a lista
		obsLista = FXCollections.observableArrayList(lista);
		// joga resultado na tableview
		tableViewSeller.setItems(obsLista);
		// chama o método la em baixo initEditButtons que acrescenta um novo botão com o
		// texto edit em cada linha da tabela
		// cada botao quando você clicar ele abre o formulario de dição usando
		// createDialogForm
		initEditButtons();
		// chama o método initRemoveButtons abaixo para por botões na tela
		initRemoveButtons();
	}

	// cria a janela do formulario para preencher novo departamento
	// essa função vai ser chamada no botao new da tela de departamento
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		try {
			// para carregar FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// pega o controlador da tela que acabei de carregar
			SellerFormController controller = loader.getController();
			// injetar o departamento nesse controlador
			controller.setSeller(obj);
			// injetar o DepartamentService
			controller.setServices(new SellerService(), new DepartmentService());
			
			// chama o metodo para carregar os departamentos do banco de dados para o controller a combobox
			controller.CarregarObjetosAssociados();
			
			// increver este objeto SellerListController para ser um listenner daquele
			// evento
			// quando alterar ele escuta aquele evento que atualizou eu me inscrevo com
			// this, este objeto
			// quando este medodosubscribe na classe SellerFormController for disparado
			// eu me inscrevo
			controller.subscribeDataChangeListener(this);

			// chamar o metodo update para carregar os dados no formulario
			controller.UpdateFormData();

			// instanciar janela na frente da outra, palco na frente do outro
			Stage dialogStage = new Stage();
			// configuar titulo
			dialogStage.setTitle("Entre com os dados do departamento");
			// criar uma nova cena com pane
			dialogStage.setScene(new Scene(pane));
			// se a janela pode ou não ser dimensionada, no caso nao pode false
			dialogStage.setResizable(false);
			// quem é o stage pai dessa janela no caso o parentSatge que foi passado no
			// parametro
			dialogStage.initOwner(parentStage);
			// enquanto você não fechar a janela você não pode acessar a de trás
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			//aparace as mensagens de erro  que aparacer
			e.printStackTrace();
			Alerts.showAlert("IoException", "Erro de carregamento", e.getMessage(), AlertType.ERROR);
		}
	}

	// quando os dados forem alterados chamara este metodo e chama a minha funcao
	// updateTableView que já tenho acima
	// essa funçao atualiza os dados da tableview
	@Override
	public void onDataChanged() {
		updateTableView();
	}

	// instancia os botoes e cria a janela do formulário com os dados ja completados
	// para edição
	// método initEditButtons que acrescenta um novo botão com o texto edit em cada
	// linha da tabela
	// cada botao quando você clicar ele abre o formulario de ediçao usando
	// createDialogForm
	// ja abre com os campos preenchidos
	private void initEditButtons() {

		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	// metodo igual edit coloca um botao remove em cada linha da mesma forma do edit
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	// para remover mostrar um alert
	private void removeEntity(Seller obj) {
		// o resultado do alert sim ou nao eu atribuo na variavel optional do tipo
		// ButtonType com nome result
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que deseja deletar?");

		// se o usuario apertar no ok confirma a deleção mas pode dar exceção testar se
		// igual nulo o service
		if (result.get() == ButtonType.OK) {
			if (servico == null) {
				throw new IllegalStateException("Serviço está nulo");
			}
			// remover dentro do try
			try {
				// deleta
				servico.remove(obj);
				// depois que remoce chama o update para atualizar os dados da tabela
				updateTableView();
			}
			// é a exceção que uso no metodo deleteById na classe SellerDaoJDBC
			catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
