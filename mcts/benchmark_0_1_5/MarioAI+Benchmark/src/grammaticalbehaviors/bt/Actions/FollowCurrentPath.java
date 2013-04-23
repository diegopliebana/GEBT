/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.Actions;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Sprite;
import grammaticalbehaviors.GEBT_Mario.Path;
import grammaticalbehaviors.GEBT_Mario.Edge;
import grammaticalbehaviors.GEBT_Mario.Enemy;
import grammaticalbehaviors.GEBT_Mario.Node;
import grammaticalbehaviors.GEBT_Mario.Graph;
import grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;
import grammaticalbehaviors.GEBT_Mario.Map;
import grammaticalbehaviors.bt.behaviortree.BTConstants;
import grammaticalbehaviors.bt.behaviortree.BTLeafNode;
import grammaticalbehaviors.bt.behaviortree.BTNode;
import grammaticalbehaviors.bt.behaviortree.IncorrectNodeException;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Diego
 */
public class FollowCurrentPath extends BTLeafNode{

    private boolean m_startingEdge;
    private boolean m_readyToJump;
    private int m_xJumpDistance;
    private int m_yJumpDistance;
    private boolean m_stopJumping;
    private boolean m_jumpStarted;
    private int m_jumpAttempts;
    private boolean m_redoJump;
    private float m_lastSpeed;
    private int m_forceMove;
    private int m_preSlowdownCount;
    private boolean m_jumpPressSpeed;
    private boolean m_grantLRMove;
    private Hashtable<Long, Integer> m_edgeAttempts;
    private int m_currentLinkType;
    private int m_jumpDestHolesMode;
    private boolean m_destOnPot;

    public FollowCurrentPath(BTNode a_parent)
    {
        super(a_parent);
        m_startingEdge = true;
        m_readyToJump = false;
        m_stopJumping = false;
        m_jumpStarted = false;
        m_redoJump = false;
        m_destOnPot = false;
        m_jumpPressSpeed = false;
        m_xJumpDistance = 0;
        m_yJumpDistance = 0;
        m_jumpAttempts = 0;
        m_lastSpeed = 0;
        m_forceMove = 0;
        m_jumpDestHolesMode = 0;
        m_preSlowdownCount = 1;
        m_grantLRMove = false;
        m_edgeAttempts = new Hashtable<Long, Integer>();
        m_currentLinkType = 0;
    }

    @Override
    public void resetNode()
    {
        m_startingEdge = true;
        m_readyToJump = false;
        m_stopJumping = false;
        m_jumpStarted = false;
        m_redoJump = false;
        m_jumpPressSpeed = false;
        m_xJumpDistance = 0;
        m_yJumpDistance = 0;
        m_jumpAttempts = 0;
        m_lastSpeed = 0;
        m_forceMove = 0;
        m_jumpDestHolesMode = 0;
        m_preSlowdownCount = 1;
        m_grantLRMove = false;
        m_edgeAttempts = new Hashtable<Long, Integer>();
        m_currentLinkType = 0;
    }
    
