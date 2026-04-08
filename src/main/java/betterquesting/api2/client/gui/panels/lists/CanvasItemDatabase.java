package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.content.PanelItemSlot;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.core.BetterQuesting;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.logging.Level;

public class CanvasItemDatabase extends CanvasSearch<ItemStack, Item>
{
    private final int btnId;
    
    public CanvasItemDatabase(IGuiRect rect, int buttonId)
    {
        super(rect);
        
        this.btnId = buttonId;
    }
    
    @Override
    protected Iterator<Item> getIterator()
    {
        return Arrays.asList(Item.itemsList).iterator();
    }
    
    @Override
    protected void queryMatches(Item item, String query, final ArrayDeque<ItemStack> results)
    {
        if(item == null) return;
        
        String idStr = String.valueOf(item.itemID);
        
        try
        {
            final List<ItemStack> subList = new ArrayList<ItemStack>();
            
            item.getSubItems(item.itemID, CreativeTabs.tabAllSearch, subList);
            if(subList.isEmpty()) subList.add(new ItemStack(item));
            
            if(idStr.toLowerCase().contains(query) || item.getUnlocalizedName().toLowerCase().contains(query) || QuestTranslation.translate(item.getUnlocalizedName()).toLowerCase().contains(query))
            {
                results.addAll(subList);
                return;
            }

            // FIXME: Return parallelism
            for (ItemStack subItem : subList) {
                try
                {
                    if(subItem.getItemName().toLowerCase().contains(query) || subItem.getDisplayName().toLowerCase().contains(query))
                    {
                        results.add(subItem);
                        continue;
                    }

                    boolean anyMatch = false;
                    for (int id : OreDictionary.getOreIDs(subItem)) {
                        if (OreDictionary.getOreName(id).toLowerCase().contains(query)) {
                            anyMatch = true;
                            break;
                        }
                    }
                    if (anyMatch) {
                        results.add(subItem);
                        continue;
                    }
                } catch(Exception e)
                {
                    BetterQuesting.logger.log(Level.SEVERE, "An error occured while searching itemstack " + subItem.toString() + " from item \"" + idStr + "\" (" + item.getClass().getName() + ").\nNBT: " + subItem.writeToNBT(new NBTTagCompound()), e);
                }
            }
        } catch(Exception e)
        {
            BetterQuesting.logger.log(Level.SEVERE, "An error occured while searching item \"" + idStr + "\" (" + item.getClass().getName() + ")", e);
        }
    }
    
    @Override
    public boolean addResult(ItemStack stack, int index, int cachedWidth)
    {
        if(stack == null) return false;
        
        int x = (index % (cachedWidth / 18)) * 18;
        int y = (index / (cachedWidth / 18)) * 18;
        
        this.addPanel(new PanelItemSlot(new GuiRectangle(x, y, 18, 18, 0), btnId, new BigItemStack(stack)));
        
        return true;
    }
}
