
package ai;
import kalaha.GameState;
import java.util.ArrayList;
import java.util.Random;

public class Node
{
    private ArrayList<Node> m_Children;
    private final GameState m_State; //A smaller gamestate might be an option to optimize memory.
    private final Node m_Parent; // not sure if needed. not needed as of yet.
    private boolean m_Leaf; 
    private int m_Depth;
    private final int m_MaxNrOfChildren;
    private int m_Utility;
    private final boolean m_Max; //true if it's max turn, false if it min's turn
    private final int m_Move;
    
    public Node(Node p_Parent, GameState p_GameState, int p_Depth, boolean p_Max, int p_Ambo)
    {
        m_Children = new ArrayList();
        m_State = p_GameState;
        m_Parent = p_Parent;
        m_Leaf = false;
        m_Depth = p_Depth;
        m_MaxNrOfChildren = 6;
        m_Utility = 0;
        m_Max = p_Max;
        m_Move = p_Ambo;
    }
    
    public void depthFirst(int p_Depth)
    {
       //only expand to specified depth
       if(m_Depth < p_Depth)
       {
            for(int i = 1; i < m_MaxNrOfChildren + 1; i++)
            {
                GameState gs = m_State.clone();
                if(gs.moveIsPossible(i))
                {
                    int prevPlayer = gs.getNextPlayer();
                    gs.makeMove(i);
                    Node child;
                    if(gs.getNextPlayer() != prevPlayer)   
                        child = new Node(this, gs, (m_Depth + 1), !m_Max, i);
                    else
                        child = new Node(this, gs, (m_Depth + 1), m_Max, i);
                    child.depthFirst(p_Depth);
                    m_Children.add(child);                       
                }
            }
            if(m_Children.isEmpty())
                calcUtility();
        }
        else
            calcUtility();
       //Propegate utility value from children nodes.
       if(!m_Leaf)
       {
           propegateUtility();
       }
    }
    
    public void depthFirst(int p_Depth, double p_Time)
    {
       //only expand to specified depth
       long time = System.currentTimeMillis();
       if(m_Depth < p_Depth && p_Time < 4.8)
       {
            for(int i = 1; i < m_MaxNrOfChildren + 1; i++)
            {
                GameState gs = m_State.clone();
                if(gs.moveIsPossible(i))
                {
                    int prevPlayer = gs.getNextPlayer();
                    gs.makeMove(i);
                    Node child;
                    if(gs.getNextPlayer() != prevPlayer)   
                        child = new Node(this, gs, (m_Depth + 1), !m_Max, i);
                    else
                        child = new Node(this, gs, (m_Depth + 1), m_Max, i);
                    long time2 = System.currentTimeMillis() - time;
                    double elapsedTime = (double)time2 / 1000.0;
                    child.depthFirst(p_Depth, elapsedTime + p_Time);
                    m_Children.add(child);                       
                }
            }
            if(m_Children.isEmpty())
                calcUtility();
        }
        else 
            calcUtility();
       
       //If this is not a leaf node, choose the correct utility value
       //to propegate from this nodes children depending on min, max;
       if(!m_Leaf)
       {
           propegateUtility();
       }
    }
    
    private void propegateUtility()
    {
        //If it's mins turn get the lowest utility value
        if(!m_Max)
        {
            m_Utility = m_Children.get(0).m_Utility;
            for(Node n : m_Children)
            {
                if(m_Utility > n.m_Utility)
                    m_Utility = n.m_Utility;
            }

        }
        //If it's max turn get the highest utility value
        else
        {
            m_Utility = m_Children.get(0).m_Utility;
            for(Node n : m_Children)
            {
                if(m_Utility < n.m_Utility)
                    m_Utility = n.m_Utility;
            }
        }
        if(m_Parent != null)
            m_Children.clear();
    }
    
    public int iterativeDeepening(int p_Depth)
    {
        int i = p_Depth;
        double elapsedTime = 0.0;
        int bestMove = 1;
        while(elapsedTime < 4.8)//while tme is not 5 secs.
        {
            //Save the best move from last search,
            //If this is the fisrt iteraion of the loop
            //no search has been performed and getMove() returns the first
            //possible move;
            bestMove = getMove();
            //Clear the nodes from the last iteration.
            m_Children.clear();
            long startT = System.currentTimeMillis();
            
            depthFirst(i, elapsedTime);
            
            long tot = System.currentTimeMillis() - startT;
            elapsedTime += (double)tot / 1000.0;
            //increase the depth
            i += 1;
        }
        return bestMove;
    }
    
