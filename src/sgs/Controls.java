package sgs;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Controls {
	private static Stage window;
	
	public static void display(Stage stage) {
		window = new Stage();
		
		StackPane layout = new StackPane();
	
		Scene scene = new Scene(layout, 700, 460);
		
		scene
		.getStylesheets()
		.add(MainSGS.class
				.getResource("controlsScene.css")
				.toExternalForm());
		
		window.getIcons().add(new Image(Controls.class.getResourceAsStream("icone-progetto/SGS-icon.png")));
		window.setResizable(false);
		window.setScene(scene);
		window.initOwner(stage);
		window.setTitle("Controls");
		window.show();
	}
	
	public static void close()
	{
		window.close();
	}
	
}