package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.events.LobbyDestructionEvent;
import dev.sl4sh.polarity.events.PlayerChangeDimensionEvent;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityColors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.storage.WorldProperties;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractGameLobby<T extends AbstractGame> implements GameLobbyBase<T> {

    @Nonnull
    private T game;
    private World lobbyWorld;
    private boolean lobbyValid;
    private Task gameLaunchTask;
    private int maxPlayers;

    @Nonnull
    @Override
    public final T getGame(){ return game; }

    @Override
    public final World getLobbyWorld(){ return lobbyWorld; }

    @Override
    public final boolean isValidLobby() { return lobbyValid && getLobbyWorld() != null; }

    @Nonnull
    @Override
    public final Optional<Task> getGameLaunchTask() { return Optional.ofNullable(gameLaunchTask); }

    @Override
    public final int getMaxPlayers() { return maxPlayers; }

    @Override
    public final void invalidateLobby() {

        lobbyValid = false;
        Sponge.getEventManager().unregisterListeners(this);

    }

    @Nonnull
    @Override
    public String getLobbyName() {
        return "Lobby";
    }

    @Override
    public void onPlayerJoinedLobby(Player player) {

        if(!isValidLobby()) { return; }

        if(registerPlayer(player)){

            Utilities.savePlayerInventory(player);
            player.getInventory().clear();
            player.offer(Keys.POTION_EFFECTS, new ArrayList<>());
            Utilities.restoreMaxHealth(player);

            for(Player regPlayer : getRegisteredPlayers()){

                regPlayer.sendMessage(Text.of(TextColors.AQUA, "[", getGame().getGameName(), "] | ", player.getName(), " joined the lobby (", getRegisteredPlayers().size(), "/", getMaxPlayers(), ")"));

            }

            if(getRegisteredPlayers().size() >= getMinPlayersToStart()){

                if(!getGameLaunchTask().isPresent()) {

                    gameLaunchTask = Task.builder().delay(30, TimeUnit.SECONDS).execute(this::launchGame).submit(Polarity.getPolarity());
                    Task.builder().delay(15, TimeUnit.SECONDS).execute(() -> this.notifyTimeBeforeLaunch(15)).submit(Polarity.getPolarity());
                    Task.builder().delay(25, TimeUnit.SECONDS).execute(() -> this.notifyTimeBeforeLaunch(5)).submit(Polarity.getPolarity());
                    this.notifyTimeBeforeLaunch(30.0);

                }

            }

        }

    }

    @Override
    public void onPlayerLeftLobby(Player player) {

        if(!isValidLobby()) { return; }

        if(unRegisterPlayer(player)){

            player.getInventory().clear();
            Utilities.restorePlayerInventory(player);
            Utilities.restoreMaxHealth(player);

            for(Player regPlayer : getRegisteredPlayers()){

                regPlayer.sendMessage(Text.of(TextColors.RED, "[", getGame().getGameName(), "] | ", player.getName(), " left the lobby (", getRegisteredPlayers().size(), "/", getMaxPlayers(), ")"));

            }

            if(getRegisteredPlayers().size() < getMinPlayersToStart()){

                if(getGameLaunchTask().isPresent()) {

                    getGameLaunchTask().get().cancel();
                    gameLaunchTask = null;

                }

            }

            if(getRegisteredPlayers().size() <= 0){

                getGame().destroyGame();
                this.destroyLobby();

            }

        }

    }

    @Override
    public final void launchGame() {

        Sponge.getEventManager().unregisterListeners(this);
        getGame().setupPreGame(getRegisteredPlayers());
        this.destroyLobby();

    }

    @Override
    public void notifyTimeBeforeLaunch(double timeInSeconds) {

        if(!isValidLobby()) { return; }

        for(Player player : getRegisteredPlayers()){

            player.sendTitle(Title.builder().title(Text.of(TextColors.AQUA, getGame().getGameName())).subtitle(Text.of(TextColors.AQUA, "Game starts in ", (int)timeInSeconds, " seconds!")).fadeIn(2).stay(30).fadeOut(2).build());
            player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 0.25);

        }

    }

    @Override
    public void destroyLobby() {

        if(isValidLobby()){

            Polarity.getLogger().info(PolarityColors.AQUA.getStringColor() + "Destroying game lobby " + getLobbyWorld().getName());

            Sponge.getEventManager().post(new LobbyDestructionEvent(this, this));

            invalidateLobby();

            for(Player player : getLobbyWorld().getPlayers()){

                if(!PolarityWarp.warp(player, "Hub", Polarity.getPolarity())){

                    World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
                    player.setLocation(new Location<>(defaultWorld, defaultWorld.getProperties().getSpawnPosition()));

                }

                Utilities.restorePlayerInventory(player);
                Utilities.restoreMaxHealth(player);

            }

            if(getLobbyWorld().getPlayers().size() > 0){

                for(Player problematicPlayer : getLobbyWorld().getPlayers()){

                    problematicPlayer.kick(Text.of(TextColors.RED, "Internal Error. Please reconnect"));

                }

            }

            try{

                Utilities.removeWorldInfo(getLobbyWorld());
                Sponge.getServer().unloadWorld(getLobbyWorld());
                Sponge.getServer().deleteWorld(getLobbyWorld().getProperties());
                this.lobbyWorld = null;

            }
            catch(IllegalStateException e){

                System.out.println(PolarityColors.RED.getStringColor() + "[Polarity] | Failed to remove lobby world " + getLobbyWorld().getName() + ". A manual removal is required.");

            }

        }

    }

    /**
     * This method should return a new instance of a game
     * @return The game instance
     * @throws IllegalStateException If the game failed to create {@link AbstractGame#AbstractGame(String)}
     */
    @Nonnull
    abstract protected T createGame() throws IllegalStateException;

    /**
     * The game lobby constructor
     * @param lobbyWorldModel The name of the world that will be copied and used as the lobby's world
     * @param maxPlayers The maximal amount of players for the game
     * @throws IllegalStateException If a creation error occurs. Check the source for more details
     */
    protected AbstractGameLobby(String lobbyWorldModel, int maxPlayers) throws IllegalStateException {

        try{

            if(!Sponge.getServer().getWorld(lobbyWorldModel).isPresent()) { throw new IllegalStateException("The specified world model name does not exist."); }

            if(maxPlayers <= 0) { throw new IllegalStateException("Tried to create a lobby with a negative or null player capacity"); }

            if(getMinPlayersToStart() < getMaxPlayers()) { throw new IllegalStateException("Tried to create a lobby with a minimal player amount to start greater than the maximal player amount"); }

            CompletableFuture<Optional<WorldProperties>> props = Sponge.getGame().getServer().copyWorld(Sponge.getServer().getWorld(lobbyWorldModel).get().getProperties(), lobbyWorldModel + "-" + (new Date().toString()).replace(":", "-"));

            if(!props.get().isPresent()) { throw new IllegalStateException("The properties failed to create."); }

            WorldProperties worldProps = props.get().get();

            worldProps.setDifficulty(Difficulties.PEACEFUL);
            worldProps.setGameMode(GameModes.ADVENTURE);
            worldProps.setPVPEnabled(false);
            worldProps.setWorldTime(18000);
            worldProps.setRaining(false);
            worldProps.setGameRule("doWeatherCycle", "false");
            worldProps.setGameRule("doDayLightCycle", "false");

            Optional<World> loadResult = Sponge.getServer().loadWorld(worldProps);

            if(!loadResult.isPresent()) {

                Sponge.getServer().deleteWorld(props.get().get());
                throw new IllegalStateException("Could not load the newly created world.");

            }

            lobbyWorld = loadResult.get();

            this.maxPlayers = maxPlayers;

            game = createGame();

            Sponge.getEventManager().registerListeners(Polarity.getPolarity(), this);

            Utilities.getOrCreateWorldInfo(getLobbyWorld()).setDimensionProtected(true);
            Utilities.getOrCreateWorldInfo(getLobbyWorld()).setGameWorld(true);

            lobbyValid = true;

        }
        catch(InterruptedException | ExecutionException e){

            e.printStackTrace();
            this.destroyLobby();

        }

    }

    @Listener
    public final void onDimensionChange_Pre(PlayerChangeDimensionEvent.Pre event){

        if(isValidLobby()){

            if(event.getToWorld().getUniqueId().equals(getLobbyWorld().getUniqueId())){

                if(getRegisteredPlayers().size() >= getMaxPlayers()){

                    event.setCancelReason(Text.of(TextColors.RED, "[", getGame().getGameName(), "] | This lobby is full."));
                    event.setCancelled(true);

                }

            }

        }

    }

    @Listener
    public final void onDimensionChange_Post(PlayerChangeDimensionEvent.Post event){

        if(isValidLobby()){

            if(event.getToWorld().getUniqueId().equals(getLobbyWorld().getUniqueId())){

                onPlayerJoinedLobby(event.getTargetEntity());

            }
            else if(event.getFromWorld().getUniqueId().equals(getLobbyWorld().getUniqueId())){

                onPlayerLeftLobby(event.getTargetEntity());

            }

        }

    }

    @Listener
    public void onPlayerMove(MoveEntityEvent event){

        if(!isValidLobby()) { return; }

        if(event.getTargetEntity() instanceof Player){

            if(event.getToTransform().getExtent().getUniqueId().equals(getLobbyWorld().getUniqueId())){

                if(event.getToTransform().getLocation().getY() <= 5.0){

                    event.getTargetEntity().setLocation(new Location<World>(getLobbyWorld(), getLobbyWorld().getProperties().getSpawnPosition()));

                }

            }

        }

    }

    @Listener
    public void onDisconnect(@Nonnull ClientConnectionEvent.Disconnect event){

        if(!isValidLobby()) { return; }

        if(event.getTargetEntity().getWorld().getUniqueId().equals(getLobbyWorld().getUniqueId())){

            onPlayerLeftLobby(event.getTargetEntity());

        }

    }

}
