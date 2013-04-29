package grammaticalbehaviorsNoAstar.bt.Conditions;

import grammaticalbehaviorsNoAstar.GEBT_Mario.GEBT_MarioAgent;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTConstants;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTLeafNode;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTNode;
import grammaticalbehaviorsNoAstar.bt.behaviortree.IncorrectNodeException;

/**
 * @author Diego
 */
public class IsBreakableUpAhead extends BTLeafNode{

    public IsBreakableUpAhead(BTNode a_parent)
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
        int aheadEndPos = GEBT_MarioAgent.MARIO_Y+2;
        boolean obsUp = mario.isBreakable(GEBT_MarioAgent.MARIO_X-5,GEBT_MarioAgent.MARIO_X-1,
                                             aheadStartPos,aheadEndPos);
        
        if(obsUp)
        {
           m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
        }
        
        //report 
        m_parent.update(m_nodeStatus);
    }
    
    
}
