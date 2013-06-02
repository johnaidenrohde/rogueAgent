/*********************************************
 *  Agent.java                               *
 *  Agent for Text-Based Adventure Game      *
 *  John Aiden Rohde (z3343276)              *
 *  Sam Basset (z3372468)                    *
 *  COMP3411 Artificial Intelligence         *
 *  UNSW Session 1, 2013                     *
 *********************************************/


import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;

public class Agent {

   final static int EAST   = 0;
   final static int NORTH  = 1;
   final static int WEST   = 2;
   final static int SOUTH  = 3;

   final static char PROMPT_USER = 'X';
   final static char WALK_DONE = 'D';

   final static int MAX_SEARCH = 10000;
   final static int MAX_EXPLORATION = 250;

   final static int WALKING = 0;
   final static int X_HUNT = 1;
   final static int PATHFINDING = 2;

   private Map     map;

   // initial row and column
   private Point   startPoint;

   // current row, column and direction of agent
   private Point   currPoint;
   private int     dirn;

   // variable for planning
   private LinkedList<Character> mission;
   private boolean onMission = false;
   private int missionStep;
   private boolean walkDone = false;

   private boolean firstRun    = true;

   private boolean willAdvance = false;
   private Point   startWalk;

   private boolean have_axe            = false;
   private boolean have_key            = false;
   private boolean have_gold           = false;
   private int     num_dynamites_held  = 0;

   private boolean game_won  = false;
   private boolean game_lost = false;

   private int     numTurns  = 0;
   private int     numWalking= 0;

   //This is each tile we will want to use dynamite on
   private Vector<Point> toDynamite;

/*******************************************************************************
 * MAIN AI FUCNTION *
 ******************************************************************************/


   public char get_action(char[][] view) {

      int ch=0;
      char action = PROMPT_USER;

      if (missionStep == WALKING) {
         action = walk();
         numWalking++;
      } else if (missionStep == X_HUNT || missionStep == PATHFINDING) {
         action = gamePlan();
      }

      if (numWalking >= MAX_EXPLORATION) {
         missionStep = PATHFINDING;
      }

      // //intially explore the map for 200 turns
      // if(numTurns < MAX_EXPLORATION){
      //    action = walk();
      // }else{
      //    //Then work out what strategy we want to take
      //    action = gamePlan();
      // }

      //Otherwise prompt for input
      if (action == PROMPT_USER) {
         // THE FOLLOWING FOR DEBUGGING ONLY:
         if (numTurns > MAX_SEARCH) {
            System.out.println("Max search moves exceeded!");
         } else {
            System.out.println("Walk completed! Found:");
         }
         Point axe, gold, key;
         Vector<Point> dynamite;
         axe = map.getAxe();
         gold = map.getGold();
         key = map.getKey();
         dynamite = map.getDynamite();

         if (axe != null) {
            System.out.println("Axe at row:" + axe.row + " col:" + axe.col);
         }
         if (gold !=null) {
            System.out.println("Gold at row:" + gold.row + " col:" + gold.col);

         }
         if (key != null) {
            System.out.println("Key at row:" + key.row + " col:" + key.col);
         }
         if (dynamite != null) {
            System.out.println("Last dynamite at row:" + dynamite.lastElement().row + " col:" + dynamite.lastElement().col);
         }
         // END DEBUGGING

         System.out.print("Enter Action(s): ");
         try {
            while ( ch != -1 ) {
               // read character from keyboard
               ch  = System.in.read();

               switch( ch ) { // if character is a valid action, return it
                  case 'F': case 'L': case 'R': case 'C': case 'O': case 'B':
                  case 'f': case 'l': case 'r': case 'c': case 'o': case 'b':
                  action = (char) ch;
               }
            }
         }

         catch (IOException e) {
            System.out.println ("IO error:" + e );
         }
      }
      numTurns = numTurns + 1;
      return action;
   }

/*******************************************************************************
 * EXPLORATION AND HELPER FUNCTIONS *
 ******************************************************************************/

