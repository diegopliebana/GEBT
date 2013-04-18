package ch.idsia.mario.engine;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.CheaterKeyboardAgent;
import ch.idsia.mario.engine.level.BgLevelGenerator;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.sprites.Sprite;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.GameViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.image.VolatileImage;
import java.text.DecimalFormat;
import java.util.List;



/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Feb 26, 2010 Time: 3:54:52 PM
 * Package: ch.idsia.mario.engine
 */

public class MarioVisualComponent extends JComponent
{
    private CheaterKeyboardAgent cheatAgent = null;

    public int width, height;

    public VolatileImage thisVolatileImage;
    public Graphics thisVolatileImageGraphics;
    public Graphics thisGraphics;

    public LevelScene levelScene;
    private LevelRenderer layer;
    private BgRenderer[] bgLayer = new BgRenderer[2];

    private Mario mario;
    private Level level;

    final private static DecimalFormat df = new DecimalFormat("00");
    final private static DecimalFormat df2 = new DecimalFormat("000");

    private static String[] LEVEL_TYPES = {"Overground(0)",
                                           "Underground(1)",
                                           "Castle(2)"};

    private long tm = System.currentTimeMillis();
    private long tm0;
    int delay;
    private KeyAdapter prevHumanKeyBoardAgent;
    private String agentNameStr;
    private GameViewer gameViewer = null;
    private static MarioVisualComponent marioVisualComponent = null;

    private MarioVisualComponent(int width, int height, LevelScene levelScene)
    {
        this.levelScene = levelScene;
        adjustFPS();

        this.setFocusable(true);
        this.setEnabled(true);
        this.width = width;
        this.height = height;

        Dimension size = new Dimension(width, height);

        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        setFocusable(true);

        if (this.cheatAgent == null)
        {
            this.cheatAgent = new CheaterKeyboardAgent();
            this.addKeyListener(cheatAgent);
        }

//        System.out.println("this (from constructor) = " + this);

        GlobalOptions.registerMarioVisualComponent(this);

        if (GlobalOptions.isGameVeiwer)
        {
            if (this.gameViewer == null)
            {

                this.setGameViewer(new GameViewer(null, null));
                this.gameViewer.setMarioVisualComponent(this);
                this.gameViewer.setVisible(true);
            }
        }
    }

    public static MarioVisualComponent getInstance(int width, int height, LevelScene levelScene)
    {
        if (marioVisualComponent == null)
        {
            marioVisualComponent = new MarioVisualComponent(width, height, levelScene);
            marioVisualComponent.CreateMarioComponentFrame(marioVisualComponent);
        }
        return marioVisualComponent;
    }

    private static JFrame marioComponentFrame = null;

