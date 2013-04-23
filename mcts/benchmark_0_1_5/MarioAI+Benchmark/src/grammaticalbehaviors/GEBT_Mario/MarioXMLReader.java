/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.GEBT_Mario;

import grammaticalbehaviors.bt.Actions.*;
import grammaticalbehaviors.bt.Conditions.*;
import grammaticalbehaviors.bt.behaviortree.*;
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
        if(opN.compareToIgnoreCase("Down") == 0)           newNode = new Down(a_parent);
        else if(opN.compareToIgnoreCase("Fire") == 0)           newNode = new Fire(a_parent);
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
        else if(opN.compareToIgnoreCase("NoPathAction") == 0)   newNode = new NoPathAction(a_parent);

        
        //CONDITIONS
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
        else if(opN.compareToIgnoreCase("IsStuck") == 0)              newNode = new IsStuck(a_parent);
        else if(opN.compareToIgnoreCase("JumpableEnemyAhead") == 0)   newNode = new JumpableEnemyAhead(a_parent);
        else if(opN.compareToIgnoreCase("JumpableEnemyBack") == 0)    newNode = new JumpableEnemyBack(a_parent);
        else if(opN.compareToIgnoreCase("NoJumpableEnemyAhead") == 0) newNode = new NoJumpableEnemyAhead(a_parent);
        else if(opN.compareToIgnoreCase("NoJumpableEnemyBack") == 0)  newNode = new NoJumpableEnemyBack(a_parent);
        else if(opN.compareToIgnoreCase("ObstacleAhead") == 0)        newNode = new ObstacleAhead(a_parent);
        else if(opN.compareToIgnoreCase("ObstacleBack") == 0)         newNode = new ObstacleBack(a_parent);
        else if(opN.compareToIgnoreCase("Success") == 0)              newNode = new Success(a_parent);



        //New ones GIC'10
        if(opN.compareToIgnoreCase("GetPathToRightMost") == 0)         newNode = new GetPathToRightMost(a_parent);
        if(opN.compareToIgnoreCase("GetPathToLeftMost") == 0)          newNode = new GetPathToLeftMost(a_parent);
        if(opN.compareToIgnoreCase("GetPathToGround") == 0)            newNode = new GetPathToGround(a_parent);
        if(opN.compareToIgnoreCase("GetPathToTop") == 0)               newNode = new GetPathToTop(a_parent);
        if(opN.compareToIgnoreCase("GetPathToClosestPotCannon") == 0)  newNode = new GetPathToClosestPotCannon(a_parent);
        if(opN.compareToIgnoreCase("GetPathToClosestBrick") == 0)      newNode = new GetPathToClosestBrick(a_parent);
        if(opN.compareToIgnoreCase("GetPathToClosestQuestion") == 0)   newNode = new GetPathToClosestQuestion(a_parent);
        if(opN.compareToIgnoreCase("GetPathToClosestItem") == 0)       newNode = new GetPathToClosestItem(a_parent);

        else if(opN.compareToIgnoreCase("IsFollowingPath") == 0)      newNode = new IsFollowingPath(a_parent);
        else if(opN.compareToIgnoreCase("GetPathAtoB") == 0)           newNode = new GetPathAtoB(a_parent);
        else if(opN.compareToIgnoreCase("IsRightMostCloseToEnd") == 0)newNode = new IsRightMostCloseToEnd(a_parent);
        else if(opN.compareToIgnoreCase("UnderBrick") == 0)           newNode = new UnderBrick(a_parent);
        else if(opN.compareToIgnoreCase("UnderQuestion") == 0)        newNode = new UnderQuestion(a_parent);
        else if(opN.compareToIgnoreCase("OverCannonPot") == 0)        newNode = new OverCannonPot(a_parent);
        else if(opN.compareToIgnoreCase("IsBulletToHead") == 0)        newNode = new IsBulletToHead(a_parent);
        else if(opN.compareToIgnoreCase("IsBulletToFeet") == 0)        newNode = new IsBulletToFeet(a_parent);

        if(opN.compareToIgnoreCase("FollowCurrentPath") == 0)         newNode = new FollowCurrentPath(a_parent);

        return newNode;
    }
}
