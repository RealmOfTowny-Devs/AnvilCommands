package me.drkmatr1984.anvilstringcommand;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract interface AnvilGUI
{
  public abstract void setSlot(AnvilSlot paramAnvilSlot, ItemStack paramItemStack);
  
  public abstract void open();
  
  public abstract void patchGUI(Player paramPlayer);
  
  public static enum AnvilSlot
  {
    INPUT_LEFT(0), 
    INPUT_RIGHT(1), 
    OUTPUT(2);
    
    private int slot;
    
    private AnvilSlot(int slot) {
      this.slot = slot;
    }
    
    public int getSlot() {
      return this.slot;
    }
    
    public static AnvilSlot bySlot(int slot) {
      for (AnvilSlot anvilSlot : AnvilSlot.values()) {
        if (anvilSlot.getSlot() == slot) {
          return anvilSlot;
        }
      }
      
      return null;
    }
  }
  

  public static class AnvilClickEvent
  {
    private AnvilGUI.AnvilSlot slot;
    private String name;
    private boolean close = true;
    private boolean destroy = true;
    
    public AnvilClickEvent(AnvilGUI.AnvilSlot slot, String name) {
      this.slot = slot;
      this.name = name;
    }
    
    public AnvilGUI.AnvilSlot getSlot() {
      return this.slot;
    }
    
    public String getName() {
      return this.name;
    }
    
    public boolean getWillClose() {
      return this.close;
    }
    
    public void setWillClose(boolean close) {
      this.close = close;
    }
    
    public boolean getWillDestroy() {
      return this.destroy;
    }
    
    public void setWillDestroy(boolean destroy) {
      this.destroy = destroy;
    }
  }
  
  public static abstract interface AnvilClickEventHandler
  {
    public abstract void onAnvilClick(AnvilGUI.AnvilClickEvent paramAnvilClickEvent);
  }
}
