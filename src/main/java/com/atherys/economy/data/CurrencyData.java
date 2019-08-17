package com.atherys.economy.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import javax.annotation.Generated;
import java.util.Optional;

@Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2019-07-27T02:02:34.189Z")
public class CurrencyData extends AbstractData<CurrencyData, CurrencyData.Immutable> {

    private Double amount;
    private Currency currency;

    {
        registerGettersAndSetters();
    }

    CurrencyData() {
        currency = Sponge.getServiceManager().provideUnchecked(EconomyService.class).getDefaultCurrency();
        amount = 0.0;
    }

    public CurrencyData(Double amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(CurrencyKeys.AMOUNT, this::getAmount);
        registerFieldSetter(CurrencyKeys.AMOUNT, this::setAmount);
        registerKeyValue(CurrencyKeys.AMOUNT, this::amount);
        registerFieldGetter(CurrencyKeys.CURRENCY, this::getCurrency);
        registerFieldSetter(CurrencyKeys.CURRENCY, this::setCurrency);
        registerKeyValue(CurrencyKeys.CURRENCY, this::currency);
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Value<Double> amount() {
        return Sponge.getRegistry().getValueFactory().createValue(CurrencyKeys.AMOUNT, amount);
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Value<Currency> currency() {
        return Sponge.getRegistry().getValueFactory().createValue(CurrencyKeys.CURRENCY, currency);
    }

    @Override
    public Optional<CurrencyData> fill(DataHolder dataHolder, MergeFunction overlap) {
        dataHolder.get(CurrencyData.class).ifPresent(that -> {
            CurrencyData data = overlap.merge(this, that);
            this.amount = data.amount;
            this.currency = data.currency;
        });
        return Optional.of(this);
    }

    @Override
    public Optional<CurrencyData> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<CurrencyData> from(DataView container) {
        container.getObject(CurrencyKeys.AMOUNT.getQuery(), Double.class).ifPresent(v -> amount = v);
        container.getObject(CurrencyKeys.CURRENCY.getQuery(), Currency.class).ifPresent(v -> currency = v);
        return Optional.of(this);
    }

    @Override
    public CurrencyData copy() {
        return new CurrencyData(amount, currency);
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(amount, currency);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(CurrencyKeys.AMOUNT.getQuery(), amount)
                .set(CurrencyKeys.CURRENCY.getQuery(), currency);
    }

    @Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2019-07-27T02:02:34.235Z")
    public static class Immutable extends AbstractImmutableData<Immutable, CurrencyData> {

        private Double amount;
        private Currency currency;
        {
            registerGetters();
        }

        Immutable() {
            currency = Sponge.getServiceManager().provideUnchecked(EconomyService.class).getDefaultCurrency();
        }

        Immutable(Double amount, Currency currency) {
            this.amount = amount;
            this.currency = currency;
        }

        @Override
        protected void registerGetters() {
            registerFieldGetter(CurrencyKeys.AMOUNT, this::getAmount);
            registerKeyValue(CurrencyKeys.AMOUNT, this::amount);
            registerFieldGetter(CurrencyKeys.CURRENCY, this::getCurrency);
            registerKeyValue(CurrencyKeys.CURRENCY, this::currency);
        }

        public Double getAmount() {
            return amount;
        }

        public ImmutableValue<Double> amount() {
            return Sponge.getRegistry().getValueFactory().createValue(CurrencyKeys.AMOUNT, amount).asImmutable();
        }

        public Currency getCurrency() {
            return currency;
        }

        public ImmutableValue<Currency> currency() {
            return Sponge.getRegistry().getValueFactory().createValue(CurrencyKeys.CURRENCY, currency).asImmutable();
        }

        @Override
        public CurrencyData asMutable() {
            return new CurrencyData(amount, currency);
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer()
                    .set(CurrencyKeys.AMOUNT.getQuery(), amount)
                    .set(CurrencyKeys.CURRENCY.getQuery(), currency);
        }

    }

    @Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2019-07-27T02:02:34.240Z")
    public static class Builder extends AbstractDataBuilder<CurrencyData> implements DataManipulatorBuilder<CurrencyData, Immutable> {

        public Builder() {
            super(CurrencyData.class, 1);
        }

        @Override
        public CurrencyData create() {
            return new CurrencyData();
        }

        @Override
        public Optional<CurrencyData> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        protected Optional<CurrencyData> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }

    }
}
