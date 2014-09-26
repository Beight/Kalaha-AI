
package ai;
import kalaha.GameState;
import java.util.ArrayList;


public class Node
{
    private final GameState m_State; //A smaller gamestate might be an option to optimize memory.
    private final Node m_Parent; // not sure if needed. not needed as of yet.
    private ArrayList<Node> m_Children;
    private boolean m_Leaf; // not sure if needed. not needed as of yet.
    private final int m_Depth;
    private final int m_MaxNrOfChildren;
    
    public Node(Node p_Parent, GameState p_GameState, int p_Depth)
    {
        m_State = p_GameState;
        m_Parent = p_Parent;
        m_Children = null;
        m_Leaf = false;
        m_Depth = p_Depth;
        m_MaxNrOfChildren = 6;
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
            m_Leaf = true; 
    }
    
    //Will be used later in optimization techniques?                                                               
    private void removeChild()
    {
        
    }
    
}
