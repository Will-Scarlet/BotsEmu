package ChannelServer;

import java.io.PrintWriter;
import java.sql.ResultSet;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Marius
 */
public class Room
{

    private int[] roomnum = new int[2];
    private byte roomtyp = -1;
    private String roomname = new String();
    private String roompass = new String();
    private String roomowner = new String();
    private int roomlevel = -1;
    private byte roomstatus = 1;
    private int roommap = -1;
    private String roomip = new String();
    private String[] roomplayer = new String[8];
    private PrintWriter[] roomsocks = new PrintWriter[8];
    private byte[][] roomips = new byte[8][4];
    private BotClass[] bot = new BotClass[8];
    private int[] roomposi = new int[8];
    private int[] roomid = new int[8];
    private int[] roomport = new int[8];
    private boolean[] roomready = new boolean[8];
    private boolean[] roomreadytoplay = new boolean[8];
    private SQLDatabase sql;
    private boolean[] dead = new boolean[8];
    private int[] killcount = new int[8];
    private int[] monster;
    private Sector sector;
    private int lala = 0;
    private int[] items;

    public Room(int[] rnum, String rname, String rpass, int rlvl, String rowner, String rip, PrintWriter osock, BotClass _bot, SQLDatabase _sql)
    {
        try
        {
            this.sql = _sql;
            this.roomnum = rnum;
            this.roomname = rname;
            this.roompass = rpass;
            this.roomlevel = rlvl;
            this.roomowner = rowner;
            this.roomip = rip;
            this.roomplayer[0] = rowner;
            this.roomsocks[0] = osock;
            this.bot[0] = _bot;
            this.roomport[0] = 0;
            this.roomposi[0] = 0x70;
            this.roomid[0] = 0;
            this.roomready[0] = true;
            this.roomreadytoplay[0] = false;

            String[] ip = rip.split("\\.");
            for (int i = 0; i < 4; i++)
            {
                this.roomips[0][i] = (byte) Integer.parseInt(ip[i]);
            }

            switch (roomnum[0])
            {
                case 0:
                    this.roomtyp = 2;
                    this.sector = new Sector();
                    break;

                case 1:
                    this.roomtyp = 0;
                    break;

                case 2:
                    this.roomtyp = 3;
                    break;
            }

            sql.doupdate("INSERT INTO `rooms` (`ip`) VALUES ('" + this.roomip + "')");

            Packet pack = new Packet();
            pack.addHeader((byte) 0xEE, (byte) 0x2E);
            pack.addPacketHead((byte) 0x01, (byte) 0x00);
            pack.addByte((byte) (this.roomnum[1] + 89));
            switch (this.roomnum[0])
            {
                case 0:
                    pack.addByte((byte) 0x02);
                    break;

                case 1:
                    pack.addByte((byte) 0x00);
                    break;

                case 2:
                    pack.addByte((byte) 0x03);
                    break;

            }
            String[] aip = this.roomip.split("\\.");
            pack.addByte4((byte) Integer.parseInt(aip[0]),
                    (byte) Integer.parseInt(aip[1]),
                    (byte) Integer.parseInt(aip[2]),
                    (byte) Integer.parseInt(aip[3]));

            writeRoomPlayer(0, pack);
            pack.clean();

            ResultSet rs;
            while (this.roomport[0] == 0)
            {
                rs = sql.doquery("SELECT `port` FROM `rooms` WHERE `ip`='" + this.roomip + "'");
                if (rs.next())
                    this.roomport[0] = rs.getInt("port");
            }

            sql.doupdate("DELETE FROM `rooms` WHERE `ip` = '"+this.roomip+"' and `port` = '"+this.roomport[0]+"'");




            pack.addHeader((byte) 0x4A, (byte) 0x2F);
            pack.addByte4((byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00);
            writeRoomPlayer(0, pack);
            pack.clean();
            this.roommap = 0;

            pack.addHeader((byte) 0x39, (byte) 0x27);
            pack.addInt(7033, 2, true);
            for (int i = 0; i < 7; i++)
            {

                pack.addByte2((byte) 0x00, (byte) 0x00);
            }
            writeRoomPlayer(0, pack);

        } catch (Exception e)
        {
        }
    }

    public boolean isEmpty()
    {
        if (this.roomowner.equals(""))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean removePlayer(String player)
    {
        int slot = getSlot(player);
        int a = 0;
        boolean setowner = false;
        for (int i = 0; i < 8; ++i)
        {
            if (this.roomplayer[i] != null && this.roomplayer[i].equals(player))
            {
                if (this.roomowner.equals(player))
                {
                    setowner = true;
                }
                writeRoomPlayer(i, getQuitPacket(slot));
                this.roomplayer[i] = "";
                this.roomsocks[i] = null;
                this.roomready[i] = false;
                this.roomposi[i] = 0;
                this.bot[i] = null;
                this.roomport[i] = -1;
                this.roomid[i] = -1;

                for (int i2 = 0; i2 < 4; ++i2)
                {
                    this.roomips[i][i2] = 0;
                }
            }
            else
            {
                if (this.roomsocks[i] == null)
                {
                    ++a;
                }
            }
        }
        if (a >= 7)
        {
            return true;
        }
        else
        {
            if (!setowner)
            {
                writeRoomAll(getQuitPacket(slot));
                return false;
            }
            for (int i = 0; i < 8; ++i)
            {
                if (this.roomplayer[i] != null && !this.roomplayer[i].equals(""))
                {
                    this.roomowner = this.roomplayer[i];
                    this.roomposi[i] = 0x70;
                    writeRoomAll(getQuitPacket(slot));
                    return false;
                }
            }
        }
        return true;
    }

    public String getName()
    {
        return this.roomname;
    }

    public String getPass()
    {
        return this.roompass;
    }

    public int getLevel()
    {
        return this.roomlevel;
    }

    public int getStatus()
    {
        return this.roomstatus;
    }

    public String getOwner()
    {
        return this.roomowner;
    }

    public int getMap()
    {
        return this.roommap;
    }

    public void setMap(int map)
    {
        this.roommap = map;
        Packet packet = new Packet();
        packet.addHeader((byte) 0x4A, (byte) 0x2F);
        packet.addPacketHead((byte) 0x01, (byte) 0x00);
        packet.addInt(map, 2, false);
        writeRoomAll(packet);
    }

    protected void writeRoomAll(Packet pack)
    {
        for (int i = 0; i < 8; i++)
        {
            Packet packet = pack;
            if (this.roomsocks[i] != null)
            {
                this.roomsocks[i].write(packet.getHeader());
                this.roomsocks[i].flush();
                this.roomsocks[i].write(packet.getPacket());
                this.roomsocks[i].flush();
            }
        }
    }

    protected void writeRoomAllExcept(int num, Packet packet)
    {
        for (int i = 0; i < 8; i++)
        {
            if (this.roomsocks[i] != null && i != num)
            {
                this.roomsocks[i].write(packet.getHeader());
                this.roomsocks[i].flush();
                this.roomsocks[i].write(packet.getPacket());
                this.roomsocks[i].flush();
            }
        }
    }

    protected void writeRoomPlayer(int num, Packet packet)
    {
        this.roomsocks[num].write(packet.getHeader());
        this.roomsocks[num].flush();
        this.roomsocks[num].write(packet.getPacket());
        this.roomsocks[num].flush();
    }

    public void startRoom()
    {
        for (int i = 0; i < 8; i++)
        {
            if (this.roomsocks[i] != null && !this.roomready[i])
            {
                for (int i2=0; i2<8; i2++)
                    this.killcount[i2] = 0;
                Packet packet = new Packet();
                packet.addHeader((byte) 0xF3, (byte) 0x2E);
                packet.addPacketHead((byte) 0x00, (byte) 0x50);
                writeRoomPlayer(getSlot(this.roomowner), packet);
                return;
            }
        }

        this.roomstatus = 3;

        if (this.roomtyp == 2)
            this.monster = sector.getMapMonster(this.roommap);

        for (int i = 0; i < 8; i++)
        {
            if (this.roomsocks[i] != null)
            {
                Packet packet = new Packet();
                packet.addHeader((byte) 0xF3, (byte) 0x2E);
                packet.addPacketHead((byte) 0x01, (byte) 0x00);
                packet.addByte((byte) (roomnum[1] + 89));
                packet.addByte(this.roomtyp);
                packet.addInt(this.roommap, 2, false);
                packet.addByte2((byte) 0x03, (byte) 0x00);
                packet.addByte2((byte) (this.roomnum[1]), (byte) this.roomid[i]);
                packet.addByte2((byte) 0x00, (byte) 0x00);
                packet.addByte2((byte) 0x03, (byte) 0x00);
                writeRoomPlayer(i, packet);
            }
        }
    }

    public void readyToPlay(String name)
    {
        for (int i = 0; i<8; i++)
        {
            if (this.roomsocks[i] != null && this.roomplayer[i].equals(name))
            {
                roomreadytoplay[i] = true;
                checkStart();
                return;
            }
        }
    }

    private void checkStart()
    {
        for (int i = 0; i<8; i++)
            if (this.roomsocks[i] != null && !roomreadytoplay[i])
                return;

        Packet packet = new Packet();
        packet.addHeader((byte) 0x24, (byte) 0x2F);
        packet.addByte((byte) 0xCC);
        writeRoomAll(packet);
    }


    public Packet addPlayer(String player, String pass, String rip, PrintWriter socks, BotClass botc)
    {
        try {
        if (!this.roompass.equals(pass)) //wrong pass
        {
            Packet packet = new Packet();
            packet.addHeader((byte) 0x28, (byte) 0x2F);
            packet.addByte2((byte) 0x00, (byte) 0x3E);
            return packet;
        }
        if (this.roomstatus == 3) //already starten
        {
            Packet packet = new Packet();
            packet.addHeader((byte) 0x28, (byte) 0x2F);
            packet.addByte2((byte) 0x00, (byte) 0x3B);
            return packet;
        }
        for (int i = 0; i < 8; i++)
        {
            if (this.roomsocks[i] == null)
            {
                sql.doupdate("INSERT INTO `rooms` (`ip`) VALUES ('" + rip + "')");
                this.roomplayer[i] = player;
                this.bot[i] = botc;
                this.roomposi[i] = 0x50;

                this.roomport[i] = 0;
                this.roomid[i] = i;
                this.roomreadytoplay[i] = false;

                this.roomready[i] = false;

                String[] ip = rip.split("\\.");
                for (int i2 = 0; i2 < 4; i2++)
                {
                    this.roomips[i][i2] = (byte) Integer.parseInt(ip[i2]);
                }
                


                Packet packet = new Packet();
                packet.addHeader((byte) 0x29, (byte) 0x27);
                packet.addString(generateUserPack(i));
                this.writeRoomAll(packet);
                this.roomsocks[i] = socks;

                return getRoomPacket();
            }
        }
        Packet packet = new Packet();
        packet.addHeader((byte) 0x28, (byte) 0x2F);
        packet.addByte2((byte) 0x00, (byte) 0x50);
        return packet;
        } catch (Exception e){

        }
        return null;
    }

    public void isConnected(String name, int port)
    {
        for (int i = 0; i < 8; i++)
        {
            if (this.roomplayer[i].equals(name))
            {
                this.roomport[i] = port;
                this.writeRoomAll(this.getConnectedPacket());
                return;
            }
        }

    }

    private String generateUserPack(int num)
    {
        Packet packet = new Packet();
        packet.addInt(this.bot[num].getLevel(), 2, false);
        int[] equip = this.bot[num].getEquipAll();
        for (int i = 0; i < 19; i++)
        {
            packet.addInt(equip[i], 4, false);
        }
        packet.addByte2((byte) 0x02, (byte) 0x00);
        packet.addByte2((byte) (this.roomnum[1]), (byte) this.roomid[num]);

        for (int i = 0; i < 4; i++)
        {
            packet.addByte(this.roomips[num][i]);
        }

        for (int i = 0; i < 2; i++)
        {
            packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
        }
        packet.addByte2((byte) 0x00, (byte) 0x00);

        if (num == 0)
        //packet.addByte2((byte)27, (byte)(this.roomid[num]+161));
        {
            packet.addInt(this.roomport[num], 2, true);
        }
        else
        {
            packet.addByte2((byte) 0x00, (byte) 0x00);
        }

        for (int i = 0; i < 4; i++)
        {
            packet.addByte(this.roomips[num][i]);
        }

        for (int i = 0; i < 5; i++)
        {
            packet.addByte2((byte) 0x00, (byte) 0x00);
        }

        packet.addByte((byte) this.roomposi[num]);


        for (int i = 0; i < 5; i++)
        {
            packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
        }

        packet.addInt(bot[num].getAttMin()+bot[num].getAttMinB(), 2, false);
        packet.addInt(bot[num].getAttMax()+bot[num].getAttMaxB(), 2, false);
        packet.addInt(bot[num].getAttMinTrans()+bot[num].getAttMinTransB(), 2, false);
        packet.addInt(bot[num].getAttMaxTrans()+bot[num].getAttMaxTransB(), 2, false);
        packet.addInt(bot[num].getHp()+bot[num].getHpB(), 2, false);
        packet.addInt(0, 2, false);
        packet.addInt(bot[num].getTransGauge()+bot[num].getTransGaugeB(), 2, false);
        packet.addInt(bot[num].getCrit()+bot[num].getCritB(), 2, false);
        packet.addInt(bot[num].getEvade()+bot[num].getEvadeB(), 2, false);
        packet.addInt(bot[num].getSpecialTrans()+bot[num].getSpecialTransB(), 2, false);
        packet.addInt(bot[num].getSpeed()+bot[num].getSpeedB(), 2, false);
        packet.addInt(bot[num].getTransDef()+bot[num].getTransDefB(), 2, false);
        packet.addInt(bot[num].getTransBotAtt()+bot[num].getTransBotAttB(), 2, false);
        packet.addInt(bot[num].getTransSpeed()+bot[num].getTransSpeedB(), 2, false);
        packet.addInt(bot[num].getRangeAtt()+bot[num].getRangeAttB(), 2, false);
        packet.addInt(bot[num].getLuk()+bot[num].getLukB(), 2, false);
        packet.addInt(bot[num].getBot(), 2, false);

        packet.addByte((byte) num);
        packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);

        String name = this.roomplayer[num];

        byte[] nullbyteb =
        {
            0x00
        };
        String nullbyte = new String(nullbyteb);

        while (name.length() != 15)
        {
            name += nullbyte;
        }

        packet.addString(name);

        for (int i = 0; i < 21; i++)
        {
            packet.addByte((byte) 0x00);
        }

        return packet.getPacket();

    }

    private Packet getRoomPacket()
    {
        Packet packet = new Packet();
        packet.addHeader((byte) 0x28, (byte) 0x2F);
        packet.addPacketHead((byte) 0x01, (byte) 0x00);
        for (int i = 0; i < 8; i++)
        {
            if (this.roomsocks[i] != null)
            {
                packet.addString(this.generateUserPack(i));
            }
            else
            {
                for (int z = 0; z < 52; z++)
                {
                    packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
                }
            }
        }
        packet.addByte2((byte) (this.roomnum[1] + 89), this.roomtyp);
        String cname = this.roomname;

        byte[] nullbyteb =
        {
            0x00
        };
        String nullbyte = new String(nullbyteb);

        while (cname.length() != 38)
        {
            cname += nullbyte;
        }

        packet.addString(cname);
        packet.addByte2((byte) this.roomtyp, (byte) 0x08);
        packet.addByte2((byte) 0x01, (byte) 0x00);
        packet.addByte2((byte) 0x00, (byte) 0x00);
        packet.addByte((byte) 0x00); //key
        packet.addByte2((byte) 0x00, (byte) 0x00);

        //16 --- locked
        return packet;
    }

    public int getSlot(String user)
    {
        for (int i = 0; i < 8; i++)
        {
            if (this.roomplayer[i].equals(user))
            {
                return i;
            }
        }
        return -1;
    }


    private Packet getQuitPacket(int slot)
    {
        Packet pack = new Packet();
        pack.addHeader((byte) 0x2E, (byte) 0x27);
        pack.addByte4((byte) 0x01, (byte) 0x00, (byte) slot, (byte) 0x01);
        for (int i = 0; i < 8; i++)
        {
            if (this.roomsocks[i] != null)
            {
                pack.addByte((byte) this.roomposi[i]);
            }
            else
            {
                pack.addByte((byte) 0x00);
            }
        }
        return pack;
    }

    public Packet getConnectedPacket()
    {
        Packet packet = new Packet();
        packet.addHeader((byte) 0x39, (byte) 0x27);
        for (int i = 0; i < 8; i++)
        {
            if (this.roomsocks[i] != null)
            {
                packet.addInt(this.roomport[i], 2, true);
            }
            else
            {
                packet.addByte2((byte) 0x00, (byte) 0x00);
            }
        }
        return packet;
    }

    public int getOwnerPort()
    {
        for (int i = 0; i < 8; i++)
        {
            if (this.roomplayer[i] != null && this.roomplayer[i].equals(this.roomowner))
            {
                return this.roomport[i];
            }
        }
        return -1;
    }

    public void changeStatus(int slot)
    {
        if (!this.roomready[slot])
        {
            this.roomready[slot] = true;
        }
        else
        {
            this.roomready[slot] = false;
        }

        Packet packet = new Packet();
        packet.addHeader((byte) 0x20, (byte) 0x2F);
        packet.addInt(slot, 2, false);
        packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00);
        if (this.roomready[slot])
        {
            packet.addByte4((byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00);
        }
        else
        {
            packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
        }

        for (int i = 0; i < 8; i++)
        {
            if (this.roomsocks != null)
            {
                writeRoomPlayer(i, packet);
            }
        }

    }

    public void died(String name)
    {
        for (int i = 0; i<8; i++)
            if (this.roomsocks[i] != null && this.roomplayer[i].equals(name))
                this.dead[i] = true;
    }

    public void monsterKilled(int typ, int num, int killedby)
    {
        try {
        if (this.monster[num] == typ)
        {
            this.monster[num] = -1;
            this.killcount[killedby]++;
            Packet packet = new Packet();
            packet.addHeader((byte)0x25, (byte)0x2F);
            packet.addPacketHead((byte)0x01, (byte)0x00);
            packet.addInt(num, 2, false);
            packet.addByte2((byte)0x00, (byte)0x00);
            if (typ == 2)
            {
                packet.addByte4((byte) 0x01, (byte) 0x00, (byte) 0x01, (byte)0x05);
            }
            else if (typ == 102)
            {
                packet.addByte4((byte) 0x01, (byte) 0x00, (byte) 0x0C, (byte) 0x06);
            }
            else
                packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);



            writeRoomAll(packet);
            
                for (int i = 0; i<sector.Mapmon; i++)
                    if (this.monster[i] != -1)
                    {
                        return;
                    }
            writeRoomMessage("All monster are killed");
            for (int i = 0; i<8; i++)
                if(this.roomsocks[i] != null)
                    writeRoomMessage(this.roomplayer[i]+" : "+this.killcount[i]);

            Thread.sleep(10000);
            for (int i=0; i<8; i++)
                if (this.roomsocks[i] != null)
                    removePlayer(this.roomplayer[i]);

        }
        } catch (Exception e){

        }
    }

    private void writeRoomMessage(String msg)
    {
        Packet packet = new Packet();
        packet.addHeader((byte)0x1A, (byte)0x27);
        packet.addByte4((byte)0x01, (byte)0x00, (byte)0x0F, (byte)0x00);
        packet.addByte2((byte)0x01, (byte)0x00);
        packet.addString("[Room] ");
        packet.addString(msg);
        packet.addByte((byte)0x00);
        writeRoomAll(packet);
    }

    public void useItem(String who, int typ, int num)
    {
        int slot = getSlot(who);
        Packet packet = new Packet();
        packet.addHeader((byte)0x23, (byte)0x2F);
        packet.addInt(slot, 2, false);
        packet.addByte((byte)typ);
        packet.addByte((byte)num);
        writeRoomAll(packet);
    }
}
