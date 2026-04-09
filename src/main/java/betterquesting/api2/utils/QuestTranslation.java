package betterquesting.api2.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StringTranslate;

public class QuestTranslation {
    public static String translate(ICommandSender sender, String text) {
        StringTranslate translator = sender instanceof EntityPlayerMP
                ? ((EntityPlayerMP) sender).getTranslator()
                : StringTranslate.getInstance();
        return translate(translator, text);
    }

    public static String translate(String text) {
        return translate(StringTranslate.getInstance(), text);
    }

    private static String translate(StringTranslate translator, String text) {
        return translator.translateKey(text);
    }

    public static String translate(ICommandSender sender, String text, Object... args) {
        StringTranslate translator = sender instanceof EntityPlayerMP
                ? ((EntityPlayerMP) sender).getTranslator()
                : StringTranslate.getInstance();
        return translate(translator, text, args);
    }

    public static String translate(String text, Object... args) {
        return translate(StringTranslate.getInstance(), text, args);
    }

    private static String translate(StringTranslate translator, String text, Object... args) {
        String out = translator.translateKeyFormat(text, args);
        if (out.startsWith("Format error: "))
            return text; // TODO: Find a more reliable way of detecting translation failure
        return out;
    }

    public static String translateTrimmed(String text, Object... args) {
        return translate(text, args).replaceAll("\r", "");
    }
}