    public void step() throws IncorrectNodeException 
    {
        super.step();
        
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent; 

        Path curPath = mario.getCurrentPath();
        int marioIndex = mario.getPosIndexInPath();
        int marioRealNode = mario.getMarioNodeInMap();
        boolean marioOnGround = mario.amIOnGround();

        if(marioRealNode == -1 && marioOnGround)
        {
            //Get the closest node:
            marioRealNode = mario.getClosestNodeToMario();

            //Is it in the current path?
            boolean stillInPath = mario.isNodeInCurrentPath(marioRealNode);
            if(!stillInPath)
            {
                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                mario.setFollowingPath(false);
                return;
            }
        }

        m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
        

        //Update if we reach next intermediate node.
        if(marioIndex+1 < curPath.m_points.size())
        {
            int nodeOldIdDest = curPath.m_points.get(marioIndex+1);
            if(nodeOldIdDest == marioRealNode || mario.isFollowingNewPath())
            {

                //We only can navigate through the graph if we are in the ground... so
                if(!marioOnGround)
                {
                    return;
                }

                //advance index;
                if(!mario.isFollowingNewPath())
                {
                    marioIndex++;
                    mario.advancePathIndex();
                }
                m_startingEdge = true;

            }
            else
            {
                if(!mario.isFollowingNewPath())
                    m_startingEdge = false;
            }
            
        }

        if(mario.isFollowingNewPath())
        {
            m_startingEdge = true;
            //Alright, no new path any more.
            mario.followingNewPath(false);
        }

        if(m_startingEdge)
        {
            m_preSlowdownCount = 1;
        }
        
        //Check if we are at the end
        if(marioIndex >= curPath.m_points.size()-1)
        {
            //Path is ended.
            mario.setFollowingPath(false);
            m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
        }
        else
        {
            //Path not ended, need to go to marioIndex+1
            int nodeIdMario = curPath.m_points.get(marioIndex);
            int nodeIdDest = curPath.m_points.get(marioIndex+1);

            //System.out.println(nodeIdMario + " -> " + nodeIdDest);

            //Check if we are lost!
            if(marioOnGround && (marioRealNode != nodeIdMario && marioRealNode != nodeIdDest))
            {
                if(curPath.m_points.contains(marioRealNode))
                {
                    //We are lost, but we can be back on the path
                    int newIndex = curPath.m_points.indexOf(marioRealNode);
                    if(newIndex == curPath.m_points.size()-1)
                    {
                        //Path is ended.
                        mario.setFollowingPath(false);
                        m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
                    }
                    else if(newIndex != -1)
                    {
                        marioIndex = newIndex;
                        mario.setCurrentPathIndex(marioIndex);
                        nodeIdMario = curPath.m_points.get(marioIndex);
                        nodeIdDest = curPath.m_points.get(marioIndex+1);
                    }
                }
                else
                {
                    //Who knows why are we here, FAIL
                    m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                    mario.setFollowingPath(false);
                    return;
                }

            }

            //Check for decceleration
            int futureLinkType1 = Graph.MODE_UNDEF;
            int nextNodeIdDest = -1;
            Edge nextLink = null;
            if(marioIndex+2 < curPath.m_points.size())
            {
                nextNodeIdDest = curPath.m_points.get(marioIndex+2);
                nextLink = mario.getMap().getGraph().getEdge(nodeIdDest, nextNodeIdDest);
                if( nextLink != null ) futureLinkType1 = nextLink.getMode();
            }

            int futureLinkType2 = Graph.MODE_UNDEF;
            int futureNodeIdDest = -1;
            Edge futureLink = null;
            if(marioIndex+3 < curPath.m_points.size() && nextNodeIdDest!=-1 /*this last one shouldnt ever happen*/)
            {
                futureNodeIdDest = curPath.m_points.get(marioIndex+3);
                futureLink = mario.getMap().getGraph().getEdge(nextNodeIdDest, futureNodeIdDest);
                if( futureLink != null ) futureLinkType2 = futureLink.getMode();
            }

            Edge link = mario.getMap().getGraph().getEdge(nodeIdMario, nodeIdDest);
            if(link == null)
            {
                //Link broken, path impossible to follow. FAIL.
                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                mario.setFollowingPath(false);
            }
            else
            {

                //Let's see the type of edge:
                m_currentLinkType = link.getMode();
                if(m_currentLinkType == Graph.MODE_BREAKABLE)
                {
                    //Breakable. Vertical jump!
                    Node marioNode = mario.getMap().getGraph().getNode(nodeIdMario);
                    Node destNode = mario.getMap().getGraph().getNode(nodeIdDest);

                    if(!verticalJump(mario,marioNode,destNode))
                    {
                        //The jumop is done... now:
                        //We need to recalculate the path, but with the same destination as before
                        int nodeDest = curPath.m_destinationID;

                        if(mario.getMap().getGraph().existsNode(nodeDest) != -1)
                        {
                            Path pathToDestination = mario.getMap().getGraph().getPath(nodeIdMario, nodeDest, mario);
                            if(pathToDestination.m_points.size() > 1 && pathToDestination.m_cost < Integer.MAX_VALUE)
                            {
                                mario.setPath(pathToDestination);
                                m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
                                mario.followingNewPath(true);
                                m_currentLinkType = 0;
                            }else
                            {
                                //Something failed (ex: I broke the path, I couldnt break the block...)
                                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                                mario.setFollowingPath(false);
                                m_currentLinkType = 0;
                            }
                        }
                        else
                        {
                            //Something failed (ex: I broke the path, I couldnt break the block...)
                            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                            mario.setFollowingPath(false);
                            m_currentLinkType = 0;
                        }
                    }
                    return;
                }

                switch(link.getMode())
                {

                    case Graph.MODE_WALK_SMALL:
                        if(mario.isMarioLarge())
                        {
                            //Mario large, but edge for small mario.
                            //THIS COLUD HAPPEN IF MARIO EATS A MUSHROOM WHILE
                            //FOLLOWING THE PATH! path impossible to follow. FAIL.
                            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                            mario.setFollowingPath(false);
                            break;
                        }

                    case Graph.MODE_WALK_BIG:
                        
                        //WALK!!
                        Node marioNode = mario.getMap().getGraph().getNode(nodeIdMario);
                        Node destNode = mario.getMap().getGraph().getNode(nodeIdDest);

                        //Check for possible deceleration.
                        float marioSpeedX = mario.getMarioSpeedX();
                        boolean shouldISlowDown = false;
                        boolean shouldISlowDown2 = false;
                        int aboutToJump = 0;
                        boolean checkJump = true;

                        //2 before jumping
                        if( /*futureLinkType2 == Graph.MODE_UNDEF ||*/
                            futureLinkType2 == Graph.MODE_JUMP_BIG || futureLinkType2 == Graph.MODE_JUMP_SMALL ||
                            futureLinkType2 >= Graph.MODE_FAITH_JUMP || futureLinkType2 == Graph.MODE_BREAKABLE)
                        {

                            Node dest = mario.getMap().getGraph().getNode(futureNodeIdDest);
                            if(dest != null)
                            {
                                if(futureLink.getMode() >= Graph.MODE_FAITH_JUMP &&
                                    futureLink.getMetadata() != Edge.META_HOLE)
                                    checkJump = false;

                                if((futureLink.getMode() == Graph.MODE_JUMP_BIG ||
                                    futureLink.getMode() == Graph.MODE_JUMP_SMALL) &&
                                    futureLink.getMetadata() == Edge.META_SOLID)
                                    checkJump = false;
                            }

                            if(checkJump)
                            {
                                //I should slow down? Cool Mario, relax!
                                aboutToJump++;
                                shouldISlowDown2 = (marioNode.getX() < destNode.getX()) && (marioSpeedX > 3); //right too fast
                                shouldISlowDown2 |= (marioNode.getX() > destNode.getX()) && (marioSpeedX < -3); //left too fast
                            }
                        }

                        if( /*futureLinkType1 == Graph.MODE_UNDEF || */
                            futureLinkType1 == Graph.MODE_JUMP_BIG || futureLinkType1 == Graph.MODE_JUMP_SMALL ||
                            futureLinkType1 >= Graph.MODE_FAITH_JUMP || futureLinkType1 == Graph.MODE_BREAKABLE)
                        {
                            checkJump = true;
                            Node dest = mario.getMap().getGraph().getNode(nextNodeIdDest);
                            if(dest != null)
                            {
                                if(nextLink.getMode() >= Graph.MODE_FAITH_JUMP &&
                                    nextLink.getMetadata() != Edge.META_HOLE)
                                    checkJump = false;

                                if((nextLink.getMode() == Graph.MODE_JUMP_BIG ||
                                    nextLink.getMode() == Graph.MODE_JUMP_SMALL) &&
                                    nextLink.getMetadata() == Edge.META_SOLID)
                                    checkJump = false;
                            }

                            if(checkJump)
                            {
                                //I should slow down? Cool Mario, relax!
                                aboutToJump++;
                                shouldISlowDown = (marioNode.getX() < destNode.getX()) && (marioSpeedX > 3); //right too fast
                                shouldISlowDown |= (marioNode.getX() > destNode.getX()) && (marioSpeedX < -3); //left too fast

                                if(shouldISlowDown) m_preSlowdownCount++;
                            }
                        }

                        if(shouldISlowDown || shouldISlowDown2)
                        {
                            //NOP this time
                            if(marioNode.getX() < destNode.getX())
                                mario.setAction(Mario.KEY_LEFT,true);
                            else
                                mario.setAction(Mario.KEY_RIGHT,true);
                        }
                        else
                        {
                            //To the right?
                            if(marioNode.getX() < destNode.getX())
                            {
                                //Walk right
                                mario.setAction(Mario.KEY_RIGHT,true);
                            }else
                            {
                                //Walk left
                                mario.setAction(Mario.KEY_LEFT,true);
                            }
                            if(aboutToJump == 0)
                                mario.setAction(Mario.KEY_SPEED,true);
                        }

                        break;

                    case Graph.MODE_JUMP_SMALL:
                        if(mario.isMarioLarge())
                        {
                            //Mario large, but edge for small mario.
                            //THIS COLUD HAPPEN IF MARIO EATS A MUSHROOM WHILE
                            //FOLLOWING THE PATH! path impossible to follow. FAIL.
                            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                            mario.setFollowingPath(false);
                            break;
                        }

                    case Graph.MODE_JUMP_BIG:

                        //JUMP!!
                        marioNode = mario.getMap().getGraph().getNode(nodeIdMario);
                        destNode = mario.getMap().getGraph().getNode(nodeIdDest);

                        if(marioNode.getX() == destNode.getX())
                        {
                            if(!verticalJump(mario,marioNode,destNode))
                            {
                                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                                mario.setFollowingPath(false);
                            }
                        }
                        else
                        {
                            boolean alreadyUnder = false;
                            boolean done = false;
                            if(marioNode.getX() > destNode.getX())
                                alreadyUnder = mario.isMarioOnLeftOf(marioNode.getX());
                            else
                                alreadyUnder = mario.isMarioOnRightOf(marioNode.getX());

                            if(alreadyUnder)
                            {
                                boolean obs = mario.getMap().
                                        checkObstacle(destNode.getX(), marioNode.getY(),
                                        destNode.getX(), destNode.getY());
                                if(!obs)
                                {
                                    if(!verticalJump(mario,marioNode,destNode))
                                    {
                                        m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                                        mario.setFollowingPath(false);
                                    }
                                    else
                                    {
                                        done = true;
                                    }
                                }
                            }

                            if(!done)
                            {
                                if(!manageJump(mario,marioNode,destNode, link))
                                {
                                    m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                                    mario.setFollowingPath(false);
                                }
                            }
                        }

                        break;

                    case Graph.MODE_FALL_SMALL:
                        if(mario.isMarioLarge())
                        {
                            //Mario large, but edge for small mario.
                            //THIS COLUD HAPPEN IF MARIO EATS A MUSHROOM WHILE
                            //FOLLOWING THE PATH! path impossible to follow. FAIL.
                            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                            mario.setFollowingPath(false);
                            break;
                        }

                    case Graph.MODE_FALL_BIG:

                        //Fall!
                        marioNode = mario.getMap().getGraph().getNode(nodeIdMario);
                        destNode = mario.getMap().getGraph().getNode(nodeIdDest);

                        manageFall(mario,marioNode,destNode);
                        
                        break;

                    case Graph.MODE_BREAKABLE:

                        marioNode = mario.getMap().getGraph().getNode(nodeIdMario);
                        destNode = mario.getMap().getGraph().getNode(nodeIdDest);

                        verticalJump(mario,marioNode,destNode);
                        break;


                    default:

                        boolean faithJumpOk = false;
                        if(link.getMode() >= Graph.MODE_FAITH_JUMP &&
                           link.getMode() < Graph.MODE_FAITH_JUMP*2)
                        {
                            //faith jump for small guys
                            if(mario.isMarioLarge())
                            {
                                //Mario large, but edge for small mario.
                                //THIS COLUD HAPPEN IF MARIO EATS A MUSHROOM WHILE
                                //FOLLOWING THE PATH! path impossible to follow. FAIL.
                                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                                mario.setFollowingPath(false);
                                break;
                            }else faithJumpOk = true;

                        }else if(link.getMode() >= Graph.MODE_FAITH_JUMP*2) faithJumpOk = true;

                        if(faithJumpOk)
                        {
                            //JUMP!!
                            marioNode = mario.getMap().getGraph().getNode(nodeIdMario);
                            destNode = mario.getMap().getGraph().getNode(nodeIdDest);

                            if(!manageJump(mario,marioNode,destNode,link))
                            {
                                m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                                mario.setFollowingPath(false);
                            }
                        }
                }
            }
        }
    }
    
