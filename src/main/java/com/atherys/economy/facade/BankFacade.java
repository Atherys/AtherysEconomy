package com.atherys.economy.facade;

import com.atherys.core.utils.EntityUtils;
import com.atherys.economy.EconomyConfig;
import com.atherys.economy.config.Bank;
import com.atherys.economy.service.BankService;
import com.atherys.economy.service.BankViewService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.RED;

@Singleton
public class BankFacade {

    @Inject
    EconomyConfig config;

    @Inject
    CarriedCurrencyFacade currencyFacade;

    @Inject
    BankViewService bankViewService;

    @Inject
    BankService bankService;

    public void createBanker(Player player, Currency from, Currency to, Optional<Text> name) {
        EntityUtils.getNonPlayerFacingEntity(player, 20).ifPresent(entity -> {

            config.BANKERS.put(entity.getUniqueId(), new Bank(from, to));

            if (name.isPresent()) {
                entity.offer(Keys.DISPLAY_NAME, name.get());
            } else if (!config.DEFAULT_BANKER_NAME.isEmpty()) {
                entity.offer(Keys.DISPLAY_NAME, config.DEFAULT_BANKER_NAME);
            }

            player.sendMessage(Text.of(DARK_GREEN, "Banker created."));
        });
    }

    public void openBank(Player player, Bank bank) {
        bankService.startSession(player, bank);
        bankViewService.openBank(player);
    }

    public boolean isPlayerInSession(Player player) {
        return bankService.isPlayerInSession(player);
    }

    public void onPlayerChat(Player player, String message) {
        try {
            bankService.depositOrWithdraw(player, message);
        } catch (NumberFormatException e) {
            player.sendMessage(Text.of(RED, "Error parsing number."));
        } finally {
            bankService.endSession(player);
        }
    }
}
