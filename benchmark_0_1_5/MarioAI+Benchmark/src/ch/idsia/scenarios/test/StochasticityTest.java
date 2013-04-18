package ch.idsia.scenarios.test;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.maibe.tasks.ProgressTask;
import ch.idsia.maibe.tasks.Task;
import ch.idsia.tools.CmdLineOptions;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 9, 2009
 * Time: 4:23:04 PM
 */
public class StochasticityTest
{
    final static int repetitions = 10;
    public static void main(String[] args)
    {
        Agent controller = AgentsPool.load (args[0]);
        CmdLineOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        options.setPauseWorld (false);
        Task task = new ProgressTask(options);
        options.setVisualization(false);
        task.setOptions(options);
        for (int i = 0; i < repetitions; i++) {
            System.out.println ("Score: " + task.evaluate (controller)[0]);
        }
    }

}
