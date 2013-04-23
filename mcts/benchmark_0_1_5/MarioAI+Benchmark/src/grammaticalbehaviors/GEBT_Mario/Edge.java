/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.GEBT_Mario;

/**
 * Edge for a graph.
 * @author Diego
 */
public class Edge {

    private long m_id;
    private int m_aId;
    private int m_bId;
    private int m_mode;
    private int m_distance;
    private int m_cost;
    private int m_metadata;

    public static final int META_NOTHING = 0;
    public static final int META_HOLE = 1;
    public static final int META_SOLID = 2;

    public Edge(){}
    public Edge(long a_id, int a_aId, int a_bId, int a_mode, int a_distance)
    {
        m_id = a_id;
        m_aId = a_aId;
        m_bId = a_bId;   
        m_mode = a_mode;
        m_distance = a_distance;
        m_metadata = 0;

        if(m_mode == Graph.MODE_JUMP_BIG ||
           m_mode == Graph.MODE_JUMP_SMALL ||
           m_mode >= Graph.MODE_FAITH_JUMP
           )
        {
            m_cost = (int) (a_distance * 1.5f); //2
        }else if(m_mode == Graph.MODE_BREAKABLE)
        {
            m_cost = a_distance * 3; //4
        }
        else
        {
            m_cost = a_distance;
        }
    }
    
    public long getID() {return m_id;}
    public int getMetadata() {return m_metadata;}
    public int getA() {return m_aId;}
    public int getB() {return m_bId;}
    public int getMode() {return m_mode;}
    public int getDistance() {return m_distance;}
    @Override
    public String toString() {return "EDGE[" + m_id + "], distance: " + m_distance + ", cost: " + m_cost;}

    public void setMetadata(int a_meta) 
    {
        m_metadata = a_meta;
    }


    public int getCost(Node a_dest, GEBT_MarioAgent a_agent, boolean a_volatile)
    {
        //Cost of the edge will be the normal cost of the edge PLUS
        // the internal cost of the destination node.
        int internalCost = a_dest.internalCost(a_agent);
        int totalCost = m_cost + internalCost;

        if(totalCost < m_cost)
        {
            int a = 0;
        }

        if(a_volatile)
            return totalCost;
        else
            return m_cost;
        
    }



}
