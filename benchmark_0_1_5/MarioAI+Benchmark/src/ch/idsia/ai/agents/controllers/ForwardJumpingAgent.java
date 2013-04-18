package ch.idsia.ai.agents.controllers;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 12:27:07 AM
 * Package: ch.idsia.ai.agents.controllers;
 */

public class ForwardJumpingAgent extends BasicAIAgent implements Agent
{
    public ForwardJumpingAgent()
    {
        super("ForwardJumpingAgent");
        reset();
    }

    public boolean[] getAction()
    {
        action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] =  isMarioAbleToJump || !isMarioOnGround;
        return action;
    }

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_SPEED] = true;
    }
}
