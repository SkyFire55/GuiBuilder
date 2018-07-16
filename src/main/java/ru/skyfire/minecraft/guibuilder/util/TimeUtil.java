package ru.skyfire.minecraft.guibuilder.util;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;

public class TimeUtil {
    public static void setCurrentTimeToConfig(ConfigurationNode node, ConfigurationLoader loader){
        node.getNode("time").setValue(System.currentTimeMillis());
        try {
            loader.save(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateTime(ConfigurationNode node){
        double lastDayTime = node.getNode("time").getDouble();
        double currentTime = System.currentTimeMillis();
        double oneDay = 24*60*60*1000;

        if(lastDayTime<currentTime-oneDay){
            while (currentTime-oneDay>lastDayTime){
                lastDayTime=lastDayTime+oneDay;
            }
            node.getNode("time").setValue(lastDayTime);
            return true;
        }
        return false;
    }
}
