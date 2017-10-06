/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ChannelServer;

import java.net.*;
import java.io.*;
import java.math.*;

/**
 *
 * @author Marius
 */
public class Lobby
{

    MiscFunctions func = new MiscFunctions();
    String nullbyte = new String(ChannelServer.NULLBYTE);
    ChannelServer server;
    private int users = 0;
    private String[] charnames = new String[300];
    private PrintWriter[] usersocks = new PrintWriter[300];
    private Socket[] socks = new Socket[300];
    private int[] bots = new int[300];
    private int[] status = new int[300];
    //rooms
    private final int PVP = 0;
    private final int SECTOR = 2;
    private final int BASE = 3;
    private final int MODES = 3;
    private final int MAXROOMS = 300;

    /*private String[][] roomname = new String[MODES][MAXROOMS];
    private String[][] roompass = new String[MODES][MAXROOMS];
    private String[][] roomowner = new String[MODES][MAXROOMS];
    private int[][] roomlevel = new int[MODES][MAXROOMS];
    private int[][] roomstatus = new int[MODES][MAXROOMS];
    private int[][] roommap = new int[MODES][MAXROOMS];
    private String[][] roomip = new String[MODES][MAXROOMS];
    private String[][][] roomplayer = new String[MODES][MAXROOMS][8];
    private PrintWriter[][][] roomsocks = new PrintWriter[MODES][MAXROOMS][8];
    private byte[][][][] roomips = new byte[MODES][MAXROOMS][8][4];*/
    private Room[][] rooms = new Room[MODES][MAXROOMS];
    SQLDatabase sql;

    public Lobby(ChannelServer server, SQLDatabase _sql)
    {
        this.sql = _sql;
        this.server = server;
        /*for (int i = 0; i < MODES; i++)
        {
        for (int i2 = 0; i2 < MAXROOMS; i2++)
        {
        roomname[i][i2]="";
        roompass[i][i2]="";
        roomowner[i][i2]="";
        roomlevel[i][i2] = -1;
        roomstatus[i][i2] = -1;
        roommap[i][i2] = -1;
        roomip[i][i2] = "";
        for (int i3 = 0; i3 < 8; ++i3)
        this.roomplayer[i][i2][i3] = "";
        }
        }*/
        debug("done");
    }

    protected void debug(String msg)
    {
        Main.debug("[Lobby]", msg);
    }

    public void adduser(String username, int bot, PrintWriter sockout, Socket socket)
    {
        debug("add user " + username);
        int i = 0;
        for (; i <= this.users; i++)
        {
            if (charnames[i] == null || charnames[i].equals(""))
            {
                this.charnames[i] = username;
                this.bots[i] = bot;
                this.usersocks[i] = sockout;
                this.socks[i] = socket;
                this.status[i] = 1;
                debug("user " + username + " added at " + i);
                break;
            }
        }
        debug(Integer.toString(i));
        if (i == this.users)
        {
            this.users++;
        }
        debug("current users(add) " + this.users);
        writelobbyall(username, getaddpacket(username));
    }

    public void createdummy(int anz)
    {
        this.users += anz;
        for (int i = anz; i > 0; i--)
        {
            charnames[this.users - i] = "Dummy" + (this.users - i);
            bots[this.users - i] = (int) Math.random() * 2 + 1;
        }
        writelobbyall("a", getaddpacket("a"));
    }

    public void removeuser(String username)
    {
        int clientnum = getNum(username);
        if (clientnum != -1)
        {
            usersocks[clientnum] = null;
            writelobbyall(username, getdelpacket(username));
            charnames[clientnum] = "";
            bots[clientnum] = 0;
            if ((clientnum + 1) == this.users)
            {
                this.users--;
            }
            debug("current users(remove) " + this.users);
        }
    }

    public Packet getlobbypacket()
    {
        Packet packet = new Packet();

        packet.addHeader((byte) 0xF2, (byte) 0x2E);


        packet.addPacketHead((byte) 0x01, (byte) 0x00);
        packet.addInt(this.users, 2, false);
        for (int i = 0; i < this.users; i++)
        {
            String user = this.charnames[i];
            while (user.length() != 15)
            {
                user += nullbyte;
            }
            packet.addString(user);
            packet.addByte2((byte) (bots[i] & 0xff), (byte) this.status[i]);
        }


        return packet;
    }

