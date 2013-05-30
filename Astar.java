import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Vector;
import java.io.*;

/*
 * Class to implement pathfinding from one point on the map to another.
 *
 * Heavily based on the notes found at
 * http://theory.stanford.edu/~amitp/GameProgramming/ImplementationNotes.html
 *
 */

public class Astar
{

   /* Finds the shortest distance between two points on a given map. Does not
    * allow for diagonal movement.
    *
    * @param Two points, curr and goal, and the map
    * @return the path as a vector of points starting at the current position
    */
   public Vector<Point> findPath( Point start, Point goal, Map map ){

      Point curr;
      Point neighbor;

      // Distance from start
      int cost;
      int tempGScore;

      //priority queue for squares to be invesitgated
      PriorityQueue<Point> openSet;

      //Already investigated squares
      ArrayList<Point> closedSet;

      //Setup the openSet
      Comparator<Point> comparator = new PointComparator();
      openSet  = new PriorityQueue<Point>(640, comparator);

      //Setup closed set
      closedSet = new ArrayList<Point>(6400);

      curr = start;

      // Add the start node to the open set
      curr.setParent(null);
      curr.setF(manDistance(curr, goal));
      curr.setG(0);
      openSet.add(curr);


      while( openSet.peek() != null ){

         curr = openSet.poll();

         //If we have arrived at the goal
         if((curr.row == goal.row) && (curr.col == goal.col)){
            return tracePath(curr);
         }

         closedSet.add(curr);

         //For each neighbor node
         for(int i = 4; i >= 0; i = i - 1){
            neighbor = map.getTileInDirection(i, curr);
            // We only consider walkable nodes
            if(map.isWalkable(neighbor)){
               //The total length of the path to each neighbor node
               cost = curr.g + 1;
               //Remove this neighbor because the current path is better
               if((openSet.contains(neighbor))&&(cost < neighbor.g)){
                  openSet.remove(neighbor);
               }
               //Shouldn't happen too often
               /*if((closedSet.contains(neighbor))&&(cost < neighbor.g)){
                  closedSet.remove(neighbor);
               }*/
               //If we haven't considered this square it yet
               if(!(closedSet.contains(neighbor))&& !(openSet.contains(neighbor))){
                  neighbor.setG(cost);
                  neighbor.setF(manDistance(neighbor, goal) + cost);
                  neighbor.setParent(curr);
                  openSet.add(neighbor);
               }
            }
         }
      }
      //If no path was found
      System.out.println ("Error: No Path Found");
      return null;

   }

   /* Backtracks along the path traveled
    *
    * @param a point with a parent to follow
    * @return a set of points traveled
    */
   private Vector<Point> tracePath( Point toFollow  ){
      Vector<Point> toReturn = new Vector<Point>(640);
      while(toFollow.parent != null){
         toReturn.add(toFollow);
         toFollow = toFollow.parent;
      }
      return toReturn;
   }

   /* Calculates the Manhattan Distance to the goal
    *
    * @return distance from currPos to goal
    */
   private int manDistance(Point curr, Point goal){
      int r, c;
      r = Math.abs(curr.row - goal.row);
      c = Math.abs(curr.col - goal.col);
      return(r+c);
   }

}
