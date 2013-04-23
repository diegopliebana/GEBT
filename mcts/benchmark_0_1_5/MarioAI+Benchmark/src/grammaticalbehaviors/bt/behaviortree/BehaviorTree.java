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
public class BehaviorTree implements BTInterface {

    //Root of the tree
    private BTRootNode m_rootNode;

    //Root of the tree
    private BTNode m_currentNode;
    
    //Agent that holds this behavior tree
    private Object m_agent; 
    
    //Execution tick
    private long m_tick;
    
    //My XML reader.
    XML_BTReader m_xmlReader;
    
    public BehaviorTree(Object a_agent, XML_BTReader a_reader)
    {
        m_rootNode = new BTRootNode();
        m_currentNode = m_rootNode;
        m_agent = a_agent;
        m_xmlReader = a_reader;
        m_tick = 0;
    }
    
    public boolean load(String a_filename)
    {
        return m_xmlReader.open(this, a_filename);
    }
    
    public boolean load(BTStream a_stream)
    {
        return m_xmlReader.read(this, a_stream);
    }

    public XML_BTReader getReader() {return m_xmlReader;}

    public long getCurTick()
    {
        return m_tick;
    } 
    
    public void setRootNode(BTNode a_root)
    {
        m_rootNode = (BTRootNode) a_root;
    }
    
    public BTNode getRootNode()
    {
        return m_rootNode;
    }
    
    public Object getAgent()
    {
        return m_agent;
    }
    
    public void setCurrentNode(BTNode a_curNode)
    {
        m_currentNode = a_curNode;
    }
    
    public BTNode getCurrentNode()
    {
        return m_currentNode;
    }
    
    /**
     * 
     * @param a_parent Parent of this node
     * @param a_properties Hashtable of properties for this node:
     *    Type: Selector, Sequence, Parallel, Filter, Action, Condition (it is NOT case sensitive).
     *    Filter Type: Non
     *    Name(Only for Actions and conditions): Name of the class for the Action/Condition.
     * @return the new node, or null if it could not be created
     */
    public BTNode createNode( BTNode a_parent, Hashtable<String, String> a_properties ) throws IncorrectNodeException
    {
        BTNode newNode = null;

        int nodeId = -1;
        String idValue = a_properties.get("Node id");
        if(idValue != null)
            nodeId = Integer.parseInt(idValue);


        String typeValue = a_properties.get("Type");
        if(typeValue != null)
        {
            //Create node depending on type
            if(typeValue.compareToIgnoreCase("Selector") == 0)
            {
                newNode = new BTSelectNode(a_parent);
            }else if(typeValue.compareToIgnoreCase("Sequence") == 0)
            {
                newNode = new BTSequenceNode(a_parent);
            }
            else if(typeValue.compareToIgnoreCase("Parallel") == 0)
            {
                newNode = new BTParallelNode(a_parent);
            }
            else if(typeValue.compareToIgnoreCase("Filter") == 0)
            {   
                String filterType = a_properties.get("Filter Type");
                if(filterType != null)
                {
                    if(filterType.compareToIgnoreCase("Non") == 0)
                    {
                        newNode = new BTNonFilter(a_parent);
                    } 
                    else if(filterType.compareToIgnoreCase("Until Fails Limited") == 0)
                    {
                        int limit = 1;
                
                        String limitValue = a_properties.get("Times");
                        if(limitValue != null) limit = Integer.parseInt(limitValue);
                        
                        newNode = new BTUntilFailsLimitedFilter(a_parent, limit);
                    }
                    else if(filterType.compareToIgnoreCase("Until Fails") == 0)
                    {
                        newNode = new BTUntilFails(a_parent);
                    }
                    else if(filterType.compareToIgnoreCase("Loop") == 0)
                    {
                        int limit = 1;
                
                        String limitValue = a_properties.get("Times");
                        if(limitValue != null) limit = Integer.parseInt(limitValue);
                        
                        newNode = new BTLoopFilter(a_parent, limit);
                    }
                }
                
            }else if( (typeValue.compareToIgnoreCase("Action") == 0) || 
                      (typeValue.compareToIgnoreCase("Condition") == 0))
            {
                newNode = m_xmlReader.readNode(a_parent, a_properties);
                if(newNode == null)
                {
                    throw new IncorrectNodeException(a_properties.get("Name") + ": undefined node.");
                }
            }
        }

        if(newNode != null)
            newNode.m_nodeId = nodeId;
        
        return newNode;
    }
    
    
    public void execute()
    {
        try
        {
            /*m_rootNode.step();
            m_tick++; //New tick
             */
            if(m_currentNode.getParent() == null)
            {
                //We are at the root, execute from here.
                m_rootNode.step();
            }
            else
            {
                m_currentNode.notifyResult();
            }
            
            m_tick++; 
            
        }
        catch(IncorrectNodeException e)
        {
            System.out.println("Error executing the behavior tree.");
            e.printStackTrace();
        }
    }
    
    public void reset()
    {
        m_currentNode = m_rootNode;
        m_tick = 0;
        m_rootNode.reset();
    }

}
