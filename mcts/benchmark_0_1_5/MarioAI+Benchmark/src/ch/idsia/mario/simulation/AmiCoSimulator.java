package ch.idsia.mario.simulation;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.controllers.ForwardAgent;
import ch.idsia.mario.environments.Environment;
import ch.idsia.mario.environments.MarioEnvironment;
import ch.idsia.tools.CmdLineOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey at idsia dot ch
 * Date: Mar 1, 2010 Time: 1:50:37 PM
 * Package: ch.idsia.scenarios
 */

public class AmiCoSimulator
{
    public static void main(String[] args)
    {
        CmdLineOptions cmdLineOptions = new CmdLineOptions(args);
        cmdLineOptions.setMarioInvulnerable(true);
        int[] options = cmdLineOptions.toIntArray();
        for (int i = 0; i < options.length; ++i)
        {
            System.out.print(options[i] + ",");
        }
        Environment environment = new MarioEnvironment();
        Agent agent = new ForwardAgent();
//        options = new int[]{0, 1, 0, 1500, 0, 2, 1, 100, 1, 0, 1, 0, 11024, 0, 0, 0, 0, 0, 1};
        options[17] = 1; //zLevelScene
//        for (int seed = 16; seed < 20; ++seed)
//        for (int length = 4500; length < 100000; length += 500)
//        {
            options[4] = 16;  // seed
//            options[3] = length;
//            options[14] = seed % 2;    // visualization
            options[14] = 1;    // visualization
//        options[9] = 1;
            environment.reset(options);
            while (!environment.isLevelFinished())
            {
                environment.tick();
//                agent.integrateObservation(environment.getSerializedLevelSceneObservationZ(options[17]),
//                                           environment.getSerializedEnemiesObservationZ(options[18]),
//                                           environment.getMarioFloatPos(),
//                                           environment.getEnemiesFloatPos(),
//                                           environment.getMarioState());
                agent.integrateObservation(environment);
                environment.performAction(agent.getAction());
            }
        System.out.println("Evaluation Info:");
        float[] ev = environment.getEvaluationInfoAsFloats();
        for (float anEv : ev)
        {
            System.out.print(anEv + ", ");
        }
//        }
        System.exit(0);
    }

    public void runEpisodes()
    {
        
    }
}