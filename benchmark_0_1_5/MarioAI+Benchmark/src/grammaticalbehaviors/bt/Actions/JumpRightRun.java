/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.Actions;

import ch.idsia.mario.engine.sprites.Mario;
import grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;
import grammaticalbehaviors.bt.behaviortree.BTConstants;
import grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import grammaticalbehaviors.bt.behaviortree.BTNode;
import grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;

/**
 *
 * @author Diego
 */
public class JumpRightRun extends BTLeafNode{
    
    public JumpRightRun(BTNode a_parent)
    {
        super(a_parent);
    }
    
    public void step() throws IncorrectNodeException 
    {
        super.step();

        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent; 
        
        mario.setAction(Mario.KEY_RIGHT,true);
        mario.setAction(Mario.KEY_JUMP,true);
        mario.setAction(Mario.KEY_SPEED,true);
        
        //report a success
        m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
        //m_parent.update(m_nodeStatus);
    }
}
