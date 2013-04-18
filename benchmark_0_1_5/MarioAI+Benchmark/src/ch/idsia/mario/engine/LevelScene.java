package ch.idsia.mario.engine;

import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.LevelGenerator;
import ch.idsia.mario.engine.level.SpriteTemplate;
import ch.idsia.mario.engine.sprites.*;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.CmdLineOptions;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelScene extends Scene implements SpriteContext
{
    final public List<Sprite> sprites = new ArrayList<Sprite>();
    final private List<Sprite> spritesToAdd = new ArrayList<Sprite>();
    final private List<Sprite> spritesToRemove = new ArrayList<Sprite>();

    public Level level;
    public Mario mario;
    public float xCam, yCam, xCamO, yCamO;

    public int tick;

    public boolean paused = false;
    public int startTime = 0;
    public int timeLeft;
    private int width;
    private int height;

    public boolean visualization = false;

    final private int rows = Environment.HalfObsHeight*2+1;
    final private int cols = Environment.HalfObsWidth*2+1;
    final private int[] serializedLevelScene = new int[rows * cols];
    final private int[] serializedEnemies = new int[rows * cols];
    final private int[] serializedMergedObservation = new int[rows * cols];

    final private byte[][] levelSceneZ = new byte[rows][cols];
    final private byte[][] enemiesZ = new byte[rows][cols];
    final private byte[][] mergedZ = new byte[rows][cols];

    final private List<Float> enemiesFloatsList = new ArrayList<Float>();
    final private float[] marioFloatPos = new float[2];
    final private int[] marioState = new int[12];
    private int numberOfHiddenCoinsGained;

//    public int getTimeLimit() {  return timeLimit; }
    public void setTimeLimit(int timeLimit) {  this.timeLimit = timeLimit; }

    private int timeLimit = 200;

    //    private Recorder recorder = new Recorder();
    //    private Replayer replayer = null;

    private long levelSeed;
    private int levelType;
    private int levelDifficulty;
    private int levelLength;
    private int levelHeight;
    public static int killedCreaturesTotal;
    public static int killedCreaturesByFireBall;
    public static int killedCreaturesByStomp;
    public static int killedCreaturesByShell;

    private int[] args; //passed to reset method. ATTENTION: not cloned.

    public LevelScene(long seed, int levelDifficulty, int type, int levelLength, int levelHeight, int timeLimit, int visualization)
    {
        this.levelSeed = seed;
        this.levelDifficulty = levelDifficulty;
        this.levelType = type;
        this.levelLength = levelLength;
        this.levelHeight = levelHeight;
        this.setTimeLimit(timeLimit);
        this.visualization = visualization == 1;
        killedCreaturesTotal = 0;
        killedCreaturesByFireBall = 0;
        killedCreaturesByStomp = 0;
        killedCreaturesByShell = 0;

        // moved from init;
        try
        {
//            System.out.println("Java::LevelScene: loading tiles.dat...");
//            System.out.println("LS: System.getProperty(\"user.dir()\") = " + System.getProperty("user.dir"));

            //Level.loadBehaviors(new DataInputStream(LevelScene.class.getResourceAsStream("C:\\tiles.dat")));
            Level.loadBehaviors(new DataInputStream(LevelScene.class.getResourceAsStream("resources/tiles.dat")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void init()
    {
//        System.out.println("\nJava:init() entered.");
//        try
//        {
//            Level.loadBehaviors(new DataInputStream(LevelScene.class.getResourceAsStream("resources/tiles.dat")));
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            System.exit(0);
//        }
        /*        if (replayer!=null)
         {                level = LevelGenerator.createLevel(2048, 15, replayer.nextLong());         }
         else
         {*/
//        level = LevelGenerator.createLevel(320, 15, levelSeed);
//        if (levelSeed != -152)
            level = LevelGenerator.createLevel(args);
//        else
//        try
//        {
//            level = Level.load(new DataInputStream(LevelScene.class.getResourceAsStream("resources/test.lvl")));
//            System.out.println("level.getWidthCells() = " + level.getWidthCells());
//            level.xExit = level.width - 6;
//            level.yExit = 5;

//            System.out.println("level.xExit = " + level.xExit);
//            System.out.println("level.yExit = " + level.yExit);
//        } catch (IOException e)
//        {
//            System.err.println("OOPS! Sorry! Critical ERROR: \n " +
//                               "Generation of secret level failed. Please, contact sergey@idsia.ch");
//        }
//        Ssystem.out.println("\nJava:level created.");
        //        }

        /*        if (recorder != null)
         {
         recorder.addLong(LevelGenerator.lastSeed);
         }*/
        paused = false;
        Sprite.spriteContext = this;
        sprites.clear();
        this.width = GlobalOptions.VISUAL_COMPONENT_WIDTH;
        this.height = GlobalOptions.VISUAL_COMPONENT_HEIGHT;

        mario = new Mario(this);
        sprites.add(mario);
//        System.out.println("mario.sheet = " + mario.sheet);
        startTime = 1;
//        if (levelSeed == 152)
//            mario.x = level.xExit * 16 - 17;
        timeLeft = timeLimit *15;

        tick = 0;
    }

    private String mapElToStr(int el)
    {
        String s = "";
        if  (el == 0 || el == 1)
            s = "##";
        s += (el == mario.kind) ? "#M.#" : el;
        while (s.length() < 4)
            s += "#";
        return s + " ";
    }

    private String enemyToStr(int el)
        {
            String s = "";
            if  (el == 0)
                s = "";
            s += (el == mario.kind) ? "-m" : el;
            while (s.length() < 2)
                s += "#";
            return s + " ";
        }

    private byte ZLevelMapElementGeneralization(byte el, int ZLevel)
    {
        if (el == 0)
            return 0;
        switch (ZLevel)
        {
            case(0):
                switch(el)
                {
                    case 16:  // brick, simple, without any surprise.
                    case 17:  // brick with a hidden coin
                    case 18:  // brick with a hidden flower
                        return 16; // prevents cheating
                    case 21:       // question brick, contains coin
                    case 22:       // question brick, contains flower/mushroom
                        return 21; // question brick, contains something
                }
                return el;
            case(1):
                switch(el)
                {
                    case 16:  // brick, simple, without any surprise.
                    case 17:  // brick with a hidden coin
                    case 18:  // brick with a hidden flower
                        return 16; // prevents cheating
                    case 21:       // question brick, contains coin
                    case 22:       // question brick, contains flower/mushroom
                        return 21; // question brick, contains something
                    case 1:   // hidden block
                        return 0; // prevents cheating
                    case(-111):
                    case(-108):
                    case(-107):
                    case(-106):
                    case(15): // Sparcle, irrelevant
                        return 0;
                    case(34): // Coin, irrelevant for the current contest
                        return 34;
                    case(-128):
                    case(-127):
                    case(-126):
                    case(-125):
                    case(-120):
                    case(-119):
                    case(-118):
                    case(-117):
                    case(-116):
                    case(-115):
                    case(-114):
                    case(-113):
                    case(-112):
                    case(-110):
                    case(-109):
                    case(-104):
                    case(-103):
                    case(-102):
                    case(-101):
                    case(-100):
                    case(-99):
                    case(-98):
                    case(-97):
                    case(-96):
                    case(-95):
                    case(-94):
                    case(-93):
                    case(-69):
                    case(-65):
                    case(-88):
                    case(-87):
                    case(-86):
                    case(-85):
                    case(-84):
                    case(-83):
                    case(-82):
                    case(-81):
                    case(-77):
                    case(4):  // kicked hidden brick
                    case(9):
                        return -10;   // border, cannot pass through, can stand on
//                    case(9):
//                        return -12; // hard formation border. Pay attention!
                    case(-124):
                    case(-123):
                    case(-122):
                    case(-76):
                    case(-74):
                        return -11; // half-border, can jump through from bottom and can stand on
                    case(10): case(11): case(26): case(27): // flower pot
                    case(14): case(30): case(46): // canon
                        return 20;  // angry flower pot or cannon
                }
                System.err.println("ZLevelMapElementGeneralization: Unknown value el = " + el + " Possible Level tiles bug; " +
                                   "Please, inform sergey@idsia.ch or julian@togelius.com. Thanks!");
                return el;
            case(2):
                switch(el)
                {
                    //cancel out half-borders, that could be passed through
                    case(0):
                    case(-108):
                    case(-107):
                    case(-106):
                    case(34): // coins
                    case(15): // Sparcle, irrelevant
                        return 0;
                }
                return 1;  // everything else is "something", so it is 1
        }
        System.err.println("Unkown ZLevel Z" + ZLevel);
        return el; //TODO: Throw unknown ZLevel exception
    }


    private byte ZLevelEnemyGeneralization(byte el, int ZLevel)
    {
        switch (ZLevel)
        {
            case(0):
                switch(el)
                {
                    // cancel irrelevant sprite codes
                    case(Sprite.KIND_COIN_ANIM):
                    case(Sprite.KIND_PARTICLE):
                    case(Sprite.KIND_SPARCLE):
                    case(Sprite.KIND_MARIO):
                        return Sprite.KIND_NONE;
                }
                return el;   // all the rest should go as is
            case(1):
                switch(el)
                {
                    case(Sprite.KIND_COIN_ANIM):
                    case(Sprite.KIND_PARTICLE):
                    case(Sprite.KIND_SPARCLE):
                    case(Sprite.KIND_MARIO):
                        return Sprite.KIND_NONE;
                    case (Sprite.KIND_FIRE_FLOWER):
                        return Sprite.KIND_FIRE_FLOWER;
                    case (Sprite.KIND_MUSHROOM):
                        return Sprite.KIND_MUSHROOM;
                    case(Sprite.KIND_FIREBALL):
                        return Sprite.KIND_FIREBALL;
                    case(Sprite.KIND_BULLET_BILL):
                    case(Sprite.KIND_GOOMBA):
                    case(Sprite.KIND_GOOMBA_WINGED):
                    case(Sprite.KIND_GREEN_KOOPA):
                    case(Sprite.KIND_GREEN_KOOPA_WINGED):
                    case(Sprite.KIND_RED_KOOPA):
                    case(Sprite.KIND_RED_KOOPA_WINGED):
                    case(Sprite.KIND_SHELL):
                        return Sprite.KIND_GOOMBA;
                    case(Sprite.KIND_SPIKY):
                    case(Sprite.KIND_ENEMY_FLOWER):
                    case(Sprite.KIND_SPIKY_WINGED):
                        return Sprite.KIND_SPIKY;
                }
                System.err.println("Z1 UNKOWN el = " + el);
                return el;
            case(2):
                switch(el)
                {
                    case(Sprite.KIND_COIN_ANIM):
                    case(Sprite.KIND_PARTICLE):
                    case(Sprite.KIND_SPARCLE):
                    case(Sprite.KIND_FIREBALL):
                    case(Sprite.KIND_MARIO):
                    case(Sprite.KIND_FIRE_FLOWER):
                    case(Sprite.KIND_MUSHROOM):
                        return Sprite.KIND_NONE;
                    case(Sprite.KIND_BULLET_BILL):
                    case(Sprite.KIND_GOOMBA):
                    case(Sprite.KIND_GOOMBA_WINGED):
                    case(Sprite.KIND_GREEN_KOOPA):
                    case(Sprite.KIND_GREEN_KOOPA_WINGED):
                    case(Sprite.KIND_RED_KOOPA):
                    case(Sprite.KIND_RED_KOOPA_WINGED):
                    case(Sprite.KIND_SHELL):
                    case(Sprite.KIND_SPIKY):
                    case(Sprite.KIND_ENEMY_FLOWER):
                    case(Sprite.KIND_SPIKY_WINGED):
                        return 1;
                }
                System.err.println("ERROR: Z2 UNKNOWNN el = " + el);
                return 1;
        }
        return el; //TODO: Throw unknown ZLevel exception
    }

    public byte[][] getLevelSceneObservationZ(int ZLevel)
    {
        //TODO: Move to constants 16
        int MarioXInMap = (int)mario.x/16;
        int MarioYInMap = (int)mario.y/16;

        for (int y = MarioYInMap - Environment.HalfObsHeight, obsX = 0; y <= MarioYInMap + Environment.HalfObsHeight; y++, obsX++)
        {
            for (int x = MarioXInMap - Environment.HalfObsWidth, obsY = 0; x <= MarioXInMap + Environment.HalfObsWidth; x++, obsY++)
            {
                if (x >=0 /*  && x <= level.xExit */ && y >= 0 && y < level.height)
                {
                    levelSceneZ[obsX][obsY] = ZLevelMapElementGeneralization(level.map[x][y], ZLevel);
                }
                else
                    levelSceneZ[obsX][obsY] = 0;
            }
        }
        return levelSceneZ;
    }

    public byte[][] getEnemiesObservationZ(int ZLevel)
    {
        //TODO: Move to constants 16
        int MarioXInMap = (int)mario.x/16;
        int MarioYInMap = (int)mario.y/16;

        for (int w = 0; w < enemiesZ.length; w++)
            for (int h = 0; h < enemiesZ[0].length; h++)
                enemiesZ[w][h] = 0;
//        enemiesZ[Environment.HalfObsWidth][Environment.HalfObsHeight] = mario.kind;
        for (Sprite sprite : sprites)
        {
            if (sprite.kind == mario.kind)
                continue;
            if (sprite.mapX >= 0 &&
                sprite.mapX >= MarioXInMap - Environment.HalfObsWidth &&
                sprite.mapX <= MarioXInMap + Environment.HalfObsWidth &&
                sprite.mapY >= 0 &&
                sprite.mapY >= MarioYInMap - Environment.HalfObsHeight &&
                sprite.mapY <= MarioYInMap + Environment.HalfObsHeight )
            {
                int obsX = sprite.mapY - MarioYInMap + Environment.HalfObsHeight;
                int obsY = sprite.mapX - MarioXInMap + Environment.HalfObsWidth;
                enemiesZ[obsX][obsY] = ZLevelEnemyGeneralization(sprite.kind, ZLevel);
            }
        }
        return enemiesZ;
    }

    public float[] getEnemiesFloatPos()
    {
        enemiesFloatsList.clear();
        for (Sprite sprite : sprites)
        {
            // check if is an influenceable creature
            if (sprite.kind >= Sprite.KIND_GOOMBA && sprite.kind <= Sprite.KIND_MUSHROOM)
            {
                enemiesFloatsList.add((float)sprite.kind);
                enemiesFloatsList.add(sprite.x);
                enemiesFloatsList.add(sprite.y);
            }
        }

        // potential memory leak while using through JNI, careful!
        float[] enemiesFloatsPosArray = new float[enemiesFloatsList.size()];

        int i = 0;
        for (Float F: enemiesFloatsList)
            enemiesFloatsPosArray[i++] = F;

        return enemiesFloatsPosArray;
    }

    public byte[][] getMergedObservationZZ(int ZLevelScene, int ZLevelEnemies)
    {

        //TODO: Move to constants 16
        int MarioXInMap = (int)mario.x/16;
        int MarioYInMap = (int)mario.y/16;


        for (int y = MarioYInMap - Environment.HalfObsHeight, obsX = 0; y <= MarioYInMap + Environment.HalfObsHeight; y++, obsX++)
        {
            for (int x = MarioXInMap - Environment.HalfObsWidth, obsY = 0; x <= MarioXInMap + Environment.HalfObsWidth; x++, obsY++)
            {
                if (x >=0 /*&& x <= level.xExit*/ && y >= 0 && y < level.height)
                {
                    mergedZ[obsX][obsY] = ZLevelMapElementGeneralization(level.map[x][y], ZLevelScene);
                }
                else
                    mergedZ[obsX][obsY] = 0;
//                if (x == MarioXInMap && y == MarioYInMap)
//                    mergedZ[obsX][obsY] = mario.kind;
            }
        }

//        for (int w = 0; w < mergedZ.length; w++)
//            for (int h = 0; h < mergedZ[0].length; h++)
//                mergedZ[w][h] = -1;
//        mergedZ[Environment.HalfObsWidth][Environment.HalfObsHeight] = mario.kind;
        for (Sprite sprite : sprites)
        {
            if (sprite.kind == mario.kind)
                continue;
            if (sprite.mapX >= 0 &&
                sprite.mapX > MarioXInMap - Environment.HalfObsWidth &&
                sprite.mapX < MarioXInMap + Environment.HalfObsWidth &&
                sprite.mapY >= 0 &&
                sprite.mapY > MarioYInMap - Environment.HalfObsHeight &&
                sprite.mapY < MarioYInMap + Environment.HalfObsHeight )
            {
                int obsX = sprite.mapY - MarioYInMap + Environment.HalfObsHeight;
                int obsY = sprite.mapX - MarioXInMap + Environment.HalfObsWidth;
                // quick fix TODO: handle this in more general way.
                if (mergedZ[obsX][obsY] != 14)
                {
                    byte tmp = ZLevelEnemyGeneralization(sprite.kind, ZLevelEnemies);
                    if (tmp != Sprite.KIND_NONE)
                        mergedZ[obsX][obsY] = tmp;
                }
            }
        }

        return mergedZ;
    }

    public List<String> LevelSceneAroundMarioASCII(boolean Enemies, boolean LevelMap,
                                                   boolean mergedObservationFlag,
                                                   int ZLevelScene, int ZLevelEnemies){
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));//        bw.write("\nTotal world width = " + level.width);
        List<String> ret = new ArrayList<String>();
        if (level != null && mario != null)
        {
            ret.add("Total world width = " + level.width);
            ret.add("Total world height = " + level.height);
            ret.add("Physical Mario Position (x,y): (" + mario.x + "," + mario.y + ")");
            ret.add("Mario Observation Width " + Environment.HalfObsWidth*2);
            ret.add("Mario Observation Height " + Environment.HalfObsHeight*2);
            ret.add("X Exit Position: " + level.xExit);
            int MarioXInMap = (int)mario.x/16;
            int MarioYInMap = (int)mario.y/16;
            ret.add("Calibrated Mario Position (x,y): (" + MarioXInMap + "," + MarioYInMap + ")\n");

            byte[][] levelScene = getLevelSceneObservationZ(ZLevelScene);
            if (LevelMap)
            {
                ret.add("~ZLevel: Z" + ZLevelScene + " map:\n");
                for (int x = 0; x < levelScene.length; ++x)
                {
                    String tmpData = "";
                    for (int y = 0; y < levelScene[0].length; ++y)
                        tmpData += mapElToStr(levelScene[x][y]);
                    ret.add(tmpData);
                }
            }

            byte[][] enemiesObservation = null;
            if (Enemies || mergedObservationFlag)
            {
                enemiesObservation = getEnemiesObservationZ(ZLevelEnemies);
            }

            if (Enemies)
            {
                ret.add("~ZLevel: Z" + ZLevelScene + " Enemies Observation:\n");
                for (int x = 0; x < enemiesObservation.length; x++)
                {
                    String tmpData = "";
                    for (int y = 0; y < enemiesObservation[0].length; y++)
                    {
//                        if (x >=0 && x <= level.xExit)
                            tmpData += enemyToStr(enemiesObservation[x][y]);
                    }
                    ret.add(tmpData);
                }
            }

            if (mergedObservationFlag)
            {
                byte[][] mergedObs = getMergedObservationZZ(ZLevelScene, ZLevelEnemies);
                ret.add("~ZLevelScene: Z" + ZLevelScene + " ZLevelEnemies: Z" + ZLevelEnemies + " ; Merged observation /* Mario ~> #M.# */");
                for (int x = 0; x < levelScene.length; ++x)
                {
                    String tmpData = "";
                    for (int y = 0; y < levelScene[0].length; ++y)
                        tmpData += mapElToStr(mergedObs[x][y]);
                    ret.add(tmpData);
                }
            }
        }
        else
            ret.add("~level or mario is not available");
        return ret;
    }


    public int fireballsOnScreen = 0;

    List<Shell> shellsToCheck = new ArrayList<Shell>();

    public void checkShellCollide(Shell shell)
    {
        shellsToCheck.add(shell);
    }

    List<Fireball> fireballsToCheck = new ArrayList<Fireball>();

    public void checkFireballCollide(Fireball fireball)
    {
        fireballsToCheck.add(fireball);
    }

    public void tick()
    {
        if (GlobalOptions.isTimer)
                timeLeft--;
        if (timeLeft==0)
        {
            mario.die();
        }
        xCamO = xCam;
        yCamO = yCam;

        if (startTime > 0)
        {
            startTime++;
        }

        float targetXCam = mario.x - 160;

        xCam = targetXCam;

        if (xCam < 0) xCam = 0;
        if (xCam > level.width * 16 - GlobalOptions.VISUAL_COMPONENT_WIDTH)
            xCam = level.width * 16 - GlobalOptions.VISUAL_COMPONENT_WIDTH;

        /*      if (recorder != null)
         {
         recorder.addTick(mario.getKeyMask());
         }

         if (replayer!=null)
         {
         mario.setKeys(replayer.nextTick());
         }*/

        fireballsOnScreen = 0;

        for (Sprite sprite : sprites)
        {
            if (sprite != mario)
            {
                float xd = sprite.x - xCam;
                float yd = sprite.y - yCam;
                if (xd < -64 || xd > GlobalOptions.VISUAL_COMPONENT_WIDTH + 64 || yd < -64 || yd > GlobalOptions.VISUAL_COMPONENT_HEIGHT + 64)
                {
                    removeSprite(sprite);
                }
                else
                {
                    if (sprite instanceof Fireball)
                    {
                        fireballsOnScreen++;
                    }
                }
            }
        }

        if (paused)
        {
            for (Sprite sprite : sprites)
            {
                if (sprite == mario)
                {
                    sprite.tick();
                }
                else
                {
                    sprite.tickNoMove();
                }
            }
        }
        else
        {
            tick++;
            level.tick();

//            boolean hasShotCannon = false;
//            int xCannon = 0;

            for (int x = (int) xCam / 16 - 1; x <= (int) (xCam + this.width) / 16 + 1; x++)
                for (int y = (int) yCam / 16 - 1; y <= (int) (yCam + this.height) / 16 + 1; y++)
                {
                    int dir = 0;

                    if (x * 16 + 8 > mario.x + 16) dir = -1;
                    if (x * 16 + 8 < mario.x - 16) dir = 1;

                    SpriteTemplate st = level.getSpriteTemplate(x, y);

                    if (st != null)
                    {
                        if (st.lastVisibleTick != tick - 1)
                        {
                            if (st.sprite == null || !sprites.contains(st.sprite))
                            {
                                st.spawn(this, x, y, dir);
                            }
                        }

                        st.lastVisibleTick = tick;
                    }

                    if (dir != 0)
                    {
                        byte b = level.getBlock(x, y);
                        if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_ANIMATED) > 0)
                        {
                            if ((b % 16) / 4 == 3 && b / 16 == 0)
                            {
                                if ((tick - x * 2) % 100 == 0)
                                {
//                                    xCannon = x;
                                    for (int i = 0; i < 8; i++)
                                    {
                                        addSprite(new Sparkle(x * 16 + 8, y * 16 + (int) (Math.random() * 16), (float) Math.random() * dir, 0, 0, 1, 5));
                                    }
                                    addSprite(new BulletBill(this, x * 16 + 8 + dir * 8, y * 16 + 15, dir));
//                                    hasShotCannon = true;
                                }
                            }
                        }
                    }
                }

            for (Sprite sprite : sprites)
            {
                sprite.tick();
            }

            for (Sprite sprite : sprites)
            {
                sprite.collideCheck();
            }

            for (Shell shell : shellsToCheck)
            {
                for (Sprite sprite : sprites)
                {
                    if (sprite != shell && !shell.dead)
                    {
                        if (sprite.shellCollideCheck(shell))
                        {
                            if (mario.carried == shell && !shell.dead)
                            {
                                mario.carried = null;
                                mario.setRacoon(false);
                                System.out.println("sprite = " + sprite);
                                shell.die();
                                ++this.killedCreaturesTotal;
                            }
                        }
                    }
                }
            }
            shellsToCheck.clear();

            for (Fireball fireball : fireballsToCheck)
            {
                for (Sprite sprite : sprites)
                {
                    if (sprite != fireball && !fireball.dead)
                    {
                        if (sprite.fireballCollideCheck(fireball))
                        {
                            fireball.die();
                        }
                    }
                }
            }
            fireballsToCheck.clear();
        }

        sprites.addAll(0, spritesToAdd);
        sprites.removeAll(spritesToRemove);
        spritesToAdd.clear();
        spritesToRemove.clear();
    }


    public void addSprite(Sprite sprite)
    {
        spritesToAdd.add(sprite);
        sprite.tick();
    }

    public void removeSprite(Sprite sprite)
    {
        spritesToRemove.add(sprite);
    }

//    public float getX(float alpha)
//    {
//        int xCam = (int) (mario.xOld + (mario.x - mario.xOld) * alpha) - 160;
//        //        int yCam = (int) (mario.yOld + (mario.y - mario.yOld) * alpha) - 120;
//        //int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
//        //        int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
//        if (xCam < 0) xCam = 0;
//        //        if (yCam < 0) yCam = 0;
//        //        if (yCam > 0) yCam = 0;
//        return xCam + 160;
//    }

//    public float getY(float alpha)
//    {
//        return 0;
//    }

    public void bump(int x, int y, boolean canBreakBricks)
    {
        byte block = level.getBlock(x, y);

        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BUMPABLE) > 0)
        {
            bumpInto(x, y - 1);
            level.setBlock(x, y, (byte) 4);
            level.setBlockData(x, y, (byte) 4);

            if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_SPECIAL) > 0)
            {
                if (!Mario.large)
                {
                    addSprite(new Mushroom(this, x * 16 + 8, y * 16 + 8));
                }
                else
                {
                    addSprite(new FireFlower(this, x * 16 + 8, y * 16 + 8));
                }
            }
            else
            {
                Mario.getCoin();
                addSprite(new CoinAnim(x, y));
            }
        }

        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BREAKABLE) > 0)
        {
            bumpInto(x, y - 1);
            if (canBreakBricks)
            {
                level.setBlock(x, y, (byte) 0);
                for (int xx = 0; xx < 2; xx++)
                    for (int yy = 0; yy < 2; yy++)
                        addSprite(new Particle(x * 16 + xx * 8 + 4, y * 16 + yy * 8 + 4, (xx * 2 - 1) * 4, (yy * 2 - 1) * 4 - 8));
            }
            else
            {
                level.setBlockData(x, y, (byte) 4);
            }
        }
    }

    public void bumpInto(int x, int y)
    {
        byte block = level.getBlock(x, y);
        if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_PICKUPABLE) > 0)
        {
            Mario.getCoin();
            level.setBlock(x, y, (byte) 0);
            addSprite(new CoinAnim(x, y + 1));
        }

        for (Sprite sprite : sprites)
        {
            sprite.bumpCheck(x, y);
        }
    }

