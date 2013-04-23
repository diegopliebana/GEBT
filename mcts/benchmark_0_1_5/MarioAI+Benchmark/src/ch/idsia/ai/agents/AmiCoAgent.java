package ch.idsia.ai.agents;

import ch.idsia.amico.JavaCallsPython;
import ch.idsia.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey at idsia dot ch
 * Date: Dec 11, 2009
 * Time: 8:29:15 PM
 * Package: ch.idsia.controllers.agents
 */
public class AmiCoAgent implements Agent
{
    static JavaCallsPython javaCallsPython = null;
    private final String moduleName;
    private final String agentName;
    private Environment env;

    public AmiCoAgent(String amicoModuleName, String amicoAgentName)
    {
        this.moduleName = amicoModuleName;
        this.agentName = amicoAgentName;
        this.reset();
    }

    public void integrateObservation(int[] serializedLevelSceneObservationZ, int[] serializedEnemiesObservationZ, float[] marioFloatPos, float[] enemiesFloatPos, int[] marioState)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean[] getAction()
    {
        return this.getAction(this.env);
    }

    public void integrateObservation(Environment environment)
    {
        this.env = environment;
    }

    public void reset()
    {
        if (javaCallsPython == null)
        {
            System.out.println("Java: Initialize AmiCo");
            javaCallsPython = new JavaCallsPython(moduleName, agentName);
        }
        else
        {
            System.out.println("Java: AmiCo is already initialized");
        }
    }

    public boolean[] getAction(Environment observation)
    {
        int ZLevelScene = 1;
        int ZLevelEnemies = 0;
        // Default hardcoded values for ZLevels used by now
        // Will use extra values from int[] action in future to tailor the representation of levels
        byte[][] levelScene = observation.getLevelSceneObservationZ(ZLevelScene);
        byte[][] enemies = observation.getEnemiesObservationZ(ZLevelEnemies);
        int rows = Environment.HalfObsHeight*2;
        int cols = Environment.HalfObsWidth*2;
        int[] squashedLevelScene = new int[rows*cols];
        int[] squashedEnemies = new int[enemies.length*enemies[0].length];

        // serialization into arrays of primitive types to speed up the data transfer.
        for (int i = 0; i < squashedLevelScene.length; ++i)
        {
            squashedLevelScene[i] = levelScene[i / cols][i % rows];
            squashedEnemies[i] = enemies[i / cols][i % rows];
        }
        float[] marioPos = observation.getMarioFloatPos();
        float[] enemiesPos = observation.getEnemiesFloatPos();
        int[] marioState = new int[]{
                observation.getMarioStatus(),
                observation.getMarioMode(),
                observation.isMarioOnGround() ? 1 : 0,
                observation.isMarioAbleToJump() ? 1 : 0,
                observation.isMarioAbleToShoot() ? 1 : 0,
                observation.isMarioCarrying() ? 1 : 0,
                observation.getKillsTotal(),
                observation.getKillsByFire(),
                observation.getKillsByStomp(),
                observation.getKillsByStomp(),
                observation.getKillsByShell()
        };

        int[] action = javaCallsPython.getAction(squashedLevelScene, squashedEnemies, marioPos, enemiesPos, marioState);

        boolean[] ret = new boolean[action.length];
        for (int i = 0; i < action.length; ++i)
            ret[i] = (action[i] != 0);
        return ret;
    }

    public AGENT_TYPE getType()
    {
        return AGENT_TYPE.AI;
    }

    public String getName()
    {
        return this.agentName;
    }

    public void setName(String name)
    {
        throw new Error("AmiCo agent name must be set only via constructor");
    }
}
