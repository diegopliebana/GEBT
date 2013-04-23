/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.Conditions;

import grammaticalbehaviors.GEBT_Mario.Path;
import grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;
import grammaticalbehaviors.bt.behaviortree.BTConstants;
import grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import grammaticalbehaviors.bt.behaviortree.BTNode;
import grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;

/**
 *
 * @author Diego
 */
public class IsRightMostCloseToEnd extends BTLeafNode{

    public IsRightMostCloseToEnd(BTNode a_parent)
    {
        super(a_parent);
    }
    
    public void step() throws IncorrectNodeException 
    {
        super.step();
        
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent; 
            
        m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
        
        Path curPath = mario.getCurrentPath();
        if(curPath.m_metadata == Path.PATH_RIGHTMOST)
        {
            int numPointsInPath = curPath.m_points.size();
            int marioIndex = mario.getPosIndexInPath();

            if(marioIndex > 0 && numPointsInPath > 3 && marioIndex >= numPointsInPath-4)
                m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
        }

        //report 
        m_parent.update(m_nodeStatus);
    }
    
    
}
