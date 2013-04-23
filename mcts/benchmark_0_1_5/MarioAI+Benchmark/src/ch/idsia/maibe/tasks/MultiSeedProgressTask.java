package ch.idsia.maibe.tasks;

import ch.idsia.ai.agents.Agent;
import ch.idsia.tools.CmdLineOptions;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 23, 2009
 * Time: 11:37:47 PM
 */

public class MultiSeedProgressTask extends BasicTask implements Task
{
    private CmdLineOptions options;
    private int startingSeed = 0;
    private int numberOfSeeds = 3;

    public MultiSeedProgressTask(CmdLineOptions evaluationOptions)
    {
        super(evaluationOptions);
        setOptions(evaluationOptions);
    }

    public float[] evaluate(Agent controller)
    {
        float distanceTravelled = 0;

        options.setAgent(controller);
        this.setAgent(controller);

        for (int i = 0; i < numberOfSeeds; i++)
        {
            controller.reset();
            options.setLevelRandSeed(startingSeed + i);
            this.reset(options);
            this.runOneEpisode();
            distanceTravelled += this.getEnvironment().getEvaluationInfo().computeDistancePassed();
        }
        distanceTravelled = distanceTravelled / numberOfSeeds;
        return new float[]{distanceTravelled};
    }

    public void setStartingSeed (int seed)
    {
        startingSeed = seed;
    }

    public void setNumberOfSeeds (int number)
    {
        numberOfSeeds = number;
    }

    public void setOptions(CmdLineOptions options)
    {
        this.options = options;
    }

    public CmdLineOptions getOptions()
    {
        return options;
    }

    public void doEpisodes(int amount, boolean verbose)
    {

    }

    public boolean isFinished()
    {
        return true;  
    }

    public void reset()
    {

    }
}
