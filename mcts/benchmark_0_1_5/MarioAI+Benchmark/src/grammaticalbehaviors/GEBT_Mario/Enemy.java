/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.GEBT_Mario;

/**
 *
 * @author Diego
 */
public class Enemy {

    //Position and type of the enemy.
    public int m_x;
    public int m_y;
    public byte m_type;

    public Enemy()
    {
        m_x = m_y = m_type = -1;
    }

    public Enemy(int a_x, int a_y, byte a_type)
    {
        m_x = a_x;
        m_y = a_y;
        m_type = a_type;
    }

    public String toString(Enemy a_other)
    {
        return "(" + m_x + "," + m_y + ") => " + m_type;
    }

}
