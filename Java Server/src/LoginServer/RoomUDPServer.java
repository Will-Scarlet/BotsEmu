package LoginServer;

import java.net.*;

public class RoomUDPServer extends Thread
{

  public void run()
  {
      try {
    DatagramSocket socket = new DatagramSocket(11011);

    while ( true )
    {

      DatagramPacket packet = new DatagramPacket( new byte[1024], 1024 );
      socket.receive( packet );

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        byte[] data = packet.getData();

      String datan = new String(data,"ISO8859-1");
      if (datan.startsWith("\u00C9\u0000"))
      {
          Main.debug("UDP", "Save port "+port+" of IP "+address.toString().substring(1));
          Main.sql.doupdate("UPDATE `rooms` SET `port`="+port+" WHERE `ip`='"+address.toString().substring(1)+"' AND `port`=0");
      }
                         
    }
  } catch (Exception e){

  }
  }
}