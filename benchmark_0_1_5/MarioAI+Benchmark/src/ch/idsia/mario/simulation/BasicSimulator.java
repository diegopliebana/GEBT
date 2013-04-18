package ch.idsia.mario.simulation;

import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.tools.EvaluationInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 7, 2009
 * Time: 2:27:48 PM
 * Package: .Simulation
 */

@Deprecated
public class BasicSimulator implements Simulation
{
    SimulationOptions simulationOptions = null;
//    private MarioComponent marioComponent;

    @Deprecated
    public BasicSimulator(SimulationOptions simulationOptions)
    {
        GlobalOptions.isVisualization = simulationOptions.isVisualization();
//        this.marioComponent = GlobalOptions.getMarioComponent();
        this.setSimulationOptions(simulationOptions);
    }

//    private MarioComponent prepareMarioComponent()
//    {
//        Agent agent = simulationOptions.getAgent();
//        agent.reset();
//        marioComponent.setAgent(agent);
//        return marioComponent;
//    }

    @Deprecated
    public void setSimulationOptions(SimulationOptions simulationOptions)
    {
        this.simulationOptions = simulationOptions;
    }

    @Deprecated
    public EvaluationInfo simulateOneLevel()
    {
        Mario.resetStatic(simulationOptions.getMarioMode());        
//        prepareMarioComponent();
//        marioComponent.setZLevelScene(simulationOptions.getZLevelScene());
//        marioComponent.setZLevelEnemies(simulationOptions.getZLevelEnemies());
//        marioComponent.startLevel(simulationOptions.getLevelRandSeed(), simulationOptions.getLevelDifficulty()
//                                 , simulationOptions.getLevelType(), simulationOptions.getLevelLength(),
//                                  simulationOptions.getTimeLimit());
//        marioComponent.setPaused(simulationOptions.isPauseWorld());
//        marioComponent.setZLevelEnemies(simulationOptions.getZLevelEnemies());
//        marioComponent.setZLevelScene(simulationOptions.getZLevelScene());
//        marioComponent.setMarioInvulnerable(simulationOptions.isMarioInvulnerable());
//        return marioComponent.run1(simulationOptions.currentTrial++
//        );
        return null;        
    }
}
