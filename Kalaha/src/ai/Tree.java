
package ai;
import kalaha.GameState;


public class Tree {
    private Node m_RootNode;
    
    public Tree()
    {
        m_RootNode = null;
    }
    
    public void CreateTree(GameState p_CurrentState, int TreeDepth)
    {
        m_RootNode = new Node(null, p_CurrentState, 0);
        m_RootNode.createChildren(TreeDepth);
    }
    public int getMove()
    {
        return 1;
    }
}
