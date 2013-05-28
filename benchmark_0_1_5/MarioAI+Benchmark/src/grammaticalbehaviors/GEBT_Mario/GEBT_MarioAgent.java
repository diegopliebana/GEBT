package grammaticalbehaviors.GEBT_Mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Sprite;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.EvaluationInfo;
import grammaticalbehaviors.bt.behaviortree.BTStream;
import grammaticalbehaviors.bt.behaviortree.BehaviorTree;
import java.util.Hashtable;
import java.util.Vector;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. \n User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 24, 2010 Time: 6:51:44 PM
 * Package: competition.evostar.sergeykarakovskiy
 * -ag grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent
 */
public class GEBT_MarioAgent implements Agent
{
    private String name;
    private boolean[] action;
    Hashtable<Integer,Integer> m_record;
    Vector<Integer> m_gaps;
    Vector<IntPair> m_forbiddenGaps;
    Path m_currentPath;
    int m_posIndexInPath;

    //private boolean[] m_lastAction;
    
    public static final int MARIO_X = 11; //Environment.HalfObsHeight; //10;
    public static final int MARIO_Y = 11;//Environment.HalfObsWidth;  //10;
    
    /*final*/ protected byte[][] levelScene;
    /*final */protected byte[][] enemies;
    protected byte[][] mergedObservation;

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
    protected int m_marioInGraphNode;
    protected boolean m_isFollowingPath;
    protected boolean m_followingNewPath;
    protected float m_speedCellsPerCycleX;
    protected float m_lastRealXPos;
    protected int m_maxGraphTo;
    protected boolean m_onRightMostFlag;

    protected int m_stuckCounter;
    private final int CYCLES_TO_STUCK = 40;

    private boolean m_isATrap;
    private boolean m_isATrapRight;
    private int m_heightTrapRight;
    private int m_heightTrapLeft;
    private int m_trapPos;

    private int m_tickCounter;
    private int m_xLastScan;
    private int m_objective;

    private int m_inertia;
    private LinkedList<Boolean[]> m_lastActions;
    private final int MAX_INERTIA_ACTIONS = 5;
    private MoveSimulator m_moveSim;
    private int m_graphLenghtMultiplier;

    //Our Behavior Tree
    protected BehaviorTree m_behaviorTree;

    //Indicates that the agent must be reseted this cycle
    private boolean m_resetThisCycle;

    //Map
    private Map m_map;

    public GEBT_MarioAgent()
    {
        m_record = new Hashtable<Integer,Integer>();
        m_gaps = new Vector<Integer>();
        m_forbiddenGaps = new Vector<IntPair>();
        m_lastActions = new LinkedList<Boolean[]>();
        m_map = new Map();
        m_moveSim = new MoveSimulator(this);
        marioFloatPos = new float[2];
        name = "GEBT_MarioAgent";
        action = new boolean[10];
        m_objective = -1;
        m_resetThisCycle = false;
        m_isFollowingPath = false;
        m_followingNewPath = false;
        m_onRightMostFlag = false;
        m_MarioXInMapChunk = 0;
        m_MarioXInMap = 0;
        m_OldMarioXInMap = 0;
        m_MarioYInMapChunk = 0;
        m_MarioYInMap = 0;
        m_tickCounter = 0;
        m_xLastScan = 0;
        m_inertia = 0;
        m_maxGraphTo = 0;
        m_marioInGraphNode = -1;
        m_currentPath = null;
        m_posIndexInPath = 0;
        m_speedCellsPerCycleX = 0;
        m_lastRealXPos = 0;
        m_graphLenghtMultiplier = 1;

        m_isATrap = false;
        m_isATrapRight = false;
        m_heightTrapRight = 0;
        m_heightTrapLeft = 0;
        m_trapPos = 0;

        reset();

        //String filename = "best-1-10-56926.8.xml";
        
        //loadBehaviorTree("bestIndividual_GEBT_MarioAgent.xml");
        //loadBehaviorTree(filename);
        //loadBehaviorTree("pathFollower.xml");
        //System.out.println("Tried to load: " + filename);
        
    }

    public void resetAgent()
    {
        m_record = new Hashtable<Integer,Integer>();
        m_gaps = new Vector<Integer>();
        m_forbiddenGaps = new Vector<IntPair>();
        m_lastActions = new LinkedList<Boolean[]>();
        m_moveSim = new MoveSimulator(this);
        m_map = new Map();
        marioFloatPos = new float[2];
        name = "GEBT_MarioAgent";
        action = new boolean[10];
        m_objective = -1;
        m_inertia = 0;
        m_graphLenghtMultiplier = 1;

        m_speedCellsPerCycleX = 0;
        m_lastRealXPos = 0;
        m_tickCounter = 0;
        m_maxGraphTo = 0;
        m_isATrap = false;
        m_isATrapRight = false;
        m_isFollowingPath = false;
        m_onRightMostFlag = false;
        m_followingNewPath = false;
        m_currentPath = null;
        m_posIndexInPath = 0;
        m_heightTrapRight = 0;
        m_heightTrapLeft = 0;
        m_trapPos = 0;
        m_marioInGraphNode = -1;
        m_xLastScan = 0;

        reset();
        m_behaviorTree.reset();


        m_resetThisCycle = false;
    }

