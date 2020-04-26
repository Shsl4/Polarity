package dev.sl4sh.polarity.data.registration.UIStack;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nonnull;
import java.util.Optional;

public class UIStackData extends AbstractData<UIStackData, ImmutableUIStackData> {

    private StackTypes stackType;
    private Integer stackDataID;
    private Integer buttonID;

    public StackTypes getStackType() {
        return stackType;
    }

    public Integer getStackDataID() { return stackDataID; }

    public Integer getButtonID() {
        return buttonID;
    }

    public void setStackDataID(Integer stackDataID) {
        this.stackDataID = stackDataID;
    }

    public void setStackType(StackTypes stackType) {
        this.stackType = stackType;
    }

    public void setButtonID(Integer buttonID) {
        this.buttonID = buttonID;
    }

    public UIStackData(){

        this(StackTypes.PLACEHOLDER, -1, -1);

    }

    public UIStackData(StackTypes stackType, Integer stackDataID, Integer buttonID) {
        this.stackType = stackType;
        this.stackDataID = stackDataID;
        this.buttonID = buttonID;
        registerGettersAndSetters();
    }

    public Value<StackTypes> stackType(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.UIStack.TYPE, stackType, StackTypes.PLACEHOLDER);
    }

    public Value<Integer> stackDataID(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.UIStack.DATA_ID, stackDataID, -1);
    }

    public Value<Integer> buttonID(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.UIStack.BUTTON_ID, buttonID, -1);
    }

    @Override
    protected void registerGettersAndSetters() {

        registerFieldGetter(Polarity.Keys.UIStack.TYPE, this::getStackType);
        registerFieldGetter(Polarity.Keys.UIStack.DATA_ID, this::getStackDataID);
        registerFieldGetter(Polarity.Keys.UIStack.BUTTON_ID, this::getButtonID);

        registerFieldSetter(Polarity.Keys.UIStack.TYPE, this::setStackType);
        registerFieldSetter(Polarity.Keys.UIStack.DATA_ID, this::setStackDataID);
        registerFieldSetter(Polarity.Keys.UIStack.BUTTON_ID, this::setButtonID);

        registerKeyValue(Polarity.Keys.UIStack.TYPE, this::stackType);
        registerKeyValue(Polarity.Keys.UIStack.DATA_ID, this::stackDataID);
        registerKeyValue(Polarity.Keys.UIStack.BUTTON_ID, this::buttonID);

    }

    @Nonnull
    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(Polarity.Keys.UIStack.TYPE, this.stackType)
                .set(Polarity.Keys.UIStack.DATA_ID, this.stackDataID)
                .set(Polarity.Keys.UIStack.BUTTON_ID, this.buttonID);
    }

    @Override
    public Optional<UIStackData> fill(DataHolder dataHolder, MergeFunction overlap) {

        UIStackData merged = overlap.merge(this, dataHolder.get(UIStackData.class).orElse(null));

        this.stackDataID = merged.stackDataID().get();
        this.stackType = merged.stackType().get();
        this.buttonID = merged.buttonID().get();

        return Optional.of(this);

    }

    @Override
    public Optional<UIStackData> from(DataContainer container) {

        if(!container.contains(Polarity.Keys.UIStack.DATA_ID) ||
                !container.contains(Polarity.Keys.UIStack.TYPE) ||
                        !container.contains(Polarity.Keys.UIStack.BUTTON_ID)) { return Optional.empty(); }

        this.stackDataID = container.getInt(Polarity.Keys.UIStack.DATA_ID.getQuery()).get();
        this.stackType = (StackTypes)container.get(Polarity.Keys.UIStack.TYPE.getQuery()).get();
        this.buttonID = container.getInt(Polarity.Keys.UIStack.BUTTON_ID.getQuery()).get();

        return Optional.of(this);

    }

    @Override
    public UIStackData copy() {

        return new UIStackData(this.stackType, this.stackDataID, this.buttonID);

    }

    @Override
    public ImmutableUIStackData asImmutable() {
        return new ImmutableUIStackData(this.stackType, this.stackDataID, this.buttonID);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

}
