package ru.skyfire.minecraft.guibuilder.util;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.util.Optional;

public class EcoUtil {
    public static boolean takeMoney(Player player, double cost, PluginContainer plugin){
        EconomyService service = Sponge.getServiceManager().provide(EconomyService.class).get();
        final Optional<UniqueAccount> account = service.getOrCreateAccount(player.getUniqueId());
        if(!account.isPresent()){
            return false;
        }
        Account acc = getAccount(player);
        if (acc.getBalance(service.getDefaultCurrency()).compareTo(BigDecimal.valueOf(cost)) < 0) {
            return false;
        }
        acc.withdraw(service.getDefaultCurrency(), BigDecimal.valueOf(cost),
                Cause.of(EventContext.builder().build(), plugin.getInstance().get()));
        return true;
    }

    public static boolean giveMoney(Player player, double cost, PluginContainer plugin){
        EconomyService service = Sponge.getServiceManager().provide(EconomyService.class).get();
        final Optional<UniqueAccount> account = service.getOrCreateAccount(player.getUniqueId());
        if(!account.isPresent()){
            return false;
        }
        Account acc = account.get();
        acc.deposit(service.getDefaultCurrency(), BigDecimal.valueOf(cost),
                Cause.of(EventContext.builder().build(), plugin.getInstance().get()));
        return true;
    }

    public static BigDecimal getBalance(Player player){
        EconomyService service = Sponge.getServiceManager().provide(EconomyService.class).get();
        final Optional<UniqueAccount> account = service.getOrCreateAccount(player.getUniqueId());
        if(!account.isPresent()){
            return BigDecimal.ZERO;
        }
        Account acc = account.get();
        return acc.getBalance(service.getDefaultCurrency());
    }

    public static double getBalanceDouble(Player player){
        EconomyService service = Sponge.getServiceManager().provide(EconomyService.class).get();
        final Optional<UniqueAccount> account = service.getOrCreateAccount(player.getUniqueId());
        if(!account.isPresent()){
            return BigDecimal.ZERO.doubleValue();
        }
        Account acc = account.get();
        return acc.getBalance(service.getDefaultCurrency()).doubleValue();
    }

    private static Account getAccount(Player player){
        EconomyService service = Sponge.getServiceManager().provide(EconomyService.class).get();
        final Optional<UniqueAccount> account = service.getOrCreateAccount(player.getUniqueId());
        return account.orElse(null);
    }

    private static EconomyService getService(){
        return Sponge.getServiceManager().provide(EconomyService.class).get();
    }
}