   /*
    * If nothing obvious available, continue on current path until an obstacle is encountered.
    * Then turn and continue straight.
    */
   private char walk() {
      if (firstRun && map.isWalkable(map.getTileInDirection(dirn, currPoint))) {
         return 'F';
      }

      if (firstRun) {
         // forwards isn't walkable, turn right.
         firstRun = false;
         startWalk = new Point(currPoint.row, currPoint.col);
         return 'R';
      } else if (!willAdvance) {
         // this is where the wall following happens
         // ensure wall is to the left
         Point adjacent = map.getTileInDirection(getDirectionFromTurn('L'), currPoint);
         Point next = map.getTileInDirection(dirn, currPoint);
         if (map.isWalkable(adjacent)) {
            // turn)towards and advance.
            willAdvance = true;
            return 'L';
         }
         if (!map.isWalkable(adjacent) && map.isWalkable(next)) {
            // wall present on left, can advance => advance
            return 'F';
         }
         if (!map.isWalkable(adjacent) && !map.isWalkable(next)) {
            // must make a right turn to keep wall on left.
            return 'R';
         }
      }
      if (willAdvance && map.isWalkable(map.getTileInDirection(dirn, currPoint))) {
         willAdvance = false;
         return 'F';
      }
      return PROMPT_USER;
   }

   private int getDirectionFromTurn(char turnDirection) {
      if (turnDirection == 'L') {
         return (dirn + 1) % 4;
      } else {
         return (dirn - 1) % 4;
      }
   }

/*******************************************************************************
 * GAME PLAYING LOGIC AND HELPER FUNCTIONS *
 ******************************************************************************/



   /* The central function which handles the basic intelligence of the game.
    * Essentially it starts the game by filling in all of the map that it can
    * and then tries to construct a plan that will allow it to win the game.
    *
    */
   private char gamePlan(){
      char result;
      //if we are currently walking a path just return the next character
      if(onMission){
         if(!mission.isEmpty()){
            System.out.println("Current mission = " + mission.toString());
            if (mission.peek() == 'F' &&
                  !map.isWalkable(map.getTileInDirection(dirn, currPoint))) {
               // A* out of sync with world, for example an obstacle has
               // been revealed where there used to be an X. Try again.
               System.out.println("A* out of sync");
               mission.clear();
               onMission = false;
            } else {
               return(mission.poll());
            }
         } else {
            onMission = false;
         }
      }
      // We need a new mission
      if (walkDone) { //Main game plan
         System.out.println("Got to the planning stage");
         if (have_gold) {
            Vector<Point> pathBack = Astar.findPath(currPoint, startPoint, map);
            mission = getMoves(pathBack);
            onMission = true;
            return mission.poll();
         }if (have_key) {
            // all doors considered opened, change isWalkable
            map.makeWalkable('-');
         }if (have_axe){
            // all trees can be removed from the map.
            map.makeWalkable('T');
         }

         ///Try to grab any and all useful stuff
         result = collectGoodies();
         if(result != '?'){
            return result;
         }

         /*
         if( num_dynamites_held > 0 ){
            //This calls a function that dynamites an item on the map
            Point mightDynamite = map.dynamite;
            while(  != null){
               result = collectGoodies();
               if(result != '?'){
                  toDynamite.add(mightDynamite);
                  return result;
               }
               mightDynamite = map.dynamite;
            }
         }*/



         //The system has found nothing to do, so get the trees!
         Vector<Point> burnEverything = map.getTrees(currPoint);
         int numTrees = burnEverything.size();
         for (int i = 0; i < numTrees; i++) {
            result = tryGet(burnEverything.get(i), map);
            return result;
         }
         // Now I bet there's more exploring to do...
         missionStep = WALKING;
         firstRun = true;
         numWalking = 0;
         firstRun = true;

         //Now we really have nothing to do
         return('?');
      }else if(!walkDone){ // We still have unexplored regions, so we explore them
         System.out.println("Still exploring");
         Vector<Point> path;
         Vector<Point> x = map.findGroupsX(currPoint);
         int size = x.size();
         for (int i = 0; i < x.size(); i++) {
            System.out.println("X group has " + x.size());
            //Chart a path to the group of X's
            result = tryGet(x.get(i), map);
            if(result != PROMPT_USER){ // If you can reach the group
               return(result);
            }
            //System.out.println("No path to that one");
         }
         // If there is no group of Xs we can reach
         System.out.println("Done Exploring");
         walkDone = true;
      }
      // Place holder move
      return('?');
   }

