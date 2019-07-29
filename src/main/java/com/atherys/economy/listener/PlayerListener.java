package com.atherys.economy.listener;

import com.atherys.economy.AtherysEconomy;
import com.atherys.economy.CurrencyKeys;
import com.atherys.economy.facade.CarriedCurrencyFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

@Singleton
public class PlayerListener {
    @Inject
    CarriedCurrencyFacade currencyFacade;

    @Listener
    public void onPlayerDeath(DestructEntityEvent event, @Getter("getTargetEntity") Player player) {
        currencyFacade.onPlayerDeath(player);
        AtherysEconomy.getInstance().getLogger().info(player.getName() + " has died gracefully.");
    }

    @Listener
    public void onItemPickup(ChangeInventoryEvent.Pickup event, @Root Player player) {
        currencyFacade.onPickupCurrency(player, event);
    }
}
