package ch.idsia.mario.environments;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.MarioVisualComponent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 3, 2010 Time: 10:08:13 PM
 * Package: ch.idsia.mario.environments
 */

public final class MarioEnvironment implements Environment
{
    final LevelScene levelScene;
//    private int frame = 0;
    private MarioVisualComponent marioVisualComponent;
    private Agent agent;

    // TODO : Singleton
    public MarioEnvironment()
    {
//        System.out.println("System.getProperty(\"java.awt.headless\") = " + System.getProperty("java.awt.headless"));
//        System.out.println("System.getProperty(\"verbose\") = " + System.getProperty("-verbose"));
//        System.out.println("Java: JA ZDES'!!");
//        System.out.flush();
        System.out.println("MarioAI Benchmark" + GlobalOptions.getVersionUID());
        levelScene = new LevelScene(0, 0, 0, 0, 0, 0, 0);
    }

    public void resetDefault()
    {
        levelScene.resetDefault();
    }

    public void reset(int[] setUpOptions)
    {
//        System.out.println("\nsetUpOptions = " + setUpOptions);
//        for (int i = 0; i < setUpOptions.length; ++i)
//        {
//            System.out.print(" op[" + i +"] = " + setUpOptions[i]);
//        }
//        System.out.println("");
//        System.out.flush();
        if (/*levelScene.visualization*/ setUpOptions[14] == 1)
        {
            if (marioVisualComponent == null)
                marioVisualComponent = MarioVisualComponent.getInstance(GlobalOptions.VISUAL_COMPONENT_WIDTH,
                                                                   GlobalOptions.VISUAL_COMPONENT_HEIGHT,
                                                                   levelScene);
            levelScene.reset(setUpOptions);
            marioVisualComponent.reset();
            marioVisualComponent.postInitGraphicsAndLevel();
            marioVisualComponent.setAgent(agent);
            marioVisualComponent.setLocation(setUpOptions[15], setUpOptions[16]);
        }
        else
            levelScene.reset(setUpOptions);
        
    }

    public void tick()
    {
//        System.out.println("MarioEnvironment: tick()");
        levelScene.tick();
        if (levelScene.visualization)
            marioVisualComponent.tick();
        // Advance the frame
//        ++frame;
    }

//    public byte[][] getCompleteObservation()
//    {
//        return levelScene.getCompleteObservation();
//    }

//    public byte[][] getEnemiesObservation()
//    {
//        return levelScene.getEnemiesObservation();
//    }

//    public byte[][] getLevelSceneObservation()
//    {
//        return levelScene.getLevelSceneObservation();
//    }

    public float[] getMarioFloatPos()
    {
        return levelScene.getMarioFloatPos();
    }

    public int getMarioMode()
    {
        return levelScene.getMarioMode();
    }

    public float[] getEnemiesFloatPos()
    {
        return levelScene.getEnemiesFloatPos();
    }

    public boolean isMarioOnGround()
    {
        return levelScene.isMarioOnGround();
    }

    public boolean isMarioAbleToJump()
    {
        return levelScene.isMarioAbleToJump();
    }

    public boolean isMarioCarrying()
    {
        return levelScene.isMarioCarrying();
    }

    public boolean isMarioAbleToShoot()
    {
        return levelScene.isMarioAbleToShoot();
    }

    public byte[][] getMergedObservationZZ(int ZLevelScene, int ZLevelEnemies)
    {
        return levelScene.getMergedObservationZZ(ZLevelScene, ZLevelEnemies);
    }

    public byte[][] getLevelSceneObservationZ(int ZLevelScene)
    {
        return levelScene.getLevelSceneObservationZ(ZLevelScene);
    }

    public byte[][] getEnemiesObservationZ(int ZLevelEnemies)
    {
        return levelScene.getEnemiesObservationZ(ZLevelEnemies);
    }

    public int getKillsTotal()
    {
        return levelScene.getKillsTotal();
    }

