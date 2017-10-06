/*
 * RelayServerConnection.java
 */
package ChannelServer;

import java.io.*;
import java.net.*;
import java.sql.*;

/**
 * RelayServerConnection reads policy requests from a client's socket and writes the server policy.
 */
public class ChannelServerConnection extends Thread
{

    protected Socket socket;
    protected BufferedReader socketIn;
    protected PrintWriter socketOut;
    protected ChannelServer server;
    protected Lobby lobby;
    protected String account;
    protected int bottype;
    protected String ip;
    protected String charname = "";
    protected MiscFunctions func = new MiscFunctions();
    protected boolean firstlog = true;
    protected BotClass bot;
    protected ItemClass item;
    protected Shop shop;
    protected SQLDatabase sql;

    /**
     * Creates a new instance of RelayServerConnection.
     */
    public ChannelServerConnection(Socket socket, ChannelServer server, Lobby _lobby, SQLDatabase _sql)
    {
        this.socket = socket;
        this.server = server;
        this.sql = _sql;
        item = new ItemClass(sql);
        this.ip = Main.getip(socket);
        this.lobby = _lobby;
        debug("" + socket.getLocalSocketAddress());
    }

    public void checkAccount()
    {
        try
        {
            ResultSet rs = Main.sql.doquery("SELECT username FROM bout_users WHERE current_ip='" + Main.getip(socket) + "' LIMIT 1");
            if (rs.next())
            {
                this.account = rs.getString("username");
            }
            if (this.account != null && isbanned(this.account) == 0)
            {
                //Main.sql.doupdate("UPDATE bout_users SET current_ip='' WHERE username='"+this.account+"'");
            }
            else
            {
                account = "a";
            }
        } catch (Exception e)
        {
            debug("Error :" + e);
        }
    }

    /**
     * Roots a debug message to the main application.
     */
    protected void debug(String msg)
    {
        Main.debug("SQLServerConnection (" + this.socket.getRemoteSocketAddress() + ")", msg);
    }

    public SocketAddress getRemoteAddress()
    {
        return this.socket.getRemoteSocketAddress();
    }

    protected int isbanned(String account)
    {
        try
        {

            ResultSet rs = Main.sql.doquery("SELECT banned FROM bout_users WHERE username='" + account + "' LIMIT 1");
            if (rs.next())
            {
                return rs.getInt("banned");
            }
        } catch (Exception e)
        {
        }
        return 0;
    }

    protected String removeheader(String packet)
    {
        return packet.substring(4);
    }

    protected String removenullbyte(String thestring)
    {
        byte[] stringbyte = thestring.getBytes();
        int a = 0;
        while (stringbyte[a] != 0x00)
        {
            a++;
        }
        return thestring.substring(0, a);
    }

