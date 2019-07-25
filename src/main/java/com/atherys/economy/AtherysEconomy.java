package com.atherys.economy;

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import static com.atherys.economy.AtherysEconomy.*;

@Plugin(
        id = ID,
        name = NAME,
        description = DESCRIPTION,
        version = VERSION,
        dependencies = {
                @Dependency(id = "atheryscore"),
                @Dependency(id = "pieconomy")
        }
)
public class AtherysEconomy {
    public static final String ID = "atheryseconomy";
    public static final String NAME = "A'therys Economy";
    public static final String DESCRIPTION = "An Economy plugin written for the A'therys Horizons server.";
    public static final String VERSION = "1.0.0a";

}
