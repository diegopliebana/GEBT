/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.GEBT_Mario;

import grammaticalbehaviors.GEBT_Mario.Path;
import java.io.BufferedWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 *
 * @author Diego
 */
public class Graph {

    // Modes for crossing the edges.
    public static final int MODE_UNDEF = -1;
    public static final int MODE_WALK_BIG = 0;
    public static final int MODE_WALK_SMALL = 1;
    public static final int MODE_JUMP_BIG = 2;
    public static final int MODE_JUMP_SMALL = 3;
    public static final int MODE_FALL_BIG = 4;
    public static final int MODE_FALL_SMALL = 5;
    public static final int MODE_BREAKABLE = 6; //Small mario cannot break blocks... should we add 2 as well?
    
    //FAITH Jumps from 100 on! 
    // THE VALUE WILL BE (100*size)+Distance, so 100+ for small mario, 200+ for big mario.
    public static final int MODE_FAITH_JUMP = 100;

    //Edges of the graph
    private HashMap<Long, Edge> m_edges;

    //Nodes of the graph.
    private HashMap<Integer, Node> m_nodes;


    //Map of shortest path from node i (index of map) to the others
    private HashMap<Integer, HashMap<Integer, Path>> m_shortestPaths;

    //Edge counter
    private int m_nodeCounter;

    //Edge counter
    private int m_edgeCounter;

    //Metadata for the edge
    private int m_metadata;

    private Map m_myMap;

    public Graph(Map a_map)
    {
        m_edges = new HashMap<Long, Edge>();
        m_nodes = new HashMap<Integer, Node>();
        m_nodeCounter = 0;
        m_edgeCounter = 0;
        m_shortestPaths = new HashMap<Integer, HashMap<Integer, Path>>();
        m_myMap = a_map;
    }

    private boolean checkSolidUnderHighest(int a_aX, int a_aY, int a_bX, int a_bY)
    {
        if(a_aY > a_bY)
        {
            return m_myMap.checkSolid(a_aX, a_bY, a_aX, a_aY-1);
        }else if(a_aY < a_bY)
        {
            return m_myMap.checkSolid(a_bX, a_aY, a_bX, a_bY-1);
        }
        return false;
    }

    //Map needs to be read before (it should).
    private void postProcessNode(Node a_node, int a_x, int a_y)
    {
        //1. Holes!
        int holes = m_myMap.checkHoleOnSides(a_x, a_y);
        if(holes != Map.MAP_OBSTACLE)
        {
            a_node.setHoles(holes);
        }

        //2. Coins, items and enemies!
        Vector<Enemy> enemies = new Vector<Enemy>();
        Vector<Item> items = new Vector<Item>();
        int numCoins = m_myMap.checkVolatileUp(a_x, a_y, enemies, items);
        
        //2.a coins
        a_node.setCoins(numCoins);

        //2.b enemies
        if(enemies != null && !enemies.isEmpty())
        {
            a_node.setEnemies(enemies);
        }

        //2.c items
        if(items != null && !items.isEmpty())
        {
            a_node.setItems(items);
        }

    }


    public int addNode(int a_x, int a_y)
    {
        //calculate an unique ID depending on the coordinates:
        int id = 100000*(100+a_y) + (10000+a_x);
        Integer idKey = new Integer(id);

        if(!m_nodes.containsKey(idKey))
        {
            //Not registered, create node and insert it.
            Node newNode = new Node(id, a_x, a_y);
            m_nodes.put(idKey, newNode);

            postProcessNode(newNode, a_x, a_y);
            m_nodeCounter++;
        }
        
        return id;
    }

    //Checks if there is an existing node in the given coordinates.
    //If it does not exist, returns -1. Otherwise, return its ID.
    public int existsNode(int a_x, int a_y)
    {
    //calculate an unique ID depending on the coordinates:
        int id = 100000*(100+a_y) + (10000+a_x);
        Integer idKey = new Integer(id);

        if(!m_nodes.containsKey(idKey))
            return -1;
        else return id;
    }


    //Checks if there is an existing node with the given ID.
    //If it does not exist, returns -1. Otherwise, return its ID.
    public int existsNode(int a_id)
    {
        if(!m_nodes.containsKey(a_id))
            return -1;
        else return a_id;
    }

