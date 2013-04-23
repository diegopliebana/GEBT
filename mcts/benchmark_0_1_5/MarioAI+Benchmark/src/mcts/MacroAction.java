package mcts;

/**
 * Created by Diego Perez, University of Essex.
 * Date: 23/04/13
 */
public class MacroAction
{
        public int m_horz;
        public boolean m_jump;
        public boolean m_speed;
        public int m_repetitions;

        public MacroAction(int a_h, boolean a_j, boolean a_s, int a_rep)
        {
            m_horz = a_h;
            m_jump = a_j;
            m_speed = a_s;
            m_repetitions = a_rep;
        }

        public MacroAction(int a_action, int a_rep)
        {
            m_horz = GameEvaluator.getMove(a_action);
            m_jump = GameEvaluator.getJump(a_action);
            m_speed = GameEvaluator.getSpeed(a_action);
            m_repetitions = a_rep;
        }

        public int buildAction()
        {
            return GameEvaluator.getActionFromInput(m_horz, m_jump, m_speed);
        }



}
