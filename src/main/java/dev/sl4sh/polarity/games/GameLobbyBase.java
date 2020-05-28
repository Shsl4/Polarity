package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.enums.PolarityColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameLobbyBase implements GameLobby {

    private UUID lobbyWorldID;
    private boolean lobbyValid;

    @Override
    public final Optional<World> getLobbyWorld(){ return Sponge.getServer().getWorld(lobbyWorldID); }

    @Override
    public final boolean isValidLobby() { return lobbyValid && getLobbyWorld() != null; }

    @Override
    public final void invalidateLobby() {

        lobbyValid = false;
        Sponge.getEventManager().unregisterListeners(this);

    }

    @Override
    public void destroyLobby() {

        if(isValidLobby()){

            Polarity.getLogger().info(PolarityColor.AQUA.getStringColor() + "Destroying game lobby " + getLobbyWorld().get().getName());

            invalidateLobby();

            for(Player player : getLobbyWorld().get().getPlayers()){

                Utilities.resetPlayer(player);

                if(!PolarityWarp.warp(player, "Hub", Polarity.getPolarity())){

                    World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
                    player.setLocation(new Location<>(defaultWorld, defaultWorld.getProperties().getSpawnPosition()));

                }

                Utilities.restoreMaxHealth(player);

            }

            if(getLobbyWorld().get().getPlayers().size() > 0){

                for(Player problematicPlayer : getLobbyWorld().get().getPlayers()){

                    problematicPlayer.kick(Text.of(TextColors.RED, "Unexpected Error. Please reconnect"));

                }

            }

            try{

                WorldProperties props = getLobbyWorld().get().getProperties();
                Utilities.removeWorldInfo(getLobbyWorld().get());
                Sponge.getServer().unloadWorld(getLobbyWorld().get());
                Sponge.getServer().deleteWorld(props);
                this.lobbyWorldID = null;

            }
            catch(IllegalStateException e){

                System.out.println(PolarityColor.RED.getStringColor() + "Failed to remove lobby world " + getLobbyWorld().get().getName() + ". A manual removal is required.");

            }

        }

    }

    /**
     * The game lobby constructor
     * @param lobbyWorldName The name of the world that will be copied and used as the lobby's world
     * @throws IllegalStateException If a creation error occurs. Check the source for more details
     */
    protected GameLobbyBase(String lobbyWorldName) throws IllegalStateException {

        try{

            Sponge.getServer().loadWorld(lobbyWorldName);
            if(!Sponge.getServer().getWorld(lobbyWorldName).isPresent()) { throw new IllegalStateException("The specified lobby world name does not exist: " + lobbyWorldName); }

            CompletableFuture<Optional<WorldProperties>> props = Sponge.getGame().getServer().copyWorld(Sponge.getServer().getWorld(lobbyWorldName).get().getProperties(), lobbyWorldName + "-" + (new Date().toString()).replace(":", "-"));

            if(!props.get().isPresent()) { throw new IllegalStateException("The properties failed to create."); }

            WorldProperties worldProps = props.get().get();

            worldProps.setDifficulty(Difficulties.PEACEFUL);
            worldProps.setGameMode(GameModes.ADVENTURE);
            worldProps.setPVPEnabled(false);
            worldProps.setWorldTime(18000);
            worldProps.setRaining(false);
            worldProps.setGameRule("doWeatherCycle", "false");
            worldProps.setGameRule("doDaylightCycle", "false");
            worldProps.setGameRule("doFireTick", "false");
            worldProps.setGameRule("announceAdvancements", "false");
            worldProps.setGameRule("commandBlocksEnabled", "true");
            worldProps.setGameRule("commandBlockOutput", "false");
            worldProps.setGameRule("keepInventory", "false");

            Optional<World> loadResult = Sponge.getServer().loadWorld(worldProps);

            if(!loadResult.isPresent()) {

                Sponge.getServer().deleteWorld(props.get().get());
                throw new IllegalStateException("Could not load the newly created world.");

            }

            lobbyWorldID = loadResult.get().getUniqueId();

            Sponge.getEventManager().registerListeners(Polarity.getPolarity(), this);

            Utilities.createWorldInfoFrom(loadResult.get(), Sponge.getServer().getWorld(lobbyWorldName).get());
            Utilities.getOrCreateWorldInfo(loadResult.get()).setDimensionProtected(true);
            Utilities.getOrCreateWorldInfo(loadResult.get()).setIsGameWorld(true);

            lobbyValid = true;

        }
        catch(InterruptedException | ExecutionException e){

            e.printStackTrace();
            this.destroyLobby();

        }

    }

}
