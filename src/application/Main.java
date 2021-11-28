package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	//atributo para guardar nesse atributo para chamar tela
	private static Scene mainScene;
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			//instanciou o loader passando o caminho da view para carregar
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			//chama o loader do scrollPane que esta no Scene Builder
			ScrollPane scrollPane = loader.load();
			
			//faz com que scrollPane fique ajustado ao tamanho da janela
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane);
			primaryStage.setScene(mainScene);
			//titulo da janela palco
			primaryStage.setTitle("Sample JavaFX application");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//criar metodo para retronar mainScene
	public static Scene getmainScene() {
		return mainScene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
