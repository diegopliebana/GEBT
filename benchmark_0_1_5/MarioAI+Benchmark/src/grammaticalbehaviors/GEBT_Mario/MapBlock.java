/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package grammaticalbehaviors.GEBT_Mario;

import java.io.BufferedWriter;

/**
 * Very simple class, just contain a map block information
 * @author Diego
 */
public class MapBlock {

    private byte[][] m_data;

    public MapBlock()
    {
        m_data = new byte[Map.MAP_BLOCK_LENGTH][Map.LEVEL_HEIGHT];
    }

    public boolean checkFlags(int a_x, int a_y, byte a_data)
    {
        if(a_x < 0 || a_x >= Map.MAP_BLOCK_LENGTH || a_y < 0 || a_y >= Map.LEVEL_HEIGHT)
            return false;

        if(m_data[a_x][a_y] != 0)
        {
            //There is something here!
            if ((m_data[a_x][a_y] == Map.MAP_ONE_SPACE_FREE_FLAG) ||
                (m_data[a_x][a_y] == Map.MAP_TWO_SPACES_FREE_FLAG))
            {
                //Do nothing
                if(Map.isNothing(a_data))
                    return false; //Cant write 'nothing'
                else
                    return true; //Write whatever...
            }
            else if(m_data[a_x][a_y] == Map.MAP_SIMPLE_BRICK)
            {
                if(Map.isNothing(a_data))
                {
                    //Recheck free space ?
                    if(a_y+1 < Map.LEVEL_HEIGHT &&
                        ((m_data[a_x][a_y+1] == Map.MAP_ONE_SPACE_FREE_FLAG) ||
                         (m_data[a_x][a_y+1] == Map.MAP_TWO_SPACES_FREE_FLAG))
                       )
                    {
                        //There was a free space over this block. It is not there
                        //anymore since the block is broken!
                        m_data[a_x][a_y+1] = Map.MAP_NOTHING;
                    }
                }
                return true;
            }
            else if(m_data[a_x][a_y] == Map.MAP_QUESTION_BRICK)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }

    }

    public void write(int a_x, int a_y, byte a_data)
    {
        if(!checkFlags(a_x, a_y, a_data)) return;

        m_data[a_x][a_y] = a_data;

        //Something solid here!
        if(Map.isSomethingSolid(a_data) || a_data == Map.MAP_SOFT_OBSTACLE)
        {
            int gapHeight = 0;
            if(a_y+1 < Map.LEVEL_HEIGHT)
            {
                
                if( Map.isNothing(m_data[a_x][a_y+1]) || m_data[a_x][a_y+1] == Map.MAP_SOFT_OBSTACLE)
                {
                    //If we are here, that means there is an empty space over the solid thingy
                    gapHeight++;
                }
            }

            if(gapHeight > 0)
            {
                if(a_y+2 < Map.LEVEL_HEIGHT)
                {
                    if( Map.isNothing(m_data[a_x][a_y+2]) || m_data[a_x][a_y+2] == Map.MAP_SOFT_OBSTACLE)
                    {
                        //If we are here, that means there is an empty space of 2 spaces!! over the solid thingy
                        gapHeight++;
                    }
                }else if(a_y+2 == Map.LEVEL_HEIGHT)
                {
                    //It is the very top. Should be nothing, so gap++
                    gapHeight++;
                }
            }

            if(gapHeight == 1)
            {
                //Space of 1:
                m_data[a_x][a_y+1] = Map.MAP_ONE_SPACE_FREE_FLAG;
            }
            else if(gapHeight == 2)
            {
                //Space of 2:
                m_data[a_x][a_y+1] = Map.MAP_TWO_SPACES_FREE_FLAG;
            }

        }
    }

    public byte read(int a_x, int a_y)
    {
        return m_data[a_x][a_y];
    }