    public void CreateMarioComponentFrame(MarioVisualComponent m)
    {
        if (marioComponentFrame == null)
        {
            marioComponentFrame = new JFrame(/*evaluationOptions.getAgentName() +*/ "Mario AI benchmark " + GlobalOptions.getVersionUID());
            marioComponentFrame.setContentPane(m);
            m.init();
            marioComponentFrame.pack();
            marioComponentFrame.setResizable(false);
            marioComponentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        marioComponentFrame.setAlwaysOnTop(true);
//        marioComponentFrame.setLocation(new Point(42,42));
        marioComponentFrame.setVisible(true);
        m.postInitGraphics();
    }

    public void setLocation(int x, int y)
    {
        marioComponentFrame.setLocation(x, y);
    }

    public void reset()
    {
        adjustFPS();
        tm = System.currentTimeMillis();
        this.tm0 = tm;
    }

    public void tick()
    {
        this.render(thisVolatileImageGraphics, 0);
        String msg = "Agent: " + this.agentNameStr;
        drawStringDropShadow(thisVolatileImageGraphics, msg, 0, 7, 5);

        msg = "Selected Actions: ";
        drawStringDropShadow(thisVolatileImageGraphics, msg, 0, 8, 6);

        msg = "";
        if (mario.keys != null)
        {
            for (int i = 0; i < Environment.numberOfButtons; ++i)
                msg += (mario.keys[i]) ? Scene.keysStr[i] : "      ";
        }
        else
            msg = "NULL";
        drawString(thisVolatileImageGraphics, msg, 6, 78, 1);

        if (!this.hasFocus() && (tm - tm0)/(delay+1) % 42 < 20 ) {
            String msgClick = "CLICK TO PLAY";
            drawString(thisVolatileImageGraphics, msgClick, 160 - msgClick.length() * 4, 110, 2);
//            drawString(thisVolatileImageGraphics, msgClick, 160 - msgClick.length() * 4, 110, 7);
        }
//        thisVolatileImageGraphics.setColor(Color.DARK_GRAY);
        drawStringDropShadow(thisVolatileImageGraphics, "FPS: ", 32, 2, 7);
        drawStringDropShadow(thisVolatileImageGraphics, ((GlobalOptions.FPS > 99) ? "\\infty" : GlobalOptions.FPS.toString()), 32, 3, 7);

//                msg = totalNumberOfTrials == -2 ? "" : currentTrial + "(" + ((totalNumberOfTrials == -1) ? "\\infty" : totalNumberOfTrials) + ")";

//            drawStringDropShadow(thisVolatileImageGraphics, "Trial:", 33, 4, 7);
        drawStringDropShadow(thisVolatileImageGraphics, msg, 33, 5, 7);

//        if (width != 320 || height != 240) {
//            thisGraphics.drawImage(thisVolatileImage, 0, 0, 320 * 4, 240 * 4, null);
//        } else {
//            thisGraphics.drawImage(thisVolatileImage, 0, 0, null);
//        }

        thisGraphics.drawImage(thisVolatileImage, 0, 0, null);
        if (this.gameViewer != null)
            this.gameViewer.tick();
        // Delay depending on how far we are behind.
        if (delay > 0)
        {
//            System.out.println("delay = " + delay);
            try {
                tm += delay;
                Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
            } catch (InterruptedException ignored) {}
        }
    }

    public void render(Graphics g, float alpha)
    {
        int xCam = (int) (mario.xOld + (mario.x - mario.xOld) * alpha) - 160;
        int yCam = (int) (mario.yOld + (mario.y - mario.yOld) * alpha) - 120;

        if (GlobalOptions.isMarioAlwaysInCenter)
        {
        }
        else
        {
            //int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
            //        int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
            if (xCam < 0) xCam = 0;
            if (yCam < 0) yCam = 0;
            if (xCam > level.width * 16 - GlobalOptions.VISUAL_COMPONENT_WIDTH)
                xCam = level.width * 16 - GlobalOptions.VISUAL_COMPONENT_WIDTH;
            if (yCam > level.height * 16 - GlobalOptions.VISUAL_COMPONENT_HEIGHT)
                yCam = level.height * 16 - GlobalOptions.VISUAL_COMPONENT_HEIGHT;
        }
        //      g.drawImage(Art.background, 0, 0, null);

        for (int i = 0; i < 2; i++)
        {
            bgLayer[i].setCam(xCam, yCam);
            bgLayer[i].render(g, levelScene.tick, alpha); //levelScene.
        }

        g.translate(-xCam, -yCam);

        for (Sprite sprite : levelScene.sprites)          // levelScene.
        {
            if (sprite.layer == 0) sprite.render(g, alpha);
        }

        g.translate(xCam, yCam);

        layer.setCam(xCam, yCam);
        layer.render(g, levelScene.tick, levelScene.paused?0:alpha); //levelScene., levelScene.
        layer.renderExit0(g, levelScene.tick, levelScene.paused?0:alpha, mario.winTime==0);

        g.translate(-xCam, -yCam);

        // TODO: Dump out of render!
        if (mario.cheatKeys[Mario.KEY_DUMP_CURRENT_WORLD])
            for (int w = 0; w < level.width; w++)
                for (int h = 0; h < level.height; h++)
                    level.observation[w][h] = -1;

        for (Sprite sprite : levelScene.sprites)
        {
            if (sprite.layer == 1) sprite.render(g, alpha);
            if (mario.cheatKeys[Mario.KEY_DUMP_CURRENT_WORLD] && sprite.mapX >= 0 && sprite.mapX < level.observation.length &&
                sprite.mapY >= 0 && sprite.mapY < level.observation[0].length)
                level.observation[sprite.mapX][sprite.mapY] = sprite.kind;

        }

        g.translate(xCam, yCam);
        g.setColor(Color.BLACK);
        layer.renderExit1(g, levelScene.tick, levelScene.paused?0:alpha);

//        drawStringDropShadow(g, "MARIO: " + df.format(Mario.lives), 0, 0, 7);
//        drawStringDropShadow(g, "#########", 0, 1, 7);


        drawStringDropShadow(g, "DIFFICULTY:   " + df.format(levelScene.getLevelDifficulty()), 0, 0, levelScene.getLevelDifficulty() > 6 ? 1 : levelScene.getLevelDifficulty() > 2 ? 4 : 7 ); drawStringDropShadow(g, "CREATURES:" + (mario.world.paused ? "OFF" : " ON"), 19, 0, 7);
        drawStringDropShadow(g, "SEED:" + levelScene.getLevelSeed(), 0, 1, 7);
        drawStringDropShadow(g, "TYPE:" + LEVEL_TYPES[levelScene.getLevelType()], 0, 2, 7);                  drawStringDropShadow(g, "ALL KILLS: " + levelScene.killedCreaturesTotal, 19, 1, 1);
        drawStringDropShadow(g, "LENGTH:" + (int)mario.x/16 + " of " + levelScene.getLevelLength(), 0, 3, 7); drawStringDropShadow(g, "by Fire  : " + levelScene.killedCreaturesByFireBall, 19, 2, 1);
        drawStringDropShadow(g,"COINS    : " + df.format(Mario.coins), 0, 4, 4);                      drawStringDropShadow(g, "by Shell : " + levelScene.killedCreaturesByShell, 19, 3, 1);
        drawStringDropShadow(g, "MUSHROOMS: " + df.format(Mario.mushroomsDevoured), 0, 5, 4);                  drawStringDropShadow(g, "by Stomp : " + levelScene.killedCreaturesByStomp, 19, 4, 1);
        drawStringDropShadow(g, "FLOWERS  : " + df.format(Mario.flowersDevoured), 0, 6, 4);

        drawStringDropShadow(g, "TIME", 33, 0, 7);
        int time = (levelScene.timeLeft+15-1)/15;
        if (time<0) time = 0;
        drawStringDropShadow(g, " "+df2.format(time), 33, 1, 7);

        drawProgress(g);

        if (GlobalOptions.areLabels)
        {
            g.drawString("xCam: " + xCam + "yCam: " + yCam, 10, 205);
            g.drawString("x : " + mario.x + "y: " + mario.y, 10, 215);
            g.drawString("xOld : " + mario.xOld + "yOld: " + mario.yOld, 10, 225);
        }

        if (levelScene.startTime > 0)
        {
            float t = levelScene.startTime + alpha - 2;
            t = t * t * 0.6f;
            renderBlackout(g, 160, 120, (int) (t));
        }
//        mario.x>level.xExit*16
        if (mario.winTime > 0)
        {
            float t = mario.winTime + alpha;
            t = t * t * 0.2f;

            if (t > 900)
            {
//                renderer.levelWon();
                mario.win();
                //              replayer = new Replayer(recorder.getBytes());
//                init();
            }

            renderBlackout(g, mario.xDeathPos - xCam, mario.yDeathPos - yCam,
                           (int) (GlobalOptions.VISUAL_COMPONENT_WIDTH - t));
        }

        if (mario.deathTime > 0)
        {
//            float t = mario.deathTime + alpha;
//            t = t * t * 0.4f;
//
//            if (t > 1800)
//            {
//                renderer.levelFailed();
            mario.die();
            //              replayer = new Replayer(recorder.getBytes());
//                init();
//            }

//            renderBlackout(g, (int) (mario.xDeathPos - xCam), (int) (mario.yDeathPos - yCam), (int) (320 - t));
        }

//        this.drawGrid(g, 5);
    }

    private void drawProgress(Graphics g)
    {
        String entirePathStr = "......................................>";
        double physLength = (levelScene.getLevelLength() - 53)*16;
        int progressInChars = (int) (mario.x * (entirePathStr.length()/physLength));
        String progress_str = "";
        for (int i = 0; i < progressInChars - 1; ++i)
            progress_str += ".";
        progress_str += "M";
        try {
            drawStringDropShadow(g, entirePathStr.substring(progress_str.length()), progress_str.length(), 28, 0);
        } catch (StringIndexOutOfBoundsException e)
        {
//            System.err.println("warning: progress line inaccuracy");
        }
        drawStringDropShadow(g, progress_str, 0, 28, 2);
    }

    public static void drawStringDropShadow(Graphics g, String text, int x, int y, int c)
    {
        drawString(g, text, x*8+5, y*8+5, 0);
        drawString(g, text, x*8+4, y*8+4, c);
    }

    public static void drawString(Graphics g, String text, int x, int y, int c)
    {
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++)
            g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
    }

