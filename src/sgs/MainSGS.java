package sgs;

import java.io.File;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


public class MainSGS extends Application
{

	private Scene gameScene, startingScene, settingsScene;
	private double     scale;
	private ScrollPane scroll;
	private Stage      stage;
	private GameField  field;
	private boolean    playing;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	public void start(Stage primaryStage)
	{
		stage = primaryStage;
		Database.readFiles();
		// SETTINGS SCENE //////////////////////////		
			// Label - length
		Text lengthText = new Text("Length:");
		lengthText.setId("Text");
		GridPane.setConstraints(lengthText, 0, 0);
			// TextField - length
		TextField lengthInput = new TextField("");
		lengthInput.setPromptText("Max: 120");
		lengthInput.setId("TextField");
		GridPane.setConstraints(lengthInput, 1, 0);
		
			// Label - height
		Text heightText = new Text("Height:");
		heightText.setId("Text");
		GridPane.setConstraints(heightText, 0, 1);
			// TextField - height
		TextField heightInput = new TextField("");
		heightInput.setPromptText("Max: 80");
		heightInput.setId("TextField");
		heightInput.setPrefWidth(106);
		GridPane.setConstraints(heightInput, 1, 1);
		
		// Text - 
		Text dimensionText = new Text("Set Field's Area");
		dimensionText.setId("Text2");
		
			// Layout - GridPane
		GridPane grid = new GridPane();
		grid.setId("GridPane");
		grid.setHgap(10);
		grid.setVgap(20);
		grid.setAlignment(Pos.CENTER);
		grid.getChildren().addAll(lengthText, lengthInput,
								  heightText, heightInput);
		
			// Layout - VBox
		VBox layout2 = new VBox(20);
		layout2.setAlignment(Pos.CENTER);
		layout2.getChildren().add(dimensionText);
		
			// Layout - BorderPane 
		BorderPane layout = new BorderPane();
		layout.setId("BorderPane");
		layout.setCenter(layout2);
		
		// ChoiceBox - dimensions
		Button createBtn = new Button();
		ChoiceBox<String> box = new ChoiceBox<>();
		createBtn.setOnAction(e -> 
		{ 
			getChoice(box.getValue(), lengthInput.getText(), heightInput.getText(), lengthInput, heightInput); 
		});
		createBtn.setText("Create");
		layout2.getChildren().add(box);
		layout2.getChildren().add(createBtn);
		box.setId("ChoiceBox");
		box.getItems().addAll("Custom", "Small", "Medium", "Large");
		box.getSelectionModel().select(2);
		box.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
				if (newValue == "Custom")
				{
					layout2.getChildren().remove(createBtn);
					layout2.getChildren().add(grid);
					layout2.getChildren().add(createBtn);
					
				} else if (oldValue == "Custom") {
					layout2.getChildren().remove(grid);
					lengthInput.setText("");
					heightInput.setText("");
				}
			});
		
			// Scene
		settingsScene = new Scene(layout, 600, 500);
		settingsScene.addEventHandler(KeyEvent.KEY_PRESSED, (key) ->
	        {
	        	if (key.getCode() == KeyCode.ENTER)
	        		getChoice(box.getValue(), lengthInput.getText(), heightInput.getText(), lengthInput, heightInput);
	        });
		settingsScene.getStylesheets().add(MainSGS.class.getResource("settingsScene.css").toExternalForm());
		
		// STARTING SCENE //////////////////////////
		
		// Button - begin button
		Button beginButton = new Button("Begin");
		beginButton.setId("beginButton");
		beginButton.setOnAction(e -> {
				stage.setScene(settingsScene);
			});
		
			// Button - info button
		Button infoButton = new Button();
		infoButton.setId("infoButton");
		infoButton.setMaxSize(25, 25); infoButton.setMinSize(25, 25);
		infoButton.setOnAction(e -> {
				Info.display(stage);
			});
		infoButton.addEventHandler(KeyEvent.KEY_PRESSED, (key) ->
        {
        	if (key.getCode() == KeyCode.ENTER)
        		beginButton.fire();
        });
		
			// Image - logo
		Button logo = new Button();
		logo.setId("LOGO");
		logo.setMaxSize(300, 300); logo.setMinSize(300, 300);
		
			// Layout - VBox
		VBox vbox = new VBox(10);
		vbox.setId("VBox");
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(logo, beginButton);
		
			// Layout - StackPane
		StackPane stack = new StackPane();
		stack.setId("StackPane");
		stack.setAlignment(Pos.BOTTOM_RIGHT);
		stack.getChildren().add(infoButton);
		
			// Layout - BorderPane
		BorderPane border = new BorderPane();
		border.setId("BorderPane");
		border.setCenter(vbox);
		border.setBottom(stack);
		
			// Scene - starting scene
		startingScene = new Scene(border, 600, 500);
		logo
			.addEventHandler(KeyEvent.KEY_PRESSED, (key) ->
	        {
	        	if (key.getCode() == KeyCode.ENTER)
	        		beginButton.fire();
	        });
		startingScene.getStylesheets().add(MainSGS.class.getResource("startingScene.css").toExternalForm());
		
		stage.setTitle("S.G.S.");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icone-progetto/SGS-icon.png")));
		stage.setScene(startingScene);
		stage.setResizable(false);
		stage.show();
	}

	/**
	 * Dona alla scena l'abililt‡ di zoomare premendo '-' e '+'
	 * @param field
	 */
	private void setZoom()
	{
		gameScene.setOnKeyPressed(e -> {
	        	if (e.getCode() == KeyCode.MINUS) 
	        	{
	        		scale /= 1.1;
	        		field.setScaleX(scale);
	        		field.setScaleY(scale);
	        	}
	        	
	        	else if (e.getCode() == KeyCode.PLUS)
	        	{
	        		scale *= 1.1;
	        		field.setScaleX(scale);
	        		field.setScaleY(scale);		
	        	}
			});
	}
	
	/**
	 * Interpreta la scelta selezionata del ChoiceBox
	 * @param s
	 * @param length
	 * @param height
	 */
	private void getChoice(String s, String length, String height, TextField lengthInput, TextField heightInput)
	{
		switch (s)
		{
		case "Custom":
			//
			try {
				if (!length.equals("") && !height.equals(""))
				{
					int x = Integer.parseInt(length);
					int y = Integer.parseInt(height);
					if (x <= 120 && y <= 80) setGameScene(x, y);
					if (x > 120) lengthInput.setText("Too much!");
					if (y > 80) heightInput.setText("Too much!");
					
				} else {
					if (length.equals("")) lengthInput.setText("Missing data!");
					if (height.equals("")) heightInput.setText("Missing data!");
				}
			} catch (NumberFormatException E) {
				try {
					Integer.parseInt(length);
				}
				catch (NumberFormatException E2) { lengthInput.setText("Wrong data!"); }
				try {
					Integer.parseInt(height);
				}
				catch (NumberFormatException E2) { heightInput.setText("Wrong data!"); }
			}
			break;
			
		case "Small":
			
			setGameScene(40, 30); // 1200
			break;
		case "Medium":
			
			setGameScene(70, 60); // 4200
			break;
		case "Large":
			setGameScene(120, 80); // 9.600
			break;
		}
	}

	/**
	 * Imposta la scena dove si svolge il gioco
	 * @param x
	 * @param y
	 */
	public void setGameScene(int x, int y)
	{
			// GameField
		field = new GameField(x, y, stage);	
		
		String path = ("SGS-OST.mp3");
        Media media = new Media(new File(path).toURI().toString());  
        MediaPlayer player = new MediaPlayer(media);
        player.setOnEndOfMedia(new Runnable()
    		{
        		@Override
        		public void run()
        		{
        			player.seek(Duration.ZERO);
        		}
    		});
        player.play();
        playing = true;
			// ComboBox - box
		ComboBox<String> box = new ComboBox<>();
		box.setPromptText("Generator");
		box.getItems().add("Random");
		box.setOnAction(e -> {
				field.setGenerator(box.getValue());
			});
		for (String p : Database.getPersonalities())
			box.getItems().add(p);
		
		TextField textCells = new TextField();
		textCells.setPromptText("Cells number");
		textCells.setPrefWidth(30);
		textCells.setText("100");
		textCells.addEventHandler(KeyEvent.KEY_PRESSED, (key) ->
        {
        	if (key.getCode() == KeyCode.ENTER)
        	{
        	int n;
    		try
    		{
    			n = Integer.parseInt(textCells.getText());
    			if (n <= 300) field.randomSpawn(n);
    			else textCells.setText("Max 300!");
    		}
    		catch(NumberFormatException e) { textCells.setText("Wrong data!");}
        }
        });
		
		Button clearBtn = new Button("Clear");
		clearBtn.setId("CLEAR");
		clearBtn.setOnAction(e -> field.killAll());
		
			// Button - go back
		Button backButton = new Button("Go Back");
		backButton.setId("menuButton");
		backButton.setOnAction(e -> { player.stop(); stage.setMaximized(false); stage.setScene(settingsScene); 
									  stage.setResizable(false); stage.centerOnScreen();});
		
			// Button - info button
		Button infoButton = new Button();
		infoButton.setId("infoButton");
		infoButton.setMaxSize(25, 25); infoButton.setMinSize(25, 25);
		infoButton.setOnAction(e -> {
				Info.display(stage);
			});
		
			// Layout - StackPane
		StackPane stack = new StackPane();
		stack.setPadding(new Insets(3));
		stack.setAlignment(Pos.CENTER_LEFT);
		stack.getChildren().add(infoButton);
		
			// Layout - ScrollPane
        scroll = new ScrollPane();
        //scroll.setId("ScrollPane");
		scroll.setContent(field);
		scroll.setVvalue(0.5); 
		scroll.setHvalue(0.5); 
		scroll.getStylesheets().add(MainSGS.class.getResource("darkScroll.css").toExternalForm());
		
			// Layout - VBox
		VBox vbox = new VBox(20);
		vbox.setId("VBox");
		vbox.getChildren().addAll(box);
		vbox.setAlignment(Pos.TOP_CENTER);
		
			// Layout - BorderPane 
		BorderPane borderLayout = new BorderPane();
		borderLayout.setPadding(new Insets(10, 10, 10, 10));
		borderLayout.setCenter(scroll);
		borderLayout.setLeft(vbox);
		borderLayout.setBottom(stack);
		
			// Timeline
		Timeline anim = new Timeline(new KeyFrame(new Duration(400), e -> field.next()));
    	anim.setCycleCount(Timeline.INDEFINITE);
    	
    	// TextCells
    	vbox.getChildren().add(textCells);
    	
    		//  Button - Start
		Button startButton = new Button("Start");
		startButton.setId("menuButton");
		startButton.setOnAction(e -> {
				anim.play();
	    	    borderLayout.getChildren().remove(vbox);
			});
		vbox.getChildren().add(startButton);
		
			// Button - Next
		Button nextButton = new Button("Next");
		nextButton.setId("menuButton");
		nextButton.setOnAction(e -> {
				field.next();
			});
		vbox.getChildren().add(nextButton);
		
		vbox.getChildren().add(clearBtn);
		
			// Button - Options
		Button optionButton = new Button("Controls");
		optionButton.setId("menuButton");
		optionButton.setOnAction(e -> {
				Controls.display(stage);
			});
		vbox.getChildren().addAll(optionButton, backButton);
		
			// Scene
		gameScene = new Scene(borderLayout, 800, 700);
		gameScene.addEventHandler(KeyEvent.KEY_PRESSED, (key) ->
	        {
	        	switch (key.getCode())
	        	{
	        	case N:
	        		field.next();
	        		break;
	        	case R:
	        		int n;
	        		try
	        		{
	        			n = Integer.parseInt(textCells.getText());
	        			if (n <= 300) field.randomSpawn(n);
	        			else textCells.setText("Too many!");
	        		}
	        		catch(NumberFormatException e) { textCells.setText("Wrong parameter!");}
	        		break;
	        	case S:
	        		anim.play();
	        	    borderLayout.getChildren().remove(vbox);
	        		break;
	        	case P:
	        		anim.pause();
	        		borderLayout.setLeft(vbox);
	        		break;
	        	case F:
	        		if (stage.isMaximized()) stage.setMaximized(false);
	        		else stage.setMaximized(true);
	        		break;
	        	case M:
	        		if (playing) { player.pause(); playing = false; }
	        		else { player.play(); playing = true; }
	        		break;
	        	case G:
	        		field.killAll(); break;
	        	case C:
	        		if ( Ranking.isDiplayed()) Ranking.remove(borderLayout);
	        		else Ranking.display(field, borderLayout, stage);
				default:
					break;
	        	}
	        	
	        });
		gameScene
			.getStylesheets()
			.add(MainSGS.class
					.getResource("gameScene.css")
					.toExternalForm());
		scale = 1;
		setZoom();
		
			// Stage 
		stage.setScene(gameScene);
		stage.setResizable(true);
		stage.setMaximized(true);
		
	}
}