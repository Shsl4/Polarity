package dev.sl4sh.polarity.data.containers;

import dev.sl4sh.polarity.games.arena.ArenaPreset;
import dev.sl4sh.polarity.games.GamePreset;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.*;

@ConfigSerializable
public class GamePresetContainer implements PolarityContainer<GamePreset> {

    @Setting(value = "list")
    @Nonnull
    private List<GamePreset> list = new ArrayList<>();

    public Optional<ArenaPreset> getRandomGamePresetForGameID(int id){

        if(id == 1){

            final List<ArenaPreset> arenaPresets = new ArrayList<>(Collections.singletonList(ArenaPreset.getRandomArenaStaticPreset()));

            for(GamePreset preset : getList()){

                if(preset.getClass().equals(ArenaPreset.class)){

                    arenaPresets.add((ArenaPreset) preset);

                }

            }

            return Optional.of(arenaPresets.get(new Random().nextInt(arenaPresets.size())));

        }

        return Optional.empty();

    }

    @Nonnull
    @Override
    public List<GamePreset> getList() {
        return list;
    }

    @Override
    public boolean add(@Nonnull GamePreset object) {
        return list.add(object);
    }

    @Override
    public boolean remove(@Nonnull GamePreset object) {
        return list.remove(object);
    }

    @Override
    public boolean shouldSave() {
        return getList().size() > 0;
    }

}
