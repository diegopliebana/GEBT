/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.GEBT_Mario;

import ch.idsia.mario.engine.sprites.Sprite;
import java.util.Vector;

/**
 * Node for a Graph.
 * @author Diego
 */
public class Node {

    private int m_id;
    private int m_x;
    private int m_y;
    private Vector<Long> m_edges;
    private Vector<Enemy> m_enemies;
    private Vector<Item> m_items;
    private int m_metadata;
    private int m_holes;
    private int m_coins;

    public static final int MAP_HOLE_ON_LEFT = -1000;
    public static final int MAP_HOLE_ON_RIGHT = -1001;
    public static final int MAP_HOLE_ON_BOTH = -1002;
    
    public Node()
    {
        m_id = -1;
        m_x = -1;
        m_y = -1;
        m_coins = 0;
        m_holes = 0;
        m_edges = new Vector<Long>();
        m_enemies = new Vector<Enemy>();
        m_items = new Vector<Item>();
        m_metadata = Map.MAP_NOTHING;
    }

    public Node(int a_id, int a_x, int a_y)
    {
        m_id = a_id;
        m_x = a_x;
        m_y = a_y;
        m_coins = 0;
        m_holes = 0;
        m_edges = new Vector<Long>();
        m_enemies = new Vector<Enemy>();
        m_items = new Vector<Item>();
        m_metadata = Map.MAP_NOTHING;
    }

    public void addEdge(long a_id) {m_edges.add(a_id);}
    public int getID() {return m_id;}
    public int getX() {return m_x;}
    public int getY() {return m_y;}
    public Vector<Long> getEdgesFromNode() {return m_edges;}

    public void setMetadata(int a_meta) {m_metadata = a_meta;}
    public int getMetadata() { return m_metadata; }

    public void setHoles(int a_h) {m_holes = a_h;}
    public int getHoles() {return m_holes;}

    public void setCoins(int a_c) {m_coins = a_c;}
    public int getCoins() {return m_coins;}

    public void setEnemies(Vector<Enemy> a_enemies) {m_enemies = a_enemies;}
    public Vector<Enemy> getEnemies() {return m_enemies;}

    public void setItems(Vector<Item> a_items) {m_items = a_items;}
    public Vector<Item> getItems() {return m_items;}


    private final float COIN_WEIGHT = 0f;
    private final float ITEM_WEIGHT_SMALL = -10f;
    private final float ITEM_WEIGHT_BIG = -15f;
    private final float ITEM_WEIGHT_FIRE = -20f;
    private final float ENEMY_WEIGHT_STOMP_SLOW = 8;
    private final float ENEMY_WEIGHT_STOMP_FAST = 16;
    private final float ENEMY_WEIGHT_WINGED = 24;
    private final float ENEMY_WEIGHT_SPIKY = 32;
     
    //Cost dependant on coins, enemies, items.
    public int internalCost(GEBT_MarioAgent a_agent)
    {
        //Coin factor.
        float coinWeight = COIN_WEIGHT;
        int costCoins = (int) (m_coins*coinWeight);

        //Item factor.
        float itemWeight = ITEM_WEIGHT_SMALL; //Small Mario: risk to die!
        if(a_agent.isMarioLarge())
            itemWeight = ITEM_WEIGHT_BIG; //Possibility to fire but risk to be small
        if(a_agent.isMarioFire())
            itemWeight = ITEM_WEIGHT_FIRE; //Possibility to keep fire if damaged in the way
        int costItems = (int)(itemWeight*m_items.size());

        //Enemy factor
        float costEnemies = 0;
        int numEnemies = m_enemies.size();
        for(int i = 0; i < numEnemies; ++i)
        {
            Enemy en = m_enemies.elementAt(i);
            float thisCost = getEnemyWeight(en.m_type);
            costEnemies += thisCost;
        }

        int totalCost = /*costCoins + costItems +*/ (int)costEnemies;

        return totalCost;
    }


    public int manhattanDistanceTo(Node a_other)
    {
        int xDiff = Math.abs(m_x-a_other.getX());
        int yDiff = Math.abs(m_y-a_other.getY());
        return xDiff + yDiff;
    }

    public int manhattanDistanceTo(int a_x, int a_y)
    {
        int xDiff = Math.abs(m_x-a_x);
        int yDiff = Math.abs(m_y-a_y);
        return xDiff + yDiff;
    }

    private float getEnemyWeight(byte a_type)
    {
        switch(a_type)
        {
            case (Sprite.KIND_GOOMBA):
            case (Sprite.KIND_GREEN_KOOPA):
            case (Sprite.KIND_RED_KOOPA):
                return ENEMY_WEIGHT_STOMP_SLOW;
            case (Sprite.KIND_SHELL):
            case (Sprite.KIND_BULLET_BILL):
                return ENEMY_WEIGHT_STOMP_FAST;
            case (Sprite.KIND_RED_KOOPA_WINGED):
            case (Sprite.KIND_GOOMBA_WINGED):
            case (Sprite.KIND_GREEN_KOOPA_WINGED):
                return  ENEMY_WEIGHT_WINGED;
            case (Sprite.KIND_SPIKY):
            case (Sprite.KIND_ENEMY_FLOWER):
            case (Sprite.KIND_SPIKY_WINGED):
                return ENEMY_WEIGHT_SPIKY;
        }
        return 1;
    }

    public boolean isFlowerOut(Graph a_graph)
    {
        //First, see this node.
        for(int i = 0; i < m_enemies.size(); ++i)
        {
            if(m_enemies.get(i).m_type == Sprite.KIND_ENEMY_FLOWER)
            {
                return true;
            }
        }

        //If not, I have neighbors IN THE SAME POT that could have the flower.
        int numN = m_edges.size();
        for(int i = 0; i < numN; ++i)
        {
            Edge edg = a_graph.getEdge(m_edges.get(i));
            if(edg != null && 
               (edg.getMode() == Graph.MODE_WALK_BIG || edg.getMode() == Graph.MODE_WALK_SMALL))
            {
                int nodeId = edg.getA() != m_id ? edg.getA() : edg.getB();
                Node n = a_graph.getNode(nodeId);

                Vector<Enemy> destEnemies = n.getEnemies();
                for(int j = 0; j < destEnemies.size(); ++j)
                {
                    if(destEnemies.get(j).m_type == Sprite.KIND_ENEMY_FLOWER)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