    protected void prasecmd(int cmd, String packet)
    {
        try
        {
            Packet pack = new Packet();
            String[] packanwser = new String[2];
            switch (cmd)
            {
                case 0xF82A:
                    debug("parse f8");
                    byte[] spacketb =
                    {
                        0x01, 0x00, 0x01, 0x00
                    };
                    socketOut.write(new String(ChannelServer.CLIENT_NUMBER_HEADER));
                    socketOut.flush();
                    debug("send cnumberhead");
                    socketOut.write(new String(spacketb));
                    socketOut.flush();
                    debug("send cnumberpacket");
                    break;

                case 0xF92A:
                    debug("parse f9");
                    pack.addHeader((byte) 0x28, (byte) 0x27);
                    pack.addInt(1, 2, false);
                    send(pack);
                    pack.clean();
                    if (bot.checkbot())
                    {
                        bot.loadchar();
                        charname = bot.getName();
                        bottype = bot.getBot();
                        socketOut.write(new String(ChannelServer.CHARACTER_INFORMATION_HEADER));
                        socketOut.flush();
                        socketOut.write(bot.getpacketcinfo());
                        socketOut.flush();
                        byte[] bytearr =
                        {
                            (byte) 0x4E, (byte) 0x95, (byte) 0xDD, (byte) 0x29, (byte) 0xCE,
                            (byte) 0x3A, (byte) 0x55, (byte) 0xDB, (byte) 0x20, (byte) 0xB6, (byte) 0xAD,
                            (byte) 0x97, (byte) 0xA6, (byte) 0x5C, (byte) 0xC0, (byte) 0x1C
                        };
                        pack.addHeader((byte) 0x53, (byte) 0x2F);
                        pack.addByteArray(bytearr);
                        send(pack);
                        pack.clean();
                        pack.addHeader((byte) 0x27, (byte) 0x27);
                        pack.addInt(1, 2, false);
                        pack.addString(charname);
                        pack.addByte((byte) 0x00);
                        pack.addByte4((byte) 0xCC, (byte) 0xCC, (byte) 0x01, (byte) 0x01);
                        send(pack);
                        pack.clean();
                        pack.addHeader((byte) 0x4F, (byte) 0x2F);
                        pack.addInt(1, 2, false);
                        pack.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
                        send(pack);
                        pack.clean();

                    }
                    else
                    {
                        socketOut.write(new String(ChannelServer.CHARACTER_INFORMATION_HEADER));
                        socketOut.flush();
                        debug("send cinfohead");
                        byte[] cinfopackbyte =
                        {
                            (byte) 0x00, (byte) 0x35
                        };
                        String cinfopack = new String(cinfopackbyte);
                        cinfopack += ChannelServer.longnullbyte;
                        socketOut.write(cinfopack);
                        socketOut.flush();
                    }
                    debug("send cinfopack");
                    break;

                case 0xFA2A:
                {
                    debug("parse fa");
                    //Packet pack = new Packet();
                    pack.setPacket(packet);
                    pack.removeHeader();
                    int client_num = pack.getInt(2);
                    int bottype = pack.getInt(2);
                    int unknown = pack.getInt(2);
                    String accountname = pack.getString(1, 23, false);
                    String charname = pack.getString(0, 15, false);
                    pack.clean();

                    if (this.account.equals("a"))
                    {
                        byte[] loginhack =
                        {
                            (byte) 0x00, (byte) 0x32
                        };
                        this.socket.getOutputStream().write(ChannelServer.BOT_CREATION_HEADER);
                        this.socket.getOutputStream().flush();
                        this.socket.getOutputStream().write(loginhack);
                        this.socket.getOutputStream().flush();
                        this.finalize();
                    }
                    else
                    {

                        if (!checkexist(charname, accountname))
                        {
                            if (charname.matches("[a-zA-Z0-9.~_!\\x2d]+"))
                            {
                                bot.createbot(accountname, charname, bottype);
                                socketOut.write(new String(ChannelServer.BOT_CREATION_HEADER));
                                socketOut.flush();
                                socketOut.write(new String(ChannelServer.CREATE_BOT_CREATED));
                                socketOut.flush();
                            }
                            else
                            {
                                socketOut.write(new String(ChannelServer.BOT_CREATION_HEADER));
                                socketOut.flush();
                                socketOut.write(new String(ChannelServer.CREATE_BOT_USERNAME_ERROR));
                                socketOut.flush();
                                this.finalize();
                            }
                        }
                        else
                        {
                            socketOut.write(new String(ChannelServer.BOT_CREATION_HEADER));
                            socketOut.flush();
                            socketOut.write(new String(ChannelServer.CREATE_BOT_USERNAME_TAKEN));
                            socketOut.flush();
                            this.finalize();
                        }
                    }
                    break;
                }

                case 0x742B:
                {
                    //Packet packs = new Packet();
                    if (firstlog)
                    {
                        lobby.adduser(this.charname, this.bottype, socketOut, socket);
                        firstlog = false;
                    }
                    //packs = lobby.getlobbypacket();
                    send(lobby.getlobbypacket());
                    // lobby.g
                    send(lobby.getroompacket(1, 0));
                    sendChatMsg("Welcome on EnchantedBots!", 4);
                    sendChatMsg("Have fun :)", 4);

                    this.socketOut.write(new String(ChannelServer.OK_HEADER));
                    this.socketOut.flush();
                    this.socketOut.write(new String(ChannelServer.OK_PACKET));
                    this.socketOut.flush();
                    break;
                }

                case 0x1A27:
                    pack.setPacket(packet);
                    pack.removeHeader();
                    String chatpack = pack.getPacket();
                    int a = func.compareChat(chatpack, this.charname, false, isGM());
                    if (a == -1)
                    {
                        lobby.kickPlayer(this.charname, "Player " + this.charname + " has been kick for wrong chatname(hacking)");
                    }
                    else
                    {
                        chatpack = chatpack.substring((a + this.charname.length() + 3));
                        chatpack = chatpack.substring(0, (chatpack.length() - 1));
                        debug(chatpack);
                        if (chatpack.startsWith("@"))
                        {
                            String command = chatpack.substring(1, chatpack.length());
                            parsechatcmd(command);
                        }
                        else
                        {
                            lobby.writeMessage(pack.getPacket(), this.charname, isGM());
                        }
                    }
                    pack.clean();
                    break;

                case 0x442B:
                    lobby.whisper(packet, this.charname);
                    break;

                case 0x222B:
                    sendBye(1);
                    this.finalize();
                    break;

                case 0xFB2A:
                    int anwser = bot.deleteBot(this.charname, this.account);
                    sendBye(anwser);
                    break;

                case 0x512B:
                    pack.addHeader((byte) 0x37, (byte) 0x2F);
                    pack.addPacketHead((byte) 0x01, (byte) 0x00);
                    pack.addInt(bot.getCoins(), 4, false);
                    send(pack);
                    send(bot.getInventPacket(0xEB));
                    break;

                case 0x022B:
                {
                    pack.setPacket(packet);
                    pack.getString(0, 42, true);
                    int itemid = pack.getInt(4);
                    send(shop.buy(itemid));
                    //Main.sql.doupdate("UPDATE `bout_items` SET `buyable` = 1 WHERE id="+itemid);
                    break;
                }

                case 0x032B:
                {
                    pack.setPacket(packet);
                    pack.getString(0, 42, true);
                    int slotnum = pack.getInt(2);
                    pack.getInt(2);
                    int itemid = pack.getInt(4);
                    send(shop.sell(itemid, slotnum));
                    break;
                }

                case 0x042B:
                {
                    pack.setPacket(packet);
                    pack.getString(0, 42, true);
                    pack.getInt(2);
                    int itemid = pack.getInt(4);

                    send(shop.buycoin(itemid));
                    //Main.sql.doupdate("UPDATE `bout_items` SET `buyable` = 1 WHERE id="+itemid);
                    break;
                }

                case 0xFC2A:
                {
                    pack.setPacket(packet);
                    pack.getString(0, 29, false);
                    int slot = pack.getInt(2);
                    pack.getInt(2);
                    send(bot.equip(slot, 1));
                    break;
                }

                case 0xFD2A:
                {
                    pack.setPacket(packet);
                    pack.getString(0, 29, false);
                    int slot = pack.getInt(2);
                    pack.getInt(2);
                    send(bot.deequip(slot, 1));
                    break;
                }

                case 0x322B:
                {
                    pack.setPacket(packet);
                    pack.getString(0, 6, false);
                    int slot = pack.getInt(2);
                    pack.clean();
                    send(bot.equip(slot, 2));
                    break;
                }

                case 0x332B:
                {
                    pack.setPacket(packet);
                    pack.getString(0, 6, false);
                    int slot = pack.getInt(2);
                    pack.clean();
                    send(bot.deequip(slot, 2));
                    break;
                }

                case 0x342B:
                {
                    pack.setPacket(packet);
                    pack.getString(0, 6, false);
                    int slot = pack.getInt(2);
                    pack.clean();
                    send(bot.equip(slot, 3));
                    break;
                }

                case 0x352B:
                {
                    pack.setPacket(packet);
                    pack.getString(0, 6, false);
                    int slot = pack.getInt(2);
                    pack.clean();
                    send(bot.deequip(slot, 3));
                    break;
                }


                case 0x412B:
                {
                    pack.setPacket(packet);
                    pack.removeHeader();
                    send(bot.getEquipByName(pack.getPacket()));
                    break;
                }

                case 0x0A2B:
                {
                    pack.setPacket(packet);
                    pack.removeHeader();
                    pack.getInt(2);
                    int page = pack.getInt(2);
                    int mode = pack.getInt(2);
                    send(lobby.getroompacket(mode, page));
                    pack.clean();
                    break;
                }

                case 0x092B:
                {
                    pack.setPacket(packet);
                    pack.removeHeader();
                    String cname = pack.getString(0, 27, false);
                    String cpass = pack.getString(0, 10, false);
                    pack.getInt(2);
                    int roommode = pack.getInt(1);
                    switch (roommode)
                    {
                        case 2:
                            roommode = 0;
                            break;

                        case 0:
                            roommode = 1;
                            break;

                        case 3:
                            roommode = 2;
                            break;
                    }
                    int num = lobby.addroom(roommode, cname, cpass, bot.getName(), bot.getLevel(), Main.getip(this.socket), this.socketOut, bot);
                    //lobby.setStatus(bot.getName(),0);
                    int[] room =
                    {
                        roommode, num
                    };
                    bot.setRoom(room);
                    break;
                }

                case 0x652B:
                {
                    int[] room = lobby.haveRoom(bot.getName());
                    if (room[0] == -1)
                    {
                        lobby.kickPlayer(this.charname, "Player " + this.charname + " has been kick for try to change map of not owning room(hacking)");
                    }
                    else
                    {
                        pack.setPacket(packet);
                        pack.removeHeader();
                        pack.getInt(2);
                        lobby.setRoomMap(room, pack.getInt(2));
                        pack.clean();
                    }
                    break;
                }

                case 0x062B:
                {
                    pack.setPacket(packet);
                    pack.removeHeader();
                    int roomnum = pack.getInt(1) - 89;
                    int roommode = pack.getInt(1);
                    switch (roommode)
                    {
                        case 2:
                            roommode = 0;
                            break;

                        case 0:
                            roommode = 1;
                            break;

                        case 3:
                            roommode = 2;
                            break;
                    }
                    String rname = pack.getString(0, 27, false);
                    String rpass = pack.getString(0, 10, false);
                    int[] room =
                    {
                        roommode, roomnum
                    };
                    Packet npacket = lobby.addRoomPlayer(room, rpass, ip, socketOut, bot);
                    if (npacket.getLen() == 1715)
                    {
                        bot.setRoom(room);
                        send(npacket);
                        //lobby.setStatus(bot.getName(),0);
                        ResultSet rs;
                        int port = 0;
                        while (port == 0)
                        {
                            rs = sql.doquery("SELECT `port` FROM `rooms` WHERE `ip`='" + ip + "'");
                            rs.next();
                            port = rs.getInt("port");
                        }

                        sql.doupdate("DELETE FROM `rooms` WHERE `ip` = '" + ip + "' and `port` = '" + port + "'");
                        lobby.isConnected(room, bot.getName(), port);
                    }
                    else
                    {
                        send(npacket);
                    }
                    break;
                }

                case 0x422B:
                {
                    int[] room = bot.getRoom();
                    if (room[0] == -1)
                    {
                        lobby.kickPlayer(this.charname, "Player " + this.charname + " has been kick for try to exit a room while he isn't in a room(hacking)");
                        break;
                    }
                    lobby.removeRoomPlayer(room, bot.getName());
                    break;
                }

                case 0x0B2B:
                {
                    int[] room = lobby.haveRoom(bot.getName());
                    if (room[0] == -1)
                    {
                        lobby.kickPlayer(this.charname, "Player " + this.charname + " has been kick for try to start not owning room(hacking)");
                    }
                    pack.setPacket(packet);
                    pack.removeHeader();
                    pack.getInt(2);
                    int map = pack.getInt(2);
                    int unknown = pack.getInt(2);
                    lobby.startRoom(room, map);
                    pack.clean();
                    break;
                }

                case 0x3E2B:
                {
                    if (bot.getRoom()[0] != -1)
                    {
                        lobby.readyToPlay(bot.getRoom(), bot.getName());
                    }
                    break;
                }

                case 0x392B:
                {
                    int slot = lobby.getSlot(bot.getRoom(), bot.getName());
                    if (slot == -1)
                    {
                        lobby.kickPlayer(this.charname, "Player " + this.charname + " has been kick for try to be ready and not being in a room(hacking)");
                    }
                    lobby.setRoomStatus(bot.getRoom(), slot);
                    break;
                }

                case 0x6F2B:
                {
                    pack.addHeader((byte)0x54, (byte)0x2F);
                    pack.addByte4((byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00);
                    send(pack);
                }

                case 0x3A2B:
                {
                    int[] room = lobby.haveRoom(bot.getName());
                    if (room[0] == -1)
                    {
                        break;
                    }
                    pack.setPacket(packet);
                    pack.removeHeader();
                    int monsternum = pack.getInt(2);
                    int monstertyp = pack.getInt(2);
                    int who = pack.getInt(2);
                    lobby.roomMonsterDead(room, monstertyp, monsternum, who);
                }

                case 0x3C2B:
                {
                    if (bot.getRoom()[0] == -1)
                    {
                        break;
                    }
                    pack.setPacket(packet);
                    pack.removeHeader();
                    pack.getInt(2);
                    int typ = pack.getInt(1);
                    int num = pack.getInt(1);
                    lobby.useItem(bot.getRoom(), bot.getName(), typ, num);
                }



                default:
                    debug("parse unknown packet");
            }
        } catch (Exception e)
        {
            debug("Error aa" + e);
        }
    }

    protected void parsechatcmd(String cmd)
    {
        debug(cmd);
        String rcmd;
        Packet packet = new Packet();
        byte[] bytecmd = cmd.getBytes();
        int i = 0;
        while (bytecmd[i] != 0x20 && i < cmd.length() - 1)
        {
            i++;
        }

        if (i == cmd.length() - 1)
        {
            rcmd = cmd.substring(0, cmd.length());
        }
        else
        {
            rcmd = cmd.substring(0, i);
        }
        debug("rcmd : -" + rcmd + "-");

        if (rcmd.equals("kick") && isGM())
        {
            String chaname = cmd.substring(i + 1);
            if (lobby.kickPlayer(chaname, "Player " + chaname + " has been kicked by " + this.charname) == 0)
            {
                sendChatMsg("Player not found", 2);
            }
        }
        else
        {
            if (rcmd.equals("add") && isGM())
            {
                int anz = Integer.parseInt(cmd.substring(i + 1));
                lobby.createdummy(anz);
            }
            else
            {
                if (rcmd.equals("addroom") && isGM())
                {
                    int anz = Integer.parseInt(cmd.substring(i + 1));
                    lobby.createdummy(anz);
                }
                else
                {
                    if (rcmd.equals("coins"))
                    {
                        int anz = Integer.parseInt(cmd.substring(i + 1));
                        bot.setCoins((bot.getCoins() + anz));
                        sendChatMsg("Current coins : " + bot.getCoins(), 2);
                    }
                    else
                    {
                        if (rcmd.equals("gigas"))
                        {
                            int anz = Integer.parseInt(cmd.substring(i + 1));
                            bot.setGigas((bot.getGigas() + anz));
                            sendChatMsg("Current gigas : " + bot.getGigas(), 2);
                        }
                        else
                        {
                            if (rcmd.equals("delinvent"))
                            {
                                int part = Integer.parseInt(cmd.substring(i + 1));
                                if (part == 0)
                                {
                                    int[] items =
                                    {
                                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                                    };
                                    bot.setInventAll(items);
                                    sendChatMsg("Invetory deleted", 2);
                                }
                                else
                                {
                                    String old = item.getItemName(bot.getInvent(part - 1));
                                    bot.setInvent(0, part - 1);
                                    sendChatMsg("Inventory-item " + old + " deleted.", 2);
                                }

                            }
                            else
                            {
                                if (rcmd.equals("item") && isGM())
                                {
                                    int id = 0;
                                    if (cmd.substring(i + 1).matches("\\d*"))
                                    {
                                        id = Integer.parseInt(cmd.substring(i + 1));
                                    }
                                    else
                                    {
                                        id = item.getItemId(cmd.substring(i + 1));
                                    }
                                    if (id != 0)
                                    {
                                        int slot = shop.slotAvaible();
                                        if (slot != -1)
                                        {
                                            bot.setInvent(id, slot);
                                            sendChatMsg("Item " + item.getItemName(id) + " added at slot " + slot, 2);
                                        }
                                        else
                                        {
                                            sendChatMsg("Your inventory is full!", 2);
                                        }
                                    }
                                    else
                                    {
                                        sendChatMsg("Item not found!", 2);
                                    }
                                }
                                else
                                {
                                    if (rcmd.equals("itemname"))
                                    {
                                        int id = Integer.parseInt(cmd.substring(i + 1));
                                        String name = item.getItemName(id);
                                        if (name != null)
                                        {
                                            sendChatMsg("Found :", 2);
                                            sendChatMsg("- " + name, 2);
                                        }
                                        else
                                        {
                                            sendChatMsg("Item not found!", 2);
                                        }
                                    }
                                    else
                                    {
                                        if (rcmd.equals("itemid"))
                                        {
                                            String name = cmd.substring(i + 1);
                                            String id[] = item.getItemIdLike(name);
                                            if (id != null)
                                            {
                                                int found = Integer.parseInt(id[5]);
                                                int display;
                                                if (found > 5)
                                                {
                                                    display = 5;
                                                }
                                                else
                                                {
                                                    display = found;
                                                }
                                                if (display == 1)
                                                {
                                                    sendChatMsg("Found " + found + " item, displaying " + display + " item.", 2);
                                                }
                                                else
                                                {
                                                    sendChatMsg("Found " + found + " items, displaying " + display + " items.", 2);
                                                }
                                                for (int i2 = 0; i2 < display; i2++)
                                                {
                                                    sendChatMsg("- " + id[i2], 2);
                                                }
                                            }
                                            else
                                            {
                                                sendChatMsg("No item found!", 2);
                                            }
                                        }
                                        else
                                        {
                                            if (rcmd.equals("help"))
                                            {
                                                sendChatMsg("@dummy <amount>          - need gm rights", 2);
                                                sendChatMsg("@kick <charactername     - need gm rights>", 2);
                                                sendChatMsg("@coins <amount>", 2);
                                                sendChatMsg("@gigas <amount>", 2);
                                                sendChatMsg("@delinvent <part/0 for all>", 2);
                                                sendChatMsg("@itemname <itemid>", 2);
                                                sendChatMsg("@itemid <itemname>", 2);
                                                sendChatMsg("@item <itemid/itemname>  - need gm rights", 2);
                                            }
                                            else
                                            {
                                                if (rcmd.equals("refresh"))
                                                {
                                                    send(lobby.getlobbypacket());
                                                    sendChatMsg("Userlist refreshed", 2);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void send(Packet pack)
    {
        String[] packet = new String[2];
        packet[0] = pack.getHeader();
        packet[1] = pack.getPacket();
        this.socketOut.write(packet[0]);
        this.socketOut.flush();
        this.socketOut.write(packet[1]);
        this.socketOut.flush();
    }

    protected void sendBye(int whatpack)
    {
        Packet pack = new Packet();
        String[] packandhead = new String[2];
        switch (whatpack)
        {
            case 1:
                pack.addHeader((byte) 0x0A, (byte) 0x2F);
                pack.addInt(1, 2, false);
                packandhead[0] = pack.getHeader();
                packandhead[1] = pack.getPacket();
                this.socketOut.write(packandhead[0]);
                this.socketOut.flush();
                this.socketOut.write(packandhead[1]);
                this.socketOut.flush();
                break;

            case 2:
                pack.addHeader((byte) 0xE3, (byte) 0x2E);
                pack.addInt(1, 2, false);
                packandhead[0] = pack.getHeader();
                packandhead[1] = pack.getPacket();
                this.socketOut.write(packandhead[0]);
                this.socketOut.flush();
                this.socketOut.write(packandhead[1]);
                this.socketOut.flush();
                break;
        }
    }

    protected boolean isGM()
    {
        if (this.account.equals("auron") || this.account.equals("auron3") || this.account.equals("kevinwagner"))
        {
            return true;
        }
        return false;
    }

    protected void sendChatMsg(String msg, int color)
    {
        Packet pack = new Packet();

        pack.addHeader((byte) 0x1A, (byte) 0x27);

        pack.addByte4((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00);
        pack.addInt(color, 2, false);
        pack.addString(msg);
        pack.addByte((byte) 0x00);

        String[] packandhead = new String[2];

        packandhead[0] = pack.getHeader();
        packandhead[1] = pack.getPacket();

        this.socketOut.write(packandhead[0]);
        this.socketOut.flush();
        this.socketOut.write(packandhead[1]);
        this.socketOut.flush();

    }

    protected boolean checkexist(String charname, String account)
    {
        try
        {

            ResultSet rs = Main.sql.doquery("SELECT username FROM bout_characters WHERE name='" + charname + "' LIMIT 1");
            if (rs.next())
            {
                String username = rs.getString("username");
                debug(username);
                return true;
            }
            rs = Main.sql.doquery("SELECT name FROM bout_characters WHERE username='" + account + "' LIMIT 1");
            if (rs.next())
            {
                String name = rs.getString("name");
                debug(name);
                return true;
            }
            rs = Main.sql.doquery("SELECT * FROM bout_users WHERE username='" + account + "' LIMIT 1");
            if (rs.next())
            {
                return false;
            }
            else
            {
                return true;
            }
        } catch (Exception e)
        {
        }
        return false;
    }

    protected String read()
    {
        StringBuffer buffer = new StringBuffer();
        int codePoint;

        try
        {
            debug("start read");
            for (int i = 0; i < 4; i++)
            {
                codePoint = this.socketIn.read();
                if (codePoint == 0)
                {
                    String nulls = new String(ChannelServer.NULLBYTE);
                    buffer.append(nulls);
                }
                else
                {
                    if (Character.isValidCodePoint(codePoint))
                    {
                        buffer.appendCodePoint(codePoint);
                    }
                }
            }
            int plen = func.bytetoint(buffer.toString().substring(2), 2);

            if (plen > 1)
            {

                for (int i = 0; i < plen; i++)
                {
                    codePoint = this.socketIn.read();
                    if (codePoint == 0)
                    {
                        String nulls = new String(ChannelServer.NULLBYTE);
                        buffer.append(nulls);
                    }
                    else
                    {
                        if (Character.isValidCodePoint(codePoint))
                        {
                            buffer.appendCodePoint(codePoint);
                        }
                    }
                }
                debug("end read");
            }
        } catch (Exception e)
        {
            debug("Error (read): " + e.getMessage());
            this.server.remove(this.getRemoteAddress());
            return null;
        }

        return buffer.toString();
    }

    /**
     * Create a reader and writer for the socket and call readPolicyRequest.
     */
    public void run()
    {
        try
        {
            this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "ISO8859-1"));
            this.socketOut = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), "ISO8859-1"));
            checkAccount();
            bot = new BotClass(this.account, this.ip, this.item, sql);
            shop = new Shop(bot, item);
            String packet;
            while ((packet = read()) != null)
            {
                debug("main");
                prasecmd(func.getcmd(packet), packet);
            }
        } catch (Exception e)
        {
            debug("Exception (runhzhz): " + e.getMessage());
        }
        debug("bye");
        this.finalize();
    }

    /**
     * Closes the reader, the writer and the socket.
     */
    protected void finalize()
    {
        try
        {
            if (!this.charname.equals(""))
            {
                int[] room = bot.getRoom();
                if (room[0] != -1)
                {
                    lobby.removeRoomPlayer(room, bot.getName());
                }
                lobby.removeuser(charname);
                debug("remove charname " + charname);
                this.charname = "";
            }
            this.server.remove(this.getRemoteAddress());
            this.socketIn.close();
            this.socketOut.close();
            this.socket.close();
        } catch (Exception e)
        {
            debug("Exception (finalize): " + e.getMessage());
        }
    }
}
