package com.atherys.economy.facade;

import com.atherys.core.economy.Economy;
import com.atherys.economy.AtherysEconomy;
import com.atherys.economy.EconomyConfig;
import com.atherys.economy.config.CarriedCurrency;
import com.atherys.economy.data.CurrencyData;
import com.atherys.economy.data.CurrencyKeys;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
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

    public void onPlayerDeath(Player player, boolean killedByPlayer) {
        Optional<UniqueAccount> accountOptional = Economy.getAccount(player.getUniqueId());

        if(!accountOptional.isPresent()) return;

        UniqueAccount account = accountOptional.get();

        for (Map.Entry<Currency, BigDecimal> currency : account.getBalances().entrySet())  {
            getCarriedCurrency(currency.getKey()).ifPresent(carriedCurrency -> {
                Cause cause = Cause.of(EventContext.empty(), player);

                BigDecimal voided = currency.getValue().multiply(BigDecimal.valueOf(carriedCurrency.VOID_RATE));
                account.withdraw(currency.getKey(), voided, cause);

                if (killedByPlayer && carriedCurrency.PVP_ONLY_DROP) return;

                BigDecimal dropped = currency.getValue().multiply(BigDecimal.valueOf(carriedCurrency.DROP_RATE));
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

    public Optional<ItemStack> createItemForCurrency(Currency currency, double amount) {
        return getCarriedCurrency(currency).map(carriedCurrency -> {
            ItemStack item = ItemStack.of(carriedCurrency.ITEM);
            item.offer(new CurrencyData(amount, carriedCurrency.CURRENCY));

            return item;
        });
    }

    public void onPickupCurrency(Player player, ChangeInventoryEvent.Pickup event) {
        for (SlotTransaction transaction : event.getTransactions()) {
            ItemStackSnapshot item = transaction.getFinal();

            if (item.get(CurrencyKeys.AMOUNT).isPresent() && item.get(CurrencyKeys.CURRENCY).isPresent()) {

                Currency currency = item.get(CurrencyKeys.CURRENCY).get();
                double amount = item.get(CurrencyKeys.AMOUNT).get();

                Economy.addCurrency(player.getUniqueId(), currency, BigDecimal.valueOf(amount), Cause.of(EventContext.empty(), player));
                player.sendMessage(Text.of(TextColors.DARK_GREEN, "You picked up ", TextColors.GOLD, String.format(".2f", amount), " ", currency.getPluralDisplayName(), "."));

                transaction.setValid(false);
            }
        }
    }

    public Optional<CarriedCurrency> getCarriedCurrency(Currency currency) {
        return config.CURRENCIES.stream()
                .filter(carriedCurrency -> carriedCurrency.CURRENCY.equals(currency))
                .findFirst();
    }
}