   /* Try to get the given goal node
    *
    * @param goal: Where you want to go, map: What you can see
    * @return Set the mission and return the first charater or PROMPT_USER
    * */
   private char tryGet( Point goal, Map map ){
      //Can we get the gold from where we are standing
      Vector<Point> path;
      path = Astar.findPath(currPoint, goal, map);
      if (path != null) {
         // Find a path to what you want
         mission = getMoves(path);
         System.out.println("Path found = " + mission.toString());
         onMission = true;
         // Return the first step on our triumphant journey
         if (mission == null || mission.size() == 0) {
            return PROMPT_USER;
         }
         return mission.poll();
      } else {
         return PROMPT_USER;
      }

   }

   /* Attempt to collect all the important pieces
    *
    * @return the first move or a X if no moves can be found
    */
   private char collectGoodies(){
      // Find the location of all essential pieces on board:
      char result;
      Point axe, gold, key;
      Vector<Point> dynamite;
      axe = map.getAxe();
      gold = map.getGold();
      key = map.getKey();
      dynamite = map.getDynamite();

      // First try for gold then axes then keys
      if (gold != null) {
         result = tryGet(gold, map);
         if( result != PROMPT_USER){
            System.out.println("Found path to gold!!!!");
            return result;
         }
      }
      if( !have_axe && axe != null){
         result = tryGet(axe, map);
         if( result != PROMPT_USER){
            System.out.println("Found a path to axe");
            return result;
         }
      }
      if( !have_key && key != null){
         result = tryGet(key, map);
         if( result != PROMPT_USER){
            System.out.println("Found a path to key");
            return result;
         }
      }
      if( (num_dynamites_held < dynamite.size()) && dynamite != null){
         for(int i = 0; i < dynamite.size(); i++){
            result = tryGet(key, map);
            if( result != PROMPT_USER){
               System.out.println("Found a path to key");
               return result;
            }
         }
      }
      return('?');
   }


   /* translate a vector of points into a list of moves
    *
    * */
   private LinkedList<Character> getMoves(Vector <Point> p) {
      LinkedList<Point> points = new LinkedList<Point>(p);
      Collections.reverse(points);

      LinkedList<Character> list = new LinkedList<Character>();
      Point nextPoint, previousPoint;
      int nextDirection = NORTH;
      int dRow, dCol;
      int currentDirection = dirn;
      previousPoint = currPoint;

      while (!points.isEmpty()) {
         // given vector is actually in reverse
         nextPoint = points.poll();
         // System.out.println("currPoint = [" + currPoint.row + "," +
         //          currPoint.col + "]");
         // System.out.println("Current Direction: " + currentDirection);
         dRow = nextPoint.row - previousPoint.row;
         dCol = nextPoint.col - previousPoint.col;
         // check that the square is adjacent
         if(Math.abs(dRow + dCol) != 1){
            System.out.println("Something fishy going on here");
            System.out.println("Problem point to add = [" + nextPoint.row + ","
                  + nextPoint.col + "]");
            previousPoint = nextPoint;
         }else{
            // Find the next direction
            if (nextPoint.row == previousPoint.row - 1) {
               nextDirection = NORTH;
            } else if (nextPoint.row == previousPoint.row + 1) {
               nextDirection = SOUTH;
            } else if (nextPoint.row == previousPoint.row) {
               if (nextPoint.col == previousPoint.col + 1) {
                  nextDirection = EAST;
               } else if (nextPoint.col == previousPoint.col - 1) {
                  nextDirection = WEST;
               }
            }
            //Turn in that direction
            while( nextDirection != currentDirection) {

               list.add('L');
               currentDirection = (currentDirection + 1) % 4;
            }

            //Work out how to go forward
            Point toEnter = map.getTileInDirection(currentDirection, currPoint);
            //If the point was on our list to dynamite we blow it up
            if( toDynamite.contains(toEnter)){
               list.add('D');
               list.add('F');
            } //If we come up to a door we open it
            else if (map.getTileInDirection(currentDirection, currPoint).value
                  == '-' && have_key) {
               list.add('O');
               list.add('F');
            } // Chop down any trees we come to
            else if (map.getTileInDirection(currentDirection, currPoint).value
                  == 'T' && have_axe) {
               list.add('C');
               list.add('F');
            }  //Otherwise we continue on our merry way
            else {
               list.add('F');
            }
            previousPoint = nextPoint;
         }
      }
      return list;
   }

/*******************************************************************************
 * GENERAL HELPER FUNCTIONS *
 ******************************************************************************/

