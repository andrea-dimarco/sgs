package sgs;

import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import java.util.Random;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map.Entry;

public class GameField extends GridPane
{
	private HashMap<Pair<Integer,Integer>, Cell> aliveCells;
	private ArrayList<Pair<Integer,Integer>> deadCells;
	private Cell[][] matrix;
	private int width, height;
	private String generator;
	private GameField field;
	private Stage stage;
	
	// Costruttore ///////////////////////////////////////
	public GameField (int width, int height, Stage stage)
	{
		this.field = this;
		this.stage = stage;
		this.generator = "Random";
		this.setId("FIELD");
		this.getStylesheets().add(MainSGS.class.getResource("field.css").toExternalForm());
		this.width = width; this.height = height;
	    aliveCells = new HashMap<Pair<Integer,Integer>, Cell>();
		deadCells = new ArrayList<Pair<Integer,Integer>>();
		this.setCache(true);
		this.setCacheHint(CacheHint.SPEED);
		this.matrix = new Cell[height][width];

		for (int y = 0; y < height; y++) 
		{
			for (int x = 0; x < width; x++)
			{
				deadCells.add(new Pair<Integer,Integer>(x,y));
			}
			RowConstraints rowConst = new RowConstraints(16);
            this.getRowConstraints().add(rowConst);
        }
		
		for (int x = 0; x < width; x++) 
        {
            ColumnConstraints colConst = new ColumnConstraints(16);
            this.getColumnConstraints().add(colConst);
        }
		
		this.setOnMouseClicked(new EventHandler<MouseEvent>() 
        {
            @Override
            public void handle(MouseEvent event) 
            {
            	int x = (int) event.getX(), y = (int) event.getY();
            	y = y/16;
            	x =  x/16;
            	if (y < matrix.length && x < matrix[0].length && matrix[y][x] == null)
    			{
            		MouseButton button = event.getButton();
	                if(button==MouseButton.PRIMARY)
					{
	                	Cell cell;
            			if (generator.equals("Random")) cell = new Cell(x,y,field);
            			else cell = new Cell(x,y, generator, field);
	    				add(cell, x, y);
	    				matrix[y][x] = cell;
	    				deadCells.remove(new Pair<Integer,Integer>(x,y));
					    aliveCells.put(new Pair<Integer,Integer>(x,y),cell);
					}
    			}
            }
        });
		        
	}
	
	
	// Metodi SET ////////////////////////////////////////
	/**
	 * Imposta il generatore della personalit‡
	 * @param generator
	 */
	public void setGenerator(String generator)
	{
		this.generator = generator;
	}
	
	
	// Metodi GET ////////////////////////////////////////
	public HashMap<Pair<Integer,Integer>, Cell> getAliveCells()
	{
		return aliveCells;
	}

	public ArrayList<Pair<Integer,Integer>> getDeathCells()
	{
		return deadCells;
	}
	
	public Cell[][] getMatrix()
	{
		return matrix;
	}
	
	public int[] getSize()
	{
		int[] size = {width,height};
		return size;
	}
	
	public Stage getStage()
	{
		return stage;
	}

	
	// Metodi ///////////////////////////////////////////
	public void next()
	{
		int x, y;
		Cell cell;
		boolean alive = false, alone = true;
		ArrayList<UAV> uavList = new ArrayList<UAV>();
		
		try {
			for (Entry<Pair<Integer, Integer>, Cell> entry : aliveCells.entrySet())
			{
				x = entry.getKey().getKey();
				y = entry.getKey().getValue();
				cell = entry.getValue();
				
				for (Cell c : cell.getAcquaintances())
				{
					if (c.isAlive()) { alone = false; break; }
				}
				if (alone)
				{
					// FASE RICERCA ////////////////////
					alive = false;
					int y1 = cell.getPosition()[1]-1;
					
					for (int i=0; i < 3; i++)
					{
						int x1 = cell.getPosition()[0]-1;
						for (int j = 0; j < 3; j++)
						{
							if (y1 >= 0 && y1 < matrix.length
									&& x1 >= 0 && x1 < matrix[0].length)
							{
								if (matrix[y1][x1] != null
										&& !matrix[y1][x1].equals(cell)) 
								{
									alive = true; 
								}
							}
							x1++;
						}
						y1++;
					}
					
					if (!alive)
					{	
						UAV uavCell = new UAV(matrix, cell);
						uavCell.start();
						uavList.add(uavCell);
					}	
				}
				else 
				{
					// la Cell ha gi‡ conoscenze e deve comunicare
					cell.sendOutput();
					cell.elaborateInput();
				}
				
				addFriends(cell, x, y);
				
				alone = true;
			}
		} catch (ConcurrentModificationException E) {
			
		}
		movement(uavList);
	}
	
