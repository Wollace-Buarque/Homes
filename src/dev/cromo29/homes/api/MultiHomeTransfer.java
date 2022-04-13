package dev.cromo29.homes.api;

import dev.cromo29.homes.HomePlugin;
import dev.cromo29.durkcore.SpecificUtils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MultiHomeTransfer extends MySQL {

    /**
     * Código feito para transferir a database de outro plugin (MultiHome), apenas ignore-o.
     */

    private final HomePlugin plugin;

    public MultiHomeTransfer(HomePlugin plugin) {
        this.plugin = plugin;
    }

    public void startTransfer() {

        plugin.getConfig().set("MySQL.Transfer", false);
        plugin.saveConfig();

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + multiHome);
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO `" + table + "` (`owner`, `home`, `date`, `public`, `location`) VALUES (?, ?, ?, ?, ?)");

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                String owner = resultSet.getString("Owner");
                String home = resultSet.getString("Home");
                String world = resultSet.getString("World");
                double x = resultSet.getDouble("X");
                double y = resultSet.getDouble("Y");
                double z = resultSet.getDouble("Z");
                float pitch = resultSet.getFloat("pitch");
                float yaw = resultSet.getFloat("yaw");
                Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);


                if (home.equals(""))
                    home = "[Default]";

                ps2.setString(1, owner);
                ps2.setString(2, home);
                ps2.setLong(3, System.currentTimeMillis());
                ps2.setBoolean(4, false);
                ps2.setString(5, LocationUtil.serializeLocation(loc));
                ps2.execute();

            }

            ps.close();
            ps2.close();

            plugin.load();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
