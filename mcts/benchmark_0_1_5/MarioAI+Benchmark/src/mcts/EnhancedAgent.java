package mcts;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by Diego Perez, University of Essex.
 * Date: 23/04/13
 */
public abstract class EnhancedAgent implements Agent {

    public static final int MARIO_X = 11; //Environment.HalfObsHeight; //10;
    public static final int MARIO_Y = 11;//Environment.HalfObsWidth;  //10;

    public String name;
    public int zLevelScene = 1;
    public int zLevelEnemies = 0;

    protected byte[][] levelScene;
    protected byte[][] enemies;
    protected byte[][] mergedObservation;
    protected boolean[] action;

    public float[] marioFloatPos = null;
    protected float[] enemiesFloatPos = null;
    protected int[] marioState = null;

    protected int marioStatus;
    protected int marioMode;
    protected boolean isMarioOnGround;
    protected boolean isMarioAbleToJump;
    protected boolean isMarioAbleToShoot;
    protected boolean isMarioCarrying;
    protected int getKillsTotal;
    protected int getKillsByFire;
    protected int getKillsByStomp;
    protected int getKillsByShell;

    protected int m_MarioYInMapChunk;
    protected int m_MarioYInMap;
    protected int m_MarioXInMapChunk;
    protected int m_MarioXInMap;
    protected int m_OldMarioXInMap;
    protected float m_speedCellsPerCycleX;
    protected float m_lastRealXPos;
    protected int m_stuckCounter;

    private int m_inertia;
    private LinkedList<Boolean[]> m_lastActions;

    public MarioSimulator m_marioSim;

    public EnhancedAgent(String a_name)
    {
        //m_record = new Hashtable<Integer,Integer>();
       // m_gaps = new Vector<Integer>();
       // m_forbiddenGaps = new Vector<IntPair>();
        m_lastActions = new LinkedList<Boolean[]>();
       // m_map = new Map();
        m_marioSim = new MarioSimulator(this);
        marioFloatPos = new float[2];
        name = a_name;
        action = new boolean[10];
        //m_objective = -1;
        //m_resetThisCycle = false;
        //m_isFollowingPath = false;
        //m_followingNewPath = false;
        //m_onRightMostFlag = false;
        m_MarioXInMapChunk = 0;
        m_MarioXInMap = 0;
        m_OldMarioXInMap = 0;
        m_MarioYInMapChunk = 0;
        m_MarioYInMap = 0;
        //m_tickCounter = 0;
        //m_xLastScan = 0;
        m_inertia = 0;
        //m_maxGraphTo = 0;
        //m_marioInGraphNode = -1;
        //m_currentPath = null;
        //m_posIndexInPath = 0;
        m_speedCellsPerCycleX = 0;
        m_lastRealXPos = 0;
        //m_graphLenghtMultiplier = 1;

        //m_isATrap = false;
        //m_isATrapRight = false;
        //m_heightTrapRight = 0;
        //m_heightTrapLeft = 0;
        //m_trapPos = 0;

        reset();

    }

    public abstract boolean[] getAction();

    public void integrateObservation(Environment environment) {
        //To change body of implemented methods use File | Settings | File Templates.

        levelScene = environment.getLevelSceneObservationZ(zLevelScene);
        enemies = environment.getEnemiesObservationZ(zLevelEnemies);
        mergedObservation = environment.getMergedObservationZZ(1, 0);
        this.marioFloatPos = environment.getMarioFloatPos();
        this.enemiesFloatPos = environment.getEnemiesFloatPos();
        this.marioState = environment.getMarioState();
        m_speedCellsPerCycleX = this.marioFloatPos[0] - m_lastRealXPos;
        m_lastRealXPos = this.marioFloatPos[0];

        m_MarioXInMap = (int)marioFloatPos[0]/16;
        m_MarioXInMapChunk = (int)marioFloatPos[0]%16;//((marioFloatPos[0]/16.0f)*10)%10;
        if(m_MarioXInMapChunk <= 4) m_MarioXInMapChunk = 0;
        else if(m_MarioXInMapChunk >= 11) m_MarioXInMapChunk = 2;
        else m_MarioXInMapChunk = 1;

        m_MarioYInMap = 15 - ((int)marioFloatPos[1]/16) - 1;
        int marioYAux = (int)marioFloatPos[1]%16;//((marioFloatPos[0]/16.0f)*10)%10;
        //m_MarioYInMapChunk = (int)marioFloatPos[1]%16;//((marioFloatPos[0]/16.0f)*10)%10;
        if(marioYAux <= 4) m_MarioYInMapChunk = 0;
        else if(marioYAux >= 11) m_MarioYInMapChunk = 2;
        else marioYAux = 1;

        //System.out.println(m_MarioYInMap + " " + m_MarioYInMapChunk + "(" + marioYAux + ") " + marioFloatPos[1]);

        if(m_OldMarioXInMap == m_MarioXInMap)
        {
            m_stuckCounter++;
        }
        else
        {
            m_stuckCounter = 0;
        }
        m_OldMarioXInMap = m_MarioXInMap;


        //Calculate inertia
        m_inertia = 0;
        int m_nActions = m_lastActions.size();
        for(int i = m_nActions-1, j = 4; i >= 0; --i, j-=2)
        {
            int weight = (j>0) ? j : 1;
            int thisInertia = 0;
            Boolean[] thisActions = m_lastActions.get(i);
            if(thisActions[Mario.KEY_SPEED])
            {
                if(thisActions[Mario.KEY_LEFT])
                    thisInertia = -2;
                else if(thisActions[Mario.KEY_RIGHT])
                    thisInertia = 2;
            }else{
                if(thisActions[Mario.KEY_LEFT])
                    thisInertia = -1;
                else if(thisActions[Mario.KEY_RIGHT])
                    thisInertia = 1;
            }

            m_inertia += thisInertia*weight;
            //System.out.print(" " + thisInertia*weight);
        }

        marioStatus = marioState[0];
        marioMode = marioState[1];
        isMarioOnGround = marioState[2] == 1;
        isMarioAbleToJump = marioState[3] == 1;
        isMarioAbleToShoot = marioState[4] == 1;
        isMarioCarrying = marioState[5] == 1;
        getKillsTotal = marioState[6];
        getKillsByFire = marioState[7];
        getKillsByStomp = marioState[8];
        getKillsByShell = marioState[9];
    }

    public void reset() {

        m_MarioXInMap = 0;
        m_OldMarioXInMap = 0;
        m_stuckCounter = 0;
        for (int i = 0; i < action.length; ++i)
            action[i] = false;
    }

    public boolean isMarioSmall()  { return !(Mario.large || Mario.fire); }
    public boolean amIOnGround() { return isMarioOnGround; }
    public float getMarioGravity() { return 1.0f; }
    public AGENT_TYPE getType() { return Agent.AGENT_TYPE.AI; }
    public String getName() { return name; }
    public void setName(String Name) { this.name = Name; }

}
