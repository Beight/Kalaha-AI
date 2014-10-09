
package ai;
import kalaha.GameState;
import java.util.ArrayList;


public class Node
{
    private ArrayList<Node> m_Children;
    private final GameState m_State; //A smaller gamestate might be an option to optimize memory.
    private final Node m_Parent; // not sure if needed. not needed as of yet.
    private boolean m_Leaf; 
    private final int m_Depth;
    private final int m_MaxNrOfChildren;
    private int m_Utility;
    private final boolean m_Max; //true if it's max turn, false if it min's turn
    private final int m_Ambo;
    
    public Node(Node p_Parent, GameState p_GameState, int p_Depth, boolean p_Max, int p_Ambo)
    {
        m_Children = new ArrayList<>();
        m_State = p_GameState;
        m_Parent = p_Parent;
        m_Leaf = false;
        m_Depth = p_Depth;
        m_MaxNrOfChildren = 6;
        m_Utility = 0;
        m_Max = p_Max;
        m_Ambo = p_Ambo;
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
            {
                calcUtility();
                m_Leaf = true;
            }
        }
        else
        {
            m_Leaf = true;
            calcUtility();
        }
       //Propegate utility value from children nodes.
       if(!m_Leaf)
       {
           try
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
           }
           catch(Exception ex)
           {
               throw ex;
           }
       }
     if(m_Parent != null)
           m_Children.clear();
    }
    
    public void iterativeDeepening(int p_Depth)
    {
        int i = p_Depth;
        double elapsedTime = 0.0;
        while(elapsedTime < 4.5)//while tme is not 5 secs.
        {
            long startT = System.currentTimeMillis();
            depthFirst(i);
            for(Node n : m_Children)
            {
                if(n.m_Utility > 10)
                {
                    elapsedTime = 4.5;
                    break;
                }
                
            }
            long tot = System.currentTimeMillis() - startT;
            elapsedTime += (double)tot / 1000.0;
            i += 1;
        }
    }
    
    public void calcUtility()
    {   
        int currentPlayer = m_State.getNextPlayer();
        //player1 is the AI player
        int player1;
        //player2 is the opponent
        int player2;
        //Calculate which player number the AI player has
        /*Note*
            The AIs player number could be saved as an int in the Node object
            but that would increase the memory usage of the Game tree so to avoid
            another int in the Node object this if-else statements was created.
            It's possible that the better way is still to save the player
            number instead of calculating it with these if-esle statements
        */
        if(m_Max)
        {
            player1 = currentPlayer;
        }
        else
        {
            if(currentPlayer == 1)
                player1 = 2;
            else
                player1 = 1;
        }
        //Calculate which player number the Opponent has
        if(player1 == 1)
            player2 = 2;
        else
            player2 = 1;
        
        int gameEnd = m_State.getWinner();
        
        if(gameEnd == -1)
        {
            //If the node is not a terminal state, calculate a utility based on score
            int ScorePlayer1 = m_State.getScore(player1);
            int ScorePlayer2 = m_State.getScore(player2);
            if(ScorePlayer1 > ScorePlayer2)
                m_Utility += 1;
            else if(ScorePlayer1 < ScorePlayer2)
                m_Utility -= 1;
            
            for(int i = 1; i < 7; i++)
            {
                //Check to see if we have any ambo that has enough seeds to
                //end in the house which gives us a extra turn.
                if(m_State.getSeeds(i, player1) != 0)
                {
                    if((7 - i) == m_State.getSeeds(i, player1))
                        m_Utility += 3;
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
                                m_Utility += 5;
                        }
                    }
                        
                }
                if(m_State.getSeeds(i, player2) != 0)
                {
                    if((7 - i) == m_State.getSeeds(i, player2))
                    {
                        m_Utility -= 3;
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
                                    m_Utility -= 5;
                            }
                        }

                    }
                }
            }        
        }
        else if (gameEnd == player1)
            m_Utility += 10;
        else if(gameEnd == player2)
            m_Utility -= 10;
         
        /*Utility rules
            * If max has more points in house +1
            * If min has more points in house -1
            * 
        */ 
    }                                                      
    
    public int getMove()
    {
        int baseUtil = m_Children.get(0).m_Utility;
        int move = m_Children.get(0).m_Ambo;
        for(int i = 1; i < m_Children.size(); i++)
        {
            if(baseUtil < m_Children.get(i).m_Utility)
            {
                baseUtil = m_Children.get(i).m_Utility;
                move = m_Children.get(i).m_Ambo;
            }    
        }
        return move; 
    }
}