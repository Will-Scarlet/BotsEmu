/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package botsitem;
import java.io.*;

/**
 *
 * @author Marius
 */
public class Main {


    public static int getInt(String thestring,int bytec)
    {
        try
        {
        String hex_data_s = "";
        for(int i = bytec-1; i >=0; i--){

        int data = thestring.getBytes("ISO8859-1")[i];
        if(data<0){
            data += 256;
        }
        String hex_data = Integer.toHexString(data);
        if (hex_data.length()==1)
        {
            hex_data_s += "0"+hex_data;
        } else {
            hex_data_s += hex_data;
        }
        }

        return Integer.parseInt(hex_data_s,16);

        } catch (Exception e){

        }
        return 0;
    }
    

    protected static String removenullbyte(String thestring)
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

    public static void main(String[] args) {
        try {
            SQLDatabase sql = new SQLDatabase();
            sql.start();
            FileInputStream fstream = new FileInputStream("item.bin");
            DataInputStream in = new DataInputStream(fstream);

            FileOutputStream fstream2 = new FileOutputStream("test2.txt");
            PrintWriter out = new PrintWriter(fstream2);
            in.readInt();

            int l = 0;

            byte[] memblock = new byte[236];

            while(in.available() != 0){

            l++;
            System.out.println(l);

            for(int i = 0; i<236; i++)
                memblock[i] = in.readByte();
            
            byte[] id = {memblock[0], memblock[1], memblock[2], memblock[3]};

            int itemid = getInt(new String(id,"ISO8859-1"),4);


            
            byte[] itemnameb = new byte[31];

            for(int i = 0; i<31; i++)
                itemnameb[i] = memblock[i+4];

            String itemname = removenullbyte(new String(itemnameb,"ISO8859-1"));


            byte level = memblock[35];


            byte[] buy = {memblock[37], memblock[38], memblock[39], memblock[40]};

            int itembuy = getInt(new String(buy,"ISO8859-1"),4);


            byte[] sell = {memblock[45], memblock[46], memblock[47], memblock[48]};

            int itemsell = getInt(new String(sell,"ISO8859-1"),4);

            byte[] hppb = {memblock[58], memblock[59], memblock[60], memblock[61]};

            int hpp = getInt(new String(hppb,"ISO8859-1"),4);


            byte[] attminb = {memblock[62], memblock[63], memblock[64], memblock[65]};

            int attmin = getInt(new String(attminb,"ISO8859-1"),4);

            byte[] attmaxb = {memblock[66], memblock[67], memblock[68], memblock[69]};

            int attmax = getInt(new String(attmaxb,"ISO8859-1"),4);

            byte[] atttransminb = {memblock[70], memblock[71], memblock[72], memblock[73]};

            int atttransmin = getInt(new String(atttransminb,"ISO8859-1"),4);

            byte[] atttransmaxb = {memblock[74], memblock[75], memblock[76], memblock[77]};

            int atttransmax = getInt(new String(atttransmaxb,"ISO8859-1"),4);

            byte[] transgaugeb = {memblock[78], memblock[79], memblock[80], memblock[81]};

            int transgauge = getInt(new String(transgaugeb,"ISO8859-1"),4);

            byte[] critb = {memblock[82], memblock[83], memblock[84], memblock[85]};

            int crit = getInt(new String(critb,"ISO8859-1"),4);

            byte[] evadeb = {memblock[86], memblock[87], memblock[88], memblock[89]};

            int evade = getInt(new String(evadeb,"ISO8859-1"),4);

            byte[] spectransb = {memblock[90], memblock[91], memblock[92], memblock[93]};

            int spectrans = getInt(new String(spectransb,"ISO8859-1"),4);

            byte[] speedb = {memblock[94], memblock[95], memblock[96], memblock[97]};

            int speed = getInt(new String(speedb,"ISO8859-1"),4);
            
            byte[] transbotdefb = {memblock[98], memblock[99], memblock[100], memblock[101]};

            int transbotdef = getInt(new String(transbotdefb,"ISO8859-1"),4);
            
            byte[] transbotattb = {memblock[102], memblock[103], memblock[104], memblock[105]};

            int transbotatt = getInt(new String(transbotattb,"ISO8859-1"),4);
            
            byte[] transspeedb = {memblock[106], memblock[107], memblock[108], memblock[109]};

            int transspeed = getInt(new String(transspeedb,"ISO8859-1"),4);

            byte[] rangeattb = {memblock[110], memblock[111], memblock[112], memblock[113]};

            int rangeatt = getInt(new String(rangeattb,"ISO8859-1"),4);

            byte[] lukb = {memblock[114], memblock[115], memblock[116], memblock[117]};

            int luk = getInt(new String(lukb,"ISO8859-1"),4);

            byte[] coinsb = {memblock[41], memblock[42], memblock[43], memblock[44]};

            int coins = getInt(new String(coinsb,"ISO8859-1"),4);

            byte[] daysb = {memblock[56], memblock[57]};

            int days = getInt(new String(daysb,"ISO8859-1"),2);

            
            String script = new String();

            if(hpp != 0)
                script += "hpp,"+hpp+"; ";

            if(attmin != 0)
                script += "attmin,"+attmin+"; ";

            if(attmax != 0)
                script += "attmax,"+attmax+"; ";

            if(atttransmin != 0)
                script += "atttransmin,"+atttransmin+"; ";

            if(atttransmax != 0)
                script += "atttransmax,"+atttransmax+"; ";

            if(transgauge != 0)
                script += "transgauge,"+transgauge+"; ";

            if(crit != 0)
                script += "crit,"+crit+"; ";

            if(evade != 0)
                script += "evade,"+evade+"; ";

            if(spectrans != 0)
                script += "spectrans,"+spectrans+"; ";

            if(speed != 0)
                script += "speed,"+speed+"; ";
            
            if(transbotdef != 0)
                script += "transbotdef,"+transbotdef+"; ";
            
            if(transbotatt != 0)
                script += "transbotatt,"+transbotatt+"; ";
            
            if(transspeed != 0)
                script += "transspeed,"+transspeed+"; ";

            if(luk != 0)
                script += "luk,"+luk+"; ";

            if(rangeatt != 0)
                script += "rangeatt,"+rangeatt+"; ";



            int bottype = 0;
            int part = 0;
            int buyable = 0;

            if (Integer.toString(itemid).startsWith("11"))
            {
                if (Integer.toString(itemid).substring(6, 7).equals("0"))
                     buyable = 1;
                bottype = 1;
                part = Integer.parseInt(Integer.toString(itemid).substring(2, 3));
            }
            else
            if (Integer.toString(itemid).startsWith("12"))
            {
                if (Integer.toString(itemid).substring(6, 7).equals("0"))
                     buyable = 1;
                bottype = 2;
                part = Integer.parseInt(Integer.toString(itemid).substring(2, 3));
            }
            else
            if (Integer.toString(itemid).startsWith("13"))
            {
                if (Integer.toString(itemid).substring(6, 7).equals("0"))
                    buyable = 1;
                bottype = 3;
                part = Integer.parseInt(Integer.toString(itemid).substring(2, 3));
            }


            sql.doupdate("INSERT INTO `bout_items` (`id`, `name`, `reqlevel`, `buyable`, `buy`, `sell`, `coins`, `days`, `bot`, `part`, `script`) " +
                    "VALUES ("+itemid+", '"+itemname+"', "+level+", "+buyable+", "+itembuy+", "+itemsell+", "+coins+", "+days+", "+bottype+", "+part+", '"+script+"')");
            }

        } catch (Exception e){
            System.out.println(e);
        }
    }

}
