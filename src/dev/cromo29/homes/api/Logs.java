package dev.cromo29.homes.api;


import dev.cromo29.homes.HomePlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logs {

    public static void logToFile(String message) {
        final HomePlugin plugin = HomePlugin.get();

        if (!plugin.getConfig().getBoolean("Settings.Log")) return;

        try {
            File dataFolder = plugin.getDataFolder();

            if (!dataFolder.exists()) dataFolder.mkdir();

            File saveTo = new File(plugin.getDataFolder(), "logs.txt");

            if (!saveTo.exists()) saveTo.createNewFile();

            FileWriter fileWriter = new FileWriter(saveTo, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[dd/MM/yyyy hh:mm:ss] ");

            String data = simpleDateFormat.format(calendar.getTime());

            printWriter.println(data + message);

            printWriter.flush();
            printWriter.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
