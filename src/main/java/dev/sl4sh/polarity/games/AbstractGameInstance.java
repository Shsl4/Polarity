package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.enums.PolarityColor;
import dev.sl4sh.polarity.enums.games.GameNotifications;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.storage.WorldProperties;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractGameInstance implements GameInstance {

    private UUID gameWorldID;
    private final GameSession<?> session;
    private boolean gameValid;

    private Date startTime;

    /**
     * The game system works by copying an existing world template (which should have been prepared on the server before utilization),
     * then use this whole new separate world to handle everything as we want. Once the game is over, the game world gets destroyed {@link #destroyGame()}} and
     * the game instance is marked invalid {@link #invalidateGame()}.
     * @param gameWorldName The world template name
     * @param session The game's assigned session.
     * @throws IllegalStateException If a creation error occurs. Use {@link Exception#getMessage()} to figure out what.
     */
    protected AbstractGameInstance(String gameWorldName, @Nonnull GameSession<?> session) throws IllegalStateException{

        this.session = session;

        try{

            Sponge.getServer().loadWorld(gameWorldName);

            if(!Sponge.getServer().getWorld(gameWorldName).isPresent()) { throw new IllegalStateException("The specified game model name does not exist: " + gameWorldName); }

            CompletableFuture<Optional<WorldProperties>> props = Sponge.getServer().copyWorld(Sponge.getServer().getWorld(gameWorldName).get().getProperties(), gameWorldName + "-" + (new Date().toString()).replace(":", "-"));

            if(!props.get().isPresent()) { throw new IllegalStateException("The properties failed to create."); }

            WorldProperties worldProps = props.get().get();

            worldProps.setPVPEnabled(getSession().getProperties().getPVP());
            worldProps.setGameMode(getMode());
            worldProps.setDifficulty(Difficulties.HARD);
            worldProps.setWorldTime(18000);
            worldProps.setRaining(false);
            worldProps.setGameRule("doWeatherCycle", "false");
            worldProps.setGameRule("doDaylightCycle", "false");
            worldProps.setGameRule("announceAdvancements", "false");
            worldProps.setGameRule("commandBlocksEnabled", "true");
            worldProps.setGameRule("commandBlockOutput", "false");
            worldProps.setGameRule("keepInventory", "false");
            worldProps.setGameRule("doTileDrops", String.valueOf(enableBlockDrops()));

            Optional<World> loadResult = Sponge.getServer().loadWorld(worldProps);

            if(!loadResult.isPresent()) {

                Sponge.getServer().deleteWorld(props.get().get());
                throw new IllegalStateException("Could not load the newly created world.");

            }

            gameWorldID = loadResult.get().getUniqueId();

            Utilities.createWorldInfoFrom(loadResult.get(), Sponge.getServer().getWorld(gameWorldName).get());
            Utilities.getOrCreateWorldInfo(loadResult.get()).setIsGameWorld(true);
            this.gameValid = true;

        }
        catch(InterruptedException | ExecutionException e){

            this.destroyGame();
            e.printStackTrace();

        }

    }

    protected boolean enableBlockDrops() { return true; }

    /**
     * This method should return the world the game is occurring in
     *
     * @return The game's world
     */
    @Override
    public final Optional<World> getGameWorld() { return Sponge.getServer().getWorld(this.gameWorldID); }

    /**
     * This method should return the game's name. Used for displaying.
     *
     * @return The game's name
     */
    @Nonnull
    @Override
    public abstract String getGameName();

    /**
     * This method should return a reference to the {@link GameSession} this game instance is part of.
     *
     * @return The {@link GameSession}
     */
    @Nonnull
    @Override
    public final GameSession<?> getSession() { return this.session; }

    /**
     * This method should return the unique game type identifier.
     *
     * @return The game type ID
     */
    @Override
    public abstract int getGameID();

    /**
     * This method should return how many time the game lasts (in seconds)
     *
     * @return The game time in seconds
     */
    @Override
    public abstract int getGameTimeInSeconds();

    /**
     * This method should set the players spawn locations when the game starts
     */
    @Override
    public final void setPlayersSpawnLocations(){

        if(!this.isValidGame()) { return; }

        for(Team team : getSession().getTeams()){

            String snapTag = "";

            TextColor color = team.getColor();

            if (TextColors.WHITE.equals(color)) {

                snapTag = PositionSnapshot.Tags.WHITE_SPAWN;

            } else if (TextColors.AQUA.equals(color)) {

                snapTag = PositionSnapshot.Tags.CYAN_SPAWN;

            } else if (TextColors.GOLD.equals(color)) {

                snapTag = PositionSnapshot.Tags.GOLD_SPAWN;

            } else if (TextColors.DARK_PURPLE.equals(color)) {

                snapTag = PositionSnapshot.Tags.PURPLE_SPAWN;

            } else if (TextColors.NONE.equals(color)) {

                snapTag = PositionSnapshot.Tags.ANY_SPAWN;

            }

            spawnPlayersFromNameWithTag(team.getMembers(), snapTag);

        }

    }


    /**
     * This method should set the spectators spawn locations when the game starts
     */
    @Override
    public final void setSpectatorsSpawnLocations(){

        spawnPlayersFromUUIDWithTag(getSession().getSpectatingPlayers(), PositionSnapshot.Tags.SPECTATOR_SPAWN);

    }

    public long getTimeElapsed(TimeUnit unit){

        if(startTime != null){

            Date now = new Date();
            return unit.convert(now.getTime() - startTime.getTime(), TimeUnit.MILLISECONDS);

        }

        return 0L;

    }

    private void spawnPlayersFromUUIDWithTag(List<UUID> players, String tag){

        if(!getGameWorld().isPresent()) { return; }

        List<PositionSnapshot> locations = new ArrayList<>(Utilities.getPositionSnapshotsByTag(getGameWorld().get(), tag));

        for(UUID playerID : players){

            Optional<Player> player = Utilities.getPlayerByUniqueID(playerID);

            if(!player.isPresent()) { continue; }

            if(locations.size() > 0){

                List<PositionSnapshot> used = new ArrayList<>();
                List<PositionSnapshot> newList = new ArrayList<>(locations);
                newList.removeAll(used);

                PositionSnapshot snap = newList.get(new Random().nextInt(newList.size()));

                if (used.size() + 1 < locations.size()) { used.add(snap); } else used.clear();

                player.get().setLocationAndRotation(new Location<>(getGameWorld().get(), snap.getLocation()), snap.getRotation().toDouble());

            }
            else{

                player.get().setLocation(new Location<>(getGameWorld().get(), getGameWorld().get().getProperties().getSpawnPosition()));

            }

        }

    }

    private void spawnPlayersFromNameWithTag(Set<Text> players, String tag){

        if(!getGameWorld().isPresent()) { return; }

        List<PositionSnapshot> locations = new ArrayList<>(Utilities.getPositionSnapshotsByTag(getGameWorld().get(), tag));

        for(Text playerName : players){

            Optional<Player> player = Utilities.getPlayerByName(playerName.toPlain());

            if(!player.isPresent()) { continue; }

            if(locations.size() > 0){

                List<PositionSnapshot> used = new ArrayList<>();
                List<PositionSnapshot> newList = new ArrayList<>(locations);
                newList.removeAll(used);

                PositionSnapshot snap = newList.get(new Random().nextInt(newList.size()));

                if (used.size() + 1 < locations.size()) { used.add(snap); } else used.clear();

                player.get().setLocationAndRotation(new Location<>(getGameWorld().get(), snap.getLocation()), snap.getRotation().toDouble());

            }
            else{

                player.get().setLocation(new Location<>(getGameWorld().get(), getGameWorld().get().getProperties().getSpawnPosition()));

            }

        }

    }

    /**
     * This method should handle when a player joins the game.
     *
     * @param player The player who joined.
     * @param role The player's role
     */
    @Override
    public void handlePlayerJoin(Player player, PlayerSessionRole role) {

        if(!getGameWorld().isPresent()) { return; }

        spawnPlayersFromUUIDWithTag(Collections.singletonList(player.getUniqueId()), PositionSnapshot.Tags.SPECTATOR_SPAWN);
        Utilities.setGameMode(player, getSpectatorMode());

    }

    /**
     * This method should handle when a player leaves the game.
     *
     * @param player The player who left.
     */
    @Override
    public void handlePlayerLeft(Player player) {

        eliminatePlayer(player, Cause.of(EventContext.empty(), player), true);

        if(getSession().getActivePlayers().size() <= 0){

            getSession().endSession(this);

        }

    }

    /**
     * This method should handle when a player dies in the game dimension.
     *
     * @param player The player who died.
     */
    @Override
    public void handlePlayerDeath(Player player, Object source) {

        eliminatePlayer(player, Cause.of(EventContext.empty(), source), false);

    }

    /**
     * This method should handle the game start
     */
    @Override
    public void handleGameStart() {

        for(UUID playerID : getSession().getActivePlayers()){

            Utilities.getPlayerByUniqueID(playerID).ifPresent((player) -> {

                player.sendTitle(Title.builder().title(Text.of(getGameTintColor(), "Let's Go!")).subtitle(Text.of(getGameTintColor(), "Good luck and have fun!")).fadeIn(5).stay(20).fadeOut(5).build());
                player.playSound(SoundTypes.BLOCK_NOTE_PLING, player.getPosition(), .25, 2.0);

            });

        }

        for(UUID playerID : getSession().getActivePlayers()){

            Utilities.getPlayerByUniqueID(playerID).ifPresent((player) -> Utilities.setGameMode(player, getMode()));

        }

        for(UUID playerID : getSession().getSpectatingPlayers()){

            Utilities.getPlayerByUniqueID(playerID).ifPresent((player) -> Utilities.setGameMode(player, getSpectatorMode()));

        }

        getSession().setState(GameSessionState.RUNNING);
        this.startTime = new Date();
        getSession().scheduleSessionTask(getGameTimeInSeconds(), this::handleGameEnd, GameNotifications.RUNNING_GAME);

    }

    @Override
    public final GameMode getSpectatorMode() { return getSession().getProperties().getSpectatorMode(); }

    /**
     * This method should handle the game end.
     */
    @Override
    public void handleGameEnd() {

        if(!getSession().getState().equals(GameSessionState.RUNNING)) { return; }

        getSession().setState(GameSessionState.FINISHING);

        Sponge.getEventManager().unregisterListeners(this);

        for(UUID playerID : getSession().getActivePlayers()){

            Utilities.getPlayerByUniqueID(playerID).ifPresent((player) -> Utilities.setGameMode(player, GameModes.ADVENTURE));

        }

        this.rewardPlayers();

        getSession().scheduleSessionTask(10, () -> getSession().endSession(this), GameNotifications.FINISHING_GAME);

    }

    /**
     * This method will get fired from the game session {@link GameSession#notifyTime(int, GameNotifications)}. Anything could be done here.
     *
     * @param timeInSeconds    The time before {@link GameSession#getSessionTask()}'s execution
     * @param notificationType The notification type
     */
    @Override
    public void notifyTime(int timeInSeconds, GameNotifications notificationType){

        if(notificationType.equals(GameNotifications.PRE_GAME) && Arrays.asList(3, 2, 1).contains(timeInSeconds)){

            for(UUID playerID : getSession().getActivePlayers()){

                Utilities.getPlayerByUniqueID(playerID).ifPresent((player) -> {

                    player.sendTitle(Title.builder().title(Text.of(getGameTintColor(), timeInSeconds)).subtitle(Text.of(getGameTintColor(), "Get ready!")).fadeIn(5).stay(40).fadeOut(5).build());
                    player.playSound(SoundTypes.BLOCK_NOTE_PLING, player.getPosition(), .25);

                });

            }

        }

    }

    /**
     * This method should handle a player's elimination
     * @param player The eliminated player
     * @param source The source of the elimination
     * @param hasLeft Whether the player has left the game
     */
    @Override
    public void eliminatePlayer(Player player, Object source, boolean hasLeft) {

        for(Inventory slot : player.getInventory().slots()){

            if(slot.peek().isPresent()){

                Utilities.spawnItem(new Location<>(player.getWorld(), player.getPosition()), slot.poll().get().createSnapshot());

            }

        }

        getSession().removeActivePlayer(player);

        if(!hasLeft){

            player.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
            getSession().getSpectatingPlayers().add(player.getUniqueId());

        }

        // By default, trigger the game end if there is only one player alive left.
        if(getSession().getActiveTeams().size() <= 1){

            handleGameEnd();

        }

    }

    /**
     * This method should handle the game's initialization
     */
    @Override
    public void setupPreGame(){

        getSession().setState(GameSessionState.PRE_GAME);

        this.setPlayersSpawnLocations();
        this.setSpectatorsSpawnLocations();

        for(UUID playerID : getSession().getActivePlayers()){

            Utilities.getPlayerByUniqueID(playerID).ifPresent((player) -> {

                Utilities.resetAllVelocities(player);
                Utilities.setGameMode(player, GameModes.ADVENTURE);

            });

        }

        for(UUID playerID : getSession().getSpectatingPlayers()){

            Utilities.getPlayerByUniqueID(playerID).ifPresent((player) -> {

                Utilities.resetAllVelocities(player);
                Utilities.setGameMode(player, getSpectatorMode());

            });

        }

        Sponge.getEventManager().registerListeners(Polarity.getPolarity(), this);

        getSession().scheduleSessionTask(5, this::handleGameStart, GameNotifications.PRE_GAME);

    }

    /**
     * This method should return true if the game can be used in any way
     *
     * @return Whether the game is considered valid or not
     */
    @Override
    public final boolean isValidGame() { return gameValid && getGameWorld().isPresent(); }

    /**
     * This method should declare the game as unusable / invalid {@link #isValidGame()}
     */
    @Override
    public final void invalidateGame() { gameValid = false; Sponge.getEventManager().unregisterListeners(this); }

    /**
     * This method should handle the game destruction
     */
    @Override
    public final void destroyGame() {

        // Check if the game has not already been destroyed
        if(this.isValidGame()){

            Sponge.getEventManager().unregisterListeners(this);

            Polarity.getLogger().info(PolarityColor.AQUA.getStringColor() + "Destroying game world " + getGameWorld().get().getName());

            this.invalidateGame();

            // If we want to destroy the game world we'll need to get rid of all the players in it, so we'll just warp all the player to the hub.
            for(Player player : getGameWorld().get().getPlayers()){

                Utilities.resetPlayer(player);

                if(!PolarityWarp.warp(player, "Hub", Polarity.getPolarity())){

                    // If it fails for some reason, teleport the players to the default world
                    // Getting a world by the server's default world name should always return a value
                    World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
                    player.setLocation(new Location<>(defaultWorld, defaultWorld.getProperties().getSpawnPosition()));

                }

            }

            // This should basically never happen, but just in case, kick all the players who haven't been teleported.
            if(getGameWorld().get().getPlayers().size() > 0){

                for(Player problematicPlayer : getGameWorld().get().getPlayers()){

                    problematicPlayer.kick(Text.of(TextColors.RED, "Unexpected Error. Please reconnect"));

                }

            }

            // Try to unload and delete the game's world
            try{

                WorldProperties props = getGameWorld().get().getProperties();

                Utilities.removeWorldInfo(getGameWorld().get());
                Sponge.getServer().unloadWorld(getGameWorld().get());
                Sponge.getServer().deleteWorld(props);
                this.gameWorldID = null;

            }
            catch(IllegalStateException e){

                // Print an error in the console on failure
                System.out.println(PolarityColor.RED.getStringColor() + "Failed to remove lobby world " + getGameWorld().get().getName() + ". A manual removal is required.");

            }

        }

    }

}
