package io.sl4sh.xmanager.economy.ui;

import de.dosmike.sponge.megamenus.api.elements.IIcon;
import de.dosmike.sponge.megamenus.api.elements.concepts.IClickable;
import de.dosmike.sponge.megamenus.api.elements.concepts.IPressable;
import de.dosmike.sponge.megamenus.api.listener.OnClickListener;
import de.dosmike.sponge.megamenus.api.listener.OnKeyListener;
import de.dosmike.sponge.megamenus.impl.BaseMenuImpl;
import de.dosmike.sponge.megamenus.impl.RenderManager;
import de.dosmike.sponge.megamenus.impl.TextMenuRenderer;
import de.dosmike.sponge.megamenus.impl.elements.IElementImpl;
import io.sl4sh.xmanager.economy.XDollar;
import io.sl4sh.xmanager.economy.XEconomyShopRecipe;
import io.sl4sh.xmanager.economy.XOnShopButtonClick;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.translation.locale.Locales;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public final class XButton extends IElementImpl implements IClickable<XButton>, IPressable<XButton> {
    private IIcon defaultIcon = null;
    private OnClickListener<XButton> clickListener = null;
    private OnKeyListener<XButton> keyListener = null;
    private Text defaultName = Text.of(this.getClass().getSimpleName());
    private List<Text> defaultLore = new LinkedList();
    private XEconomyShopRecipe shopRecipe = null;
    private Locale playerLocale = null;

    public OnClickListener<XButton> getOnClickListener() {
        return this.clickListener;
    }

    public void fireClickEvent(Player viewer, int button, boolean shift) {
        if (this.clickListener != null) {
            this.clickListener.onClick(this, viewer, button, shift);
        }

    }

    public void setOnClickListener(OnClickListener<XButton> listener) {
        this.clickListener = listener;
    }

    public OnKeyListener<XButton> getOnKeyListener() {
        return this.keyListener;
    }

    public void fireKeyEvent(Player viewer, Buttons key, boolean ctrl) {
        if (this.keyListener != null) {
            this.keyListener.onKeyPress(this, viewer, key, ctrl);
        }

    }

    public void setOnKeyListener(OnKeyListener<XButton> listener) {
        this.keyListener = listener;
    }

    public IIcon getIcon(Player viewer) {
        return this.defaultIcon;
    }

    public Text getName(Player viewer) {
        return this.defaultName;
    }

    public List<Text> getLore(Player viewer) {
        return this.defaultLore;
    }

    public void setIcon(IIcon icon) {
        this.defaultIcon = icon;
    }

    public void setName(Text name) {
        this.defaultName = name;
    }

    public void setLore(List<Text> lore) {
        this.defaultLore = new LinkedList(lore);
    }

    public XButton() {
    }

    public static XButton.Builder builder() {
        return new XButton.Builder();
    }

    public Text renderTUI(Player viewer) {
        IIcon icon = this.getIcon(viewer);
        List<Text> lore = this.getLore(viewer);
        Text display = this.getName(viewer);
        display = Text.builder().append(new Text[]{display}).style(new TextStyle[]{TextStyles.of(new TextStyle[]{TextStyles.RESET})}).build();
        if (lore.isEmpty()) {
            return Text.builder().append(new Text[]{display}).onClick(TextActions.executeCallback((src) -> {
                RenderManager.getRenderFor((Player)src).filter((r) -> {
                    return r instanceof TextMenuRenderer;
                }).ifPresent((r) -> {
                    ((TextMenuRenderer)r).delegateClickEvent(this, (Player)src);
                });
            })).build();
        } else {
            List<Text> sublore = lore.size() > 1 ? lore.subList(1, lore.size()) : Collections.EMPTY_LIST;
            return Text.builder().append(new Text[]{display}).onHover((HoverAction)(icon != null ? TextActions.showItem(ItemStack.builder().fromSnapshot(icon.render()).add(Keys.DISPLAY_NAME, lore.get(0)).add(Keys.ITEM_LORE, sublore).build().createSnapshot()) : TextActions.showText(Text.of(new Object[]{Text.joinWith(Text.of(new Object[]{Text.NEW_LINE}), lore)})))).onClick(TextActions.executeCallback((src) -> {
                RenderManager.getRenderFor((Player)src).filter((r) -> {
                    return r instanceof TextMenuRenderer;
                }).ifPresent((r) -> {
                    ((TextMenuRenderer)r).delegateClickEvent(this, (Player)src);
                });
            })).build();
        }
    }

    public XButton copy() {
        XButton copy = new XButton();
        copy.setPosition(this.getPosition());
        copy.setParent(this.getParent());
        copy.defaultName = this.defaultName;
        copy.defaultIcon = this.defaultIcon;
        copy.defaultLore = new LinkedList(this.defaultLore);
        copy.clickListener = new XOnShopButtonClick();
        copy.shopRecipe = this.shopRecipe;
        copy.playerLocale = this.playerLocale;

        if(copy.shopRecipe != null && copy.shopRecipe.isValidRecipe()){

            copy.defaultIcon = IIcon.of(shopRecipe.getTargetItem());
            Locale locale = copy.playerLocale == null ? Locales.EN_US : copy.playerLocale;
            copy.defaultName = Text.of(shopRecipe.getTargetItem().getTranslation().get(locale));

            copy.defaultLore = new LinkedList<>();
            copy.defaultLore.add(Text.of(TextColors.AQUA, "Price : ", new XDollar().format(BigDecimal.valueOf(shopRecipe.getPrice()))));

        }

        return copy;
    }

    public XEconomyShopRecipe getShopRecipe() {
        return shopRecipe;
    }

    public void setShopRecipe(XEconomyShopRecipe shopRecipe) {
        this.shopRecipe = shopRecipe;
    }

    public Locale getPlayerLocale() {
        return playerLocale;
    }

    public void setPlayerLocale(Locale playerLocale) {
        this.playerLocale = playerLocale;
    }

    public static class Builder {
        XButton element;

        private Builder() {
            this.element = new XButton();
        }

        public XButton.Builder setPosition(SlotPos position) {
            this.element.setPosition(position);
            return this;
        }

        public XButton.Builder setIcon(IIcon icon) {
            this.element.defaultIcon = icon;
            return this;
        }

        public XButton.Builder setIcon(ItemStackSnapshot icon) {
            this.element.defaultIcon = IIcon.of(icon);
            return this;
        }

        public XButton.Builder setIcon(ItemStack icon) {
            this.element.defaultIcon = IIcon.of(icon);
            return this;
        }

        public XButton.Builder setIcon(ItemType icon) {
            this.element.defaultIcon = IIcon.of(icon);
            return this;
        }

        public XButton.Builder setName(Text name) {
            this.element.defaultName = name;
            return this;
        }

        public XButton.Builder setRecipe(XEconomyShopRecipe recipe) {
            this.element.shopRecipe = recipe;
            return this;
        }

        public XButton.Builder setLore(List<Text> lore) {
            this.element.defaultLore.clear();
            this.element.defaultLore.addAll(lore);
            return this;
        }

        public XButton.Builder setOnClickListener(OnClickListener<XButton> listener) {
            this.element.clickListener = listener;
            return this;
        }

        public XButton.Builder setPlayerLocale(Locale locale) {
            this.element.playerLocale = locale;
            return this;
        }

        public XButton build() {
            XButton copy = this.element.copy();
            return copy;
        }
    }
}

