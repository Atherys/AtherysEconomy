package com.atherys.economy.facade;

import com.atherys.core.economy.Economy;
import com.atherys.economy.AtherysEconomy;
import com.atherys.economy.CurrencyData;
import com.atherys.economy.CurrencyKeys;
import com.atherys.economy.EconomyConfig;
import com.atherys.economy.config.CarriedCurrency;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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
            getCarriedCurrency(currency.getKey()).ifPresent(carriedCurrency -> {
                AtherysEconomy.getInstance().getLogger().info("Currency found.");

                BigDecimal voided = currency.getValue().multiply(BigDecimal.valueOf(carriedCurrency.VOID_RATE));
                BigDecimal dropped = currency.getValue().multiply(BigDecimal.valueOf(carriedCurrency.DROP_RATE));

                Cause cause = getTransactionCause(player);

                account.withdraw(currency.getKey(), voided, cause);
                account.withdraw(currency.getKey(), dropped, cause);

                dropCurrency(carriedCurrency, dropped, player.getLocation());
            });
        }
    }

    public void dropCurrency(CarriedCurrency currency, BigDecimal amount, Location<World> location) {
        ItemStack item = ItemStack.builder()
                .itemType(currency.ITEM)
                .build();

        item.offer(new CurrencyData(amount.doubleValue(), currency.CURRENCY));

        Entity itemEntity = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
        itemEntity.offer(Keys.REPRESENTED_ITEM, item.createSnapshot());

        location.getExtent().spawnEntity(itemEntity);
    }

    public void onPickupCurrency(Player player, ItemStackSnapshot currencyItem) {
        Currency currency = currencyItem.get(CurrencyKeys.CURRENCY).get();
        double amount = currencyItem.get(CurrencyKeys.AMOUNT).get();

        player.sendMessage(Text.of(TextColors.DARK_GREEN, "You picked up ", TextColors.GOLD, amount, " ", currency.getPluralDisplayName(), "."));

        Economy.addCurrency(player.getUniqueId(), currency, BigDecimal.valueOf(amount), getTransactionCause(player));
    }

    public Optional<CarriedCurrency> getCarriedCurrency(Currency currency) {
        return config.CURRENCIES.stream()
                .filter(carriedCurrency -> carriedCurrency.CURRENCY.equals(currency))
                .findFirst();
    }

    private Cause getTransactionCause(Player player) {
        return Cause.of(EventContext.builder().build(), player);
    }
}
