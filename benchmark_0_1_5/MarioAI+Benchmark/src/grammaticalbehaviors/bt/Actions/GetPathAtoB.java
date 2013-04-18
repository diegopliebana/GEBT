/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.Actions;

import grammaticalbehaviors.GEBT_Mario.Path;
import grammaticalbehaviors.GEBT_Mario.Graph;
import grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;
import grammaticalbehaviors.bt.behaviortree.BTConstants;
import grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import grammaticalbehaviors.bt.behaviortree.BTNode;
import grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;

/**
 *
 * @author Diego
 */
public class GetPathAtoB extends BTLeafNode{


    private int m_Ax;
    private int m_Ay;
    private int m_Bx;
    private int m_By;
    private int m_ABStatus;

    public GetPathAtoB(BTNode a_parent)
    {
        super(a_parent);
        
        m_Ax = 13;
        m_Ay = 2;
        m_Bx = 14;
        m_By = 3;
        m_ABStatus = 0;
    }


    public void step() throws IncorrectNodeException 
    {
        super.step();
        
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent;

        
        Graph levelGraph = mario.getMap().getGraph();
        int marioNodeId = mario.getMarioNodeInMap();

        m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;

        if(levelGraph.getNumNodes() == 0)
        {
            //Graph not initialized, or no node found at Mario's position
            m_parent.update(BTConstants.NODE_STATUS_FAILURE);
            return;
        }
        else if(marioNodeId == -1)
        {
            if(mario.amIOnGround())
            {
                //Get the closest.
                marioNodeId = mario.getClosestNodeToMario();
            }
            else
            {
                m_parent.update(BTConstants.NODE_STATUS_FAILURE);
                return;
            }
        }
        
        {
            int idNodeDest = -1;
            if(m_ABStatus == 0)
            {
                idNodeDest = mario.getMap().getGraph().existsNode(m_Ax, m_Ay);
            }
            else if(m_ABStatus == 1)
            {
                int nodeA = mario.getMap().getGraph().existsNode(m_Ax, m_Ay);
                if(nodeA != marioNodeId)
                {
                    m_parent.update(BTConstants.NODE_STATUS_FAILURE);
                    return;
                }
                
                idNodeDest = mario.getMap().getGraph().existsNode(m_Bx, m_By);
            }
            else if(m_ABStatus == 2)
            {
                m_parent.update(BTConstants.NODE_STATUS_FAILURE);
                return;
            }

            if(idNodeDest == -1)
            {
                //No destination node
                m_parent.update(BTConstants.NODE_STATUS_FAILURE);
                return;
            }
            else
            {
                Path pathToDestination = levelGraph.getPath(marioNodeId, idNodeDest, mario);
                if(pathToDestination.m_points.size() > 1)
                {
                    mario.setPath(pathToDestination);
                    m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
                    m_ABStatus++;
                }
            }

        }

        //Following path
        mario.setFollowingPath(m_nodeStatus == BTConstants.NODE_STATUS_SUCCESS);
        mario.followingNewPath(m_nodeStatus == BTConstants.NODE_STATUS_SUCCESS);

        //report
        m_parent.update(m_nodeStatus);
    }
    
    
}