    public void addBreakableEdge(int a_aID, int a_bID, int a_mode)
    {
        Node nodeA = m_nodes.get(a_aID);
        Node nodeB = m_nodes.get(a_bID);

        if(nodeA != null && nodeB != null)
        {
            int aY = nodeA.getY();
            int aX = nodeA.getX();
            int bY = nodeB.getY();
            int bX = nodeB.getX();

            int distance = nodeA.manhattanDistanceTo(nodeB);
            Edge newEdge = null;
            Long idKey = new Long(0);

            //We need to see which one is the lower one
            if(nodeA.getY() < nodeB.getY())
            {
                //A under B

                //calculate an unique ID depending on the IDs of the nodes:
                long id = (100000000L * a_aID) + a_bID;
                idKey = new Long(id);

                if(!m_edges.containsKey(idKey))
                {
                    newEdge = new Edge(id, a_aID, a_bID, a_mode, distance);
                    nodeA.addEdge(id);
                    m_edges.put(idKey, newEdge);
                    m_edgeCounter++;
                }
            }
            else
            {
                //B under A

                //calculate an unique ID depending on the IDs of the nodes:
                long id = (100000000L * a_bID) + a_aID;
                idKey = new Long(id);

                if(!m_edges.containsKey(idKey))
                {
                    newEdge = new Edge(id, a_bID, a_aID, a_mode, distance);
                    nodeB.addEdge(id);
                    m_edges.put(idKey, newEdge);
                    m_edgeCounter++;
                }
            }
        }

    }

    public void addEdge(int a_aID, int a_bID, int a_mode)
    {
        //Special case: MODE_FALL Only goes DOWN.
        if(a_mode == Graph.MODE_FALL_BIG || a_mode == Graph.MODE_FALL_SMALL)
        {
            Node nodeA = m_nodes.get(a_aID);
            Node nodeB = m_nodes.get(a_bID);

            if(nodeA != null && nodeB != null)
            {
                int aY = nodeA.getY();
                int aX = nodeA.getX();
                int bY = nodeB.getY();
                int bX = nodeB.getX();

                int distance = nodeA.manhattanDistanceTo(nodeB);
                Edge newEdge = null;
                Long idKey = new Long(0);
                //We need to see which one is the upper one
                if(nodeA.getY() > nodeB.getY())
                {
                    //A over B

                    //calculate an unique ID depending on the IDs of the nodes:
                    long id = (100000000L * a_aID) + a_bID;
                    idKey = new Long(id);

                    if(!m_edges.containsKey(idKey))
                    {
                        newEdge = new Edge(id, a_aID, a_bID, a_mode, distance);
                        nodeA.addEdge(id);
                        m_edges.put(idKey, newEdge);
                        m_edgeCounter++;
                    }
                }
                else
                {
                    //B over A

                    //calculate an unique ID depending on the IDs of the nodes:
                    long id = (100000000L * a_bID) + a_aID;
                    idKey = new Long(id);

                    if(!m_edges.containsKey(idKey))
                    {
                        newEdge = new Edge(id, a_bID, a_aID, a_mode, distance);
                        nodeB.addEdge(id);
                        m_edges.put(idKey, newEdge);
                        m_edgeCounter++;
                    }
                }

            }

        }
        else //NOT MODE FALL
        {
            //calculate an unique ID depending on the IDs of the nodes:
            long id = (100000000L * a_aID) + a_bID;
            Long idKey = new Long(id);

            boolean isSolid = false;

            if(!m_edges.containsKey(idKey))
            {
                //Not registered, create node and insert it.
                //Get the nodes of the edge
                Node nodeA = m_nodes.get(a_aID);
                Node nodeB = m_nodes.get(a_bID);

                if(nodeA != null && nodeB != null)
                {
                    int aY = nodeA.getY();
                    int aX = nodeA.getX();
                    int bY = nodeB.getY();
                    int bX = nodeB.getX();

                    /*if(aX == 69 && aY == 11)
                    {
                        int a = 0;
                    }*/

                    int distance = nodeA.manhattanDistanceTo(nodeB);
                    int modeAB = a_mode;
                    int modeBA = a_mode;

                    if((a_mode == Graph.MODE_JUMP_BIG) || (a_mode == Graph.MODE_JUMP_SMALL))
                    {
                        int xDistance = Math.abs(nodeA.getX() - nodeB.getX());
                        isSolid = checkSolidUnderHighest(aX, aY, bX, bY);
                        if(xDistance == 1)
                        {
                            //One of them is fall?
                            if(nodeA.getY() > nodeB.getY() && a_mode == Graph.MODE_JUMP_BIG)
                                modeAB = Graph.MODE_FALL_BIG;
                            else if(nodeA.getY() > nodeB.getY() && a_mode == Graph.MODE_JUMP_SMALL)
                                modeAB = Graph.MODE_FALL_SMALL;
                            else if(nodeB.getY() > nodeA.getY() && a_mode == Graph.MODE_JUMP_BIG)
                                modeBA = Graph.MODE_FALL_BIG;
                            else if(nodeB.getY() > nodeA.getY() && a_mode == Graph.MODE_JUMP_SMALL)
                                modeBA = Graph.MODE_FALL_SMALL;
                        }
                        else if(xDistance == 0)
                        {
                            //Only add node Jump from bottom to top
                            if(nodeA.getY() < nodeB.getY())
                            {
                                //A LOWER THAN B: FROM A TO B
                                Edge newEdgeAB = new Edge(id, a_aID, a_bID, a_mode, distance);
                                m_edges.put(idKey, newEdgeAB);
                                nodeA.addEdge(id);
                            }
                            else
                            {
                                //B LOWER THAN A: FROM B TO A
                                long id2 = (100000000L * a_bID) + a_aID;
                                Long idKey2 = new Long(id2);
                                Edge newEdgeBA = new Edge(id2, a_bID, a_aID, a_mode, distance);
                                m_edges.put(idKey2, newEdgeBA);
                                nodeB.addEdge(id2);
                            }
                            m_edgeCounter++;
                            return;
                        }
                    }



                    //The normal direction
                    Edge newEdgeAB = new Edge(id, a_aID, a_bID, modeAB, distance);
                    m_edges.put(idKey, newEdgeAB);
                    nodeA.addEdge(id);
                    if(isSolid) 
                    {
                        newEdgeAB.setMetadata(Edge.META_SOLID);
                    }

                    //The other direction
                    long id2 = (100000000L * a_bID) + a_aID;
                    Long idKey2 = new Long(id2);
                    Edge newEdgeBA = new Edge(id2, a_bID, a_aID, modeBA, distance);
                    m_edges.put(idKey2, newEdgeBA);
                    nodeB.addEdge(id2);
                    if(isSolid)
                    {
                        newEdgeBA.setMetadata(Edge.META_SOLID);
                    }

                    // System.out.println(a_aID + " -> " + a_bID);
                    m_edgeCounter+=2;
                }
            }
        }

    }

