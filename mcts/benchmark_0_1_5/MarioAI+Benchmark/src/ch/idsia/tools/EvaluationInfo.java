package ch.idsia.tools;

import ch.idsia.maibe.tasks.MarioSystemOfValues;
import ch.idsia.maibe.tasks.SystemOfValues;
import ch.idsia.mario.engine.sprites.Mario;

import java.text.DecimalFormat;


/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 12, 2009
 * Time: 12:44:51 AM
 * Package: .Tools
 */

public final class EvaluationInfo
{
    private static final int MagicNumberUnDef = -42;

    public static final int numberOfElements = 14;

    // ordered in alphabetical order;
    public int distancePassedCells =MagicNumberUnDef;
    // TODO: migrate to all integers.
    public float distancePassedPhys =MagicNumberUnDef;
    public int flowersDevoured=MagicNumberUnDef;
    public int killsByFire=MagicNumberUnDef;
    public int killsByShell=MagicNumberUnDef;
    public int killsByStomp=MagicNumberUnDef;
    public int killsTotal=MagicNumberUnDef;
    public int marioMode=MagicNumberUnDef;
    public int marioStatus=MagicNumberUnDef;
    public int mushroomsDevoured=MagicNumberUnDef;
    public int numberOfCoinsGained=MagicNumberUnDef;
    public int numberOfHiddenItemsGained =MagicNumberUnDef;
    public int timeLeft=MagicNumberUnDef;
    public int timeSpent =MagicNumberUnDef;

    private static final float[] retFloatArray = new float[EvaluationInfo.numberOfElements];
    private static final float[] zeros = new float[EvaluationInfo.numberOfElements];
    public String Memo = "";
    
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static MarioSystemOfValues marioSystemOfValues = new MarioSystemOfValues();

    public EvaluationInfo()
    {
        System.arraycopy(EvaluationInfo.zeros, 0, retFloatArray, 0, EvaluationInfo.numberOfElements);
    }

    public float computeBasicFitness()
    {
        return distancePassedPhys - timeSpent + numberOfCoinsGained + marioStatus*marioSystemOfValues.win;
    }

    public float computeMultiObjectiveFitness(SystemOfValues sov)
    {
        return
                distancePassedPhys * sov.distance +
                flowersDevoured * sov.flowerFire +
                marioStatus * sov.win +
                marioMode * sov.mode  +
                mushroomsDevoured * sov.mushrooms +
                numberOfCoinsGained * sov.coins+
                killsTotal * sov.kills +
                killsByStomp * sov.killedByStomp +
                killsByFire * sov.killedByFire +
                killsByShell * sov.killedByShell +
                numberOfHiddenItemsGained * sov.hiddenItems +
                timeLeft * sov.timeLeft;
    }

    public float computeMultiObjectiveFitness()
    {
        return this.computeMultiObjectiveFitness(marioSystemOfValues);
    }

    public float computeDistancePassed()
    {
        return distancePassedPhys;
    }

    public int computeKillsTotal()
    {
        return this.killsTotal;
    }

    //TODO: possible fitnesses adjustments: penalize for collisions with creatures and especially for suicide. It's a sin.
    public float[] toFloatArray()
    {
        retFloatArray[0] = this.distancePassedCells;
        retFloatArray[1] = this.distancePassedPhys;
        retFloatArray[2] = this.flowersDevoured;
        retFloatArray[3] = this.killsByFire;
        retFloatArray[4] = this.killsByShell;
        retFloatArray[5] = this.killsByStomp;
        retFloatArray[6] = this.killsTotal;
        retFloatArray[7] = this.marioMode;
        retFloatArray[8] = this.marioStatus;
        retFloatArray[9] = this.mushroomsDevoured;
        retFloatArray[10] = this.numberOfCoinsGained;
        retFloatArray[11] = this.numberOfHiddenItemsGained;
        retFloatArray[12] = this.timeLeft;
        retFloatArray[13] = this.timeSpent;

        return retFloatArray;
    }

    public String toString()
    {
        return "\nEvaluation Information. Statistics and Score:" +
            "\n                       Mario Status : " + ((marioStatus == Mario.STATUS_WIN) ? "WIN!" : "Loss...") +
            "\n                         Mario Mode : " + Mario.MODES[marioMode] +
            "\n               Passed (Cells, Phys) : " + df.format((double) distancePassedCells) + ", " +
                                                         df.format(distancePassedPhys) +
            "\n           Time Spent(marioseconds) : " + timeSpent +
            "\n            Time Left(marioseconds) : " + timeLeft +
            "\n                       Coins Gained : " + numberOfCoinsGained +
            "\n                 Hidden Items Found : " + numberOfHiddenItemsGained +
            "\n                 Mushrooms Devoured : " + mushroomsDevoured +
            "\n                   Flowers Devoured : " + flowersDevoured +
            "\n                        kills Total : " + killsTotal +
            "\n                      kills By Fire : " + killsByFire +
            "\n                     kills By Shell : " + killsByShell +
            "\n                     kills By Stomp : " + killsByStomp +
            "\n               multiObjectiveFitness : " + df.format(computeMultiObjectiveFitness()) +
            ((Memo.equals("")) ? "" : "\nMemo: " + Memo);
    }

