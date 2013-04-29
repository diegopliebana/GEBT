/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.bt.behaviortree;

import java.util.Vector;

/**
 *
 * @author Diego
 */
public abstract class BTNode {

    public int m_nodeId;

    //Status of this node
    protected int m_nodeStatus;
    
    //Children of this node
    protected Vector<BTNode> m_children; 
    
    //Parent of this node
    protected BTNode m_parent;
    
    //Current child node
    protected int m_curNode;
    
    //Child node count
    protected int m_nodeCount;
    
    //Reference to the behavior tree that owns this node
    protected BehaviorTree m_tree;
    
    //Last cycle id when this node was executed. This value must be updated in the 
    protected long m_lastTick;
    
    //Default constructor
    public BTNode()
    {
        m_nodeStatus = BTConstants.NODE_STATUS_IDLE;
        m_children = new Vector<BTNode>();
        m_parent = null;
        m_curNode = 0;
        m_nodeCount = 0;
        m_tree = null;
        m_lastTick = -1;
        m_nodeId = -1;
    }
    
    //Constructor specifying parent.
    public BTNode(BTNode a_parent)
    {
        m_nodeStatus = BTConstants.NODE_STATUS_IDLE;
        m_children = new Vector<BTNode>();
        m_parent = a_parent;
        m_curNode = 0;
        m_nodeCount = 0; 
        m_tree = null;
        m_lastTick = -1;
        m_nodeId = -1;
    }
    

    public void setParent(BTNode a_parent)
    {
        m_parent = a_parent;
    }
    
    public BTNode getParent()
    {
        return m_parent;
    }
    
    //Adds a new node to the list of children, in the given index
    public void add(BTNode a_node, int a_index)
    {
        a_node.setParent(this);
        m_children.add(a_index, a_node);
        m_nodeCount++;
    }
    
    //Adds a new node to the list of children.
    public void add(BTNode a_node)
    {
        a_node.setParent(this);
        m_children.add(a_node);
        m_nodeCount++;
    }
    
    //Gets the node in the given index. Exception must be catched in caller
    public BTNode get(int a_index) throws ArrayIndexOutOfBoundsException
    {
        return m_children.get(a_index);
    }
    
    public void setBehaviorTree(BehaviorTree a_bt)
    {
        m_tree = a_bt;
    }
    
    public BehaviorTree getBehaviorTree()
    {
        return m_tree;
    }
    
    //Function to notify the parent about my result.
    public abstract void update(int a_nodeStatus) throws IncorrectNodeException;
    
    public void notifyResult()  throws IncorrectNodeException
    {
        if((m_nodeStatus == BTConstants.NODE_STATUS_FAILURE) ||
           (m_nodeStatus == BTConstants.NODE_STATUS_SUCCESS))
        {
            int nodeStatusResult = m_nodeStatus;
            m_nodeStatus = BTConstants.NODE_STATUS_IDLE;
            m_parent.update(nodeStatusResult);
        }
        else
        {
            //This will be executed if an action returns 
            //something different than SUCCESS or FAILURE (action executed again)
            this.step();
        }
    }
    
    
    //Function to execute this node.
    public void step() throws IncorrectNodeException
    {
        m_tree.setCurrentNode(this);
        m_lastTick = m_tree.getCurTick();
    }
    
}
