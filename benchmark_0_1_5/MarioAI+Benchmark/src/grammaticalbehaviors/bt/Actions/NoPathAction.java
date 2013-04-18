/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.Actions;

import grammaticalbehaviors.GEBT_Mario.Path;
import grammaticalbehaviors.GEBT_Mario.Node;
import grammaticalbehaviors.GEBT_Mario.Graph;
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
public class NoPathAction extends BTLeafNode{
 
    public NoPathAction(BTNode a_parent)
    {
        super(a_parent);
    }
    
    public void step() throws IncorrectNodeException
    {
        
        super.step();
     
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent;
        Graph levelGraph = mario.getMap().getGraph();
        int marioNodeId = mario.getMarioNodeInMap();

        if(marioNodeId == -1 && mario.amIOnGround())
        {
            marioNodeId = mario.getClosestNodeToMario();
        }

        if(levelGraph.getNumNodes() == 0)
        {
            //Move Right
            mario.setAction(Mario.KEY_RIGHT,true);
        }
        else
        {
            if(mario.onRightMostFlag() && mario.amIOnGround() && marioNodeId != -1)
            {
                Node n = levelGraph.getNode(marioNodeId);
                boolean desperateJump = (n.getHoles() == Node.MAP_HOLE_ON_BOTH ||
                                        n.getHoles() == Node.MAP_HOLE_ON_RIGHT);

                if(desperateJump)
                {
                    //REPORT THIS!
                    m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                    m_parent.update(m_nodeStatus);
                }
                else
                {
                    if(mario.isMarioOnLeftOf(n.getX()))
                    {
                        //On right, move left
                        mario.setAction(Mario.KEY_RIGHT,true);
                    }else if(mario.isMarioOnRightOf(n.getX()))
                    {
                        //On left, move right
                        mario.setAction(Mario.KEY_LEFT, true);
                    }
                }
            }


        }

        //report a success
        m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
    }
}