    public void loadBehaviorTree(BTStream a_stream)
    {
        reset();
        m_behaviorTree = new BehaviorTree(this, new MarioXMLReader());
        m_behaviorTree.load(a_stream);
    }

    
    public void loadBehaviorTree(String a_filename)
    {
        reset();
        m_behaviorTree = new BehaviorTree(this, new MarioXMLReader());
        m_behaviorTree.load(a_filename);
    }

    public Map getMap() {return m_map;}
    public Vector<IntPair> getForbiddenGaps() {return m_forbiddenGaps;}

    public void deleteForbiddenGap(int toDelete)
    {
        boolean deleted = false;
        int gapIndex = 0;
        while(!deleted && gapIndex < m_forbiddenGaps.size())
        {
            IntPair storedPair = m_forbiddenGaps.get(gapIndex);
            //if(toDelete == storedPair.a  &&  toDelete == storedPair.b)
            if(toDelete == storedPair.a  &&  toDelete == storedPair.b)
            {
                //The stored pair must be deleted
                m_forbiddenGaps.remove(gapIndex);
                deleted = true;
            }
            else if(toDelete >= storedPair.a  &&  toDelete < storedPair.b)
            {
                storedPair.a = toDelete+1;
                deleted = true;
            }
            else
            {
                gapIndex++;
            }

        }
    }

    public void addForbiddenGap(int a, int b)
    {
        if(a > b)
        {
            int swp = b;
            b = a;
            a = swp;
        }
        IntPair newPair = new IntPair(a, b);

        boolean mustAdd = true;
        int gapIndex = 0;
        while(mustAdd && gapIndex < m_forbiddenGaps.size())
        {
            IntPair storedPair = m_forbiddenGaps.get(gapIndex);
            if(newPair.a <= storedPair.a  &&  newPair.b >= storedPair.b)
            {
                //The stored pair can be deleted
                m_forbiddenGaps.remove(gapIndex); //Dont increase the index in this case!
            }
            else if(newPair.a >= storedPair.a  &&  newPair.b <= storedPair.b)
            {
                //This pair is covered y another one already in the vector.
                mustAdd = false;
            }
            else
            {
                gapIndex++;
            }
            
        }

        if(mustAdd)
            m_forbiddenGaps.add(newPair);
    }
    
    public boolean isGapForbidden(int a_gapPos)
    {
        boolean forbidden = false;
        int gapIndex = 0;
        while(!forbidden && gapIndex < m_forbiddenGaps.size())
        {
            IntPair storedPair = m_forbiddenGaps.get(gapIndex);
            if(a_gapPos >= storedPair.a  &&  a_gapPos <= storedPair.b)
            {
                //This gap is forbidden by the pairs I have stored.
                forbidden = true;
            }

            gapIndex++;
        }
        
        return forbidden;
    }

    public boolean isNodeInCurrentPath(int a_nodeId)
    {
        return m_currentPath.m_points.contains(a_nodeId);
    }

    public int getClosestNodeToMario()
    {
        int nodeIdOld = m_map.getGraph().getClosestNodeTo(m_MarioXInMap, m_MarioYInMap);
        int nodeId = m_map.getGraph().getClosestNodeToFloatPos(marioFloatPos[0], marioFloatPos[1]);
        return nodeId;
    }

    public float getMarioSpeedX() {return m_speedCellsPerCycleX;}
    public MoveSimulator getMoveSimulator() {return m_moveSim;}
    public boolean isFollowingNewPath() {return m_followingNewPath;}
    public void followingNewPath(boolean a_new) {m_followingNewPath = a_new;}
    public void setFollowingPath(boolean a_follow) {m_isFollowingPath = a_follow;}
    public boolean isFollowingPath() {return m_isFollowingPath;}
    public boolean inTrap() {return m_isATrap;}
    public boolean isRightTrap() {return m_isATrapRight;}
    public int heightTrapRight() {return m_heightTrapRight;}
    public int heightTrapLeft() {return m_heightTrapLeft;}
    public int trapPos()    {return m_trapPos;}
    public int getObjective()   {return m_objective;}
    public void setObjective(int o) {m_objective = o;}

    public Hashtable<Integer,Integer> getRecord() {return m_record;}
    public Vector<Integer> getGaps() {return m_gaps;}
    
    // values of these variables could be changed during the Agent-Environment interaction.
    // Use them to get more detailed or less detailed description of the level.
    // for information see documentation for the benchmark <link: marioai.org/marioaibenchmark/zLevels

    // *** Level 2: Obstacle (1) or not (0) ***
    /*
     LevelScene:
        1 - any kind of obstacle, cannot pass through
        0 - no obstacle, can pass through

     Enemies:
        1 - some enemy
        0 - no any enemy, Sprite.KIND_NONE

     */

