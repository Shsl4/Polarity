package dev.sl4sh.polarity.games.arena;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.enums.games.GameNotifications;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.games.AbstractGameInstance;
import dev.sl4sh.polarity.games.GameSession;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.GoldenApples;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.*;

public class ArenaGameInstance extends AbstractGameInstance {

    protected ArenaGameInstance(String gameWorldModel, @Nonnull GameSession<?> session) throws IllegalStateException {
        super(gameWorldModel, session);
    }

    @Override
    public void setupPreGame() {

        if(!isValidGame()) { return; }

        super.setupPreGame();

        ArenaPreset preset = Polarity.getGamePresets().getRandomGamePresetForGameID(getGameID()).orElse(ArenaPreset.getRandomArenaStaticPreset());

        for(UUID playerID : getSession().getActivePlayers()){

            if(!Utilities.getPlayerByUniqueID(playerID).isPresent()) { continue; }

            Player player = Utilities.getPlayerByUniqueID(playerID).get();

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
    public void handleGameEnd() {

        if(!isValidGame()) { return; }

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        getSession().getSessionTask().ifPresent(Task::cancel);
        getSession().getNotificationTask().ifPresent(Task::cancel);

        if(getSession().getActivePlayers().size() == 1){

            UUID winnerID = getSession().getActivePlayers().get(0);

            if(Utilities.getPlayerByUniqueID(winnerID).isPresent()){

                for(UUID sessionPlayerID : getSession().getSessionPlayers()){

                    Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> sessionPlayer.sendTitle(Title.builder().title(Text.of(TextColors.GREEN, Utilities.getPlayerByUniqueID(winnerID).get().getName(), " wins!")).subtitle(Text.of(TextColors.GREEN, "Well played!")).actionBar(Text.EMPTY).fadeIn(5).fadeOut(5).stay(40).build()));

                }

            }

            super.handleGameEnd();

        }
        else if(getSession().getActivePlayers().size() == 0){

            for(UUID sessionPlayerID : getSession().getSessionPlayers()){

                Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> sessionPlayer.sendTitle(Title.builder().title(Text.of(TextColors.GRAY, "It's a tie!")).subtitle(Text.of(TextColors.GRAY, "No one wins on this one")).fadeIn(5).fadeOut(5).stay(40).actionBar(Text.EMPTY).build()));

            }

            super.handleGameEnd();

        }
        else{

            for(UUID sessionPlayerID : getSession().getSessionPlayers()){

                Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> {

                    sessionPlayer.sendTitle(Title.builder().title(Text.of(TextColors.RED, "Sudden death!")).subtitle(Text.of(TextColors.RED, "Let's make it harder")).fadeIn(5).fadeOut(5).stay(20).actionBar(Text.EMPTY).build());

                    if(getSession().getActivePlayers().contains(sessionPlayerID)){

                        List<PotionEffect> effects = Arrays.asList(PotionEffect.builder().potionType(PotionEffectTypes.WEAKNESS).particles(false).duration(1000000).amplifier(2).build(),
                                PotionEffect.builder().potionType(PotionEffectTypes.WITHER).particles(false).duration(1000000).amplifier(1).build());
                        sessionPlayer.offer(Keys.POTION_EFFECTS, effects);

                    }

                });

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

        if(!isValidGame()) { return; }

        super.notifyTime(timeInSeconds, notificationType);

        if(notificationType.equals(GameNotifications.RUNNING_GAME) && Arrays.asList(60, 30, 15).contains(timeInSeconds)){

            for(UUID playerID : getSession().getActivePlayers()){

                Utilities.getPlayerByUniqueID(playerID).ifPresent((player) -> {

                    player.sendTitle(Title.builder().title(Text.of(TextColors.RED, "Arena")).subtitle(Text.of(TextColors.RED, timeInSeconds, " seconds remaining")).fadeIn(5).stay(20).fadeOut(5).build());
                    player.playSound(SoundTypes.BLOCK_NOTE_BASS, player.getPosition(), .25);

                });

            }

        }

    }

    @Override
    public int getGameID() {
        return 1;
    }

    @Override
    public void eliminatePlayer(Player player, Object source, boolean hasLeft) {

        if(!isValidGame()) { return; }

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        super.eliminatePlayer(player, source, hasLeft);

        if(!hasLeft){

            player.sendTitle(Title.builder().title(Text.of(TextColors.RED, "You died!")).subtitle(Text.of(TextColors.RED, "They've been better than you")).actionBar(Text.EMPTY).fadeIn(5).fadeOut(5).stay(60).build());

        }

        for(UUID sessionPlayerID : getSession().getSessionPlayers()){

            Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> {

                if(sessionPlayer != player){

                    if(getSession().getActivePlayers().size() > 1){

                        sessionPlayer.sendMessage(Text.of(TextColors.RED, "[", getGameName(), "] | ", player.getName(), " died! ", getSession().getActivePlayers().size(), " players remaining!"));

                    }

                }

                sessionPlayer.playSound(SoundTypes.ENTITY_WITHER_DEATH, sessionPlayer.getPosition(), 0.25);

            });

        }

    }

    @Override
    public int getGameTimeInSeconds() {
        return 90;
    }

    @Override
    public void rewardPlayers() {

        if(!Polarity.getEconomyService().isPresent()) { return; }

        PolarityEconomyService service = Polarity.getEconomyService().get();
        PolarityCurrency currency = new PolarityCurrency();

        for(UUID playerID : getSession().getActivePlayers()){

            service.getOrCreateAccount(playerID).get().deposit(currency, BigDecimal.valueOf(2.5), Cause.of(EventContext.empty(), this));

        }

    }

    /**
     * This method should return a color that will be used to color displayed texts
     *
     * @return The game's color
     */
    @Override
    public TextColor getGameTintColor() {
        return TextColors.RED;
    }

    /**
     * This method should return the GameMode players should play in
     *
     * @return The GameMode
     */
    @Override
    public GameMode getMode() {
        return GameModes.ADVENTURE;
    }

}
