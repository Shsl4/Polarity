package dev.sl4sh.polarity.commands.spleef;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.games.GameLobbyBase;
import dev.sl4sh.polarity.games.spleef.SpleefLobby;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class XSpleefMain implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Spleef test."))
                .permission("polarity.spleef")
                .executor(new XSpleefMain())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            Optional<GameLobbyBase<?>> lobby = Polarity.getGameManager().createNewGameInstance(SpleefLobby.class, 4);
            lobby.ifPresent(xSpleefGameXGameLobby -> caller.setLocation(new Location<World>(xSpleefGameXGameLobby.getLobbyWorld(), xSpleefGameXGameLobby.getLobbyWorld().getProperties().getSpawnPosition())));

        }

        return CommandResult.empty();

    }

}