    private boolean manageJump(GEBT_MarioAgent mario, Node a_marioNode,
                                Node a_destNode, Edge a_edge)
    {
        int marioInertia = mario.getInertia();
        float marioSpeedX = mario.getMarioSpeedX();

        if(m_startingEdge)
        {
            //1.a PRECALCULATION OF THE JUMP

            //NO OPERATION!
            m_xJumpDistance = a_destNode.getX() - a_marioNode.getX();
            m_yJumpDistance = a_destNode.getY() - a_marioNode.getY();

            //Get & Increase edge attempts.
            int prevAttempts = 0;
            long edgeId = a_edge.getID();
            Integer numAttempts = m_edgeAttempts.get(edgeId);
            if(numAttempts == null) m_edgeAttempts.put(edgeId, 1);
            else
            {
                prevAttempts = numAttempts;
                m_edgeAttempts.put(edgeId, prevAttempts+1);
            }

            m_jumpDestHolesMode = a_destNode.getHoles();
            m_destOnPot = a_destNode.getMetadata() == Map.MAP_FLOWER_POT;

            //Decide modifiers depending on the number of attempts.
            m_jumpPressSpeed = false;
            m_grantLRMove = false;


            if(a_edge.getMetadata() == Edge.META_HOLE && m_xJumpDistance > 3)
            {
                m_jumpPressSpeed = true;
                m_grantLRMove = true;
            }
            else
            {
                switch(prevAttempts)
                {
                    case 0:
                        //Normal case.
                        if(m_xJumpDistance > 3 || m_yJumpDistance > 3)
                            m_jumpPressSpeed = true;
                        if(Math.abs(m_xJumpDistance) == 1 && m_yJumpDistance == 1)
                            m_grantLRMove = true;
                        break;
                    case 1:
                        m_jumpPressSpeed = true;
                        m_grantLRMove = true;
                        break;
                    case 2:
                        m_jumpPressSpeed = true;
                        break;
                    case 3:
                        m_grantLRMove = true;
                        break;
                    default:
                        m_jumpPressSpeed = true;
                        m_grantLRMove = true;
                        break;
                }
            }

            //reset the state:
            m_readyToJump = false;
            m_stopJumping = false;
            m_jumpStarted = false;
            m_jumpAttempts = 1;
            m_redoJump = false;
            m_forceMove = 0;
            mario.getMoveSimulator().reset();
            mario.getMoveSimulator().resetReg();
        }
        else if( m_redoJump )
        {
            //1.b PRECALCULATION OF THE JUMP, again
            
            //reset PART of the state:
            m_readyToJump = false;
            if(m_jumpAttempts == 3)
                m_readyToJump = true;
            m_stopJumping = false;
            m_jumpStarted = false;
            m_forceMove = 0;
            m_redoJump = false;
            mario.getMoveSimulator().reset();
        }
        else if(!m_readyToJump)
        {
            //2. PREPARING TO JUMP.
            
            if(mario.isMarioOnLeftOf(a_marioNode.getX()))
            {
                //we are at the left of the source node, go right
                mario.setAction(Mario.KEY_RIGHT,true);
            }
            else if (mario.isMarioOnRightOf(a_marioNode.getX()))
            {
                //We are at the right, go left.
                mario.setAction(Mario.KEY_LEFT,true);
            }
            else if (mario.isMarioOnPos(a_marioNode.getX()))
            {
                boolean checkSpeed = true;
                if( a_edge.getMode() >= Graph.MODE_FAITH_JUMP &&
                    a_edge.getMetadata() != Edge.META_HOLE &&
                    m_jumpDestHolesMode != 0)
                     checkSpeed = false;
                
                if(checkSpeed /*&& Math.abs(marioSpeedX) < 0.25*/)//0.25) //0.1
                {
                    
                    m_readyToJump = true;
                    float speed = marioSpeedX != 0 ? marioSpeedX : m_lastSpeed;

                    //Face properly. If we are facing badly, we are going to do 2 movements
                    //to face correctly and stay at the same spot: 1st in the wrong direction
                    //(where Mario is facing to) and 2nd in the correct one (where Mario should).
                    /*if(speed < 0 && m_xJumpDistance>0) //jumping right but looking left.
                    {
                        mario.setAction(Mario.KEY_LEFT,true); //1st, wrong way
                        m_forceMove = 1;    //2nd force RIGHT in next cycle.
                    }
                    else if (speed > 0 && m_xJumpDistance<0) //jumping left but looking right.
                    {
                        mario.setAction(Mario.KEY_RIGHT,true); //1st, wrong way.
                        m_forceMove = -1;   //2nd force LEFT in next cycle.
                    }*/

                    if( m_xJumpDistance>0 )
                    {
                        //Going to the right.

                        if(Math.abs(speed) > 1.0)
                        {
                            //Going too fast:
                            if(speed > 0.0)
                                mario.setAction(Mario.KEY_LEFT,true);
                            else if(speed < 0.0)
                                mario.setAction(Mario.KEY_RIGHT,true);
                            m_readyToJump = false;
                        }
                        else if(speed < 0)
                        {
                             //jumping right but looking left.
                            mario.setAction(Mario.KEY_LEFT,true); //1st, wrong way
                            m_forceMove = 1;    //2nd force RIGHT in next cycle.
                        } 

                    }else if (m_xJumpDistance<0)
                    {
                        if(Math.abs(speed) > 1.0)
                        {
                            //Going too fast:
                            if(speed > 0.0)
                                mario.setAction(Mario.KEY_LEFT,true);
                            else if(speed < 0.0)
                                mario.setAction(Mario.KEY_RIGHT,true);
                            
                            m_readyToJump = false;
                        }
                        else if (speed > 0) //jumping left but looking right.
                        {
                            mario.setAction(Mario.KEY_RIGHT,true); //1st, wrong way.
                            m_forceMove = -1;   //2nd force LEFT in next cycle.
                        } 
                    }




                }
                else
                {
                    m_readyToJump = true;
                }

                //Check jumps to pots:
                if(m_readyToJump && m_destOnPot)
                {
                    boolean floOut = a_destNode.isFlowerOut(mario.getMap().getGraph());
                    if(floOut)
                    {
                        m_readyToJump = false;
                    }        
                }
            }               
        }
        else if(m_forceMove != 0)
        {
            if(m_forceMove > 0){
                mario.setAction(Mario.KEY_RIGHT,true);
            }else{
                mario.setAction(Mario.KEY_LEFT,true);
            }
            m_forceMove = 0;
        }
        else if(!m_stopJumping)
        {
            //3. IN THE PROCESS OF JUMPING!!

            //Check for possible problems:
            if(m_jumpStarted && mario.amIOnGround())
            {
                //WTF! We have missed the jump, but don't panic.
                //Have we passed the fail limit, OR
                //we are in other node than the origin of the jump?
                if((m_jumpAttempts > 2) || (mario.getMarioNodeInMap() != a_marioNode.getID()))
                {
                    //Ok, fail, we cannot make this jump
                    return false;
                }
                else{
                    //We at the origin yet, make another attempt.
                    m_jumpAttempts++;
                    m_startingEdge = true;
                    m_redoJump = true;
                    m_lastSpeed = marioSpeedX;
                    return true;
                }
            }

            //Where would I land if I dont press any more keys?
            m_stopJumping = mario.getMoveSimulator().simulateFall(a_destNode.getX(), a_destNode.getY(), m_xJumpDistance>0);
            if(!m_stopJumping)
            {
                //JUMP
                mario.setAction(Mario.KEY_JUMP,true);
                if(m_jumpPressSpeed)
                    mario.setAction(Mario.KEY_SPEED,true);

                //LEFT-RIGHT
                if(m_grantLRMove || mario.isOverOrEqualTo(a_destNode.getY()))
                {
                    manageLeftRight(mario, a_destNode, marioInertia);
                }

                m_jumpStarted = true;
                mario.registerActions();
            }
        }
        else
        {
            //4. JUMP IS DONE, JUST FALLING

            //I'm done pressing JUMP, just manage inertia to reduce fall speed.
            manageLeftRightInertia(mario, marioInertia, marioSpeedX ,a_edge);

            if(mario.amIOnGround())
            {
                return false;
            }
        }

        m_lastSpeed = marioSpeedX;
        return true;
    }

