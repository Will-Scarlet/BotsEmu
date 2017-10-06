/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChannelServer;

import java.sql.*;

/**
 *
 * @author Marius
 */
public class BotClass {

    private ItemClass item;
    private MiscFunctions func = new MiscFunctions();

    private String ip = "";

    private int id;
    private String account;
    private String botname = "";
    private int bottype = 0;
    private int exp = 0;
    private int level = 0;
    private int hp = 0, hpb = 0;
    private int attmin = 0, attminb = 0;
    private int attmax = 0, attmaxb = 0;
    private int attmintrans = 0, attmintransb = 0;
    private int attmaxtrans = 0, attmaxtransb = 0;
    private int transgauge = 0, transgaugeb = 0;
    private int crit = 0, critb = 0;
    private int evade = 0, evadeb = 0;
    private int spectrans = 0, spectransb = 0;
    private int speed = 0, speedb = 0;
    private int transdef = 0, transdefb = 0;
    private int transbotatt = 0, transbotattb = 0;
    private int transspeed = 0, transspeedb = 0;
    private int rangeatt = 0, rangeattb = 0;
    private int luk = 0, lukb = 0;
    private int botstract = 0;

    private int[] equipitemspart = new int[3];
    private int[] equipitemsgear = new int[8];
    private int[] equipitemspack = new int[6];
    private int[] equipitemscoin = new int[2];

    private int[] inventitems = new int[10];

    private int[] room = {-1,-1};

    private int gigas = 0;
    private int coins = 0;

    private SQLDatabase sql;

    public BotClass(String accountn, String ipadd, ItemClass itemn, SQLDatabase _sql) {
        this.sql = _sql;
        this.account = accountn;
        this.ip = ipadd;
        this.item = itemn;
    }

    protected void debug(String msg) {
        Main.debug("Botclass (" + this.ip + ")", msg);
    }

    public void loadchar()
    {
        try {
            ResultSet rs = sql.doquery("SELECT * FROM bout_characters WHERE username='"+ this.account +"' LIMIT 1");
            if(rs.next())
            {
                this.id = rs.getInt("id");
                this.botname = rs.getString("name");
                this.bottype = rs.getInt("bot");
                this.exp = rs.getInt("exp");
                this.level = rs.getInt("level");
                this.hp = rs.getInt("hp");
                this.gigas = rs.getInt("gigas");
                this.attmin = rs.getInt("attmin");
                this.attmax = rs.getInt("attmax");
                this.attmintrans = rs.getInt("attmintrans");
                this.attmaxtrans = rs.getInt("attmaxtrans");
                this.transgauge = rs.getInt("transgauge");
                this.crit = rs.getInt("crit");
                this.evade = rs.getInt("evade");
                this.spectrans = rs.getInt("specialtrans");
                this.speed = rs.getInt("speed");
                this.transdef = rs.getInt("transdef");
                this.transbotatt = rs.getInt("transbotatt");
                this.transspeed = rs.getInt("transspeed");
                this.rangeatt = rs.getInt("rangeatt");
                this.luk = rs.getInt("luk");
                this.botstract = rs.getInt("botstract");

                this.equipitemspart[0] = rs.getInt("equiphead");
                this.equipitemspart[1] = rs.getInt("equipbody");
                this.equipitemspart[2] = rs.getInt("equiparm");
                this.equipitemsgear[0] = rs.getInt("equipminibot");
                this.equipitemsgear[1] = rs.getInt("equipgun");
                this.equipitemsgear[2] = rs.getInt("equipefield");
                this.equipitemsgear[3] = rs.getInt("equipwing");
                this.equipitemsgear[4] = rs.getInt("equipshield");
                this.equipitemsgear[5] = rs.getInt("equiparmpart");
                this.equipitemsgear[6] = rs.getInt("equipflag1");
                this.equipitemsgear[7] = rs.getInt("equipflag2");
                this.equipitemspack[0] = rs.getInt("equippassivskill");
                this.equipitemspack[1] = rs.getInt("equipaktivskill");
                this.equipitemspack[2] = rs.getInt("equippack");
                this.equipitemspack[3] = rs.getInt("equiptransbot");
                this.equipitemspack[4] = rs.getInt("equipmerc");
                this.equipitemspack[5] = rs.getInt("equipmerc2");
                this.equipitemscoin[0] = rs.getInt("equipheadcoin");
                this.equipitemscoin[1] = rs.getInt("equipminibotcoin");
            }
            rs = sql.doquery("SELECT * FROM bout_inventory WHERE name='"+ this.botname +"' LIMIT 1");
            if(rs.next())
            {
                for(int i=2; i<12; i++)
                {
                    this.inventitems[i-2] = rs.getInt(i);
                    debug(""+inventitems[i-2]);
                }
            }

            rs = sql.doquery("SELECT coins FROM bout_users WHERE username='"+ this.account +"' LIMIT 1");
            if(rs.next())
            {
                this.coins = rs.getInt("coins");
            }
            debug(""+this.coins);

            debug("Gigas : "+this.gigas+" Botname: "+this.botname+" Bottype :"+this.bottype+" level : "+this.level+" hp : "+this.hp);
            debug("attmin : "+this.attmin+" attmax : "+this.attmax);
        } catch (Exception e)
        {

        }
    }

