package com.atherys.economy;

import com.atherys.economy.facade.BankFacade;
import com.atherys.economy.facade.CarriedCurrencyFacade;
import com.atherys.economy.facade.TransferFacade;
import com.atherys.economy.listener.PlayerListener;
import com.atherys.economy.service.BankService;
import com.atherys.economy.service.BankViewService;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AtherysEconomyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EconomyConfig.class).in(Scopes.SINGLETON);

        bind(CarriedCurrencyFacade.class).in(Scopes.SINGLETON);
        bind(TransferFacade.class).in(Scopes.SINGLETON);
        bind(BankFacade.class).in(Scopes.SINGLETON);

        bind(BankService.class).in(Scopes.SINGLETON);
        bind(BankViewService.class).in(Scopes.SINGLETON);

        bind(PlayerListener.class).in(Scopes.SINGLETON);
    }
}
