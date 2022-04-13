package dev.cromo29.homes.api;


import dev.cromo29.homes.HomePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    public static Connection connection = null;

    private static final HomePlugin PLUGIN = HomePlugin.get();

    static String multiHome = PLUGIN.getConfig().getString("MySQL.MultiHome");
    static String table = PLUGIN.getConfig().getString("MySQL.Homes");

    public static boolean openConnection() {
        FileConfiguration configuration = PLUGIN.getConfig();

        String user = configuration.getString("MySQL.User");
        String password = configuration.getString("MySQL.Password");
        String database = configuration.getString("MySQL.Database");
        String host = configuration.getString("MySQL.Host");
        int port = configuration.getInt("MySQL.Port");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
            connection = DriverManager.getConnection(url, user, password);
            PLUGIN.log(" <a>Conexao com MySQL estabelecida.");

            return true;
        } catch (SQLException exception) {
            PLUGIN.log(" <c>Conexao com MySQL cancelada.");
            Bukkit.getPluginManager().disablePlugin(PLUGIN);
            return false;
        }
    }

    public static void createTable() {
        if (connection == null) return;

        PreparedStatement ps;

        try {
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (`id` INT NOT NULL AUTO_INCREMENT, `owner` TEXT NULL, `home` TEXT NULL, `date` VARCHAR(21) NULL, `public` BOOLEAN NULL, `location` TEXT NULL, PRIMARY KEY (`id`));");
            ps.executeUpdate();

            PLUGIN.log(" <a>A Tabela '" + table + "' foi carregada.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            PLUGIN.log(" <c>Nao foi possivel carregar a Tabela.");
        }
    }

    public static void close() {
        if (connection == null) return;

        try {
            connection.close();
            connection = null;

            PLUGIN.log(" <a>Conexao com MySQL fechada.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            PLUGIN.log(" <c>Nao foi possivel fechar a conexao.");
        }
    }
}