    private void manageLeftRight(GEBT_MarioAgent mario, Node a_destNode, float a_xSpeed)
    {
        if(m_xJumpDistance > 0)
        {
            //JUMPING TO THE RIGHT!

            //Mario of the left of destination yet!?
            if(mario.isMarioOnLeftOf(a_destNode.getX()))
            {
                //I havent reached the position
                mario.setAction(Mario.KEY_RIGHT,true);
            }
            else if(mario.isMarioOnRightOf(a_destNode.getX()))
            {
                //I jumped further than the position!
                mario.setAction(Mario.KEY_LEFT,true);
            }
            else if(mario.isMarioOnPos(a_destNode.getX()))
            {
                //I am over the position
                if(a_xSpeed > 10)
                {
                    //But I am going further, lets correct that
                    mario.setAction(Mario.KEY_LEFT,true);
                }
            }

        }
        else if(m_xJumpDistance < 0)
        {
            //JUMPING TO THE LEFT!

            //Mario of the right of destination yet!?
            if(mario.isMarioOnRightOf(a_destNode.getX()))
            {
                mario.setAction(Mario.KEY_LEFT,true);
            }
            else if(mario.isMarioOnLeftOf(a_destNode.getX()))
            {
                mario.setAction(Mario.KEY_RIGHT,true);
            }
            else if(mario.isMarioOnPos(a_destNode.getX()))
            {
                //I am over the position
                if(a_xSpeed < -10)
                {
                    //But I am going further, lets correct that
                    mario.setAction(Mario.KEY_RIGHT,true);
                }
            }
        }
    }

