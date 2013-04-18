package ch.idsia.mario.engine.sprites;

import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.MarioVisualComponent;
import ch.idsia.mario.engine.level.SpriteTemplate;

import java.awt.*;

public class Sprite
{
    public static final int KIND_NONE = 0;
    public static final int KIND_MARIO = -31;
    public static final int KIND_GOOMBA = 2;
    public static final int KIND_GOOMBA_WINGED = 3;
    public static final int KIND_RED_KOOPA = 4;
    public static final int KIND_RED_KOOPA_WINGED = 5;
    public static final int KIND_GREEN_KOOPA = 6;
    public static final int KIND_GREEN_KOOPA_WINGED = 7;
    public static final int KIND_BULLET_BILL = 8;
    public static final int KIND_SPIKY = 9;
    public static final int KIND_SPIKY_WINGED = 10;
//    public static final int KIND_ENEMY_FLOWER = 11;
    public static final int KIND_ENEMY_FLOWER = 12;
    public static final int KIND_SHELL = 13;
    public static final int KIND_MUSHROOM = 14;
    public static final int KIND_FIRE_FLOWER = 15;
    public static final int KIND_PARTICLE = 21;
    public static final int KIND_SPARCLE = 22;
    public static final int KIND_COIN_ANIM = 20;
    public static final int KIND_FIREBALL = 25;

    public static final int KIND_UNDEF = -42;

    public static SpriteContext spriteContext;
    public byte kind = KIND_UNDEF;

    protected static float GROUND_INERTIA = 0.89f;
    protected static float AIR_INERTIA = 0.89f;

    public float xOld, yOld, x, y, xa, ya;
    public int mapX, mapY;

    public int xPic, yPic;
    public int wPic = 32;
    public int hPic = 32;
    public int xPicO, yPicO;
    public boolean xFlipPic = false;
    public boolean yFlipPic = false;
    public Image[][] sheet;
    public Image[][] prevSheet;

    public boolean visible = true;

    public int layer = 1;

    public SpriteTemplate spriteTemplate;

    public void move()
    {
        x+=xa;
        y+=ya;
    }

    public void render(Graphics og, float alpha)
    {
        if (!visible) return;

//        int xPixel = (int)(xOld+(x-xOld)*alpha)-xPicO;
//        int yPixel = (int)(yOld+(y-yOld)*alpha)-yPicO;

        int xPixel = (int)x-xPicO;
        int yPixel = (int)y-yPicO;

//        System.out.print("xPic = " + xPic);
//        System.out.print(", yPic = " + yPic);
//        System.out.println(", kind = " + this.kind);

        try
        {
            og.drawImage(sheet[xPic][yPic],
                    xPixel+(xFlipPic?wPic:0),
                    yPixel+(yFlipPic?hPic:0),
                    xFlipPic?-wPic:wPic,
                    yFlipPic?-hPic:hPic, null);
        } catch (ArrayIndexOutOfBoundsException ex)
        {
            System.err.println("ok:" + this.kind + ", " + xPic);
        }
        // Labels
        if (GlobalOptions.areLabels)
            og.drawString("" + xPixel + "," + yPixel, xPixel, yPixel);

        // Mario Grid Visualization Enable
        if (GlobalOptions.isShowGrid)
        {
            if (this.kind == KIND_MARIO)
            {
//                og.drawString("M", (int) x, (int) y);
                int width = GlobalOptions.observationGridWidth *16;
                int height = GlobalOptions.observationGridHeight *16;

                int rows = GlobalOptions.observationGridHeight;
                int columns = GlobalOptions.observationGridWidth;

                int htOfRow = 16;//height / (columns);
                int k;
                // horizontal lines
                og.setColor(Color.BLACK);
                for (k = -rows/2 - 1; k <= rows/2; k++)
                    og.drawLine((int) x - width/2, (int) (y + k * htOfRow), (int) (x +  width/2), (int) (y + k * htOfRow));

//                og.setColor(Color.RED);
                // vertical lines
                int wdOfRow = 16;// width / (rows);
                for (k = -columns/2 - 1; k < columns/2 + 1; k++)
                    og.drawLine((int) (x + k*wdOfRow + 8), (int) y - height/2 - 8, (int) (x + k*wdOfRow + 8), (int) (y + height/2 - 8));
            }
            og.setColor(Color.GREEN);
            MarioVisualComponent.drawString(og, String.valueOf(this.kind), (int) x - 4, (int) y - 8, 2);
        }

        if (GlobalOptions.isMatrixView)
            og.drawString("Matrix View", xPixel, yPixel);

    }

    public final void tick()
    {
        xOld = x;
        yOld = y;
        mapX = (int)(xOld / 16);
        mapY = (int)(yOld / 16);
        move();
    }

    public final void tickNoMove()
    {
        xOld = x;
        yOld = y;
    }

//    public float getX(float alpha)
//    {
//        return (xOld+(x-xOld)*alpha)-xPicO;
//    }
//
//    public float getY(float alpha)
//    {
//        return (yOld+(y-yOld)*alpha)-yPicO;
//    }

    public void collideCheck()
    {
    }

    public void bumpCheck(int xTile, int yTile)
    {
    }

    public boolean shellCollideCheck(Shell shell)
    {
        return false;
    }

    public void release(Mario mario)
    {
    }

    public boolean fireballCollideCheck(Fireball fireball)
    {
        return false;
    }
}