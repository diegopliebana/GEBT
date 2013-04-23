/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.behaviortree;

import java.util.Hashtable;

/**
 *
 * @author Diego
 */
public interface BTInterface {

    void execute();

    boolean load(String a_filename);

    boolean load(BTStream a_stream);

    public XML_BTReader getReader();
    public long getCurTick();
    public void setRootNode(BTNode a_root);
    public BTNode getRootNode();
    public Object getAgent();
    public void setCurrentNode(BTNode a_curNode);
    public BTNode getCurrentNode();
    public BTNode createNode( BTNode a_parent, Hashtable<String, String> a_properties ) throws IncorrectNodeException;
    public void reset();

}
