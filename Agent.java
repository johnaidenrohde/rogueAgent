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

   final static int MAX_SEARCH = 500;

   private Map     map;

   // initial row and column
   private Point   startPoint;

   // current row, column and direction of agent
   private Point   currPoint;
   private int     dirn;

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


   public char get_action(char[][] view) {

      int ch=0;

      char action = walk();
      Vector<Point> x = map.findGroupsX();
      for (int i = 0; i < x.size(); i++) {
         Point y = x.get(i);
         System.out.println("X group at: row="+y.row+", col="+y.col);
      }
      //char action = PROMPT_USER;
      if (action == PROMPT_USER || numTurns > MAX_SEARCH) {
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
         if (gold != null) {
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
      numTurns++;
      return action;
   }

   /* A* with limited visibility. To execute, need a couple of data structures:
    * OPEN list can be represented as a Priority Queue
    * CLOSED list could be a HashMap (to maybe speed up checking) or simple Vector
    */

   /* The hard part here is choosing a good heuristic, especially since most of the
    * map won't be immediately visible. Maybe prioritise exploration unless a tool
    * is easily reachable or an obstacle is surmountable. Wrong usage of dynamite is
    * especially fatal to getting a good solution.
    *
    * If nothing obvious available, continue on current path until an obstacle is encountered. 
    * Then turn and continue straight. 
    */

   

   private char walk() {
      if (firstRun && isWalkable(map.getTileInDirection(dirn, currPoint))) {
         return 'F';
      } 
      // this needs a rethink. Left-hand wallfollowing with Pledge.
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
         if (isWalkable(adjacent)) {
            // turn towards and advance.
            willAdvance = true;
            return 'L';
         }
         if (!isWalkable(adjacent) && isWalkable(next)) {
            // wall present on left, can advance => advance
            return 'F';
         }
         if (!isWalkable(adjacent) && !isWalkable(next)) {
            // must make a right turn to keep wall on left.
            return 'R';
         }
      }
      if (willAdvance && isWalkable(map.getTileInDirection(dirn, currPoint))) {
         willAdvance = false;
         return 'F';
      }
      return 'X';
   }

   private int getDirectionFromTurn(char turnDirection) {
      if (turnDirection == 'L') {
         return (dirn + 1) % 4;
      } else {
         return (dirn - 1) % 4;
      }
   }

   private boolean isWalkable(Point p) {
      char tile = p.value;
      switch(tile) {
         case '*': case 'T': case '-': case '~':
         return false;
         default:
         return true;
      }
   }

   // translate a vector of points into a list of moves
   private LinkedList getMoves(Vector<Point> p) {
      LinkedList list = new LinkedList();
      Iterator<Point> i = p.iterator();
      // needs to consider agent direction at each point as well as 
      // absolute direction.
      int nextDirn;
      while (i.hasNext()) {
         int currentDirection = dirn;
         nextDirn = map.getDirection(i.next(), currPoint);
         if (nextDirn < map.NORTH_EAST) {
            // one of four cardinal directions, easy. 
            while (currentDirection != nextDirn) {
               // turn left until we're facing the right way
               list.add('L');
               currentDirection = (currentDirection + 1) % 4;
            }
            list.add('F');
         } else {
            /****************************************
             * ASSUMING NO DIAGONALS, FIX OTHERWISE *
             ****************************************

            // one of four "halfway" directions, requires some Manhattan traversal
            // two choices - change row first or change col first. 
            switch (nextDirn) {
               case map.NORTH_EAST:
                  break;
               case map.NORTH_WEST:
                  break;
               case map.SOUTH_EAST:
                  break;
               case map.SOUTH_WEST:
                  break;
            }
            */
         }
      }
      return list;
   }

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
         return( true );
      } else if (( action == 'R' )||( action == 'r' )) {
         dirn = ( dirn + 3 ) % 4;
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


   //Perform the inital setup of the agent and the world map
   private void init_world(char view[][] ) {

      //The intial setup of agent position
      currPoint = new Point(100,100);
      startPoint = new Point(100,100);

      //The way the agent is facing is considered north
      dirn = NORTH;

      //Initialize the map
      map = new Map(200,200,Map.UNVISITED);
      
      map.updateMap(view, currPoint);
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
            agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
            action = agent.get_action(view);
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
