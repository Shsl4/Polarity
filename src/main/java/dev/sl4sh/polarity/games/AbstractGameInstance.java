package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.enums.games.GameNotifications;
import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityColors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.storage.WorldProperties;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class AbstractGameInstance implements GameInstance {

    private World gameWorld;
    private final GameSession<?> session;
    private boolean gameValid;

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

            worldProps.setPVPEnabled(true);
            worldProps.setGameMode(GameModes.SURVIVAL);
            worldProps.setDifficulty(Difficulties.HARD);
            worldProps.setWorldTime(18000);
            worldProps.setRaining(false);
            worldProps.setGameRule("doWeatherCycle", "false");
            worldProps.setGameRule("doDayLightCycle", "false");

            Optional<World> loadResult = Sponge.getServer().loadWorld(worldProps);

            if(!loadResult.isPresent()) {

                Sponge.getServer().deleteWorld(props.get().get());
                throw new IllegalStateException("Could not load the newly created world.");

            }

            gameWorld = loadResult.get();

            Utilities.createWorldInfoFrom(getGameWorld(), Sponge.getServer().getWorld(gameWorldName).get());
            Utilities.getOrCreateWorldInfo(getGameWorld()).setIsGameWorld(true);
            this.gameValid = true;

        }
        catch(InterruptedException | ExecutionException e){

            this.destroyGame();
            e.printStackTrace();

        }

    }

    /**
     * This method should return the world the game is occurring in
     *
     * @return The game's world
     */
    @Override
    public final World getGameWorld() { return this.gameWorld; }

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
     *
     * @param players The players and their bound team ID
     */
    @Override
    public abstract void setPlayerSpawnLocations(Map<Player, Integer> players);

    /**
     * This method should set the spectators spawn locations when the game starts
     *
     * @param players The game spectators list
     */
    @Override
    public abstract void setSpectatorsSpawnLocations(List<Player> players);
    /**
     * This method should handle when a player joins the game.
     *
     * @param player The player who joined.
     * @param role The player's role
     */
    @Override
    public void handlePlayerJoin(Player player, PlayerSessionRole role) {

        if(player.setLocation(new Location<>(getGameWorld(), getGameWorld().getProperties().getSpawnPosition()))){

            Utilities.setGameMode(player, GameModes.SPECTATOR);

        }

    }

    /**
     * This method should handle when a player leaves the game.
     *
     * @param player The player who joined.
     */
    @Override
    public void handlePlayerLeft(Player player) {

        eliminatePlayer(player, Cause.of(EventContext.empty(), player));

        if(getSession().getActivePlayers().size() <= 0){

            getSession().endSession(this);

        }

    }

    /**
     * This method should handle when a player dies in the game dimension.
     *
     * @param player The player who joined.
     */
    @Override
    public void handlePlayerDeath(Player player) {

        eliminatePlayer(player, Cause.of(EventContext.empty(),this));

    }

    /**
     * This method should handle the game start
     */
    @Override
    public void handleGameStart() {

        getSession().setState(GameSessionState.RUNNING);
        getSession().scheduleTask(getGameTimeInSeconds(), this::handleGameEnd, GameNotifications.RUNNING_GAME);

        for(Player player : getSession().getActivePlayers()){

            Utilities.setGameMode(player, GameModes.SURVIVAL);

        }

    }

    /**
     * This method should handle the game end.
     */
    @Override
    public void handleGameEnd() {

        getSession().setState(GameSessionState.FINISHING);

        Sponge.getEventManager().unregisterListeners(this);

        for(Player player : getSession().getActivePlayers()){

            Utilities.setGameMode(player, GameModes.ADVENTURE);

        }

        getSession().scheduleTask(10, () -> getSession().endSession(this), GameNotifications.FINISHING_GAME);

    }

    /**
     * This method will get fired from the game session {@link GameSession#notifyTime(int, GameNotifications)}. Anything could be done here.
     *
     * @param timeInSeconds    The time before {@link GameSession#getSessionTask()}'s execution
     * @param notificationType The notification type
     */
    @Override
    public abstract void notifyTime(int timeInSeconds, GameNotifications notificationType);

    /**
     * This method should handle a player's elimination
     *
     * @param player The eliminated player
     * @param cause The cause of the elimination. If the cause contains the player, it means they they left the game
     */
    @Override
    public void eliminatePlayer(Player player, Cause cause) {

        player.getInventory().clear();
        Utilities.removePotionEffects(player);

        if(cause.contains(this)){

            player.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
            getSession().removeActivePlayer(player);
            getSession().getSpectatingPlayers().add(player);

        }

        // By default, trigger the game end if there is only one player alive left.
        if(getSession().getActivePlayers().size() <= 1){

            handleGameEnd();

        }

    }

    /**
     * This method should handle the game's initialization
     *
     * @param players The participating players
     */
    @Override
    public void setupPreGame(Map<Player, Integer> players, List<Player> spectators){

        this.setPlayerSpawnLocations(players);
        this.setSpectatorsSpawnLocations(spectators);

        getSession().setState(GameSessionState.PRE_GAME);

        for(Player player : players.keySet()){

            Utilities.setGameMode(player, GameModes.ADVENTURE);

        }

        for(Player player : spectators){

            Utilities.setGameMode(player, GameModes.SPECTATOR);

        }

        Sponge.getEventManager().registerListeners(Polarity.getPolarity(), this);

        getSession().scheduleTask(5, this::handleGameStart, GameNotifications.PRE_GAME);

    }

    /**
     * This method should return true if the game can be used in any way
     *
     * @return Whether the game is considered valid or not
     */
    @Override
    public final boolean isValidGame() { return gameValid && getGameWorld() != null; }

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

            Polarity.getLogger().info(PolarityColors.AQUA.getStringColor() + "Destroying game world " + getGameWorld().getName());

            this.invalidateGame();

            // If we want to destroy the game world we'll need to get rid of all the players in it, so we'll just warp all the player to the hub.
            for(Player player : getGameWorld().getPlayers()){

                if(!PolarityWarp.warp(player, "Hub", Polarity.getPolarity())){

                    // If it fails for some reason, teleport the players to the default world
                    // Getting a world by the server's default world name should always return a value
                    World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
                    player.setLocation(new Location<>(defaultWorld, defaultWorld.getProperties().getSpawnPosition()));

                }

            }

            // This should basically never happen, but just in case, kick all the players who haven't been teleported.
            if(getGameWorld().getPlayers().size() > 0){

                for(Player problematicPlayer : getGameWorld().getPlayers()){

                    problematicPlayer.kick(Text.of(TextColors.RED, "Internal Error. Please reconnect"));

                }

            }

            // Try to unload and delete the game's world
            try{

                Utilities.removeWorldInfo(getGameWorld());
                Sponge.getServer().unloadWorld(getGameWorld());
                Sponge.getServer().deleteWorld(getGameWorld().getProperties());
                this.gameWorld = null;

            }
            catch(IllegalStateException e){

                // Print an error in the console on failure
                System.out.println(PolarityColors.RED.getStringColor() + "Failed to remove lobby world " + getGameWorld().getName() + ". A manual removal is required.");

            }

        }

    }

}
