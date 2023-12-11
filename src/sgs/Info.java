package sgs;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Info {

	public static void display(Stage stage)
	{
		Stage window = new Stage();
		// BorderPane
		BorderPane border = new BorderPane();
		border.setPadding(new Insets(10));
		
		// GridPane
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setVgap(8);
		grid.setHgap(20);
		grid.setAlignment(Pos.CENTER);
		border.setCenter(grid);
		
		int x = 0;
		int y = 0;
		int k = 0;
		
		for (String p : Database.getPersonalities())
		{
			Button button = new Button();
			button.setId(p);
			button.setOnAction(e -> details(p, window));
			Text textPers;
			
			Text text = new Text(p);
			text.setId("TEXT");
	
			if (k == 8)
			{
				x += 2;
				y = 0;
			}
			switch(k)
			{
				case 0: 
					textPers = new Text("Analists");
					textPers.setId("Analists");
					GridPane.setConstraints(textPers, x, y);
					grid.getChildren().add(textPers);					
					++y;
					break;
				case 4: 
					textPers = new Text("Diplomats");
					textPers.setId("Diplomats");
					GridPane.setConstraints(textPers, x, y);
					grid.getChildren().add(textPers);
					++y;
					break;
				case 8: 
					textPers = new Text("Sentinels");
					textPers.setId("Sentinels");
					GridPane.setConstraints(textPers, x, y);
					grid.getChildren().add(textPers);
					++y;
					break;
				case 12: 
					textPers = new Text("Explorers");
					textPers.setId("Explorers");
					GridPane.setConstraints(textPers, x, y);
					grid.getChildren().add(textPers);
					++y;
			}
			HBox hbox = new HBox(10);
			hbox.getChildren().addAll(button, text);
			GridPane.setConstraints(hbox, x, y);
			y++;
			grid.getChildren().add(hbox);
			k++;
		}
		
		// HyperLink
		Button link = new Button("16personalities.com");
		link.setId("LINK");
		link.setOnAction(e -> {
				try {
					Desktop.getDesktop().browse(new URI("https://www.16personalities.com"));
				} catch (IOException E1) {
					
				} catch (URISyntaxException E2) {
					
				}
			});
		
		StackPane stack = new StackPane();
		stack.getChildren().add(link);
		stack.setAlignment(Pos.BOTTOM_RIGHT);
		border.setBottom(stack);
		
		// Scena
		Scene scene = new Scene(border, 500, 500);
		scene
			.getStylesheets()
			.add(MainSGS.class
					.getResource("info.css")
					.toExternalForm());
		
		// Stage
		window.initOwner(stage);
		window.setResizable(false);
		window.getIcons().add(new Image(Info.class.getResourceAsStream("icone-progetto/SGS-icon.png")));
		window.setScene(scene);
		window.setTitle("Info");
		window.show();
	}
	
	public static void details(String personality, Stage stage)
	{
		// Title
		Text title = new Text(Database.getPersonalityName(personality) + "(" + personality + ")");
		title.setId("TITLE" + Database.getGroup(personality));
		GridPane.setConstraints(title, 1, 0);
		
		// Text
		Text description = new Text(Database.getDescription(personality));
		description.setId("TEXT");
		GridPane.setConstraints(description, 1, 1);
		
		// Image
		Button img = new Button();
		img.setMinSize(200, 200);
		img.setId(personality + "i");
		GridPane.setConstraints(img, 0, 1);
		
		// GridPane
		GridPane layout = new GridPane();
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);
		layout.setHgap(8);
		layout.getChildren().addAll(title, description, img);
		
		// Scene
		Scene scene = new Scene(layout, 700, 400);
		scene
		.getStylesheets()
		.add(MainSGS.class
				.getResource("details.css")
				.toExternalForm());
		description.wrappingWidthProperty().bind(scene.widthProperty().subtract(250));
		
		// Stage
		Stage window = new Stage();
		window.getIcons().add(new Image(Info.class.getResourceAsStream("icone-progetto/SGS-icon.png")));
		window.setScene(scene);
		window.setResizable(false);
		window.setTitle(personality + "'s details");
		window.initOwner(stage);
		window.show();
	}
}
