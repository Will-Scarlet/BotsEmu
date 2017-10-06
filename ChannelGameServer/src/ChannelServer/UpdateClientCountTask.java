/*
 * UpdateClientCountTask.java
 * This file checks how many clients are waiting for login information.
 */

package ChannelServer;

import java.util.*;
import javax.swing.*;

/**
 * UpdateClientCountTask updates the amount of connected clients.
 */
public class UpdateClientCountTask extends TimerTask {
    protected int count;

    
    /**
     * Updates the label with the number of connected clients.
     */
    public void run() {
        count = Main.channelServer.getClientCount();
        String msg = count + " client" + ((count != 1) ? "s" : "");
        Main.gui.setClientCount(msg);
    }   
}
