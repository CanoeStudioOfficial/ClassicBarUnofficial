package tfar.classicbar.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.overlays.*;
import tfar.classicbar.overlays.modoverlays.LavaCharmNoBaublesRenderer;
import tfar.classicbar.overlays.modoverlays.LavaCharmRenderer;
//import tfar.classicbar.overlays.modoverlays.SuperiorShieldRenderer;
import tfar.classicbar.overlays.modoverlays.ThirstBarRenderer;

import static tfar.classicbar.config.ModConfig.ConfigEventHandler;
import static tfar.classicbar.config.ModConfig.general;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());

        //Register renderers for events
        ClassicBar.logger.info("Registering Vanilla Overlays");
        MinecraftForge.EVENT_BUS.register(new HealthBarRenderer());
        MinecraftForge.EVENT_BUS.register(new HealthBarMountRenderer());
        MinecraftForge.EVENT_BUS.register(new ArmorBarRenderer());
        if (general.overlays.displayToughnessBar) MinecraftForge.EVENT_BUS.register(new ArmorToughnessBarRenderer());
        MinecraftForge.EVENT_BUS.register(new HungerBarRenderer());

        //mod renderers
        if (Loader.isModLoaded("randomthings")) {
            if (Loader.isModLoaded("baubles"))
            MinecraftForge.EVENT_BUS.register(new LavaCharmRenderer());
            else MinecraftForge.EVENT_BUS.register(new LavaCharmNoBaublesRenderer());
        }
        //if (Loader.isModLoaded("superiorshields"))
          //  MinecraftForge.EVENT_BUS.register(new SuperiorShieldRenderer());
        if (Loader.isModLoaded("toughasnails"))
            MinecraftForge.EVENT_BUS.register(new ThirstBarRenderer());
            //MinecraftForge.EVENT_BUS.register(new TemperatureBarRenderer());
//MinecraftForge.EVENT_BUS.register(new BetterDivingRenderer());
            MinecraftForge.EVENT_BUS.register(new OxygenBarRenderer());

    }
}