    public int getKillsByFire()
    {
        return levelScene.getKillsByFire();
    }

    public int getKillsByStomp()
    {
        return levelScene.getKillsByStomp();
    }

    public int getKillsByShell()
    {
        return levelScene.getKillsByShell();
    }

    public int getMarioStatus()
    {
        return levelScene.getMarioStatus();
    }

    public float[] getSerializedFullObservationZZ(int ZLevelScene, int ZLevelEnemies)
    {
        return levelScene.getSerializedFullObservationZZ(ZLevelScene, ZLevelEnemies);
    }

    public int[] getSerializedLevelSceneObservationZ(int ZLevelScene)
    {
        return levelScene.getSerializedLevelSceneObservationZ(ZLevelScene);
    }

    public int[] getSerializedEnemiesObservationZ(int ZLevelEnemies)
    {
        return levelScene.getSerializedEnemiesObservationZ(ZLevelEnemies);
    }

    public int[] getSerializedMergedObservationZZ(int ZLevelScene, int ZLevelEnemies)
    {
        return levelScene.getSerializedMergedObservationZZ(ZLevelScene, ZLevelEnemies);
    }

    public float[] getCreaturesFloatPos()
    {
        return levelScene.getCreaturesFloatPos();
    }

    public int[] getMarioState()
    {
        return levelScene.getMarioState();
    }

    public void performAction(boolean[] action)
    {
        levelScene.performAction(action);
    }

    public boolean isLevelFinished()
    {
        return levelScene.isLevelFinished();
    }

    public float[] getEvaluationInfoAsFloats()
    {
        return this.getEvaluationInfo().toFloatArray();
    }

    public String getEvaluationInfoAsString()
    {
        return this.getEvaluationInfo().toString();
    }

    public EvaluationInfo getEvaluationInfo()
    {
        // TODO: make static field
        EvaluationInfo evaluationInfo = new EvaluationInfo();

//        evaluationInfo.agentType = agent.getClass().getSimpleName();
//        evaluationInfo.agentName = agent.getName();
        evaluationInfo.marioStatus =         levelScene.getMarioStatus();
        evaluationInfo.flowersDevoured = Mario.flowersDevoured;
        evaluationInfo.distancePassedPhys = levelScene.getMarioFloatPos()[0];
        evaluationInfo.distancePassedCells = levelScene.mario.mapX;
//     evaluationInfo.totalLengthOfLevelCells = levelScene.level.getWidthCells();
//     evaluationInfo.totalLengthOfLevelPhys = levelScene.level.getWidthPhys();
        evaluationInfo.timeSpent = levelScene.getTimeSpent();
        evaluationInfo.timeLeft = levelScene.getTimeLeft();
//     evaluationInfo.totalTimeGiven = levelScene.getTimeLimit();
        evaluationInfo.numberOfCoinsGained = Mario.coins;
//        evaluationInfo.totalNumberOfCoins   = -1 ; // TODO: total Number of coins.
        evaluationInfo.marioMode = levelScene.getMarioMode();
        evaluationInfo.mushroomsDevoured = Mario.mushroomsDevoured;
        evaluationInfo.killsTotal = levelScene.getKillsTotal();
        evaluationInfo.killsByStomp = levelScene.getKillsByStomp();
        evaluationInfo.killsByFire = levelScene.getKillsByFire();
        evaluationInfo.killsByShell = levelScene.getKillsByShell();
        evaluationInfo.numberOfHiddenItemsGained = levelScene.getNumberOfHiddenCoinsGained();

//        evaluationInfo.Memo = "Number of attempt: " + Mario.numberOfAttempts;
        return evaluationInfo;
    }

    public void reset(CmdLineOptions cmdLineOptions)
    {
        this.reset(cmdLineOptions.toIntArray());
    }

    public void setAgent(Agent agent)
    {
        this.agent = agent;
    }
}
