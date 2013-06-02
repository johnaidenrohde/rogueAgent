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
    public static final int MAX_ITERATIONS = 10000;

   /* Finds the shortest distance between two points on a given map. Does not
    * allow for diagonal movement.
    *
    * @param Two points, curr and goal, and the map
    * @return the path as a vector of points starting at the current position
    */
   public static Vector<Point> findPath( Point start, Point goal, Map map ){

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
      openSet  = new PriorityQueue<Point>(6400, comparator);

      //Setup closed set
      closedSet = new ArrayList<Point>(6400);

      curr = start;

      // Add the start node to the open set
      if (goal == null || curr == null) {
              // System.out.println("Null goal : curr=" + curr.row +", "+curr.col);
      }
      curr.setParent(null);
      curr.setF(manDistance(curr, goal));
      curr.setG(0);
      openSet.add(curr);
      int iterations = 1;

      //What am I searching for
      // System.out.println ("Goal point " + goal.row + ","
      //                 + goal.col + " " + goal.value);
      // System.out.println ("Start point " + start.row + ","
      //                 + start.col + " " + start.value);

      while( openSet.size() != 0 && iterations < MAX_ITERATIONS  ){

         if( iterations == (MAX_ITERATIONS - 1)){
            System.out.println("Failed due to iterations");
            System.out.println("Open set contains " + openSet.size());
         }
         iterations++;
         // print progress
      //map.rewrite(curr, '@');
      //map.printMap(curr);


         curr = openSet.poll();

         //If we have arrived at the goal
         if((curr.row == goal.row) && (curr.col == goal.col)){
            return tracePath(curr);
         }

         closedSet.add(curr);
         //For each neighbor node
         for(int i = 0; i < 4; i = i + 1){
            neighbor = map.getTileInDirection(i, curr);
          
            // We only consider walkable nodes
            if(map.astarIsWalkable(neighbor)){
               //The total length of the path to each neighbor node
               cost = curr.g + manDistance(curr, neighbor);
               //Remove this neighbor because the current path is better
               if((openSet.contains(neighbor))&&(cost < neighbor.g)){
                  openSet.remove(neighbor);
               }
               if((closedSet.contains(neighbor))&&(cost < neighbor.g)){
                  closedSet.remove(neighbor);
               }
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
      // System.out.println ("Error: No Path Found");
      return null;

   }

   /* Backtracks along the path traveled
    *
    * @param a point with a parent to follow
    * @return a set of points traveled
    */
   private static Vector<Point> tracePath( Point toFollow  ){
      Vector<Point> toReturn = new Vector<Point>(640);
      while(toFollow.parent != null){
        //System.out.println("Tracepath: "+ toFollow.row + ","
          //                      + toFollow.col);
         toReturn.add(toFollow);
         toFollow = toFollow.parent;
      }
      return toReturn;
   }

   /* Calculates the Manhattan Distance to the goal
    *
    * @return distance from currPos to goal
    */
   public static int manDistance(Point curr, Point goal){
      if (curr == null || goal == null) {
        // System.out.println("Considering a null point");
        // try {
        //   System.out.println("Curr: " + curr.row + " ," + curr.col);
        // } catch (Exception e) {
        //   System.out.println("Curr");
        // }
        // try {
        //   System.out.println("Goal :" + goal.row + " ," + goal.col);
        // } catch (Exception e) {
        //   System.out.println("Goal");
        // }
      }
      int r, c;
      r = Math.abs(curr.row - goal.row);
      c = Math.abs(curr.col - goal.col);
      return(r+c);
   }

}
