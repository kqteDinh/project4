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
	public void genMaze() { // worked on this with peerConnections tutor
		Stack<cellNode> cellStack = new Stack<cellNode>(); // stores list of cell locations
		int total = size * size;
		cellNode start = maze[0][0];
		start.setVisited();
		int visitCount = 1;
		
		while(visitCount < total) {		//Creating the path of the maze by tearing down walls
			ArrayList<cellNode> next = directions(start); // this is an arraylist of nodes that are part of the path from the current cell
			if(!next.isEmpty()) { // if one or more neighbors with walls intact are found
				int cell = random.nextInt(next.size()); //pick one at random to knock down
				cellNode current = next.get(cell); // assign that random cell to the one that is to have the wall knocked down
				start.breakWall(current); //break the wall between the chosen random room and the current cell
				cellStack.push(start);// push current cell location on cellStack
				start = current; // set the new starting point to the current cell
				start.setVisited(); // set it as visited and start over
				visitCount++;
			}
			else if(!cellStack.isEmpty()) { // if the cellstack is full and we do not find neighbors with walls intact
				start = cellStack.pop(); // since the stack is LIFO, we can pop the most recent entry off of it
				// this assignment makes the most recent entry the current cell
			}
		}
	}
	
	//Finds available path from current cell
	public ArrayList<cellNode> directions(cellNode current){
		ArrayList<cellNode> next = new ArrayList<cellNode>();
		int x = current.x;
		int y = current.y;
		
		if(x-1 >= 0 && maze[x-1][y].getWalls() && !maze[x-1][y].getVisited()){		
			//West; if neighbor to the west's arraylist with neighbors is empty(means wall isnt broken), 
			//and it has not been visited yet
			next.add(maze[x-1][y]); // add neighbor to arraylist
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
		return next; // this is an arraylist of nodes that are part of the path from the current cell
	}
	
	//Colors all cells in the maze white and calls checkDFS to find the solution through the maze
	public void DFS() {
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++){
				maze[x][y].COLOR = colors.WHITE;	//Sets all cells in maze to white, cells are not explored
			}
			for(cellNode[] iterate: maze) {		//Starts traversing through maze to find end, for every cellNode iterate inside the mazde
				for(cellNode current: iterate) {
					if(current.COLOR == colors.WHITE) { // if the cell has not been explored
						checkDFS(current); // explore the cell
					}
				}
			}
		}
		end = false;
	}
	
	//Checks if current cell have been visited in DFS
	public void checkDFS(cellNode current) {
		if(current.equals(new cellNode(size - 1, size -1))) { 	//when it reaches the exit for the maze
			end = true;
			DFSnode.add(current);				//Adds coordinate to an array
		}
		if(!end) { // if we haven't reached the end
			DFSnode.add(current); // add the current coordinate to the array
		}
		current.COLOR = colors.GREY;	//Color current cell grey, meaning the cell is not fully explored
		time++;
		current.startTime = time;
		for(cellNode iterate: current.neighbor) { //for all neighbors of the current node
			if(iterate.COLOR == colors.WHITE) { // if it has not been explored
				checkDFS(iterate); // explore the node
			}
		}
		current.COLOR = colors.BLACK;	//Color current cell black, meaning the cell is fully explored
		time++;
		current.endTime = time;
	}
	
	//Prints the solution for the maze using DFS, will print out the string representation with the values of the coordinates
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
		Queue<cellNode> cellQueue = new LinkedList<cellNode>(); // create a queue FIFO
		cellNode current = maze[0][0]; // start at the beginnning of the maze
		int tempCount = 0;
		current.visitedNodes = tempCount;
		current.distance = 0;
		current.COLOR = colors.GREY; // because we are currently visiting the beginnin of the maze
		tempCount++;
		while(cellQueue.size() != 0) {
			current = cellQueue.remove();
			if(current.x == size - 1 && current.y == size -1) { // at the end of the maze ( bottom right corner)
				end = true;
			}
			for(cellNode iterate: current.neighbor) {		//Changes color of cell depending on whether it was visited or not
				if(iterate.COLOR == colors.WHITE) { // if cell hasn't been visited
					iterate.COLOR = colors.GREY;		//Currently visiting cell
					iterate.visitedNodes = tempCount;	//Sets current cell with count
					tempCount++;				//Increase count number
					iterate.distance = current.distance + 1;//Choose direciton from current cell
					iterate.next = current;
					if(iterate.x == size - 1 && iterate.y == size -1) {	//Reach the end of the maze
						end = true;
					}
					if(!end) {				//Didn't reach the end
						BFSnode.add(iterate);		//Add the coordinate to an array
						}
					cellQueue.add(iterate);			//Add the coordinate to the queue and run white loop
					}
				}
			current.COLOR = colors.BLACK;				//Cell has been visited
		}
		BFSnode.add(maze[size-1][size-1]);				//Adds the final destination of the maze
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
		String string = ""; // created empty string
		String[][] createMaze = new String[1 + (size * 2)][1 + (size * 2)]; //we have the string this size because we also have to print the walls, in addition to the rooms

		for(int x = 0; x < createMaze.length; x++) {
			for (int y = 0; y < createMaze.length; y++) {
				cellNode current = null;
				if (x % 2 == 1 && y % 2 == 1) {
					current = maze[x / 2][y / 2];
				} // FROM HERE CREATES THE INSIDE OF THE MAZE, SO THE CORNERS AND WALLS
				if((x == 0 && y == 1) || (x == size * 2 && y == (size * 2) - 1)) { 
						createMaze[x][y] = " "; // entry of the maze, or exit of the maze
				}
				else if(createMaze[x][y] == null && x % 2 == 0 && y % 2 == 0) {
					createMaze[x][y] = "+"; // corner starting index at (1,1) since corner is at (0,0)
				}
				else if(x == 0 || x == createMaze.length - 1) {
					createMaze[x][y] = "--"; // this is basically any wall, second parameter makes sure we do not write past the outer wall of the maze
				}
				else if(x % 2 == 1 && (y == 0 || y == createMaze[0].length - 1)) {
					createMaze[x][y] = "|"; // this creates the downwards wall, as long as it doesnt go past the length
					// starting index at (1,1) because we have the wall at (0,1) and the corner at (0,0)
				}
				else if(current != null) { // 	THIS CREATES THE ENTIRE BORDER OF THE MAZE WITHOUT THE ROADS INSIDE OR THE CORNERS
					if (current.west) { // if we are at the left wall
						createMaze[x][y - 1] = "|";
					}
					else {
						createMaze[x][y - 1] = " "; // just a normal space since its inside the maze
					}
					if(current.east) {
						createMaze[x][y + 1] = "|"; // if we're at the right wall
					}
					else {
						createMaze[x][y + 1] = " ";
					}
					if(current.north) {
						createMaze[x - 1][y] = "--";// if we're at the top
					}
					if(current.south) {
						createMaze[x + 1][y] = "--";// if we're at the bottom
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
						createMaze[x][y] = "##"; // supposed to map out the direction of the solution for the maze
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
		return string; // returns the representation of the solved maze for bfs and dfs
	}
}