    //Checks if there is an existing edge between two nodes.
    //If it does not exist, returns -1. Otherwise, return its ID.
    public long existsEdge(int a_aID, int a_bID)
    {
        //calculate an unique ID depending on the nodes:
        /*long id = (100000000L * a_aID) + a_bID;
        Long idKey = new Long(id);

        if(!m_edges.containsKey(idKey))
        {
            //Check the other way
            id = (100000000L * a_bID) + a_aID;
            idKey = new Long(id);
            if(!m_edges.containsKey(idKey))
                return -1;
            else return id;
        }else return id;*/
        long ab = existsEdgeDirectional(a_aID, a_bID);
        if(ab == -1) return existsEdgeDirectional(a_bID, a_aID);
        else return ab;
    }


    //Checks if there is an existing edge between two nodes.
    //If it does not exist, returns -1. Otherwise, return its ID.
    public long existsEdgeDirectional(int a_aID, int a_bID)
    {
        //calculate an unique ID depending on the nodes:
        long id = (100000000L * a_aID) + a_bID;
        Long idKey = new Long(id);

        if(!m_edges.containsKey(idKey))
        {
            return -1;
        }else return id;
    }

    //Gets and edge betwwen two nodes.
    public Edge getEdge(int a_aID, int a_bID)
    {
        //calculate an unique ID depending on the nodes:
        long id = (100000000L * a_aID) + a_bID;
        Long idKey = new Long(id);

        if(!m_edges.containsKey(idKey))
        {
            //No edge
            return null;
        }

        return m_edges.get(idKey);
    }

    //Gets and edge betwwen two nodes.
    public Edge getEdge(long a_ID)
    {
        if(!m_edges.containsKey(a_ID))
        {
            //No edge
            return null;
        }

        return m_edges.get(a_ID);
    }


    //Removes node and edges to and from it.
    public void removeNode(int a_x, int a_y)
    {
        //TO DO.
    }


    private int nodeGetX(int a_id)
    {
        return a_id % 10000;
    }


    private int nodeGetY(int a_id)
    {
        int chop = (int) (a_id * 0.00001);
        return chop % 100;
    }

