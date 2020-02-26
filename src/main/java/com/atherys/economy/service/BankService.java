package com.atherys.economy.service;

import com.atherys.core.economy.Economy;
import com.atherys.economy.AtherysEconomy;
import com.atherys.economy.config.Bank;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class BankService {

    private Map<UUID, Session> bankSessions = new HashMap<>();

    @Inject
    BankViewService bankViewService;

    public void withdraw(Player player, double amount) {
        exchange(
                player,
                amount,
                getSession(player).getTo(),
                getSession(player).getFrom(),
                "Withdrew ",
                "You do not have enough to withdraw."
        );
        bankViewService.withdrawView.updateElement(player, 49);
    }


    void deposit(Player player, double amount) {
        exchange(
                player,
                amount,
                getSession(player).getFrom(),
                getSession(player).getTo(),
                "Deposited ",
                "You do not have enough to deposit."
        );
        bankViewService.depositView.updateElement(player, 49);
    }

    public void depositOrWithdraw(Player player, String amount) {
        if (getSession(player).transactionType == TransactionType.DEPOSIT) {
            deposit(player, Double.parseDouble(amount));
        } else {
            withdraw(player, Double.parseDouble(amount));
        }
    }

    private void exchange(Player player, double amount, Currency from, Currency to, String success, String fail) {
        BigDecimal exchange = BigDecimal.valueOf(amount);
        UUID playerId = player.getUniqueId();

        if (Economy.getAccount(playerId).get().getBalance(from).compareTo(exchange) >= 0) {
            Cause cause = Cause.of(EventContext.empty(), player);
            Sponge.getCauseStackManager().getCurrentCause();

            Economy.addCurrency(
                    playerId,
                    to,
                    exchange,
                    cause
            );

            Economy.removeCurrency(
                    playerId,
                    from,
                    exchange,
                    cause
            );

            player.sendMessage(Text.of(DARK_GREEN, success, GOLD, from.format(exchange, 2), DARK_GREEN, "."));
        } else {
            player.sendMessage(Text.of(RED, fail));
        }
    }

    public void startCustomWithdrawal(Player player) {
        startCustomExchange(player, TransactionType.WITHDRAW, "Enter an amount to withdraw.");
    }


    public void startCustomDeposit(Player player) {
        startCustomExchange(player, TransactionType.DEPOSIT, "Enter an amount to deposit.");
    }

    private void startCustomExchange(Player player, TransactionType transactionType, String prompt) {
        Session session = getSession(player);
        session.transactionType = transactionType;
        Task.builder().execute(player::closeInventory).submit(AtherysEconomy.getInstance());
        player.sendMessage(Text.of(DARK_GREEN, prompt));
    }

    public void onClose(Player player) {
        if (getSession(player).transactionType == TransactionType.NONE) {
            endSession(player);
        }
    }

    public Text getPlayerFrom(Player player) {
        Currency from = getSession(player).getFrom();
        BigDecimal fromAmount = Economy.getAccount(player.getUniqueId()).get().getBalance(from);
        return from.format(fromAmount, 2);
    }

    public Text getPlayerTo(Player player) {
        Currency to = getSession(player).getTo();
        BigDecimal toAmount = Economy.getAccount(player.getUniqueId()).get().getBalance(to);
        return to.format(toAmount, 2);
    }

    public boolean isPlayerInSession(Player player) {
        return bankSessions.containsKey(player.getUniqueId());
    }

    public void startSession(Player player, Bank bank) {
        bankSessions.put(player.getUniqueId(), new Session(bank, TransactionType.NONE));
    }

    public void endSession(Player player) {
        bankSessions.remove(player.getUniqueId());
    }

    private Session getSession(Player player) {
        return bankSessions.get(player.getUniqueId());
    }

    private enum TransactionType {
        WITHDRAW, DEPOSIT, NONE
    }

    private static class Session {
        private Bank bank;
        private TransactionType transactionType;

        private Session(Bank bank, TransactionType transactionType) {
            this.bank = bank;
            this.transactionType = transactionType;
        }

        private Currency getFrom() {
            return bank.FROM;
        }

        private Currency getTo() {
            return bank.TO;
        }
    }
}
