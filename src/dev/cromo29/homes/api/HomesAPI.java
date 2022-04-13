package dev.cromo29.homes.api;

import dev.cromo29.homes.HomePlugin;
import dev.cromo29.homes.object.Home;
import dev.cromo29.durkcore.SpecificUtils.LocationUtil;
import dev.cromo29.durkcore.Util.TXT;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomesAPI extends MySQL {

    private static final HomePlugin PLUGIN = HomePlugin.get();

    private static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) openConnection();

        return connection;
    }

    private static void addHome(Home home) {
        List<Home> ownerHomes = PLUGIN.homes.getOrDefault(home.getOwner().toLowerCase(), new ArrayList<>());
        ownerHomes.add(home);

        PLUGIN.homes.put(home.getOwner().toLowerCase(), ownerHomes);
    }

    public static Home setHome(Player player, String name) {
        Home home = new Home(player.getName(), name, player.getLocation(), false, System.currentTimeMillis());
        addHome(home);

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO `" + table + "` (`owner`, `home`, `date`, `public`, `location`) VALUES (?, ?, ?, ?, ?)");

            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, home.getName());
            preparedStatement.setLong(3, home.getDate());
            preparedStatement.setBoolean(4, home.isCanAcess());
            preparedStatement.setString(5, LocationUtil.serializeLocation(player.getLocation()));

            if (!preparedStatement.isClosed()) {
                preparedStatement.execute();
                preparedStatement.close();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            PLUGIN.log(" <c>Erro ao salvar a casa de <f>" + player.getName() + "<c>!");
        }

        return home;
    }

    public static Home updateHome(Player player, String name) {
        Home home = getHouseByName(player.getName(), name);

        if (home == null) return setHome(player, name);

        home.setLocation(player.getLocation());
        home.setDate(System.currentTimeMillis());

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE " + table + " SET `date` = ?, `location` = ?  WHERE `owner` = ? AND `home` = ?");

            preparedStatement.setLong(1, home.getDate());
            preparedStatement.setString(2, LocationUtil.serializeLocation(home.getLocation()));
            preparedStatement.setString(3, player.getName());
            preparedStatement.setString(4, name);

            if (!preparedStatement.isClosed()) {
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            PLUGIN.log(" <c>Erro ao atualizar a casa de <f>" + player.getName() + "<c>!");
        }

        return home;
    }

    public static List<Home> getHomes(String target) {
        return PLUGIN.homes.getOrDefault(target.toLowerCase(), new ArrayList<>());
    }

    public static Home getHouseByName(String owner, String name) {
        for (Home home : getHomes(owner)) {
            if (home.getName().equalsIgnoreCase(name)) return home;
        }
        return null;
    }

    public static int getPublicSize(String owner) {
        int toReturn = 0;
        for (Home home : getHomes(owner)) {
            if (home.isCanAcess()) toReturn++;
        }
        return toReturn;
    }

    public static int getHomesSize(String owner) {
        return getHomes(owner).size();
    }

    public static int getLimit(Player player) {
        if (player.hasPermission("29Homes.ADM")) return 1000;

        int toReturn = 0;

        for (int index = 0; index < 1000; index++) {
            if (player.hasPermission("29Homes.limit." + index)) {
                toReturn = index;
                break;
            }
        }

        return toReturn;
    }

    public static String format(Home home) {
        return TXT.parse("<e>Coordenadas: <d>x: " + home.getLocation().getBlockX()
                + " y: " + home.getLocation().getBlockY()
                + " z: " + home.getLocation().getBlockZ()
                + "\n"
                + "<e>Data de criação: <d>" + getDate(home.getDate())
                + "\n"
                + "\n"
                + "<e>Clique para ir.");
    }


    private static String getDate(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy # HH:mm");

        return simpleDateFormat.format(calendar.getTime()).replace("#", "às");
    }
}
