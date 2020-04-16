package dev.sl4sh.polarity.games;

import com.flowpowered.math.vector.Vector3d;
import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.enums.games.GameState;
import dev.sl4sh.polarity.events.GameDestructionEvent;
import dev.sl4sh.polarity.events.PlayerChangeDimensionEvent;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityColors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
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
import java.util.concurrent.TimeUnit;

public abstract class AbstractGame implements GameBase {

    private World gameWorld;
    private GameState gameState = GameState.IDLE;
    boolean gameValid;
    Task preGameTimerTask;
    Task gameTimerTask;
    @Nonnull
    List<Player> activePlayers = new ArrayList<>();
    @Nonnull
    List<Player> spectatingPlayers = new ArrayList<>();

    @Override
    public final World getGameWorld(){ return gameWorld; }

    @Override
    public final GameState getState() {
        return gameState;
    }

    @Override
    public boolean isValidGame() { return gameValid && getGameWorld() != null; }

    @Override
    public final Optional<Task> getGameTimerTask() { return Optional.ofNullable(gameTimerTask); }

    @Nonnull
    @Override
    public List<Player> getActivePlayers() {
        return activePlayers;
    }

    @Nonnull
    @Override
    public List<Player> getSpectatingPlayers() {
        return spectatingPlayers;
    }

    @Override
    public void invalidateGame() {

        gameValid = false;
        Sponge.getEventManager().unregisterListeners(this);

    }

    @Override
    public void eliminatePlayer(Player player) {

        activePlayers.remove(player);

    }

    @Override
    public void onPlayerJoinedGame(Player player) {

        if(!this.isValidGame()) { return; }

        if(!getActivePlayers().contains(player)){

            player.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
            spectatingPlayers.add(player);

        }

    }

    @Override
    public void onPlayerLeftGame(Player player) {

        if(!this.isValidGame()) { return; }

        if(getActivePlayers().contains(player)){

            eliminatePlayer(player);

        }

        getSpectatingPlayers().remove(player);
        player.getInventory().clear();

        Utilities.restorePlayerInventory(player);
        Utilities.restoreMaxHealth(player);

        if(getActivePlayers().size() <= 0){

            if(getGameTimerTask().isPresent()){

                getGameTimerTask().get().cancel();

            }

            this.destroyGame();

        }

    }

    @Override
    public void setupPreGame(List<Player> players) {

        Sponge.getEventManager().registerListeners(Polarity.getPolarity(), this);

        this.gameState = GameState.PRE_GAME;

        getActivePlayers().addAll(players);

        for(Player player : getActivePlayers()){

            Vector3d loc = this.getSpawnLocationForPlayer(player);
            player.setLocation(new Location<>(gameWorld, loc));

        }

        preGameTimerTask = Task.builder().delay(5, TimeUnit.SECONDS).execute(this::startGame).submit(Polarity.getPolarity());
        Task.builder().delay(4, TimeUnit.SECONDS).execute(() -> this.notifyTimeBeforeStart(1)).submit(Polarity.getPolarity());
        Task.builder().delay(3, TimeUnit.SECONDS).execute(() -> this.notifyTimeBeforeStart(2)).submit(Polarity.getPolarity());
        Task.builder().delay(2, TimeUnit.SECONDS).execute(() -> this.notifyTimeBeforeStart(3)).submit(Polarity.getPolarity());
        Task.builder().delay(1, TimeUnit.SECONDS).execute(() -> this.notifyTimeBeforeStart(4)).submit(Polarity.getPolarity());
        this.notifyTimeBeforeStart(5.0);

    }

    @Override
    public void startGame() {

        this.gameState = GameState.RUNNING;
        gameTimerTask = Task.builder().delay(getGameTimeInSeconds(), TimeUnit.SECONDS).execute(this::onGameEnd).submit(Polarity.getPolarity());

    }

    @Override
    public void onGameEnd() {

        this.gameState = GameState.FINISHING;

        Task.builder().delay(10, TimeUnit.SECONDS).execute(() -> {

            this.gameState = GameState.OVER;
            this.destroyGame();

        }).submit(Polarity.getPolarity());

    }

