package ru.skyfire.minecraft.guibuilder;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import ru.skyfire.minecraft.guibuilder.cmd.CmdBuilder;
import ru.skyfire.minecraft.guibuilder.db.ConfigDB;
import ru.skyfire.minecraft.guibuilder.db.SqliteDB;
import ru.skyfire.minecraft.guibuilder.listeners.Listeners;
import ru.skyfire.minecraft.guibuilder.util.ConfigUtil;
import ru.skyfire.minecraft.guibuilder.util.ItemUtil;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Plugin(
        id = "guibuilder",
        name = "GuiBuilder"
)
public class GuiBuilder {
    private static GuiBuilder instance;
    @Inject private PluginContainer plugin;
    public static Logger logger;
    @Inject private void setLogger(Logger logger){
        this.logger = logger;
    }

    private ConfigurationNode defNode;
    private ConfigurationNode guiNode;
    private ConfigurationNode itemsNode;
    private ConfigurationLoader defLoader;
    private ConfigurationLoader guiLoader;
    private ConfigurationLoader itemsLoader;

    private Map<String, GuiPage> guiPageMap;
    private Map<String, ItemStack> itemsMap;

    private SqliteDB sqliteDB;
    private ConfigDB configDB;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        instance=this;
        initConfigs();
        initCmds();
        initGui();
        Sponge.getEventManager().registerListeners(this, new Listeners());
        logger.info("Hello world!");
    }

    public void initConfigs() {
        defLoader = ConfigUtil.getLoader(configDir, plugin, "guibuilder.conf");
        defNode = ConfigUtil.initConfig(defLoader);
        guiLoader = ConfigUtil.getLoader(configDir, plugin, "guis.conf");
        guiNode = ConfigUtil.initConfig(guiLoader);
        itemsLoader = ConfigUtil.getLoader(configDir, plugin, "items.conf");
        itemsNode = ConfigUtil.initConfig(itemsLoader);
    }

    public void initGui(){
        Map<String, ItemStack> itemsMap = new HashMap<>();
        for(Object o : itemsNode.getChildrenMap().keySet()){
            itemsMap.put(String.valueOf(o), ItemUtil.fromNode(itemsNode.getNode(o)));
        }
        Map<String, GuiPage> guiPageMap = new HashMap<>();
        for(Object o : guiNode.getChildrenMap().keySet()){
            guiPageMap.put(String.valueOf(o), GuiPage.fromConfig(guiNode.getNode(o), itemsMap));
        }
        this.itemsMap = itemsMap;
        this.guiPageMap = guiPageMap;
    }

    public void initCmds(){
        CmdBuilder.initCmds(plugin);
    }

    public static GuiBuilder getInstance() {
        return instance;
    }

    public ConfigurationNode getDefNode() {
        return defNode;
    }

    public ConfigurationNode getTransNode() {
        return null;
    }

    public Map<String, GuiPage> getGuiPageMap() {
        return guiPageMap;
    }
}
