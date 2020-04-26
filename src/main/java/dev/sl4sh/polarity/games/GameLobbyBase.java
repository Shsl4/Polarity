package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.enums.PolarityColors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameLobbyBase implements GameLobby {

    private World lobbyWorld;
    private boolean lobbyValid;

    @Override
    public final World getLobbyWorld(){ return lobbyWorld; }

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

            Polarity.getLogger().info(PolarityColors.AQUA.getStringColor() + "Destroying game lobby " + getLobbyWorld().getName());

            invalidateLobby();

            for(Player player : getLobbyWorld().getPlayers()){

                if(!PolarityWarp.warp(player, "Hub", Polarity.getPolarity())){

                    World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
                    player.setLocation(new Location<>(defaultWorld, defaultWorld.getProperties().getSpawnPosition()));

                }

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

                System.out.println(PolarityColors.RED.getStringColor() + "Failed to remove lobby world " + getLobbyWorld().getName() + ". A manual removal is required.");

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
            worldProps.setGameRule("doDayLightCycle", "false");

            Optional<World> loadResult = Sponge.getServer().loadWorld(worldProps);

            if(!loadResult.isPresent()) {

                Sponge.getServer().deleteWorld(props.get().get());
                throw new IllegalStateException("Could not load the newly created world.");

            }

            lobbyWorld = loadResult.get();

            Sponge.getEventManager().registerListeners(Polarity.getPolarity(), this);

            Utilities.createWorldInfoFrom(getLobbyWorld(), Sponge.getServer().getWorld(lobbyWorldName).get());
            Utilities.getOrCreateWorldInfo(getLobbyWorld()).setDimensionProtected(true);
            Utilities.getOrCreateWorldInfo(getLobbyWorld()).setIsGameWorld(true);

            lobbyValid = true;

        }
        catch(InterruptedException | ExecutionException e){

            e.printStackTrace();
            this.destroyLobby();

        }

    }

}
