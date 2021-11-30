package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

//controller implementando inicializable pr padrao nome do controller fica nome da view seguido da palvra controller
public class MainViewController implements Initializable {

	// atributos
	@FXML
	private MenuItem menuItemVendedor;

	@FXML
	private MenuItem menuItemDepartamento;

	@FXML
	private MenuItem menuItemSobre;

	// metodos
	@FXML
	public void onMenuItemVendedor() {
		//carrega a tela passando expresao lambda como parametro controller
				loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
					//injeta dependencia do service no controller
					controller.setSellerService(new SellerService());
					//atualiza os dados
					controller.updateTableView();
					});
	}

	@FXML
	public void onMenuItemDepartamento() {
		//carrega a tela passando expresao lambda como parametro controller
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			//injeta dependencia do service no controller
			controller.setDepartmentService(new DepartmentService());
			//atualiza os dados
			controller.updateTableView();
			});
	}

	@FXML
	public void onMenuItemSobre() {
		//chama o metodo loadView abaixo passando o caminho da view como parametro e expressão que nao leva em nada
		loadView("/gui/Sobre.fxml", x -> {});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

	// carregar uma outra tela que recebe o nome da view caminho completo
	//synchronized serve para garantir que este método ocorra sem ser interropido
	//pois aplicação grafica é multithread, tem varias threads sendo executadas 
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			//para carregar FXMLLoader
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			//fazer o objeto vbox
			VBox newVBox = loader.load();
			
			//chama o metodo getmainScene em main.java
			Scene mainScene = Main.getmainScene();
			//vou adicionar a tela sobre dentro da janela main 
			//no arquivo xml da tela vou adiconar dentro dos childrens a tela sobre
			//crio uma tela chamada mainVbox e getroot pega o primeiro elemento que no caso é o scrollpane
			//faço casting pro compilador entender que estou pegando um scrollPane
			VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			//receb os filhos do vbox
			Node mainMenu =mainVbox.getChildren().get(0);
			//limpa todos filhos do mainVbox
			mainVbox.getChildren().clear();
			//adiconar o main menu
			mainVbox.getChildren().add(mainMenu);
			//adiciona os filhos do newVbox
			mainVbox.getChildren().addAll(newVBox.getChildren());
			
			//essas 2 linha executa a funçao que passou por argumento a expressao lambda
			T controller = loader.getController();
			initializingAction.accept(controller);
		}
		catch(IOException e) {
			Alerts.showAlert("IO Exception", "Erro de carregamento da View", e.getMessage(), AlertType.ERROR);
		}
	}

}
