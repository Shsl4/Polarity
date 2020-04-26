package dev.sl4sh.polarity.UI;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface PolarityUI {

    void make();

    void refreshUI();

    Inventory getUI();

    @Nonnull
    Text getTitle();

    @Nonnull
    InventoryDimension getUIDimensions();

}
