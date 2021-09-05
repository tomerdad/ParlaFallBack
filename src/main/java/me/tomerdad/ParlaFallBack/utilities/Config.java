package me.tomerdad.ParlaFallBack.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.common.reflect.TypeToken;
import me.tomerdad.ParlaFallBack.ParlaFallBack;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import ninja.leaping.configurate.ConfigurationNode;

public class Config {

    public static Path configPath;
    public static ConfigurationNode rootNode;

    public static void setupConfig(){
        try {


            configPath = Paths.get(ParlaFallBack.configpath + "/config.yml");

            if (!Files.exists(configPath.getParent())) {
                Files.createDirectories(ParlaFallBack.configpath);
            }
            InputStream realConfigPath = Config.class.getResourceAsStream("/config.yml");
            if (!Files.exists(configPath)) {
                assert realConfigPath != null;
                Files.copy(realConfigPath, configPath);
            }

            YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder().setPath(configPath).build();
            rootNode = loader.load();
        }catch (IOException e){
            e.printStackTrace();
            // return error
        }
    }

    public static ConfigurationNode getrootNode() {
        return rootNode;
    }

    public static Map<Object ,? extends  ConfigurationNode> nodeConfigMap(Object... Node) {
        return rootNode.getNode(Node).getChildrenMap();
    }

    public static List<String> nodeConfigList(Object... Node){
        try {
            return rootNode.getNode(Node).getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return rootNode.getNode(Node).getList(Object::toString);
    }
}