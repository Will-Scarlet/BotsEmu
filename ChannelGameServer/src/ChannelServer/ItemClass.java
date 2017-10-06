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
public class ItemClass {
    private String ip;
    private String botname;
    private SQLDatabase sql;


    public ItemClass(SQLDatabase _sql) {
        this.sql = _sql;
    }

    public String getItemName(int id)
    {
        try {
        ResultSet rs = sql.doquery("SELECT name FROM bout_items WHERE id="+ id +" LIMIT 1");
        if (rs.next())
        {
            return rs.getString("name");
        }
        return null;
        } catch (Exception e){

        }
        return null;
    }

    public int getItemId(String name)
    {
        try {
        ResultSet rs = sql.doquery("SELECT id FROM bout_items WHERE name='"+ name +"' LIMIT 1");
        if (rs.next())
        {
            return rs.getInt("id");
        }
        return 0;
        } catch (Exception e){

        }
        return 0;
    }

public String[] getItemIdLike(String name)
    {
        try {
            int i = 0;
            String[] ret = new String[6];
            ResultSet rs = sql.doquery("SELECT id,name FROM bout_items WHERE name LIKE '" + name + "%'");
            while (rs.next())
            {
                if (i < 5) {
                    ret[i] = "" + rs.getInt("id") + " - " + rs.getString("name");
                }
                i++;
            }
            if (i > 0)
            {
                ret[5] = Integer.toString(i);
                return ret;
            }
            return null;
        } catch (Exception e) {
        }
        return null;
    }

    public ResultSet getItemShopInfos(int id)
    {
        try {
        String ret = new String();
        ResultSet rs = sql.doquery("SELECT * FROM bout_items WHERE id="+ id +" LIMIT 1");
        if (rs.next())
        {
            return rs;
        }
        return null;
        } catch (Exception e){

        }
        return null;
    }

    public int getSell(int id)
    {
        try {
        ResultSet rs = sql.doquery("SELECT sell FROM bout_items WHERE id="+ id +" LIMIT 1");
        if (rs.next())
        {
                return rs.getInt("sell");
        }
        return -1;
        } catch (Exception e){

        }
        return -1;
    }

    public int getBuy(int id)
    {
        try {
        ResultSet rs = sql.doquery("SELECT buy, buyable FROM bout_items WHERE id="+ id +" LIMIT 1");
        if (rs.next())
        {
            if(rs.getInt("buyable") == 1)
                return rs.getInt("buy");
        }
        return -1;
        } catch (Exception e){

        }
        return -1;
    }

    public int getBuyCoins(int id)
    {
        try {
        ResultSet rs = sql.doquery("SELECT coins, buyable FROM bout_items WHERE id="+ id +" LIMIT 1");
        if (rs.next())
        {
            if(rs.getInt("buyable") == 1)
                return rs.getInt("coins");
        }
        return -1;
        } catch (Exception e){

        }
        return -1;
    }

    public int getCoinDays(int id)
    {
        try {
        ResultSet rs = sql.doquery("SELECT day FROM bout_items WHERE id="+ id +" LIMIT 1");
        if (rs.next())
        {
                return rs.getInt("day");
        }
        return -1;
        } catch (Exception e){

        }
        return -1;
    }

    public String getItemScript(int id)
    {
        try {
        ResultSet rs = sql.doquery("SELECT script FROM bout_items WHERE id="+ id +" LIMIT 1");
        if (rs.next())
        {
            return rs.getString("script");
        }
        return null;
        } catch (Exception e){

        }
        return null;
    }

    public ResultSet getItemInfo(int id)
    {
        ResultSet rs = sql.doquery("SELECT reqlevel, bot, part FROM bout_items WHERE id="+ id +" LIMIT 1");
        return rs;
    }

}

