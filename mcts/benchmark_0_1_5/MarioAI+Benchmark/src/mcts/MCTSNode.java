package mcts;

import java.util.ArrayList;

/**
 * Created by Diego Perez, University of Essex.
 * Date: 23/04/13
 */
public class MCTSNode
{
    /**
     * Parent of this node.
     */
    public MCTSNode m_parent;

    /**
     * Children of this node.
     */
    public ArrayList<MCTSNode> m_children;

    /**
     * Reference to the game.
     */
    public MarioSimulator m_game;

    /**
     * Last move of this node.
     */
    public MacroAction m_lastMove;

    /**
     * Number of moves of this node.
     */
    public int m_numMoves;

    /**
     * Times this node has been executed.
     */
    public int m_visits;

    /**
     * Score value of the node
     */
    public double m_value=0;


    /**
     * Node depth in tree;
     */
    public int m_depth;


    /**
     * Constructor
     * @param a_game
     */
    public MCTSNode(MarioSimulator a_game)
    {
        m_game=a_game;
        m_parent=null;
        m_children=new ArrayList<MCTSNode>();
        m_numMoves= GameEvaluator.NUM_ACTIONS;
        m_lastMove=null;
        m_depth = 0;
    }

    public MCTSNode(MarioSimulator a_game, MCTSNode a_parent,MacroAction a_movePlayed)
    {
        m_game=a_game;
        m_parent=a_parent;
        m_lastMove=a_movePlayed;
        m_children=new ArrayList<MCTSNode>();
        m_numMoves=GameEvaluator.NUM_ACTIONS;
        m_depth = a_parent.m_depth+1;
    }

}