    @Override
    public final void destroyGame(){

        if(this.isValidGame()){

            Polarity.getLogger().info(PolarityColors.AQUA.getStringColor() + "Destroying game world " + getGameWorld().getName());

            Sponge.getEventManager().post(new GameDestructionEvent(this, this));

            this.invalidateGame();

            for(Player player : getGameWorld().getPlayers()){

                player.getInventory().clear();

                if(!PolarityWarp.warp(player, "Hub", Polarity.getPolarity())){

                    // Getting a world by the server's default world name should always return a value
                    World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
                    player.setLocation(new Location<>(defaultWorld, defaultWorld.getProperties().getSpawnPosition()));

                }

                Utilities.restorePlayerInventory(player);
                Utilities.restoreMaxHealth(player);

            }

            if(getGameWorld().getPlayers().size() > 0){

                for(Player problematicPlayer : getGameWorld().getPlayers()){

                    problematicPlayer.kick(Text.of(TextColors.RED, "Internal Error. Please reconnect"));

                }

            }

            try{

                Utilities.removeWorldInfo(getGameWorld());
                Sponge.getServer().unloadWorld(getGameWorld());
                Sponge.getServer().deleteWorld(getGameWorld().getProperties());
                this.gameWorld = null;

            }
            catch(IllegalStateException e){

                System.out.println(PolarityColors.RED.getStringColor() + "[Polarity] | Failed to remove lobby world " + getGameWorld().getName() + ". A manual removal is required.");

            }

        }

    }

    protected AbstractGame(String gameWorldModel) throws IllegalStateException{

        try{

            if(!Sponge.getServer().getWorld(gameWorldModel).isPresent()) { throw new IllegalStateException("The specified world model name does not exist."); }

            CompletableFuture<Optional<WorldProperties>> props = Sponge.getGame().getServer().copyWorld(Sponge.getServer().getWorld(gameWorldModel).get().getProperties(), gameWorldModel + "-" + (new Date().toString()).replace(":", "-"));

            if(!props.get().isPresent()) { throw new IllegalStateException("The properties failed to create."); }

            WorldProperties worldProps = props.get().get();

            worldProps.setPVPEnabled(false);
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

            this.gameValid = true;

            Utilities.getOrCreateWorldInfo(gameWorld).setGameWorld(true);

        }
        catch(InterruptedException | ExecutionException e){

            this.destroyGame();
            e.printStackTrace();

        }

    }

    @Listener
    public final void onDimensionChange(PlayerChangeDimensionEvent.Post event){

        if(!this.isValidGame()) { return; }

        if(event.getToWorld().getUniqueId().equals(getGameWorld().getUniqueId())){

            onPlayerJoinedGame(event.getTargetEntity());

        }
        else if(event.getFromWorld().getUniqueId().equals(getGameWorld().getUniqueId())){

            onPlayerLeftGame(event.getTargetEntity());

        }

    }

    @Listener
    public void onDisconnect(@Nonnull ClientConnectionEvent.Disconnect event){

        if(!this.isValidGame()) { return; }

        if(getState().equals(GameState.PRE_GAME) || getState().equals(GameState.RUNNING)) {

            if(event.getTargetEntity().getWorld().getUniqueId().equals(getGameWorld().getUniqueId())){

                onPlayerLeftGame(event.getTargetEntity());

            }

        }

    }

    @Listener
    public void onPlayerDeath(DamageEntityEvent event){

        if(!this.isValidGame()) { return; }

        if(event.getTargetEntity() instanceof Player){

            if(event.getTargetEntity().getWorld().getUniqueId().equals(getGameWorld().getUniqueId())){

                Player player = (Player)event.getTargetEntity();

                if(getActivePlayers().contains(player) && player.health().get() <= event.getFinalDamage()){

                    event.setCancelled(true);
                    player.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
                    spectatingPlayers.add(player);
                    eliminatePlayer(player);

                }

            }

        }

    }

}
