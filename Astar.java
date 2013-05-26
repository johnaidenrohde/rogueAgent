import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

/*
 * Class to implement pathfinding from one point on the map to another.
 *
 * Heavily based on the notes found at
 * http://theory.stanford.edu/~amitp/GameProgramming/ImplementationNotes.html
 *
 */

public class Astar
{

   private Map map;

   private Point goal;
   private Point start;
   private Point curr;

   //priority queue for squares to be invesitgated
   private PriorityQueue<Point> openSet;

   //Already investigated squares
   private ArrayList<Point> closedSet;

   private int


   /* Finds the shortest distance between two points on a given map
    *
    * @param Two points, curr and goal, and the map
    * @return the path as a vector of points starting at the current position
    */
   public Vector<Point> findPath( Point start, Point goal, Map givenMap ){
      this.map = givenMap;
      this.start = start;
      this.curr = start;
      this.goal = goal;
      //If the current best node on the list is the goal we are done
      while( openSet.peek() != goal ){
          for(int i=4; i = 4<)

            get
      }
      //At the end we track our way back to the start via the parent pointer
      return tracePath();

   }

   /* Backtracks along the path traveled
    *
    * @param a point with a parent to follow
    * @return a set of points traveled
    */
   private Vector<Point> tracePath(){

   }

   /* Calculates the Manhattan Distance to the goal
    *
    * @return distance from currPos to goal
    */
   private int manDistance(){
      int r, c;
      r = Math.abs(curr.row - goal.row);
      c = Math.abs(curr.col - goal.col);
      return(r+c);
   }

}
