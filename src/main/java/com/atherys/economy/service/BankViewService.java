package com.atherys.economy.service;

import com.atherys.core.menu.PlayerView;
import com.atherys.economy.AtherysEconomy;
import com.atherys.economy.EconomyConfig;
import com.atherys.economy.config.BankIncrement;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;
import static org.spongepowered.api.text.format.TextStyles.BOLD;

@Singleton
public class BankViewService {
    @Inject
    BankService bankService;

    @Inject
    EconomyConfig config;

    PlayerView bankView;
    PlayerView depositView;
    PlayerView withdrawView;

    private ItemStack depositItem = ItemStack.builder()
            .itemType(ItemTypes.GREEN_GLAZED_TERRACOTTA)
            .quantity(1)
            .add(Keys.DISPLAY_NAME, Text.of("Deposit"))
            .build();

    private ItemStack withdrawItem = ItemStack.builder()
            .itemType(ItemTypes.RED_GLAZED_TERRACOTTA)
            .quantity(1)
            .add(Keys.DISPLAY_NAME, Text.of("Withdraw"))
            .build();

    public void openBank(Player player) {
        bankView.startView(player, AtherysEconomy.getInstance().getContainer());
    }

    public void init() {
        Consumer<Action<InteractInventoryEvent.Close>> onClose = action -> bankService.onClose(action.getPlayer());
        depositView = new PlayerView(
                View.builder().archetype(archetype("Deposit", true)),
                depositLayout(),
                ImmutableMap.of(49, this::balanceDeposit)
        );
        depositView.setOnClose(onClose);

        withdrawView = new PlayerView(
                View.builder().archetype(archetype("Withdraw", true)),
                withdrawLayout(),
                ImmutableMap.of(49, this::balanceWithdraw)
        );
        withdrawView.setOnClose(onClose);

        bankView = new PlayerView(
                View.builder().archetype(archetype("Bank", false)),
                bankLayout(),
                ImmutableMap.of(
                        22, this::balanceBank,
                        10, p -> depositButton(),
                        16, p -> withdrawButton()
                )
        );
        bankView.setOnClose(onClose);
    }

    private Layout bankLayout() {
        return Layout.builder()
                .fill(background(DyeColors.GRAY))
                .set(background(DyeColors.LIME), 1, 9, 11, 19)
                .set(background(DyeColors.RED), 7, 15, 17, 25)
                .build();
    }

    private Layout depositLayout() {
        return transactionMenu(
                Text.of("Deposit"),
                bankService::deposit,
                bankService::startCustomDeposit
        );
    }

    private Layout withdrawLayout() {
        return transactionMenu(
                Text.of("Withdraw"),
                bankService::withdraw,
                bankService::startCustomWithdrawal
        );
    }

    private Layout transactionMenu(Text label, BiConsumer<Player, Integer> consumer, Consumer<Player> custom) {
        Layout.Builder layout = Layout.builder()
                .fill(background(DyeColors.GRAY));

        int i = 12;
        for (BankIncrement increment : config.BANK_INCREMENTS) {
            AtherysEconomy.getInstance().getLogger().info(increment.ITEM.getName());
            AtherysEconomy.getInstance().getLogger().info("" + i);
            ItemStack incrementItem = ItemStack.builder()
                    .itemType(increment.ITEM)
                    .quantity(1)
                    .add(Keys.DISPLAY_NAME, Text.of(label, " ", increment.AMOUNT))
                    .build();

            layout.set(Element.of(incrementItem, action -> {
                consumer.accept(action.getPlayer(), increment.AMOUNT);
            }), i);

            if (i == 14) {
                i = 21;
            } else {
                i++;
            }
        }

        ItemStack customIncrement = ItemStack.builder()
                .itemType(ItemTypes.WRITABLE_BOOK)
                .quantity(1)
                .add(Keys.DISPLAY_NAME, Text.of("Custom ", label))
                .build();

        layout.set(Element.of(customIncrement, action -> custom.accept(action.getPlayer())), 31);

        return layout.build();
    }

    private InventoryArchetype archetype(String title, boolean large) {
        return InventoryArchetype.builder()
                .with(large ? InventoryArchetypes.DOUBLE_CHEST : InventoryArchetypes.CHEST)
                .build("atheryseconomy:" + title.toLowerCase(), title);
    }

    private Element background(DyeColor color) {
        return Element.of(
                ItemStack.builder()
                        .itemType(ItemTypes.STAINED_GLASS_PANE)
                        .quantity(1)
                        .add(Keys.DISPLAY_NAME, Text.EMPTY)
                        .add(Keys.DYE_COLOR, color)
                        .build()
        );
    }

    private Element balanceBank(Player player) {
        return Element.of(
                balanceItem(player),
                action -> bankView.updateElement(player, 22)
        );
    }

    private Element balanceWithdraw(Player player) {
        return Element.of(
                balanceItem(player),
                action -> withdrawView.updateElement(player, 49)
        );
    }

    private Element balanceDeposit(Player player) {
        return Element.of(
                balanceItem(player),
                action -> depositView.updateElement(player, 49)
        );
    }

    private ItemStack balanceItem(Player player) {
        return ItemStack.builder()
                .itemType(ItemTypes.GOLD_BLOCK)
                .quantity(1)
                .add(Keys.DISPLAY_NAME, Text.of(GOLD, BOLD, "Balance"))
                .add(Keys.ITEM_LORE, Lists.newArrayList(
                        Text.of(GOLD, "In Bank: ", DARK_GREEN, bankService.getPlayerTo(player)),
                        Text.of(GOLD, "On Hand: ", DARK_GREEN, bankService.getPlayerFrom(player)),
                        Text.of("Click to update balance!")
                ))
                .build();
    }

    private Element depositButton() {
        return Element.of(
                depositItem,
                a -> bankView.switchToView(
                        a.getPlayer(),
                        depositView,
                        AtherysEconomy.getInstance().getContainer()
                )
        );
    }

    private Element withdrawButton() {
        return Element.of(
                withdrawItem,
                a -> bankView.switchToView(
                        a.getPlayer(),
                        withdrawView,
                        AtherysEconomy.getInstance().getContainer()
                )
        );
    }
}
