/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.GEBT_Mario;

/**
 *
 * @author Diego
 */
public class Item {

    //Position and type of the item.
    public int m_x;
    public int m_y;
    public byte m_type;

    public Item()
    {
        m_x = m_y = m_type = -1;
    }

    public Item(int a_x, int a_y, byte a_type)
    {
        m_x = a_x;
        m_y = a_y;
        m_type = a_type;
    }

    public String toString(Item a_other)
    {
        return "(" + m_x + "," + m_y + ") =*> " + m_type;
    }

}
