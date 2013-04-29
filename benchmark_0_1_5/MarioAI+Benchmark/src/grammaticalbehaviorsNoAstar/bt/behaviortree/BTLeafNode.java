/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.bt.behaviortree;

/**
 *
 * @author Diego
 */
public abstract class BTLeafNode extends BTNode {

    public BTLeafNode(BTNode a_parent)
    {
        super(a_parent);
    }
    
    
    //Function to notify the parent about my result.
    public void update(int a_nodeStatus) throws IncorrectNodeException
    {
        //Nothing to do.
    }
    

    //Function to execute this node.
    public void step() throws IncorrectNodeException
    {
        super.step();
    }
}