    // *** Level 1: More detailed ***
    /*
     LevelScene:
        0 - no obstacle
        -10 -- hard obstacle, cannot pass through
        -11 -- soft obstacle, can overjump (// half-border, can jump through from bottom and can stand on)
        20 -- angry enemy flower pot or parts of a cannon
        16 - brick (simple or with a hidden coin or with a hidden mushroom/flower)
        21 - question brick (with a coin or mushroom/flower)

     Enemies:
        0 - no enemy in a cell
        2 - Enemy that you can kill by shooting or jumping on it (KIND_BULLET_BILL, KIND_GOOMBA, KIND_GOOMBA_WINGED,
            KIND_GREEN_KOOPA, KIND_GREEN_KOOPA_WINGED, KIND_RED_KOOPA, KIND_RED_KOOPA_WINGED, KIND_SHELL)
        9 - KIND_SPIKY or KIND_ENEMY_FLOWER (cannot kill by jumping, but can kill one of them by shooting;
            hint: FLOWER is only above the flower spot and always above ground)
        25 - KIND_FIREBALL, mario weapon projectile.
     */

     // *** Level 0: Crazy: see http://www.marioai.org/gameplay-track/marioai-benchmark ***

    int zLevelScene = 1;
    int zLevelEnemies = 0;

    /*public boolean[] getActionsCopy()
    {
        boolean[] newActions = new boolean[action.length];
        for(int i = 0; i < action.length; ++i)
        {
            newActions[i] = action[i];
        }
        return newActions;
    }

    public void cleanThisActions(boolean[] a_actions)
    {
        for(int i = 0; i < a_actions.length; ++i)
        {
            a_actions[i] = false;
        }
    }*/

    public void setOnRightMostFlag(boolean a_flag) {m_onRightMostFlag = true;}
    public boolean onRightMostFlag() {return m_onRightMostFlag;}

    private void cleanActions()
    {
        for(int i = 0; i < action.length; ++i)
        {
            action[i] = false;
        }
    }

    public void registerActions()
    {
        m_moveSim.registerAction(action);
    }

    /*private void recordActions()
    {
        for(int i = 0; i < action.length; ++i)
        {
            m_lastAction[i] = action[i];
        }
    }*/
    
    public void setAction(int a_actionIndex, boolean a_value)
    {
        action[a_actionIndex] = a_value;
    }   
    
    
    public boolean[] getAction()
    {
        m_tickCounter++;

        //Clean all the actions
        cleanActions();
        
        //Execute BT to get the action to do.
        m_behaviorTree.execute();

        /*cleanActions();
         long c = m_behaviorTree.getCurTick();
         if(c%10 != 0)
            action[Mario.KEY_JUMP] = true;

         if(isMarioOnGround)
         {
             int asd = 0;
         }
         */

        if(m_resetThisCycle)
        {
            resetAgent();
        }

        recordActions();
        
        //And this is the action that must be taken:
        return action;
    }

    private void recordActions()
    {
        if(m_lastActions.size() >= MAX_INERTIA_ACTIONS)
        {
            m_lastActions.removeFirst();
        }
        Boolean[] actionArray = new Boolean[action.length];
        for(int i = 0; i < actionArray.length; ++i) actionArray[i] = action[i];
        m_lastActions.addLast(actionArray);
    }

    public byte[][] getLevelScene()
    {
        return levelScene;
    }
    
    public byte[][] getEnemyScene()
    {
        return enemies;
    }

    public boolean isOverOrEqualTo(int a_compareY)
    {
        boolean over = m_MarioYInMap >= a_compareY;
        return over;
    }

    public int getDistanceYTo(int a_compareY)
    {
        return a_compareY - m_MarioYInMap;
    }

    public int getDistanceXTo(int a_compareX)
    {
        return a_compareX - m_MarioXInMap;
    }

    public boolean isMarioOnLeftOf(int a_compareX)
    {
        boolean onLeft = m_MarioXInMap < a_compareX ||
                (m_MarioXInMap == a_compareX && m_MarioXInMapChunk == 0);
        return onLeft;
    }

    public boolean isMarioOnRightOf(int a_compareX)
    {
        boolean onRight = m_MarioXInMap > a_compareX ||
                (m_MarioXInMap == a_compareX && m_MarioXInMapChunk == 2);
        return onRight;
    }

    public boolean isMarioOnPos(int a_compareX)
    {
        boolean onPos = (m_MarioXInMap == a_compareX && m_MarioXInMapChunk == 1);
        return onPos;
    }

    public boolean CellOnRight()
    {
        return m_MarioXInMapChunk == 2;
    }

    public boolean CellOnLeft()
    {
        return m_MarioXInMapChunk == 0;
    }

    public int getMarioSafePos()
    {
        int pos = m_MarioXInMap;
        if(m_MarioXInMapChunk == 1) return pos;
        else return -1;
    }


    public void integrateObservation(Environment environment)
    {
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

        EvaluationInfo evaluationInfo = environment.getEvaluationInfo();
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

        if(m_MarioXInMapChunk == 1)
        {
            scanLevel();
            trap();
        }

        m_marioInGraphNode = m_map.getGraph().existsNode(m_MarioXInMap, m_MarioYInMap);


        if(environment.isLevelFinished())
        {
            m_resetThisCycle = true;
        }

    }

