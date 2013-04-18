package ch.idsia.scenarios.test;

import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.scenarios.oldscenarios.Stats;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.ai.Evolvable;
import ch.idsia.ai.ea.ES;
import ch.idsia.maibe.tasks.MultiDifficultyProgressTask;
import ch.idsia.ai.agents.learning.SmallMLPAgent;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import wox.serial.Easy;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Jun 14, 2009
 * Time: 1:12:02 PM
 */
public class EvolveWithChangingSeeds {

    final static int generations = 100;
    final static int populationSize = 100;

    public static void main(String[] args) {
        CmdLineOptions options = new CmdLineOptions(new String[0]);
//        options.setNumberOfTrials(1);
        options.setPauseWorld(false);
        Evolvable initial = new SmallMLPAgent();
        AgentsPool.addAgent((Agent) initial);
        options.setFPS(GlobalOptions.MaxFPS);
        options.setVisualization(false);
        MultiDifficultyProgressTask task = new MultiDifficultyProgressTask(options);

        ES es = new ES (task, initial, populationSize);
        System.out.println("Evolving " + initial + " with task " + task);
        final String fileName = "evolved" + (int) (Math.random () * Integer.MAX_VALUE) + ".xml";
        for (int gen = 0; gen < generations; gen++) {
            task.setStartingSeed((int) (Math.random () * Integer.MAX_VALUE));
            es.nextGeneration();
            float bestResult = es.getBestFitnesses()[0];
            System.out.println("Generation " + gen + " best " + bestResult);
            Evolvable bestEvolvable = es.getBests()[0];
            float[] fitnesses = task.evaluate((Agent) bestEvolvable);
            System.out.printf("%.4f  %.4f  %.4f  %.4f  %.4f\n",
                    fitnesses[0], fitnesses[1], fitnesses[2], fitnesses[3], fitnesses[4]);
            Easy.save (es.getBests()[0], fileName);
        }
        Stats.main(new String[]{fileName, "0"});
    }

}