    protected void loadEquipBonus()
    {
        hpb = 0;
        attminb = 0;
        attmaxb = 0;
        attmintransb = 0;
        attmaxtransb = 0;
        transgaugeb = 0;
        critb = 0;
        evadeb = 0;
        spectransb = 0;
        speedb = 0;
        transdefb = 0;
        transbotattb = 0;
        transspeedb = 0;
        rangeattb = 0;
        lukb = 0;
        for (int i = 0; i<3; i++)
        {
            if (this.equipitemspart[i] != 0)
            {
                String script = item.getItemScript(equipitemspart[i]);
                if (!script.equals(null)){
                    parseScript(script);
                }
            }
        }
        for (int i = 0; i<8; i++)
        {
            if (this.equipitemsgear[i] != 0)
            {
                String script = item.getItemScript(equipitemsgear[i]);
                if (!script.equals(null)){
                    parseScript(script);
                }
            }
        }
        for (int i = 0; i<6; i++)
        {
            if (this.equipitemspack[i] != 0)
            {
                String script = item.getItemScript(equipitemspack[i]);
                if (!script.equals(null)){
                    parseScript(script);
                }
            }
        }
        for (int i = 0; i<2; i++)
        {
            if (this.equipitemscoin[i] != 0)
            {
                String script = item.getItemScript(equipitemscoin[i]);
                if (!script.equals(null)){
                    parseScript(script);
                }
            }
        }
    }

    protected void parseScript(String script)
    {
        try
        {
            while (true) 
            {
                int i = 0;
                while (script.charAt(i) != ';' && i < script.length()-1)
                {
                    i++;
                }
                if (i<5)
                    break;
                String onescript = script.substring(0, i);
                debug(onescript);
                script = script.substring(i + 2);
                int i2 = 0;
                while (onescript.charAt(i2) != ',')
                {
                    i2++;
                }
                String stat = onescript.substring(0, i2);
                int value = Integer.parseInt(onescript.substring(i2 + 1));
                debug(stat+"  "+value);
                parseStat(stat, value);
            }

        }
        catch(Exception e)
        {}
    }

    protected void parseStat(String stat, int value)
    {
        if (stat.equals("hpp"))
        {
            this.hpb += value;
        } else if (stat.equals("attmin"))
        {
            this.attminb += value;
        } else if (stat.equals("attmax"))
        {
            this.attmaxb += value;
        } else if (stat.equals("atttransmin"))
        {
            this.attmintransb += value;
        } else if (stat.equals("atttransmax"))
        {
            this.attmaxtransb += value;
        } else if (stat.equals("transgauge"))
        {
            this.transgaugeb += value;
        } else if (stat.equals("crit"))
        {
            this.critb += value;
        } else if (stat.equals("evade"))
        {
            this.evadeb += value;
        } else if (stat.equals("spectrans"))
        {
            this.spectransb += value;
        } else if (stat.equals("speed"))
        {
            this.speedb += value;
        } else if (stat.equals("transbotdef"))
        {
            this.transdefb += value;
        } else if (stat.equals("transbotatt"))
        {
            this.transbotattb += value;
        } else if (stat.equals("transspeed"))
        {
            this.transspeedb += value;
        } else if (stat.equals("luk"))
        {
            this.lukb += value;
        } else if (stat.equals("rangeatt"))
        {
            this.rangeattb += value;
        }
    }
    
