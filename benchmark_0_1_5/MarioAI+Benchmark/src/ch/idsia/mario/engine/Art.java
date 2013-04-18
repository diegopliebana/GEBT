package ch.idsia.mario.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Art
{
    public static Image[][] mario;
    public static Image[][] racoonmario;
    public static Image[][] smallMario;
    public static Image[][] fireMario;
    public static Image[][] enemies;
    public static Image[][] items;
    public static Image[][] level;
    public static Image[][] particles;
    public static Image[][] font;
    public static Image[][] bg;
//    public static Image[][] map;
//    public static Image[][] endScene;
//    public static Image[][] gameOver;
//    public static Image logo;
//    public static Image titleScreen;
//    final static String curDir = System.getProperty("user.dir");

    public static void init(GraphicsConfiguration gc)
    {
        try
        {
            mario = cutImage(gc, "resources/mariosheet.png", 32, 32);
            racoonmario = cutImage(gc, "resources/racoonmariosheet.png", 32, 32);
            smallMario = cutImage(gc, "resources/smallmariosheet.png", 16, 16);
            fireMario = cutImage(gc, "resources/firemariosheet.png", 32, 32);
            enemies = cutImage(gc, "resources/enemysheet.png", 16, 32);
            items = cutImage(gc, "resources/itemsheet.png", 16, 16);
            level = cutImage(gc, "resources/mapsheet.png", 16, 16);
//            map = cutImage(gc, "resources/worldmap.png", 16, 16);
            particles = cutImage(gc, "resources/particlesheet.png", 8, 8);
            bg = cutImage(gc, "resources/bgsheet.png", 32, 32);
//            logo = getImage(gc, "resources/logo.gif");
//            titleScreen = getImage(gc, "resources/title.gif");
            font = cutImage(gc, "resources/font.gif", 8, 8);
//            endScene = cutImage(gc, "resources/endscene.gif", 96, 96);
//            gameOver = cutImage(gc, "resources/gameovergost.gif", 96, 64);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static Image getImage(GraphicsConfiguration gc, String imageName) throws IOException
    {
        BufferedImage source = null;
        try
        { source = ImageIO.read(Art.class.getResourceAsStream(imageName));        }
        catch (Exception e) {            e.printStackTrace ();        }

        assert source != null;
        Image image = gc.createCompatibleImage(source.getWidth(), source.getHeight(), Transparency.BITMASK);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return image;
    }

    private static Image[][] cutImage(GraphicsConfiguration gc, String imageName, int xSize, int ySize) throws IOException
    {
        Image source = getImage(gc, imageName);
        Image[][] images = new Image[source.getWidth(null) / xSize][source.getHeight(null) / ySize];
        for (int x = 0; x < source.getWidth(null) / xSize; x++)
        {
            for (int y = 0; y < source.getHeight(null) / ySize; y++)
            {
                Image image = gc.createCompatibleImage(xSize, ySize, Transparency.BITMASK);
                Graphics2D g = (Graphics2D) image.getGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(source, -x * xSize, -y * ySize, null);
                g.dispose();
                images[x][y] = image;
            }
        }

        return images;
    }

}