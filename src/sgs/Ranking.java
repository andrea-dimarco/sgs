package sgs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Ranking {

	private static HashMap<String, Integer> ranking;
	private static ArrayList<String>        list;
	private static ScrollPane               scroll;
	private static boolean                  displayed;
	
	/**
	 * Genera la classifica delle Cell dalla pi˘ frequente alla meno frequente.
	 * @param field il campo di cui si vuole calcolare la classifica
	 * @param border il layout su cui deve comparire la classifica
	 * @param window la finestra padre
	 */
	public static void display(GameField field, BorderPane border, Stage window)
	{	
		scroll = new ScrollPane();
		scroll.getStylesheets().add(MainSGS.class.getResource("info.css").toExternalForm());
		
    	sort(field.getAliveCells().values());
		setContent(window);
		
		// BorderPane
		border.setRight(scroll);
		
		displayed = true;
	}
	
	/**
	 * Sistema la classifica in un VBox e lo mette all'interno della scroll pane
	 * @param window la finestra della classifica, questo parametro serve per impostare la finestra padre di info in quanto cliccando sulle icone si apre la finestra di approfondimento
	 */
	private static void setContent(Stage window)
	{
		// VBox - Layout
		VBox layout = new VBox(20);
		layout.setId("vbox");
		
		// Posiziono clasifica
		for (String p : list)
		{
			// Button
			Button button = new Button();
			button.setId(p);
			button.setOnAction(e -> Info.details(p, window));
			
			// Text
			Text text = new Text(p + ": " + ranking.get(p));
			text.setId("TEXT");
			
			// HBox
			HBox hbox = new HBox(10);
			hbox.getChildren().addAll(button, text);
			
			layout.getChildren().add(hbox);
		}
		
		// ScrollPane - scroll
		scroll.setContent(layout);
	}
	
	/**
	 * Chiude la classifica
	 * @param border il BorderPane su cui Ë stata inserita la classifica
	 */
	public static void remove(BorderPane border)
	{
		border.getChildren().remove(scroll);
		displayed = false;
	}
	
	/**
	 * Informa se la classifica e in mostra o no
	 * @return true = in mostra false = non in mostra
	 */
	public static boolean isDiplayed()
	{
		return displayed;
	}
	
	/**
	 * Sistema i valori nel dizionario e li ordina in una lista
	 */
	private static void sort(Collection<Cell> values)
	{
		// creo il dizionario con 
		// {personalita : numero ricorrenze}
		list = new ArrayList<>();
		ranking = new HashMap<>();
		
		String p;
		for (Cell c : values)
		{
			p = c.getPersonality();
			if (ranking.containsKey(p))
			{
				ranking.put(p, ranking.get(p) + 1);
			} else {
				ranking.put(p, 1);
			}
		}
		
		// ordino i valori dell'array
		int[] array = new int[ranking.size()];
		int k = 0;
		for (int v : ranking.values())
		{
			array[k] = v;
			k++;
		}
		Arrays.sort(array);
		
		for(int i = 0; i < array.length / 2; i++)
		{
		    int temp = array[i];
		    array[i] = array[array.length - i - 1];
		    array[array.length - i - 1] = temp;
		}
		
		// sistemo le personalit‡ in una lista ordinata
		for (int x : array)
		{
			search:
			for (String s : ranking.keySet())
			{
				// ho trovato la personalit‡ 
				if (ranking.get(s) == x
					&& !list.contains(s))
				{
					list.add(s);
					break search;
				}
			}
		}
	}
	
	
}