    public void dumpToFile(BufferedWriter a_bw, int a_x, int a_y) throws Exception
    {
        //SEE ZLevelMapElementGeneralization(byte el, int ZLevel) in ch/idsia/mario/engine/LevelScene.java

        if(m_data[a_x][a_y] == Map.MAP_OBSTACLE)
        {
            a_bw.write('X'); //Obstacle
        }
        else if(m_data[a_x][a_y] == Map.MAP_SIMPLE_BRICK)
        {
            a_bw.write('B'); //Simple brick
        }
        else if(m_data[a_x][a_y] == Map.MAP_QUESTION_BRICK)
        {
            a_bw.write('?'); //Question brick
        }
        //else if(m_data[a_x][a_y] == Map.MAP_POT_OR_CANNON)
        else if(Map.isPotOrCannon(m_data[a_x][a_y]))
        {
            a_bw.write('P'); //Pot or cannon
        }
        else if(m_data[a_x][a_y] == Map.MAP_SOFT_OBSTACLE)
        {
            a_bw.write('~'); //Soft obstacle, climbable.
        }
        else
        {
            //Our flags:

            if(m_data[a_x][a_y] == Map.MAP_ONE_SPACE_FREE_FLAG)
            {
                a_bw.write('a'); //one space with block under
            }
            else if(m_data[a_x][a_y] == Map.MAP_TWO_SPACES_FREE_FLAG)
            {
                a_bw.write('A'); //two spaces with block under
            }
            else
            {
                a_bw.write(' ');
            }
        }
    }

        public void dumpToFileProcessing(BufferedWriter a_bw, int a_x, int a_y, int a_realX) throws Exception
    {
        //SEE ZLevelMapElementGeneralization(byte el, int ZLevel) in ch/idsia/mario/engine/LevelScene.java

        if(m_data[a_x][a_y] == Map.MAP_OBSTACLE)
        {
            a_bw.write("fill(128,64,0);");
            a_bw.write("rect(" + (((a_realX)*10) - 3) + "," + (((15-a_y)*10) - 3) + ",6,6);"); //Obstacle
            a_bw.newLine();

        }
        else if(m_data[a_x][a_y] == Map.MAP_SIMPLE_BRICK)
        {
            a_bw.write("fill(255,128,0);");
            a_bw.write("rect(" + (((a_realX)*10) - 3) + "," + (((15-a_y)*10) - 3) + ",6,6);"); //Obstacle
            a_bw.newLine();
        }
        else if(m_data[a_x][a_y] == Map.MAP_QUESTION_BRICK)
        {
            a_bw.write("fill(255,255,0);");
            a_bw.write("rect(" + (((a_realX)*10) - 3) + "," + (((15-a_y)*10) - 3) + ",6,6);"); //Obstacle
            a_bw.newLine();
        }
        else if( m_data[a_x][a_y] == Map.MAP_FLOWER_POT )
        {
            a_bw.write("fill(0,196,0);");
            a_bw.write("rect(" + (((a_realX)*10) - 3) + "," + (((15-a_y)*10) - 3) + ",6,6);"); //Obstacle
            a_bw.newLine();
        }
        else if(m_data[a_x][a_y] == Map.MAP_CANNON_MUZZLE || m_data[a_x][a_y] == Map.MAP_CANNON_TRUNK)
        {
            a_bw.write("fill(0,0,0);");
            a_bw.write("rect(" + (((a_realX)*10) - 3) + "," + (((15-a_y)*10) - 3) + ",6,6);"); //Obstacle
            a_bw.newLine();
        }
        else if(m_data[a_x][a_y] == Map.MAP_SOFT_OBSTACLE)
        {
            a_bw.write("fill(128,128,0);");
            a_bw.write("rect(" + (((a_realX)*10) - 3) + "," + (((15-a_y)*10) - 3) + ",6,6);"); //Obstacle
            a_bw.newLine();
        }
        /*else
        {
            //Our flags:

            if(m_data[a_x][a_y] == Map.MAP_ONE_SPACE_FREE_FLAG)
            {
                a_bw.write('a'); //one space with block under
            }
            else if(m_data[a_x][a_y] == Map.MAP_TWO_SPACES_FREE_FLAG)
            {
                a_bw.write('A'); //two spaces with block under
            }
            else
            {
                a_bw.write(' ');
            }
        }*/
    }




}
