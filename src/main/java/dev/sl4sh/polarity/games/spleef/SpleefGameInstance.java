package dev.sl4sh.polarity.games.spleef;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.games.AbstractGameInstance;
import dev.sl4sh.polarity.games.GameSession;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
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
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SpleefGameInstance extends AbstractGameInstance {

    @Override
    public int getGameID() {
        return 0;
    }

    @Override
    protected boolean enableBlockDrops() {
        return false;
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
    public void eliminatePlayer(Player player, Object source, boolean hasLeft) {

        if(!isValidGame()) { return; }

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        super.eliminatePlayer(player, source, hasLeft);

        player.getInventory().clear();

        player.sendTitle(Title.builder().title(Text.of(TextColors.RED, "You died!")).subtitle(Text.of(TextColors.RED, "Better luck next time!")).actionBar(Text.EMPTY).fadeIn(5).fadeOut(40).stay(5).build());
        player.playSound(SoundTypes.ENTITY_WITHER_DEATH, player.getPosition(), 0.25);

        if(getSession().getActiveTeams().size() <= 1){

            handleGameEnd();
            return;

        }

        for(UUID sessionPlayerID : getSession().getSessionPlayers()){

            if(sessionPlayerID != player.getUniqueId()){

                Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) ->sessionPlayer.sendMessage(Text.of(TextColors.RED, "[", getGameName(), "] | ", player.getName(), " died! ", getSession().getActivePlayers().size(), " players remaining!")));

            }

        }

    }

    @Override
    public int getGameTimeInSeconds() {
        return 60;
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
        return TextColors.AQUA;
    }

    /**
     * This method should return the GameMode players should play in
     *
     * @return The GameMode
     */
    @Override
    public GameMode getMode() {
        return GameModes.SURVIVAL;
    }

    @Override
    public void setupPreGame() {

        if(!isValidGame()) { return; }

        super.setupPreGame();

        for(UUID playerID : getSession().getActivePlayers()){

            if(!Utilities.getPlayerByUniqueID(playerID).isPresent()) { continue; }

            Player player = Utilities.getPlayerByUniqueID(playerID).get();

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

    }

    @Override
    public void handleGameEnd() {

        if(!isValidGame()) { return; }

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        getSession().getSessionTask().ifPresent(Task::cancel);
        getSession().getNotificationTask().ifPresent(Task::cancel);

        if(getSession().getActiveTeams().size() == 1){

            Team winningTeam = getSession().getActiveTeams().get(0);

            for(UUID sessionPlayerID : getSession().getSessionPlayers()){

                Utilities.getPlayerByUniqueID(sessionPlayerID).ifPresent((sessionPlayer) -> sessionPlayer.sendTitle(Title.builder().title(Text.of(winningTeam.getColor(), winningTeam.getName(), " wins!")).subtitle(Text.of(winningTeam.getColor(), "Well played!")).actionBar(Text.EMPTY).fadeIn(5).fadeOut(5).stay(40).build()));

            }


            super.handleGameEnd();

        }
        else if(getSession().getActiveTeams().size() == 0){

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

                        List<PotionEffect> effects = Collections.singletonList(PotionEffect.builder().potionType(PotionEffectTypes.SLOWNESS).particles(false).duration(1000000).amplifier(1).build());
                        sessionPlayer.offer(Keys.POTION_EFFECTS, effects);

                    }

                });

            }

        }

    }

}
