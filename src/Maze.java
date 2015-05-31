import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * The Class Maze.
 */
public class Maze {

	/** The Constant random object. */
	public static final Random r = new Random();
	
	/** My maze. */
	public Cell[][] myMaze;
	
	/** The number of visited. */
	public int visited;
	
	/** The last good cell. */
	private Stack<Cell> lastGoodCell;
	
	/** The solution. */
	private Stack<Cell> solution;
	
	/** Solution found? */
	private boolean solutionFound;
	
	/** Debug. */
	private boolean debug;
	
	/** My rows. */
	private int myRows;
	
	/** My columns. */
	private int myCols;
	
	/** The number of cells. */
	private int numCells;
	
	/**
	 * Instantiates a new maze.
	 *
	 * @param height the height
	 * @param width the width
	 * @param theDebug debug
	 * O(n^2) - the call to fillMaze() takes the longest out of anything else happening in this constructor. 
	 */
	Maze(int height, int width, boolean theDebug) {
		
		debug = theDebug;
		myRows = (height * 2) + 1;
		myCols = (width * 2) + 1;
		
		numCells = height * width;
		myMaze = new Cell[myRows][myCols];
		
		visited = 0;
		
		lastGoodCell = new Stack<Cell>();
		solution = new Stack<Cell>();
		
		solutionFound = false;
		
		fillMaze();
	}
	
	/**
	 * Display maze with solution.
	 * O(n^2)
	 */
	public void display() {
		System.out.println("S = Start point\nE = End point\nX = Wall or border\n' ' = Path\n* = Solution path\n");
		for (int i = 0; i < myRows; i++) {
			for (int j = 0; j < myCols; j++) {
				
				if (myMaze[i][j].isBorder 
				    || (!myMaze[i][j].visited && !myMaze[i][j].isPath) && (!myMaze[i][j].isStart && !myMaze[i][j].isEnd)) {
					
					System.out.print("X ");
				} else if (myMaze[i][j].isStart) {
					System.out.print("S ");
				
				} else if (myMaze[i][j].isEnd) {
					System.out.print("E ");
					
				} else if (solution.contains(myMaze[i][j])) {
					System.out.print("* ");
					
				} else {
					System.out.print("  ");
				}
			
			}
			System.out.print('\n');
		}
		System.out.print('\n');
	}
	
	/**
	 * Fill maze with cells.
	 * O(n^2)
	 */
	private void fillMaze() {
		for (int i = 0; i < myRows; i++) {
			for (int j = 0; j < myCols; j++) {
				if (i == 0 || i == myRows - 1 || j == 0 || j == myCols - 1)
					myMaze[i][j] = new Cell(i, j, true);
				else {
					myMaze[i][j] = new Cell(i, j, false);
				}
				
			}
		}
		// Set start and end points
		myMaze[0][1].isStart = true;
		myMaze[myRows - 1][myCols - 2].isEnd = true;
		
		myMaze[0][1].isBorder = false;
		myMaze[myRows - 1][myCols - 2].isBorder = false;
		dig();
	}
	
	/**
	 * Dig dig out the maze from the surrounding wall cells.
	 *
	 * @param row the row
	 * @param col the column
	 * O(n*m) - I think this is right because the method will iterate over all cells of the maze and change something about them. 
	 * I could also see it being O(n) but we can't prove that m will be less than n so it can't be safely ignored. O(n*m) is a safer guess.
	 */
	private void dig() {
		myMaze[1][1].visited = true;
		Cell current = myMaze[1][1];
		solution.push(current);
		
		visited = 1;
		
		while (visited < numCells) {
			ArrayList<Cell> neighbors = unvisitedNeighbors(current.row, current.col);
			
			if (neighbors.size() > 0) {
				if (debug) debugDisplay();
				
				int neighborIndex = r.nextInt(neighbors.size());
				
				Cell randNeighbor = neighbors.get(neighborIndex);
				
				randNeighbor.visited = true;
				
				//figure out which way to tunnel
				if (randNeighbor.row < current.row) {
					myMaze[current.row - 1][current.col].isPath = true;
					
				} else if (randNeighbor.row > current.row) {
					myMaze[current.row + 1][current.col].isPath = true;
					
				} else if (randNeighbor.col < current.col) {
					myMaze[current.row][current.col - 1].isPath = true;
					
				} else if (randNeighbor.col > current.col){
					myMaze[current.row][current.col + 1].isPath = true;
				}
				
				lastGoodCell.push(current);				
				current = randNeighbor;
				
				//check if the endpoint has been reached
				if (current.row == myRows - 2 && current.col == myCols - 2) {
					solutionFound = true;
					solution.push(current);
				} 
				
				if (!solutionFound) {
					solution.push(current);
				}
				
				++visited;
				
			} else {
				if (!lastGoodCell.isEmpty()) current = lastGoodCell.pop();
				if (!solution.isEmpty() && !solutionFound) solution.pop();
			}
			
		}
	}
	
	/**
	 * Returns the number of unvisited neighbors surrounding a cell.
	 *
	 * @param row the row
	 * @param col the col
	 * @return the array list
	 * O(n)
	 */
	private ArrayList<Cell> unvisitedNeighbors(int row, int col) {
		ArrayList<Cell> result = new ArrayList<Cell>();
		
		if (row - 2 >= 0 && !myMaze[row - 2][col].visited && !myMaze[row - 2][col].isBorder) {
			result.add(myMaze[row - 2][col]);
		}
		if (row + 2 < myRows && !myMaze[row + 2][col].visited && !myMaze[row + 2][col].isBorder) {
			result.add(myMaze[row + 2][col]);
		}
		if (col - 2 >= 0 && !myMaze[row][col - 2].visited && !myMaze[row][col - 2].isBorder) {
			result.add(myMaze[row][col - 2]);
		}
		if (col + 2 < myCols && !myMaze[row][col + 2].visited && !myMaze[row][col + 2].isBorder) {
			result.add(myMaze[row][col + 2]);
		}
		
		return result;
	}
	
	/**
	 * Debug display.
	 * O(n^2)
	 */
	private void debugDisplay() {
		for (int i = 0; i < myRows; i++) {
			for (int j = 0; j < myCols; j++) {
				if (myMaze[i][j].visited) {

					System.out.print("  ");
				} else if (myMaze[i][j].isBorder || (!myMaze[i][j].visited && !myMaze[i][j].isPath)) {
					System.out.print("X ");
				} else {
					System.out.print("  ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	
	/**
	 * The Cell Class.
	 */
	private class Cell {
		
		/** The row. */
		public int row;
		
		/** The column. */
		public int col;
		
		/** Visited. */
		public boolean visited;
		
		/** The is start. */
		private boolean isStart;
		
		/** The is end. */
		public boolean isEnd;
		
		/** The is border. */
		public boolean isBorder;
		
		/** The is path. */
		public boolean isPath;
						
		/**
		 * Instantiates a new cell.
		 *
		 * @param theRow the the row
		 * @param theCol the the col
		 * @param theBorder the the border
		 * O(1)
		 */
		public Cell(int theRow, int theCol,  boolean theBorder) {
			row = theRow;
			col = theCol;
			visited = false;
			isStart = false;
			isEnd = false;
			isPath = false;
			isBorder = theBorder;
			
		}
	}
	
}
