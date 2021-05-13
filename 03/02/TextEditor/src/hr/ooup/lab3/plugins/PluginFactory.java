package hr.ooup.lab3.plugins;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginFactory {
    private static final String PLUGINS_DIRECTORY = "./plugins";
    private static ClassLoader parent;
    private static Map<String, URLClassLoader> plugins = new HashMap<>();

    public static Plugin newInstance(String pluginName) throws Exception {
        if (parent == null) parent = PluginFactory.class.getClassLoader();

        URLClassLoader classLoader = plugins.get(pluginName);
        if (classLoader == null) {
            classLoader = new URLClassLoader(
                    new URL[] {
                            new File(PLUGINS_DIRECTORY).toURI().toURL(),
                            new File(PLUGINS_DIRECTORY + pluginName + ".jar").toURI().toURL()
                    }, parent);
        }

        Class<Plugin> clazz = (Class<Plugin>) classLoader.loadClass("hr.ooup.lab3.plugins." + pluginName);
        Constructor<?> ctr = clazz.getConstructor();
        return (Plugin) ctr.newInstance();
    }

    public static List<Plugin> getAllPlugins() {
        List<Plugin> plugins = new ArrayList<>();
        Path pluginsDirectory = Path.of(PLUGINS_DIRECTORY);
        if (!Files.isDirectory(pluginsDirectory)) {
            return plugins;
        }

        try {
            for (File file : pluginsDirectory.toFile().listFiles()) {
                if (file.getName().endsWith(".jar") && Files.isReadable(file.toPath())) {
                    String path = file.getName();
                    plugins.add(newInstance(path.substring(0, path.length()-4)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return plugins;
    }
}
