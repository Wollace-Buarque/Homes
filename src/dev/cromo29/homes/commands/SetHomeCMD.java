package dev.cromo29.homes.commands;

import dev.cromo29.homes.api.HomesAPI;
import dev.cromo29.homes.HomePlugin;
import dev.cromo29.homes.object.Home;
import dev.cromo29.durkcore.api.DurkCommand;

import java.util.List;

public class SetHomeCMD extends DurkCommand {

    @Override
    public void perform() {

        if (!isArgsLength(1)) {
            sendMessage("<d>* <f>/sethome <casa>");
            return;
        }

        String name = argAt(0);

        boolean can = false;
        int limit = HomesAPI.getLimit(asPlayer());

        for (String world : HomePlugin.get().getConfig().getStringList("Settings.Worlds")) {
            if (asPlayer().getWorld().getName().equalsIgnoreCase(world)) {
                can = true;
                break;
            }
        }

        if (!can) {
            sendMessage("<c>Você não pode setar casas neste mundo!");
            return;
        }

        if (HomesAPI.getHomesSize(asPlayer().getName()) >= limit) {
            sendMessage("<c>Você só pode ter <f>" + limit + " <c>casas.");
            return;
        }

        if (!isNumOrLet(name)) {
            sendMessage("<c>O nome só pode conter numeros e letras.");
            return;
        }

        Home home;

        if (HomesAPI.getHouseByName(asPlayer().getName(), name) == null) {
            home = HomesAPI.setHome(asPlayer(), name);
            sendMessage("<f>A casa <d>" + home.getName() + " <f>foi criada.");
        } else {
            home = HomesAPI.updateHome(asPlayer(), name);
            sendMessage("<f>A casa <d>" + home.getName() + " <f>foi atualizada.");
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
        return "sethome";
    }

    @Override
    public List<String> getAliases() {
        return getList("setarcasa");
    }

    @Override
    public String getDescription() {
        return null;
    }

    private boolean isNumOrLet(String string) {
        for (char letter : string.toCharArray()) {
            if (!Character.isLetterOrDigit(letter)) return false;
        }
        return true;
    }
}
