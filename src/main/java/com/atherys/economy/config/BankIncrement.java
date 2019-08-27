package com.atherys.economy.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

@ConfigSerializable
public class BankIncrement {
    @Setting("amount")
    public int AMOUNT = 10;

    @Setting("item")
    public ItemType ITEM = ItemTypes.GOLD_INGOT;

    public BankIncrement() {}

    public BankIncrement(int amount, ItemType item) {
        this.AMOUNT = amount;
        this.ITEM = item;
    }
}
