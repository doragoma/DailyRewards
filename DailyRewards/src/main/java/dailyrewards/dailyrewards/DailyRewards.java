package dailyrewards.dailyrewards;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public final class DailyRewards extends JavaPlugin {
    File file = new File("plugins/DailyRewards/config.yml");
    FileConfiguration data = YamlConfiguration.loadConfiguration(file);
    File p_file = new File("plugins/DailyRewards/data.yml");
    FileConfiguration p_data = YamlConfiguration.loadConfiguration(p_file);

    @Override
    public void onEnable() {
        saveResource("config.yml", false);
        getLogger().info("AziRewardsのデータ取得中・・・ (鯖の負荷を減らすため起動時に負荷のかかる行為を行います)");
        new config().reg("prefix", data.getString("prefix"));
        new config().reg("bonus.no-perm", data.getString("bonus.no-perm"));
        new config().reg("bonus.already", data.getString("bonus.already"));
        new config().reg("bonus.get", data.getString("bonus.get"));
        List<String> cmds = new ArrayList<>();
        for (String s : data.getStringList("bonus.commands")) {
            cmds.add(s);
        }
        new config().addcmd("cmds", cmds);
        if (data.getString("bonus.sound.enable").equalsIgnoreCase("true")) {
            new config().reg("bonus.sound", data.getString("bonus.sound.name"));
        }
        if (data.getString("bonus.login.enable").equalsIgnoreCase("true")) {
            new config().reg("bonus.login", data.getString("bonus.login.send"));
        }
        Bukkit.getServer().getPluginManager().registerEvents(new config(), this);
        getCommand("reward").setExecutor(new command());
        getCommand("reset").setExecutor(new command());
        getLogger().info("AziRewardsのデータ取得を終了しました。");
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            new config().DownServer(p);
        }
        try {
            p_data.save(p_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}