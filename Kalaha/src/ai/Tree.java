
package ai;
import kalaha.GameState;


public class Tree {
    private Node m_RootNode;
    private final int m_Depth;
    
    public Tree(int p_Depth)
    {
        m_RootNode = null;
        m_Depth = p_Depth;
    }
    
    public void CreateTree(GameState p_CurrentState)
    {
        m_RootNode = new Node(null, p_CurrentState, 0, true, 0);
        m_RootNode.createChildren(m_Depth);
    }
    public int getMove()
    {
        return m_RootNode.getMove();
    }
}
