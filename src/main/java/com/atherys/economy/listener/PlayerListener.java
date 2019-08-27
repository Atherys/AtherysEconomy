package com.atherys.economy.listener;

import com.atherys.economy.AtherysEconomy;
import com.atherys.economy.EconomyConfig;
import com.atherys.economy.facade.BankFacade;
import com.atherys.economy.facade.CarriedCurrencyFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;

@Singleton
public class PlayerListener {
    @Inject
    CarriedCurrencyFacade currencyFacade;

    @Inject
    BankFacade bankFacade;

    @Inject
    EconomyConfig config;

    @Listener
    public void onPlayerDeath(DestructEntityEvent event, @Getter("getTargetEntity") Player player) {
        currencyFacade.onPlayerDeath(player);
        AtherysEconomy.getInstance().getLogger().info(player.getName() + " has died gracefully.");
    }

    @Listener
    public void onItemPickup(ChangeInventoryEvent.Pickup event, @Root Player player) {
        currencyFacade.onPickupCurrency(player, event);
    }

    @Listener
    public void onRightClickEntity(InteractEntityEvent.Secondary.MainHand event, @Root Player player) {
        if (config.BANKERS.containsKey(event.getTargetEntity().getUniqueId())) {
            bankFacade.openBank(player, config.BANKERS.get(event.getTargetEntity().getUniqueId()));
        }
    }

    @Listener
    public void onPlayerChat(MessageChannelEvent.Chat event, @Root Player player) {
        if (bankFacade.isPlayerInSession(player)) {
            bankFacade.onPlayerChat(player, event.getRawMessage().toPlain());
            event.setMessageCancelled(true);
        }
    }
}
