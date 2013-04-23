package mcts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Diego Perez, University of Essex.
 * Date: 23/04/13
 */
public class MCTS
{
        /**
         * Root node
         */
        public MCTSNode m_rootNode;

        /**
         * Copy of the game.
         */
        public MarioSimulator m_currentGameCopy;

        /**
         * Random number generator.
         */
        public Random m_rnd;

        /**
         * Best route found.
         */
        public int[] m_bestRoute;

        /**
         * Average of executions
         */
        public int m_acumMCTSCount;

        /**
         * Number of MCTS executions
         */
        public int m_mctsCount;

        /**
         * Depth of the tree
         */
        public int m_treeDepth;

        /**
         * Average of executions
         */
        public int m_acumTreeDepth;

        /**
         * Value for K, rollout depth
         */
        protected static final float K = (float)Math.sqrt(2); //0.025f;
        public static int ROLLOUT_DEPTH = 10;   //10;     //0 for UCT with no rollouts

        public double m_bestScoreSoFar;
        public ArrayList<MacroAction> m_bestActions;
        public ArrayList<MacroAction> m_currentActions;

        public int m_depthDiff;

        public ArrayList<MacroAction> m_actionList;

        public GameEvaluator m_gameEvaluator;

        /**
         * Default constructor.
         */
        public MCTS(MarioSimulator a_game, long a_timeDue)
        {
            m_rnd = new Random();
            m_acumMCTSCount = 0;
            m_treeDepth = 0;
            m_mctsCount = 0;
            m_acumTreeDepth = 0;
            m_depthDiff = 0;
            m_bestScoreSoFar = -Double.MAX_VALUE;

            //m_bestRouteSoFar = new ArrayList<Vector2d>();
            //m_currentRoute = new ArrayList<Vector2d>();
            m_bestActions = new ArrayList<MacroAction>();
            m_currentActions = new ArrayList<MacroAction>();
            m_actionList = new ArrayList<MacroAction>();


            m_gameEvaluator = new GameEvaluator();

            // Create actions
            for(int i = 0; i <= GameEvaluator.NUM_ACTIONS; ++i)  //all actions
            {
                int m = GameEvaluator.getMove(i);
                boolean j = GameEvaluator.getJump(i);
                boolean s = GameEvaluator.getSpeed(i);
                m_actionList.add(new MacroAction(m,j,s,GameEvaluator.MACRO_ACTION_LENGTH));
            }

        }

        /**
         * Executes the MCTS algorithm
         * @param a_gameCopy Copy of the game state.
         * @param a_timeDue when this is due to end.
         * @return the action to execute.
         */
        public MacroAction execute(MarioSimulator a_gameCopy, long a_timeDue, boolean a_throwTree)
        {
            m_currentGameCopy = a_gameCopy;


            //m_bestRouteSoFar.clear();
            //m_currentRoute.clear();
            m_bestActions.clear();
            m_currentActions.clear();
            m_bestScoreSoFar = -Double.MAX_VALUE;
            boolean check = false;

            if(m_bestActions.size() <= 1)
            {
                m_bestActions.clear();
                m_bestScoreSoFar = -Double.MAX_VALUE;
            }

            m_rootNode = new MCTSNode(m_currentGameCopy);
            m_treeDepth = 0;
            m_depthDiff=0;

            double avgTimeTaken = 0;
            int loopCount = 1;
            m_mctsCount++;
            mctsLoop();

            double remaining = (a_timeDue-System.currentTimeMillis());
            while(remaining > 10)
            //while(System.currentTimeMillis()+2*(avgTimeTaken/loopCount)<a_timeDue)  //FOR REAL TIME CONSTRAINTS
            //while(loopCount < 200)
            {
                double start = System.currentTimeMillis();
                mctsLoop();
                loopCount++;
                double end = System.currentTimeMillis();
                avgTimeTaken+=(end-start);
                remaining = (a_timeDue-System.currentTimeMillis());
            }

            m_acumMCTSCount += loopCount;
            m_acumTreeDepth +=  m_treeDepth ;

            MCTSNode best = bestChild();
            if(best == null) return null;

            return best.m_lastMove;
        }

