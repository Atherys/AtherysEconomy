package com.atherys.economy;

import com.atherys.economy.facade.CarriedCurrencyFacade;
import com.atherys.economy.listener.PlayerListener;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AtherysEconomyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EconomyConfig.class).in(Scopes.SINGLETON);

        bind(CarriedCurrencyFacade.class).in(Scopes.SINGLETON);

        bind(PlayerListener.class).in(Scopes.SINGLETON);
    }
}
