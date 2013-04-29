/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.GEBT_Mario;

import grammaticalbehaviorsNoAstar.bt.Actions.*;
import grammaticalbehaviorsNoAstar.bt.Conditions.*;
import grammaticalbehaviorsNoAstar.bt.behaviortree.*;
import java.util.Hashtable;

/**
 *
 * @author Diego
 */
public class MarioXMLReader extends XML_BTReader {

    
    public BTLeafNode readNode(BTNode a_parent, Hashtable<String, String> a_properties)
    {
        BTLeafNode newNode = null;
        String opN = a_properties.get("Name"); 
        
        //ACTIONS
        if(opN.compareToIgnoreCase("CrouchRunLeft") == 0)       newNode = new CrouchRunLeft(a_parent);
        else if(opN.compareToIgnoreCase("CrouchRunRight") == 0) newNode = new CrouchRunRight(a_parent);
        else if(opN.compareToIgnoreCase("Down") == 0)           newNode = new Down(a_parent);
        else if(opN.compareToIgnoreCase("Fire") == 0)           newNode = new Fire(a_parent);
        else if(opN.compareToIgnoreCase("GoGapOnLeft") == 0)    newNode = new GoGapOnLeft(a_parent);
        else if(opN.compareToIgnoreCase("GoGapOnRight") == 0)   newNode = new GoGapOnRight(a_parent);
        else if(opN.compareToIgnoreCase("JumpLeft") == 0)       newNode = new JumpLeft(a_parent);
        else if(opN.compareToIgnoreCase("JumpLeftRun") == 0)    newNode = new JumpLeftRun(a_parent);
        else if(opN.compareToIgnoreCase("JumpRight") == 0)      newNode = new JumpRight(a_parent);
        else if(opN.compareToIgnoreCase("JumpRightRun") == 0)   newNode = new JumpRightRun(a_parent);
        else if(opN.compareToIgnoreCase("NOP") == 0)            newNode = new NOP(a_parent);
        else if(opN.compareToIgnoreCase("RunLeft") == 0)        newNode = new RunLeft(a_parent);
        else if(opN.compareToIgnoreCase("RunRight") == 0)       newNode = new RunRight(a_parent);
        else if(opN.compareToIgnoreCase("Speed") == 0)          newNode = new Speed(a_parent);
        else if(opN.compareToIgnoreCase("VerticalJump") == 0)   newNode = new VerticalJump(a_parent);
        else if(opN.compareToIgnoreCase("WalkLeft") == 0)       newNode = new WalkLeft(a_parent);
        else if(opN.compareToIgnoreCase("WalkRight") == 0)      newNode = new WalkRight(a_parent);

        
        //CONDITIONS
        else if(opN.compareToIgnoreCase("AvailableJumpAhead") == 0)   newNode = new AvailableJumpAhead(a_parent);
        else if(opN.compareToIgnoreCase("AvailableJumpBack") == 0)    newNode = new AvailableJumpBack(a_parent);
        else if(opN.compareToIgnoreCase("CanIFire") == 0)             newNode = new CanIFire(a_parent);
        else if(opN.compareToIgnoreCase("CanIJump") == 0)             newNode = new CanIJump(a_parent);
        else if(opN.compareToIgnoreCase("EnemyAhead") == 0)           newNode = new EnemyAhead(a_parent);
        else if(opN.compareToIgnoreCase("EnemyAheadDown") == 0)       newNode = new EnemyAheadDown(a_parent);
        else if(opN.compareToIgnoreCase("EnemyAheadUp") == 0)         newNode = new EnemyAheadUp(a_parent);
        else if(opN.compareToIgnoreCase("EnemyBack") == 0)            newNode = new EnemyBack(a_parent);
        else if(opN.compareToIgnoreCase("EnemyBackDown") == 0)        newNode = new EnemyBackDown(a_parent);
        else if(opN.compareToIgnoreCase("EnemyBackUp") == 0)          newNode = new EnemyBackUp(a_parent);
        else if(opN.compareToIgnoreCase("Failure") == 0)              newNode = new Failure(a_parent);
        else if(opN.compareToIgnoreCase("HoleAhead") == 0)            newNode = new HoleAhead(a_parent);
        else if(opN.compareToIgnoreCase("HoleBack") == 0)             newNode = new HoleBack(a_parent);
        else if(opN.compareToIgnoreCase("IsALeftTrap") == 0)          newNode = new IsALeftTrap(a_parent);
        else if(opN.compareToIgnoreCase("IsARightTrap") == 0)         newNode = new IsARightTrap(a_parent);
        else if(opN.compareToIgnoreCase("IsBreakableUp") == 0)        newNode = new IsBreakableUp(a_parent);
        else if(opN.compareToIgnoreCase("IsBreakableUpAhead") == 0)   newNode = new IsBreakableUpAhead(a_parent);
        else if(opN.compareToIgnoreCase("IsBreakableUpBack") == 0)    newNode = new IsBreakableUpBack(a_parent);
        else if(opN.compareToIgnoreCase("IsClimbableUp") == 0)        newNode = new IsClimbableUp(a_parent);
        else if(opN.compareToIgnoreCase("IsClimbableUpAhead") == 0)   newNode = new IsClimbableUpAhead(a_parent);
        else if(opN.compareToIgnoreCase("IsClimbableUpBack") == 0)    newNode = new IsClimbableUpBack(a_parent);
        else if(opN.compareToIgnoreCase("IsGapOnRight") == 0)         newNode = new IsGapOnRight(a_parent);
        else if(opN.compareToIgnoreCase("IsGapOnLeft") == 0)          newNode = new IsGapOnLeft(a_parent);
        else if(opN.compareToIgnoreCase("IsItemUp") == 0)             newNode = new IsItemUp(a_parent);
        else if(opN.compareToIgnoreCase("IsItemUpAhead") == 0)        newNode = new IsItemUpAhead(a_parent);
        else if(opN.compareToIgnoreCase("IsItemUpBack") == 0)         newNode = new IsItemUpBack(a_parent);
        else if(opN.compareToIgnoreCase("IsGapOnLeft") == 0)          newNode = new IsGapOnLeft(a_parent);
        else if(opN.compareToIgnoreCase("IsJumpPlatformUpAhead") == 0) newNode = new IsJumpPlatformUpAhead(a_parent);
        else if(opN.compareToIgnoreCase("IsJumpPlatformUpBack") == 0)  newNode = new IsJumpPlatformUpBack(a_parent);
        else if(opN.compareToIgnoreCase("IsPushableUp") == 0)         newNode = new IsPushableUp(a_parent);
        else if(opN.compareToIgnoreCase("IsPushableUpAhead") == 0)    newNode = new IsPushableUpAhead(a_parent);
        else if(opN.compareToIgnoreCase("IsPushableUpBack") == 0)     newNode = new IsPushableUpBack(a_parent);
        else if(opN.compareToIgnoreCase("IsStuck") == 0)              newNode = new IsStuck(a_parent);
        else if(opN.compareToIgnoreCase("IsTrapRightEnded") == 0)     newNode = new IsTrapRightEnded(a_parent);
        else if(opN.compareToIgnoreCase("IsTrapLeftEnded") == 0)      newNode = new IsTrapLeftEnded(a_parent);
        else if(opN.compareToIgnoreCase("JumpableEnemyAhead") == 0)   newNode = new JumpableEnemyAhead(a_parent);
        else if(opN.compareToIgnoreCase("JumpableEnemyBack") == 0)    newNode = new JumpableEnemyBack(a_parent);
        else if(opN.compareToIgnoreCase("NoJumpableEnemyAhead") == 0) newNode = new NoJumpableEnemyAhead(a_parent);
        else if(opN.compareToIgnoreCase("NoJumpableEnemyBack") == 0)  newNode = new NoJumpableEnemyBack(a_parent);
        else if(opN.compareToIgnoreCase("ObstacleAhead") == 0)        newNode = new ObstacleAhead(a_parent);
        else if(opN.compareToIgnoreCase("ObstacleBack") == 0)         newNode = new ObstacleBack(a_parent);
        else if(opN.compareToIgnoreCase("ObstacleHead") == 0)         newNode = new ObstacleHead(a_parent);
        else if(opN.compareToIgnoreCase("ObstacleHeadBack") == 0)     newNode = new ObstacleHeadBack(a_parent);
        else if(opN.compareToIgnoreCase("ObstacleUp") == 0)           newNode = new ObstacleUp(a_parent);
        else if(opN.compareToIgnoreCase("Success") == 0)              newNode = new Success(a_parent);
        
        return newNode;
    }
}