    public Object[] getNodes()
    {
        return m_nodes.values().toArray();
    }

    public Object[] getEdges()
    {
        return m_edges.values().toArray();
    }


    public Node getNode(int a_id)
    {
        return m_nodes.get(a_id);
    }

    public int getNumNodes() {return m_nodes.size();}


    public int getRightMostNode(Vector<Integer> a_forbidden)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int maxX = 0;
        int maxXNode = -1;
        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];

            //1st, it is not forbidden.
            if(!a_forbidden.contains(n.getID()))
            {
                //2nd, check distance.
                if(n.getX() > maxX)
                {
                    maxX = n.getX();
                    maxXNode = n.getID();
                }
            }
        }

        return maxXNode;
    }

    public int getLeftMostNodeClose(int a_originNode, Vector<Integer> a_forbidden)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int minX = Integer.MAX_VALUE;
        int minXNode = -1;
        int minDistance = Integer.MAX_VALUE;
        Node orgNode = m_nodes.get(a_originNode);

        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];
            //1st, it is not forbidden.
            if(!a_forbidden.contains(n.getID()))
            {
                int distance = n.manhattanDistanceTo(orgNode);

                //2nd, check distance.
                if(n.getX() < minX)
                {
                    minX = n.getX();
                    minXNode = n.getID();
                    minDistance = distance;
                }
                else if(n.getX() == minX)
                {
                    if(distance < minDistance)
                    {
                        minX = n.getX();
                        minXNode = n.getID();
                        minDistance = distance;
                    }
                }
            }
        }

        return minXNode;
    }

    public int getRightMostNodeClose(int a_originNode, Vector<Integer> a_forbidden)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int maxX = 0;
        int maxXNode = -1;
        int minDistance = Integer.MAX_VALUE;
        Node orgNode = m_nodes.get(a_originNode);

        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];
            //1st, it is not forbidden.
            if(!a_forbidden.contains(n.getID()))
            {
                int distance = n.manhattanDistanceTo(orgNode);

                //2nd, check distance.
                if(n.getX() > maxX)
                {
                    maxX = n.getX();
                    maxXNode = n.getID();
                    minDistance = distance;
                }
                else if(n.getX() == maxX)
                {
                    if(distance < minDistance)
                    {
                        maxX = n.getX();
                        maxXNode = n.getID();
                        minDistance = distance;
                    }
                }


            }
        }

        return maxXNode;
    }

    public int getGroundMostNodeClose(int a_originNode, Vector<Integer> a_forbidden)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int minY = Integer.MAX_VALUE;
        int minYNode = -1;
        int minDistance = Integer.MAX_VALUE;
        Node orgNode = m_nodes.get(a_originNode);
        
        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];
            //1st, it is not forbidden.
            if(!a_forbidden.contains(n.getID()))
            {
                int distance = n.manhattanDistanceTo(orgNode);

                //2nd, check Y.
                if(n.getY() < minY)
                {
                    minY = n.getY();
                    minYNode = n.getID();
                    minDistance = distance;
                }
                else if(n.getY() == minY)
                {
                    if(distance < minDistance)
                    {
                        minY = n.getY();
                        minYNode = n.getID();
                        minDistance = distance;
                    }
                }


            }
        }

        return minYNode;
    }


    public int getTopMostNodeClose(int a_originNode, Vector<Integer> a_forbidden)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int maxY = 0;
        int maxYNode = -1;
        int minDistance = Integer.MAX_VALUE;
        Node orgNode = m_nodes.get(a_originNode);

        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];
            //1st, it is not forbidden.
            if(!a_forbidden.contains(n.getID()))
            {
                int distance = n.manhattanDistanceTo(orgNode);

                //2nd, check Y.
                if(n.getY() > maxY)
                {
                    maxY = n.getY();
                    maxYNode = n.getID();
                    minDistance = distance;
                }
                else if(n.getY() == maxY)
                {
                    if(distance < minDistance)
                    {
                        maxY = n.getY();
                        maxYNode = n.getID();
                        minDistance = distance;
                    }
                }


            }
        }

        return maxYNode;
    }


    public int getClosestPotCannonNode(int a_originNode, Vector<Integer> a_forbidden)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int closestNode = -1;
        int minDistance = Integer.MAX_VALUE;
        Node orgNode = m_nodes.get(a_originNode);

        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];
            //1st, the node is valid and it is not forbidden
            //if(n.getMetadata() == Map.MAP_POT_OR_CANNON && !a_forbidden.contains(n.getID()))
            if( Map.isPotOrCannon((byte)n.getMetadata()) && !a_forbidden.contains(n.getID()))
            {
                int distance = n.manhattanDistanceTo(orgNode);

                //2nd, check Y.
                if(distance < minDistance)
                {
                    closestNode = n.getID();
                    minDistance = distance;
                }


            }
        }

        return closestNode;
    }


    public int getClosestBrickNode(int a_originNode, Vector<Integer> a_forbidden)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int closestNode = -1;
        int minDistance = Integer.MAX_VALUE;
        Node orgNode = m_nodes.get(a_originNode);

        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];
            //1st, the node is valid and it is not forbidden
            if(n.getMetadata() == Map.MAP_SIMPLE_BRICK && !a_forbidden.contains(n.getID()))
            {
                int distance = n.manhattanDistanceTo(orgNode);

                //2nd, check Y.
                if(distance < minDistance)
                {
                    closestNode = n.getID();
                    minDistance = distance;
                }


            }
        }

        return closestNode;
    }

    public int getClosestItemNode(int a_originNode, Vector<Integer> a_forbidden)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int closestNode = -1;
        int minDistance = Integer.MAX_VALUE;
        Node orgNode = m_nodes.get(a_originNode);

        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];

            //1st, the node is valid and it is not forbidden
            if(n.getItems().size() > 0  && !a_forbidden.contains(n.getID()))
            {
                int distance = n.manhattanDistanceTo(orgNode);

                //2nd, check Y.
                if(distance < minDistance)
                {
                    closestNode = n.getID();
                    minDistance = distance;
                }


            }
        }

        return closestNode;
    }



    public int getClosestQuestionNode(int a_originNode, Vector<Integer> a_forbidden)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int closestNode = -1;
        int minDistance = Integer.MAX_VALUE;
        Node orgNode = m_nodes.get(a_originNode);

        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];
            //1st, the node is valid and it is not forbidden
            if(n.getMetadata() == Map.MAP_QUESTION_BRICK && !a_forbidden.contains(n.getID()))
            {
                int distance = n.manhattanDistanceTo(orgNode);

                //2nd, check Y.
                if(distance < minDistance)
                {
                    closestNode = n.getID();
                    minDistance = distance;
                }


            }
        }

        return closestNode;
    }

    public int getClosestNodeTo(int a_x, int a_y)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        int minDistance = Integer.MAX_VALUE;
        int closestNode = -1;
        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];
            int distance = n.manhattanDistanceTo(a_x, a_y);
            if(distance < minDistance)
            {
                closestNode = n.getID();
                minDistance = distance;
            }
        }

        return closestNode;
    }

    public int getClosestNodeToFloatPos(float a_x, float a_y)
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        float minDistance = Float.MAX_VALUE;
        int closestNode = -1;
        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];

            float nX = n.getX()*16 + 8; //The center value on the grid.
            float nY = (15 - n.getY())*16 + 8;

            float xDiff = Math.abs(nX-a_x);
            float yDiff = Math.abs(nY-a_y);
            float distance =  xDiff + yDiff;
            if(distance < minDistance)
            {
                closestNode = n.getID();
                minDistance = distance;
            }
        }

        return closestNode;
    }


    public void dumpProcessing(BufferedWriter a_bw) throws Exception
    {
        //nodes
        Object[] coll = m_nodes.values().toArray();
        for(int i = 0; i < m_nodeCounter; ++i)
        {
            Node n = (Node)coll[i];
            a_bw.write("ellipse(" + (n.getX())*10 + "," + (15-n.getY())*10 + ",4,4);");

            if( !Map.isNothing((byte) n.getMetadata()))
            {
                switch(n.getMetadata())
                {
                    case Map.MAP_QUESTION_BRICK:
                        a_bw.write("stroke(238,223,45);");
                        break;
                    case Map.MAP_SIMPLE_BRICK:
                        a_bw.write("stroke(185,122,87);");
                        break;
                    case Map.MAP_OBSTACLE:
                        a_bw.write("stroke(136,0,21);");
                        break;
                    case Map.MAP_FLOWER_POT:
                        a_bw.write("stroke(250,0,0);");
                        break;
                    case Map.MAP_CANNON_MUZZLE:
                    case Map.MAP_CANNON_TRUNK:
                        a_bw.write("stroke(0,0,0);");
                        break;
                }

                a_bw.write("ellipse(" + (n.getX())*10 + "," + (15-n.getY())*10 + ",6,6);");
                a_bw.write("stroke(0,0,0);");
            }

            a_bw.newLine();
        }

        //Edges
        coll = m_edges.values().toArray();
        int numEdges = m_edges.size();
        for(int i = 0;i < numEdges; ++i)
        {
            Edge e = (Edge)coll[i];
            int aX = nodeGetX(e.getA());
            int aY = nodeGetY(e.getA());
            int bX = nodeGetX(e.getB());
            int bY = nodeGetY(e.getB());

            String text = " (" + aX + "," + aY + " -> " + bX + "," + bY + ");";

            switch(e.getMode())
            {
                case MODE_WALK_BIG:
                    a_bw.write("strokeWeight(2);");
                    a_bw.write("stroke(0,0,0);");
                    break;
                case MODE_WALK_SMALL:
                    a_bw.write("strokeWeight(1);");
                    a_bw.write("stroke(0,0,0);");
                    break;
                case MODE_JUMP_BIG:
                    a_bw.write("strokeWeight(2);");
                    a_bw.write("stroke(255,0,0);");
                    break;
                case MODE_JUMP_SMALL:
                    a_bw.write("strokeWeight(1);");
                    a_bw.write("stroke(255,0,0);");
                    break;
                case MODE_FALL_BIG:
                    a_bw.write("strokeWeight(2);");
                    a_bw.write("stroke(0,255,0);");
                    break;
                case MODE_FALL_SMALL:
                    a_bw.write("strokeWeight(1);");
                    a_bw.write("stroke(0,255,0);");
                    break;
                case MODE_BREAKABLE:
                    a_bw.write("strokeWeight(2);");
                    a_bw.write("stroke(128,64,0);");
                    break;

                default:
                    
                    if (e.getMode() >= MODE_FAITH_JUMP*2)
                    {
                        a_bw.write("strokeWeight(2);");
                        a_bw.write("stroke(255,0,255);");

                    }
                    else if (e.getMode() >= MODE_FAITH_JUMP)
                    {
                        a_bw.write("strokeWeight(1);");
                        a_bw.write("stroke(255,0,255);");
                    }
                    else
                    {
                        //What is this?? No print, continue.
                        a_bw.newLine();
                        continue;
                    }
            }


            if (e.getMode() >= MODE_FAITH_JUMP)
            {
                a_bw.write("line(" + aX*10 + "," + (15-aY)*10 +
                "," +bX*10 + "," + (15-bY)*10 + "); //" + e.getMode() + text);
            }
            else
            {
                if(aX == bX)
                {
                    a_bw.write("line(" + aX*10 + "," + (15-aY)*10 +
                        "," +bX*10 + "," + (15-bY)*10 + "); //" + e.getMode() + text);
                }
                else
                if(aX < bX)
                {
                    if(aY < bY)
                    {
                        a_bw.write("line(" + aX*10 + "," + (15-aY)*10 +
                            "," +aX*10 + "," + (15-bY)*10 + ");");
                        a_bw.newLine();
                        a_bw.write("line(" + aX*10 + "," + (15-bY)*10 +
                            "," +bX*10 + "," + (15-bY)*10 + "); //" + e.getMode() + text);
                    }
                    else
                    {
                        a_bw.write("line(" + aX*10 + "," + (15-aY)*10 +
                            "," +bX*10 + "," + (15-aY)*10 + ");");
                        a_bw.newLine();
                        a_bw.write("line(" + bX*10 + "," + (15-aY)*10 +
                            "," +bX*10 + "," + (15-bY)*10 + "); //" + e.getMode() + text);
                    }
                }
                else if(aX > bX)
                {

                    if(aY < bY)
                    {
                        a_bw.write("line(" + aX*10 + "," + (15-aY)*10 +
                            "," +aX*10 + "," + (15-bY)*10 + ");");
                        a_bw.newLine();
                        a_bw.write("line(" + aX*10 + "," + (15-bY)*10 +
                            "," +bX*10 + "," + (15-bY)*10 + "); //" + e.getMode() + text);
                    }
                    else
                    {
                        a_bw.write("line(" + aX*10 + "," + (15-aY)*10 +
                            "," +bX*10 + "," + (15-aY)*10 + ");");
                        a_bw.newLine();
                        a_bw.write("line(" + bX*10 + "," + (15-aY)*10 +
                            "," +bX*10 + "," + (15-bY)*10 + "); //" + e.getMode() + text);
                    }
                }
            }

            a_bw.newLine();
        }

        a_bw.write("strokeWeight(1);");
        a_bw.write("stroke(0,0,0);");
    }


    public Path getPath(int a_origin, int a_destination, GEBT_MarioAgent a_agent)
    {
        Path p = new Path(a_origin, a_destination);
        Path empty = new Path(a_origin, a_origin);

        boolean pathFound = _a_star(p, a_agent, true);

        if(!pathFound)
	{
		p.m_points.clear();
	}else
	{
		p = getShortestPath(a_origin,a_destination);
	}

	if (p.m_points.size() > 0 && p.m_cost < Integer.MAX_VALUE)
		return p;
	else return empty;
    }

    private Path getShortestPath(int a_origin, int a_destination)
    {
        return m_shortestPaths.get(a_origin).get(a_destination);
    }

    private HashMap<Integer, Path> getShortestPaths(int a_origin){return m_shortestPaths.get(a_origin);}
    private HashMap<Integer, HashMap<Integer, Path>> getShortestPaths() {return m_shortestPaths;}


    //Sets path
    private void setShortestPath(Path a_p)
    {
        HashMap<Integer, Path> shortest = getShortestPaths(a_p.m_originID);
        shortest.put(a_p.m_destinationID, a_p);
    }

    private void assignCost(int a_originID, int a_destID, int a_cost)
    {
        //1st, get the origin:
        HashMap<Integer, Path> originPaths = m_shortestPaths.get(a_originID);
        if(originPaths == null)
        {
             originPaths = new HashMap<Integer, Path>();

             //There will be no destination, for sure, so lets assign it.
             Path newPath = new Path(a_originID, a_destID, a_cost);
             originPaths.put(a_destID, newPath);

             //To the array!
             m_shortestPaths.put(a_originID, originPaths);

             //And that's all
             return;
        }

        //2nd, destination
        Path pathToDest = originPaths.get(a_destID);
        if(pathToDest == null)
        {
            //no path, create it and insert.
            Path newPath = new Path(a_originID, a_destID, a_cost);
            originPaths.put(a_destID, newPath);
        
            //And that's all
            return;
        }

        //3rd, there is a path, no cost; assign.
        pathToDest.m_cost = a_cost;
    }

    private boolean canGoThrough(Edge a_edge, GEBT_MarioAgent a_agent)
    {
    
        if(a_agent.isMarioLarge())
        {
            //I am big, I only could go if the path is for big people.
            if(a_edge.getMode() == Graph.MODE_FALL_SMALL || a_edge.getMode() == Graph.MODE_JUMP_SMALL
              || a_edge.getMode() == Graph.MODE_WALK_SMALL)
            {
                return false;
            }
        }

        if(a_edge.getMode() == Graph.MODE_BREAKABLE)
        {
            //Breakable, but origin is over the destination, cant go.
            if(a_edge.getA() > a_edge.getB())
                return false;

            //I cant break them if I am small.
            if(!a_agent.isMarioLarge())
                return false;
        }

        return true;
    }

    //Init shortest path structure
    private void initShortestPaths(int a_originID, GEBT_MarioAgent a_agent, boolean a_volatile)
    {
        Object []nodes = getNodes();
	for(int j = 0; j < nodes.length; ++j)
	{
            Node jNode = (Node)nodes[j];
            int nodeID = jNode.getID();

            //Set path
            if(a_originID == nodeID)
            {
                //If this is the same, cost=0
                //m_shortestPaths.get(a_originID).get(nodeID).m_cost = 0;
                assignCost(a_originID, nodeID, 0);
            }
            else
            {
                long eConnID = existsEdgeDirectional(a_originID, nodeID);

                //We need to check that Mario can go through this edge (Big, Small?)
                Edge edg = m_edges.get(eConnID);

                if(eConnID == -1 || !canGoThrough(edg, a_agent))
                {
                    //Does not exist.
                    assignCost(a_originID, nodeID, Integer.MAX_VALUE);
                }
                else
                {
                    Node dest = m_nodes.get(nodeID);
                    assignCost(a_originID, nodeID, edg.getCost(dest, a_agent, a_volatile));
                }
            }
	}
    }



    boolean _a_star(Path a_path, GEBT_MarioAgent a_agent, boolean a_volatile)
    {
	Vector<Integer> marked = new Vector<Integer>();

        PriorityQueue<PathCH> pathsQueue = new PriorityQueue<PathCH>(100, new PathCHComparator());
        Object []nodes = getNodes();

	int numNodes = nodes.length;

	//Init paths
        for(int i = 0; i < numNodes; ++i)
	{
            Node n = (Node)nodes[i];
            initShortestPaths(n.getID(), a_agent, a_volatile);
	}

	int currentNodeID = a_path.m_originID;
        Node curNode = m_nodes.get(currentNodeID);

	while(marked.size() < numNodes)
	{
            Vector<Long> edgesIDs = curNode.getEdgesFromNode();
            int numEdgesFromCurrentNode = edgesIDs.size();

            for(int i = 0; i < numEdgesFromCurrentNode; ++i)
            {
                Edge edge = m_edges.get(edgesIDs.get(i));

                //Connected to... (take care not to take the same!)
                int connectedID = edge.getA() != currentNodeID ? edge.getA() : edge.getB();

                //Only check if this is not marked
                if( ! marked.contains(connectedID)  )
                {
                        // Cost from origin to 'connected' stored
                        Path D1 = getShortestPath(a_path.m_originID,connectedID);
                        // Cost from origin to current node stored
                        Path DA = getShortestPath(a_path.m_originID,currentNodeID);
                        // Cost from current to connected (edge cost)
                        //int dA1 = edge.getCost();
                        //BETTER THIS WAY, we are taking into account the mario size!
                       int dA1 = getShortestPath(currentNodeID,connectedID).m_cost;

                        //Path to this node
                        PathCH pc = new PathCH();
                        pc.p = D1;
                        pc.destID = connectedID;

                        //If this path is better, create a new path and update paths list
                        int sumCost = DA.m_cost + dA1;
                        if(sumCost < 0)  //DOnt panic, this is for overflows.
                            sumCost = Integer.MAX_VALUE;

                        //But take care, that only because of OVERFLOWS!
                        if(dA1 < 0 || DA.m_cost < 0)
                        {
                            sumCost = DA.m_cost + dA1;
                        }



                        if(D1.m_cost > sumCost)
                        {
                            //update cost
                            Path newD1 = new Path(DA);
                            newD1.m_destinationID = connectedID;
                            newD1.m_cost += dA1;
                            //update path
                            pc.p = newD1;
                            newD1.m_points.add(connectedID);
                            setShortestPath(newD1);
                            /*System.out.println("SETTING SHORTEST PATH FROM " + newD1.m_originID +
                                    " TO " + newD1.m_destinationID + " with COST: " +  newD1.m_cost );
                            
                            for(int ik = 0; ik < newD1.m_points.size(); ++ik)
                            {
                                System.out.print(" " + newD1.m_points.get(ik));
                            }
                            System.out.println();*/
                        }

                        //Set heuristic cost, used by priority queue to navigate more efficiently
                        pc.heuristicCost = pc.p.m_cost +
                                heuristic(connectedID, a_path.m_destinationID);

                        if(pc.heuristicCost < -1000 ) //Overflow!
                        {
                            pc.heuristicCost = Integer.MAX_VALUE;
                        }

                        //add this conexion to the queue
                        //System.out.println("Adding to path queue: " + pc.toString());
                        pathsQueue.add(pc);
                }
            }

            //Mark current node...
            if(!marked.contains(currentNodeID))
            {
                marked.add(currentNodeID);
            }


            //And select new node to follow
            if(pathsQueue.size() > 0)
            {
                do{
                    currentNodeID = pathsQueue.peek().destID;
                    curNode = m_nodes.get(currentNodeID); //Get the node too.
                    pathsQueue.poll();
                    if(currentNodeID == a_path.m_destinationID)
                        return true;
                }while(!(pathsQueue.size()==0) && currentNodeID == pathsQueue.peek().destID);
            }
            else
            {
                //There is no way to go!
                return false;
            }

        }

	return false;
    }

    private int heuristic(int a_or, int a_dest)
    {
        return m_nodes.get(a_or).manhattanDistanceTo(m_nodes.get(a_dest));
    }
}



class PathCHComparator implements Comparator<PathCH>
{
    public int compare(PathCH x, PathCH y)
    {
        // Assume neither string is null. Real code should
        // probably be more robust
        if (x.heuristicCost < y.heuristicCost)
        {
            return -1;
        }
        if (x.heuristicCost > y.heuristicCost)
        {
            return 1;
        }
        return 0;
    }
}

class PathCH
{
    public Path p;
    public int destID;
    public int heuristicCost;

    @Override
    public String toString() {return "[" + p.toString() + "] hC: " + heuristicCost; }
};
