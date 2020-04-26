package dev.sl4sh.polarity.data.registration.UIStack;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.enums.UI.StackTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableUIStackData extends AbstractImmutableData<ImmutableUIStackData, UIStackData> {

    private final StackTypes stackType;
    private final Integer stackDataID;
    private final Integer buttonID;

    public StackTypes getStackType() {
        return stackType;
    }

    public Integer getStackDataID() { return stackDataID; }

    public Integer getButtonID() { return buttonID; }

    public ImmutableUIStackData(StackTypes stackType, Integer stackDataID, Integer buttonID) {
        this.stackType = stackType;
        this.stackDataID = stackDataID;
        this.buttonID = buttonID;
        registerGetters();
    }

    public ImmutableValue<StackTypes> stackType(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.UIStack.TYPE, stackType, StackTypes.PLACEHOLDER).asImmutable();
    }

    public ImmutableValue<Integer> stackDataID(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.UIStack.DATA_ID, stackDataID, -1).asImmutable();
    }

    public ImmutableValue<Integer> buttonID(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.UIStack.BUTTON_ID, buttonID, -1).asImmutable();
    }

    @Override
    protected void registerGetters() {

        registerFieldGetter(Polarity.Keys.UIStack.TYPE, this::getStackType);
        registerFieldGetter(Polarity.Keys.UIStack.DATA_ID, this::getStackDataID);
        registerFieldGetter(Polarity.Keys.UIStack.BUTTON_ID, this::getButtonID);

        registerKeyValue(Polarity.Keys.UIStack.TYPE, this::stackType);
        registerKeyValue(Polarity.Keys.UIStack.DATA_ID, this::stackDataID);
        registerKeyValue(Polarity.Keys.UIStack.BUTTON_ID, this::buttonID);

    }

    @Override
    public UIStackData asMutable() {
        return new UIStackData(this.stackType, this.stackDataID, this.buttonID);
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(Polarity.Keys.UIStack.TYPE, this.stackType)
                .set(Polarity.Keys.UIStack.DATA_ID, this.stackDataID)
                .set(Polarity.Keys.UIStack.BUTTON_ID, this.buttonID);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

}
