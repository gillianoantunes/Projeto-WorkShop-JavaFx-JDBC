package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{
	
	//cria varivel do tipoDepartmentService para chamar o método findALL
	private DepartmentService servico;
	
	 
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	private ObservableList< Department> obsLista;
	
	//clique do botao
	@FXML
	public void onBTNewAction(ActionEvent event) {
		//chama a função em utils que pega a janela atual o stage atual
		Stage parentStage = Utils.cuurentStage(event);
		//instanciar departamento vazio e injetar no controlador
		Department obj = new Department();
		//chama a função createDialogForm que cria o formulario de cadastro de departamento
		// o segundo argumento é a janela pai
		createDialogForm(obj, "/gui/DepartmentForm.fxml",parentStage);
	}
	public void setDepartmentService(DepartmentService servico) {
		this.servico = servico;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//criar um metodo para iniciar
		IniciarNode();
		
	}

	private void IniciarNode() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		//ajustar a tableview até o fim da janela
		Stage stage = (Stage) Main.getmainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	
	//se for igual nulo o serviço
	public void updateTableView() {
		if(servico == null) {
			throw new IllegalStateException("Serviço estava nulo");
		}
		
		//lista receb os dados
		List<Department> lista = servico.findAll();
		//obs recebe a lista
		obsLista = FXCollections.observableArrayList(lista);
		//joga resultado na tableview
		tableViewDepartment.setItems(obsLista);
	}

	//cria a janela do formulario para preencher novo departamento
	//essa função vai ser chamada no botao new da tela de departamento
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			//para carregar FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			//pega o controlador da tela que acabei de carregar
			DepartmentFormController controller = loader.getController();
			//injetar o departamento nesse controlador
			controller.setDepartment(obj);
			//injetar o DepartamentService
			controller.setDepartmentService(new DepartmentService());
			//chamar o metodo update para carregar os dados no formulario
			controller.UpdateFormData();
			
			
			//instanciar janela na frente da outra, palco na frente do outro
			Stage dialogStage = new Stage();
			//configuar titulo
			dialogStage.setTitle("Entre com os dados do departamento");
			//criar uma nova cena com pane
			dialogStage.setScene(new Scene(pane));
			//se a janela pode ou não ser dimensionada, no caso nao pode false
			dialogStage.setResizable(false);
			//quem é o stage pai dessa janela no caso o parentSatge que foi passado no parametro
			dialogStage.initOwner(parentStage);
			//enquanto você não fechar a janela você não pode acessar a de trás
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		}
		catch(IOException e) {
			Alerts.showAlert("IoException","Erro de carregamento", e.getMessage(), AlertType.ERROR);
		}
		
	}
}
