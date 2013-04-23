package ch.idsia.scenarios.oldscenarios;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.learning.SimpleMLPAgent;
import ch.idsia.ai.ea.ES;
import ch.idsia.maibe.tasks.MultiSeedProgressTask;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.tools.CmdLineOptions;
import wox.serial.Easy;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 24, 2009
 * Time: 1:18:44 AM
 */

public class EvolveMultiSeed
{

    final static int generations = 100;
    final static int populationSize = 100;

    public static void main(String[] args) {
        CmdLineOptions options = new CmdLineOptions(new String[0]);
//        options.setNumberOfTrials(1);
        options.setPauseWorld(true);
        Evolvable initial = new SimpleMLPAgent();

        if (args.length > 0) {
            initial = (Evolvable) AgentsPool.load (args[0]);
        }
        options.setFPS(GlobalOptions.MaxFPS);
        options.setVisualization(false);
        MultiSeedProgressTask task = new MultiSeedProgressTask(options);
        task.setNumberOfSeeds(3);
        task.setStartingSeed(0);
        ES es = new ES (task, initial, populationSize);
        System.out.println("Evolving " + initial + " with task " + task);
        for (int gen = 0; gen < generations; gen++) {
            //task.setStartingSeed((int)(Math.random () * Integer.MAX_VALUE));
            es.nextGeneration();
            double bestResult = es.getBestFitnesses()[0];
            System.out.println("Generation " + gen + " best " + bestResult);
            options.setVisualization(gen % 5 == 0 || bestResult > 4000);
            Agent a = (Agent) es.getBests()[0];
            a.setName(((Agent)initial).getName() + gen);
//                RegisterableAgent.registerAgent(a);
//                AgentsPool.setCurrentAgent(a);
            double result = task.evaluate(a)[0];
            options.setVisualization(false);
            Easy.save (es.getBests()[0], "evolved-" + gen + ".xml");
            if (result > 4000) {
                break; //finished
            }
        }
    }
}
