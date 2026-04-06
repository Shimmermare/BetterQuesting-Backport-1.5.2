package betterquesting.handlers;

import betterquesting.api.storage.BQ_Settings;
import betterquesting.core.BetterQuesting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.Configuration;
import java.util.logging.Level;

public class ConfigHandler
{
	public static Configuration config;

	public static void initConfigs()
	{
		if(config == null)
		{
			BetterQuesting.logger.log(Level.SEVERE, "Config attempted to be loaded before it was initialised!");
			return;
		}

		config.load();

        BQ_Settings.questNotices = config.get("Quest Notices", Configuration.CATEGORY_GENERAL, true, "Enabled the popup notices when quests are completed or updated").getBoolean(true);
        BQ_Settings.curTheme = config.get("Theme", Configuration.CATEGORY_GENERAL, "betterquesting:light", "The current questing theme").getString();
		BQ_Settings.useBookmark = config.get("Use Quest Bookmark", Configuration.CATEGORY_GENERAL, true, "Jumps the user to the last opened quest").getBoolean(true);
		int guiWidth = config.get("Max GUI Width", Configuration.CATEGORY_GENERAL, -1, "Clamps the max UI width (-1 to disable)").getInt();
        BQ_Settings.guiWidth = MathHelper.clamp_int(guiWidth, -1, Integer.MAX_VALUE);
		int guiHeight = config.get("Max GUI Height", Configuration.CATEGORY_GENERAL, -1, "Clamps the max UI height (-1 to disable)").getInt();
        BQ_Settings.guiHeight = MathHelper.clamp_int(guiHeight, -1, Integer.MAX_VALUE);
		config.save();
	}
}