    public void scanScreen()
    {
        m_map.flushEnemiesAndItems();

        int posX = m_xLastScan;
        //for(int posX = 0; posX < levelScene[0].length; ++posX)
        for(int scanCounter = 0; scanCounter < levelScene[0].length; ++scanCounter)
        {

            int posXInMap = m_MarioXInMap + (posX - GEBT_MarioAgent.MARIO_Y) /*+ 1*/;
            m_map.setCurrentWrittingX(posXInMap);

            //On whole scans, only print if never printed before (it is the whole point of all this).
            //if(posXInMap <= 0) continue;
            
            //Let's scan this vertical: (15 is the height of the map, constant)
            //Do it form top to bottom to get positions where Mario can stand.
            for(int i = 14; i >=0 ; --i)
            {
                int diffValHeight = i - m_MarioYInMap;
                int gridPosHeight = (GEBT_MarioAgent.MARIO_X - diffValHeight)/* - 1*/;

                //Scan this column
                if(gridPosHeight >= 0 && gridPosHeight < levelScene.length)
                {
                    byte data = levelScene[gridPosHeight][posX];

                    //if(data != 0)
                    /*if(posXInMap == 60)
                    {
                        int a = 0;
                    }*/


                    m_map.writeLevel(i, data);

                    byte enemyData = enemies[gridPosHeight][posX];
                    if(enemyData != Sprite.KIND_NONE && enemyData != Sprite.KIND_FIREBALL)
                    {
                        m_map.writeEnemy(i, enemyData);
                    }


                    m_map.updateLastCell();
                }
            }

            //advance pos scan
            posX++; 
            if(posX >= levelScene[0].length) posX = 0; //Keep in bounds.
        }
        m_xLastScan = posX;
    }

    public void resetGraphLength()
    {
        m_graphLenghtMultiplier = 1;
    }


    public boolean increaseGraphLength()
    {
        m_graphLenghtMultiplier++;
        if(m_graphLenghtMultiplier>2)
        {
            m_graphLenghtMultiplier = 2;
            return false;
        }
        return true;
    }

    public void scanLevel()
    {
        if(m_MarioXInMapChunk == 1)
        {
            //Time to a whole scan.
            scanScreen();
            //m_map.dumpToFile("levelDump.txt");

            //Let's try something!
            //m_map.processGraph(m_MarioXInMap - 50,m_MarioXInMap + 50);

            //long ms1 = System.currentTimeMillis();

          //  System.out.println("###########################");
          int from = m_MarioXInMap - 40*m_graphLenghtMultiplier;
          int to = m_MarioXInMap + 40;
          if(to < m_maxGraphTo)
            to = m_maxGraphTo;

          m_maxGraphTo = to;


          m_map.processGraph(from,to);

          //Mario position in graph:
          m_marioInGraphNode = m_map.getGraph().existsNode(m_MarioXInMap, m_MarioYInMap);

          //  System.out.println("###########################");

            //long ms2 = System.currentTimeMillis();
            //System.out.println("PROCESS GRAPH: " + (long)(ms2 - ms1));

            /*if(m_MarioXInMap == 65)
                m_map.graphCheckJumps(50,75);*/

//m_map.dumpToFileProcessing("processingGraphHelper/processingGraphHelper.pde");

            //long ms3 = System.currentTimeMillis();
            //System.out.println("PROCESS GRAPH: " + (long)(ms3 - ms2) + ", graph total: " + (long)(ms2 - ms1));

        }

    
    }

    public void setPath(Path a_destPath)
    {
        m_currentPath = a_destPath;
        m_posIndexInPath = 0;
    }

    public void advancePathIndex() {m_posIndexInPath++;}
    public void setCurrentPathIndex(int a_newIndex) {m_posIndexInPath = a_newIndex;}
    public int getInertia() {return m_inertia;}
    public Path getCurrentPath() {return m_currentPath;}
    public int getPosIndexInPath() {return m_posIndexInPath;}
    public int getMarioNodeInMap() {return m_marioInGraphNode;}
    //public int getXInMap(){return m_MarioXInMap;}
    //public int getYInMap(){return m_MarioYInMap;}
    public boolean canIJump() {return isMarioAbleToJump;}
    public boolean canIFire() {return isMarioAbleToShoot;}
    public boolean amIOnGround() {return isMarioOnGround;}

    private boolean checkMarioFits(int horizontal, int vertical, int minX)
    {
        boolean fits = false;
        boolean oldFree = false;

        boolean marioSmall = marioMode == 0;
        int h = horizontal;
        while(!fits && h <= minX)
        {
            //Go down looking for gaps.
            boolean freeHere = !isObstacle(levelScene[h][vertical]);
            
            //1-cell gaps, for small Mario.
            if(freeHere && marioSmall)
            {
                //This is a 1-cell gap.
                if(h >= MARIO_X-4)
                    fits = true; //And reachable!
            }

            ///2-cell gaps, for large Mario.
            if(freeHere && oldFree)
            {
                //This is a 2-cell gap.
                if(h >= MARIO_X-4)
                    fits = true; //And reachable!
            }

            oldFree = freeHere;
            ++h;
        }
        return fits;
    }


