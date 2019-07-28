package com.atherys.economy;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.service.economy.Currency;

public class CurrencyKeys {

    private CurrencyKeys() {}

    public static final Key<Value<Double>> AMOUNT;
    public static final Key<Value<Currency>> CURRENCY;

    static {
        AMOUNT = Key.builder()
                .id("atheryseconomy:amount")
                .name("Amount")
                .query(DataQuery.of("Amount"))
                .type(new TypeToken<Value<Double>>() {
                })
                .build();

        CURRENCY = Key.builder()
                .id("atheryseconomy:currency")
                .name("Currency")
                .query(DataQuery.of("Currency"))
                .type(new TypeToken<Value<Currency>>() {
                })
                .build();
    }
}
