package gui;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.IllegalSelectorException;
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
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	// cria varivel do tipoDepartmentService para chamar o m�todo findALL
	private DepartmentService servico;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	// criou uma coluna para criar em cada linha um bot�o para editar e atualizar
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;

	// criou uma coluna para criar em cada linha um bot�o para deletar
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsLista;

	// clique do botao
	@FXML
	public void onBTNewAction(ActionEvent event) {
		// chama a fun��o em utils que pega a janela atual o stage atual
		Stage parentStage = Utils.currentStage(event);
		// instanciar departamento vazio e injetar no controlador
		Department obj = new Department();
		// chama a fun��o createDialogForm que cria o formulario de cadastro de
		// departamento
		// o segundo argumento � a janela pai
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	public void setDepartmentService(DepartmentService servico) {
		this.servico = servico;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// criar um metodo para iniciar
		IniciarNode();

	}

	private void IniciarNode() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// ajustar a tableview at� o fim da janela
		Stage stage = (Stage) Main.getmainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());

	}

	// se for igual nulo o servi�o
	// fun�ao que atualiza os dados da tabela tableview
	public void updateTableView() {
		if (servico == null) {
			throw new IllegalStateException("Servi�o estava nulo");
		}

		// lista receb os dados
		List<Department> lista = servico.findAll();
		// obs recebe a lista
		obsLista = FXCollections.observableArrayList(lista);
		// joga resultado na tableview
		tableViewDepartment.setItems(obsLista);
		// chama o m�todo la em baixo initEditButtons que acrescenta um novo bot�o com o
		// texto edit em cada linha da tabela
		// cada botao quando voc� clicar ele abre o formulario de di��o usando
		// createDialogForm
		initEditButtons();
		// chama o m�todo initRemoveButtons abaixo para por bot�es na tela
		initRemoveButtons();
	}

	// cria a janela do formulario para preencher novo departamento
	// essa fun��o vai ser chamada no botao new da tela de departamento
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			// para carregar FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// pega o controlador da tela que acabei de carregar
			DepartmentFormController controller = loader.getController();
			// injetar o departamento nesse controlador
			controller.setDepartment(obj);
			// injetar o DepartamentService
			controller.setDepartmentService(new DepartmentService());

			// increver este objeto DepartmentListController para ser um listenner daquele
			// evento
			// quando alterar ele escuta aquele evento que atualizou eu me inscrevo com
			// this, este objeto
			// quando este medodosubscribe na classe DepartmentFormController for disparado
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
			// se a janela pode ou n�o ser dimensionada, no caso nao pode false
			dialogStage.setResizable(false);
			// quem � o stage pai dessa janela no caso o parentSatge que foi passado no
			// parametro
			dialogStage.initOwner(parentStage);
			// enquanto voc� n�o fechar a janela voc� n�o pode acessar a de tr�s
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IoException", "Erro de carregamento", e.getMessage(), AlertType.ERROR);
		}
	}

	// quando os dados forem alterados chamara este metodo e chama a minha funcao
	// updateTableView que j� tenho acima
	// essa fun�ao atualiza os dados da tableview
	@Override
	public void onDataChanged() {
		updateTableView();
	}

	// instancia os botoes e cria a janela do formul�rio com os dados ja completados
	// para edi��o
	// m�todo initEditButtons que acrescenta um novo bot�o com o texto edit em cada
	// linha da tabela
	// cada botao quando voc� clicar ele abre o formulario de edi�ao usando
	// createDialogForm
	// ja abre com os campos preenchidos
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	// metodo igual edit coloca um botao remove em cada linha da mesma forma do edit
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
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
	private void removeEntity(Department obj) {
		// o resultado do alert sim ou nao eu atribuo na variavel optional do tipo
		// ButtonType com nome result
		Optional<ButtonType> result = Alerts.showConfirmation("Confirma��o", "Tem certeza que deseja deletar?");

		// se o usuario apertar no ok confirma a dele��o mas pode dar exce��o testar se
		// igual nulo o service
		if (result.get() == ButtonType.OK) {
			if (servico == null) {
				throw new IllegalStateException("Servi�o est� nulo");
			}
			// remover dentro do try
			try {
				// deleta
				servico.remove(obj);
				// depois que remoce chama o update para atualizar os dados da tabela
				updateTableView();
			}
			// � a exce��o que uso no metodo deleteById na classe DepartmentDaoJDBC
			catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
