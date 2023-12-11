package sgs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

/**
 * La singola Cellula che compone il Field
 */
public class Cell extends Button
{
	private Cell    cell;
	private char    mind;    // 'E' or 'I' 
	private char    energy;  // 'S' or 'N' 
	private char    nature;  // 'T' or 'F' 
	private char    tactics; // 'J' or 'P' 
	
	private int     x, y; 
	
	private boolean alive;
	
	private Hashtable<Cell, Integer>   acquaintances;
	private Hashtable<Cell, Integer>   sons;
	private Cell                       parent;
	private int                      parentMessage;
	
	private int    status;
	private float  perception;
	
	private String name;
	private int    personalityGroup;
	private GameField field;
	private Timeline anim;

	// Costruttore /////////////////////////////
	/**
	 * Costruttore che genera la Cell con
	 * stato di default 'dead'
	 * @param x
	 * @param y
	 */
	public Cell(int x, int y, GameField field)
	{
		generatePersonality();
		constructor(x, y, field);
		this.parent = null;	
		perception = (tactics =='J')? 0.25f : 0.15f;

	}
	
	/**
	 * Sveglia la Cell donandole la personalit‡ scelta
	 * @param pers la personalit‡ da donare a Cell
	 */
	public Cell(int x, int y, String pers, GameField field)
	{
		mind = pers.charAt(0);
		energy = pers.charAt(1);
		nature = pers.charAt(2);
		tactics = pers.charAt(3);
		this.parent = null;
		constructor(x, y, field);
	}
	
	/**
	 * Fa nascere la Cell da una Cell parent
	 * @param x
	 * @param y
	 * @param parent
	 */
	public Cell(int x, int y, Cell parent, GameField field)
	{
		// Prende la personalit‡ del padre
		this.mind = parent.getMind();
		this.energy = parent.getEnergy();
		this.nature = parent.getNature();
		this.tactics = parent.getTactics();
		constructor(x, y, field);
		this.parent = parent;
		this.parentMessage = 0;
	
		parent.addSon(this);
		this.perception = parent.perception;
	}
	
	private void constructor(int x, int y, GameField field)
	{
		this.cell = this;
		this.x = x;
		this.y = y;
		alive = true;
		this.setMaxSize(16, 16); this.setMinSize(16, 16);
		this.setCache(true);
		this.setCacheHint(CacheHint.SPEED);
		this.setFocusTraversable(false);
		this.field = field;
		status = 50;
		
		this.name = Database.getRandomName();
		this.personalityGroup = Database.getGroup(this.getPersonality());
		this.setId(getPersonality());
		
		anim = new Timeline(new KeyFrame(new Duration(800), e -> checkStatus()));
    	anim.setCycleCount(Timeline.INDEFINITE);
    	anim.play();
		
		sons = new Hashtable<>();
		acquaintances = new Hashtable<>();
		
		this.setOnMouseClicked(new EventHandler<MouseEvent>() 
		{
			@Override
			public void handle(MouseEvent event) 
			{ 
				MouseButton button = event.getButton();
                if(button==MouseButton.PRIMARY)
				{
                	field.getMatrix()[y][x] = null;
				    field.getDeathCells().add(new Pair<Integer,Integer>(x,y));
				    field.getAliveCells().remove(new Pair<Integer,Integer>(x,y));
				    field.getChildren().remove(cell);
				    cell.kill();
				}
                else if(button==MouseButton.SECONDARY)
                {
                	int x = cell.getPosition()[0], y = cell.getPosition()[1];
                	Insight.display(field.getMatrix()[y][x], field.getStage());
                }
			} 
		});
	}
	
	// Metodi GET //////////////////////////////
	public int getStatus()
	{
		return status;
	}
	
	public float getPerception()
	{
		return perception;
	}
	
	public String getPersonality()
	{ 
		return "" + mind + energy + nature + tactics; 
	}

	public char getMind()
	{
		return mind;
	}
	public char getEnergy()
	{
		return energy;
	}
	public char getNature()
	{
		return nature;
	}
	public char getTactics()
	{
		return tactics;
	}
	