    public boolean trap()
    {

        if(!isMarioOnGround)
        {
            //If we are jumping, it is no sense in searching for traps. Do it on ground!
            return false;
        }

        //First, find obstacle in vertical:
        int xmin = MARIO_X-1;
        int xmax = MARIO_X-10; //Environment.HalfObsHeight;
        if(xmax < 0) xmax = 0;
        
        boolean obsFound = false;
        int i = xmin;
        m_isATrap = false;


        for(;!obsFound && i > xmax; --i)
        {//WATCH OUT: THIS GOES BOTTOM UP!!!
           obsFound = isObstacle(levelScene[i][MARIO_Y-1]);
        }

        if(obsFound)
        {
            //Second A, follow obstacles to the RIGHT
            {
                int x = i+1;
                int ymin = MARIO_Y+1;
                int ymax = MARIO_Y+10;
                if(ymax >= levelScene[x].length)
                    ymax = levelScene[x].length-1;

                for(int j = ymin;obsFound && j < ymax; ++j)
                {
                    obsFound = isObstacle(levelScene[x][j]);

                    int y = j;
                    int k = x+1;
                    if(!obsFound)
                    {
                        //There is a gap in the wall, we need to check that mario fits (this checks the whole vertical)
                        boolean marioFits = checkMarioFits(k, y, MARIO_X);
                        //if Mario does not fit, then it is a trap
                        if(!marioFits)
                        {
                            m_isATrap = true;
                            m_isATrapRight = true;
                            m_trapPos = m_MarioXInMap;
                            m_heightTrapRight = x;
                            return true;
                        }
                    }
                    else //obsFound
                    {
                        boolean obsBack = true;
                        //Third, check the vertical down.
                        for(;obsBack && k <= MARIO_X; ++k)
                        {
                            obsBack &= isObstacle(levelScene[k][y]);
                        }

                        //If obsBack... OH, IT IS A TRAP!!
                        if(obsBack)
                        {
                            m_isATrap = true;
                            m_isATrapRight = true;
                            m_trapPos = m_MarioXInMap;
                            m_heightTrapRight = x;
                            return true;
                        }
                        else
                        {
                            //There is a gap in the wall, we need to check that mario fits (this checks the whole vertical)
                            boolean marioFits = checkMarioFits(k-1, y, MARIO_X);
                            //if Mario does not fit, then it is a trap
                            if(!marioFits)
                            {
                                m_isATrap = true;
                                m_isATrapRight = true;
                                m_trapPos = m_MarioXInMap;
                                m_heightTrapRight = x;
                                return true;
                            }
                        }


                    }
                }
            }

            //Second B, follow obstacles to the LEFT
            {
                obsFound = true;
                int x = i+1;
                int ymin = MARIO_Y-1;
                int ymax = MARIO_Y-10;

                if(ymax < 0)
                    ymax = 0;

                for(int j = ymin;obsFound && j > ymax; --j)
                {
                    obsFound = isObstacle(levelScene[x][j]);
                    int y = j;
                    int k = x+1;
                    if(!obsFound)
                    {
                        //There is a gap in the wall, we need to check that mario fits (this checks the whole vertical)
                        boolean marioFits = checkMarioFits(k, y, MARIO_X);
                        //if Mario does not fit, then it is a trap
                        if(!marioFits)
                        {
                            m_isATrap = true;
                            m_isATrapRight = false;
                            m_trapPos = m_MarioXInMap;
                            m_heightTrapLeft = x;
                            return true;
                        }
                    }
                    else //obsFound
                    {
                        boolean obsBack = true;
                        //Third, check the vertical down.
                        for(;obsBack && k <= MARIO_X; ++k)
                        {
                            obsBack &= isObstacle(levelScene[k][y]);
                        }

                        //If obsBack... OH, IT IS A TRAP!!
                        if(obsBack)
                        {
                            m_isATrap = true;
                            m_isATrapRight = false;
                            m_trapPos = m_MarioXInMap;
                            m_heightTrapLeft = x;
                            return true;
                        }
                        else
                        {
                            //There is a gap in the wall, we need to check that mario fits (this checks the whole vertical)
                            boolean marioFits = checkMarioFits(k-1, y, MARIO_X);
                            //if Mario does not fit, then it is a trap
                            if(!marioFits)
                            {
                                m_isATrap = true;
                                m_isATrapRight = false;
                                m_trapPos = m_MarioXInMap;
                                m_heightTrapLeft = x;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean reachableGap(int a_xmin, int a_xmax, int pos)
    {
        //1st check, I can reach the gap
        boolean reachableGap = false;
        int destinationY = GEBT_MarioAgent.MARIO_Y;
        if(pos-1 > m_MarioXInMap)
        {
            destinationY = GEBT_MarioAgent.MARIO_Y + (pos - 1 - m_MarioXInMap);
            reachableGap = !isObstacle(GEBT_MarioAgent.MARIO_X-1, GEBT_MarioAgent.MARIO_X,GEBT_MarioAgent.MARIO_Y, destinationY);
        }
        else if(pos-1 < m_MarioXInMap)
        {
            destinationY = GEBT_MarioAgent.MARIO_Y + (pos - 1 - m_MarioXInMap);
            reachableGap = !isObstacle(GEBT_MarioAgent.MARIO_X-1, GEBT_MarioAgent.MARIO_X, destinationY, GEBT_MarioAgent.MARIO_Y);
        }
        else reachableGap = true;

        //2nd check, there must be space for me over the gap
        if(reachableGap)
        {
            boolean obsInTop = isObstacle(a_xmin, a_xmin, destinationY+1, destinationY+1); //Identify where the obstacle is.
            boolean obsInDown = isObstacle(a_xmax, a_xmax, destinationY+1, destinationY+1);
            int hMin = a_xmin, hMax = a_xmax;
            if(obsInTop && !obsInDown)
            {
//hMin = a_xmax;//THIS IS BAD
//hMax = a_xmax + 1;//THIS IS BAD

                hMin = a_xmin-2;   //THIS IS GOOD
                hMax = a_xmax-2;  //THIS IS GOOD
            }else if(obsInDown)
            {
//hMin = a_xmin + 2;//THIS IS BAD
//hMax = a_xmax + 2;//THIS IS BAD
                hMin = a_xmin-1;//THIS IS GOOD
                hMax = a_xmax-1;//THIS IS GOOD
            }

            boolean marioSmall = marioMode == 0;
            if(marioSmall) hMax = hMin;
            reachableGap = !isObstacle(hMin, hMax, destinationY+1, destinationY+1); //Check if there is space enough for Mario to jump.
        }

        return reachableGap;
    }



    public void snapshot(int a_xmin, int a_xmax, int a_ymin, int a_ymax)
    {
        m_record = new Hashtable<Integer,Integer>();
        if(!isMarioOnGround) return;

        int minKey = m_MarioXInMap + (a_ymin - GEBT_MarioAgent.MARIO_Y);
        int maxKey = m_MarioXInMap + (a_ymax - GEBT_MarioAgent.MARIO_Y);
        for(int j = a_ymin; j <= a_ymax; ++j)
        {
            boolean obstacle = false;
            for(int i = a_xmin; i <= a_xmax; ++i)
            {
                boolean obsFound = isObstacle(levelScene[i][j]);
                if(obsFound)
                {
                    obstacle = true;
                }
            }

            int desp = j - GEBT_MarioAgent.MARIO_Y;
            int pos = m_MarioXInMap + desp;
            if(!obstacle)
            {
                m_record.put(pos, Sprite.KIND_NONE);
            }
            else
            {
                m_record.put(pos, 1);
            }
        }

        //Now, detect and store gaps
        m_gaps = new Vector<Integer>();
        boolean lastObs = false;
        int obsState = 0; //When is 2, gap found.
        for(int i = minKey; i <= maxKey; ++i)
        {
            int type = m_record.get(i);
            if(!lastObs && type == 1)
            {
                //Found an obstacle
                ++obsState;
                lastObs = true;
                if(obsState == 1)
                {
                    boolean reachableGap = reachableGap(a_xmin, a_xmax, i);
                    if(reachableGap)
                    {
                        m_gaps.add(i-1);
                    }
                }
                else if(obsState == 2)
                {
                    
                    boolean reachableGap = reachableGap(a_xmin, a_xmax, i);
                    if(reachableGap)
                    {
                        m_gaps.add(i-1);
                    }
                    obsState = 1;

                    //1st check, I can reach the gap
                    /*boolean reachableGap = false;
                    int destinationY = GEBT_MarioAgent.MARIO_Y;
                    if(i-1 > m_cellsRun)
                    {
                        destinationY = GEBT_MarioAgent.MARIO_Y + (i - 1 - m_cellsRun);
                        reachableGap = !isObstacle(GEBT_MarioAgent.MARIO_X-1, GEBT_MarioAgent.MARIO_X,GEBT_MarioAgent.MARIO_Y, destinationY);
                    }
                    else if(i-1 < m_cellsRun)
                    {
                        destinationY = GEBT_MarioAgent.MARIO_Y - (i - 1 - m_cellsRun);
                        reachableGap = !isObstacle(GEBT_MarioAgent.MARIO_X-1, GEBT_MarioAgent.MARIO_X, destinationY, GEBT_MarioAgent.MARIO_Y);
                    }
                    else reachableGap = true;

                    //2nd check, there must be space for me over the gap
                    if(reachableGap)
                    {
                        boolean obsInMin = isObstacle(a_xmin, a_xmin, destinationY+1, destinationY+1); //Identify where the obstacle is.
                        boolean obsInMax = isObstacle(a_xmax, a_xmax, destinationY+1, destinationY+1);
                        int hMin = a_xmin, hMax = a_xmax;
                        if(obsInMin && !obsInMax)
                        {
                            hMin = a_xmax;
                            hMax = a_xmax + 1;
                        }else if(obsInMax)
                        {
                            hMin = a_xmin + 2;
                            hMax = a_xmax + 2;
                        }

                        reachableGap = !isObstacle(hMin, hMax, destinationY+1, destinationY+1); //Check if there is space enough for Mario to jump.
                    }*/


                }
            }
            else if(lastObs && type == Sprite.KIND_NONE)
            {
                lastObs = false;
            }
        }

    }

    public boolean isBulletToHead()
    {
        int destY = m_MarioYInMap;
        if(this.isMarioLarge())
        {
            destY++;
        }

        return m_map.checkForEnemyType(m_MarioXInMap-2, m_MarioXInMap+2, destY, destY, Sprite.KIND_BULLET_BILL);
    }


    public boolean isBulletToFeet()
    {
        int destY = m_MarioYInMap;
        return m_map.checkForEnemyType(m_MarioXInMap-2, m_MarioXInMap+2, destY, destY, Sprite.KIND_BULLET_BILL);
    }



    public boolean isJumpableEnemy(byte a_type)
    {
        switch(a_type)
        {
            case (Sprite.KIND_GOOMBA):
            case (Sprite.KIND_GREEN_KOOPA):
            case (Sprite.KIND_RED_KOOPA):
            case (Sprite.KIND_SHELL):
            case (Sprite.KIND_BULLET_BILL):
            case (Sprite.KIND_RED_KOOPA_WINGED):
            case (Sprite.KIND_GOOMBA_WINGED):
            case (Sprite.KIND_GREEN_KOOPA_WINGED):
                return true;
            case (Sprite.KIND_SPIKY):
            case (Sprite.KIND_ENEMY_FLOWER):
            case (Sprite.KIND_SPIKY_WINGED):
                return false;
        }
        return false;
    }


    /*public boolean isJumpableEnemy(byte a_type)
    {
        if((a_type >= Sprite.KIND_GOOMBA && a_type <= Sprite.KIND_BULLET_BILL) || 
            a_type == Sprite.KIND_SHELL)
            return true;
        return false;
    }*/
    
    
    public boolean isJumpableEnemy(int a_xmin, int a_xmax, int a_ymin, int a_ymax)
    {
        if(a_xmin < 0) a_xmin = 0;
        if(a_ymin < 0) a_ymin = 0;
        if(a_xmax >= enemies.length) a_xmax = enemies.length-1;
        if(a_ymax >= enemies[0].length) a_ymax = enemies[0].length-1;
        
        for(int i = a_xmin; i <= a_xmax; ++i)
        {
            for(int j = a_ymin; j <= a_ymax; ++j)
            {
                boolean enemyFound = isJumpableEnemy(enemies[i][j]);
                if(enemyFound) return true;
            }    
        }
    
        return false;
    }
    
    
    public boolean isNoJumpableEnemy(byte a_type)
    {
        return !isJumpableEnemy(a_type);
    }
    
    
    public boolean isNoJumpableEnemy(int a_xmin, int a_xmax, int a_ymin, int a_ymax)
    {
        if(a_xmin < 0) a_xmin = 0;
        if(a_ymin < 0) a_ymin = 0;
        if(a_xmax >= enemies.length) a_xmax = enemies.length-1;
        if(a_ymax >= enemies[0].length) a_ymax = enemies[0].length-1;
        
        for(int i = a_xmin; i <= a_xmax; ++i)
        {
            for(int j = a_ymin; j <= a_ymax; ++j)
            {
                boolean enemyFound = isNoJumpableEnemy(enemies[i][j]);
                if(enemyFound) return true;
            }    
        }
    
        return false;
    }
    
    
    public boolean isEnemy(byte a_type)
    {
        /*if(a_type >= Sprite.KIND_GOOMBA && a_type <= Sprite.KIND_SHELL)
            return true;
        return false;*/
        switch(a_type)
        {
            case (Sprite.KIND_GOOMBA):
            case (Sprite.KIND_GREEN_KOOPA):
            case (Sprite.KIND_RED_KOOPA):
            case (Sprite.KIND_SHELL):
            case (Sprite.KIND_BULLET_BILL):
            case (Sprite.KIND_RED_KOOPA_WINGED):
            case (Sprite.KIND_GOOMBA_WINGED):
            case (Sprite.KIND_GREEN_KOOPA_WINGED):
            case (Sprite.KIND_SPIKY):
            case (Sprite.KIND_ENEMY_FLOWER):
            case (Sprite.KIND_SPIKY_WINGED):
                return true;
        }
        return false;
    }
    
    
    public boolean isEnemy(int a_xmin, int a_xmax, int a_ymin, int a_ymax)
    {
        if(a_xmin < 0) a_xmin = 0;
        if(a_ymin < 0) a_ymin = 0;
        if(a_xmax >= enemies.length) a_xmax = enemies.length-1;
        if(a_ymax >= enemies[0].length) a_ymax = enemies[0].length-1;
        
        for(int i = a_xmin; i <= a_xmax; ++i)
        {
            for(int j = a_ymin; j <= a_ymax; ++j)
            {
                boolean enemyFound = isEnemy(enemies[i][j]);
                if(enemyFound) return true;
            }    
        }
    
        return false;
    }


    public boolean isItem(int a_xmin, int a_xmax, int a_ymin, int a_ymax)
    {
        if(a_xmin < 0) a_xmin = 0;
        if(a_ymin < 0) a_ymin = 0;
        if(a_xmax >= enemies.length) a_xmax = enemies.length-1;
        if(a_ymax >= enemies[0].length) a_ymax = enemies[0].length-1;

        for(int i = a_xmin; i <= a_xmax; ++i)
        {
            for(int j = a_ymin; j <= a_ymax; ++j)
            {
                boolean itemFound = isItem(enemies[i][j]);
                if(itemFound) return true;
            }
        }

        return false;
    }
     
    public boolean isObstacle(byte a_type)
    {
        if(a_type != Map.MAP_NOTHING && a_type != Map.MAP_SOFT_OBSTACLE)
            return true;
        return false;
    }
    
/*
     LevelScene:
        0 - no obstacle
        -10 -- hard obstacle, cannot pass through
        -11 -- soft obstacle, can overjump (// half-border, can jump through from bottom and can stand on)
        20 -- angry enemy flower pot or parts of a cannon
        16 - brick (simple or with a hidden coin or with a hidden mushroom/flower)
        21 - question brick (with a coin or mushroom/flower)
*/
    public boolean isBreakable(byte a_type)
    {
        if(a_type == 16)
            return true;
        return false;
    }
    
    public boolean isPushable(byte a_type)
    {
        if(a_type == 21)
            return true;
        return false;
    }

    public boolean isItem(byte a_type)
    {
        if(a_type == Sprite.KIND_MUSHROOM || a_type == Sprite.KIND_FIRE_FLOWER || a_type == Sprite.KIND_COIN_ANIM) 
            return true;
        return false;
    }

    public boolean isClimbable(byte a_type)
    {
        if(a_type == -11)
            return true;
        return false;
    }
    
    public boolean isObstacle(int a_xmin, int a_xmax, int a_ymin, int a_ymax)
    {
        if(a_xmin < 0) a_xmin = 0;
        if(a_ymin < 0) a_ymin = 0;
        if(a_xmax >= levelScene.length) a_xmax = levelScene.length-1;
        if(a_ymax >= levelScene[0].length) a_ymax = levelScene[0].length-1;
        
        for(int i = a_xmin; i <= a_xmax; ++i)
        {
            for(int j = a_ymin; j <= a_ymax; ++j)
            {
                boolean obsFound = isObstacle(levelScene[i][j]);
                if(obsFound) return true;
            }    
        }
    
        return false;
    }
    
    
    public boolean isClimbable(int a_xmin, int a_xmax, int a_ymin, int a_ymax)
    {
        if(a_xmin < 0) a_xmin = 0;
        if(a_ymin < 0) a_ymin = 0;
        if(a_xmax >= levelScene.length) a_xmax = levelScene.length-1;
        if(a_ymax >= levelScene[0].length) a_ymax = levelScene[0].length-1;
        
        for(int i = a_xmin; i <= a_xmax; ++i)
        {
            for(int j = a_ymin; j <= a_ymax; ++j)
            {
                boolean obsFound = isClimbable(levelScene[i][j]);
                if(obsFound) return true;
            }    
        }
    
        return false;
    }
    
    public boolean isPushable(int a_xmin, int a_xmax, int a_ymin, int a_ymax)
    {
        if(a_xmin < 0) a_xmin = 0;
        if(a_ymin < 0) a_ymin = 0;
        if(a_xmax >= levelScene.length) a_xmax = levelScene.length-1;
        if(a_ymax >= levelScene[0].length) a_ymax = levelScene[0].length-1;
        
        for(int i = a_xmin; i <= a_xmax; ++i)
        {
            for(int j = a_ymin; j <= a_ymax; ++j)
            {
                boolean obsFound = isPushable(levelScene[i][j]);
                if(obsFound) return true;
            }    
        }
    
        return false;
    }
    
    public boolean isBreakable(int a_xmin, int a_xmax, int a_ymin, int a_ymax)
    {
        if(a_xmin < 0) a_xmin = 0;
        if(a_ymin < 0) a_ymin = 0;
        if(a_xmax >= levelScene.length) a_xmax = levelScene.length-1;
        if(a_ymax >= levelScene[0].length) a_ymax = levelScene[0].length-1;
        
        for(int i = a_xmin; i <= a_xmax; ++i)
        {
            for(int j = a_ymin; j <= a_ymax; ++j)
            {
                boolean obsFound = isBreakable(levelScene[i][j]);
                if(obsFound) return true;
            }    
        }
    
        return false;
    }

    public void reset()
    {
        //m_map.dumpToFile("levelDump.txt");

        m_map = new Map();
        m_MarioXInMap = 0;
        m_OldMarioXInMap = 0;
        m_stuckCounter = 0;
        for (int i = 0; i < action.length; ++i)
            action[i] = false;
        //action[Mario.KEY_RIGHT] = true;
       // action[Mario.KEY_SPEED] = true;
    }

    public boolean isMarioSmall()
    {
        return !(Mario.large || Mario.fire);
    }

    public boolean isMarioLarge()
    {
        return Mario.large;
    }    
    
    public boolean isMarioFire()
    {
        return Mario.fire;
    }
    
    public boolean isMarioStuck()
    {
        return m_stuckCounter >= CYCLES_TO_STUCK;
    }
    
    public String getName() {        return name;    }

    public void setName(String Name) { this.name = Name;    }

    public boolean[] getAction(Environment observation)
    {
        return null;
    }

    public void integrateObservation(int[] serializedLevelSceneObservationZ, int[] serializedEnemiesObservationZ, float[] marioFloatPos, float[] enemiesFloatPos, int[] marioState)
    {
    }
    
    public void giveIntermediateReward(float intermediateReward){}
    public float getMarioGravity() {return 1.0f;}

    public AGENT_TYPE getType() {return Agent.AGENT_TYPE.AI;}
}
