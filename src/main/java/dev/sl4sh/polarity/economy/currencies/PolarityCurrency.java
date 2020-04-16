package dev.sl4sh.polarity.economy.currencies;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.math.BigDecimal;

public class PolarityCurrency implements Currency {
    @Override
    public Text getDisplayName() {
        return Text.of("Dollar");
    }

    @Override
    public Text getPluralDisplayName() {
        return Text.of("Dollars");
    }

    @Override
    public Text getSymbol() {
        return Text.of("$");
    }

    @Override
    public Text format(BigDecimal amount, int numFractionDigits) {
        return Text.of(TextColors.GOLD, getSymbol(), amount.setScale(numFractionDigits,BigDecimal.ROUND_HALF_UP));
    }

    @Override
    public int getDefaultFractionDigits() {
        return 2;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public String getId() {
        return "xdollar";
    }

    @Override
    public String getName() {
        return "PolarityCurrency";
    }
}
