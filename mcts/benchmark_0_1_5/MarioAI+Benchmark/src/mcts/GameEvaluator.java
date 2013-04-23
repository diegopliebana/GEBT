package mcts;

/**
 * Created by Diego Perez, University of Essex.
 * Date: 23/04/13
 */
public class GameEvaluator
{

    public static final int NUM_ACTIONS = 12;
    public static final int MACRO_ACTION_LENGTH = 8;
    public static final double MAX_FITNESS = Double.MAX_VALUE;

    public static final int ACTION_S_0_0 = 0;       //Stay, no jump, no speed
    public static final int ACTION_S_0_1 = 1;       //Stay, no jump, speed
    public static final int ACTION_S_1_1 = 2;       //Stay, jump, speed
    public static final int ACTION_S_1_0 = 3;       //Stay, jump, no speed

    public static final int ACTION_R_0_0 = 4;       //Right, no jump, no speed
    public static final int ACTION_R_0_1 = 5;       //Right, no jump, speed
    public static final int ACTION_R_1_1 = 6;       //Right, jump, speed
    public static final int ACTION_R_1_0 = 7;       //Right, jump, no speed

    public static final int ACTION_L_0_0 = 8;       //Left, no jump, no speed
    public static final int ACTION_L_0_1 = 9;       //Left, no jump, speed
    public static final int ACTION_L_1_1 = 10;       //Left, jump, speed
    public static final int ACTION_L_1_0 = 11;       //Left, jump, no speed


    public static int getMove(int a_actionId)       //0: stay, 1: right, 2: left
    {
        if(a_actionId >= ACTION_S_0_0 && a_actionId <= ACTION_S_1_0)
            return 0;
        if(a_actionId >= ACTION_R_0_0 && a_actionId <= ACTION_R_1_0)
            return 1;

        //if(a_actionId >= ACTION_L_0_0 && a_actionId <= ACTION_L_1_0)
            return 2;
    }

    public static boolean getJump(int a_actionId)
    {
        if(a_actionId == ACTION_S_1_1 || a_actionId == ACTION_S_1_0)
            return true;

        if(a_actionId == ACTION_R_1_1 || a_actionId == ACTION_R_1_0)
            return true;

        if(a_actionId == ACTION_L_1_1 || a_actionId == ACTION_L_1_0)
            return true;

        return false;
    }

    public static boolean getSpeed(int a_actionId)
    {
        if(a_actionId == ACTION_S_0_0 || a_actionId == ACTION_S_0_1)
            return true;

        if(a_actionId == ACTION_R_0_0 || a_actionId == ACTION_R_0_1)
            return true;

        if(a_actionId == ACTION_L_0_0 || a_actionId == ACTION_L_0_1)
            return true;

        return false;
    }


    public static int getActionFromInput(int a_h, boolean a_j, boolean a_s)
    {
        if(a_h == 0)
        {
            //Stay
            if(!a_j && !a_s) return ACTION_S_0_0;
            if(!a_j && a_s) return ACTION_S_0_1;
            if(a_j && a_s) return ACTION_S_1_1;
            if(a_j && !a_s) return ACTION_S_1_0;

        }else if(a_h == 1)
        {
            //Right
            if(!a_j && !a_s) return ACTION_R_0_0;
            if(!a_j && a_s) return ACTION_R_0_1;
            if(a_j && a_s) return ACTION_R_1_1;
            if(a_j && !a_s) return ACTION_R_1_0;

        }else if(a_h == 2)
        {
            //Left
            if(!a_j && !a_s) return ACTION_L_0_0;
            if(!a_j && a_s) return ACTION_L_0_1;
            if(a_j && a_s) return ACTION_L_1_1;
            if(a_j && !a_s) return ACTION_L_1_0;
        }
        throw new RuntimeException("Error in GameEvaluator.getActionFromInput(): unknown action from inputs.");
    }


    public GameEvaluator() {}


    public static boolean isEndGame(MarioSimulator a_ms)
    {
        //TODO
        return false;
    }


    public double scoreGame(MarioSimulator a_ms)
    {
        //TODO
        return 0.0f;
    }


}
