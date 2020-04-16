package dev.sl4sh.polarity.games.spleef;

import com.flowpowered.math.vector.Vector3d;
import dev.sl4sh.polarity.games.AbstractGame;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SpleefGame extends AbstractGame {

    int maxPlayers;

    protected SpleefGame(String spleefWorldModel, int maxPlayers) throws IllegalStateException {

        super(spleefWorldModel);

        if(maxPlayers <= 0) { throw new IllegalStateException("Tried to create a game with a negative or null player capacity"); }

        this.maxPlayers = maxPlayers;

    }

        /*@Listener
    public void onPlayerMove(MoveEntityEvent event){

        if(getState().equals(GameState.PRE_GAME)){

            if(event.getTargetEntity() instanceof Player){

                Player player = (Player)event.getTargetEntity();

                if(getActivePlayers().contains(player)){

                    event.setCancelled(true);

                }

            }

        }

    }*/

    /*@Listener
    public void onBlockBroken(ChangeBlockEvent.Break event){

        for(Transaction<BlockSnapshot> snap : event.getTransactions()){

            if(snap.getFinal().getWorldUniqueId().equals(getGameWorld().getUniqueId())){

                if(snap.getFinal().getState().getType().equals(BlockTypes.SNOW)){

                   return;

                }

            }

        }

        event.setCancelled(true);

    }*/

    @Nonnull
    @Override
    public Vector3d getSpawnLocationForPlayer(Player player) {
        return getGameWorld().getProperties().getSpawnPosition().toDouble();
    }

    @Nonnull
    @Override
    public String getGameName() {
        return "Spleef";
    }

    @Override
    public void notifyTimeBeforeStart(double timeInSeconds) {

        for(Player player : getActivePlayers()){

            player.sendTitle(Title.builder().title(Text.of(TextColors.AQUA, (int)timeInSeconds)).actionBar(Text.of()).subtitle(Text.of()).fadeIn(5).stay(40).fadeOut(5).build());

        }

    }

    @Override
    public void notifyTimeBeforeEnd(double timeInSeconds) {

    }

    @Override
    public void onPlayerJoinedGame(Player player) {
        super.onPlayerJoinedGame(player);
    }

    @Override
    public void onPlayerLeftGame(Player player) {
        super.onPlayerLeftGame(player);
    }

    @Override
    public void eliminatePlayer(Player player) {

        super.eliminatePlayer(player);
        player.getInventory().clear();

    }

    @Override
    public long getGameTimeInSeconds() {
        return 10;
    }

    @Override
    public void setupPreGame(List<Player> players) {

        super.setupPreGame(players);

        for(Player player : getActivePlayers()){

            /*Utilities.savePlayerInventory(player);
            player.getInventory().clear();*/

            List<Enchantment> armorEnchantments = new ArrayList<>();
            armorEnchantments.add(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(3).build());

            player.setHelmet(ItemStack.builder().itemType(ItemTypes.LEATHER_HELMET).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Helmet")).build());
            player.setChestplate(ItemStack.builder().itemType(ItemTypes.LEATHER_CHESTPLATE).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Chestplate")).build());
            player.setLeggings(ItemStack.builder().itemType(ItemTypes.LEATHER_LEGGINGS).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Leggings")).build());
            player.setBoots(ItemStack.builder().itemType(ItemTypes.LEATHER_BOOTS).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Boots")).build());

            List<Enchantment> shovelEnchantments = new ArrayList<>();
            shovelEnchantments.add(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(3).build());
            shovelEnchantments.add(Enchantment.builder().type(EnchantmentTypes.EFFICIENCY).level(5).build());

            player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.builder().itemType(ItemTypes.IRON_SHOVEL).add(Keys.ITEM_ENCHANTMENTS, shovelEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Shovel")).build());

        }

    }

    @Override
    public void startGame() {

        super.startGame();

        for(Player player : getActivePlayers()){

            player.sendTitle(Title.builder().title(Text.of(TextColors.GREEN, "Let's Go!")).subtitle(Text.of(TextColors.GREEN, "Good luck and have fun!")).fadeIn(5).stay(40).fadeOut(5).build());

        }

    }

    @Override
    public void onGameEnd() {

        super.onGameEnd();

        for(Player player : getGameWorld().getPlayers()){

            player.sendTitle(Title.builder().title(Text.of(TextColors.AQUA, "It's Over!")).subtitle(Text.of(TextColors.AQUA, "Bye bye!")).fadeIn(5).stay(40).fadeOut(5).build());

        }

    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

}
