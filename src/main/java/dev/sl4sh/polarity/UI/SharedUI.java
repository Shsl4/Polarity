package dev.sl4sh.polarity.UI;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;

import java.util.HashSet;
import java.util.Set;

public abstract class SharedUI extends AbstractUI{

    public SharedUI() {}

    @Override
    public final void make() {

        if(ui == null){

            ui = Inventory.builder()
                    .property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(getTitle()))
                    .property(InventoryDimension.PROPERTY_NAME, getUIDimensions())
                    .listener(ClickInventoryEvent.class, this::onInteract)
                    .listener(ClickInventoryEvent.Primary.class, this::handleOnPrimary)
                    .listener(ClickInventoryEvent.Secondary.class, this::handleOnSecondary)
                    .listener(InteractInventoryEvent.Close.class, this::handleOnClosed)
                    .listener(InteractInventoryEvent.Open.class, this::handleOnOpened)
                    .build(Polarity.getPolarity());

        }

        this.setupLayout(ui);

    }

    protected abstract void setupLayout(Inventory newUI);

    private void handleOnPrimary(ClickInventoryEvent.Primary event) { ItemStack stack = event.getCursorTransaction().getFinal().createStack(); event.setCancelled(Utilities.isUIStack(stack)); Utilities.delayOneTick(() -> this.onPrimary(event)); }
    private void handleOnSecondary(ClickInventoryEvent.Secondary event) { ItemStack stack = event.getCursorTransaction().getFinal().createStack(); event.setCancelled(Utilities.isUIStack(stack)); Utilities.delayOneTick(() -> this.onSecondary(event)); }
    private void handleOnClosed(InteractInventoryEvent.Close event) { event.getCursorTransaction().setValid(false); Utilities.delayOneTick(() -> this.onClosed(event)); }
    private void handleOnOpened(InteractInventoryEvent.Open event) { event.setCancelled(false); }

    protected void onInteract(InteractInventoryEvent event) { event.setCancelled(true); }
    protected void onPrimary(ClickInventoryEvent.Primary event) {}
    protected void onSecondary(ClickInventoryEvent.Secondary event) {}

    protected void onOpened(Player player) {}
    protected void onClosed(InteractInventoryEvent.Close event) {}

    @Override
    public final void refreshUI() { Utilities.delayOneTick(this::make); }

    public Set<Player> getViewers(){

        Player player;

        if(getUI() != null){

            Container container = (Container)getUI();
            return container.getViewers();

        }

        return new HashSet<>();

    }

    public boolean openFor(Player player){

        this.make();

        if(player.openInventory(getUI()).isPresent()){

            onOpened(player);
            return true;

        }

        return false;

    }

}
