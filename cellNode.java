import java.util.ArrayList;

//This class monitors the cells in the maze.
public class cellNode {
	boolean visited;
	boolean north, south, west, east;
	int x, y;
	cellNode next;
	Integer visitedNodes, distance;
	colors COLOR;
	ArrayList<cellNode> neighbor;
	int startTime, endTime;
	
	//Constructor for the cells in the maze
	public cellNode(int x, int y) {
		neighbor = new ArrayList<cellNode>();
		this.x = x;
		this.y = y;
		north = south = east = west = true;
		COLOR = colors.WHITE;
		visitedNodes = distance = startTime = endTime = 0;
		next = null;
		visited = false;
	}
	
	public boolean getVisited() {
		return visited; // returns teur or false based on if the room was visited
	}
	
	public boolean getWalls() {
		return neighbor.size() == 0;
	}
	
	public void setVisited() {
		visited = true;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		cellNode that = (cellNode) o;
		return that.x == x && that.y == y;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")"; // returns the coordinates
	}
	
	//Breaks down wall in the maze
	public void breakWall(cellNode current) {
		if(current.x == x - 1 && current.y == y) {	//North
			north = false;				//
			current.south = false;			//
			neighbor.add(current);			//Add coorindates to an arraylist
			current.neighbor.add(this);		//
		}
		if(current.x == x + 1 && current.y == y) {	//South
			south = false;
			current.north = false;
			neighbor.add(current);
			current.neighbor.add(this);
		}
		if(current.x == x && current.y == y + 1) {	//East
			east = false;
			current.west = false;
			neighbor.add(current);
			current.neighbor.add(this);
		}
		if(current.x == x && current.y == y - 1) {	//West
			west = false;
			current.east = false;
			neighbor.add(current);
			current.neighbor.add(this);
		}
	}
}