    private void drawGrid(Graphics g, int length)
    {
        width = length*16;
        height = length*16;
        g.setColor(Color.GREEN);
        
        int rows = length;
        int columns = length;

        int htOfRow = height / (rows);
        int k;
        for (k = 0; k < rows; k++)
            g.drawLine((int) mario.x, k * htOfRow , (int) (mario.x +  width), k * htOfRow );

        int wdOfRow = width / (columns);
        for (k = 0; k < columns; k++)
            g.drawLine(k*wdOfRow , 0, k*wdOfRow , height);

    }

    private void renderBlackout(Graphics g, int x, int y, int radius)
    {
        if (radius > GlobalOptions.VISUAL_COMPONENT_WIDTH) return;

        int[] xp = new int[20];
        int[] yp = new int[20];
        for (int i = 0; i < 16; i++)
        {
            xp[i] = x + (int) (Math.cos(i * Math.PI / 15) * radius);
            yp[i] = y + (int) (Math.sin(i * Math.PI / 15) * radius);
        }
        xp[16] = GlobalOptions.VISUAL_COMPONENT_WIDTH;
        yp[16] = y;
        xp[17] = GlobalOptions.VISUAL_COMPONENT_WIDTH;
        yp[17] = GlobalOptions.VISUAL_COMPONENT_HEIGHT;
        xp[18] = 0;
        yp[18] = GlobalOptions.VISUAL_COMPONENT_HEIGHT;
        xp[19] = 0;
        yp[19] = y;
        g.fillPolygon(xp, yp, xp.length);

        for (int i = 0; i < 16; i++)
        {
            xp[i] = x - (int) (Math.cos(i * Math.PI / 15) * radius);
            yp[i] = y - (int) (Math.sin(i * Math.PI / 15) * radius);
        }
        xp[16] = GlobalOptions.VISUAL_COMPONENT_WIDTH;
        yp[16] = y;
        xp[17] = GlobalOptions.VISUAL_COMPONENT_WIDTH;
        yp[17] = 0;
        xp[18] = 0;
        yp[18] = 0;
        xp[19] = 0;
        yp[19] = y;

        g.fillPolygon(xp, yp, xp.length);
    }

//    static GraphicsConfiguration CreateMarioComponentFrame(EvaluationOptions evaluationOptions)
//    {
////        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
////        frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);
//        if (marioComponentFrame == null)
//        {
//            marioComponentFrame = new JFrame(/*evaluationOptions.getAgentName() +*/ "Mario AI benchmark-" + GlobalOptions.MAIBeVersionStr);
//            marioVisualComponent = new MarioVisualComponent(320, 240);
//            marioComponentFrame.setContentPane(marioVisualComponent);
//            marioVisualComponent.init();
//            marioComponentFrame.pack();
//            marioComponentFrame.setResizable(false);
//            marioComponentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        }
////        marioComponentFrame.setTitle(evaluationOptions.getAgent().getName() + " - Mario Intelligent 2.0");
//        marioComponentFrame.setAlwaysOnTop(evaluationOptions.isViewAlwaysOnTop());
//        marioComponentFrame.setLocation(evaluationOptions.getViewLocation());
//        marioComponentFrame.setVisible(evaluationOptions.isVisualization());
//        return graphicsConfiguration;
//    }

