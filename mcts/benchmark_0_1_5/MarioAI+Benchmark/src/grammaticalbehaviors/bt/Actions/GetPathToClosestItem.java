/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.Actions;

import grammaticalbehaviors.GEBT_Mario.Path;
import grammaticalbehaviors.GEBT_Mario.Node;
import grammaticalbehaviors.GEBT_Mario.Graph;
import grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;
import grammaticalbehaviors.bt.behaviortree.BTConstants;
import grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import grammaticalbehaviors.bt.behaviortree.BTNode;
import grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;
import java.util.Vector;

/**
 *
 * @author Diego
 */
public class GetPathToClosestItem extends BTLeafNode{

    private Vector<Integer> m_forbidden;

    public GetPathToClosestItem(BTNode a_parent)
    {
        super(a_parent);

        m_forbidden = new Vector<Integer>();
    }

    @Override
    public void resetNode()
    {
        m_forbidden = new Vector<Integer>();
    }

    public void step() throws IncorrectNodeException 
    {
        super.step();
        m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
        
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent;
        Graph levelGraph = mario.getMap().getGraph();
        int marioNodeId = mario.getMarioNodeInMap();
        Path curPath = mario.getCurrentPath();

        //Use this block to test this node form where you like:
        //######
        /*if(marioNodeId < 10210033 || marioNodeId >   10210034) //Input here the positions
        {
            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
            m_parent.update(m_nodeStatus);
            return;
        }else if(mario.getCurrentPath().m_metadata == Path.PATH_CLOSESTBRICK)
        {
            m_parent.update(BTConstants.NODE_STATUS_FAILURE);
            return;
        }*/
        //######

        if(levelGraph.getNumNodes() == 0 || (curPath != null && curPath.m_metadata == Path.PATH_CLOSESTITEM))
        {
            //Graph not initialized, or no node found at Mario's position
            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
            m_parent.update(m_nodeStatus);
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
                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                m_parent.update(m_nodeStatus);
                return;
            }

        }

        int idNodeDest = levelGraph.getClosestItemNode(marioNodeId, m_forbidden);
        if(idNodeDest == -1 || idNodeDest == marioNodeId)
        {
            //No destination node, or it is the same I am in right now.
            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
        }
        else
        {
            Path pathToDestination = levelGraph.getPath(marioNodeId, idNodeDest, mario);
            if(pathToDestination.m_points.size() > 1 &&
                 pathToDestination.m_cost < Integer.MAX_VALUE)
            {
                if(pathToDestination.m_cost == 1)
                {
                    Node org = levelGraph.getNode(marioNodeId);
                    Node dst = levelGraph.getNode(idNodeDest);
                    if(org.manhattanDistanceTo(dst) == 1)
                    {
                        mario.increaseGraphLength();
                    }
                }
                else
                {
                    mario.resetGraphLength();
                }


                pathToDestination.m_metadata = Path.PATH_CLOSESTITEM;
                mario.setPath(pathToDestination);
                m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
                m_forbidden.clear();
            }
            else
            {
                if(mario.inTrap())
                {
                    boolean canGoFurhter = mario.increaseGraphLength();
                    if(!canGoFurhter)
                    {
                        m_forbidden.add(idNodeDest);
                    }
                }
                else
                {
                    m_forbidden.add(idNodeDest);
                }
            }

        }

        if(m_nodeStatus == BTConstants.NODE_STATUS_SUCCESS)
        {
            mario.followingNewPath(true);
            mario.setFollowingPath(true);
        }

        //report
        m_parent.update(m_nodeStatus);
    }
    
    
}
