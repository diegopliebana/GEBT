package ch.idsia.scenarios.oldscenarios;
//https://marioai.googlecode.com/svn/trunk/src

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AmiCoAgent;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.Evaluator;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstName_at_idsia_dot_ch
 * Date: May 7, 2009
 * Time: 4:38:23 PM
 * Package: ch.idsia
 */

public class CustomRun
{
    public static void main(String[] args)
    {
        CmdLineOptions options = new CmdLineOptions(args);
//        Evaluator evaluator = new Evaluator(options);
//        Agent agent = new ForwardAgent();
        options.setFPS(24);
//        options.setVisualization(false);
        String amicoModuleName = options.getPyAmiCoModuleName();
        System.out.println("amicoModuleName = " + amicoModuleName);
//        String amicoAgentName = "ForwardAgent";
        System.out.println("options.getAgentName() = " + options.getAgentName());
        Agent agent = new AmiCoAgent(amicoModuleName, options.getAgentName());
        options.setAgent(agent);
//        evaluator.evaluate();
//        System.out.println("evaluator = " + evaluator.getMeanEvaluationSummary());
        System.exit(0);
    }
}
