/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.behaviortree;

/**
 *
 * @author Diego
 */
public class BTSelectNode extends BTNode{
    
    BTSelectNode(BTNode a_parentNode)
    {
        super(a_parentNode);
    }

    @Override
    public void resetNode(){

        m_curNode = 0;
    }
    
    //Function to notify the parent about my result.
    public void update(int a_nodeStatus) throws IncorrectNodeException
    {
        if(a_nodeStatus != BTConstants.NODE_STATUS_EXECUTING)
            ++m_curNode;
        
        if(a_nodeStatus == BTConstants.NODE_STATUS_FAILURE)
        {
            if(m_curNode < m_nodeCount)
            {
                m_children.get(m_curNode).step();
            }
            else 
            {
                m_curNode = 0;
                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                m_parent.update(m_nodeStatus);
            }
        }
        else if(a_nodeStatus == BTConstants.NODE_STATUS_SUCCESS)
        {
            m_curNode = 0;
            m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
            m_parent.update(m_nodeStatus);
        }
        
    }

    //Function to execute this node.
    public void step() throws IncorrectNodeException
    {
        super.step();
        
        if(m_nodeCount == 0)
            throw new IncorrectNodeException("BT Syntax error: Select node must have at least 1 child.");
        
        m_curNode = 0;
        m_children.get(m_curNode).step();
    }

}