    private void manageLeftRightInertia(GEBT_MarioAgent mario, float a_xInertia, float a_xSpeed, Edge a_edge)
    {
        int maxRightSpeed = 10;
        int maxLeftSpeed = -10;
        boolean jumpOverHole = false;
        if(a_edge != null)
            jumpOverHole = (a_edge.getMetadata() == Edge.META_HOLE);

        if(m_xJumpDistance > 0)
        {
            //JUMPING TO THE RIGHT!
            if(a_xInertia > maxRightSpeed)
            {
                //But I am going further, lets correct that
                mario.setAction(Mario.KEY_LEFT,true);
            }else if ((Math.abs(a_xSpeed) < 0.1) || (a_xInertia <= 1 && jumpOverHole))
            {
                mario.setAction(Mario.KEY_RIGHT,true);
            }
        }
        else if(m_xJumpDistance < 0)
        {
            //JUMPING TO THE LEFT!
            if(a_xInertia < maxLeftSpeed)
            {
                //But I am going further, lets correct that
                mario.setAction(Mario.KEY_RIGHT,true);
            }else if ((Math.abs(a_xSpeed) < 0.1) || (a_xInertia >= -1 && jumpOverHole))
            {
                mario.setAction(Mario.KEY_LEFT,true);
            }
        }
    }

