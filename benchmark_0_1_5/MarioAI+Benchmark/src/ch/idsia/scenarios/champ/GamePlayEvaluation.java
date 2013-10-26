package ch.idsia.scenarios.champ;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.controllers.ForwardJumpingAgent;
import ch.idsia.ai.agents.controllers.TimingAgent;
import ch.idsia.maibe.tasks.BasicTask;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.utils.StatisticalSummary;


/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey at idsia dot ch
 * Date: Mar 17, 2010 Time: 8:33:43 AM
 * Package: ch.idsia.scenarios
 */
public final class GamePlayEvaluation
{
    final static int numberOfTrials = 10;
    final static boolean scoring = false;
    private static int killsSum = 0;
    private static float marioStatusSum = 0;
    private static int timeLeftSum = 0;
    private static int marioModeSum = 0;
    private static boolean detailedStats = false;

    public static void main(String[] args)
    {
        final CmdLineOptions cmdLineOptions = new CmdLineOptions(args);

        int levelLength = cmdLineOptions.getLevelLength();

        final int[] timeLimits = new int[]{/*levelLength / 10,
                                           levelLength*2 / 10,*/
                                           levelLength*4 / 10};

        final int[] levelDifficulties = new int[]{0, 1, 2, 3, 4, 5, 6, 12, 16, 20};
        final int[] levelTypes = new int[]{0, /*1, 2*/};
        final int[] levelLengths = new int[]{320/*, 320, 320, 320, 320, 320*/};
        final boolean[] creaturesEnables = new boolean[]{true};
        int levelSeed = cmdLineOptions.getLevelRandSeed();
//        cmdLineOptions.setVisualization(false);
        cmdLineOptions.setFPS(80);  //100
        //cmdLineOptions.setLevelRandSeed(6189642);
        cmdLineOptions.setLevelRandSeed(0);

//        final Environment environment = new MarioEnvironment();
        //final Agent agent = new ForwardJumpingAgent();
        final Agent agent = cmdLineOptions.getAgent();
//        final Agent agent = (SimpleCNAgent) Easy.load("sergeypolikarpov.xml");
//        System.out.println("agent = " + agent);

        //((grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent)agent).loadBehaviorTree("pathFollower.xml");

        if(args[1].equalsIgnoreCase("grammaticalbehaviorsNoAstar.GEBT_Mario.GEBT_MarioAgent"))
        {
            //((grammaticalbehaviorsNoAstar.GEBT_Mario.GEBT_MarioAgent)agent).loadBehaviorTree("bestIndividual_GEBT_MarioAgent_NoAstar.xml");
            ((grammaticalbehaviorsNoAstar.GEBT_Mario.GEBT_MarioAgent)agent).loadBehaviorTree("RunRightSafe.xml");

        }else if(args[1].equalsIgnoreCase("grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent")){
            ((grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent)agent).loadBehaviorTree("bestIndividual_GEBT_MarioAgent.xml");
            //((grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent)agent).loadBehaviorTree("pathFollower.xml");
        }

        cmdLineOptions.setAgent(agent);
        final BasicTask basicTask = new BasicTask(cmdLineOptions);
        float fitness = 0;
        boolean verbose = false;
        int trials = 0;
        int disqualifications = 0;


        // todo: include level lengths.

        for (int ll : levelLengths)

        for (int levelDifficulty : levelDifficulties)
        {
            for (int levelType : levelTypes)
            {
                for (boolean creaturesEnable : creaturesEnables)
                {
                    for (int timeLimit : timeLimits)
                    {
                        trials ++;
                        cmdLineOptions.setLevelLength(ll);
                        cmdLineOptions.setLevelDifficulty(levelDifficulty);
                        cmdLineOptions.setLevelType(levelType);
                        cmdLineOptions.setPauseWorld(!creaturesEnable);
                        cmdLineOptions.setTimeLimit(timeLimit);
                        basicTask.reset(cmdLineOptions);
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
                            System.out.println("LEVEL OPTIONS: -ld " + levelDifficulty + " -lt " + levelType + " -pw " + !creaturesEnable +
                                " -tl " + timeLimit);
                            System.out.println("Intermediate SCORE = " + f + "; Details: " + evaluationInfo.toStringSingleLine());
                        }
                        fitness += f;
                    }
                }
            }
        }

