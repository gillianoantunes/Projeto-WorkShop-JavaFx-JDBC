package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	public void onBTNewAction() {
		System.out.println("botão");
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

}