    public String toStringSingleLine()
    {
        return "##" +
        " Status: " + ((marioStatus == Mario.STATUS_WIN) ? "WIN!" : "Loss") +
        "; Mode: " + Mario.MODES[marioMode] +
        " +  Passed (Cells, Phys): " + df.format((double) distancePassedCells) + ", " +
                                                            df.format(distancePassedPhys) +
        "; Time Spent: " + timeSpent +
        "; Time Left: " + timeLeft +
        "; Coins: " + numberOfCoinsGained +
        "; Mushrooms: " + mushroomsDevoured +
        "; Flowers: " + flowersDevoured +        
        "; kills: " + killsTotal +
        "; By Fire: " + killsByFire +
        "; By Shell: " + killsByShell + 
        "; By Stomp: " + killsByStomp;
    }

//    public int levelType = MagicNumberUnDef;
//    public float totalLengthOfLevelCells = MagicNumberUnDef;
//    public float totalLengthOfLevelPhys = MagicNumberUnDef;
//    public int totalTimeGiven = MagicNumberUnDef;
//    public int totalNumberOfCoins = MagicNumberUnDef;
    // Number Of collisions with creatures
    // if large
    // if fire


//    public String agentName = "undefinedAgentName";
//    public String agentType = "undefinedAgentType";
//    public int levelDifficulty = MagicNumberUnDef;
//    public int levelRandSeed = MagicNumberUnDef;


//    public EvaluationInfo(float[] evaluationInfoArray)
//    {
//        // Turn double[] into a plausible form!
//        assert (evaluationInfoArray.length == 11);
//
//        this.marioStatus = (int) evaluationInfoArray[0];
//        this.distancePassedCells = (int) evaluationInfoArray[1];
//        this.distancePassedPhys = evaluationInfoArray[2];
//        this.totalLengthOfLevelCells = (int) evaluationInfoArray[3];
//        this.totalLengthOfLevelPhys = evaluationInfoArray[4];
//        this.timeSpent = (int) evaluationInfoArray[5];
//        this.timeLeft = (int) evaluationInfoArray[6];
//        this.totalTimeGiven = (int) evaluationInfoArray[7];
//        this.numberOfCoinsGained = (int) evaluationInfoArray[8];
//        this.numberOfHiddenItemsGained = (int)evaluationInfoArray[9];
//        this.marioMode = (int) evaluationInfoArray[9];
//        this.killsTotal = (int) evaluationInfoArray[10];
//        this.killsByFire = (int) evaluationInfoArray[11];
//        this.killsByShell = (int) evaluationInfoArray[12];
//        this.killsByStomp = (int) evaluationInfoArray[13];
//
//    }

//    public String toString()
//    {
//        String ret = "\nStatistics. Score:";
//        ret += "\n                  Player/Agent type : " + agentType;
//        ret += "\n                  Player/Agent name : " + agentName;
//        ret += "\n                       Mario Status : " + ((marioStatus == Mario.STATUS_WIN) ? "Win!" : "Loss...");
////        ret += "\n                         Level Type : " + levelType;
////        ret += "\n                   Level Difficulty : " + levelDifficulty;
////        ret += "\n                    Level Rand Seed : " + levelRandSeed;
//        ret += "\nTotal Length of Level (Phys, Cells) : " + "(" + totalLengthOfLevelPhys + "," + totalLengthOfLevelCells + ")";
//        ret += "\n               Passed (Phys, Cells) : " +
//               df.format(distancePassedPhys / totalLengthOfLevelPhys *100) +
//               "% ( " + df.format(distancePassedPhys) + " of " + totalLengthOfLevelPhys + "), " +
//               df.format((double)distancePassedCells / totalLengthOfLevelCells *100) + "% ( " + distancePassedCells + " of " + totalLengthOfLevelCells + ")";
//        ret += "\n           Time Spent(marioseconds) : " + timeSpent + " ( " + df.format((double)timeSpent/totalTimeGiven*100) + "% )";
//        ret += "\n            Time Left(marioseconds) : " + timeLeft + " ( " + df.format((double)timeLeft/totalTimeGiven*100) + "% )";
//        ret += "\n                   Total time given : " + totalTimeGiven;
////        ret += "\nCoins Gained: " + numberOfCoinsGained/totalNumberOfCoins*100 + "%. (" + numberOfCoinsGained + " of " + totalNumberOfCoins + ")";
//        ret += "\n                       Coins Gained : " + numberOfCoinsGained;
//        ret += "\n               multiObjectiveFitness : " + df.format(computeMultiObjectiveFitness());
//        ret += ((Memo.equals("")) ? "" : "\nMemo: " + Memo);
//        return ret;
//    }
}