    protected String getbyte(int var, int num)
    {
        try {
        if(num == 2){
            int b1 = var & 0xff;
            int b2 = (var >> 8) & 0xff;
            byte[] varbyte = {(byte)b1,(byte)b2};
            return new String(varbyte,"ISO8859-1");
        } else if(num == 4){
            int b1 = var & 0xff;
            int b2 = (var >> 8) & 0xff;
            int b3 = (var >> 16) & 0xff;
            int b4 = (var >> 24) & 0xff;
            byte[] varbyte = {(byte)b1,(byte)b2,(byte)b3,(byte)b4};
            return new String(varbyte,"ISO8859-1");
        }
        } catch (Exception e){

        }
        return null;
    }

    public String getpacketcinfo()
    {
        try {
        loadEquipBonus();
        String packet = "";

        String packetshead = new String(ChannelServer.PACKETS_HEADER);
        String nullbyte = new String(ChannelServer.NULLBYTE);


        String charname = this.botname;
        while(charname.length() != 15)
            charname += nullbyte;

        String bottyp = getbyte(this.bottype,2);
        String cexp = getbyte(this.exp,4);
        String lvl = getbyte(this.level, 2);
        String chp = getbyte(this.hp+this.hpb, 2);
        String giga = getbyte(this.gigas, 4);
        String attamin = getbyte(this.attmin+this.attminb,2);
        String attamax = getbyte(this.attmax+this.attmaxb,2);
        String attamintrans = getbyte(this.attmintrans+this.attmintransb,2);
        String attamaxtrans = getbyte(this.attmaxtrans+this.attmaxtransb,2);
        String stransgauge = getbyte(this.transgauge+this.transgaugeb,2);
        String scrit = getbyte(this.crit+this.critb,2);
        String sevade = getbyte(this.evade+this.evadeb,2);
        String specialtrans = getbyte(this.spectrans+this.spectransb,2);
        String sspeed = getbyte(this.speed+this.speedb,2);
        String stransdef = getbyte(this.transdef+this.transdefb,2);
        String stransbotatt = getbyte(this.transbotatt+this.transbotattb,2);
        String stransspeed = getbyte(this.transspeed+this.transspeedb,2);
        String rangeatta = getbyte(this.rangeatt+this.rangeattb,2);
        String bluk = getbyte(this.luk+this.lukb,2);
        String bstract = getbyte(this.botstract,4);
        String v8 = getbyte(800,2);


        byte[] emptyitem = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
        byte[] aaa = {(byte)0x01};
        byte[] aaa2 = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};

            packet = packetshead + charname + bottyp + cexp + lvl + chp
                 + attamin + attamax + attamintrans + attamaxtrans
                 + v8 + stransgauge + scrit + sevade
                 + specialtrans + sspeed + stransdef
                 + stransbotatt + stransspeed + rangeatta
                 + bluk + bstract;

        for(int i = 0; i<16;i++)
            packet += nullbyte;

        for(int i=0; i<3; i++)
        {
            if (this.equipitemspart[i] == 0)
            {
                packet += new String(emptyitem);
            }
            else
            {
                packet += getbyte(this.equipitemspart[i], 4);
                packet += new String(aaa2);
            }
            packet += nullbyte;
        }


        packet += new String(aaa);

        for(int i=0; i<10; i++)
        {
            debug("Invent "+i+" "+this.inventitems[i]);
            if(this.inventitems[i] == 0)
                packet += new String(emptyitem);
            else
            {
                packet += getbyte(this.inventitems[i],4);
                packet += new String(aaa2);
            }
            packet += nullbyte;
        }

        packet += giga;
        for(int i = 0; i<12; i++)
        packet += nullbyte;

        String friend = "Lukas";
        while(friend.length() != 15)
            friend += nullbyte;

        //packet += friend;
        //packet += friend;
        //packet += friend;

                for (int i = 0; i < 230; i++)
                {
                    packet += nullbyte;
                }
            

        for(int i=0; i<8; i++)
        {
            if (this.equipitemsgear[i] == 0)
            {
                packet += new String(emptyitem);
            }
            else
            {
                packet += getbyte(this.equipitemsgear[i], 4);
                packet += new String(aaa2);
            }
            packet += nullbyte;
        }

        for(int i=0; i<6; i++)
        {
            if (this.equipitemspack[i] == 0)
            {
                packet += new String(emptyitem);
            }
            else
            {
                packet += getbyte(this.equipitemspack[i], 4);
                packet += new String(aaa2);
            }
            packet += nullbyte;
        }

        for (int i = 0; i<200; i++)
            packet += nullbyte;

        for(int i=0; i<2; i++)
        {
            if (this.equipitemscoin[i] == 0)
            {
                packet += new String(emptyitem);
            }
            else
            {
                packet += getbyte(this.equipitemscoin[i], 4);
                packet += new String(aaa2);
            }
            packet += nullbyte;
        }

        while(packet.length() != 1374) //1374
            packet += nullbyte;

        return packet;
        } catch(Exception e){

        }
        return null;
    }

    public String getName()
    {
        return this.botname;
    }

    public int getBot()
    {
        return this.bottype;
    }

    protected int createbot(String username, String name, int bottype)
    {
        int anwser = sql.doupdate("INSERT INTO `bout_characters` (`username`, `name`, `bot`)"
        +"VALUES ('"+username+"', '"+name+"', "+bottype+")");
        sql.doupdate("INSERT INTO `bout_inventory` (`name`) VALUES ('"+name+"')");
        return anwser;
    }

    public int deleteBot(String charname, String username){
        int anwser = sql.doupdate("DELETE FROM `bout_characters` WHERE `username` = '"+username+"' and `name` = '"+charname+"'");
        return anwser;
    }
    
    protected boolean checkbot()
    {
        try {
        ResultSet rs = sql.doquery("SELECT * FROM bout_characters WHERE username='"+ this.account +"' LIMIT 1");
        if (rs.next())
        {
            return true;
        }
        return false;
        } catch (Exception e){

        }
        return false;
    }

    public int getLevel()
    {
        return this.level;
    }

    public void setLevel(int nlevel)
    {
        this.level = nlevel;
        updateBot();
    }

    public int getGigas()
    {
        return this.gigas;
    }

    public void setGigas(int giga)
    {
        this.gigas = giga;
        updateBot();
    }

    public int getCoins()
    {
        debug("getcoins : "+this.coins);
        return this.coins;
    }

    public void setCoins(int coins)
    {
        this.coins = coins;
        debug("Setcoins : "+this.coins);
        updateCoins();
    }

    public int getInvent(int slot)
    {
        return this.inventitems[slot];
    }

    public void setInvent(int item, int slot)
    {
        this.inventitems[slot] = item;
        updateInvent();
    }

    public int[] getInventAll()
    {
        return this.inventitems;
    }

    public void setInventAll(int[] items)
    {
        this.inventitems = items;
        updateInvent();
    }

    public Packet getInventPacket(int head)
    {
        Packet packet = new Packet();
        packet.addHeader((byte)head, (byte)0x2E);
        packet.addPacketHead((byte)0x01, (byte)0x00);
        packet.addByte((byte)0x01);
        for (int i = 0; i<10; i++)
        {
            packet.addInt(this.inventitems[i], 4, false);
            if (this.inventitems[i] == 0)
                packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
            else
                packet.addByte4((byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF);

            packet.addByte((byte) 0x00);
        }
        packet.addInt(this.gigas, 4, false);
        return packet;
    }

    public void updateBot()
    {
        sql.doupdate("UPDATE `bout_characters` SET " +
                "`bot` = "+this.bottype+"," +
                "`exp` = "+this.exp+"," +
                "`level` = "+this.level+"," +
                "`hp` = "+this.hp+"," +
                "`gigas` = "+this.gigas+"," +
                "`attmin` = "+this.attmin+"," +
                "`attmax` = "+this.attmax+"," +
                "`attmintrans` = "+this.attmintrans+"," +
                "`attmaxtrans` = "+this.attmaxtrans+"," +
                "`specialtrans` = "+this.spectrans+"," +
                "`rangeatt` = "+this.rangeatt+"," +
                "`botstract` = "+this.botstract+
                " WHERE name = '"+this.botname+"'");
    }

    public void updateInvent()
    {
        sql.doupdate("UPDATE `bout_inventory` SET " +
                "`item1` = "+this.inventitems[0]+"," +
                "`item2` = "+this.inventitems[1]+"," +
                "`item3` = "+this.inventitems[2]+"," +
                "`item4` = "+this.inventitems[3]+"," +
                "`item5` = "+this.inventitems[4]+"," +
                "`item6` = "+this.inventitems[5]+"," +
                "`item7` = "+this.inventitems[6]+"," +
                "`item8` = "+this.inventitems[7]+"," +
                "`item9` = "+this.inventitems[8]+"," +
                "`item10` = "+this.inventitems[9]+
                " WHERE name = '"+this.botname+"'");
    }

    public void updateCoins()
    {
        sql.doupdate("UPDATE `bout_users` SET `coins` = "+this.coins+" WHERE username='"+ this.account +"'");
    }

    public int getEquip(int epart, int part)
    {
        switch (epart)
        {
            case 1:
                return this.equipitemspart[part];

            case 2:
                return this.equipitemsgear[part-3];

            case 3:
                return this.equipitemspack[part-11];

            case 4:
                return this.equipitemscoin[part];

            default:
                return -1;
        }
    }

    public void setEquip(int id, int epart, int part)
    {
        switch (epart)
        {
            case 1:
                this.equipitemspart[part] = id;
                break;

            case 2:
                this.equipitemsgear[part-3] = id;
                break;

            case 3:
                this.equipitemspack[part-11] = id;
                break;

            case 4:
                this.equipitemscoin[part] = id;
                break;
        }
        updateEquip();
    }

    public void updateEquip()
    {
        sql.doupdate("UPDATE `bout_characters` SET " +
                "`equiphead` = "+this.equipitemspart[0]+"," +
                "`equipbody` = "+this.equipitemspart[1]+"," +
                "`equiparm` = "+this.equipitemspart[2]+"," +
                "`equipminibot` = "+this.equipitemsgear[0]+"," +
                "`equipgun` = "+this.equipitemsgear[1]+"," +
                "`equipefield` = "+this.equipitemsgear[2]+"," +
                "`equipwing` = "+this.equipitemsgear[3]+"," +
                "`equipshield` = "+this.equipitemsgear[4]+"," +
                "`equiparmpart` = "+this.equipitemsgear[5]+"," +
                "`equipflag1` = "+this.equipitemsgear[6]+"," +
                "`equipflag2` = "+this.equipitemsgear[7]+"," +
                "`equippassivskill` = "+this.equipitemspack[0]+"," +
                "`equipaktivskill` = "+this.equipitemspack[1]+"," +
                "`equippack` = "+this.equipitemspack[2]+"," +
                "`equiptransbot` = "+this.equipitemspack[3]+"," +
                "`equipmerc` = "+this.equipitemspack[4]+"," +
                "`equipmerc2` = "+this.equipitemspack[5]+"," +
                "`equipheadcoin` = "+this.equipitemscoin[0]+"," +
                "`equipminibotcoin` = "+this.equipitemscoin[1]+
                " WHERE name = '"+this.botname+"'");
    }
    
    public Packet equip(int slot, int epart)
    {
        Packet packet = new Packet();
        //packet.addHeader((byte)0xE4, (byte)0x2E);
        switch (epart)
        {
            case 1:
                packet.addHeader((byte) 0xE4, (byte) 0x2E);
                break;
                
            case 2:
                packet.addHeader((byte) 0x19, (byte) 0x2F);
                break;

            case 3:
                packet.addHeader((byte) 0x1B, (byte) 0x2F);
                break;
            
        }
        try
        {
            int aid = this.getInvent(slot);
            if (aid == 0)
            {
                packet.addPacketHead((byte) 0x00, (byte) 0x60);
                return packet;
            }

            ResultSet rs = item.getItemInfo(aid);
            rs.next();
            if (rs.getInt("reqlevel") > this.level)
            {
                packet.addPacketHead((byte) 0x00, (byte) 0x65);
                return packet;
            }
            int bott = rs.getInt("bot");
            if (bott != this.bottype && bott != 0)
            {
                packet.addPacketHead((byte)0x00, (byte)0x60);
                return packet;
            }
            int part = rs.getInt("part")-1;

            if (part == 17)
            {
                epart = 4;
                part = 0;
            }
            else
            if (part == 18)
            {
                epart = 4;
                part = 1;
            }

            int old = this.getEquip(epart, part);

            if (part == 15)
            {
                if (old != 0)
                {
                    int old2 = this.getEquip(epart, part+1);
                    if (old2 == 0){
                        old = 0;
                        part++;
                    }
                }

            }

            if (old != -1)
            {
                if (old == 0)
                {
                    this.setInvent(0, slot);
                    this.setEquip(aid, epart, part);
                    //packet.addPacketHead((byte) 0x01, (byte) 0x00);
                    packet.setPacket(this.getpacketcinfo());
                    return packet;
                }
                this.setInvent(old, slot);
                this.setEquip(aid, epart, part);
                //packet.addPacketHead((byte) 0x01, (byte) 0x00);
                packet.setPacket(this.getpacketcinfo());
                return packet;
            }
            packet.addPacketHead((byte)0x00, (byte)0x60);
            return packet;
        } catch (Exception e)
        {
            debug(e.getMessage());
        }
        packet.addPacketHead((byte)0x00, (byte)0x60);
        return packet;
    }

    public Packet deequip(int slot, int epart)
    {
        Packet packet = new Packet();
        switch (epart)
        {
            case 1:
                packet.addHeader((byte) 0xE5, (byte) 0x2E);
                if (slot == 0 && this.equipitemscoin[0] != 0){
                    epart = 4;
                    slot = 0;
                }

                break;
                
            case 2:
                packet.addHeader((byte) 0x1A, (byte) 0x2F);
                if (slot == 0 && this.equipitemscoin[1] != 0){
                    epart = 4;
                    slot = 1;
                }
                else
                    slot += 3;
                break;

            case 3:
                packet.addHeader((byte) 0x1C, (byte) 0x2F);
                slot += 11;
                break;
            
        }
        try
        {
            int aid = this.getEquip(epart, slot);
            if (aid == 0)
            {
                packet.addPacketHead((byte) 0x00, (byte) 0x60);
                return packet;
            }

            int islot = this.slotAvaible();

            if (islot != -1)
            {
                this.setInvent(aid, islot);
                this.setEquip(0, epart, slot);
                //packet.addPacketHead((byte) 0x01, (byte) 0x00);
                packet.setPacket(this.getpacketcinfo());
                return packet;
            }

            packet.addPacketHead((byte) 0x00, (byte) 0x61);
            return packet;
        } catch (Exception e)
        {
            debug(e.getMessage());
        }
        packet.addPacketHead((byte) 0x00, (byte) 0x60);
        return packet;
    }


    protected int slotAvaible()
    {
        for (int i = 0; i<10; i++) {
            if(this.inventitems[i] == 0)
                return i;
        }
        return -1;
    }
    
    public Packet getEquipByName(String charname)
    {
        try
        {
            Packet packet = new Packet();
            packet.addHeader((byte)0x27, (byte)0x2F);
            packet.addInt(1, 4, false);
            ResultSet rs = sql.doquery("SELECT * FROM bout_characters WHERE name='" + func.removenullbyte(charname) + "' LIMIT 1");
            rs.next();
            int clevel = rs.getInt("level");
            int cbot = rs.getInt("bot");
            packet.addInt(clevel, 2, false);
            packet.addInt(0, 2, false);
            for (int i = 0; i < 11; i++)
            {
                packet.addInt(rs.getInt(i + 24), 4, false);
            }
            packet.addInt(rs.getInt("equipheadcoin"), 4, false);
            packet.addInt(rs.getInt("equipminibotcoin"), 4, false);
            packet.addByte((byte)0x00);
            packet.addByte((byte)cbot);
            packet.addByte((byte)0x01);
            packet.addByte((byte)0x00);
            packet.addString(charname);
            return packet;
        } catch (Exception e)
        {
        }
        return null;
    }

    public int[] getEquipAll()
    {
        int[] ret = new int[19];
        for (int i = 0; i<3; i++)
            ret[i] = this.equipitemspart[i];
        for (int i = 0; i<8; i++)
            ret[i+3] = this.equipitemsgear[i];
        for (int i = 0; i<6; i++)
            ret[i+11] = this.equipitemspack[i];
        for (int i = 0; i<2; i++)
            ret[i+17] = this.equipitemscoin[i];

        return ret;
    }

    public int getAttMin()
    {
        return this.attmin;
    }

    public void setAttMin(int _attmin)
    {
        this.attmin = _attmin;
        updateBot();
    }

    public int getAttMax()
    {
        return this.attmax;
    }

    public void setAttMax(int _attmax)
    {
        this.attmax = _attmax;
        updateBot();
    }

    public int getAttMinTrans()
    {
        return this.attmintrans;
    }

    public void setAttMinTrans(int _attmintrans)
    {
        this.attmintrans = _attmintrans;
        updateBot();
    }

    public int getAttMaxTrans()
    {
        return this.attmaxtrans;
    }

    public void setAttMaxTrans(int _attmaxtrans)
    {
        this.attmaxtrans = _attmaxtrans;
        updateBot();
    }

    public int getHp()
    {
        return this.hp;
    }

    public void setHp(int _hp)
    {
        this.hp = _hp;
        updateBot();
    }

    public int getTransGauge()
    {
        return this.transgauge;
    }

    public void setTransGauge(int _transgauge)
    {
        this.transgauge = _transgauge;
        updateBot();
    }

    public int getCrit()
    {
        return this.crit;
    }

    public void setCrit(int _crit)
    {
        this.crit = _crit;
        updateBot();
    }

    public int getEvade()
    {
        return this.evade;
    }

    public void setEvade(int _evade)
    {
        this.evade = _evade;
        updateBot();
    }

    public int getSpecialTrans()
    {
        return this.spectrans;
    }

    public void setSpecialTans(int _spectrans)
    {
        this.spectrans = _spectrans;
        updateBot();
    }

    public int getSpeed()
    {
        return this.speed;
    }

    public void setSpeed(int _speed)
    {
        this.speed = _speed;
        updateBot();
    }

    public int getTransDef()
    {
        return this.transdef;
    }

    public void setTransDef(int _transdef)
    {
        this.transdef = _transdef;
        updateBot();
    }

    public int getTransBotAtt()
    {
        return this.transbotatt;
    }

    public void setTransBotAtt(int _transbotatt)
    {
        this.transbotatt = _transbotatt;
        updateBot();
    }

    public int getTransSpeed()
    {
        return this.transspeed;
    }

    public void setTransSpeed(int _transspeed)
    {
        this.transspeed = _transspeed;
        updateBot();
    }

    public int getRangeAtt()
    {
        return this.rangeatt;
    }

    public void setRangeAtt(int _rangeatt)
    {
        this.rangeatt = _rangeatt;
        updateBot();
    }

    public int getLuk()
    {
        return this.luk;
    }

    public void setLuk(int _luk)
    {
        this.luk = _luk;
        updateBot();
    }

    public int getAttMinB()
    {
        return this.attminb;
    }

    public int getAttMaxB()
    {
        return this.attmaxb;
    }


    public int getAttMinTransB()
    {
        return this.attmintransb;
    }

    public int getAttMaxTransB()
    {
        return this.attmaxtransb;
    }


    public int getHpB()
    {
        return this.hpb;
    }

    public int getTransGaugeB()
    {
        return this.transgaugeb;
    }

    public int getCritB()
    {
        return this.critb;
    }

    public int getEvadeB()
    {
        return this.evadeb;
    }

    public int getSpecialTransB()
    {
        return this.spectransb;
    }

    public int getSpeedB()
    {
        return this.speedb;
    }

    public int getTransDefB()
    {
        return this.transdefb;
    }

    public int getTransBotAttB()
    {
        return this.transbotattb;
    }


    public int getTransSpeedB()
    {
        return this.transspeedb;
    }

    public int getRangeAttB()
    {
        return this.rangeattb;
    }

    public int getLukB()
    {
        return this.lukb;
    }

    public void setRoom(int[] _room)
    {
        this.room = _room;
    }

    public int[] getRoom()
    {
        return this.room;
    }

}
