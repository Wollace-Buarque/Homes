package dev.cromo29.homes.commands;

import dev.cromo29.homes.api.HomesAPI;
import dev.cromo29.homes.object.Home;
import dev.cromo29.durkcore.API.DurkCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomesCMD extends DurkCommand {
    @Override
    public void perform() {
        if (!isArgsLength(1)) {
            listHomes(asPlayer(), asPlayer().getName());
            return;
        }

        if (!hasPermission("29Homes.ADM")) listHomes(asPlayer(), asPlayer().getName());
        else listHomes(asPlayer(), argAt(0));
    }

    @Override
    public boolean canConsolePerform() {
        return true;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getCommand() {
        return "listhomes";
    }

    @Override
    public List<String> getAliases() {
        return getList("casas", "homes");
    }

    @Override
    public String getDescription() {
        return null;
    }

    private void listHomes(Player player, String target) {

        if (HomesAPI.getHomesSize(target) == 0) {

            if (asPlayer().getName().equalsIgnoreCase(target)) sendMessage("<c>Você não criou nenhuma casa.");
            else sendMessage(target + " <c>não possui nenhuma casa.");

            return;
        }

        List<Home> homes = new ArrayList<>();
        List<Home> publics = new ArrayList<>();

        for (Home home : HomesAPI.getHomes(target)) {

            if (home.isCanAcess()) publics.add(home);
            else homes.add(home);

        }

        if (!homes.isEmpty()) {
            TextComponent textComponent = new TextComponent("");

            String homesString = "<d>Casas: ";
            if (!asPlayer().getName().equalsIgnoreCase(target)) homesString = "<d>Casas de <f>" + target + "<d>: ";

            textComponent.addExtra(new TextComponent(parse(homesString)));

            for (Home home : homes) {

                String comma = ", ";
                if (homes.indexOf(home) == homes.size() - 1) comma = ".";

                textComponent.addExtra(new TextComponent(""));

                TextComponent homeComponent = new TextComponent(home.getName());

                homeComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(HomesAPI.format(home))}));
                homeComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/casa ir " + home.getName()));

                textComponent.addExtra(homeComponent);

                textComponent.addExtra(new TextComponent(comma));
            }

            player.spigot().sendMessage(textComponent);
        }

        if (!publics.isEmpty()) {
            TextComponent textComponent = new TextComponent("");

            String homesString = "<d>Públicas: ";
            if (!asPlayer().getName().equalsIgnoreCase(target)) homesString = "<d>Públicas de <f>" + target + "<d>: ";

            textComponent.addExtra(new TextComponent(parse(homesString)));

            for (Home home : publics) {

                String comma = ", ";
                if (publics.indexOf(home) == publics.size() - 1) comma = ".";

                textComponent.addExtra(new TextComponent(""));
                TextComponent homeComponent = new TextComponent(home.getName());

                homeComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(HomesAPI.format(home))}));
                homeComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/casa ir " + home.getName()));

                textComponent.addExtra(homeComponent);
                textComponent.addExtra(new TextComponent(comma));
            }

            player.spigot().sendMessage(textComponent);
        }
    }
}