	public int[] getPosition()
	{
		int[] position = {x,y};
		return position;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getAcquaintancesSize()
	{
		return this.acquaintances.size();
	}
	
	public int getGroup()
	{
		return this.personalityGroup;
	}
	
	public Cell getParentCell()
	{
		return this.parent;
	}
	
	public String getPersonalityName()
	{
		return Database.getPersonalityName(this.getPersonality());
	}
	
	public Set<Cell> getAcquaintances()
	{
		return this.acquaintances.keySet();
	}
	
	public Set<Cell> getSons()
	{
		return this.sons.keySet();
	}
	
	
	// Metodi SET /////////////////////////////
	/**
	 * Imposta la posizione della Cell
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	
	// Metodi /////////////////////////////////
	/**
	 * Aggiorna lo stato della Cell
	 * @param k
	 */
	public void updateStatus(int k)
	{
		status += k;
	}
	
	/**
	 * Ritorna se la Cell Ë viva o meno
	 * @return
	 */
	public boolean isAlive()
	{
		return alive;
	}
	
	/**
	 * Genera casualmente la personalit‡ della Cell
	 */
	private void generatePersonality()
	{
		Random rand = new Random();
		
		mind = (rand.nextInt(2) == 0) ? 'E' : 'I';
		energy = (rand.nextInt(2) == 0) ? 'S' : 'N';
		nature = (rand.nextInt(2) == 0) ? 'T' : 'F';
		tactics = (rand.nextInt(2) == 0) ? 'J' : 'P';
	}

	private void checkStatus()
	{
		if (this.status >= 500)
		{
			// nasce un figlio
			this.status = 50;
			field.newBorn(this);
		} 
		else if (this.status <= 0) 
		{
			field.killCell(this);
			cell.kill();
		}
	}
	
	/**
	 * Uccide la Cell
	 */
	public void kill()
	{
		alive = false;
		this.setId("DEAD");
		//this.setCache(false);
		if (parent != null && parent.isAlive()) parent.updateStatus(-20);
		
		for (Cell son : sons.keySet())
		{
			if (son.isAlive()) son.updateStatus(-20);
		}
		anim.stop();
	}
	
	/**
	 * Aggiunge un figlio alla Cell
	 * @param son
	 */
	public void addSon(Cell son)
	{
		this.sons.put(son, 0);
	}
	
	/**
	 * Assegna i messaggi dei figli al padre
	 * @param son
	 * @param message
	 */
	public void updateSon(Cell son, int message)
	{
		this.sons.put(son, message);
	}
	
	/**
	 * Aggiumge una conoscenza alla Cell
	 * @param friend
	 */
	public void addAcquaintances(Cell friend)
	{
		if (!this.acquaintances.containsKey(friend))
		{
			if (friend.mind == 'I') // controlla distanza
			{
				
				if (this.x - friend.x <= 1 && this.x - friend.x >= -1
					&& this.y - friend.y <= 1 && this.y - friend.y	>= -1)
				{
					this.acquaintances.put(friend, 0);
				}
			} 
			else 
			{
			    this.acquaintances.put(friend, 0);
			}
		}
	}
	
	/**
	 * Reagisce agli input ricevuti dalle cellule conoscenti
	 */
	public void elaborateInput()//GameField field)
	{
		int average = 0;
		int k = 0;
		
		if (this.acquaintances.size() > 0)
		{
			for (Entry<Cell, Integer> entry : acquaintances.entrySet())
			{
				// 'N' amplifica input del suo 30%
				// 'S' depotenzia input del suo 30%
				int v = entry.getValue();
				Cell cell = entry.getKey();
				if (cell.isAlive())
				{
					average += v;
					k++;
				}
			}
		}
		if (this.sons.size() > 0)
		{
			for (Entry<Cell, Integer> entry : sons.entrySet())
			{
				// 'N' amplifica input del suo 30%
				// 'S' depotenzia input del suo 30%
				int v = entry.getValue();
				Cell cell = entry.getKey();
				if (cell.isAlive())
				{
					average += v;
					k++;
				}
			}
		}
		if (this.parent != null)
		{
			if (parent.isAlive())
			{
				average += this.parentMessage;
				k++;
			}
		}
		
		// Aggiorno lo stato di Cell con la media degli input ricevuti
		average += (int) (average + ((this.energy == 'N') ? average*0.3f : -average*0.3f));
		average /= ((average == 0) ? 1 : k);
		this.status += average;
		
	}

	
	/**
	 * Manda alle Cell acquaintances e figli gli output rispettivi
	 */
	public void sendOutput()
	{
		int output;
		Random rand = new Random();
		if (this.acquaintances.size() > 0)
		{
			// Mando messaggi ad amici
			for (Cell c : this.acquaintances.keySet())
			{ 
				if (c.isAlive())
				{
					output = this.elaborateOutput(c);
					
					// il tratto 'nature' influisce sull'output
					// 'F' -> +30%
					// 'T' -> -30%
					output += (int) ((this.nature == 'F') ? output*0.3f : -output*0.3f);
					
					// mantengo output entro [-10:10]
					if (output > 10)
						output = 10;
					else if (output < -10)
						output = -10;
					
					output = (output + this.acquaintances.get(c))/2;
					c.acquaintances.put(this, output);
				// consegno output
				}
		
			}
		}
		if (this.sons.size() > 0)
		{
			// Mando messaggi ai miei figli
			for (Cell son : this.sons.keySet())
			{ 
				if (son.isAlive())
				{
					output = rand.nextInt(6) + 5; // [5:10]
					
					// il tratto 'nature' influisce sull'output
					// 'F' -> +30%
					// 'T' -> -30%
					output += (int) ((this.nature == 'F') ? output*0.3f : -output*0.3f);
					
					// mantengo output entro [-10:10]
					if (output > 10)
						output = 10;
					else if (output < -10)
						output = -10;

					output = (output + this.sons.get(son))/2;
					son.parentMessage = output;
					
				}
			}
		}
		if (this.parent != null)
		{
			// Mando messaggi al mio parent
			if (this.parent.isAlive())
				
			{
				output = rand.nextInt(6) + 5; // [5:10]
				// il tratto 'nature' influisce sull'output
				// 'F' -> +30%
				// 'T' -> -30%
				output += (int) ((this.nature == 'F') ? output*0.3f : -output*0.3f);
				
				// mantengo output entro [-10:10]
				if (output > 10)
					output = 10;
				else if (output < -10)
					output = -10;
				
				output = (output +this.parentMessage)/2;
				this.parent.updateSon(this, output);
			}

		}
	}
	
	/**
	 * Ritorna l'int dell'output in base alle due personalit‡ in gioco
	 * @param c
	 * @return risposta da mandare
	 */
	private int elaborateOutput(Cell other)
	{
		int output = 0;
		Random rand = new Random();
		
		switch (this.personalityGroup)
		{
		case 1: // Analisti
			switch (other.getGroup())
			{
			case 1: // Analisti
				output = rand.nextInt(8) - 2; // [-2:5]
				break;
			case 2: // Diplomatici
				output = rand.nextInt(11) - 5; // [-5:5]
				break;
			case 3: // Sentinelle
				output = rand.nextInt(11); // [0:10]
				break;
			case 4: // Esploratori
				output = - (rand.nextInt(11)); // [-10:0]
				break;
			}
			break;
		case 2: // Diplomatici
			switch (other.getGroup())
			{
			case 1: // Analisti
				output = rand.nextInt(11) - 5; // [-5:5]
				break;
			case 2: // Diplomatici
				output = rand.nextInt(8) - 2; // [-2:5]
				break;
			case 3: // Sentinelle
				output = - (rand.nextInt(11)); // [-10:0]
				break;
			case 4: // Esploratori
				output = rand.nextInt(11); // [0:10]
				break;
			}
			break;
		case 3: // Sentinelle
			switch (other.getGroup())
			{
			case 1: // Analisti
				output = rand.nextInt(11); // [0:10]
				break;
			case 2: // Diplomatici
				output = - (rand.nextInt(11)); // [-10:0]
				break;
			case 3: // Sentinelle
				output = rand.nextInt(8) - 2; // [-2:5]
				break;
			case 4: // Esploratori
				output = rand.nextInt(11) - 5; // [-5:5]
				break;
			}
			break;
		case 4: // Esploratori
			switch (other.getGroup())
			{
			case 1: // Analisti
				output = - (rand.nextInt(11)); // [-10:0]
				break;
			case 2: // Diplomatici
				output = rand.nextInt(11); // [0:10]
				break;
			case 3: // Sentinelle
				output = rand.nextInt(11) - 5; // [-5:5]
				break;
			case 4: // Esploratori
				output = rand.nextInt(8) - 2; // [-2:5]
				break;
			}
			break;
		}
		return output;
	}
	
	
}