        /**
         * Executes a MCTS iteration.
         */
        private void mctsLoop()
        {
            ArrayList<MCTSNode> visited = new ArrayList<MCTSNode>();
            MCTSNode currentNode = m_rootNode;
            boolean m_end = false;
            double outcome=0;
            MCTSNode newNode;
            int depth = 0;

            while(!m_end)
            {
                //if(currentNode.m_game.isEnded())
                if(m_gameEvaluator.isEndGame(currentNode.m_game))
                {
                    outcome = m_gameEvaluator.scoreGame(currentNode.m_game);
                    checkBestRoute(outcome);
                    m_end = true;
                }
                else
                {
                    //Game is not ended.
                    if(currentNode.m_visits == 0 && currentNode.m_parent!=null)
                    {
                        outcome = rollout(currentNode, depth);
                        checkBestRoute(outcome);
                        m_end = true;
                    }else
                    {
                        if(currentNode.m_children.size()<currentNode.m_numMoves)
                        {
                            newNode=expand(currentNode);
                        }else{
                            newNode=chooseChildUCB1(currentNode);
                        }
                        depth++;
                        visited.add(newNode);
                        currentNode=newNode;
                    }
                }
            }

            for(int i=0;i<visited.size();i++)
            {
                visited.get(i).m_value+=outcome;
                visited.get(i).m_visits++;
            }
            m_rootNode.m_visits++;
        }


        private void advanceGame(MarioSimulator a_game, MacroAction a_move)
        {
            int singleMove = a_move.buildAction();
            boolean end = false;
            for(int i = 0; !end &&  i < a_move.m_repetitions; ++i)
            {
                a_game.tick(singleMove);
                end = GameEvaluator.isEndGame(a_game);
                //m_currentRoute.add(a_game.getShip().s.copy());
            }
            m_currentActions.add(a_move);
        }


        private void checkBestRoute(double a_score)
        {
            if(a_score > m_bestScoreSoFar)
            {
                m_bestScoreSoFar = a_score;
                m_bestActions.clear();
                for(MacroAction i : m_currentActions)
                {
                    m_bestActions.add(i);
                }
            }
            //m_currentRoute.clear();
            m_currentActions.clear();
        }

        /**
         * Gets the best child.
         * @return the best child (node).
         */
        private MCTSNode bestChild()
        {
            double[] values=new double[m_rootNode.m_children.size()];

            for(int i=0;i<values.length;i++)
            {
                values[i]= m_rootNode.m_children.get(i).m_value;
            }

            if(values.length==0)
            {
                return null;
            }
            return m_rootNode.m_children.get(argmax(values));
        }


        /**
         * Gets the best child (average).
         * @return the best child (node).
         */
        private MCTSNode bestAvgChild()
        {
            double[] values=new double[m_rootNode.m_children.size()];

            for(int i=0;i<values.length;i++)
            {
                int nMoves = m_rootNode.m_children.get(i).m_numMoves;
                if(nMoves == 0)
                    values[i]=0;
                else
                    values[i]= m_rootNode.m_children.get(i).m_value / nMoves;
            }

//		if(values.length>0)
            return m_rootNode.m_children.get(argmax(values));
        }

        private MCTSNode mostVistiedChild()
        {
            double[] values=new double[m_rootNode.m_children.size()];

            for(int i=0;i<values.length;i++)
            {
                values[i]= m_rootNode.m_children.get(i).m_numMoves;
            }

//		if(values.length>0)
            return m_rootNode.m_children.get(argmax(values));
        }

        private MCTSNode leadingToBestRoute()
        {
            MacroAction next = m_bestActions.get(0);
            return m_rootNode.m_children.get(next.buildAction());

        }

