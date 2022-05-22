package dev.cromo29.homes.commands;

import dev.cromo29.homes.api.HomesAPI;
import dev.cromo29.homes.api.Logs;
import dev.cromo29.homes.object.Home;
import dev.cromo29.durkcore.api.DurkCommand;

import java.util.List;

public class PublishCMD extends DurkCommand {
    @Override
    public void perform() {

        if (!isArgsLength(1)) {
            sendMessage(" <d>* <f>/publicar <casa>");
            return;
        }

        if (!hasPermission("29Homes.VIP") && !hasPermission("29Homes.ADM")) {
            sendMessage("<c>Apenas jogadores <6>VIPs <c>podem deixar uma casa pública!");
            return;
        }

        String dice = argAt(0);
        Home home = HomesAPI.getHouseByName(asPlayer().getName(), dice);

        if (home == null) {
            sendMessage("<c>A casa <f>" + dice + " <c>não existe.");
            return;
        }

        if (home.isCanAcess()) {
            home.setCanAcess(!home.isCanAcess());

            String privacity = "privada";
            String additional = "<f>Agora ninguem podera acessa-la.";

            sendMessage("<f>A privacidade da sua casa foi alterada para <d>" + privacity + "<f>.");
            sendMessage(additional);
            return;
        }

        if (HomesAPI.getPublicSize(asPlayer().getName()) >= 3) {
            sendMessage("<c>Você só pode publicar <f>3 <c>casas.");
            return;
        }

        home.setCanAcess(!home.isCanAcess());
        String privacity = "privada";
        String additional = "<f>Agora ninguem podera acessa-la.";

        if (home.isCanAcess()) {
            privacity = "publica";
            additional = "<f>Agora todos poderão acessa-la usando '<d>/home " + asPlayer().getName() + ":" + home.getName() + "<f>'.";
        }

        sendMessage("A privacidade da sua casa foi alterada para <d>" + privacity + "<f>.");
        sendMessage(additional);

        Logs.logToFile(asPlayer().getName() + " setou a sua home " + home.getName() + " como " + privacity + ".");
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
        return "publicar";
    }

    @Override
    public List<String> getAliases() {
        return getList("publish");
    }

    @Override
    public String getDescription() {
        return null;
    }
}
