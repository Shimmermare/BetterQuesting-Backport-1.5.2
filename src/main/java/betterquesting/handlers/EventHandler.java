package betterquesting.handlers;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.events.BQLivingUpdateEvent;
import betterquesting.api.events.DatabaseEvent;
import betterquesting.api.events.QuestEvent;
import betterquesting.api.events.QuestEvent.Type;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.storage.BQ_Settings;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.cache.QuestCache.QResetTime;
import betterquesting.api2.storage.DBEntry;
import betterquesting.network.handlers.NetNotices;
import betterquesting.network.handlers.NetQuestSync;
import betterquesting.questing.QuestDatabase;
import betterquesting.storage.LifeDatabase;
import betterquesting.storage.QuestSettings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.*;

/**
 * Event handling for standard quests and core BetterQuesting functionality
 */
public class EventHandler
{
	public static final EventHandler INSTANCE = new EventHandler();

    private static final Map<String, NBTTagCompound> deadPlayerCache = new HashMap<String, NBTTagCompound>();

    @ForgeSubscribe
    public void onLivingDeath(LivingDeathEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        if (!event.entityLiving.worldObj.isRemote) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        QuestCache cache = (QuestCache) player.getExtendedProperties(QuestCache.LOC_QUEST_CACHE);
        if (cache != null) {
            NBTTagCompound tmp = new NBTTagCompound();
            cache.saveNBTData(tmp);
            deadPlayerCache.put(player.username, tmp);
        }

        if(QuestSettings.INSTANCE.getProperty(NativeProps.HARDCORE))
        {
            UUID uuid = QuestingAPI.getQuestingUUID(((EntityPlayer)event.entityLiving));
            int lives = LifeDatabase.INSTANCE.getLives(uuid);
            LifeDatabase.INSTANCE.setLives(uuid, lives - 1);
        }
    }

    @ForgeSubscribe
    public void onEntityJoin(EntityJoinWorldEvent event)
    {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entity;
        NBTTagCompound savedCache = deadPlayerCache.remove(player.username);

        if(event.entity.getExtendedProperties(QuestCache.LOC_QUEST_CACHE) == null)
        {
            QuestCache questCache = new QuestCache();
            if (savedCache != null) {
                questCache.loadNBTData(savedCache);
            }
            event.entity.registerExtendedProperties(QuestCache.LOC_QUEST_CACHE, questCache);
        }
    }
	
