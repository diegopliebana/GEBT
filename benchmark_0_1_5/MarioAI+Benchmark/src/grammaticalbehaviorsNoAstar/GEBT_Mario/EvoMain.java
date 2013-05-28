/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.GEBT_Mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.maibe.tasks.BasicTask;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author Diego
 */
public class EvoMain {

    private static int[]       timeLimits;
    private static int[]       levelDifficulties;
    private static int[]       levelTypes;
    private static int[]       levelLengths;
    private static boolean[]   creaturesEnabled;
    private static int         levelSeed;
    private static boolean     visualization;
    private static int         fps;
    private static String      inputFile;
    private static String      outputFile;


    private static int[] toIntArray(String []array)
    {
        int values[] = new int[array.length];
        for(int i = 0; i < array.length; ++i)
        {
            values[i] = Integer.parseInt(array[i]);
        }

        return values;
    }

    private static boolean[] toBoolArray(String []array)
    {
        boolean values[] = new boolean[array.length];
        for(int i = 0; i < array.length; ++i)
        {
            int val = Integer.parseInt(array[i]);
            if(val == 0)
                values[i] = false;
            else
                values[i] = true;
        }

        return values;
    }


    private static void parse(String parameter, String[] values)
    {
        if(parameter.compareToIgnoreCase("-tl") == 0)
        {
            timeLimits = toIntArray(values);
        }
        else if(parameter.compareToIgnoreCase("-ld") == 0)
        {
            levelDifficulties = toIntArray(values);
        }
        else if(parameter.compareToIgnoreCase("-lt") == 0)
        {
            levelTypes = toIntArray(values);
        }
        else if(parameter.compareToIgnoreCase("-ll") == 0)
        {
            levelLengths = toIntArray(values);
        }
        else if(parameter.compareToIgnoreCase("-ce") == 0)
        {
            creaturesEnabled = toBoolArray(values);
        }
        else if(parameter.compareToIgnoreCase("-vis") == 0)
        {
            visualization = toBoolArray(values)[0];
        }
        else if(parameter.compareToIgnoreCase("-fps") == 0)
        {
            fps = toIntArray(values)[0];
        }
        else if(parameter.compareToIgnoreCase("-rnd") == 0)
        {
            levelSeed = toIntArray(values)[0];

        }else if(parameter.compareToIgnoreCase("-if") == 0)
        {
            inputFile = values[0];
        }
        else if(parameter.compareToIgnoreCase("-of") == 0)
        {
            outputFile = values[0];
        }

    }

    private static void parseArgs(String[] args)
    {
        int index = 0;
        int numArgs = args.length;

        String thisArg;
        String parameter = new String();
        String values = new String();

        while(index < numArgs)
        {
            thisArg = args[index];
            if(thisArg.length() > 0 && thisArg.charAt(0) == '-')
            {
                //Then, this is a new parameter
                
                //And end of the last parameter
                if(values.length() > 0)
                {
                    //Last parameter parse
                    parse(parameter, values.split(" "));
                }
                
                parameter = thisArg;
                values = new String();
            }
            
            if(thisArg.length() > 0 && thisArg.charAt(0) != '-')
            {
                //This is a value
                values = values.concat(thisArg + " ");
            }

            ++index;
        }

        //Last parameter parse
        parse(parameter, values.split(" "));

    }

    private static void printFitnessToFile(double fitness)
    {
        try
        {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
            out.print(fitness + " ");
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String[] args)
    {
        String[] defaultArgs = new String[]{"-lt", "0 1",                       //Level types
                            "-ld", "0 1 2 3 4 5 6 12 16 20",    //Level difficulties
                            "-ll", "256",                       //Level length
                            "-tl", "102",                       //Time limit (levelLength*4 / 10)
                            "-ce", "0 1",                       //creatures enabled
                            "-vis", "1",                        //Visualization
                            "-fps", "60",                       //FPS
                            "-rnd", "0",                        //Random Seed
                };
        //parse code args (the ones above).
        parseArgs(defaultArgs);

        //parse execution / command line args
        parseArgs(args);

        //Load the agent:
        String[] agentArgs = new String[]{"-ag", "grammaticalbehaviorsNoAstar.GEBT_Mario.GEBT_MarioAgent"};
        final CmdLineOptions cmdLineOptions = new CmdLineOptions(agentArgs);
        Agent agent = cmdLineOptions.getAgent();
        GEBT_MarioAgent myAgent = (GEBT_MarioAgent) agent;


        //Setting some things:
        cmdLineOptions.setVisualization(visualization);     //TO SET GRAPHICS OFF
        cmdLineOptions.setFPS(fps);                 //TO ACTIVATE TIME COMPRESSION
        cmdLineOptions.setLevelRandSeed(levelSeed); //TO SET THE RANDOM SEED (Seed 0 must be submitted. )
        
        //Load the behavior tree to evaluate (either XML or through BTStream)
        myAgent.loadBehaviorTree(inputFile);
        
        //Set the agent (maybe this step is not needed...)
        cmdLineOptions.setAgent(myAgent);

        //Score!
        double fitness = score(cmdLineOptions,timeLimits,levelDifficulties,
                              levelTypes,levelLengths,creaturesEnabled);

	//double realFitness =  100000.0 / (1.0 + fitness);

        printFitnessToFile(fitness);
    }


    private static float score(CmdLineOptions cmdLineOptions,int[] timeLimits,int[] levelDifficulties,
                              int[] levelTypes,int[] levelLengths,boolean[] creaturesEnables)
    {
    
//        final Environment environment = new MarioEnvironment();
//        System.out.println("agent = " + agent);
        final BasicTask basicTask = new BasicTask(cmdLineOptions);
        float fitness = 0;
        boolean verbose = false;
        int trials = 0;
        int disqualifications = 0;
        //----------------------------------------------------------------------
        
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
        
        return fitness;
    }
    


}
