package ch.idsia.scenarios.test;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.learning.MediumSRNAgent;
import ch.idsia.ai.ea.ES;
import ch.idsia.maibe.tasks.ProgressTask;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.utils.Stats;
import wox.serial.Easy;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Jun 13, 2009
 * Time: 2:16:18 PM
 */
public class PaperEvolve
{
    final static int generations = 5000;
    final static int populationSize = 100;

    public static void main(String[] args)
    {
        CmdLineOptions options = new CmdLineOptions(args);
//        Evolvable initial = new LargeSRNAgent();
//        Evolvable initial = new SmallSRNAgent();
//        Evolvable initial = new MediumSRNAgent();
//        Evolvable initial = new SmallMLPAgent();
        Evolvable initial = new MediumSRNAgent();
//        Evolvable initial = new MediumMLPAgent();
//        if (args.length > 0)
//        {
//            initial = (Evolvable) AgentsPool.load (args[0]);
//        }
        options.setTimeLimit(100);
        options.setAgent((Agent) initial);
        options.setFPS(GlobalOptions.MaxFPS);
        options.setPauseWorld(false);
        options.setVisualization(false);
        ProgressTask task = new ProgressTask(options);
//        MultiSeedProgressTask task = new MultiSeedProgressTask(options);
//        MushroomTask task = new MushroomTask(options);
                options.setLevelRandSeed(6189642);
//        int seed = (int) (Math.random () * Integer.MAX_VALUE / 100000);
        int seed = options.getLevelRandSeed();
        ES es = new ES (task, initial, populationSize);
        System.out.println("Evolving " + initial + " with task " + task);
//        int difficulty = 0;
//        System.out.println("seed = " + seed);
//        task.uid = seed;

//        options.setLevelRandSeed(seed);
//        BasicTask bt = new MultiSeedProgressTask(new CmdLineOptions(args));
//        CmdLineOptions c = new CmdLineOptions(new String[]{"-vis", "on", "-fps", "24"});

        // start learning in mode 0
        System.out.println("options.getTimeLimit() = " + options.getTimeLimit());
//        options.setMarioMode(0);
        String fileName = "evolved-" + "-uid-" + seed + ".xml";
        DecimalFormat df = new DecimalFormat();
        float bestScore = 250;

        options.setLevelDifficulty(16);
        
        for (int gen = 0; gen < generations; gen++)
        {
//            System.out.print("<a = " + options.getMarioMode() + "> ");
//            task.setStartingSeed(gen);
            es.nextGeneration();

            float fitn = es.getBestFitnesses()[0];
            System.out.print("Generation: " + gen + " current best: " + df.format(fitn) + ";  ");
//            int marioStatus = task.getEnvironment().getEvaluationInfo().marioStatus;

            if (fitn > bestScore /*&& marioStatus == Environment.MARIO_STATUS_WIN*/)
            {
                bestScore = fitn;
                fileName = "evolved-progress-" + options.getAgentName() + gen + "-uid-" + seed + ".xml";
                final Agent a = (Agent) es.getBests()[0];
                Easy.save (a, fileName);
                task.dumpFitnessEvaluation(bestScore, "fitnessImprovements-" + options.getAgentName() + ".txt");
//                c.setLevelRandSeed(options.getLevelRandSeed());
//                c.setLevelDifficulty(options.getLevelDifficulty());
//                c.setTimeLimit(options.getTimeLimit());
                options.setAgent(a);
                System.out.println("a = " + options.getMarioMode());
                task.setAgent(a);
                options.setVisualization(true);
                options.setFPS(42);
                task.getEnvironment().reset(options);
                task.evaluate(a);
                options.setVisualization(false);
                options.setFPS(100);

                System.out.print("MODE: = " + task.getEnvironment().getEvaluationInfo().marioMode);
                System.out.print("TIME LEFT: " + task.getEnvironment().getEvaluationInfo().timeLeft);
                System.out.println(", STATUS = " + task.getEnvironment().getEvaluationInfo().marioStatus);
                
//                difficulty++;
//                options.setLevelDifficulty(difficulty);
            }
        }
        System.out.println("\n\n\n\n\n\n\n\n\n");
        try
        {
            Stats.main(new String[]{fileName, "0"});
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
