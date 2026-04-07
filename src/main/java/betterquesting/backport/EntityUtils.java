package betterquesting.backport;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.StatCollector;

import java.io.File;
import java.io.IOException;

public class EntityUtils {
    public static String getLocalizedName(Entity entity) {
        String entityName = EntityList.getEntityString(entity);
        if (entityName == null) entityName = "generic";
        return StatCollector.translateToLocal("entity." + entityName + ".name");
    }
}
