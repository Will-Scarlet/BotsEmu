/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ChannelServer;

/**
 *
 * @author Marius
 */
public class MiscFunctions
{

    protected void debug(String msg)
    {
        Main.debug("[Function]", msg);
    }

    public int bytetoint(String thestring, int bytec)
    {
        try
        {
            String hex_data_s = "";
            for (int i = bytec - 1; i >= 0; i--)
            {

                int data = thestring.getBytes("ISO8859-1")[i];
                if (data < 0)
                {
                    data += 256;
                }
                String hex_data = Integer.toHexString(data);
                if (hex_data.length() == 1)
                {
                    hex_data_s += "0" + hex_data;
                }
                else
                {
                    hex_data_s += hex_data;
                }
            }
            return Integer.parseInt(hex_data_s, 16);
        } catch (Exception e)
        {
        }
        return 0;
    }

    public static String getbyte(int var, int num)
    {
        StringBuffer tstring = new StringBuffer();
        if (num == 2)
        {
            int b1 = var & 0xff;
            int b2 = (var >> 8) & 0xff;
            tstring.appendCodePoint(b1);
            tstring.appendCodePoint(b2);
            return tstring.toString();

        }
        else
        {
            if (num == 4)
            {
                int b1 = var & 0xff;
                int b2 = (var >> 8) & 0xff;
                int b3 = (var >> 16) & 0xff;
                int b4 = (var >> 24) & 0xff;
                tstring.appendCodePoint(b1);
                tstring.appendCodePoint(b2);
                tstring.appendCodePoint(b3);
                tstring.appendCodePoint(b4);
                return tstring.toString();
            }
        }
        return null;
    }

    public int getcmd(String packet)
    {
        try
        {
            String hex_data_s = "";
            for (int i = 0; i < 2; i++)
            {
                int data = packet.getBytes("ISO8859-1")[i];
                if (data < 0)
                {
                    data += 256;
                }
                String hex_data = Integer.toHexString(data);
                if (hex_data.length() == 1)
                {
                    hex_data_s += "0" + hex_data;
                }
                else
                {
                    hex_data_s += hex_data;
                }
            }
            return Integer.parseInt(hex_data_s, 16);
        } catch (Exception e)
        {
        }
        return 0;
    }

    public int compareChat(String chatpack, String rlcharname, boolean whisper, boolean isgm)
    {
        try
        {
            byte[] stringbyte = chatpack.getBytes("ISO8859-1");
            if (whisper == false){
                if (stringbyte[4] != 0x00 || stringbyte[6] != 0x5B && !isgm)
                {
                    return -1;
                }
            }
            int a = 0;
            while (stringbyte[a] != 0x5B)
            {
                a++;
            }

            int b = a;
            int chstart = b + 1;
            int c = 0;
            while (stringbyte[b] != 0x5D)
            {
                if (c>16)
                {
                    return -1;
                }
                b++;
                c++;
            }

            int chende = b;
            String chname = chatpack.substring(chstart, chende);
            debug(chname + "-  -" + rlcharname);
            if (chname.equals(rlcharname))
            {
                return a;
            }
            else
            {
                return -1;
            }
        } catch (Exception e)
        {
        }
        return 0;
    }

    protected String removenullbyte(String thestring)
    {
        try
        {
        byte[] stringbyte = thestring.getBytes("ISO8859-1");
        int a = 0;
        while(stringbyte[a] != 0x00)
            a++;

        return thestring.substring(0, a);
        } catch (Exception e){

        }
        return null;
    }
}
