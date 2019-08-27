package com.atherys.economy;

import com.atherys.core.utils.PluginConfig;
import com.atherys.economy.config.Bank;
import com.atherys.economy.config.BankIncrement;
import com.atherys.economy.config.CarriedCurrency;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class EconomyConfig extends PluginConfig {

    @Setting("currencies")
    public List<CarriedCurrency> CURRENCIES = Lists.newArrayList(new CarriedCurrency());

    @Setting("pay-distance")
    public int PAY_DISTANCE = 50;

    @Setting("default-banker-name")
    public Text DEFAULT_BANKER_NAME = Text.EMPTY;

    @Setting("bankers")
    public Map<UUID, Bank> BANKERS = new HashMap<>();

    @Setting("bank-increments")
    public List<BankIncrement> BANK_INCREMENTS = Lists.newArrayList(
            new BankIncrement(25, ItemTypes.IRON_INGOT),
            new BankIncrement(50, ItemTypes.GOLD_INGOT),
            new BankIncrement(100, ItemTypes.LAPIS_BLOCK)
    );

    protected EconomyConfig() throws IOException {
        super("config/" + AtherysEconomy.ID, "config.conf");
    }
}
