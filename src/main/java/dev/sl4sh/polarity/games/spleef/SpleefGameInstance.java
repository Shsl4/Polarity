package dev.sl4sh.polarity.games.spleef;

import com.flowpowered.math.vector.Vector3d;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityColors;
import dev.sl4sh.polarity.enums.games.GameNotifications;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import dev.sl4sh.polarity.games.AbstractGameInstance;
import dev.sl4sh.polarity.games.GameSession;
import dev.sl4sh.polarity.games.PositionSnapshot;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.*;

public class SpleefGameInstance extends AbstractGameInstance {

    @Override
    public int getGameID() {
        return 0;
    }

    /**
     * This method should set the players spawn locations when the game starts
     *
     * @param players The players and their bound team ID
     */
    @Override
    public void setPlayerSpawnLocations(Map<Player, Integer> players) {

        List<PositionSnapshot> locations = new ArrayList<>(Utilities.getPositionSnapshotsByTag(getGameWorld(), PositionSnapshot.Tags.SPAWN_ANY));

        for(Player player : players.keySet()){

            if(locations.size() > 0){

                Random rand = new Random();
                PositionSnapshot randSnap = locations.get(rand.nextInt(locations.size()));
                player.setLocationAndRotation(new Location<>(getGameWorld(), randSnap.getLocation()), randSnap.getRotation());
                player.offer(Keys.VELOCITY, Vector3d.ZERO);
                player.offer(Keys.FALL_DISTANCE, 0.0f);
                player.offer(Keys.FALL_TIME, 0);

                if(locations.size() > 1){

                    locations.remove(randSnap);

                }

                break;

            }
            else{

                player.setLocation(new Location<>(getGameWorld(), getGameWorld().getProperties().getSpawnPosition()));

            }

        }

    }

    /**
     * This method should set the spectators spawn locations when the game starts
     *
     * @param players The game spectators list
     */
    @Override
    public void setSpectatorsSpawnLocations(List<Player> players) {

        for (Player player : players){

            player.setLocation(new Location<World>(getGameWorld(), getGameWorld().getProperties().getSpawnPosition()));

        }

    }

    @Nonnull
    @Override
    public String getGameName() {
        return "Spleef";
    }

    public <T extends SpleefGameInstance> SpleefGameInstance(String gameWorldModel, @Nonnull GameSession<T> session) throws IllegalStateException {
        super(gameWorldModel, session);
    }

    @Override
    public void eliminatePlayer(Player player, Cause cause) {

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        player.getInventory().clear();

        super.eliminatePlayer(player, cause);

        if (cause.contains(this)){

            player.sendTitle(Title.builder().title(Text.of(TextColors.RED, "You died!")).subtitle(Text.of(TextColors.RED, "Better luck next time!")).actionBar(Text.EMPTY).fadeIn(5).fadeOut(40).stay(5).build());

        }

        if(getSession().getActivePlayers().size() <= 1){

            handleGameEnd();
            return;

        }

        for(Player sessionPlayer : getSession().getSessionPlayers()){

            if(sessionPlayer != player){

                sessionPlayer.sendMessage(Text.of(TextColors.RED, "[", getGameName(), "] | ", player.getName(), " died! ", getSession().getActivePlayers().size(), " players remaining!"));

            }

        }

    }

    @Override
    public int getGameTimeInSeconds() {
        return 60;
    }

    @Override
    public void setupPreGame(Map<Player, Integer> players, List<Player> spectators) {

        super.setupPreGame(players, spectators);

        for(Player player : getSession().getActivePlayers()){

            List<Enchantment> armorEnchantments = new ArrayList<>();
            armorEnchantments.add(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(3).build());

            player.setHelmet(ItemStack.builder().itemType(ItemTypes.LEATHER_HELMET).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Helmet")).build());
            player.setChestplate(ItemStack.builder().itemType(ItemTypes.LEATHER_CHESTPLATE).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Chestplate")).build());
            player.setLeggings(ItemStack.builder().itemType(ItemTypes.LEATHER_LEGGINGS).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Leggings")).build());
            player.setBoots(ItemStack.builder().itemType(ItemTypes.LEATHER_BOOTS).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Boots")).build());

            List<Enchantment> shovelEnchantments = new ArrayList<>();
            shovelEnchantments.add(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(3).build());
            shovelEnchantments.add(Enchantment.builder().type(EnchantmentTypes.EFFICIENCY).level(5).build());

            player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.builder().itemType(ItemTypes.IRON_SHOVEL).add(Keys.ITEM_ENCHANTMENTS, shovelEnchantments).add(Keys.UNBREAKABLE, true).add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Spleef Shovel")).build());

        }

    }

    @Override
    public void handleGameStart() {

        super.handleGameStart();

        for(Player player : getSession().getActivePlayers()){

            player.sendTitle(Title.builder().title(Text.of(TextColors.GREEN, "Let's Go!")).subtitle(Text.of(TextColors.GREEN, "Good luck and have fun!")).fadeIn(5).stay(20).fadeOut(5).build());
            player.playSound(SoundTypes.BLOCK_NOTE_PLING, player.getPosition(), .25, 2.0);

        }

    }

    @Override
    public void handleGameEnd() {

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        getSession().getSessionTask().ifPresent(Task::cancel);
        getSession().getNotificationTask().ifPresent(Task::cancel);

        if(getSession().getActivePlayers().size() == 1){

            Player winner = getSession().getActivePlayers().get(0);

            for(Player sessionPlayer : getSession().getSessionPlayers()){

                sessionPlayer.sendTitle(Title.builder().title(Text.of(TextColors.GREEN, winner.getName(), " wins!")).subtitle(Text.of(TextColors.GREEN, "Well played!")).actionBar(Text.EMPTY).fadeIn(5).fadeOut(5).stay(40).build());

            }

            super.handleGameEnd();

        }
        else if(getSession().getActivePlayers().size() == 0){

            for(Player sessionPlayer : getSession().getSessionPlayers()){

                sessionPlayer.sendTitle(Title.builder().title(Text.of(TextColors.GRAY, "It's a tie!")).subtitle(Text.of(TextColors.GRAY, "No one wins on this one")).fadeIn(5).fadeOut(5).stay(40).actionBar(Text.EMPTY).build());

            }

            super.handleGameEnd();

        }
        else{

            for(Player sessionPlayer : getSession().getSessionPlayers()){

                sessionPlayer.sendTitle(Title.builder().title(Text.of(TextColors.RED, "Sudden death!")).subtitle(Text.of(TextColors.RED, "Let's make it harder")).fadeIn(5).fadeOut(5).stay(40).actionBar(Text.EMPTY).build());

            }

            for(Player alivePlayer : getSession().getActivePlayers()){

                List<PotionEffect> effects = Collections.singletonList(PotionEffect.builder().potionType(PotionEffectTypes.SLOWNESS).particles(false).duration(1000000).amplifier(1).build());
                alivePlayer.offer(Keys.POTION_EFFECTS, effects);

            }

        }

    }

    @Override
    public void notifyTime(int timeInSeconds, GameNotifications notificationType) {

        if(notificationType.equals(GameNotifications.PRE_GAME) && Arrays.asList(3, 2, 1).contains(timeInSeconds)){

            for(Player player : getSession().getActivePlayers()){

                player.sendTitle(Title.builder().title(Text.of(TextColors.GREEN, timeInSeconds)).subtitle(Text.of(TextColors.GREEN, "Get ready!")).fadeIn(5).stay(40).fadeOut(5).build());
                player.playSound(SoundTypes.BLOCK_NOTE_PLING, player.getPosition(), .25);

            }

        }

    }

}
