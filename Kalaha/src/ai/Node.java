
package ai;
import kalaha.GameState;
import java.util.ArrayList;


public class Node
{
    private ArrayList<Node> m_Children;
    private final GameState m_State; //A smaller gamestate might be an option to optimize memory.
    private final Node m_Parent; // not sure if needed. not needed as of yet.
    private boolean m_Leaf; // not sure if needed. not needed as of yet.
    private final int m_Depth;
    private final int m_MaxNrOfChildren;
    private int m_Utility;
    
    public Node(Node p_Parent, GameState p_GameState, int p_Depth)
    {
        m_State = p_GameState;
        m_Parent = p_Parent;
        m_Children = null;
        m_Leaf = false;
        m_Depth = p_Depth;
        m_MaxNrOfChildren = 6;
        m_Utility = 0;
    }
    
    public void createChildren(int p_Depth)
    {
       //only expand to specified depth
       if(m_Depth < p_Depth)
       {
            m_Children = new ArrayList<>();
            for(int i = 1; i < m_MaxNrOfChildren + 1; i++)
            {
                GameState gs = m_State.clone();
                if(gs.moveIsPossible(i))
                {
                    gs.makeMove(i);
                    m_Children.add(new Node(this, gs, (m_Depth + 1)));
                }            
            }
            for (Node Child : m_Children) 
                Child.createChildren(p_Depth);
        }
        else
       {
            m_Leaf = true;
            calcUtility();
       }
    }
    
    public void calcUtility()
    { 
        int ScorePlayer1 = m_State.getScore(1);
        int ScorePlayer2 = m_State.getScore(2);

        if(ScorePlayer1 > ScorePlayer2)
        {
            m_Utility = 1;
        }
        else if(ScorePlayer1 < ScorePlayer2)
        {
            m_Utility = -1;
        }
        else m_Utility = 0;
        /*Utility rules
            * If max has more points in house +1
            * If min has more points in house -1
        */ 
    }
    
    //Will be used later in optimization techniques?                                                               
    private void removeChild()
    {
        
    }
    
    private boolean isLeaf()
    {
        return m_Leaf;
    }

}
