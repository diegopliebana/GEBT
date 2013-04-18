/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.behaviortree;

/**
 *
 * @author Diego
 */
public class BTUntilFails extends BTNode{

    BTUntilFails(BTNode a_parentNode)
    {
        super(a_parentNode);
    }

    @Override
    public void resetNode(){}
    
    //Function to notify the parent about my result.
    public void update(int a_nodeStatus) throws IncorrectNodeException
    {
        if(a_nodeStatus == BTConstants.NODE_STATUS_FAILURE)
        {   
            //FAIL: This node fails.
            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
            m_parent.update(m_nodeStatus);
        }
        else if(a_nodeStatus == BTConstants.NODE_STATUS_SUCCESS)
        {
            //Execute again.
            m_curNode = 0;
            m_children.get(m_curNode).step();
        }
        
    }

    //Function to execute this node.
    public void step() throws IncorrectNodeException
    {
        super.step();
        
        if(m_nodeCount != 1)
            throw new IncorrectNodeException("BT Syntax error: UntilFail Filter must have one and just one child.");
        
        m_curNode = 0;
        m_children.get(m_curNode).step();
    }

}
