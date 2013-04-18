/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.behaviortree;

/**
 *
 * @author Diego
 */
public abstract class BTLeafNode extends BTNode {

    public BTLeafNode(BTNode a_parent)
    {
        super(a_parent);
    }

    @Override
    public void resetNode(){}

    
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
