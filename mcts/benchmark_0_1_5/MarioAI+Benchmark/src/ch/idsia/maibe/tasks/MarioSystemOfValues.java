package ch.idsia.maibe.tasks;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey at idsia dot ch
 * Date: Mar 27, 2010 Time: 5:55:38 PM
 * Package: ch.idsia.scenarios.champ
 */

/**
 * tune the parameters of the multiobjective function with MarioSystemOfValues class
 * assigning a high value to a certain parameter should steer your agent to maximize
 * objective function w.r.t this value,
 * e.g.
 * assigning timeLeft = 0 and coins = 1000 should make your agent collect all coins before
 * advanching to finish. If win = 0 as well, this agent will not have motivation to win.
 * or
 * very high value of kills should produce a true `MARIONATOR`, making him a perfect killer.
 * By tuning killedByFire, killedByShell, killedByStomp you make the killer
 * stylish and of refined manners. 
 */

public class MarioSystemOfValues extends SystemOfValues
{
    final public int distance = 1;
    final public int win = 1024;
    final public int mode = 32;
    final public int coins = 16;
    final public int hiddenItems = 24;
    final public int flowerFire = 64;  // not used for now
    final public int kills = 42;
    final public int killedByFire = 4;
    final public int killedByShell = 17;
    final public int killedByStomp = 12;
    final public int timeLeft = 8;

    public interface timeLengthMapping 
    {
        final public static int TIGHT = 10;
        final public static int MEDIUM = 20;
        final public static int FLEXIBLE = 30;
    }
}
