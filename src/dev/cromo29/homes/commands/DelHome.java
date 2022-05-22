package dev.cromo29.homes.commands;

import dev.cromo29.homes.api.HomesAPI;
import dev.cromo29.homes.api.Logs;
import dev.cromo29.homes.object.Home;
import dev.cromo29.durkcore.api.DurkCommand;

import java.util.List;

public class DelHome extends DurkCommand {

    @Override
    public void perform() {
        if (isArgsLength(2)) {

            if (!hasPermission("29Homes.ADM")) {
                warnNoPermission();
                return;
            }

            String dice = argAt(1);
            Home home = HomesAPI.getHouseByName(argAt(0), dice);

            if (home == null) {
                sendMessage("<c>A casa <f>" + dice + " <c>de <f>" + argAt(0) + " <c>não existe!");
                return;
            }

            home.delete();

            sendMessage("Casa <d>" + home.getName() + " <f>de <d>" + argAt(0) + " <f>deletada!");

            Logs.logToFile(asPlayer().getName() + " removeu a casa " + home.getName() + " de " + argAt(0));

        } else if (isArgsLength(1)) {

            String dice = argAt(0);
            if (dice.contains(":")) {

                if (dice.split(":").length == 2) {
                    String owner = dice.split(":")[0];
                    String name = dice.split(":")[1];

                    if (!owner.equalsIgnoreCase(asPlayer().getName()) && !hasPermission("29Casas.ADM")) {
                        warnNoPermission();
                        return;
                    }

                    Home home = HomesAPI.getHouseByName(owner, name);

                    if (home == null) {
                        sendMessage("<c>A casa <f>" + name + " <c>de <f>" + owner + " <c>não existe!");
                        return;
                    }

                    home.delete();

                    sendMessage("Casa <d>" + home.getName() + " <f>de <d>" + owner + " <f>deletada!");

                    Logs.logToFile(asPlayer().getName() + " removeu a casa " + home.getName() + " de " + owner);


                } else {

                    if (hasPermission("29Homes.ADM")) {
                        sendMessage(" <d>* <f>/" + getUsedCommand() + " <jogador> casa");
                        sendMessage(" <d>* <f>/" + getUsedCommand() + " <jogador:casa>");
                    }

                    sendMessage(" <d>* <f>/" + getUsedCommand() + " <casa>");
                }

                return;
            }

            Home home = HomesAPI.getHouseByName(asPlayer().getName(), dice);

            if (home == null) {
                sendMessage("<c>A casa <f>" + dice + " <c>não existe!");
                return;
            }

            home.delete();
            sendMessage("A casa <d>" + home.getName() + " <f>foi deletada.");

        } else {

            if (hasPermission("29Casas.ADM")) {
                sendMessage(" <d>* <f>/" + getUsedCommand() + " <jogador> casa");
                sendMessage(" <d>* <f>/" + getUsedCommand() + " <jogador:casa>");
            }

            sendMessage(" <d>* <f>/" + getUsedCommand() + " <casa>");
        }
    }

    @Override
    public boolean canConsolePerform() {
        return false;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getCommand() {
        return "delhome";
    }

    @Override
    public List<String> getAliases() {
        return getList("deletehome");
    }

    @Override
    public String getDescription() {
        return null;
    }
}
