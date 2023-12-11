package sgs;

import java.util.Timer;
import java.util.TimerTask;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Insight 
{
	
	public static void display(Cell cell, Stage stage)
	{		
		////////////////////////// LEFT ///////////////////////////////////
			// Button - Cell's icon
		Button cellIcon = new Button();
		cellIcon.setMaxSize(16, 16); cellIcon.setMinSize(16, 16);
		
		Timer iconTimer = new Timer();
		iconTimer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				cellIcon.setId(cell.getId());
				if (cell.getId().equals("DEAD")) 
				{
					cellIcon.setOnAction(e -> {;});
					iconTimer.cancel();
			        iconTimer.purge();
				}
				
			}
		}, 0, 1000);
		
		cellIcon.setId(cell.getId());
		cellIcon.setFocusTraversable(false);

		cellIcon.setOnAction(e -> Info.details(cell.getPersonality(), stage));
			// Text - Cell's name
		Text cellTitleText = new Text(cell.getName());
		cellTitleText.setId("TITLE");
			// text - Cell's coordinates
		Text cellCoordinates = new Text("  [" + cell.getPosition()[0] + "; " + cell.getPosition()[1] + "]");
		cellCoordinates.setId("STATS");
			// HBox - Title HBox
		HBox titleHBox = new HBox(10);
		titleHBox.getChildren().addAll(cellIcon, cellTitleText,
								       cellCoordinates);
		titleHBox.setAlignment(Pos.CENTER_LEFT);
		
			// Text - status
		Text statusText = new Text();
		statusText.setId("STATS");
		
		Timer textTimer = new Timer();
		textTimer.scheduleAtFixedRate(new TimerTask() 
		{
	        @Override
	        public void run() 
	        {
	        	statusText.setText("Status:           " + cell.getStatus());
	        	if (cell.getStatus() <= 0)
	        	{
	        		textTimer.cancel();
			        textTimer.purge();
	        	}
	        }
	    }, 0, 500);
		
			// Text - Parent
		Text parentText = new Text("Parent:   ");
		parentText.setId("STATSparent");
			// Button - Parent's icon
		Button parentIcon = new Button();
		parentIcon.setMaxSize(16, 16); parentIcon.setMinSize(16, 16);
		
		
		if (cell.getParentCell() != null)
		{
			Timer dadTimer = new Timer();
			dadTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run()
			{		if (!cell.getParentCell().isAlive())
					{
						dadTimer.cancel();
						dadTimer.purge();
					}
					parentIcon.setMaxSize(16, 16); parentIcon.setMinSize(16, 16);
					parentIcon.setId(cell.getParentCell().getId());
					parentIcon.setFocusTraversable(false);
					parentIcon.setOnAction(e -> { if(cell.getParentCell().isAlive()) Insight.display(cell.getParentCell(), stage);});
			}}, 0, 1000);
        	
		}
			// text - Parent's name
		Text parentName = new Text("No one");
		parentName.setId("STATS");
		if (cell.getParentCell() != null)
		{
			parentName.setText(cell.getParentCell().getName()
					+ "  [" + cell.getParentCell().getPosition()[0] + "; " + cell.getParentCell().getPosition()[1] + "]");
		}
			// HBox - Parent HBox
		HBox parentHBox = new HBox(10);
		parentHBox.getChildren().addAll(parentText, parentIcon,
									    parentName);
		parentHBox.setAlignment(Pos.CENTER_LEFT);
		
			// Text - personality
		Text personalityText = new Text("Personality:    " + cell.getPersonalityName()
				+ " (" + cell.getPersonality() + ")");
		personalityText.setId("STATS");
		
			// VBox - Left VBox
		VBox leftVBox = new VBox(10);
		leftVBox.getChildren().addAll(titleHBox, statusText,
				                      parentHBox, personalityText);
		leftVBox.setId("LEFT");
		
		////////////////////////// RIGHT //////////////////////////////////
			// Text - Acquaintances
		Text acquaintancesText = new Text("Acquaintances:");
		acquaintancesText.setId("STATS");
		
			// ScrollPane - Acquaintances' scroll
		ScrollPane acquaintancesScroll = new ScrollPane();
		
			// VBox - Acquaintances List
		VBox acquaintancesList = new VBox();
		acquaintancesList.setId("LIST");
		
		//Timer acquaintancesTimer = new Timer();
		
		textTimer.scheduleAtFixedRate(new TimerTask() 
		{
	        @Override
	        public void run() 
	        {
	        	statusText.setText("Status:           " + cell.getStatus());
	        	if (cell.getStatus() <= 0)
	        	{
	        		textTimer.cancel();
			        textTimer.purge();
	        	}
	        }
	    }, 0, 500);
		

		for (Cell c : cell.getAcquaintances())
		{
				// Button - icon
					Button icon = new Button();
					icon.setMaxSize(16, 16); cellIcon.setMinSize(16, 16);
					icon.setId(c.getId());
					icon.setFocusTraversable(false);
					icon.setOnAction(e -> { if (c.isAlive()) Insight.display(c, stage); });
						// Text - name
					Text name = new Text(c.getName()
							+ "  [" + c.getPosition()[0] + "; " + c.getPosition()[1] + "]");
					
					name.setId("NAME");
					
						// HBox - layout
					HBox layout = new HBox(10);
					layout.setId("LIST");
					layout.getChildren().addAll(icon, name);
					layout.setAlignment(Pos.CENTER_LEFT);
		
					acquaintancesList.getChildren().add(layout);
					
		}
		acquaintancesScroll.setContent(acquaintancesList);

		
		acquaintancesScroll.getStylesheets().add(MainSGS.class.getResource("darkScroll.css").toExternalForm());
		
			// Text - Sons
		Text sonsText = new Text("Sons:");
		sonsText.setId("STATS");
			// ScrollPane - sons' scroll
		ScrollPane sonsScroll = new ScrollPane();
			// VBox - Sons List
		VBox sonsList = new VBox();
		sonsList.setId("LIST");
		for (Cell c : cell.getSons())
		{
				// Button - icon
			Button icon = new Button();
			icon.setMaxSize(16, 16); cellIcon.setMinSize(16, 16);
			icon.setId(c.getId());
			icon.setFocusTraversable(false);
			icon.setOnAction(e -> { if (c.isAlive()) Insight.display(c, stage); });
				// Text - name
			Text name = new Text(c.getName()
					+ "  [" + c.getPosition()[0] + "; " + c.getPosition()[1] + "]");
			
			name.setId("NAME");
			
				// HBox - layout
			HBox layout = new HBox(10);
			layout.setId("LIST");
			layout.getChildren().addAll(icon, name);
			layout.setAlignment(Pos.CENTER_LEFT);
			sonsList.getChildren().add(layout);
		}
		sonsScroll.setContent(sonsList);
		sonsScroll.getStylesheets().add(MainSGS.class.getResource("darkScroll.css").toExternalForm());
		
			// VBox - Right VBox
		VBox rightVBox = new VBox(10);
		rightVBox.getChildren().addAll(acquaintancesText, acquaintancesScroll,
								       sonsText, sonsScroll);
		rightVBox.setId("RIGHT");
		
		////////////////////////// END ////////////////////////////////////
		// Layout - BorderPane
		BorderPane borderLayout = new BorderPane();
		borderLayout.setLeft(leftVBox);
		borderLayout.setCenter(rightVBox);
		borderLayout.setId("BORDER");
		
			// Scene - scene
		Scene scene = new Scene(borderLayout, 500, 200);
		scene
			.getStylesheets()
			.add(Insight.class
					.getResource("insight.css")
					.toExternalForm());
		// Stage - window
		Stage window = new Stage();
		
		
		window.initOwner(stage);
		window.getIcons().add(new Image(Insight.class.getResourceAsStream("icone-progetto/SGS-icon.png")));
		window.setScene(scene);
		window.setTitle(cell.getName() + "'s insight page");
		window.setResizable(false);
		window.show();
	}

}

