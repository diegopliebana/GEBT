package ch.idsia.maibe.tasks;

import ch.idsia.ai.agents.Agent;
import ch.idsia.tools.CmdLineOptions;
//import ch.idsia.tools.EvaluationInfo;
//import ch.idsia.tools.Evaluator;

//import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Jun 13, 2009
 * Time: 2:44:59 PM
 */
public class MultiDifficultyProgressTask implements Task
{
    private CmdLineOptions options;
    private int startingSeed = 0;
    private int[] difficulties = {0, 3, 5, 10};

    public MultiDifficultyProgressTask(CmdLineOptions evaluationOptions) {
        setOptions(evaluationOptions);
    }

    public float[] evaluate(final Agent controller) {
        float distanceTravelled = 0;
        float[] fitnesses = new float[difficulties.length + 1];
        for (int difficulty : difficulties) {
            controller.reset();
            options.setLevelRandSeed(startingSeed);
            options.setLevelDifficulty(difficulty);
            options.setAgent(controller);
//            Evaluator evaluator = new Evaluator(options);
//            List<EvaluationInfo> results = evaluator.evaluate();
//            EvaluationInfo result = results.get(0);
//            float thisDistance = result.computeDistancePassed();
//            fitnesses[i + 1] = thisDistance;
//            distanceTravelled += thisDistance;
        }
        distanceTravelled = distanceTravelled / difficulties.length;
        fitnesses[0] = distanceTravelled;
        return fitnesses;
        //return new double[]{distanceTravelled};
    }

    public void setStartingSeed (int seed) {
        startingSeed = seed;
    }

    public void setOptions(CmdLineOptions options) {
        this.options = options;
    }

    public CmdLineOptions getOptions() {
        return options;
    }

    public void doEpisodes(int amount, boolean verbose)
    {

    }

    public boolean isFinished()
    {
        return false;
    }

    public void reset()
    {
        
    }
}
