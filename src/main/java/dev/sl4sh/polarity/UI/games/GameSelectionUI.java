package dev.sl4sh.polarity.UI.games;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.SharedUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameSelectionUI extends SharedUI {

    @Nonnull
    @Override
    public Text getTitle() { return Text.of(TextColors.GOLD, "Game selection"); }

    @Nonnull
    private final List<LobbySelectionUI> lobbySelectionUIs = new ArrayList<>();

    @Nonnull
    public final List<LobbySelectionUI> getLobbySelectionUIs(){

        return this.lobbySelectionUIs;

    }

    public GameSelectionUI(){

        for(int value : Polarity.getGameManager().getValidGameIDs()){

            lobbySelectionUIs.add(new LobbySelectionUI(value, 0));

        }

    }

    private Optional<LobbySelectionUI> getLobbySelectionUIByGameID(int id){

        for(LobbySelectionUI ui : lobbySelectionUIs){

            if (ui.gameID == id){

                return Optional.of(ui);

            }

        }

        return Optional.empty();

    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 3);
    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event){

        if(!(event.getSource() instanceof Player)) { return; }

        Player player = (Player)event.getSource();

        ItemStack stack = event.getCursorTransaction().getFinal().createStack();

        if(stack.get(Polarity.Keys.UIStack.TYPE).isPresent() &&
                stack.get(Polarity.Keys.UIStack.DATA_ID).isPresent()){

            StackTypes type = stack.get(Polarity.Keys.UIStack.TYPE).get();
            int gameID = stack.get(Polarity.Keys.UIStack.DATA_ID).get();

            if(type.equals(StackTypes.GAME_SELECTION_STACK) && Utilities.isValidGameID(gameID)){

                if(getLobbySelectionUIByGameID(gameID).isPresent()){

                    LobbySelectionUI lobbyUI = getLobbySelectionUIByGameID(gameID).get();
                    lobbyUI.openFor(player);

                }

            }

        }

    }

    @Override
    public void setupLayout(Inventory newUI) {

        int it = 1;

        for(Inventory slot : newUI.slots()){

            if(it == 12){

                List<Text> loreList = new ArrayList<>();
                loreList.add(Text.of(TextColors.YELLOW, "Try and defeat your enemies by making"));
                loreList.add(Text.of(TextColors.YELLOW, "them fall into lava!"));

                ItemStack stack = Utilities.makeUIStack(ItemTypes.IRON_SHOVEL, 1, Text.of(TextColors.AQUA, "Spleef"), loreList, true);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.GAME_SELECTION_STACK);
                stack.offer(Polarity.Keys.UIStack.DATA_ID, 0);
                slot.set(stack);

                it++;

                continue;

            }

            if(it == 14){

                List<Text> loreList = new ArrayList<>();
                loreList.add(Text.of(TextColors.YELLOW, "Fight your opponents in a"));
                loreList.add(Text.of(TextColors.YELLOW, "closed arena!"));

                ItemStack stack = Utilities.makeUIStack(ItemTypes.DIAMOND_SWORD, 1, Text.of(TextColors.RED, "Arena"), loreList, true);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.GAME_SELECTION_STACK);
                stack.offer(Polarity.Keys.UIStack.DATA_ID, 1);
                slot.set(stack);

                it++;

                continue;

            }

            if(it == 16){

                List<Text> loreList = new ArrayList<>();
                loreList.add(Text.of(TextColors.YELLOW, "The classic island mini-game!"));

                ItemStack stack = Utilities.makeUIStack(ItemTypes.OBSIDIAN, 1, Text.of(TextColors.DARK_PURPLE, "Rush"), loreList, true);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.GAME_SELECTION_STACK);
                stack.offer(Polarity.Keys.UIStack.DATA_ID, 2);
                slot.set(stack);

                it++;

                continue;

            }

            ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(), new ArrayList<>(), false);
            stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
            slot.set(stack);

            it++;

        }

    }

}
