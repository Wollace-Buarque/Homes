package dev.cromo29.homes;

import dev.cromo29.homes.api.HomesAPI;
import dev.cromo29.homes.api.MultiHomeTransfer;
import dev.cromo29.homes.api.MySQL;
import dev.cromo29.homes.commands.*;
import dev.cromo29.homes.object.Home;
import dev.cromo29.durkcore.API.DurkPlugin;
import dev.cromo29.durkcore.SpecificUtils.LocationUtil;
import dev.cromo29.durkcore.Util.GsonManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class HomePlugin extends DurkPlugin {

    public Map<String, List<Home>> homes;
    public GsonManager homesGson;

    @Override
    public void onStart() {
        homesGson = new GsonManager(this, "homes.json").prepareGson();
        homes = new HashMap<>();

        registerCommands(new SetHomeCMD(), new HomeCMD(), new HomesCMD(), new DelHome(), new PublishCMD());

        saveDefaultConfig();

        if (getConfig().getBoolean("MySQL.Enable")) {
            if (!MySQL.openConnection()) return;

            MySQL.createTable();

            if (getConfig().getBoolean("MySQL.Transfer")) new MultiHomeTransfer(this).startTransfer();
        }

        load();
    }

    @Override
    public void onStop() {
        save();

        MySQL.close();
    }

    public static HomePlugin get() {
        return getPlugin(HomePlugin.class);
    }

    public void save() {
        if (getConfig().getBoolean("MySQL.Enable")) return;

        homesGson.removeAll("Homes");

        for (OfflinePlayer player : getServer().getOfflinePlayers()) {
            for (Home home : HomesAPI.getHomes(player.getName())) {

                String name = home.getName();
                String owner = home.getOwner();
                long date = home.getDate();
                boolean canAcess = home.isCanAcess();

                homesGson.put("Homes." + owner + "." + name + ".Date", date);
                homesGson.put("Homes." + owner + "." + name + ".Public", canAcess);
                homesGson.putLocation("Homes." + owner + "." + name + ".Location", home.getLocation());
            }
        }

        homesGson.save();
    }

    public void load() {

        if (getConfig().getBoolean("MySQL.Enable")) {

            if (MySQL.connection == null) {
                log(" <c>O MySQL nao esta aberto!");
                return;
            }

            PreparedStatement preparedStatement;

            try {
                preparedStatement = MySQL.connection.prepareStatement("SELECT * FROM " + getConfig().getString("MySQL.Homes"));
                ResultSet resultSet = preparedStatement.executeQuery();

                int homesSize = 0;
                while (resultSet.next()) {
                    homesSize++;

                    String owner = resultSet.getString("owner");
                    String name = resultSet.getString("home");
                    long date = resultSet.getLong("date");
                    boolean canAcess = resultSet.getBoolean("public");
                    Location location = LocationUtil.unserializeLocation(resultSet.getString("location"));

                    Home home = new Home(owner, name, location, canAcess, date);

                    List<Home> ownerHomes = homes.getOrDefault(owner.toLowerCase(), new ArrayList<>());
                    ownerHomes.add(home);

                    homes.put(owner.toLowerCase(), ownerHomes);

                }

                if (homesSize > 0) log(" <b>" + homesSize + " <a>casas carregadas!");

            } catch (SQLException throwables) {
                log(" <c>Erro ao carregar casas dos jogadores.");
            }
            return;
        }

        if (homesGson.getSection("Homes") == null) {
            log(" <c>Nao foi encontrada nenhuma casa.");
            return;
        }

        int homesSize = 0;
        for (String owner : homesGson.getSection("Homes")) {
            homesSize++;

            for (String name : homesGson.getSection("Homes." + owner)) {

                long date = homesGson.get("Homes." + owner + "." + name + ".Date").asLong();
                boolean isPublic = homesGson.get("Homes." + owner + "." + name + ".Public").asBoolean();
                Location location = homesGson.get("Homes." + owner + "." + name + ".Location").asLocation();

                Home home = new Home(owner, name, location, isPublic, date);

                List<Home> ownerHomes = homes.getOrDefault(owner.toLowerCase(), new ArrayList<>());
                ownerHomes.add(home);

                homes.put(owner.toLowerCase(), ownerHomes);
            }

            if (homesSize > 0) log(" <b>" + homesSize + " <a>casas carregadas!");
        }
    }
}