    public void writeMessage(String msg, String charna, boolean isgm)
    {
        try
        {

            Packet packet = new Packet();
            packet.addHeader((byte) 0x1A, (byte) 0x27);
            if (isgm)
            {
                byte[] stringbyte = msg.getBytes("ISO8859-1");
                stringbyte[4] = 0x01;
                packet.addString(new String(stringbyte));
            }
            else
            {
                packet.addString(msg);
            }
            String[] packandhead = new String[2];

            packandhead[0] = packet.getHeader();
            packandhead[1] = packet.getPacket();

            int i = 0;
            do
            {
                if (usersocks[i] == null)
                {
                    debug("client nr:" + i + " is empty");
                }
                else
                {
                    usersocks[i].write(packandhead[0]);
                    usersocks[i].flush();
                    usersocks[i].write(packandhead[1]);
                    usersocks[i].flush();
                    debug("send");
                }
                i++;
            } while (i < this.users);
        } catch (Exception e)
        {
        }
    }

    public String[] getaddpacket(String chname)
    {
        int num = getNum(chname);
        Packet packet = new Packet();

        packet.addHeader((byte) 0x27, (byte) 0x27);
        packet.addPacketHead((byte) 0x01, (byte) 0x00);
        packet.addString(chname);
        packet.addByte((byte) 0x00);
        while (packet.getLen() != 17)
        {
            packet.addByte((byte) 0xCC);
        }

        packet.addByte2((byte) (bots[num] & 0xff), (byte) this.status[num]);

        String[] packandhead = new String[2];

        packandhead[0] = packet.getHeader();
        packandhead[1] = packet.getPacket();

        packet.clean();

        return packandhead;
    }

    public String[] getdelpacket(String chname)
    {
        int num = getNum(chname);
        Packet packet = new Packet();

        packet.addHeader((byte) 0x27, (byte) 0x27);
        packet.addPacketHead((byte) 0x01, (byte) 0x00);
        packet.addString(chname);
        packet.addByte((byte) 0x00);
        while (packet.getLen() != 17)
        {
            packet.addByte((byte) 0xCC);
        }

        packet.addByte2((byte) (bots[num] & 0xff), (byte) 0xFF);

        String[] packandhead = new String[2];

        packandhead[0] = packet.getHeader();
        packandhead[1] = packet.getPacket();

        packet.clean();

        return packandhead;
    }

    public void writelobbyall(String chname, String[] spacket)
    {
        String[] packet = new String[2];
        packet = spacket;


        int i = 0;
        do
        {
            if (usersocks[i] != null && !charnames[i].equals(chname))
            {
                usersocks[i].write(packet[0]);
                usersocks[i].flush();
                usersocks[i].write(packet[1]);
                usersocks[i].flush();
                debug("send to " + charnames[i]);
            }
            i++;
        } while (i < this.users + 1);
    }

    public void LobbyMsg(String msg, int color)
    {

        Packet packet = new Packet();
        packet.addHeader((byte) 0x1A, (byte) 0x27);

        packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
        packet.addInt(color, 2, false);
        packet.addString("[Server] " + msg);
        packet.addByte((byte) 0x00);

        String[] packandhead = new String[2];

        packandhead[0] = packet.getHeader();
        packandhead[1] = packet.getPacket();

        for (int i = 0; i < this.users; i++)
        {
            if (usersocks[i] == null)
            {
                debug("client nr:" + i + " is empty");
            }
            else
            {
                usersocks[i].write(packandhead[0]);
                usersocks[i].flush();
                usersocks[i].write(packandhead[1]);
                usersocks[i].flush();
            }

        }
    }

    public int getNum(String player)
    {
        for (int i = 0; i < this.users; i++)
        {
            if (this.charnames[i].equals(player))
            {
                return i;
            }
        }
        return -1;
    }

