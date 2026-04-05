package betterquesting.api2.client.gui.events;

import betterquesting.backport.Function;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.GuiOpenEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map.Entry;
import betterquesting.backport.Consumer;

/*
    Provides a means of broadcasting various things to and around the currently open GUI.
    Useful if your panel/canvas is contained within a screen you're not in control of but still needs to respond to events
 */
public class PEventBroadcaster
{
	public static PEventBroadcaster INSTANCE = new PEventBroadcaster();
	
	private final HashMap<Class<? extends PanelEvent>, PEventEntry<? extends PanelEvent>> entryList
            = new HashMap<Class<? extends PanelEvent>, PEventEntry<? extends PanelEvent>>();
	
	@Deprecated
	public void register(@Nonnull final IPEventListener l, @Nonnull Class<? extends PanelEvent> type)
	{
	    register(new Consumer<PanelEvent>() {
			@Override
			public void accept(PanelEvent panelEvent) {
				l.onPanelEvent(panelEvent);
			}
		}, type);
	}
	
	public void register(@Nonnull Consumer<PanelEvent> consumer, @Nonnull Class<? extends PanelEvent> type)
    {
        PEventEntry<?> pe = entryList.get(type);
        if (pe == null) {
            pe = new PEventEntry<PanelEvent>((Class)type);
            entryList.put(type, pe);
        }
        pe.registerListener(consumer);
    }
	
	public void register(@Nonnull Consumer<PanelEvent> consumer, @Nonnull Iterable<Class<? extends PanelEvent>> type)
    {
        for(Class<? extends PanelEvent> c : type) {
            PEventEntry<?> pe = entryList.get(c);
            if (pe == null) {
                pe = new PEventEntry<PanelEvent>((Class)c);
                entryList.put(c, pe);
            }
            pe.registerListener(consumer);
        }
    }
	
    @Deprecated
	public void unregister(final IPEventListener l)
	{
		unregister(new Consumer<PanelEvent>() {
			@Override
			public void accept(PanelEvent panelEvent) {
				l.onPanelEvent(panelEvent);
			}
		});
	}
	
	public void unregister(@Nonnull Consumer<PanelEvent> consumer)
    {
        for(PEventEntry<? extends PanelEvent> value : entryList.values()) {
            value.unregisterListener(consumer);
        }
    }
	
	public boolean postEvent(@Nonnull PanelEvent event)
	{
	    // We cycle over all entries incase we need to fire events for parent class types
		for(Entry<Class<? extends PanelEvent>, PEventEntry<? extends PanelEvent>> e : entryList.entrySet())
		{
		    if(!e.getKey().isAssignableFrom(event.getClass())) continue;
			e.getValue().fire(event);
		}
		
		return event.canCancel() && event.isCancelled();
	}
	
	/**
	 * Clears event listeners whenever a new GUI loads. If you must have cross GUI communication either handle this yourself or re-register the relevant listeners.
	 */
	@SubscribeEvent
	public void onGuiOpened(GuiOpenEvent event)
	{
		entryList.clear();
	}
}
