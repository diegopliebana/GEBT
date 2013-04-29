/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.bt.behaviortree;

/**
 *
 * @author Diego
 */
public class BTParallelNode extends BTNode{

    private int m_nodesToFail;
    private int m_nodesToSucceed;
    private int m_curNodesFailed;
    private int m_curNodesSucceeded;
    
    BTParallelNode(BTNode a_parentNode)
    {
        super(a_parentNode);
        m_nodesToFail = 1;
        m_nodesToSucceed = 1;
        m_curNodesFailed = 0;
        m_curNodesSucceeded = 0;
    }
    
    
    BTParallelNode(BTNode a_parentNode, int a_nodesToSucceed, int a_nodesToFail)
    {
        super(a_parentNode);
        m_nodesToFail = a_nodesToFail;
        m_nodesToSucceed = a_nodesToSucceed;
        m_curNodesFailed = 0;
        m_curNodesSucceeded = 0;
    }

    //Function to notify the parent about my result.
    public void update(int a_nodeStatus) throws IncorrectNodeException
    {
        m_nodeStatus = BTConstants.NODE_STATUS_EXECUTING;
        
        if(a_nodeStatus == BTConstants.NODE_STATUS_FAILURE)
        {
            m_curNodesFailed++;
            if(m_curNodesFailed == m_nodesToFail)
            {
                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                m_parent.update(m_nodeStatus);
            }
        }else if(a_nodeStatus == BTConstants.NODE_STATUS_SUCCESS)
        {
            m_curNodesSucceeded++;
            if(m_curNodesSucceeded == m_nodesToSucceed)
            {
                m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
                m_parent.update(m_nodeStatus);
            }
        }
        
    }

    //Function to execute this node.
    public void step() throws IncorrectNodeException
    {
        super.step();
        
        m_nodeStatus = BTConstants.NODE_STATUS_EXECUTING;
        
        if(m_nodeCount < 2)
            throw new IncorrectNodeException("BT Syntax error: Parallel node must have at least 2 children.");

        if(m_nodeCount > (m_nodesToFail + m_nodesToSucceed))
            throw new IncorrectNodeException("BT Syntax error: Parallel node cannot have more children that the sum of the policies.");
        
        m_curNode = 0;
        //In this parallel node, we execute everything until we reach the last child or ...
        //one of the policies is fulfilled (m_nodeStatus changed because of calls to BTParallelNode.update())
        while((m_curNode < m_nodeCount) && (m_nodeStatus == BTConstants.NODE_STATUS_EXECUTING))
        {
            m_children.get(m_curNode).step();
            m_curNode++;
        }
        
        //If we reach this point, no policy has been fulfilled and all children were executed.
        if(m_nodeStatus == BTConstants.NODE_STATUS_EXECUTING)
        {
            //We have a problem, this should not happen (incorrect number of policies);
            System.out.println("ERROR: Parallel node policies do not match the number of children.");
            System.out.println("Maximum number of success/fails taken by default as result for this node.");

            //We are going to return anything, anyway...
            if(m_curNodesSucceeded > m_curNodesFailed)
            {
                m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
                m_parent.update(m_nodeStatus);
            }
            else
            {
                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                m_parent.update(m_nodeStatus);
            }
        }
    }
    
}