   void print_view(char view[][] )
   {
      int i,j;

      System.out.println("\n+-----+");
      for( i=0; i < 5; i++ ) {
         System.out.print("|");
         for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
               System.out.print('^');
            }
            else {
               System.out.print( view[i][j] );
            }
         }
         System.out.println("|");
      }
      System.out.println("+-----+");
   }
   /****************************************************************
    * DISCLAIMER: update_world and update_map are inspired in part *
    * by code supplied in Rogue.java                               *
    ****************************************************************/

   //Update the world view based on the previous action taken
   private boolean update_world( char action, char view[][] ){
      int d_row, d_col;
      int new_row, new_col;
      char ch;
      Point p;

      // run the first time only
      if ( action == 'i') {
         init_world(view);
         return( true );
      }

      // make changes to direction only if dir'n changed
      if (( action == 'L' )||( action == 'l' )) {
         dirn = ( dirn + 1 ) % 4;
         currPoint.dirn = dirn;
         return( true );
      } else if (( action == 'R' )||( action == 'r' )) {
         dirn = ( dirn + 3 ) % 4;
         currPoint.dirn = dirn;
         return( true );
      } else {
         // if direction not changed, look ahead 1 space to see what's there
         p = map.getTileInDirection(dirn, currPoint);
         ch = p.value;
      }

      // if no direction changes to be made:
      switch( action ) {
      case 'F': case 'f': // forward
         switch( ch ) {   // can't move into an obstacle or water
            case '*': case 'T': case '-': case '~':
            System.out.println("Illegal forwards move made");
            return( false );
         }
         map.setTile(new Point(currPoint.row, currPoint.col, ' ')); // clear current location
         currPoint.row = p.row;
         currPoint.col = p.col;
         currPoint.dirn = dirn;
         map.setTile(new Point(currPoint.row, currPoint.col, ' ')); // clear new location

         switch( ch ) {
            case 'a':
            have_axe  = true;     break;
            case 'k':
            have_key  = true;     break;
            case 'g':
            have_gold = true;     break;
            case 'd':
            num_dynamites_held++; break;
            case '~':
            game_lost = true;     break;
         }
         if( have_gold &&( currPoint.row == startPoint.row )&&( currPoint.col == startPoint.col )) {
            game_won = true;
         }
         map.updateMap(view, currPoint);
         return( true );
         case 'L': case 'R': case 'C': case 'O': case 'B':
         case 'l': case 'r': case 'c': case 'o': case 'b':
         map.updateMap(view, currPoint);
         return(true);
      }
      return(false);
   }


/*******************************************************************************
 * INITIAL GAME SETUP AND OVERALL GAME LOOP *
 ******************************************************************************/

   //Perform the inital setup of the agent and the world map
   private void init_world(char view[][] ) {

      //The intial setup of agent position
      currPoint = new Point(100,100);
      startPoint = new Point(100,100);

      //The way the agent is facing is considered north
      dirn = NORTH;

      //Initialize boths maps
      map = new Map(200,200,Map.UNVISITED);
      map.updateMap(view, currPoint);

      //and the list of places to dynamite
      toDynamite = null;
   }

   public static void main( String[] args ){
      InputStream  in = null;
      OutputStream out= null;
      Socket socket   = null;
      Agent  agent    = new Agent();
      char   view[][] = new char[5][5];
      //Special action i used to intialize the map
      char   action   = 'i';
      int port;
      int ch;
      int i,j;

      if( args.length < 2 ) {
         System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );

      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }

      //GAME LOOP
      try { // scan 5-by-5 wintow around current location
         while( true ) {
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     if( ch == -1 ) {
                        System.exit(-1);
                     }
                     view[i][j] = (char) ch;
                  }
               }
            }
            agent.update_world( action, view ); //Update the map and other things
            //agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
            action = agent.get_action(view);
            System.out.println("Making move: " + action);
            out.write( action );
         }
      }
      catch( IOException e ) {
         System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }
}
