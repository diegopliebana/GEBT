/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviorsNoAstar.bt.behaviortree;

import grammaticalbehaviorsNoAstar.GEBT_Mario.MarioXMLReader;

/**
 *
 * @author Diego
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        BehaviorTree bt = new BehaviorTree(null,new MarioXMLReader());
        bt.execute();
    
    }

}
