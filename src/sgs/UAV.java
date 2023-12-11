package sgs;

public class UAV extends Thread
{
	private Cell cell;
	private Cell[][] matrix;
	private boolean isDone;
	private Cell aliveCell;
	
	public UAV(Cell[][] matrix, Cell cell)
	{
		isDone = false;
		aliveCell = null;
		this.cell = cell;
		this.matrix = matrix;
	}
	
	public boolean isDone()
	{
		return isDone;
	}
	
	public Cell getCell()
	{
		return cell;
	}
	
	public Cell getAliveCell()
	{
		return aliveCell;
	}
	
	@Override
	public void run()
	{
		int n = 5;
		int range = (int) (Math.round(matrix[0].length*cell.getPerception()))/2;
		range = (range < 2)? 2 : range;
		int y = cell.getPosition()[1]-2, x = cell.getPosition()[0]-2;
		int x1,y1;
		
		outerLoop:
		for (int i = 0; i < range; i++)
		{

			x1 = x+n-1;
			while (x < x1)
			{

				if (x < matrix[0].length && x > -1 && y < matrix.length && y > -1)
				{
		
						if (matrix[y][x] != null)
						{
							aliveCell = matrix[y][x];
							break outerLoop;
						}
				}
				x++;
			}
			y1 = y+n-1;
			while (y < y1)
			{
				if (x < matrix[0].length && x > -1 && y < matrix.length && y > -1)
				{


						if (matrix[y][x] != null)
						{

							aliveCell = matrix[y][x];
							break outerLoop;
						}
				}
				y++;
			}
			x1 = x-n+1;
			while (x > x1)
			{
				if (x < matrix[0].length && x > -1 && y < matrix.length && y > -1)
				{
		
						if (matrix[y][x] != null)
						{

							aliveCell = matrix[y][x];
							break outerLoop;
						}
				}
				x--;
			}
			y1 = y-n+1;
			while (y > y1)
			{
				if (x < matrix[0].length && x > -1 && y < matrix.length && y > -1)
				{
			
						if (matrix[y][x] != null)
						{
							
							aliveCell = matrix[y][x];
							break outerLoop;
						}
				}
				y--;
			}
			n+=2; y--; x--;
		
		}
		isDone=true;

	}
}

