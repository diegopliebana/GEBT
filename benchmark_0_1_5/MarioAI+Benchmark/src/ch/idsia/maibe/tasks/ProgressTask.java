package ch.idsia.maibe.tasks;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.tools.CmdLineOptions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:26:43 AM
 * Package: ch.idsia.maibe.tasks
 */

public final class ProgressTask extends BasicTask implements Task
{
    private int uniqueSeed;
    private int fitnessEvaluations = 0;
    public int uid;
    private String fileTimeStamp = "-uid-" + uid + "-" + GlobalOptions.getTimeStamp();

//    private int startingSeed;
    public ProgressTask(CmdLineOptions evaluationOptions)
    {
        super(evaluationOptions);
        setOptions(evaluationOptions);
    }

    public int totalEpisodes = 0;

    private float evaluateSingleLevel(int ld, int tl, int ls, boolean vis, Agent controller)
    {
        this.totalEpisodes++;

        float distanceTravelled = 0;
        options.setAgent(controller);
//        options.setLevelDifficulty(ld);
//        options.setTimeLimit(tl);
//        options.setLevelRandSeed(ls);
//        options.setVisualization(vis);
//        options.setFPS(vis ? 42 : 100);
        this.setAgent(controller);
        this.reset(options);
        this.runOneEpisode();
        distanceTravelled += this.getEnvironment().getEvaluationInfo().computeDistancePassed();
        return distanceTravelled;
    }

    public float[] evaluate(Agent controller)
    {
//        controller.reset();
//        options.setLevelRandSeed(startingSeed++);
//        System.out.println("controller = " + controller);
        float fitn = this.evaluateSingleLevel(0, 40, this.uniqueSeed, false, controller);
//        System.out.println("fitn = " + fitn);
//        if (fitn > 1000)
//            fitn = this.evaluateSingleLevel(0, 150, this.uniqueSeed, false, controller);
////        System.out.println("fitn2 = " + fitn);
//        if (fitn > 4000)
//            fitn = 10000 + this.evaluateSingleLevel(1, 150, this.uniqueSeed, false, controller);
////        System.out.println("fitn3 = " + fitn);
//        if (fitn > 14000)
//            fitn = 20000 + this.evaluateSingleLevel(3, 150, this.uniqueSeed, false, controller);
//        if (fitn > 24000)
//        {
////            this.evaluateSingleLevel(3, 150, this.uniqueSeed, true, controller);
//            fitn = 40000 + this.evaluateSingleLevel(5, 160, this.uniqueSeed, false, controller);
//        }
////        if (fitn > 34000)
////            fitn = 40000 + this.evaluateSingleLevel(5, 160, this.uniqueSeed, false, controller);
//        if (fitn > 44000)
//            fitn = 50000 + this.evaluateSingleLevel(7, 160, this.uniqueSeed, false, controller);

        this.uniqueSeed += 1;
        this.fitnessEvaluations++;
        this.dumpFitnessEvaluation(fitn, "fitnesses-");
        return new float[]{fitn};
    }

    public void dumpFitnessEvaluation(float fitness, String fileName)
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName + fileTimeStamp + ".txt", true));
            out.write(this.fitnessEvaluations + " " + fitness + "\n");
            out.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void doEpisodes(int amount, boolean verbose)
    {
        System.out.println("amount = " + amount);
    }

    public boolean isFinished()
    {
        System.out.println("options = " + options);
        return false;
    }

    public void reset()
    {
        System.out.println("options = " + options);
    }

}
