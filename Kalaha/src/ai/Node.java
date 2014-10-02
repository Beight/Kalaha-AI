
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
    
    public void createChildren(int p_Depth)
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
                    child.createChildren(p_Depth);
                    m_Children.add(child);                       
                } 
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
           //If it's mins turn get the lowest utility value
           if(!m_Max)
           {
               for(Node n : m_Children)
               {
                   if(m_Utility > n.m_Utility)
                       m_Utility = n.m_Utility;
               }
               
           }
           //If it's max turn get the highest utility value
           else
           {
               for(Node n : m_Children)
               {
                   if(m_Utility < n.m_Utility)
                       m_Utility = n.m_Utility;
               }
           }
       }
       if(m_Parent != null)
            m_Children.clear();
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
            
            // will not work becasue ambo will always be empty since we emptied it last move.
            switch(m_Ambo)
            {
                case 1:
                    if(m_State.getSeeds(m_Ambo, player1) == 6)
                        m_Utility += 1;
                case 2:
                    if(m_State.getSeeds(m_Ambo, player1) == 5)
                        m_Utility += 1;
                case 3:
                    if(m_State.getSeeds(m_Ambo, player1) == 4)
                        m_Utility += 1;
                case 4:
                    if(m_State.getSeeds(m_Ambo, player1) == 3)
                        m_Utility += 1;
                case 5:
                    if(m_State.getSeeds(m_Ambo, player1) == 2)
                        m_Utility += 1;
                case 6:
                    if(m_State.getSeeds(m_Ambo, player1) == 1)
                        m_Utility += 1;
            }
            // will not work becasue ambo will always be empty since we emptied it last move.
            if(m_Max)
                if((m_Ambo + m_State.getSeeds(m_Ambo, player1)) == 7)
                    m_Utility += 1;
            else
                if((m_Ambo + m_State.getSeeds(m_Ambo, player2)) == 7)
                    m_Utility -= 1;
            
            //ambo 1 = 6 seeds
            //ambo 2 = 5 seeds
            //ambo 3 = 4 seeds
            //ambo 4 = 3 seeds
            //ambo 5 = 2 seeds
            //ambo 6 = 1 seeds
           
            
                    
            
        }
        else if (gameEnd == player1)
            m_Utility = 1;
        else if(gameEnd == player2)
            m_Utility = -1;
         
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