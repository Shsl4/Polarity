package dev.sl4sh.polarity.games.rush;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.games.GameNotifications;
import dev.sl4sh.polarity.enums.games.PlayerSessionRole;
import dev.sl4sh.polarity.games.AbstractGameInstance;
import dev.sl4sh.polarity.games.GameSession;
import dev.sl4sh.polarity.games.PositionSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RushGameInstance extends AbstractGameInstance {

    Map<Player, Integer> teamPlayerMap = new HashMap<>();

    Task brickSpawnTask;
    Task ironSpawnTask;
    Task goldSpawnTask;
    Task emeraldSpawnTask;

    private Integer getPlayerTeamID(Player player){

        if(teamPlayerMap.get(player) != null){

            return teamPlayerMap.get(player);

        }

        return -1;

    }

    /**
     * The game system works by copying an existing world template (which should have been prepared on the server before utilization),
     * then use this whole new separate world to handle everything as we want. Once the game is over, the game world gets destroyed {@link #destroyGame()}} and
     * the game instance is marked invalid {@link #invalidateGame()}.
     *
     * @param gameWorldName The world template name
     * @param session       The game's assigned session.
     * @throws IllegalStateException If a creation error occurs. Use {@link Exception#getMessage()} to figure out what.
     */
    protected RushGameInstance(String gameWorldName, @Nonnull GameSession<?> session) throws IllegalStateException {
        super(gameWorldName, session);
    }

    /**
     * This method should return the game's name. Used for displaying.
     *
     * @return The game's name
     */
    @Nonnull
    @Override
    public String getGameName() {
        return "Rush";
    }

    /**
     * This method should return the unique game type identifier.
     *
     * @return The game type ID
     */
    @Override
    public int getGameID() {
        return 2;
    }

    /**
     * This method should return how many time the game lasts (in seconds)
     *
     * @return The game time in seconds
     */
    @Override
    public int getGameTimeInSeconds() {
        return 0;
    }

    /**
     * This method should set the players spawn locations when the game starts
     *
     * @param players The players and their bound team ID
     */
    @Override
    public void setPlayerSpawnLocations(Map<Player, Integer> players) {

        for(Player player : players.keySet()){

            Integer teamID = players.get(player);

            if(teamID == 0){

                try{

                    List<PositionSnapshot> locations = new ArrayList<>(Utilities.getPositionSnapshotsByTag(getGameWorld(), PositionSnapshot.Tags.TEAM1_SPAWN));
                    PositionSnapshot snap = locations.get(0);
                    player.setLocationAndRotation(new Location<>(getGameWorld(), snap.getLocation()), snap.getRotation());

                }
                catch(IndexOutOfBoundsException e){

                    player.setLocation(new Location<>(getGameWorld(), getGameWorld().getProperties().getSpawnPosition()));

                }

            }
            else if (teamID == 1){

                try{

                    List<PositionSnapshot> locations = new ArrayList<>(Utilities.getPositionSnapshotsByTag(getGameWorld(), PositionSnapshot.Tags.TEAM1_SPAWN));
                    PositionSnapshot snap = locations.get(0);
                    player.setLocationAndRotation(new Location<>(getGameWorld(), snap.getLocation()), snap.getRotation());

                }
                catch(IndexOutOfBoundsException e){

                    player.setLocation(new Location<>(getGameWorld(), getGameWorld().getProperties().getSpawnPosition()));

                }

            }
            else if (teamID == 2){

                try{

                    List<PositionSnapshot> locations = new ArrayList<>(Utilities.getPositionSnapshotsByTag(getGameWorld(), PositionSnapshot.Tags.TEAM1_SPAWN));
                    PositionSnapshot snap = locations.get(0);
                    player.setLocationAndRotation(new Location<>(getGameWorld(), snap.getLocation()), snap.getRotation());

                }
                catch(IndexOutOfBoundsException e){

                    player.setLocation(new Location<>(getGameWorld(), getGameWorld().getProperties().getSpawnPosition()));

                }

            }
            else if (teamID == 3){

                try{

                    List<PositionSnapshot> locations = new ArrayList<>(Utilities.getPositionSnapshotsByTag(getGameWorld(), PositionSnapshot.Tags.TEAM1_SPAWN));
                    PositionSnapshot snap = locations.get(0);
                    player.setLocationAndRotation(new Location<>(getGameWorld(), snap.getLocation()), snap.getRotation());

                }
                catch(IndexOutOfBoundsException e){

                    player.setLocation(new Location<>(getGameWorld(), getGameWorld().getProperties().getSpawnPosition()));

                }

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

    /**
     * This method should handle when a player joins the game.
     *
     * @param player The player who joined.
     * @param role   The player's role
     */
    @Override
    public void handlePlayerJoin(Player player, PlayerSessionRole role) {
        super.handlePlayerJoin(player, role);
    }

    /**
     * This method should handle when a player leaves the game.
     *
     * @param player The player who joined.
     */
    @Override
    public void handlePlayerLeft(Player player) {
        super.handlePlayerLeft(player);
    }

    /**
     * This method should handle when a player dies in the game dimension.
     *
     * @param player The player who joined.
     */
    @Override
    public void handlePlayerDeath(Player player) {
        super.handlePlayerDeath(player);
    }

    /**
     * This method should handle the game start
     */
    @Override
    public void handleGameStart() {

        super.handleGameStart();

        brickSpawnTask = Task.builder().delay(1, TimeUnit.SECONDS).interval(1, TimeUnit.SECONDS).execute(this::generateBricks).submit(Polarity.getPolarity());
        ironSpawnTask = Task.builder().delay(15, TimeUnit.SECONDS).interval(15, TimeUnit.SECONDS).execute(this::generateIron).submit(Polarity.getPolarity());
        goldSpawnTask= Task.builder().delay(60, TimeUnit.SECONDS).interval(60, TimeUnit.SECONDS).execute(this::generateGold).submit(Polarity.getPolarity());
        emeraldSpawnTask = Task.builder().delay(120, TimeUnit.SECONDS).interval(120, TimeUnit.SECONDS).execute(this::generateEmerald).submit(Polarity.getPolarity());

    }

    private void generateBricks(){

        List<PositionSnapshot> snaps = Utilities.getPositionSnapshotsByTag(getGameWorld(), PositionSnapshot.Tags.BRICK_SPAWN);

        for(PositionSnapshot snap : snaps){

            Utilities.spawnItem(new Location<>(getGameWorld(), snap.getLocation()), ItemStack.builder().itemType(ItemTypes.BRICK).build().createSnapshot());

        }

    }

    private void generateIron(){

        List<PositionSnapshot> snaps = Utilities.getPositionSnapshotsByTag(getGameWorld(), PositionSnapshot.Tags.IRON_SPAWN);

        for(PositionSnapshot snap : snaps){

            Utilities.spawnItem(new Location<>(getGameWorld(), snap.getLocation()), ItemStack.builder().itemType(ItemTypes.IRON_INGOT).build().createSnapshot());

        }

    }

    private void generateGold(){

        List<PositionSnapshot> snaps = Utilities.getPositionSnapshotsByTag(getGameWorld(), PositionSnapshot.Tags.GOLD_SPAWN);

        for(PositionSnapshot snap : snaps){

            Utilities.spawnItem(new Location<>(getGameWorld(), snap.getLocation()), ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).build().createSnapshot());

        }

    }

    private void generateEmerald(){

        List<PositionSnapshot> snaps = Utilities.getPositionSnapshotsByTag(getGameWorld(), PositionSnapshot.Tags.EMERALD_SPAWN);

        for(PositionSnapshot snap : snaps){

            Utilities.spawnItem(new Location<>(getGameWorld(), snap.getLocation()), ItemStack.builder().itemType(ItemTypes.EMERALD).build().createSnapshot());

        }

    }

    /**
     * This method should handle the game end.
     */
    @Override
    public void handleGameEnd() {
        super.handleGameEnd();
    }

    /**
     * This method will get fired from the game session {@link GameSession#notifyTime(int, GameNotifications)}. Anything could be done here.
     *
     * @param timeInSeconds    The time before {@link GameSession#getSessionTask()}'s execution
     * @param notificationType The notification type
     */
    @Override
    public void notifyTime(int timeInSeconds, GameNotifications notificationType) {

    }

    /**
     * This method should handle a player's elimination
     *
     * @param player The eliminated player
     * @param cause  The cause of the elimination. If the cause contains the player, it means they they left the game
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

        Map<UUID, RespawnLocation> locations = player.get(Keys.RESPAWN_LOCATIONS).get();

        if(locations.get(getGameWorld().getUniqueId()) != null){

            RespawnLocation loc = locations.get(getGameWorld().getUniqueId());

            Polarity.getLogger().info(String.valueOf(loc.isForced()));

        }
        else{

            Polarity.getLogger().info("Absent");

        }

        // By default, trigger the game end if there is only one player alive left.
        if(getSession().getActivePlayers().size() <= 1){

            handleGameEnd();

        }


    }

    /**
     * This method should handle the game's initialization
     *
     * @param players    The participating players
     * @param spectators
     */
    @Override
    public void setupPreGame(Map<Player, Integer> players, List<Player> spectators) {

        super.setupPreGame(players, spectators);

        spawnTraders();




    }

    private void spawnTraders(){



    }

}