	@ForgeSubscribe
	public void onLivingUpdate(BQLivingUpdateEvent event)
	{
		if(event.entityLiving.worldObj.isRemote) return;
		if(!(event.entityLiving instanceof EntityPlayerMP)) return;
        if(event.entityLiving.ticksExisted%20 != 0) return; // Only triggers once per second
        
        EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;
        QuestCache qc = (QuestCache)player.getExtendedProperties(QuestCache.LOC_QUEST_CACHE);
        boolean editMode = QuestSettings.INSTANCE.getProperty(NativeProps.EDIT_MODE);
        
        if(qc == null) return;
        
        List<DBEntry<IQuest>> activeQuests = QuestDatabase.INSTANCE.bulkLookup(qc.getActiveQuests());
        List<DBEntry<IQuest>> pendingAutoClaims = QuestDatabase.INSTANCE.bulkLookup(qc.getPendingAutoClaims());
        QResetTime[] pendingResets = qc.getScheduledResets();
        
        UUID uuid = QuestingAPI.getQuestingUUID(player);
        boolean refreshCache = false;
        
        if(!editMode && player.ticksExisted%60 == 0) // Passive quest state check every 3 seconds
        {
            List<Integer> com = new ArrayList<Integer>();
            
            for(DBEntry<IQuest> quest : activeQuests)
            {
                if(!quest.getValue().isUnlocked(uuid)) continue; // Although it IS active, it cannot be completed yet
                
                if(quest.getValue().canSubmit(player)) quest.getValue().update(player);
                
                if(quest.getValue().isComplete(uuid) && !quest.getValue().canSubmit(player))
                {
                    refreshCache = true;
                    qc.markQuestDirty(quest.getID());
                    
                    com.add(quest.getID());
                    if(!quest.getValue().getProperty(NativeProps.SILENT)) postPresetNotice(quest.getValue(), player, 2);
                }
            }
            
            MinecraftForge.EVENT_BUS.post(new QuestEvent(Type.COMPLETED, uuid, com));
        }
        
        if(!editMode && MinecraftServer.getServer() != null) // Repeatable quest resets
        {
            List<Integer> res = new ArrayList<Integer>();
            long totalTime = System.currentTimeMillis();
            
            for(QResetTime rTime : pendingResets)
            {
                IQuest entry = QuestDatabase.INSTANCE.getValue(rTime.questID);
                
                if(totalTime >= rTime.time && !entry.canSubmit(player)) // REEEEEEEEEset
                {
                    if(entry.getProperty(NativeProps.GLOBAL))
                    {
                        entry.resetUser(null, false);
                    } else
                    {
                        entry.resetUser(uuid, false);
                    }
                    
                    refreshCache = true;
                    qc.markQuestDirty(rTime.questID);
                    res.add(rTime.questID);
                    if(!entry.getProperty(NativeProps.SILENT)) postPresetNotice(entry, player, 1);
                } else break; // Entries are sorted by time so we fail fast and skip checking the others
            }
            
            MinecraftForge.EVENT_BUS.post(new QuestEvent(Type.RESET, uuid, res));
        }
        
        if(!editMode)
        {
            for(DBEntry<IQuest> entry : pendingAutoClaims) // Auto claims
            {
                if(entry.getValue().canClaim(player))
                {
                    entry.getValue().claimReward(player);
                    refreshCache = true;
                    qc.markQuestDirty(entry.getID());
                    // Not going to notify of auto-claims anymore. Kinda pointless if they're already being pinged for completion
                }
            }
        }
        
        if(refreshCache || player.ticksExisted % 200 == 0) // Refresh the cache if something changed or every 10 seconds
        {
            qc.updateCache(player);
        }
        
        if(qc.getDirtyQuests().length > 0) NetQuestSync.sendSync(player, qc.getDirtyQuests(), false, true);
        qc.cleanAllQuests();
	}
	
	// TODO: Create a new message inbox system for these things. On screen popups aren't ideal in combat
	private static void postPresetNotice(IQuest quest, EntityPlayer player, int preset)
	{
	    if(!(player instanceof EntityPlayerMP)) return;
        ItemStack icon = quest.getProperty(NativeProps.ICON).getBaseStack();
        String mainText = "";
        String subText = quest.getProperty(NativeProps.NAME);
        String sound = "";
	    
		switch(preset)
		{
			case 0:
            {
                mainText = "betterquesting.notice.unlock";
                sound = quest.getProperty(NativeProps.SOUND_UNLOCK);
                break;
            }
			case 1:
            {
                mainText = "betterquesting.notice.update";
                sound = quest.getProperty(NativeProps.SOUND_UPDATE);
                break;
            }
			case 2:
            {
                mainText = "betterquesting.notice.complete";
                sound = quest.getProperty(NativeProps.SOUND_COMPLETE);
                break;
            }
		}
		
		NetNotices.sendNotice(quest.getProperty(NativeProps.GLOBAL) ? null : new EntityPlayerMP[]{(EntityPlayerMP)player}, icon, mainText, subText, sound);
	}
	
	@ForgeSubscribe
	public void onWorldSave(WorldEvent.Save event)
	{
		if(!event.world.isRemote && BQ_Settings.curWorldDir != null && event.world.provider.dimensionId == 0)
		{
			SaveLoadHandler.INSTANCE.saveDatabases();
		}
	}
	
	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void onDataUpdated(DatabaseEvent.Update event)
	{
		// TODO: Change this to a proper panel event. Also explain WHAT updated
		final GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(screen instanceof INeedsRefresh) {
            ((INeedsRefresh)screen).refreshGui();
        }
	}
	
	@ForgeSubscribe
	public void onCommand(CommandEvent event)
	{
		MinecraftServer server = MinecraftServer.getServer();
		
		if(server != null && (event.command.getCommandName().equalsIgnoreCase("op") || event.command.getCommandName().equalsIgnoreCase("deop")))
		{
		    EntityPlayerMP playerMP = server.getConfigurationManager().getPlayerForUsername(event.parameters[0]);
			if(playerMP != null) ServerTickHandler.queueOp(playerMP); // Has to be delayed until after the event when the command has executed
		}
	}
}