        System.out.println("trials = " + trials);
        System.out.println("disqualifications = " + disqualifications);
        System.out.println("GamePlayEvaluation final score = " + fitness);

//        EvaluationInfo evaluationInfo = new EvaluationInfo(environment.getEvaluationInfoAsFloats());
//        System.out.println("evaluationInfo = " + evaluationInfo);
        System.exit(0);
    }

    public static void scoreAllAgents(CmdLineOptions cmdLineOptions)
    {
        int startingSeed = cmdLineOptions.getLevelRandSeed();
        for (Agent agent : AgentsPool.getAgentsCollection())
            score(agent, startingSeed, cmdLineOptions);

//        startingSeed = 0;
//        for (Agent agent : AgentsPool.getAgentsCollection())
//            score(agent, startingSeed, cmdLineOptions);

    }

    public static void score(Agent agent, int startingSeed, CmdLineOptions cmdLineOptions)
    {
        TimingAgent controller = new TimingAgent (agent);
        //        options.setNumberOfTrials(1);
//        options.setVisualization(false);
//        options.setMaxFPS(true);
        System.out.println("\nScoring controller " + agent.getName() + " with starting seed " + startingSeed);

        double competitionScore = 0;
        killsSum = 0;
        marioStatusSum = 0;
        timeLeftSum = 0;
        marioModeSum = 0;

        competitionScore += testConfig (controller, cmdLineOptions, startingSeed, 0, false);
        competitionScore += testConfig (controller, cmdLineOptions, startingSeed, 3, false);
        competitionScore += testConfig (controller, cmdLineOptions, startingSeed, 5, false);
        competitionScore += testConfig (controller, cmdLineOptions, startingSeed, 10, false);

        System.out.println("\nCompetition score: " + competitionScore + "\n");
        System.out.println("Number of levels cleared = " + marioStatusSum);
        System.out.println("Additional (tie-breaker) info: ");
        System.out.println("Total time left = " + timeLeftSum);
        System.out.println("Total kills = " + killsSum);
        System.out.println("Mario mode (small, large, fire) sum = " + marioModeSum);
        System.out.println("TOTAL SUM for " + agent.getName() + " = " + (competitionScore + killsSum + marioStatusSum + marioModeSum + timeLeftSum));
    }

    public static double testConfig (TimingAgent controller, EvaluationOptions options, int seed, int levelDifficulty, boolean paused)
    {
        options.setLevelDifficulty(levelDifficulty);
        options.setPauseWorld(paused);
        StatisticalSummary ss = test (controller, options, seed);
        double averageTimeTaken = controller.averageTimeTaken();
        System.out.printf("Difficulty %d score %.4f (avg time %.4f)\n",
                levelDifficulty, ss.mean(), averageTimeTaken);
        return ss.mean();
    }

    public static StatisticalSummary test (Agent controller, EvaluationOptions options, int seed)
    {
        StatisticalSummary ss = new StatisticalSummary ();
        int kills = 0;
        int timeLeft = 0;
        int marioMode = 0;
        float marioStatus = 0;

//        options.setNumberOfTrials(numberOfTrials);
        options.resetCurrentTrial();
        for (int i = 0; i < numberOfTrials; i++) {
            options.setLevelRandSeed(seed + i);
            options.setLevelLength (200 + (i * 128) + (seed % (i + 1)));
            options.setLevelType(i % 3);
            controller.reset();
            options.setAgent(controller);
//            Evaluator evaluator = new Evaluator (options);
//            EvaluationInfo result = evaluator.evaluate().get(0);
//            kills += result.computeKillsTotal();
//            timeLeft += result.timeLeft;
//            marioMode += result.marioMode;
//            marioStatus += result.marioStatus;
//            System.out.println("\ntrial # " + i);
//            System.out.println("result.timeLeft = " + result.timeLeft);
//            System.out.println("result.marioMode = " + result.marioMode);
//            System.out.println("result.marioStatus = " + result.marioStatus);
//            System.out.println("result.computeKillsTotal() = " + result.computeKillsTotal());
//            ss.add (result.computeDistancePassed());
        }

        if (detailedStats)
        {
            System.out.println("\n===================\nStatistics over " + numberOfTrials + " trials for " + controller.getName());
            System.out.println("Total kills = " + kills);
            System.out.println("marioStatus = " + marioStatus);
            System.out.println("timeLeft = " + timeLeft);
            System.out.println("marioMode = " + marioMode);
            System.out.println("===================\n");
        }

        killsSum += kills;
        marioStatusSum += marioStatus;
        timeLeftSum += timeLeft;
        marioModeSum += marioMode;

        return ss;
    }
    
}
