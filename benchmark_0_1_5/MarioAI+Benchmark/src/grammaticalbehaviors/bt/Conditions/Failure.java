/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.Conditions;

import grammaticalbehaviors.bt.behaviortree.BTConstants;
import grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import grammaticalbehaviors.bt.behaviortree.BTNode;
import grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;

/**
 *
 * @author Diego
 */
public class Failure extends BTLeafNode{

    public Failure(BTNode a_parent)
    {
        super(a_parent);
    }
    
    public void step() throws IncorrectNodeException 
    {
        super.step();

        //This is easy;

        //report
        m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
        m_parent.update(m_nodeStatus);
    }
    
    
}
