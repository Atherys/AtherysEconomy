package com.atherys.economy.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CarriedCurrency {

    @Setting("currency")
    public String CURRENCY = "";

    @Setting("drop-rate")
    public double DROP_RATE = 0.75;

    @Setting("void-rate")
    public double VOID_RATE = 0.25;
}
