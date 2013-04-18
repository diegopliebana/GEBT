/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.GEBT_Mario;

import ch.idsia.mario.engine.sprites.Sprite;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Diego
 */
public class Map {

    public static final int LEVEL_HEIGHT = 15;
    public static final int MAP_BLOCK_LENGTH = 100;
    public int MAX_JUMP_HEIGHT = 4;
    public int MAX_JUMP_HEIGHT_PUSH = 6;


    /*** ENGINE 0.1.9 ***/
    //Defined by the engine.
    /*public static final int MAP_OBSTACLE = -127;
    public static final int MAP_SOFT_OBSTACLE = -62; //Those that Mario can jump from the bottom.
    public static final int MAP_SOFT_OBSTACLE2 = -76; //Strange, but this is another one too.
    public static final int MAP_SIMPLE_BRICK = -20;
    public static final int MAP_QUESTION_BRICK = -22;
    //public static final int MAP_POT_OR_CANNON = -85;
    public static final int MAP_COIN_ANIM = 1;

    public static final int MAP_BORDER_CANNOT_PASS_THROUGH = -60;
    public static final int MAP_CANNON_MUZZLE = -82;
    public static final int MAP_CANNON_TRUNK = -80;
    public static final int MAP_FLOWER_POT = -90;
    public static final int MAP_NOTHING = 0;*/


    /*** ENGINE 0.1.5 ***/
    public static final int MAP_OBSTACLE = -127;
    public static final int MAP_SOFT_OBSTACLE = -11;//-62; //Those that Mario can jump from the bottom.
    public static final int MAP_SOFT_OBSTACLE2 = -76; //Strange, but this is another one too.
    public static final int MAP_SIMPLE_BRICK = 16;
    public static final int MAP_QUESTION_BRICK = 21;
    //public static final int MAP_POT_OR_CANNON = -85;
    public static final int MAP_COIN_ANIM = 34; //CHANGED FOR 0.1.5

    public static final int MAP_BORDER_CANNOT_PASS_THROUGH = -60;
    public static final int MAP_CANNON_MUZZLE = -82;
    public static final int MAP_CANNON_TRUNK = -80;
    public static final int MAP_FLOWER_POT = -90;
    public static final int MAP_NOTHING = 0;
    
    public static boolean isObstacle(byte a_data)
    {
        return a_data != MAP_NOTHING &&   //nothing
               a_data != MAP_SOFT_OBSTACLE && //border hill, jumpable from the bottom.
               a_data != MAP_SIMPLE_BRICK  && //Breakable brick
               a_data != MAP_QUESTION_BRICK  && //Unbreakable brick
               //a_data != MAP_POT_OR_CANNON && //Por or cannon for 1 level zoom view
               //a_data != MAP_BORDER_CANNOT_PASS_THROUGH && //Not sure what is this, but obstacle
               a_data != MAP_CANNON_MUZZLE && //Muzzle of the cannon
               a_data != MAP_CANNON_TRUNK && //Trunk of the cannon
               a_data != MAP_FLOWER_POT &&  //Flower pot
               a_data != MAP_SOFT_OBSTACLE2 && //COIN!
               a_data != MAP_COIN_ANIM; //COIN!
    }

    public static boolean isPotOrCannon(byte a_data)
    {
        return
               //a_data == MAP_POT_OR_CANNON || //Por or cannon for 1 level zoom view
               a_data == MAP_CANNON_MUZZLE || //Muzzle of the cannon
               a_data == MAP_CANNON_TRUNK || //Trunk of the cannon
               a_data == MAP_FLOWER_POT; //Flower pot
    }
    
    //Defined by us.
    public static final int MAP_ONE_SPACE_FREE_FLAG = 101; //One space over a solid block
    public static final int MAP_TWO_SPACES_FREE_FLAG = 102; //Two spaces over a solid block


    private int m_lastCell; //Used just for dumping to file (write limit).
    private int m_currentXWritting;
    private int m_currentBlockIndex;
    private int m_currentXInBlockIndex;

    private Vector<MapBlock> m_levelMap;

    //Graph
    private Graph m_levelGraph;

    //data for coins and enemies:
    Hashtable<Integer, Vector<Integer>> m_coinCollection;
    Hashtable<Integer, Vector<Enemy>> m_enemyCollection;
    Hashtable<Integer, Vector<Item>> m_itemCollection;




