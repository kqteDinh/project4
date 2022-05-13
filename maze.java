import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class maze {
	private int size;
	private int time;
	private Random random;
	private cellNode[][] maze;
	private boolean end = false;
	private boolean DFS, shortDFS, BFS, shortBFS;
	private ArrayList<cellNode> DFSnode, BFSnode;
	
	//Constructor for the maze
	public maze (int rows) {
		DFS = shortDFS = BFS = shortBFS = false;
		DFSnode = new ArrayList<cellNode>();
		BFSnode = new ArrayList<cellNode>();
		random = new Random();
		size = rows;
		time = 0;
		maze = new cellNode[size][size];
		for(int x = 0; x < size; x++) {		//Creating the layout of the maze
			for(int y = 0; y < size; y++){
				maze[x][y] = new cellNode(x, y);
				if(x == 0 && y == 0) {
					maze[x][y].north = false;
				}
				if(x == size - 1 && y == size -1) {
					maze[x][y].south = false;
				}
			}
		}
		genMaze();		//Generating the path for the maze
	}
	
	//Creates the official maze using DFS
	public void genMaze() {
		Stack<cellNode> cellStack = new Stack<cellNode>();
		int total = size * size;
		cellNode start = maze[0][0];
		start.setVisited();
		int visitCount = 1;
		
		while(visitCount < total) {		//Creating the path of the maze by tearing down walls
			ArrayList<cellNode> next = directions(start);
			if(!next.isEmpty()) {
				int cell = random.nextInt(next.size());
				cellNode current = next.get(cell);
				start.breakWall(current);
				cellStack.push(start);
				start = current;
				start.setVisited();
				visitCount++;
			}
			else if(!cellStack.isEmpty()) {
				start = cellStack.pop();
			}
		}
	}
	
	//Finds available path from current cell
	public ArrayList<cellNode> directions(cellNode current){
		ArrayList<cellNode> next = new ArrayList<cellNode>();
		int x = current.x;
		int y = current.y;
		
		if(x-1 >= 0 && maze[x-1][y].getWalls() && !maze[x-1][y].getVisited()){		//West
			next.add(maze[x-1][y]);
		}
		if(x+1 < size && maze[x+1][y].getWalls() && !maze[x+1][y].getVisited()){	//East
			next.add(maze[x+1][y]);
		}
		if(y-1 >= 0 && maze[x][y-1].getWalls() && !maze[x][y-1].getVisited()){		//South
			next.add(maze[x][y-1]);
		}
		if(y+1 < size && maze[x][y+1].getWalls() && !maze[x][y+1].getVisited()){	//North
			next.add(maze[x][y+1]);
		}
		return next;
	}
	
	//Colors all cells in the maze white and calls checkDFS to find the solution through the maze
	public void DFS() {
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++){
				maze[x][y].COLOR = colors.WHITE;	//Sets all cells in maze to white, cells are not explored
			}
			for(cellNode[] iterate: maze) {		//Starts traversing through maze to find end
				for(cellNode current: iterate) {
					if(current.COLOR == colors.WHITE) {
						checkDFS(current);
					}
				}
			}
		}
		end = false;
	}
	
	//Checks if current cell have been visited in DFS
	public void checkDFS(cellNode current) {
		if(current.equals(new cellNode(size - 1, size -1))) {
			end = true;
			DFSnode.add(current);
		}
		if(!end) {
			DFSnode.add(current);
		}
		current.COLOR = colors.GREY;	//Color current cell grey, meaning the cell is not fully explored
		time++;
		current.startTime = time;
		for(cellNode iterate: current.neighbor) {
			if(iterate.COLOR == colors.WHITE) {
				checkDFS(iterate);
			}
		}
		current.COLOR = colors.BLACK;	//Color current cell black, meaning the cell is fully explored
		time++;
		current.endTime = time;
	}
	
	//Prints the solution for the maze using DFS
	public String solutionDFS() {
		DFS = true;
		String string = this.toString();
		DFS = false;
		return string;
	}
	
	//Using BFS to traverse through the maze
	public void BFS() {
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++) {
				maze[x][y].COLOR = colors.WHITE;	//Sets all cells in the maze to white, cells are not explored
			}
		}
		Queue<cellNode> cellQueue = new LinkedList<cellNode>();
		cellNode current = maze[0][0];
		int tempCount = 0;
		current.visitedNodes = tempCount;
		current.distance = 0;
		current.COLOR = colors.GREY;
		tempCount++;
		while(cellQueue.size() != 0) {
			current = cellQueue.remove();
			if(current.x == size - 1 && current.y == size -1) {
				end = true;
			}
			for(cellNode iterate: current.neighbor) {		//Changes color of cell depending on whether it was visited or not
				if(iterate.COLOR == colors.WHITE) {
					iterate.COLOR = colors.GREY;
					iterate.visitedNodes = tempCount;
					tempCount++;
					iterate.distance = current.distance + 1;
					iterate.next = current;
					if(iterate.x == size - 1 && iterate.y == size -1) {
						end = true;
					}
					if(!end) {
						BFSnode.add(iterate);
						}
					cellQueue.add(iterate);
					}
				}
			current.COLOR = colors.BLACK;
		}
		BFSnode.add(maze[size-1][size-1]);
	}
	
	//Prints the solution for the maze using BFS
	public String solutionBFS() {
		BFS = true;
		String string = this.toString();
		BFS = false;
		return string;
	}
	
	//A toString that builds the maze to look like the expected
	@Override
	public String toString() {
		String string = "";
		String[][] createMaze = new String[1 + (size * 2)][1 + (size * 2)];

		for(int x = 0; x < createMaze.length; x++) {
			for (int y = 0; y < createMaze.length; y++) {
				cellNode current = null;
				if (x % 2 == 1 && y % 2 == 1) {
					current = maze[x / 2][y / 2];
				}
				if((x == 0 && y == 1) || (x == size * 2 && y == (size * 2) - 1)) {
						createMaze[x][y] = " ";
				}
				else if(createMaze[x][y] == null && x % 2 == 0 && y % 2 == 0) {
					createMaze[x][y] = "+";
				}
				else if(x == 0 || x == createMaze.length - 1) {
					createMaze[x][y] = "--";
				}
				else if(x % 2 == 1 && (y == 0 || y == createMaze[0].length - 1)) {
					createMaze[x][y] = "|";
				}
				else if(current != null) {
					if (current.west) {
						createMaze[x][y - 1] = "|";
					}
					else {
						createMaze[x][y - 1] = " ";
					}
					if(current.east) {
						createMaze[x][y + 1] = "|";
					}
					else {
						createMaze[x][y + 1] = " ";
					}
					if(current.north) {
						createMaze[x - 1][y] = "--";
					}
					if(current.south) {
						createMaze[x + 1][y] = "--";
					}
					
					Integer begin = (Integer)current.startTime;
					Integer node = current.visitedNodes;
					if(DFS && begin < 10 && DFSnode.contains(current)) {
						createMaze[x][y] = begin.toString() + " ";
					}
					else if(DFS && begin >= 10 && DFSnode.contains(current)) {
						createMaze[x][y] = begin.toString() + "";
					}
					else if(shortDFS && DFSnode.contains(current)) {
						createMaze[x][y] = "##";
					}
					else if(BFS && node < 10 && BFSnode.contains(current)) {
						createMaze[x][y] = node.toString() + " ";
					}
					else if(BFS && node >= 10 && BFSnode.contains(current)) {
						createMaze[x][y] = node.toString() + "";
					}
					else if(shortBFS && BFSnode.contains(current)) {
						createMaze[x][y] = "##";
					}
					else {
						createMaze[x][y] = "  ";
					}
				}
				else {
					createMaze[x][y] = "  ";
				}
			}
		}
		for (int x = 0; x < createMaze.length; x++) {
			for (int y = 0; y < createMaze.length; y++) {
				string += createMaze[x][y];
			}
			string += "\n";
		}
		return string;
	}
}
