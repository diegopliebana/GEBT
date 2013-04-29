/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.bt.behaviortree;

/**
 *
 * @author Diego
 */
public class BTLoopFilter extends BTNode{

    private int m_times;
    private int m_limit;
    
    BTLoopFilter(BTNode a_parentNode, int a_limit)
    {
        super(a_parentNode);
                
        m_times = 0; 
        m_limit = a_limit;
    }
    
    
    
    //Function to notify the parent about my result.
    public void update(int a_nodeStatus) throws IncorrectNodeException
    {
        //Does not matter, just execute it again until loop counter expires
        if(m_times == m_limit)
        {
            m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
            m_parent.update(m_nodeStatus);
        }
        else
        {
            m_times++;
            m_curNode = 0;
            m_children.get(m_curNode).step();
        }
    }

    //Function to execute this node.
    public void step() throws IncorrectNodeException
    {
        super.step();
        
        if(m_nodeCount != 1)
            throw new IncorrectNodeException("BT Syntax error: Non Filter must have one and just one child.");
        
        m_times=1;
        m_curNode = 0;
        m_children.get(m_curNode).step();
    }

}
