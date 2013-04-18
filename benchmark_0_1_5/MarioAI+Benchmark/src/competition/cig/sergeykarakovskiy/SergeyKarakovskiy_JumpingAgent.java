package competition.cig.sergeykarakovskiy;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.controllers.BasicAIAgent;
import ch.idsia.mario.engine.sprites.Mario;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: Sep 1, 2009
 * Time: 3:12:07 PM
 * Package: competition.cig.sergeykarakovskiy
 */
public class SergeyKarakovskiy_JumpingAgent extends BasicAIAgent implements Agent
{
    public SergeyKarakovskiy_JumpingAgent()
    {
        super("SergeyKarakovskiy_JumpingAgent");
        reset();
    }

    public boolean[] getAction()
    {
        action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] =  isMarioAbleToJump || !isMarioOnGround;
        return action;
    }

    public void reset()
    {
        for (int i = 0; i < action.length; ++i)
            action[i] = false;
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_SPEED] = true;
    }

    public Agent.AGENT_TYPE getType()
    {
        return Agent.AGENT_TYPE.AI;
    }

    public String getName() {        return name;    }

    public void setName(String Name) { this.name = Name;    }
}