        /**
         * Creates a new child of the node received by parameter.
         * @param a_node Node parent of the new node to be created.
         * @return the node created.
         */
        private MCTSNode expand(MCTSNode a_node)
        {
            MarioSimulator newGame=a_node.m_game.getCopy();
            int nthMove=a_node.m_children.size();
            MacroAction move = m_actionList.get(nthMove);
            advanceGame(newGame, move);

            MCTSNode child=new MCTSNode(newGame,a_node,m_actionList.get(nthMove));
            a_node.m_children.add(child);

            if(child.m_depth > m_treeDepth)
                m_treeDepth = child.m_depth;

            return child;

        }

        /**
         * TREE SELECTION: Chooses a node between the children of the node given.
         * @param a_node THe node, parent of the children to be chosen.
         * @return the chosen node.
         */
        private MCTSNode chooseChild(MCTSNode a_node)
        {
            double[] values=new double[a_node.m_children.size()];

            double part=Math.log(a_node.m_visits);

            for(int i=0;i<values.length;i++)
            {
                double vis=a_node.m_children.get(i).m_visits;
                double val=a_node.m_children.get(i).m_value;

                //average value, as usual
                values[i]=(val/ vis)+K*Math.sqrt(part/vis)+m_rnd.nextFloat()*1e-8;
            }

            return a_node.m_children.get(argmax(values));
        }

        private MCTSNode chooseChildUCB1(MCTSNode a_node)
        {
            double[] values=new double[a_node.m_children.size()];
            double part=Math.log(a_node.m_visits + 1);
            double minFitness = 0;
            double maxFitness = GameEvaluator.MAX_FITNESS;

            for(int i=0;i<values.length;i++)
            {
                double vis=a_node.m_children.get(i).m_visits;
                double val=a_node.m_children.get(i).m_value;
                double estimatedValue = val / vis;
                double estimatedValueN = normalise(estimatedValue, minFitness, maxFitness);
                double valAux = part / vis;

                //average value, as usual
                values[i] = estimatedValueN+K*Math.sqrt(valAux)+m_rnd.nextFloat()*1e-8;
            }

            int nextAction = argmax(values);
            MCTSNode node = a_node.m_children.get(nextAction);
            m_currentActions.add(m_actionList.get(nextAction));
            return node;
        }

        //Normalizes a value between its MIN and MAX.
        public static double normalise(double a_value, double a_min, double a_max)
        {
            return (a_value - a_min)/(a_max - a_min);
        }

        /**
         * Performs a rollout until the end of the game or a maximum depth reached.
         * @param a_node Node to start the rollout from.
         * @return score value of the game after the rollout.
         */
        private double rollout(MCTSNode a_node, int a_startingDepth)
        {
            MarioSimulator newGame=null;
            int count=a_startingDepth;
            try{
                newGame=a_node.m_game.getCopy();
                boolean gameEnded = m_gameEvaluator.isEndGame(newGame);

                //while(count++<ROLLOUT_DEPTH && !newGame.isEnded())
                while(count++<ROLLOUT_DEPTH && !gameEnded)
                {
                    int action=m_rnd.nextInt(GameEvaluator.NUM_ACTIONS);
                    MacroAction ma = m_actionList.get(action);
                    advanceGame(newGame, ma);
                    gameEnded = m_gameEvaluator.isEndGame(newGame);
                }

               /* if(gameEnded)
                    System.out.println("Game ended at: " + (count-1) + " / " + ROLLOUT_DEPTH); */

                double value = m_gameEvaluator.scoreGame(newGame);
                return value;

            }catch(Exception e)
            {
                throw new RuntimeException(e.toString());
            }
        }

        public int argmax(double... sequence)
        {
            try{
                int index=0;
                double max=sequence[index];

                for(int i=0;i<sequence.length;i++)
                    if(Double.compare(sequence[i],max)>0)
                    {
                        max=sequence[i];
                        index=i;
                    }

                return index;
            }
            catch(Exception e)
            {
//			System.out.println("ERROR");
                return 0;
            }
        }


}
