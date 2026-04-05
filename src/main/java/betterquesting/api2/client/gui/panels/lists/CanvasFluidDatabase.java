package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.content.PanelFluidSlot;
import betterquesting.core.BetterQuesting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.logging.Level;

public class CanvasFluidDatabase extends CanvasSearch<LiquidStack, String>
{
    private final int btnId;
    
    public CanvasFluidDatabase(IGuiRect rect, int buttonId)
    {
        super(rect);
        
        this.btnId = buttonId;
    }
    
    @Override
    protected Iterator<String> getIterator()
    {
        return LiquidDictionary.getLiquids().keySet().iterator();
    }
    
    @Override
    protected void queryMatches(String liquidName, String query, final ArrayDeque<LiquidStack> results)
    {
        if(liquidName == null)
        {
            return;
        }
        LiquidStack liquidStack = LiquidDictionary.getLiquid(liquidName, 1000);
        if (liquidStack == null) {
            return;
        }
        
        try
        {
            ItemStack itemStack = liquidStack.asItemStack();
            Item item = itemStack.getItem();

            if(item.getUnlocalizedName().toLowerCase().contains(query)
                    || item.getLocalizedName(itemStack).toLowerCase().contains(query)
                    || liquidName.toLowerCase().contains(query))
            {
                results.add(liquidStack);
            }
        } catch(Exception e)
        {
            BetterQuesting.logger.log(Level.SEVERE, "An error occurred while searching fluid \"" + liquidName
                    + "\" (" + liquidName.getClass().getName() + ")", e);
        }
    }
    
    @Override
    protected boolean addResult(LiquidStack stack, int index, int cachedWidth)
    {
        if(stack == null)
        {
            return false;
        }
        
        int x = (index % (cachedWidth / 18)) * 18;
        int y = (index / (cachedWidth / 18)) * 18;
        
        this.addPanel(new PanelFluidSlot(new GuiRectangle(x, y, 18, 18, 0), btnId, stack));
        
        return true;
    }
}
