package ch.idsia.scenarios.champ;

import ch.idsia.ai.agents.BasicLearningAgent;
import ch.idsia.ai.agents.LearningAgent;
import ch.idsia.maibe.tasks.BasicTask;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey at idsia dot ch
 * Date: Mar 17, 2010 Time: 8:34:17 AM
 * Package: ch.idsia.scenarios
 */

public final class LearningEvaluation
{
    final static int numberOfTrials = 10000;
    final static boolean scoring = false;
    private static int killsSum = 0;
    private static float marioStatusSum = 0;
    private static int timeLeftSum = 0;
    private static int marioModeSum = 0;
    private static boolean detailedStats = false;

    public static void main(String[] args)
    {
        final CmdLineOptions cmdLineOptions = new CmdLineOptions(args);

        // learn on a particular level
        // create particular level
        // several
        float fitness = 0;
        int disqualifications = 0;
        boolean verbose = false;

        LearningAgent lagent = new BasicLearningAgent();
        final BasicTask basicTask = new BasicTask(cmdLineOptions);

        for (int i = 0; i < numberOfTrials; ++i)
        {
            basicTask.reset(cmdLineOptions);
            lagent.newEpisode();
            if (!basicTask.runOneEpisode())
            {
                System.out.println("MarioAI: out of computational time per action!");
                disqualifications++;
                continue;
            }
            EvaluationInfo evaluationInfo = basicTask.getEnvironment().getEvaluationInfo();
            float f = evaluationInfo.computeMultiObjectiveFitness();
            if (verbose)
            {
                System.out.println("Intermediate SCORE = " + f + "; Details: " + evaluationInfo.toStringSingleLine());
            }
            lagent.giveReward(f);

        }
        // perform on the same level
        lagent.learn();
        basicTask.runOneEpisode();
        EvaluationInfo evaluationInfo = basicTask.getEnvironment().getEvaluationInfo();

        System.out.println("LearningEvaluation final score = " + evaluationInfo.computeMultiObjectiveFitness());

//        EvaluationInfo evaluationInfo = new EvaluationInfo(environment.getEvaluationInfoAsFloats());
//        System.out.println("evaluationInfo = " + evaluationInfo);
        System.exit(0);
    }
}
