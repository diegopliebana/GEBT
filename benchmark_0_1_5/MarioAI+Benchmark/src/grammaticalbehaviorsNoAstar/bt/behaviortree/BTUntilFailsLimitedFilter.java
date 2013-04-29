/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.bt.behaviortree;

/**
 *
 * @author Diego
 */
public class BTUntilFailsLimitedFilter extends BTNode{

    private int m_numExecutions;
    private int m_limit;
    
    BTUntilFailsLimitedFilter(BTNode a_parentNode, int a_limit)
    {
        super(a_parentNode);
        m_numExecutions = 0;
        m_limit = a_limit;
    }
    
    
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
            //SUCCESS: Execute again if we are still at the limit.
            if(m_numExecutions == m_limit)
            {
                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                m_parent.update(m_nodeStatus);
            }
            else
            {
                //Execute again.
                m_numExecutions++;
                m_curNode = 0;
                m_children.get(m_curNode).step();
            }
            
        }
        
    }

    //Function to execute this node.
    public void step() throws IncorrectNodeException
    {
        super.step();
        m_numExecutions = 1;
        
        if(m_nodeCount != 1)
            throw new IncorrectNodeException("BT Syntax error: UntilFailsLimited Filter must have one and just one child.");
        
        m_curNode = 0;
        m_children.get(m_curNode).step();
    }

}
