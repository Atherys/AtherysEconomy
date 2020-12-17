package com.atherys.economy;

import com.atherys.core.command.CommandService;
import com.atherys.core.command.CommandService.AnnotatedCommandException;
import com.atherys.core.economy.Economy;
import com.atherys.economy.command.CreateBankerCommand;
import com.atherys.economy.command.PayCommand;
import com.atherys.economy.config.CarriedCurrency;
import com.atherys.economy.data.CurrencyData;
import com.atherys.economy.data.CurrencyKeys;
import com.atherys.economy.facade.BankFacade;
import com.atherys.economy.facade.CarriedCurrencyFacade;
import com.atherys.economy.facade.TransferFacade;
import com.atherys.economy.listener.PlayerListener;
import com.atherys.economy.service.BankService;
import com.atherys.economy.service.BankViewService;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.Currency;

import static com.atherys.economy.AtherysEconomy.*;

@Plugin(
        id = ID,
        name = NAME,
        description = DESCRIPTION,
        version = VERSION,
        dependencies = {
                @Dependency(id = "atheryscore"),
        }
)
public class AtherysEconomy {
    public static final String ID = "atheryseconomy";
    public static final String NAME = "A'therys Economy";
    public static final String DESCRIPTION = "An Economy plugin written for the A'therys Horizons server.";
    public static final String VERSION = "1.0.0a";

    private static AtherysEconomy instance;

    @Inject
    Injector injector;

    @Inject
    Logger logger;

    @Inject
    PluginContainer container;

    private Components components;
    private Injector econInjector;

    private boolean init = true;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        DataRegistration<CurrencyData, CurrencyData.Immutable> registration = DataRegistration.builder()
                .dataClass(CurrencyData.class)
                .immutableClass(CurrencyData.Immutable.class)
                .builder(new CurrencyData.Builder())
                .dataName("CurrencyData")
                .manipulatorId("atheryseconomy:currency")
                .buildAndRegister(container);

        Key key = CurrencyKeys.CURRENCY;
        key = CurrencyKeys.AMOUNT;
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        if (!Economy.isPresent())  {
            logger.error("An economy service is not installed. AtherysEconomy cannot function without one.");
            init = false;
            return;
        }

        instance = this;
        components = new Components();

        econInjector = injector.createChildInjector(new AtherysEconomyModule());
        econInjector.injectMembers(components);

        components.config.init();
        components.bankViewService.init();
    }

    @Listener
    public void onStart(GameStartedServerEvent event) {
        if (!init) {
            return;
        }

        for (CarriedCurrency currency : components.config.CURRENCIES) {
            if (!Sponge.getRegistry().getType(Currency.class, currency.CURRENCY.getId()).isPresent()) {
                logger.warn("Currency {} is not a loaded currency.", currency.CURRENCY);
            }
        }

        Sponge.getEventManager().registerListeners(this, components.playerListener);

        try {
            CommandService.getInstance().register(new PayCommand(), this);
            CommandService.getInstance().register(new CreateBankerCommand(), this);
        } catch (AnnotatedCommandException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        components.config.load();
    }

    @Listener
    public void onStop(GameStoppedEvent event) {
        components.config.save();
    }

    public static AtherysEconomy getInstance() {
        return instance;
    }

    public PluginContainer getContainer() {
        return container;
    }

    public TransferFacade getTransferFacade() {
        return components.transferFacade;
    }

    public BankFacade getBankFacade() {
        return components.bankFacade;
    }

    public Logger getLogger() {
        return logger;
    }

    private class Components {
        @Inject
        EconomyConfig config;

        @Inject
        CarriedCurrencyFacade carriedCurrencyFacade;

        @Inject
        TransferFacade transferFacade;

        @Inject
        BankFacade bankFacade;

        @Inject
        BankService bankService;

        @Inject
        BankViewService bankViewService;

        @Inject
        PlayerListener playerListener;
    }
}
