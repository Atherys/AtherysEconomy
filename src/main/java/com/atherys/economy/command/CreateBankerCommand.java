package com.atherys.economy.command;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.economy.AtherysEconomy;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nonnull;

@Aliases("banker")
@Permission("atheryseconomy.create.banker")
@Description("Turns the entity into a Banker.")
public class CreateBankerCommand implements PlayerCommand, ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.catalogedElement(Text.of("from"), Currency.class),
                GenericArguments.catalogedElement(Text.of("to"), Currency.class),
                GenericArguments.optional(GenericArguments.text(
                        Text.of("name"),
                        TextSerializers.FORMATTING_CODE,
                        true
                ))
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysEconomy.getInstance().getBankFacade().createBanker(
                source,
                args.<Currency>getOne("from").get(),
                args.<Currency>getOne("to").get(),
                args.getOne("name")
        );
        return CommandResult.success();
    }
}