    public Map()
    {
        m_levelMap = new Vector<MapBlock>();
        m_coinCollection = new Hashtable<Integer, Vector<Integer>>();
        m_enemyCollection = new Hashtable<Integer, Vector<Enemy>>();
        m_itemCollection = new Hashtable<Integer, Vector<Item>>();
        m_levelGraph = new Graph(this);
        m_lastCell = -1;
        m_currentXWritting = -1;
        m_currentBlockIndex = -1;
        m_currentXInBlockIndex = -1;
    }

    public static boolean isNothing(byte a_data)
    {
        return a_data == MAP_NOTHING || a_data == MAP_COIN_ANIM;
    }


    public Graph getGraph() { return m_levelGraph; }

    public void setCurrentWrittingX (int a_x)
    {
        m_currentXWritting = a_x;
        m_currentBlockIndex = (int)(m_currentXWritting / MAP_BLOCK_LENGTH);
        m_currentXInBlockIndex = (int)(m_currentXWritting % MAP_BLOCK_LENGTH);
    }
    

    public MapBlock getBlock(int a_blockNumber)
    {
        if(a_blockNumber >= m_levelMap.size())
        {
            //We need to create a new block
            m_levelMap.add(new MapBlock());
        }

        return m_levelMap.get(a_blockNumber);
    }


    private byte preprocessData(int a_x, int a_y, byte a_data)
    {
        if(Map.isObstacle(a_data)) //Gather all types of obstacles.
        {
           return Map.MAP_OBSTACLE;
        }

        /*if(Map.isPotOrCannon(a_data))
        {
            return Map.MAP_POT_OR_CANNON;
        }*/

        if(a_data == Map.MAP_SOFT_OBSTACLE2)
        {
            return Map.MAP_SOFT_OBSTACLE;
        }

        if(a_data == Map.MAP_COIN_ANIM)
        {
            if(!m_coinCollection.containsKey(a_x))
            {
                m_coinCollection.put(a_x, new Vector<Integer>());
            }
            m_coinCollection.get(a_x).add(a_y);
        }

        return a_data;
    }

    //X is defined by m_currentXWritting
    public void writeLevel(int a_y, byte a_data)
    {
        //Check boundaries
        if(m_currentXWritting == -1 || a_y < 0 || a_y >= LEVEL_HEIGHT)
            return;

        //Preprocess data:
        a_data = preprocessData(m_currentXWritting, a_y, a_data);

        getBlock(m_currentBlockIndex).write(m_currentXInBlockIndex,a_y,a_data);

    }

    public void flushEnemiesAndItems()
    {
        m_enemyCollection.clear();
        m_itemCollection.clear();
    }

    //X is defined by m_currentXWritting
    public void writeEnemy(int a_y, byte a_data)
    {
        //Check boundaries
        if(m_currentXWritting == -1 || a_y < 0 || a_y >= LEVEL_HEIGHT)
            return;

        if(a_data == Sprite.KIND_MUSHROOM || a_data == Sprite.KIND_FIRE_FLOWER)
        {
            if(!m_itemCollection.containsKey(m_currentXWritting))
            {
                m_itemCollection.put(m_currentXWritting, new Vector<Item>());
            }
            m_itemCollection.get(m_currentXWritting).add(new Item(m_currentXWritting, a_y, a_data));
        }
        else
        {
            if(!m_enemyCollection.containsKey(m_currentXWritting))
            {
                m_enemyCollection.put(m_currentXWritting, new Vector<Enemy>());
            }
            m_enemyCollection.get(m_currentXWritting).add(new Enemy(m_currentXWritting, a_y, a_data));
        }
    }