    private boolean verticalJump(GEBT_MarioAgent mario, Node a_marioNode,
                                Node a_destNode)
    {
        int marioInertia = mario.getInertia();
        float marioSpeedX = mario.getMarioSpeedX();

        if(m_startingEdge)
        {
            //1.a PRECALCULATION OF THE JUMP

            //NO OPERATION!
            m_xJumpDistance = a_destNode.getX() - a_marioNode.getX();
            m_yJumpDistance = a_destNode.getY() - a_marioNode.getY();

            //Decide modifiers depending on the number of attempts.
            m_jumpPressSpeed = false;
            m_grantLRMove = false;

            //reset the state:
            m_readyToJump = false;
            m_stopJumping = false;
            m_jumpStarted = false;
            m_jumpAttempts = 1;
            m_redoJump = false;
            m_forceMove = 0;
            //m_currentLinkType = Graph.MODE_BREAKABLE;
            mario.getMoveSimulator().reset();
            mario.getMoveSimulator().resetReg();
        }
        else if(!m_readyToJump)
        {
            //2. PREPARING TO JUMP.

            if(mario.isMarioOnLeftOf(a_marioNode.getX()))
            {
                //we are at the left of the source node, go right
                mario.setAction(Mario.KEY_RIGHT,true);
            }
            else if (mario.isMarioOnRightOf(a_marioNode.getX()))
            {
                //We are at the right, go left.
                mario.setAction(Mario.KEY_LEFT,true);
            }
            else if (mario.isMarioOnPos(a_marioNode.getX()))
            {
                if(Math.abs(marioSpeedX) < 0.1)
                {

                    m_readyToJump = true;
                    float speed = marioSpeedX != 0 ? marioSpeedX : m_lastSpeed;

                    //Face properly. If we are facing badly, we are going to do 2 movements
                    //to face correctly and stay at the same spot: 1st in the wrong direction
                    //(where Mario is facing to) and 2nd in the correct one (where Mario should).
                    if(speed < 0 && m_xJumpDistance>0) //jumping right but looking left.
                    {
                        mario.setAction(Mario.KEY_LEFT,true); //1st, wrong way
                        m_forceMove = 1;    //2nd force RIGHT in next cycle.
                    }
                    else if (speed > 0 && m_xJumpDistance<0) //jumping left but looking right.
                    {
                        mario.setAction(Mario.KEY_RIGHT,true); //1st, wrong way.
                        m_forceMove = -1;   //2nd force LEFT in next cycle.
                    }
                }
            }

        }
        else if(m_forceMove != 0)
        {
            if(m_forceMove > 0){
                mario.setAction(Mario.KEY_RIGHT,true);
            }else{
                mario.setAction(Mario.KEY_LEFT,true);
            }
            m_forceMove = 0;
        }
        else if(!m_stopJumping)
        {
            //3. IN THE PROCESS OF JUMPING!!
            if(m_jumpStarted && mario.amIOnGround())
            {
                //We are done.
                return false;
            }

            //JUMP
            mario.setAction(Mario.KEY_JUMP,true);
            mario.setAction(Mario.KEY_SPEED,true);

            m_jumpStarted = true;
            mario.registerActions();
        }

        m_lastSpeed = marioSpeedX;
        return true;
    }


