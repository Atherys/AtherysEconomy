package com.atherys.economy;

import com.atherys.core.utils.PluginConfig;
import com.atherys.economy.config.CarriedCurrency;
import com.google.inject.Singleton;
import ninja.leaping.configurate.objectmapping.Setting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class EconomyConfig extends PluginConfig {

    @Setting("currencies")
    public List<CarriedCurrency> CURRENCIES = new ArrayList<>();

    {
        CURRENCIES.add(new CarriedCurrency());
    }

    protected EconomyConfig() throws IOException {
        super("config/" + AtherysEconomy.ID, "config.conf");
    }
}
