import static org.junit.Assert.*;
import org.junit.Test;

public class mazeTest {

	@Test
	public void testMaze() {
		maze example1 = new maze(4);
		maze example2 = new maze(4);
		example1.DFS();
		example2.BFS();
		assertEquals(example1.toString(), example2.toString());
	}
}
