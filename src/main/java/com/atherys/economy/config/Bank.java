package com.atherys.economy.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.service.economy.Currency;

@ConfigSerializable
public class Bank {
    @Setting("from")
    public Currency FROM;

    @Setting("to")
    public Currency TO;

    public Bank() {}

    public Bank(Currency from, Currency to) {
        FROM = from;
        TO = to;
    }
}
