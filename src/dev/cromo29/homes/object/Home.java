package dev.cromo29.homes.object;

import dev.cromo29.durkcore.util.TXT;
import dev.cromo29.homes.api.MySQL;
import dev.cromo29.homes.HomePlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Home {

    private static final HomePlugin PLUGIN = HomePlugin.get();

    private final String owner, name;
    private Location location;
    private boolean canAcess;
    private long date;

    public Home(String owner, String name, Location location, boolean canAcess, long date) {
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.canAcess = canAcess;
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isCanAcess() {
        return canAcess;
    }

    public void setCanAcess(boolean canAcess) {
        this.canAcess = canAcess;

        PreparedStatement preparedStatement;
        String table = PLUGIN.getConfig().getString("MySQL.Homes");

        try {
            preparedStatement = MySQL.connection.prepareStatement("UPDATE `" + table + "` SET `public` = ? WHERE `owner` = ? AND `home` = ?");

            preparedStatement.setBoolean(1, canAcess);
            preparedStatement.setString(2, owner);
            preparedStatement.setString(3, name);

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException throwables) {
            PLUGIN.log(" <c>Erro ao atualizar a casa de <f>" + owner + "<c>!");
        }
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void delete() {
        PLUGIN.homes.remove(this);

        PreparedStatement preparedStatement;
        String table = PLUGIN.getConfig().getString("MySQL.Homes");

        try {
            preparedStatement = MySQL.connection.prepareStatement("DELETE FROM `" + table + "` WHERE `owner` = ? AND `home` = ?");

            preparedStatement.setString(1, owner);
            preparedStatement.setString(2, name);

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException throwables) {
            PLUGIN.log(" <c>Erro ao deletar a casa de <f>" + owner + "<c>!");
        }
    }

    public void teleport(Player player) {
        if (!player.hasPermission("29Homes.VIP")) {

            TXT.sendMessages(player, "Você vai ser teleportado em 3 segundos.");

            String finalOwner = owner;

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(location);

                    if (!finalOwner.equalsIgnoreCase(player.getName())) {
                        String anotherOwner = " de '<d>" + finalOwner + "<f>'";

                        TXT.sendMessages(player, "Teleportado para a casa <d>" + name + "<f>" + anotherOwner + ".");
                    } else TXT.sendMessages(player, "Teleportado para a casa <d>" + name + "<f>.");

                }
            }.runTaskLater(PLUGIN, 60);

        } else {

            player.teleport(location);

            TXT.sendMessages(player, "Teleportado para a casa <d>" + name + "<f>.");

            if (owner.equalsIgnoreCase(player.getName())) return;

            String anotherOwner = " de '<d>" + owner + "<f>'";

            TXT.sendMessages(player, "Teleportado para a casa <d>" + name + "<f>" + anotherOwner + ".");
        }
    }
}
