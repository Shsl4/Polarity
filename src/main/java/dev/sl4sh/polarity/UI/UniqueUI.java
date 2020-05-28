package dev.sl4sh.polarity.UI;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public abstract class UniqueUI extends AbstractUI {

    @Nonnull
    final UUID targetViewer;

    public UniqueUI(@Nonnull UUID viewerID){

        this.targetViewer = viewerID;

    }

    protected final Optional<Player> getTargetViewer() { return Utilities.getPlayerByUniqueID(targetViewer); }

    public boolean open() {

        if(!getTargetViewer().isPresent()) { return false; }

        this.make();

        if (getTargetViewer().get().openInventory(getUI()).isPresent()) {

            onOpened();
            return true;

        }


        return false;

    }

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

    protected void onOpened() {}
    protected void onClosed(InteractInventoryEvent.Close event) {}

    @Override
    public final void refreshUI() { Utilities.delayOneTick(this::make); }

}
