package dev.cromo29.homes.commands;

import dev.cromo29.homes.api.HomesAPI;
import dev.cromo29.homes.HomePlugin;
import dev.cromo29.homes.object.Home;
import dev.cromo29.durkcore.API.DurkCommand;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class HomeCMD extends DurkCommand {

    @Override
    public void perform() {

        if (!isArgsLength(1)) {
            sendMessage(" <d>* <f>/home <jogador:casa>");
            return;
        }

        String dice = argAt(0);
        String name;
        String owner = asPlayer().getName();

        Home home;

        if (dice.contains(":") && dice.split(":").length == 2) {
            name = dice.split(":")[1];
            owner = dice.split(":")[0];
        } else name = dice;

        home = HomesAPI.getHouseByName(owner, name);

        if (home == null) {
            sendMessage("<c>A casa <f>" + name + " <c>não existe!");
            return;
        }

        if (owner.equalsIgnoreCase(asPlayer().getName()) || home.isCanAcess()) {

            // Método no objeto para corrigir uma falha.
            home.teleport(asPlayer());

        } else {

            if (hasPermission("29Casas.ADM")) {
                asPlayer().teleport(home.getLocation());

                String anotherOwner = " de '<d>" + owner + "<f>'";
                sendMessage("Teleportado para a casa <d>" + home.getName() + "<f>" + anotherOwner + ".");
                return;
            }

            sendMessage("<c>Esta casa está privada!");
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
        return "home";
    }

    @Override
    public List<String> getAliases() {
        return getList("casa");
    }

    @Override
    public String getDescription() {
        return null;
    }
}