    private static GraphicsConfiguration graphicsConfiguration;

    public void init()
    {
        graphicsConfiguration = getGraphicsConfiguration();
//        System.out.println("!!HRUYA: graphicsConfiguration = " + graphicsConfiguration);
//        assert (graphicsConfiguration == null);
//        if (graphicsConfiguration != null) {
        Art.init(graphicsConfiguration);
//        }


    }

    public void postInitGraphics()
    {
//        System.out.println("this = " + this);
        this.thisVolatileImage = this.createVolatileImage(GlobalOptions.VISUAL_COMPONENT_WIDTH, GlobalOptions.VISUAL_COMPONENT_HEIGHT);
        this.thisGraphics = getGraphics();
        this.thisVolatileImageGraphics = this.thisVolatileImage.getGraphics();
//        System.out.println("thisGraphics = " + thisGraphics);
//        System.out.println("thisVolatileImageGraphics = " + thisVolatileImageGraphics);
    }


    public void postInitGraphicsAndLevel()
    {
        if (graphicsConfiguration != null)
        {
//            System.out.println("level = " + level);
//            System.out.println("levelScene .level = " + levelScene.level);
            level =                                     levelScene.level;

            this.mario = levelScene.mario;
//            System.out.println("mario = " + mario);
            this.level = levelScene.level;
            layer = new LevelRenderer(level, graphicsConfiguration, this.width, this.height);
            for (int i = 0; i < 2; i++)
            {
                int scrollSpeed = 4 >> i;
                int w = ((level.width * 16) - GlobalOptions.VISUAL_COMPONENT_WIDTH) / scrollSpeed + GlobalOptions.VISUAL_COMPONENT_WIDTH;
                int h = ((level.height * 16) - GlobalOptions.VISUAL_COMPONENT_HEIGHT) / scrollSpeed + GlobalOptions.VISUAL_COMPONENT_HEIGHT;
                Level bgLevel = BgLevelGenerator.createLevel(w / 32 + 1, h / 32 + 1, i == 0, levelScene.getLevelType());
                bgLayer[i] = new BgRenderer(bgLevel, graphicsConfiguration, GlobalOptions.VISUAL_COMPONENT_WIDTH, GlobalOptions.VISUAL_COMPONENT_HEIGHT, scrollSpeed);
            }
        }
    }