    public int kickPlayer(String player, String reson)
    {
        int num = getNum(player);
        if (num != -1)
        {
            if (usersocks[num] != null)
            {
                Packet pack = new Packet();
                pack.addHeader((byte) 0x0A, (byte) 0x2F);
                pack.addInt(1, 2, false);
                String[] packandhead = new String[2];
                packandhead[0] = pack.getHeader();
                packandhead[1] = pack.getPacket();
                usersocks[num].write(packandhead[0]);
                usersocks[num].flush();
                usersocks[num].write(packandhead[1]);
                usersocks[num].flush();
                usersocks[num].close();
                this.server.remove(socks[num].getRemoteSocketAddress());
                this.LobbyMsg(reson, 2);
                this.removeuser(player);
                return 1;
            }
            else
            {
                this.removeuser(player);
                this.LobbyMsg(reson, 2);
                return 1;
            }
        }
        else
        {
            return 0;
        }
    }

    public void whisper(String packet, String sender)
    {
        Packet pack = new Packet();
        String[] packs = new String[2];
        pack.setPacket(packet);
        pack.removeHeader();
        int len = pack.getInt(2);
        String recvUser = pack.getString(0, 15, false);
        String message = pack.getString(0, pack.getLen(), false);
        pack.clean();
        if (func.compareChat(message, sender, true, false) == -1)
        {
            kickPlayer(sender, "Player " + sender + " has been kick for wrong chatname(hacking)");
        }
        else
        {
            pack.addHeader((byte) 0x2B, (byte) 0x2F);
            int num = getNum(recvUser);
            if (num == -1)
            {
                pack.addByte4((byte) 0x00, (byte) 0x6B, (byte) 0x00, (byte) 0x00);
                packs[0] = pack.getHeader();
                packs[1] = pack.getPacket();
                num = getNum(sender);
                usersocks[num].write(packs[0]);
                usersocks[num].flush();
                usersocks[num].write(packs[1]);
                usersocks[num].flush();
            }
            else
            {
                pack.addByte2((byte) 0x01, (byte) 0x00);
                pack.addInt(len, 2, false);
                pack.addString(message);
                pack.addByte((byte) 0x00);
                packs[0] = pack.getHeader();
                packs[1] = pack.getPacket();
                debug("test " + packs[1]);
                usersocks[num].write(packs[0]);
                usersocks[num].flush();
                usersocks[num].write(packs[1]);
                usersocks[num].flush();
                num = getNum(sender);
                usersocks[num].write(packs[0]);
                usersocks[num].flush();
                usersocks[num].write(packs[1]);
                usersocks[num].flush();
            }
        }
    }

    public int addroom(int mode, String cname, String cpass, String owner, int masterlvl, String ip, PrintWriter socks, BotClass bot)
    {
        for (int i = 0; i <= MAXROOMS; i++)
        {
            if (this.rooms[mode][i] == null || this.rooms[mode][i].isEmpty())
            {
                int[] room =
                {
                    mode, i
                };
                this.rooms[mode][i] = new Room(room, cname, cpass, masterlvl, owner, ip, socks, bot, sql);

                //writelobbyall(owner, getaddpacketroom(room));
                return i;
            }
        }
        return -1;
    }

    public Packet addRoomPlayer(int[] room, String pass, String ip, PrintWriter socks, BotClass bot)
    {
        return this.rooms[room[0]][room[1]].addPlayer(bot.getName(), pass, ip, socks, bot);
    }

    public void removeroom(int[] room)
    {
        this.rooms[room[0]][room[1]] = null;
    //writelobbyall("", getdelpacketroom(room));
    }

    public void removeRoomPlayer(int[] room, String player)
    {
        if (this.rooms[room[0]][room[1]].removePlayer(player))
        {
            this.rooms[room[0]][room[1]] = null;
        //writelobbyall("", getdelpacketroom(room));
        }

    }

