/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;
import kalaha.GameState;
/**
 *
 * @author BTH
 */
public class Node
{
    private GameState m_State; //A smaller gamestate might be an option to optimize memory.
    private Node m_Parent;
    private Node m_Children;
    private boolean Leaf;
    
    public Node(Node p_Parent)
    {
        m_State = null;
        m_Parent = p_Parent;
        m_Children = null;
        Leaf = false;
    }
    
    private void createChildren()
    {
        //Create children nodes, also check if it's a leaf node.
    }
    
}