	private void addFriends(Cell cell, int x, int y)
	{
		int range = (cell.getMind() == 'I')? 3 : 5;
		int y1 = (cell.getMind() == 'I')? cell.getPosition()[1]-1 : cell.getPosition()[1]-2;
		
		for (int i=0; i < range; i++)
		{
			int x1 = (cell.getMind() == 'I')? cell.getPosition()[0]-1 : cell.getPosition()[0]-2;
			for (int j = 0; j < range; j++)
			{
				if (y1 >= 0 && y1 < matrix.length
						&& x1 >= 0 && x1 < matrix[0].length)
				{
					if (matrix[y1][x1] != null
							&& !matrix[y1][x1].equals(cell)) 
					{
						// aggiungo le Cell ai conoscenti
						cell.addAcquaintances(matrix[y1][x1]);
					}
				}
				x1++;
			}
			y1++;
		}
		
	}
	
	private void movement(ArrayList<UAV> uavList)
	{
		ArrayList<UAV> app = new ArrayList<UAV>();
		Cell aliveCell;
		Cell cell;
		
		int nY, nX;
		do
		{
			for (UAV uav : uavList)
			{
				if (uav.isDone()) 
				{
					cell = uav.getCell();
					aliveCell = uav.getAliveCell();
					
					if (cell.getMind() == 'I') cell.updateStatus(-1);
					else cell.updateStatus(-3);
					
					if (aliveCell != null)
					{
						nY = cell.getPosition()[1];
						nX = cell.getPosition()[0];
						if (nY < aliveCell.getPosition()[1]) nY++;
						else if (nY > aliveCell.getPosition()[1]) nY--;
						if (nX < aliveCell.getPosition()[0]) nX++;
						else if (nX > aliveCell.getPosition()[0]) nX--;
						
						if (matrix[nY][nX] == null)
						{
							swap(cell, nX, nY);
						}
					}
					else if (cell.getTactics() == 'P')
					{
						Random rand = new Random();
						int x = rand.nextInt(width);
						int y = rand.nextInt(height);
						
						nY = cell.getPosition()[1];
						nX = cell.getPosition()[0];
						
						if (nY < y) nY++;
						else if (nY > y) nY--;
						if (nX < x) nX++;
						else if (nX > x) nX--;
						
						if (matrix[nY][nX] == null)
						{
							swap(cell, nX, nY);
						}
					}
				}
				else {app.add(uav);};
			}
			uavList = app; 
			app = new ArrayList<UAV>();
		}
		while (uavList.size() > 0);
	}
	
	private void swap(Cell cell, int nX, int nY)
	{
		this.getChildren().remove(cell);
		this.add(cell,nX,nY); 
		deadCells.remove(new Pair<Integer,Integer>(nX,nY));
		deadCells.add(new Pair<Integer,Integer>(cell.getPosition()[0],cell.getPosition()[1]));
		aliveCells.remove(new Pair<Integer,Integer>(cell.getPosition()[0],cell.getPosition()[1]));
		aliveCells.put(new Pair<Integer,Integer>(nX,nY),cell);
		matrix[nY][nX] = cell;
		matrix[cell.getPosition()[1]][cell.getPosition()[0]] = null;
		cell.setPosition(nX, nY);
	}
	
	public void randomSpawn(int nCells)
	{
		Random random = new Random();
		int x, y, r;
		Cell cell;
		nCells = (nCells > deadCells.size() ) ? deadCells.size() : nCells;
		
		for (int k = 0; k < nCells; k++)
		{
			r = random.nextInt(deadCells.size());
			x = deadCells.get(r).getKey();
			y = deadCells.get(r).getValue();
		    if (generator == "Random")
		    {
		    	cell = new Cell(x, y, field);
		    } else 
		    {
		    	cell = new Cell(x, y, generator, field);
		    }
		    matrix[y][x] = cell;
			aliveCells.put(new Pair<Integer,Integer>(x,y), cell);
			deadCells.remove(new Pair<Integer,Integer>(x,y));
			
			this.add(cell,x,y);
		}
	}
	
	public void newBorn(Cell parent)
	{
		if (deadCells.size() > 0)
		{
			Random random = new Random();
			int x, y, r;
			Cell cell;
	
			r = random.nextInt(deadCells.size());
			x = deadCells.get(r).getKey();
			y = deadCells.get(r).getValue();
			
			cell = new Cell(x, y, parent, field);
			matrix[y][x] = cell;
			aliveCells.put(new Pair<Integer,Integer>(x,y), cell);
			deadCells.remove(new Pair<Integer,Integer>(x,y));
			this.add(cell, x, y);

		}
		else 
		{
			parent.kill();
			killCell(parent);
		}
	}
	
	public void killAll()
	{
		if (aliveCells.size() > 0)
		{
			for (Entry<Pair<Integer, Integer>, Cell> entry : aliveCells.entrySet())
			{
				Cell cell = entry.getValue();
				int x = entry.getKey().getKey();
				int y = entry.getKey().getValue();
				cell.kill();
				deadCells.add(new Pair<Integer,Integer>(x,y));
				matrix[y][x] = null;
			}
			aliveCells.clear();
			this.getChildren().clear();
		}
	}
	
	public void killCell(Cell cell)
	{
		this.getChildren().remove(cell);
		deadCells.add(new Pair<Integer,Integer>(cell.getPosition()[0],cell.getPosition()[1]));
		aliveCells.remove(new Pair<Integer,Integer>(cell.getPosition()[0],cell.getPosition()[1]));
		matrix[cell.getPosition()[1]][cell.getPosition()[0]] = null;
		cell.kill();
	}
	
}


