package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

//retorna o stage atual , o palco atual
public class Utils {

	// funçaõ que retorna o palco atual que recebe como parametro o evento que o
	// botao recebeu
	public static Stage cuurentStage(ActionEvent event) {
		// getSouce pega tipo object generico , colocar downcasting na forma de node
		// pega a cena e depois pega a janela window
		// converter para stage
		return (Stage) ((Node) event.getSource()).getScene().getWindow();

	}

//metodo para ajudar o valor da caixinha para inteiro
	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		}
		// se houver excecçao retorna nada
		catch (NumberFormatException e) {
			return null;
		}
	}
}