    public boolean checkForEnemyType(int a_xSrc, int a_xDest, int a_ySrc, int a_yDest, int a_type)
    {
        int min_x = Math.min(a_xSrc, a_xDest);
        int min_y = Math.min(a_ySrc, a_yDest);
        int max_x = Math.max(a_xSrc, a_xDest);
        int max_y = Math.max(a_ySrc, a_yDest);

        if(min_x < 0) min_x = 0;
        if(max_y >= 15) max_y = 14;
        if(min_y < 0) min_y = 0;


        for(int x = min_x; x <= max_x; ++x)
        {
            for(int y = min_y; y <= max_y; ++y)
            {
                if(m_enemyCollection.containsKey(x))
                {
                    Vector<Enemy> enemies = m_enemyCollection.get(x);
                    for(int i = 0; i < enemies.size(); ++i)
                    {
                        Enemy en = enemies.elementAt(i);
                        if(en.m_type == a_type && en.m_y == y)
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
        
    }


    public void updateLastCell()
    {
        //Update last cell written
        if(m_currentXWritting > m_lastCell)
            m_lastCell = m_currentXWritting;
    }

    public byte read(int a_x, int a_y)
    {
        //Check boundaries
        if(a_y < 0 || a_y >= LEVEL_HEIGHT)
            return -1;

        //Get the block for this position
        int blockIndex = (int)(a_x / MAP_BLOCK_LENGTH);
        int xInBlock = (int)(a_x % MAP_BLOCK_LENGTH);
        if(blockIndex < m_levelMap.size())
        {
            return getBlock(blockIndex).read(xInBlock,a_y);
        }
        
        return -1;
    }

    public void dumpToFile(String filename)
    {
        try
        {
            FileWriter fw = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(fw);

            for(int h = 14; h >= 0; --h)
            {
                for(int w = 0; w < m_lastCell; ++w)
                {
                    //Get the block for this position
                    int blockIndex = (int)(w / MAP_BLOCK_LENGTH);
                    int xInBlock = (int)(w % MAP_BLOCK_LENGTH);
                    getBlock(blockIndex).dumpToFile(bw,xInBlock,h);
                }

                //End of this line
                bw.newLine();
            }

            //And that's all!
            bw.close();

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void dumpToFileProcessing(String filename)
    {
        try
        {
            FileWriter fw = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("void setup() { size(" + m_lastCell*10 + ", 240); background(134,183,183); }");
            bw.newLine();
            bw.write("void draw() {");
            bw.newLine();

            for(int h = 14; h >= 0; --h)
            {
                for(int w = 0; w < m_lastCell; ++w)
                {
                    //Get the block for this position
                    int blockIndex = (int)(w / MAP_BLOCK_LENGTH);
                    int xInBlock = (int)(w % MAP_BLOCK_LENGTH);
                    //getBlock(blockIndex).dumpToFile(bw,xInBlock,h);
                    getBlock(blockIndex).dumpToFileProcessing(bw,xInBlock,h,w);
                }

                //End of this line
                bw.newLine();
            }

            //Print graph
            m_levelGraph.dumpProcessing(bw);

            bw.write("}");

            bw.newLine();

            //And that's all!
            bw.close();

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    //returns an integer: 0: no standing, 1 means small, 2 means big
    public static int CanMarioStand(int a_data)
    {
        if (a_data == Map.MAP_ONE_SPACE_FREE_FLAG) return 1;
        if (a_data == Map.MAP_TWO_SPACES_FREE_FLAG) return 2;
        return 0;
    }


    public static boolean isSomethingSolid(int a_data)
    {
        return (a_data == Map.MAP_OBSTACLE ||
                a_data == Map.MAP_SIMPLE_BRICK ||
                //a_data == Map.MAP_POT_OR_CANNON ||
                isPotOrCannon((byte)a_data) ||
                a_data == Map.MAP_QUESTION_BRICK);
    }

    public void processGraph(int a_xInit, int a_xEnd)
    {
        m_levelGraph = new Graph(this);

        //Some important checks!
        int xInit = (a_xInit <= 0)? 1 : a_xInit;
        int xEnd = (a_xEnd > m_lastCell)? m_lastCell : a_xEnd;

        if(xInit > xEnd) return;

        int currentBlockIndex = 0;
        int currentXInBlockIndex = 0;
        
        //For each horizontal line:
        for(int h = 0; h < 15; ++h)
        {
            int nodeAX = 0, nodeAY = 0;
            //Watch out: This does not count the free flags
            //0 means not found, 1 found and looking for the end of free flags row
            int freeflagOnThisRow = 0;

            //0: means no running, 1 means small, 2 means big
            int runningSize = 0;

            //Search for a FREE_FLAG:
            for(int cX = xInit; cX <= xEnd; ++cX)
            {
                //TODO? Watch out when breaking blocks: New links appear, old can be broken
                //EX: (27,3) -> (28,6) should dissappear. Not sure if it is worth to
                //implement this, maybe is better to have a complete new snapshot every time!

                currentBlockIndex = (int)(cX / MAP_BLOCK_LENGTH);
                currentXInBlockIndex = (int)(cX % MAP_BLOCK_LENGTH);

                byte dataInMap =  getBlock(currentBlockIndex)
                                    .read(currentXInBlockIndex,h);

                int thisStand = CanMarioStand(dataInMap);
                if(thisStand > 0)
                {
                    //Check verticals:
                    boolean stop = false;
                    int vert = h+1;
                    boolean overSoftFlag = false;
                    boolean youWontFall = false;
                    boolean tooHigh = false;
                    boolean tooHighPush = false;

                    //Watch out: DO not change the order (always up to down)
                    while(vert < 15 && !stop)
                    {

                        byte dataInVertical = getBlock(currentBlockIndex)
                                    .read(currentXInBlockIndex,vert);
                        byte dataInVerticalPlus1 = Map.MAP_NOTHING;
                        if(vert < 14)
                            dataInVerticalPlus1 = getBlock(currentBlockIndex)
                                    .read(currentXInBlockIndex,vert+1);

                        //First of all, if we are oversoft, add a link.
                        if(overSoftFlag)
                        {
                            //Add node and link! (These are vertical jumps)
                            int aId = m_levelGraph.addNode(cX, h);
                            int bId = m_levelGraph.addNode(cX, vert);
                            if(dataInVertical == Map.MAP_ONE_SPACE_FREE_FLAG)
                            {
                                m_levelGraph.addEdge(aId, bId, Graph.MODE_JUMP_SMALL);
                            }
                            else if(dataInVertical == Map.MAP_TWO_SPACES_FREE_FLAG)
                            {
                                m_levelGraph.addEdge(aId, bId, Graph.MODE_JUMP_BIG);
                            }

                            overSoftFlag = false;
                            stop = true;
                            continue;
                        }

                        if(Map.isSomethingSolid(dataInVertical))
                        {
                            stop = true;

                            //Add metadata to the node below, if applicable
                            if(!tooHighPush)
                            {
                                int aId = m_levelGraph.addNode(cX, h);
                                Node below = m_levelGraph.getNode(aId);
                                below.setMetadata(dataInVertical);
                            }

                            boolean reallyHigh = (vert+1 - h > MAX_JUMP_HEIGHT) || (vert+1>=15);

                            //Add breakable nodes?
                            if(!reallyHigh && dataInVertical == Map.MAP_SIMPLE_BRICK)
                            {
                                //################
                                //Check right and left.
                                int blockLeft = (int)((cX-1) / MAP_BLOCK_LENGTH);
                                int inBlockLeft = (int)((cX-1) % MAP_BLOCK_LENGTH);
                                int blockRight = (int)((cX+1) / MAP_BLOCK_LENGTH);
                                int inBlockRight = (int)((cX+1) % MAP_BLOCK_LENGTH);


                                byte dataOnLeft =  getBlock(blockLeft).read(inBlockLeft,vert+1);
                                if(CanMarioStand(dataOnLeft) > 0)
                                {
                                    //Add node and link!
                                    int aId = m_levelGraph.addNode(cX, h);
                                    int bId = m_levelGraph.addNode(cX-1, vert+1);

                                    int mode = Graph.MODE_BREAKABLE;
                                    m_levelGraph.addBreakableEdge(aId, bId, mode);
                                }

                                byte dataOnRight = getBlock(blockRight).read(inBlockRight,vert+1);
                                if(CanMarioStand(dataOnRight) > 0)
                                {
                                    //Add node and link!
                                    int aId = m_levelGraph.addNode(cX, h);
                                    int bId = m_levelGraph.addNode(cX+1, vert+1);
                                    int mode = Graph.MODE_BREAKABLE;
                                    m_levelGraph.addBreakableEdge(aId, bId, mode);
                                }
                                //################
                            }

                        }
                        else
                        {
                            //Check special case: soft obstacles can be jump from under them.
                            if (dataInVertical == Map.MAP_SOFT_OBSTACLE)
                            {
                                //We'll add the link in the next cycle (to avoid out of bounds).
                                overSoftFlag = true;
                                youWontFall = true;
                            }
                            
                            //Check right and left.
                            int blockLeft = (int)((cX-1) / MAP_BLOCK_LENGTH);
                            int inBlockLeft = (int)((cX-1) % MAP_BLOCK_LENGTH);
                            int blockRight = (int)((cX+1) / MAP_BLOCK_LENGTH);
                            int inBlockRight = (int)((cX+1) % MAP_BLOCK_LENGTH);

                            byte dataOnLeft =  getBlock(blockLeft).read(inBlockLeft,vert);
                            if(CanMarioStand(dataOnLeft) > 0)
                            {
                                //Add node and link!
                                int aId = m_levelGraph.addNode(cX, h);
                                int bId = m_levelGraph.addNode(cX-1, vert);

                                int mode = -1;
                                if(tooHigh && !youWontFall)
                                {
                                    if(dataOnLeft == MAP_ONE_SPACE_FREE_FLAG)  mode = Graph.MODE_FALL_SMALL;
                                    else if(dataOnLeft == MAP_TWO_SPACES_FREE_FLAG)
                                    {
                                        if(isSomethingSolid(dataInVerticalPlus1))
                                        {
                                            mode = Graph.MODE_FALL_SMALL;
                                        }else{
                                            mode = Graph.MODE_FALL_BIG;
                                        }
                                    }
                                }
                                else if(!tooHigh)
                                {
                                    if(dataOnLeft == MAP_ONE_SPACE_FREE_FLAG)  mode = Graph.MODE_JUMP_SMALL;
                                    else if(dataOnLeft == MAP_TWO_SPACES_FREE_FLAG)
                                    {
                                        if(isSomethingSolid(dataInVerticalPlus1))
                                        {
                                            mode = Graph.MODE_JUMP_SMALL;
                                        }else{
                                            mode = Graph.MODE_JUMP_BIG;
                                        }
                                    }
                                    
                                }

                                if(mode != -1)
                                {
                                    m_levelGraph.addEdge(aId, bId, mode);
                                }

                                //Check what is under this position:
                                byte dataOnLeftUnder = getBlock(blockLeft).read(inBlockLeft,vert-1);
                                if(isPotOrCannon(dataOnLeftUnder))
                                //if(dataOnLeftUnder == Map.MAP_POT_OR_CANNON)
                                {
                                    m_levelGraph.getNode(bId).setMetadata(dataOnLeftUnder);
                                }

                            }

                            byte dataOnRight = getBlock(blockRight).read(inBlockRight,vert);
                            if(CanMarioStand(dataOnRight) > 0)
                            {
                                //Add node and link!
                                int aId = m_levelGraph.addNode(cX, h);
                                int bId = m_levelGraph.addNode(cX+1, vert);

                                int mode = -1;
                                if(tooHigh && !youWontFall)
                                {
                                    if(dataOnRight == MAP_ONE_SPACE_FREE_FLAG)  mode = Graph.MODE_FALL_SMALL;
                                    else if(dataOnRight == MAP_TWO_SPACES_FREE_FLAG)
                                    {
                                        if(isSomethingSolid(dataInVerticalPlus1))
                                        {
                                            mode = Graph.MODE_FALL_SMALL;
                                        }else{
                                            mode = Graph.MODE_FALL_BIG;
                                        }
                                    }
                                }
                                else if(!tooHigh)
                                {
                                    if(dataOnRight == MAP_ONE_SPACE_FREE_FLAG)  mode = Graph.MODE_JUMP_SMALL;
                                    else if(dataOnRight == MAP_TWO_SPACES_FREE_FLAG)
                                    {
                                        if(isSomethingSolid(dataInVerticalPlus1))
                                        {
                                            mode = Graph.MODE_JUMP_SMALL;
                                        }else{
                                            mode = Graph.MODE_JUMP_BIG;
                                        }
                                    }
                                }

                                if(mode != -1)
                                {
                                    m_levelGraph.addEdge(aId, bId, mode);
                                }
                                
                                //Check what is under this position:
                                byte dataOnRightUnder = getBlock(blockRight).read(inBlockRight,vert-1);
                                if(isPotOrCannon(dataOnRightUnder))
                                //if(dataOnRightUnder == Map.MAP_POT_OR_CANNON)
                                {
                                    m_levelGraph.getNode(bId).setMetadata(dataOnRightUnder);
                                }
                            }

                        }//end - Not something solid!

                        vert++;

                        int jumpHeight = vert - h; //Check if we can reach higher points.
                        if(jumpHeight > MAX_JUMP_HEIGHT)
                            tooHigh = true; //Reached top height for jump, from now on we can only FALL.
                        if(jumpHeight > MAX_JUMP_HEIGHT_PUSH)
                            tooHighPush = true;


                    }//end - while checking vertical

                } //end dataInMap == MAP_TWO_SPACES_FREE_FLAG

                //Check horizontals:
                //if(freeflagOnThisRow == 0 && dataInMap == MAP_TWO_SPACES_FREE_FLAG )
                if(freeflagOnThisRow == 0 && thisStand>0 )
                {
                    //Free flag found!
                    //We have to keep going right until no free flag.
                    freeflagOnThisRow++;
                    nodeAX = cX; nodeAY = h;
                    runningSize = thisStand;
                }
                else if(freeflagOnThisRow == 1 && thisStand>0)
                {
                     //Add a new node.
                     int aId = m_levelGraph.addNode(nodeAX, nodeAY); //We were linking from here
                     int bId = m_levelGraph.addNode(cX, h);
                     if(thisStand == 1)
                        m_levelGraph.addEdge(aId, bId, Graph.MODE_WALK_SMALL);
                     else if(thisStand == 2)
                        m_levelGraph.addEdge(aId, bId, Graph.MODE_WALK_BIG);

                     //Link from here now.
                     nodeAX = cX; nodeAY = h;
                     //freeflagOnThisRow = 0;
                }
                else if(freeflagOnThisRow == 1 && thisStand==0)
                {
                    //End of the flag. Create nodes and edge.
                    freeflagOnThisRow = 0;
                    int nodeBX = cX-1;
                    int nodeBY = h;

                    //Lets check if source and dest are the same.
                    //It can happen: XXAXX or XXXA
                    if((nodeBX == nodeAX) && (nodeBY == nodeAY))
                    {
                        //no edge or nodes: Not to itself and the node will be created
                        //if some other node can reach this.
                        //continue;
                    }
                    else
                    {
                        int aId = m_levelGraph.addNode(nodeAX, nodeAY);
                        int bId = m_levelGraph.addNode(nodeBX, nodeBY); 
                        if(runningSize == 1)
                            m_levelGraph.addEdge(aId, bId, Graph.MODE_WALK_SMALL);
                        else if(runningSize == 2)
                            m_levelGraph.addEdge(aId, bId, Graph.MODE_WALK_BIG);

                    }

                    //Try some jumps
                    if(Map.isNothing(dataInMap))
                    {
                        //And here, we should check if a faith jump is possible!
                        graphCheckJumps(cX-1, nodeBY, runningSize, xEnd);
                    }

                    //Lets try to avoid annoying cannons (or bricks)
                    if( dataInMap == Map.MAP_CANNON_TRUNK
                        || dataInMap == Map.MAP_CANNON_MUZZLE
                        || dataInMap == Map.MAP_SIMPLE_BRICK
                        || dataInMap == Map.MAP_QUESTION_BRICK)
                    {
                        //is it a cannon?
                        int blockRight = (int)((cX+1) / MAP_BLOCK_LENGTH);
                        int inBlockRight = (int)((cX+1) % MAP_BLOCK_LENGTH);
                        byte dataOnRight =  getBlock(blockRight).read(inBlockRight,h);
                        boolean dataOk = dataOnRight != Map.MAP_CANNON_TRUNK &&
                                dataOnRight != Map.MAP_CANNON_MUZZLE &&
                                dataOnRight != Map.MAP_SIMPLE_BRICK &&
                                dataOnRight != Map.MAP_QUESTION_BRICK;
                        if(dataOk && h+2<15)
                        {
                            //For our understanding, this is a cannon.
                            //Let's check the height of the cannon
                            //byte dataOverThere1 =  getBlock(currentBlockIndex).read(currentXInBlockIndex,h+1);
                             byte dataOverThere2 =  getBlock(currentBlockIndex)
                                    .read(currentXInBlockIndex,h+2);

                             if(Map.isNothing(dataOverThere2))
                             {
                                int aId = m_levelGraph.addNode(cX-1, h);
                                int bId = m_levelGraph.addNode(cX+1,h);

                                int distanceFaith = 2;
                                if(runningSize == 1 || dataOnRight == Map.MAP_ONE_SPACE_FREE_FLAG)
                                {
                                    int mode = Graph.MODE_FAITH_JUMP + distanceFaith;
                                    m_levelGraph.addEdge(aId, bId, mode);
                                }
                                else if(runningSize == 2)
                                {
                                    int mode = (Graph.MODE_FAITH_JUMP * 2) + distanceFaith;
                                    m_levelGraph.addEdge(aId, bId, mode);
                                }

                             }

                        }
                    }

                    
                }// end - check horizontals

            }// end for - x

        }//end for - h

        //graphCheckJumps(a_xInit, a_xEnd);
    }

    public static int MAX_DIST_JUMP = 5;
    public void graphCheckJumps(int a_xInit, int a_height, int a_runSize, int a_xEnd)
    {
        //Some important checks!
        int xInit = (a_xInit < 0)? 0 : a_xInit;
        int xEnd = (a_xEnd > m_lastCell)? m_lastCell : a_xEnd;

        if(xInit > xEnd) return;

        int maxHorJump = 6;
        for (int i = 0; i < maxHorJump; ++i)
        {
            //Checking the level data in the positions marked with X.
            //  X
            //  XX
            //  XXX
            //  XXXX
            //  XXXXX
            //I XXXXXX (I=Init point).
            //  XXXXX
            //  XXXX
            //  XXX
            //  XX
            //  X

            int yMin = a_height - ((maxHorJump-1)-i);
            if (yMin < 0) yMin = 0; //Check bounds.
            int yMax = a_height + ((maxHorJump-1)-i);
            if (yMax > 14) yMax = 14; //Check bounds.
            int x = xInit+i+2;

            if(x >= m_lastCell) x=m_lastCell-1;

            for(int j = yMin; j < yMax; ++j)
            {
                int currentBlockIndex = (int)(x / MAP_BLOCK_LENGTH);
                int currentXInBlockIndex = (int)(x % MAP_BLOCK_LENGTH);

                byte dataInMap =  getBlock(currentBlockIndex)
                                    .read(currentXInBlockIndex,j);

                int thisStand = CanMarioStand(dataInMap);
                if(thisStand > 0 && thisStand >= a_runSize)
                {
                    //Check if I can jump
                    boolean jumpAvailable = checkJump(a_xInit,a_height,x,j);
                    if(jumpAvailable)
                    {
                        //Check hole under jump
                        boolean hole = checkHoleUnderJump(a_xInit, a_height,x, j);

                        //Add node
                        int aId = m_levelGraph.addNode(a_xInit, a_height);
                        int bId = m_levelGraph.addNode(x, j);
                        int size = thisStand < a_runSize ? thisStand : a_runSize;

                        int distanceFaith = Math.abs(a_xInit - x);
                        int mode = (Graph.MODE_FAITH_JUMP * size) + distanceFaith;
                        m_levelGraph.addEdge(aId, bId, mode);

                        if(hole)
                        {
                            Edge edg = m_levelGraph.getEdge(aId, bId);
                            if(edg != null) edg.setMetadata(Edge.META_HOLE);

                            edg = m_levelGraph.getEdge(bId, aId);
                            if(edg != null) edg.setMetadata(Edge.META_HOLE);
                        }
                    }
                }
            }


        }

    }

    //Enemies, coins and items.
    public int checkVolatileUp(int a_x, int a_y, Vector<Enemy> a_enemies, Vector<Item> a_items)
    {
        int numCoins = 0;
        int i = a_y;
        boolean end = false;

        int currentBlockIndex = (int)(a_x / MAP_BLOCK_LENGTH);
        int currentXInBlockIndex = (int)(a_x % MAP_BLOCK_LENGTH);

        while(!end && i < 15)
        {
            int jumpHeight = i - a_y;
            byte dataInMap = getBlock(currentBlockIndex)
                               .read(currentXInBlockIndex,i);
            if(Map.isSomethingSolid(dataInMap) || dataInMap == Map.MAP_SOFT_OBSTACLE)
            {
                //Something solid up. I cant reach higher, end of analysis.
                end = true;
            }
            else
            {
                //Check for coins
                if ((m_coinCollection.get(a_x) != null) && (m_coinCollection.get(a_x).contains(i)))
                {
                    //A coin! :D
                    if(jumpHeight <= MAX_JUMP_HEIGHT_PUSH)
                    {
                        numCoins++;
                    }
                }
            
                //Check for enemies
                Enemy here = thereIsEnemy(m_enemyCollection.get(a_x), i);
                if ( here != null )
                {
                    //An enemy! :S
                    a_enemies.add(here);
                }

                Item hereIt = thereIsItem(m_itemCollection.get(a_x), i);
                if ( hereIt != null )
                {
                    //An item! :D
                    a_items.add(hereIt);
                }

                ++i;
            }
        }

        return numCoins;
    }

    private Enemy thereIsEnemy(Vector<Enemy> a_enemies, int a_y)
    {
        if(a_enemies == null || a_enemies.isEmpty())
        {
            return null;
        }

        for(int i = 0; i < a_enemies.size(); ++i)
        {
            if(a_enemies.get(i).m_y == a_y)
            {
                return a_enemies.get(i);
            }
        }

        return null;
    }

    
    private Item thereIsItem(Vector<Item> a_items, int a_y)
    {
        if(a_items == null || a_items.isEmpty())
        {
            return null;
        }

        for(int i = 0; i < a_items.size(); ++i)
        {
            if(a_items.get(i).m_y == a_y)
            {
                return a_items.get(i);
            }
        }

        return null;
    }


    public int checkHoleOnSides(int a_x, int a_y)
    {
        //Check right
        boolean onRight = false;
        if((m_lastCell > a_x+1) && !checkObstacle(a_x+1, 0, a_x+1, a_y))
        {
            onRight = true;
        }

        //Check left
        boolean onLeft = false;
        if(!checkObstacle(a_x-1, 0, a_x-1, a_y))
        {
            onLeft = true;
        }

        //RETURN SOMETHING
        if(onRight && onLeft)
            return Node.MAP_HOLE_ON_BOTH;
        else if(onRight)
            return Node.MAP_HOLE_ON_RIGHT;
        else if(onLeft)
            return Node.MAP_HOLE_ON_LEFT;
        else
            return MAP_OBSTACLE;
    }

    private boolean checkHoleUnderJump(int a_xSrc, int a_ySrc, int a_xDest, int a_yDest)
    {
        //Min height:
        int minHeight = Math.min(a_ySrc, a_yDest);
        int minX = Math.min(a_xSrc, a_xDest) + 1;
        int maxX = Math.max(a_xSrc, a_xDest) - 1;
      
        for(int i = minX; i <= maxX; ++i)
        {
            boolean obstacleInThisX = checkObstacle(i, 0, i, minHeight);
            if(!obstacleInThisX)
                return true;
        }

        return false;
    }

    //aX must be lower than bX, otherwise this function doesn't work.
    private boolean checkJump(int a_ax, int a_ay ,int a_bx, int a_by)
    {
        boolean obstacle = false;
        int heightCheck = 3; //at least this must be 2. Greater if more restristive for jumps
        // (it would check for higher obstacles than the dest point).

       /* if(a_ax == 69 && a_ay == 11 && a_bx == 71 && a_by == 10)
        {
            int a  = 0;
        } */

        if(Math.abs(a_ax - a_bx) < 4)
            heightCheck = 2;

        //Same height
        if(a_ay == a_by)
        {
            obstacle = checkObstacle(a_ax, a_by+1, a_bx,  a_by+heightCheck);
        }
        else if(a_ay < a_by) //jump to a higher place.
        {
            obstacle = checkObstacle(a_ax, a_by+1, a_bx,  a_by+heightCheck);
            if(!obstacle) obstacle = checkObstacle(a_ax, a_ay+1, a_bx-1, a_by);
        }
        else  //jump to a lower place.
        {
            obstacle = checkObstacle(a_ax, a_ay+1, a_bx, a_ay+heightCheck /*a_ay+1+heightCheck*/);
            if(!obstacle)
                obstacle = checkObstacle(a_ax+2, a_ay, a_bx, a_by+1);
        }

        return !obstacle;
    }

    public boolean checkObstacle(int a_xSrc,int a_ySrc, int a_xDest, int a_yDest)
    {
        int min_x = Math.min(a_xSrc, a_xDest);
        int min_y = Math.min(a_ySrc, a_yDest);
        int max_x = Math.max(a_xSrc, a_xDest);
        int max_y = Math.max(a_ySrc, a_yDest);

        if(min_x < 0) min_x = 0;
        if(max_y >= 15) max_y = 14;
        if(min_y < 0) min_y = 0;

        for(int x = min_x; x <= max_x; ++x)
        {
            for(int y = min_y; y <= max_y; ++y)
            {
                int currentBlockIndex = (int)(x / MAP_BLOCK_LENGTH);
                int currentXInBlockIndex = (int)(x % MAP_BLOCK_LENGTH);
                byte dataInMap =  getBlock(currentBlockIndex)
                                    .read(currentXInBlockIndex,y);

                if(isSomethingSolid(dataInMap))
                    return true;

            }
        }
        return false;
    }

    public boolean checkSolid(int a_xSrc,int a_ySrc, int a_xDest, int a_yDest)
    {
        int min_x = Math.min(a_xSrc, a_xDest);
        int min_y = Math.min(a_ySrc, a_yDest);
        int max_x = Math.max(a_xSrc, a_xDest);
        int max_y = Math.max(a_ySrc, a_yDest);

        if(min_x < 0) min_x = 0;
        if(max_y >= 15) max_y = 14;
        if(min_y < 0) min_y = 0;


        for(int x = min_x; x <= max_x; ++x)
        {
            for(int y = min_y; y <= max_y; ++y)
            {
                int currentBlockIndex = (int)(x / MAP_BLOCK_LENGTH);
                int currentXInBlockIndex = (int)(x % MAP_BLOCK_LENGTH);
                byte dataInMap =  getBlock(currentBlockIndex)
                                    .read(currentXInBlockIndex,y);

                if(!isSomethingSolid(dataInMap))
                    return false;

            }
        }
        return true;
    }


}