    private boolean manageFall(GEBT_MarioAgent mario, Node a_marioNode, Node a_destNode)
    {
        if(m_startingEdge)
        {
            //NO OPERATION!
            //BUT, let's calculate here the number of cycles I have to press jump!
            m_xJumpDistance = a_destNode.getX() - a_marioNode.getX();
            m_yJumpDistance = a_destNode.getY() - a_marioNode.getY();

            //reset the counter:
            m_readyToJump = false;
            m_stopJumping = false;
        }
        else if(mario.amIOnGround())
        {
            float marioSpeedX = mario.getMarioSpeedX();

            if(m_xJumpDistance > 0)
            {
                //JUMPING TO THE RIGHT!

                if(marioSpeedX > 3)
                {
                    mario.setAction(Mario.KEY_LEFT,true);                    
                }else if(marioSpeedX < 1)
                {
                    mario.setAction(Mario.KEY_RIGHT,true);
                }

            }
            else if(m_xJumpDistance < 0)
            {
                //JUMPING TO THE LEFT!
                if(marioSpeedX < -3)
                {
                    mario.setAction(Mario.KEY_RIGHT,true);
                }else if(marioSpeedX > -1)
                {
                    mario.setAction(Mario.KEY_LEFT,true);
                }
            }
        }
        else
        {
            int marioInertia = mario.getInertia();
            float marioSpeedX = mario.getMarioSpeedX();

            if((m_xJumpDistance>0) && (marioSpeedX > 0.25))
            {
                mario.setAction(Mario.KEY_LEFT,true);
            }
            else if((m_xJumpDistance<0) && (marioSpeedX < -0.25))
            {
                mario.setAction(Mario.KEY_RIGHT,true);
            }


            manageLeftRightInertia(mario, marioInertia, marioSpeedX, null);

            if(mario.amIOnGround())
            {
                return false;
            }
        }
        
        return true;
    }
}
