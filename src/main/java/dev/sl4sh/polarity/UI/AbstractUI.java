package dev.sl4sh.polarity.UI;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class AbstractUI implements PolarityUI {

    protected Inventory ui;

    @Override
    public final Inventory getUI() { return ui; }

    @Override
    public abstract void make();

    @Override
    public abstract void refreshUI();

    @Nonnull
    @Override
    public abstract Text getTitle();

    @Nonnull
    public abstract InventoryDimension getUIDimensions();

}