    public Packet getroompacket(int mode, int page)
    {
        int cmode = 0;
        int firstnr = 0;
        switch (mode)
        {
            case 1:
                cmode = 0;
                mode = SECTOR;
                firstnr = 89;
                break;

            case 0:
                cmode = 1;
                mode = PVP;
                firstnr = 89;
                break;

            case 2:
                cmode = 2;
                mode = BASE;
                firstnr = 89;
                break;

            default:
                cmode = 0;
                firstnr = 89;
                break;
        }

        Packet packet = new Packet();
        packet.addHeader((byte) 0xEF, (byte) 0x2E);
        packet.addPacketHead((byte) 0x01, (byte) 0x00);

        for (int i = 0; i < 6; i++)
        {
            if (this.rooms[cmode][i + (6 * page)] != null)
            {
                packet.addByte((byte) ((i + (6 * page)) + firstnr));
                packet.addByte((byte) mode);
                String cname, cpass;

                cname = this.rooms[cmode][i + (6 * page)].getName();
                while (cname.length() != 27)
                {
                    cname += nullbyte;
                }

                cpass = this.rooms[cmode][i + (6 * page)].getPass();
                while (cpass.length() != 11)
                {
                    cpass += nullbyte;
                }

                packet.addString(cname);
                packet.addString(cpass);
                packet.addByte((byte) 0x02);
                packet.addByte((byte) 0x08);
                packet.addByte((byte) this.rooms[cmode][i + (6 * page)].getStatus());
                packet.addByte((byte) this.rooms[cmode][i + (6 * page)].getLevel());
                packet.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
            }
            else
            {
                for (int z = 0; z < 48; z++)
                {
                    packet.addByte((byte) 0x00);
                }
            }
        }

        return packet;
    }

    public String[] getaddpacketroom(int[] room)
    {
        Packet packet = new Packet();

        packet.addHeader((byte) 0x27, (byte) 0x27);
        packet.addPacketHead((byte) 0x01, (byte) 0x00);



        String[] packandhead = new String[2];

        packandhead[0] = packet.getHeader();
        packandhead[1] = packet.getPacket();

        packet.clean();

        return packandhead;
    }

    public String[] getdelpacketroom(int[] room)
    {
        Packet packet = new Packet();

        packet.addHeader((byte) 0x27, (byte) 0x27);
        packet.addPacketHead((byte) 0x01, (byte) 0x00);

        String[] packandhead = new String[2];

        packandhead[0] = packet.getHeader();
        packandhead[1] = packet.getPacket();

        packet.clean();

        return packandhead;
    }

    public int[] haveRoom(String user)
    {
        int ret[] = new int[2];
        ret[0] = -1;
        ret[1] = -1;
        for (int i = 0; i < this.MODES; i++)
        {
            for (int i2 = 0; i2 < this.MAXROOMS; i2++)
            {
                if (this.rooms[i][i2] != null && this.rooms[i][i2].getOwner().equals(user))
                {
                    ret[0] = i;
                    ret[1] = i2;
                    return ret;
                }
            }
        }
        return ret;
    }

    public int getRoomMap(int[] room)
    {
        return this.rooms[room[0]][room[1]].getMap();
    }

    public void setRoomMap(int[] room, int map)
    {
        this.rooms[room[0]][room[1]].setMap(map);
    }

    public void startRoom(int[] room, int map)
    {
        this.rooms[room[0]][room[1]].startRoom();
    }

    public void setStatus(String user, int what)
    {
        int num = this.getNum(user);
        this.status[num] = what;
        writelobbyall(user, getaddpacket(user));
    }

    public int getSlot(int[] room, String user)
    {
        return this.rooms[room[0]][room[1]].getSlot(user);
    }

    public Packet getConnectPacket(int[] room)
    {
        return this.rooms[room[0]][room[1]].getConnectedPacket();
    }

    public int getOwnerPort(int[] room)
    {
        return this.rooms[room[0]][room[1]].getOwnerPort();
    }

    public void setRoomStatus(int[] room, int slot)
    {
        this.rooms[room[0]][room[1]].changeStatus(slot);
    }

    public void readyToPlay(int[] room, String name)
    {
        this.rooms[room[0]][room[1]].readyToPlay(name);
    }

    public void isConnected(int[] room, String name, int port)
    {
        this.rooms[room[0]][room[1]].isConnected(name, port);
    }

    public void roomDied(int[] room, String name)
    {
        this.rooms[room[0]][room[1]].died(name);
    }

    public void roomMonsterDead(int[] room, int typ, int num, int killedby)
    {
        this.rooms[room[0]][room[1]].monsterKilled(typ, num, killedby);
    }

    public void useItem(int[] room, String who, int typ, int num)
    {
        this.rooms[room[0]][room[1]].useItem(who, typ, num);
    }

    public void killRoom(int[] room)
    {
        this.rooms[room[0]][room[1]] = null;
    }
}
