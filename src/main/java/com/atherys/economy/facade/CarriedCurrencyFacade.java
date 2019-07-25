package com.atherys.economy.facade;

import com.atherys.core.economy.Economy;
import com.atherys.economy.AtherysEconomy;
import com.atherys.economy.EconomyConfig;
import com.atherys.economy.config.CarriedCurrency;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Singleton
public class CarriedCurrencyFacade {

    @Inject
    EconomyConfig config;

    public void onPlayerDeath(Player player) {
        UniqueAccount account = Economy.getUniqueAccount(player.getUniqueId()).get();

        for (Map.Entry<Currency, BigDecimal> currency : account.getBalances().entrySet())  {
            AtherysEconomy.getInstance().getLogger().info("Currency found: {}", currency.getKey().getId());
            getCarriedCurrency(currency.getKey().getId()).ifPresent(carriedCurrency -> {
                AtherysEconomy.getInstance().getLogger().info("Currency: {}", currency.getKey().getId());

                BigDecimal voided = currency.getValue().multiply(BigDecimal.valueOf(carriedCurrency.VOID_RATE));
                BigDecimal dropped = currency.getValue().multiply(BigDecimal.valueOf(carriedCurrency.DROP_RATE));

                Cause cause = Cause.builder()
                        .append(player)
                        .build(EventContext.builder().build());

                account.withdraw(currency.getKey(), voided, cause);
                AtherysEconomy.getInstance().getLogger().info("Voided amount: {}", voided.toString());

                account.withdraw(currency.getKey(), dropped, cause);
                AtherysEconomy.getInstance().getLogger().info("Dropped amount: {}", dropped.toString());
            });
        }
    }

    public Optional<CarriedCurrency> getCarriedCurrency(String currencyId) {
        return config.CURRENCIES.stream()
                .filter(carriedCurrency -> carriedCurrency.CURRENCY.equals(currencyId))
                .findFirst();
    }
}
