package dev.sl4sh.polarity.UI.games;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.SharedUI;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import dev.sl4sh.polarity.games.GameManager;
import dev.sl4sh.polarity.games.SessionProperties;
import dev.sl4sh.polarity.games.GameSession;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
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

public class LobbySelectionUI extends SharedUI {

    final int gameID;
    final int pageID;

    @Nonnull
    @Override
    public Text getTitle() {
        return Text.of(TextColors.WHITE, "Lobby selection");
    }

    @Nonnull
    @Override
    public InventoryDimension getUIDimensions() {
        return new InventoryDimension(9, 5);
    }

    public LobbySelectionUI(int gameID, int pageID) {
        this.gameID = gameID;
        this.pageID = pageID;
    }

    @Override
    protected void onPrimary(ClickInventoryEvent.Primary event){

        ItemStack stack = event.getCursorTransaction().getFinal().createStack();

        if(!(event.getSource() instanceof Player)) { return; }

        Player player = (Player)event.getSource();

        if(stack.get(Polarity.Keys.UIStack.TYPE).isPresent() &&
                stack.get(Polarity.Keys.UIStack.DATA_ID).isPresent() &&
                stack.get(Polarity.Keys.UIStack.BUTTON_ID).isPresent()){

            StackTypes type = stack.get(Polarity.Keys.UIStack.TYPE).get();
            int buttonID = stack.get(Polarity.Keys.UIStack.BUTTON_ID).get();

            if(type.equals(StackTypes.LOBBY_SELECTION_STACK)){

                int wrapperID = stack.get(Polarity.Keys.UIStack.DATA_ID).get();

                if(!Utilities.isValidSessionID(wrapperID)) { player.sendMessage(Text.of(TextColors.RED, "Unable to join game session.")); return;}

                GameSession<?> session = Utilities.getGameSessionByID(wrapperID).get();
                session.joinSession(player, PlayerSessionRole.PLAYER);

            }
            else if(type.equals(StackTypes.LOBBY_CREATE_STACK)){

                if(Polarity.getPartyManager().getPlayerParty(player).isPresent() && !Polarity.getPartyManager().getPlayerParty(player).get().getPartyOwner().equals(player.getUniqueId())){

                    player.sendMessage(Text.of(TextColors.RED, "Only the party owner may join game lobbies."));
                    return;

                }

                GameManager manager = Polarity.getGameManager();

                // Delaying one tick so the world creation occurs on a new thread (doesn't cause lag on the main one)
                Utilities.delayOneTick(() ->{

                    Optional<GameSession<?>> session = manager.createNewGameSession(gameID, Utilities.getNextFreeWrapperID(), SessionProperties.getGameProperties(gameID, pageID, buttonID));

                    if(session.isPresent()){

                        session.get().joinSession(player, PlayerSessionRole.PLAYER);

                    }
                    else{

                        player.sendMessage(Text.of(TextColors.RED, "Unable to join game session."));

                    }

                });

            }

        }

    }

    @Override
    protected void onSecondary(ClickInventoryEvent.Secondary event){

        if(!(event.getSource() instanceof Player)) { return; }

        Player player = (Player)event.getSource();

        ItemStack stack = event.getCursorTransaction().getFinal().createStack();

        if(stack.get(Polarity.Keys.UIStack.TYPE).isPresent() &&
                stack.get(Polarity.Keys.UIStack.DATA_ID).isPresent() &&
                stack.get(Polarity.Keys.UIStack.BUTTON_ID).isPresent()){

            StackTypes type = stack.get(Polarity.Keys.UIStack.TYPE).get();
            int wrapperID = stack.get(Polarity.Keys.UIStack.DATA_ID).get();

            if(type.equals(StackTypes.LOBBY_SELECTION_STACK) && Utilities.isValidSessionID(wrapperID)){

                GameSession<?> session = Utilities.getGameSessionByID(wrapperID).get();

                Utilities.closePlayerInventory(player);
                session.joinSession(player, PlayerSessionRole.SPECTATOR);

            }

        }

    }

    @Override
    public void setupLayout(Inventory newUI) {

        int it = 1;
        int buttonIndex = 0;

        List<GameSession<?>> gameSessions = Utilities.getGameSessionsByGameID(gameID);

        for(Inventory slot : newUI.slots()){

            if(it == 11 || it == 14 || it == 17 || it == 29 || it == 32 || it == 35){

                slot.set(makeLobbyButton(gameSessions, buttonIndex));
                buttonIndex++;

            }
            else{

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS_PANE, 1, Text.of(), new ArrayList<>(), false);
                stack.offer(Keys.DYE_COLOR, DyeColors.BLACK);
                slot.set(stack);

            }

            it++;

        }

    }

    private ItemStack makeLobbyButton(List<GameSession<?>> gameSessions, int buttonIndex){

        SessionProperties properties = SessionProperties.getGameProperties(gameID, pageID, buttonIndex);

        for(GameSession<?> session : gameSessions){

            if(session.getProperties().getProfileID() == SessionProperties.getGameProperties(gameID, pageID, buttonIndex).getProfileID()){

                List<Text> loreList = new ArrayList<>();
                DyeColor dyeColor;

                loreList.add(Text.of(TextColors.GOLD, "State: ", session.getState().getNiceName()));
                loreList.add(Text.of(TextColors.AQUA, "Players: ", session.getActivePlayers().size(), "/", session.getProperties().getMaxPlayers()));

                if(session.getActivePlayers().size() < session.getProperties().getMaxPlayers()){

                    loreList.add(Text.of(TextColors.GREEN, "Left click to join"));
                    dyeColor = DyeColors.WHITE;

                }
                else{

                    loreList.add(Text.of(TextColors.RED, "Lobby Full"));
                    dyeColor = DyeColors.RED;

                }

                loreList.add(Text.of(TextColors.GREEN, "Right click to spectate"));


                if(session.getState().equals(GameSessionState.PRE_GAME) || session.getState().equals(GameSessionState.RUNNING) || session.getState().equals(GameSessionState.FINISHING)){

                    dyeColor = DyeColors.LIME;

                }

                ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS, 1, Text.of(TextColors.AQUA, properties.getSessionDisplayName()), loreList, true);
                stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.LOBBY_SELECTION_STACK);
                stack.offer(Polarity.Keys.UIStack.DATA_ID, session.getSessionID());
                stack.offer(Polarity.Keys.UIStack.BUTTON_ID, buttonIndex);
                stack.offer(Keys.DYE_COLOR, dyeColor);

                return stack;

            }

        }

        List<Text> loreList = new ArrayList<>();

        loreList.add(Text.of(TextColors.GOLD, "State: Inactive"));
        loreList.add(Text.of(TextColors.GREEN, "Left click to join"));

        ItemStack stack = Utilities.makeUIStack(ItemTypes.STAINED_GLASS, 1, Text.of(TextColors.AQUA, properties.getSessionDisplayName()), loreList, false);
        stack.offer(Keys.DYE_COLOR, DyeColors.GRAY);
        stack.offer(Polarity.Keys.UIStack.TYPE, StackTypes.LOBBY_CREATE_STACK);
        stack.offer(Polarity.Keys.UIStack.BUTTON_ID, buttonIndex);
        return stack;

    }

}
