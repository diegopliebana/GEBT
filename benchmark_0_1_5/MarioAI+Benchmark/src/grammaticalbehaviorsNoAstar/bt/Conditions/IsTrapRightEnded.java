/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.bt.Conditions;

import grammaticalbehaviorsNoAstar.GEBT_Mario.GEBT_MarioAgent;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTConstants;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTLeafNode;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTNode;
import grammaticalbehaviorsNoAstar.bt.behaviortree.IncorrectNodeException;

/**
 *
 * @author Diego
 */
public class IsTrapRightEnded extends BTLeafNode{


    private int m_detectedTrapPos;

    public IsTrapRightEnded(BTNode a_parent)
    {
        super(a_parent);
        m_detectedTrapPos = -1;
    }
    
    public void step() throws IncorrectNodeException 
    {
        super.step();
        
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent; 

        int marioPos = mario.getCellsRun();
        m_detectedTrapPos = (mario.inTrap() && mario.isRightTrap())  ? mario.trapPos() : m_detectedTrapPos;

        int trapHeight = mario.heightTrapRight();
        boolean marioOnGround = mario.amIOnGround();
        boolean trapIsThere = mario.isObstacle(trapHeight,trapHeight,
                                            GEBT_MarioAgent.MARIO_Y,GEBT_MarioAgent.MARIO_Y);

        m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;

        if(!trapIsThere && marioOnGround)
        {
            m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
            boolean isForbidden = mario.isGapForbidden(marioPos);
            if(isForbidden)
            {
                mario.deleteForbiddenGap(marioPos);
                m_detectedTrapPos = marioPos + 1;
            }
        }
        else if(trapIsThere && marioOnGround)
        {
            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
            mario.addForbiddenGap(marioPos, m_detectedTrapPos);
        }

        //report 
        m_parent.update(m_nodeStatus);
    }
    
    
}
