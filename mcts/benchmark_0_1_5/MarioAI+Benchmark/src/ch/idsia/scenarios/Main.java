package ch.idsia.scenarios;

//import ch.idsia.ai.agents.Agent;
import ch.idsia.maibe.tasks.BasicTask;
import ch.idsia.tools.CmdLineOptions;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class Main
{
    public static void main(String[] args)
    {
//        final String argsString = "-vis on";
//        args = argsString.split("\\s");
        final CmdLineOptions cmdLineOptions = new CmdLineOptions(args);
//        final Environment environment = new MarioEnvironment();
//        final Agent agent = new ForwardAgent();
//        final Agent agent = cmdLineOptions.getAgent();
//        final Agent agent = new RobinBaumgarten_AStarAgent();
//        final Agent a = AgentsPool.load("ch.idsia.controllers.agents.controllers.ForwardJumpingAgent");
        final BasicTask basicTask = new BasicTask(cmdLineOptions);
//        for (int i = 0; i < 10; ++i)
//        {
//            int seed = 0;
//            do
//            {
//                cmdLineOptions.setLevelDifficulty(i);
//                cmdLineOptions.setLevelRandSeed(seed++);
                basicTask.reset(cmdLineOptions);
                basicTask.runOneEpisode();
                System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
//            } while (basicTask.getEnvironment().getEvaluationInfo().marioStatus != Environment.MARIO_STATUS_WIN);
//        }
//
        System.out.println("cmdLineOptions.getLevelLength() = " + cmdLineOptions.getLevelLength());
        System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
        System.exit(0);
    }
}
