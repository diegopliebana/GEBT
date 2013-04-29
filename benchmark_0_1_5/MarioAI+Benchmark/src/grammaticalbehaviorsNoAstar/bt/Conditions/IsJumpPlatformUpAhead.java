package grammaticalbehaviorsNoAstar.bt.Conditions;

import grammaticalbehaviorsNoAstar.GEBT_Mario.GEBT_MarioAgent;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTConstants;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTLeafNode;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTNode;
import grammaticalbehaviorsNoAstar.bt.behaviortree.IncorrectNodeException;

/**
 * @author Diego
 */
public class IsJumpPlatformUpAhead extends BTLeafNode{

    public IsJumpPlatformUpAhead(BTNode a_parent)
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
        
        int aheadStartPos = GEBT_MarioAgent.MARIO_Y+1;
        int aheadEndPos = GEBT_MarioAgent.MARIO_Y+3;
        boolean obsUp = mario.isObstacle(GEBT_MarioAgent.MARIO_X-3,GEBT_MarioAgent.MARIO_X-1,
                                             aheadStartPos,aheadEndPos);
        
        if(obsUp)
        {
           m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
        }
        
        //report 
        m_parent.update(m_nodeStatus);
    }
    
    
}
