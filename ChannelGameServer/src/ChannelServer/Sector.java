/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChannelServer;

/**
 *
 * @author Marius
 */
public class Sector {
    public int Mapmon;

    public int[] getMapMonster(int map)
    {
        switch (map)
        {
            case 0:
            {
                int[] ret = new int[38];
                int i = 0;

                ret[0] = 2;
                ret[1] = 2;
                ret[2] = 2;
                ret[3] = 2;
                ret[4] = 0;
                ret[5] = 0;
                ret[6] = 0;
                ret[7] = 1;
                ret[8] = 0;
                ret[9] = 0;
                ret[10] = 0;
                ret[11] = 0;
                ret[12] = 2;
                ret[13] = 2;
                ret[14] = 4;
                ret[15] = 4;
                ret[16] = 4;
                ret[17] = 2;
                ret[18] = 82;
                ret[19] = 2;
                ret[20] = 2;
                ret[21] = 2;
                ret[22] = 2;
                ret[23] = 0;
                ret[24] = 0;
                ret[25] = 2;
                ret[26] = 2;
                ret[27] = 1;
                ret[28] = 2;
                ret[29] = 2;
                ret[30] = 0;
                ret[31] = 2;
                ret[32] = 0;
                ret[33] = 0;
                ret[34] = 2;
                ret[35] = 0;
                ret[36] = 3;
                ret[37] = 102;
                this.Mapmon = 38;
                return ret;
            }

        }
        return new int[0];
    }
}
