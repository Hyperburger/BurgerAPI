package hyperburger.me.burgerapi.nms;

import org.bukkit.Bukkit;

public class NMS {

    private static INMS handler;

    public static void init(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            final Class<?> clazz = Class.forName("hyperburger.me.burgerapi.nms." + version + ".NMSHandler");
            if (INMS.class.isAssignableFrom(clazz)) {
                handler = (INMS) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static INMS getHandler() {
        return handler;
    }
}