    public void adjustFPS()
    {
        int fps = GlobalOptions.FPS;
        delay = (fps > 0) ? (fps >= GlobalOptions.MaxFPS) ? 0 : (1000 / fps) : 100;
//        System.out.println("Delay: " + delay);
    }

    // THis method here solely for the displaying information in order to reduce
    // amount of info passed between Env and VisComponent
    public void setAgent(Agent agent)
    {
        this.agentNameStr = agent.getName();
//        System.out.println("agent = " + agent);
        if (agent instanceof KeyAdapter)
        {
            if (prevHumanKeyBoardAgent != null)
                this.removeKeyListener(prevHumanKeyBoardAgent);
            this.prevHumanKeyBoardAgent = (KeyAdapter) agent;
            this.addKeyListener(this.prevHumanKeyBoardAgent);
        }
    }
    /// `huge` inheritance stuff from MarioComponent
    public void setGameViewer(GameViewer gameViewer)
    {
        this.gameViewer = gameViewer;
    }

    public List<String> getTextObservation(boolean showEnemies, boolean showLevelScene, boolean showMerged, int zLevelMapValue, int zLevelEnemiesValue)
    {
        return levelScene.LevelSceneAroundMarioASCII(showEnemies, showLevelScene, showMerged, zLevelMapValue, zLevelEnemiesValue);
    }
}


