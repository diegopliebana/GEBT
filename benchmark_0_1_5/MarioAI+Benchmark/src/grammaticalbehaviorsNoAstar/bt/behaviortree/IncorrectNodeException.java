/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.bt.behaviortree;

/**
 *
 * @author Diego
 */
public class IncorrectNodeException extends Exception{

    
    IncorrectNodeException()
    {
    }
    
    IncorrectNodeException(String a_message)
    {
        super(a_message);
    }
}
