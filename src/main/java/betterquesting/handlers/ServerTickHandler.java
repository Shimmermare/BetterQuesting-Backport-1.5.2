package betterquesting.handlers;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.events.BQLivingUpdateEvent;
import betterquesting.api.questing.party.IParty;
import betterquesting.api2.storage.DBEntry;
import betterquesting.network.handlers.NetNameSync;
import betterquesting.questing.party.PartyInvitations;
import betterquesting.questing.party.PartyManager;
import betterquesting.storage.NameCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ServerTickHandler implements ITickHandler {
    private static final ArrayDeque<EntityPlayerMP> opQueue = new ArrayDeque<EntityPlayerMP>();
    private static boolean openToLAN = false;

    private static final ArrayDeque<FutureTask<?>> serverTasks = new ArrayDeque<FutureTask<?>>();
    private static Thread serverThread = null;

    @Override
    public void tickStart(EnumSet<TickType> enumSet, Object... objects) {
        if(serverThread == null) serverThread = Thread.currentThread();

        synchronized(serverTasks)
        {
            while(!serverTasks.isEmpty()) serverTasks.poll().run();
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> enumSet, Object... objects) {
        MinecraftServer server = MinecraftServer.getServer();

        if(!server.isDedicatedServer())
        {
            boolean tmp = openToLAN;
            openToLAN = server instanceof IntegratedServer && ((IntegratedServer)server).getPublic();
            if(openToLAN && !tmp) opQueue.addAll(server.getConfigurationManager().playerEntityList);
        } else if(!openToLAN)
        {
            openToLAN = true;
        }

        while(!opQueue.isEmpty())
        {
            EntityPlayerMP playerMP = opQueue.poll();
            if(playerMP != null && NameCache.INSTANCE.updateName(playerMP))
            {
                DBEntry<IParty> party = PartyManager.INSTANCE.getParty(QuestingAPI.getQuestingUUID(playerMP));
                if(party != null)
                {
                    NetNameSync.quickSync(null, party.getID());
                } else
                {
                    NetNameSync.sendNames(new EntityPlayerMP[]{playerMP}, new UUID[]{QuestingAPI.getQuestingUUID(playerMP)}, null);
                }
            }
        }

        if(server.getTickCounter() % 60 == 0) PartyInvitations.INSTANCE.cleanExpired();

        // === FIX FOR OnLivingUpdate FIRING MULTIPLE TIMES PER TICK ===
        //noinspection unchecked
        for(EntityPlayerMP player : (List<EntityPlayerMP>)server.getConfigurationManager().playerEntityList)
        {
            MinecraftForge.EVENT_BUS.post(new BQLivingUpdateEvent(player));
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel() {
        return "BetterQuestingServer";
    }

    public static void queueOp(EntityPlayerMP player) {
        opQueue.add(player);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static <T> ListenableFuture<T> scheduleServerTask(Callable<T> task)
    {
        if (task == null) {
            throw new NullPointerException("task");
        }

        if (Thread.currentThread() != serverThread)
        {
            ListenableFutureTask<T> listenablefuturetask = ListenableFutureTask.create(task);

            synchronized (serverTasks)
            {
                serverTasks.add(listenablefuturetask);
                return listenablefuturetask;
            }
        }
        else
        {
            try
            {
                return Futures.immediateFuture(task.call());
            }
            catch (Exception exception)
            {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
    }
}