    private void calcUtility()
    {   
        m_Leaf = true;
        //player1 is the AI player
        int player1;
        //player2 is the opponent
        int player2;
        

        if(m_Max)
        {
            player1 = m_State.getNextPlayer();
            player2 = m_State.getOppositePlayer();
        }
        else
        {
            player1 = m_State.getOppositePlayer();
            player2 = m_State.getNextPlayer();
        }
        
        int gameEnd = m_State.getWinner();
        
        if(gameEnd == -1)
        {
            //If the node is not a terminal state, calculate a utility based on score
            int ScorePlayer1 = m_State.getScore(player1);
            int ScorePlayer2 = m_State.getScore(player2);
            if(ScorePlayer1 > ScorePlayer2)
                m_Utility += 7;
            else if(ScorePlayer1 < ScorePlayer2)
                m_Utility -= 7;
            
            for(int i = 1; i < 7; i++)
            {
                //Check to see if we have any ambo that has enough seeds to
                //end in the house which gives us a extra turn.
                if(m_State.getSeeds(i, player1) != 0)
                {
                    if((7 - i) == m_State.getSeeds(i, player1))
                        m_Utility += 5;
                }
                else
                {
                    //If an ambo is empty we check if the opposite player has any 
                    //seeds to steal in their ambo opposite of our empty ambo.
                    if(m_State.getSeeds(i, player2) > 0)
                    {
                        //loop through ambos prior to the empty ambo to see if
                        //any of the ambos has the correct number of seeds to land
                        //in the empty ambo.
                        for(int j = 1; j < i; j++)
                        {
                            if((i - j) == m_State.getSeeds(j, player1))
                                m_Utility += 3;
                        }
                    }
                        
                }
                if(m_State.getSeeds(i, player2) != 0)
                {
                    if((7 - i) == m_State.getSeeds(i, player2))
                    {
                        m_Utility -= 5;
                    }
                    else
                    {
                        //If an ambo is empty we check if the opposite player has any 
                        //seeds to steal in their ambo opposite of our empty ambo.
                        if(m_State.getSeeds(i, player1) > 0)
                        {
                            //loop through ambos prior to the empty ambo to see if
                            //any of the ambos has the correct number of seeds to land
                            //in the empty ambo.
                            for(int j = 1; j < i; j++)
                            {
                                if((i - j) == m_State.getSeeds(j, player2))
                                    m_Utility -= 3;
                            }
                        }

                    }
                }
            }        
        }
        else if (gameEnd == player1)
            m_Utility = 50;
        else if(gameEnd == player2)
            m_Utility = -50;
        
        
    }                                                      
    
    public int getMove()
    {
        //Saftey if-statement to check that we actually have a child to get a move from
        // if no childs are found loop through the gamestate to find the first
        //possible move.
        if(m_Children.size() > 0)
        {
            int baseUtil = m_Children.get(0).m_Utility;
            ArrayList<Integer> bestMoves;
            bestMoves = new ArrayList();
            
            //Find the highest utility value
            for(int i = 1; i < m_Children.size(); i++)
            {
                if(baseUtil < m_Children.get(i).m_Utility)
                {
                    baseUtil = m_Children.get(i).m_Utility;
                }
            }
            //Check if more than one node has the same utility
            for(int i = 0; i < m_Children.size(); i++)
            {
                if(m_Utility == m_Children.get(i).m_Utility)
                {
                    bestMoves.add(m_Children.get(i).m_Move);
                }
            }
            
            return bestMoves.get(randInt(0, bestMoves.size()-1)); 
        }
        else
        {
            for(int i = 1; i < 7; i++)
                if(m_State.moveIsPossible(i))
                    return i;
            
            //if no possible moves is found return 1
            //if this happends something should be wrong.
            return 1;
        }
    }
    
 
   private int randInt(int min, int max) {

       // NOTE: Usually this should be a field rather than a method
       // variable so that it is not re-seeded every call.
       Random rand = new Random();
       
       // nextInt is normally exclusive of the top value,
       // so add 1 to make it inclusive
       int randomNum = rand.nextInt((max - min) + 1) + min;

       return randomNum;
   }


}