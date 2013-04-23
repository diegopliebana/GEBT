/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.bt.behaviortree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 *
 * @author Diego
 */
public abstract class XML_BTReader {

    BufferedReader m_reader;

    public XML_BTReader()
    {
    }
    

    public BehaviorTree openDouble(BTInterface a_bt, String a_filename)
    {

        BehaviorTree bt_temp = null;

        try
        {
            bt_temp = new BehaviorTree(a_bt.getAgent(),a_bt.getReader());
            
            //For a file, create a writer.
            m_reader = new BufferedReader(new FileReader(a_filename));

            BTRootNode btRoot = new BTRootNode();
            bt_temp.setRootNode(btRoot);
            btRoot.setBehaviorTree(bt_temp);

            //Read the temp bt!
            readXMLRecur(bt_temp, btRoot);

            //And close it
            m_reader.close();

        }catch(FileNotFoundException e)
        {
            System.out.println("Error: file not found: " + a_filename);
            e.printStackTrace();
            return null;
        }catch(IOException e)
        {
            System.out.println("Error reading the file: " + a_filename);
            e.printStackTrace();
            return null;
        }

        return bt_temp;
    }

    public boolean open(BTInterface a_bt, String a_filename)
    {
        boolean ok = true;
        try
        {
            //For a file, create a writer.
            m_reader = new BufferedReader(new FileReader(a_filename));

            BTRootNode btRoot = new BTRootNode();
            a_bt.setRootNode(btRoot);
            btRoot.setBehaviorTree((BehaviorTree)a_bt);
            
            //Read!
            readXMLRecur((BehaviorTree)a_bt, btRoot);
            
            //And close it
            m_reader.close();
            
        }catch(FileNotFoundException e)
        {
            System.out.println("Error: file not found: " + a_filename);
            e.printStackTrace();
            ok = false;
        }catch(IOException e)
        {
            System.out.println("Error reading the file: " + a_filename);
            e.printStackTrace();
            ok = false;
        }
        
        return ok;
    }

    public BehaviorTree readDouble(BTInterface a_bt, BTStream a_stream)
    {
        boolean ok = true;

        BehaviorTree bt_temp = new BehaviorTree(a_bt.getAgent(),a_bt.getReader());

        BTRootNode btRoot = new BTRootNode();
        bt_temp.setRootNode(btRoot);
        btRoot.setBehaviorTree(bt_temp);

        //Read!
        readXMLRecurStream(bt_temp, btRoot, a_stream);

        return bt_temp;
    }


    public boolean read(BTInterface a_bt, BTStream a_stream)
    {
        boolean ok = true;
        
        BTRootNode btRoot = new BTRootNode();
        a_bt.setRootNode(btRoot);
        btRoot.setBehaviorTree((BehaviorTree)a_bt);

        //Read!
        readXMLRecurStream((BehaviorTree)a_bt, btRoot, a_stream);

        return ok;
    }

    
    private String unformatDataXML(String prop)
    {
        String newProp;
        newProp = prop.replace("__", " ");
        newProp = newProp.replace("_", " ");
        return newProp;
    }
    
    private void readXMLRecurStream(BehaviorTree a_bt, BTNode a_parent, BTStream a_stream)
    {
        try
        {
            String line = a_stream.readLine();
            boolean endChilds = false;
            BTNode newNode = null;
            while (!endChilds && line != null && line.length() > 0)
            {
                if (line.contains("<Node"))
                {

                    Hashtable<String, String> properties = new Hashtable<String, String>();

                    //Lets split its components
                    String chunks[] = line.split(" ");
                    for (int i = 0; i < chunks.length; ++i)
                    {
                        if (chunks[i].contains("="))
                        {
                            //It is a property!
                            int index = chunks[i].indexOf('=');
                            String property = chunks[i].substring(0, index);
                            //String value = chunks[i].substring(index + 2, (chunks[i].length() - 1) - (index + 2));
                            String value = chunks[i].substring(index + 2, chunks[i].length() - 1);

                            String propUnformatted = unformatDataXML(property);
                            String valueUnformated = unformatDataXML(value);

                            properties.put(propUnformatted, valueUnformated);
                        }
                    }

                    newNode = a_bt.createNode(a_parent, properties);
                    if(newNode != null)
                    {
                        newNode.setBehaviorTree(a_bt);
                        a_parent.add(newNode);
                    }
                    else
                    {
                        //Could not be created... skip this.
                        newNode = a_parent;
                    }

                }
                else if (line.contains("<Connector"))
                {
                    //These are childs of the node.
                    readXMLRecurStream(a_bt, newNode, a_stream);
                }
                else if (line.contains("</Connector>"))
                {
                    //End of children.
                    endChilds = true;
                }
                else if (line.contains("</Node"))
                {
                    //End of node!
                }

                if(!endChilds)
                    line = a_stream.readLine();
            }
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    
    private void readXMLRecur(BehaviorTree a_bt, BTNode a_parent) throws IOException
    {
        String line = m_reader.readLine();
        boolean endChilds = false;
        BTNode newNode = null;
        while (!endChilds && line != null)
        {
            if (line.contains("<Node"))
            {
                
                Hashtable<String, String> properties = new Hashtable<String, String>();
                
                //Lets split its components
                String chunks[] = line.split(" ");
                for (int i = 0; i < chunks.length; ++i)
                {
                    if (chunks[i].contains("="))
                    {
                        //It is a property!
                        int index = chunks[i].indexOf('=');
                        String property = chunks[i].substring(0, index);
                        //String value = chunks[i].substring(index + 2, (chunks[i].length() - 1) - (index + 2));
                        String value = chunks[i].substring(index + 2, chunks[i].length() - 1);
                        
                        String propUnformatted = unformatDataXML(property);
                        String valueUnformated = unformatDataXML(value);

                        properties.put(propUnformatted, valueUnformated);
                    }
                }

                try
                {
                    newNode = a_bt.createNode(a_parent, properties);
                    if(newNode != null)
                    {
                        newNode.setBehaviorTree(a_bt);
                        a_parent.add(newNode);
                    }
                    else
                    {
                        //Could not be created... skip this.
                        newNode = a_parent;
                    }
                }
                catch(IncorrectNodeException e)
                {
                    System.out.println("**** INCORRECT NODE *****");
                    e.printStackTrace();
                }

            }
            else if (line.contains("<Connector"))
            {
                //These are childs of the node.
                readXMLRecur(a_bt, newNode);
            }
            else if (line.contains("</Connector>"))
            {
                //End of children.
                endChilds = true;
            }
            else if (line.contains("</Node"))
            {
                //End of node!
            }

            if(!endChilds)
                line = m_reader.readLine();
        }

    }

    public abstract BTLeafNode readNode(BTNode a_parent, Hashtable<String, String> a_properties);
    
}
