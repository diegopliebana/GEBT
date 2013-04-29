/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.bt.Actions;

import ch.idsia.mario.engine.sprites.Mario;
import grammaticalbehaviorsNoAstar.GEBT_Mario.GEBT_MarioAgent;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTConstants;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTLeafNode;
import grammaticalbehaviorsNoAstar.bt.behaviortree.BTNode;
import grammaticalbehaviorsNoAstar.bt.behaviortree.IncorrectNodeException;
import java.util.Vector;

/**
 *
 * @author Diego
 */
public class GoGapOnLeft extends BTLeafNode{

    int m_stepsInObjective;
    int m_nopTime;
    private final int MAX_OBJ_STEPS = 10;

    public GoGapOnLeft(BTNode a_parent)
    {
        super(a_parent);
        m_stepsInObjective = 0;
        m_nopTime = 0;
    }
    
    public void step() throws IncorrectNodeException 
    {
        super.step();
        
        //Get the agent
        Object agent = m_tree.getAgent();
        GEBT_MarioAgent mario = (GEBT_MarioAgent)agent;
        Vector<Integer> gaps = mario.getGaps();
        int marioPos = mario.getCellsRun();
        int objective = mario.getObjective();

        boolean thereIsGap = false;
        boolean forceRecalculate = false;

        if(objective == -2)
        {
            forceRecalculate = true;
        }


        boolean thereIsGapOnRight = false;
        int gapPosOnRight = -1;
        if(marioPos == objective)
        {
            int aheadStartPos = GEBT_MarioAgent.MARIO_Y;
            int aheadEndPos = GEBT_MarioAgent.MARIO_Y;
            boolean obsUp = mario.isObstacle(GEBT_MarioAgent.MARIO_X-3,GEBT_MarioAgent.MARIO_X-1,
                                                aheadStartPos,aheadEndPos);

            if(obsUp)
            {
                //I am at the objective, but there is an obstacle in my vertical... gap failed!
                //Need to recalculate
                forceRecalculate = true;
            }
        }

        if((objective == -1) || (marioPos != objective) || forceRecalculate)
        {
           int thisGap = -1;
           for(int i = 0;!thereIsGap && i < gaps.size(); ++i)
            {
                thisGap = gaps.get(i);
                if(thisGap < marioPos)
                {
                    boolean isForbidden = mario.isGapForbidden(thisGap);
                    //if(!isForbidden)
                    {
                        thereIsGap = true;
                        objective = thisGap;
                        mario.setObjective(thisGap);
                    }
                }
                else if(thisGap > marioPos)
                {
                    thereIsGapOnRight = true;
                    gapPosOnRight = thisGap;
                }
            }
        }

        if(forceRecalculate && objective == -2)
        {
            //FAILURE
            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
            return;
        }
        else if(forceRecalculate && !thereIsGap && thereIsGapOnRight)
        {
            thereIsGap = true;
            objective = gapPosOnRight;
            mario.setObjective(gapPosOnRight);
        }

        if(thereIsGap || (objective != -1))
        {
            
            //Enemy warnings:
            int aheadStartPos = GEBT_MarioAgent.MARIO_Y+1;
            int aheadEndPos = GEBT_MarioAgent.MARIO_Y+2;
            boolean needJumpAhead = mario.isEnemy(GEBT_MarioAgent.MARIO_X-1,GEBT_MarioAgent.MARIO_X,
                                                       aheadStartPos,aheadEndPos);

            needJumpAhead = needJumpAhead || mario.isMarioStuck();

            aheadStartPos = GEBT_MarioAgent.MARIO_Y-2;
            aheadEndPos = GEBT_MarioAgent.MARIO_Y-1;
            boolean needJumpBack = mario.isEnemy(GEBT_MarioAgent.MARIO_X-1,GEBT_MarioAgent.MARIO_X,
                                                       aheadStartPos,aheadEndPos);

            needJumpBack = needJumpBack || mario.isMarioStuck();
            
           //m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
           //Then go...
            if(m_nodeStatus != BTConstants.NODE_STATUS_EXECUTING)
            {
                m_stepsInObjective = 0;
            }

            //Get the agent
            m_nodeStatus = BTConstants.NODE_STATUS_EXECUTING;
            if(marioPos > objective)
            {
                //GO TO THE LEFT
                if(m_nopTime == 0) mario.setAction(Mario.KEY_LEFT,true);
                if(needJumpBack || needJumpAhead) mario.setAction(Mario.KEY_JUMP,true);
                m_nopTime++; m_nopTime %= 2;
                m_stepsInObjective = 0;
            }
            else if(marioPos < objective)
            {
                //GO TO THE RIGHT
                if(m_nopTime == 0) mario.setAction(Mario.KEY_RIGHT,true);
                if(needJumpAhead || needJumpBack) mario.setAction(Mario.KEY_JUMP,true);
                m_nopTime++; m_nopTime %= 2;
                m_stepsInObjective = 0;
            }
            else
            {
                 //Check for enemies.
                 if(needJumpBack || needJumpAhead)
                 {
                     mario.setAction(Mario.KEY_JUMP,true);
                 }
                 else
                 {
                    m_stepsInObjective++;
                    if(m_stepsInObjective == MAX_OBJ_STEPS)
                    {
                        aheadStartPos = GEBT_MarioAgent.MARIO_Y+1;
                        aheadEndPos = GEBT_MarioAgent.MARIO_Y+1;
                        boolean obsAhead = mario.isObstacle(GEBT_MarioAgent.MARIO_X-3,GEBT_MarioAgent.MARIO_X-2,
                                            aheadStartPos,aheadEndPos);

                        aheadStartPos = GEBT_MarioAgent.MARIO_Y;
                        aheadEndPos = GEBT_MarioAgent.MARIO_Y;
                        boolean obsUp = mario.isObstacle(GEBT_MarioAgent.MARIO_X-3,GEBT_MarioAgent.MARIO_X-1,
                                                            aheadStartPos,aheadEndPos);

                        if(obsAhead && !obsUp)
                        {
                            m_nodeStatus = BTConstants.NODE_STATUS_SUCCESS;
                            mario.setObjective(-1);
                        }
                        else
                        {
                            mario.setObjective(-2);
                        }

                    }
                 }
            }

            if(mario.isMarioStuck() && (m_stepsInObjective > MAX_OBJ_STEPS))
            {
               m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
                mario.setObjective(-1);
            }

        }
        else
        {
            m_nodeStatus = BTConstants.NODE_STATUS_FAILURE;
        }
        
    }
    
    
}
