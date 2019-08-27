package com.atherys.economy.facade;

import com.atherys.core.AtherysCore;
import com.atherys.core.economy.Economy;
import com.atherys.economy.EconomyConfig;
import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;

@Singleton
public class TransferFacade {
    @Inject
    EconomyConfig config;

    public void payPlayer(Player sender, double amount, Player receiver) throws CommandException {
        boolean sameWorld = sender.getWorld().getUniqueId().equals(receiver.getWorld().getUniqueId());
        Vector3i senderPosition = sender.getLocation().getBlockPosition();
        Vector3i receiverPosition = receiver.getLocation().getBlockPosition();
        boolean closeEnough = senderPosition.distance(receiverPosition) < config.PAY_DISTANCE;

        if (sameWorld && closeEnough) {
            Currency currency = AtherysCore.getEconomyService().get().getDefaultCurrency();

            Economy.transferCurrency(
                    sender.getUniqueId(),
                    receiver.getUniqueId(),
                    currency,
                    BigDecimal.valueOf(amount),
                    Cause.of(EventContext.empty(), sender)
            );

            sender.sendMessage(Text.of(GOLD, receiver.getName(), DARK_GREEN, " was sent ", GOLD, amount, DARK_GREEN, "."));
            receiver.sendMessage(Text.of(GOLD, sender.getName(), DARK_GREEN, " sent you ", GOLD, amount, DARK_GREEN, "."));
        } else {
            throw new CommandException(Text.of("You are too far away from ", receiver.getName(), "."));
        }
    }
}
