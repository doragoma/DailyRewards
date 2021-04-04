package dailyrewards.dailyrewards;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class command implements Listener, CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        Player p = (Player) s;
        if (cmd.getName().equalsIgnoreCase("reward")) {
            new config().usecmd(p);
        }
        if (cmd.getName().equalsIgnoreCase("reset")) {
            if (!p.isOp()) return true;
            new config().remove(p);
        }
        return true;
    }
}