package dailyrewards.dailyrewards;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class config implements Listener {
    /*
    PCとしてのカレンダーなので信用できる
     */
    Calendar cal = Calendar.getInstance();
    Integer ddd = cal.get(Calendar.DAY_OF_MONTH);
    Integer mmm = cal.get(Calendar.MONTH) + 1;
    /*
    ハッシュマップここでconfigの登録をして鯖の負荷を減らす
    後プレイヤーの取得も追加する
     */
    public static HashMap<String, String> cc = new HashMap<>();
    public static HashMap<Player, String> cooldown = new HashMap<>();
    public static HashMap<String, List> cmds = new HashMap<>();

    public void remove(Player p) {
        cooldown.remove(p);
    }

    /*
    違うクラスでコマンドを打ってこっちに移す
    それでその人がその日に既に取得してるかなどを確認する
     */
    public void usecmd(Player p) {
        String dm = ddd + "月" + mmm + "日";
        if (cooldown.containsKey(p)) {
            if (cooldown.get(p).equalsIgnoreCase(dm)) {
                p.sendMessage(cc.get("bonus.already").replace("&", "§").replace("%prefix%", cc.get("prefix").replace("&", "§")));
                return;
            } else {
                cooldown.put(p, dm);
                reward(p);
                return;
            }
        }
        cooldown.put(p, dm);
        reward(p);
    }

    /*
    報酬が２個に分かれるとぐちゃぐちゃになるので１個にまとめた
     */
    public void reward(Player p) {
        String dm = mmm + "月" + ddd + "日";
        p.sendMessage(cc.get("bonus.get").replace("&", "§").replace("%prefix%", cc.get("prefix").replace("&", "§")));
        List<String> list = cmds.get("cmds");
        if (cc.containsKey("bonus.sound")) {
            p.getWorld().playSound(p.getLocation(), cc.get("bonus.sound"), 1, 1);
        }
        for (String s : list) {
            s = s.replace("[", "").replace("]", "").replace("&", "§").replace(",", "");
            s = s.replace("%player%", p.getDisplayName());
            getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
        }
    }

    /*
    オンラインのプレイヤーを全員をデータに追加して保存
    これがなければ無限に報酬がぁ～
     */
    public void DownServer(Player p) {
        if (cooldown.containsKey(p)) {
            String dm = ddd + "月" + mmm + "日";
            File file = new File("plugins/AziReward/data.yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            data.set("data." + p.getUniqueId(), dm);
            try {
                data.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    /*
    プレイヤーがログインした時に報酬を無限に受け取れないように
    ログインで登録ができる
     */
    @EventHandler
    public void JoinServer(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        File file = new File("plugins/AziReward/data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        String dm = ddd + "月" + mmm + "日";
        if (data.contains("data."+p.getUniqueId())) {
            cooldown.put(p, dm);
        }
    }

    /*
    プレイヤーが人に抜けた時だけデータにアクセスして
    データを追加
     */
    @EventHandler
    public void QuitServer(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        DownServer(p);
        cooldown.remove(p);
    }

    /*
    サーバーの起動時に全て取得と追加で後々の鯖の負荷を減らすコード
    起動時に活用
     */
    public void reg(String key, String value) {
        cc.put(key, value);
    }
    public void addcmd(String key, List<String> cmd) {
        cmds.put(key, cmd);
    }
}
