/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChannelServer;

/**
 *
 * @author Marius
 */

public class Packet {

    private String packet = "";
    private String header = "";
    private boolean calced = false;

    protected void debug(String msg) {
        Main.debug("Packet", msg);
    }
    
    public int getLen()
    {
        return this.packet.length();
    }

    public void setPacket(String pack)
    {
        this.packet = pack;
    }
    
    public void addHeader(byte b1, byte b2){
        calced = false;
        byte[] headbyte = {b1, b2};
        String head = new String(headbyte);
        this.header = head;
    }

    public void removeHeader()
    {
        this.packet = this.packet.substring(4);
    }
    
    protected void calcHeader()
    {
        this.header += getasByte(this.packet.length(),2);
        calced = true;
    }
    
    public String getHeader()
    {
        if (!calced)
            this.calcHeader();
        try
        {
        byte[] packb = this.header.getBytes("ISO8859-1");
        return new String(packb,"ISO8859-1");
        } catch (Exception e)
        {
        }
        return null;
    }

    public void addPacketHead(byte b1, byte b2)
    {
        try
        {
        byte[] head = {b1,b2};
        this.packet += new String(head,"ISO8859-1");
        }
        catch (Exception e) {
        }
    }


    public void addString(String string)
    {
        this.packet += string;
    }

    public String getString(int start, int end, boolean nulled)
    {
        String thestring = this.packet.substring(start, end);
        this.packet = this.packet.substring(end);
        if(nulled)
            return thestring;
        else
            return removenullbyte(thestring);

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

    public String getasByte(int var, int num)
    {
        try
        {
        if(num == 2){
            int b1 = var & 0xff;
            int b2 = (var >> 8) & 0xff;
            byte[] varbyte = {(byte)b1, (byte)b2};
            return new String(varbyte,"ISO8859-1");

        } else if(num == 4){
            int b1 = var & 0xff;
            int b2 = (var >> 8) & 0xff;
            int b3 = (var >> 16) & 0xff;
            int b4 = (var >> 24) & 0xff;
            byte[] varbyte = {(byte)b1, (byte)b2, (byte)b3, (byte)b4};
            return new String(varbyte,"ISO8859-1");
        }
        } catch (Exception e)
        {
        }
        return null;

    }




    public void addInt(int var, int num, boolean reverse)
    {
        try
        {
        if(num == 2){
            int b1 = var & 0xff;
            int b2 = (var >> 8) & 0xff;

            if (!reverse)
            {
                byte[] varbyte = {(byte)b1,(byte)b2};
                this.packet += new String(varbyte,"ISO8859-1");
            }
            else
            {
                byte[] varbyte = {(byte)b2,(byte)b1};
                this.packet += new String(varbyte,"ISO8859-1");
            }

        } else if(num == 4){
            int b1 = var & 0xff;
            int b2 = (var >> 8) & 0xff;
            int b3 = (var >> 16) & 0xff;
            int b4 = (var >> 24) & 0xff;
            byte[] varbyte = {(byte)b1,(byte)b2,(byte)b3,(byte)b4};
            this.packet += new String(varbyte,"ISO8859-1");
        }
        }
        catch (Exception e) {
        }
    }

    
    
    public int getInt(int bytec)
    {
        try
        {

        String thestring = this.packet.substring(0, bytec);
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
        this.packet = this.packet.substring(bytec);
        return Integer.parseInt(hex_data_s,16);

        } catch (Exception e){

        }
        return 0;
    }

    public void addByte(byte b1)
    {
        try
        {
        byte[] packbyte = {b1};
        this.packet += new String(packbyte,"ISO8859-1");
        }
        catch (Exception e) {
        }
    }

    public void addByte2(byte b1, byte b2)
    {
        try
        {
        byte[] packbyte = {b1, b2};
        this.packet += new String(packbyte,"ISO8859-1");
        }
        catch (Exception e) {
        }
    }

    public void addByte4(byte b1, byte b2, byte b3, byte b4)
    {
        try
        {
        byte[] packbyte = {b1, b2, b3, b4};
        this.packet += new String(packbyte,"ISO8859-1");
        }
        catch (Exception e) {
        }
    }

    public void addByteArray(byte[] bytearr)
    {
        try
        {
        this.packet += new String(bytearr,"ISO8859-1");
        }
        catch (Exception e) {
        }
    }
    
    public String getPacket()
    {
        try 
        {
        byte[] packb = this.packet.getBytes("ISO8859-1");
        return new String(packb,"ISO8859-1");
        } catch (Exception e)
        {
        }
        return null;
    }

    public void clean() {
        this.header = "";
        this.packet = "";
    }


}
