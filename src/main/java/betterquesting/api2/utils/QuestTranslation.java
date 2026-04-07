package betterquesting.api2.utils;

import net.minecraft.util.StringTranslate;

public class QuestTranslation
{
    public static String translate(String text)
    {
        return StringTranslate.getInstance().translateKey(text);
    }

    public static String translate(String text, Object... args)
    {
        String out = StringTranslate.getInstance().translateKeyFormat(text, args);
        if(out.startsWith("Format error: ")) return text; // TODO: Find a more reliable way of detecting translation failure
        return out;
    }
    
    public static String translateTrimmed(String text, Object... args)
    {
        return translate(text, args).replaceAll("\r", "");
    }
}