//    public void update(boolean[] action)
//    {
//        System.arraycopy(action, 0, mario.keys, 0, 6);
//    }

    public int getTimeSpent() {  return startTime / 15;    }

    public int getTimeLeft() {        return timeLeft / 15;    }

    public int getKillsTotal()
    {
        return mario.world.killedCreaturesTotal;
    }

    public int getKillsByFire()
    {
        return mario.world.killedCreaturesByFireBall;
    }

    public int getKillsByStomp()
    {
        return mario.world.killedCreaturesByStomp;
    }

    public int getKillsByShell()
    {
        return mario.world.killedCreaturesByShell;
    }

    public int[] getMarioState()
    {

        marioState[0] =         this.getMarioStatus();
        marioState[1] =         this.getMarioMode();
        marioState[2] =         this.isMarioOnGround() ? 1 : 0;
        marioState[3] =         this.isMarioAbleToJump() ? 1 : 0;
        marioState[4] =         this.isMarioAbleToShoot() ? 1 : 0;
        marioState[5] =         this.isMarioCarrying() ? 1 : 0;
        marioState[6] =         this.getKillsTotal();
        marioState[7] =         this.getKillsByFire();
        marioState[8] =         this.getKillsByStomp();
        marioState[9] =         this.getKillsByStomp();
        marioState[10] =        this.getKillsByShell();
//        marioState[11] =        this.getTimeLimit();
        marioState[11] =        this.getTimeLeft();
        return marioState;
    }

    public void performAction(boolean[] action)
    {
        // might look ugly , but arrayCopy is not necessary here:
        this.mario.keys = action;
    }

    public boolean isLevelFinished()
    {
        return mario.getStatus() != Mario.STATUS_RUNNING;
    }


    public void reset(CmdLineOptions cmdLineOptions)
    {
        reset(cmdLineOptions.toIntArray());
    }

    public boolean isMarioAbleToShoot()
    {
        return mario.isCanShoot();
    }

    public int getMarioStatus()
    {
        return mario.getStatus();
    }

    public float[] getSerializedFullObservationZZ(int ZLevelScene, int ZLevelEnemies)
    {
        // TODO:SK, serialize all data to a sole double[]
        assert false;
        return new float[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int[] getSerializedLevelSceneObservationZ(int ZLevelScene)
    {
        // serialization into arrays of primitive types to speed up the data transfer.
        byte[][] levelScene = this.getLevelSceneObservationZ(ZLevelScene);
        for (int i = 0; i < serializedLevelScene.length; ++i)
        {
            serializedLevelScene[i] = (int)levelScene[i / cols][i % rows];
        }
        return serializedLevelScene;
    }

    public int[] getSerializedEnemiesObservationZ(int ZLevelEnemies)
    {
        // serialization into arrays of primitive types to speed up the data transfer.
        byte[][] enemies = this.getEnemiesObservationZ(ZLevelEnemies);
        for (int i = 0; i < serializedEnemies.length; ++i)
        {
            serializedEnemies[i] = (int)enemies[i / cols][i % rows];
        }
        return serializedEnemies;
    }

    public int[] getSerializedMergedObservationZZ(int ZLevelScene, int ZLevelEnemies)
    {
        // serialization into arrays of primitive types to speed up the data transfer.
        byte[][] merged = this.getMergedObservationZZ(ZLevelScene, ZLevelEnemies);
        for (int i = 0; i < serializedMergedObservation.length; ++i)
        {
            serializedMergedObservation[i] = (int)merged[i / cols][i % rows];
        }
        return serializedMergedObservation;
    }

    public float[] getCreaturesFloatPos()
    {
        float[] enemies = this.getEnemiesFloatPos();
        float ret[] = new float[enemies.length + 2];
        System.arraycopy(this.getMarioFloatPos(), 0, ret, 0, 2);
        System.arraycopy(enemies, 0, ret, 2, enemies.length);
        return ret;
    }

    public boolean isMarioOnGround()
    {        return mario.isOnGround();    }

    public boolean isMarioAbleToJump()
    {        return mario.mayJump();    }


    public void resetDefault()
    {
        // TODO: set values ot defaults
        init();
    }

    public void reset(int[] setUpOptions)
    {
//        this.gameViewer = setUpOptions[0] == 1;
//        System.out.println("\nLevelScene RESET!");

//        System.out.println("\n setUpOptions:");
//        for (int i = 0; i < setUpOptions.length; ++i)
//        {
//            System.out.print(setUpOptions[i] + ",");
//        }

        this.mario.isMarioInvulnerable = setUpOptions[1] == 1;
//        System.out.println("this.mario.isMarioInvulnerable = " + this.mario.isMarioInvulnerable);
        this.levelDifficulty = setUpOptions[2];
//        System.out.println("this.levelDifficulty = " + this.levelDifficulty);
        this.levelLength = setUpOptions[3];
//        System.out.println("this.levelLength = " + this.levelLength);
        this.levelSeed = setUpOptions[4];
//        System.out.println("levelSeed = " + levelSeed);
        this.levelType = setUpOptions[5];
//        System.out.println("levelType = " + levelType);
        Mario.resetStatic(setUpOptions[6]);

        GlobalOptions.FPS = setUpOptions[7];
//        System.out.println("GlobalOptions.FPS = " + GlobalOptions.FPS);
        GlobalOptions.isPowerRestoration = setUpOptions[8] == 1;
//        System.out.println("GlobalOptions.isPowerRestoration = " + GlobalOptions.isPowerRestoration);
        GlobalOptions.isPauseWorld = setUpOptions[9] == 1;
//        System.out.println("GlobalOptions = " + GlobalOptions.isPauseWorld);
        GlobalOptions.isTimer = setUpOptions[10] == 1;
//        System.out.println("GlobalOptions.isTimer = " + GlobalOptions.isTimer);
//        isToolsConfigurator = setUpOptions[11] == 1;
        this.setTimeLimit(setUpOptions[12]);
//        System.out.println("this.getTimeLimit() = " + this.getTimeLimit());
//        this.isViewAlwaysOnTop() ? 1 : 0, setUpOptions[13]
        this.visualization = setUpOptions[14] == 1;
//        System.out.println("visualization = " + visualization);
//        this.getViewLocation().x, setUpOptions[15] == 1;
//        this.getViewLocation().y, setUpOptions[16] == 1;
//        this.getZLevelEnemies(),setUpOptions[17] ;
//        this.getZLevelScene()   setUpOptions[18] ;
        this.levelHeight = setUpOptions[19];

        killedCreaturesTotal = 0;
        killedCreaturesByFireBall = 0;
        killedCreaturesByStomp = 0;
        killedCreaturesByShell = 0;
//        System.out.println("Call to init:");
        args = setUpOptions;
        init();
    }

    public float[] getMarioFloatPos()
    {
        marioFloatPos[0] = this.mario.x;
        marioFloatPos[1]  = this.mario.y;
        return marioFloatPos;
    }

    public int getMarioMode()
    {   return mario.getMode();    }

    public boolean isMarioCarrying()
    {   return mario.carried != null; }


//    public byte[][] getCompleteObservation()
//    {  return getMergedObservationZZ(1, 0);    }
//
//    public byte[][] getEnemiesObservation()
//    {        return getEnemiesObservationZ(0);    }
//
//    public byte[][] getLevelSceneObservation()
//    {         return getLevelSceneObservationZ(1);    }

    public int getLevelDifficulty()
    {         return levelDifficulty;    }

    public long getLevelSeed()
    {                                        return levelSeed;    }

    public int getLevelLength()
    {        return levelLength;    }

    public int getLevelType()
    {        return levelType;    }

    public int getNumberOfHiddenCoinsGained()
    {
        return numberOfHiddenCoinsGained;
    }
}