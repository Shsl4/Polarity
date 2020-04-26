package dev.sl4sh.polarity.games.arena;

import com.flowpowered.math.vector.Vector3d;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.games.GameNotifications;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import dev.sl4sh.polarity.games.AbstractGameInstance;
import dev.sl4sh.polarity.games.GameSession;
import dev.sl4sh.polarity.games.PositionSnapshot;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.GoldenApples;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
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

public class ArenaGameInstance extends AbstractGameInstance {

    protected ArenaGameInstance(String gameWorldModel, @Nonnull GameSession<?> session) throws IllegalStateException {
        super(gameWorldModel, session);
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

    @Override
    public void setupPreGame(Map<Player, Integer> players, List<Player> spectators) {

        super.setupPreGame(players, spectators);

        ArenaPreset preset = Polarity.getGamePresets().getRandomGamePresetForGameID(getGameID()).orElse(ArenaPreset.getRandomArenaStaticPreset());

        for(Player player : players.keySet()){

            List<Enchantment> armorEnchantments = new ArrayList<>();
            armorEnchantments.add(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(10).build());

            player.setHelmet(ItemStack.builder().itemType(preset.helmetType).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Arena Helmet")).build());
            player.setChestplate(ItemStack.builder().itemType(preset.chestplateType).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Arena Chestplate")).build());
            player.setLeggings(ItemStack.builder().itemType(preset.leggingsType).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Arena Leggings")).build());
            player.setBoots(ItemStack.builder().itemType(preset.bootsType).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, armorEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Arena Boots")).build());

            List<Enchantment> swordEnchantments = new ArrayList<>();
            swordEnchantments.add(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(10).build());
            swordEnchantments.add(Enchantment.builder().type(EnchantmentTypes.SHARPNESS).level(1).build());

            List<Enchantment> bowEnchantments = new ArrayList<>();
            bowEnchantments.add(Enchantment.builder().type(EnchantmentTypes.UNBREAKING).level(10).build());
            bowEnchantments.add(Enchantment.builder().type(EnchantmentTypes.INFINITY).level(1).build());

            Utilities.givePlayer(player, ItemStack.builder().itemType(preset.swordType).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, swordEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Arena Sword")).build(), true);
            Utilities.givePlayer(player, ItemStack.builder().itemType(preset.bowType).add(Keys.UNBREAKABLE, true).add(Keys.ITEM_ENCHANTMENTS, bowEnchantments).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Arena Bow")).build(), true);
            Utilities.givePlayer(player, ItemStack.builder().itemType(ItemTypes.ARROW).build(), true);
            Utilities.givePlayer(player, ItemStack.builder().itemType(ItemTypes.GOLDEN_APPLE).add(Keys.GOLDEN_APPLE_TYPE, GoldenApples.GOLDEN_APPLE).build(), true);
            Utilities.givePlayer(player, ItemStack.builder().itemType(ItemTypes.COOKED_BEEF).quantity(8).build(), true);

        }

    }

    @Override
    public void handleGameStart() {

        super.handleGameStart();

        for(Player player : getSession().getActivePlayers()){

            player.sendTitle(Title.builder().title(Text.of(TextColors.RED, "Fight!")).subtitle(Text.of(TextColors.RED, "Good luck! You'll need it...")).fadeIn(5).stay(20).fadeOut(5).build());
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

                sessionPlayer.sendTitle(Title.builder().title(Text.of(TextColors.RED, "Sudden death!")).subtitle(Text.of(TextColors.RED, "Let's make it harder")).fadeIn(5).fadeOut(5).stay(20).actionBar(Text.EMPTY).build());

            }

            for(Player alivePlayer : getSession().getActivePlayers()){

                List<PotionEffect> effects = Arrays.asList(PotionEffect.builder().potionType(PotionEffectTypes.WEAKNESS).particles(false).duration(1000000).amplifier(2).build(),
                        PotionEffect.builder().potionType(PotionEffectTypes.WITHER).particles(false).duration(1000000).amplifier(1).build());
                alivePlayer.offer(Keys.POTION_EFFECTS, effects);

            }

        }


    }

    @Nonnull
    @Override
    public String getGameName() {
        return "Arena";
    }

    @Override
    public void notifyTime(int timeInSeconds, GameNotifications notificationType) {

        if(notificationType.equals(GameNotifications.PRE_GAME) && Arrays.asList(3, 2, 1).contains(timeInSeconds)){

            for(Player player : getSession().getActivePlayers()){

                player.sendTitle(Title.builder().title(Text.of(TextColors.RED, timeInSeconds)).subtitle(Text.of(TextColors.RED, "Get ready!")).fadeIn(5).stay(40).fadeOut(5).build());
                player.playSound(SoundTypes.BLOCK_NOTE_PLING, player.getPosition(), .25);

            }

        }

        if(notificationType.equals(GameNotifications.RUNNING_GAME) && Arrays.asList(60, 30, 15).contains(timeInSeconds)){

            for(Player player : getSession().getActivePlayers()){

                player.sendTitle(Title.builder().title(Text.of(TextColors.RED, "Arena")).subtitle(Text.of(TextColors.RED, timeInSeconds, " seconds remaining")).fadeIn(5).stay(20).fadeOut(5).build());
                player.playSound(SoundTypes.BLOCK_NOTE_BASS, player.getPosition(), .25);

            }

        }

    }

    @Override
    public int getGameID() {
        return 1;
    }

    @Override
    public void eliminatePlayer(Player player, Cause cause) {

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        if (cause.contains(this)){

            player.sendTitle(Title.builder().title(Text.of(TextColors.RED, "You died!")).subtitle(Text.of(TextColors.RED, "They've been better than you")).actionBar(Text.EMPTY).fadeIn(5).fadeOut(5).stay(60).build());

        }

        for(Player sessionPlayer : getSession().getSessionPlayers()){

            if(sessionPlayer != player){

                sessionPlayer.sendMessage(Text.of(TextColors.RED, "[", getGameName(), "] | ", player.getName(), " died! ", getSession().getActivePlayers().size(), " players remaining!"));

            }

        }

        super.eliminatePlayer(player, cause);

    }

    @Override
    public int getGameTimeInSeconds() {
        return 90;
    }

}
