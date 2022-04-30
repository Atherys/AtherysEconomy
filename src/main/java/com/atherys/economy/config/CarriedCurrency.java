package com.atherys.economy.config;

import com.atherys.core.AtherysCore;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.service.economy.Currency;

@ConfigSerializable
public class CarriedCurrency {

    @Setting("currency")
    public Currency CURRENCY = AtherysCore.getEconomyService().get().getDefaultCurrency();

    @Setting(value = "drop-rate", comment = "What percentage of the currency gets dropped")
    public double DROP_RATE = 0.0;

    @Setting(value = "void-rate", comment = "What percentage of the currency gets lost")
    public double VOID_RATE = 0.0;

    @Setting(value = "item", comment = "What item the currency looks like when it gets dropped")
    public ItemType ITEM = ItemTypes.GOLD_INGOT;

    @Setting(value = "pvp-only", comment = "Should the currency be dropped only in PvP?")
    public boolean PVP_ONLY_DROP = false;
}
