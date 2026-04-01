package actuallyharvest.data;

import java.util.concurrent.CompletableFuture;

import actuallyharvest.common.Translations;
import com.google.common.base.Joiner;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

import actuallyharvest.ActuallyHarvest;

public class ActuallyHarvestLanguageProvider extends FabricLanguageProvider {

    private static final Joiner LINE_JOINER = Joiner.on("\n");

    protected ActuallyHarvestLanguageProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryFuture) {
        super(dataOutput, "en_us", registryFuture);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder builder) {
        addTranslationTitle(builder, "Actually Harvest");
        addTranslationName(builder, "general", "General");
        addTranslationDescription(builder, "general");
        addTranslationName(builder, "allowemptyhand", "Allow Empty Hand");
        addTranslationDescription(builder, "allowemptyhand");
        addTranslationName(builder, "allowfakeplayer", "Allow Fake Player");
        addTranslationDescription(builder, "allowfakeplayer");
        addTranslationName(builder, "autoconfigmods", "Auto Config Mods");
        addTranslationDescription(builder, "autoconfigmods");
        addTranslationName(builder, "blacklistcrops", "Blacklist Crops");
        addTranslationDescription(builder, "blacklistcrops");
        addTranslationName(builder, "blacklisthelditems", "Blacklist Held Items");
        addTranslationDescription(builder, "blacklisthelditems");
        addTranslationName(builder, "blacklistmods", "Blacklist Mods");
        addTranslationDescription(builder, "blacklistmods");
        addTranslationName(builder, "damagetool", "Damage Tool");
        addTranslationDescription(builder, "damagetool");
        addTranslationName(builder, "expandhoerange", "Expand Hoe Range");
        addTranslationDescription(builder, "expandhoerange");
        addTranslationName(builder, "expandhoerangeenchanted", "Expand Enchanted Hoe Range");
        addTranslationDescription(builder, "expandhoerangeenchanted");
        addTranslationName(builder, "harvestableblocks", "Harvestable Blocks");
        addTranslationDescription(builder, "harvestableblocks");
        addTranslationName(builder, "harvestablecrops", "Harvestable Crops");
        addTranslationDescription(builder, "harvestablecrops");
        addTranslationName(builder, "hightierexpansionrange", "High Tier Expansion Range");
        addTranslationDescription(builder, "hightierexpansionrange");
        addTranslationName(builder, "hoeitems", "Hoe Items");
        addTranslationDescription(builder, "hoeitems");
        addTranslationName(builder, "maxhoeexpansionrange", "Max Hoe Expansion Range");
        addTranslationDescription(builder, "maxhoeexpansionrange");
        addTranslationName(builder, "smalltierexpansionrange", "Small Tier Expansion Range");
        addTranslationDescription(builder, "smalltierexpansionrange");
        addTranslationName(builder, "xpfromharvestamount", "XP From Harvest Amount");
        addTranslationDescription(builder, "xpfromharvestamount");
        addTranslationName(builder, "xpfromharvestchance", "XP From Harvest Chance");
        addTranslationDescription(builder, "xpfromharvestchance");
        addTranslationName(builder, "xpfromharvestrangeamount", "XP From Harvest Range Amount");
        addTranslationDescription(builder, "xpfromharvestrangeamount");
        addTranslationName(builder, "xpfromharvestuserange", "XP From Harvest Use Range");
        addTranslationDescription(builder, "xpfromharvestuserange");
        addTranslationName(builder, "replantcrops", "Replant Crops");
        addTranslationDescription(builder, "replantcrops");
    }

    private void addTranslationTitle(TranslationBuilder builder, String title) {
        builder.add(ActuallyHarvest.MODID + ".configuration.title", title);
    }

    private void addTranslationName(TranslationBuilder builder, String id, String name) {
        builder.add(ActuallyHarvest.MODID + ".configuration." + id + ".name", name);
    }

    private void addTranslationDescription(TranslationBuilder builder, String id) {
        builder.add(ActuallyHarvest.MODID + ".configuration." + id + ".description", Translations.get(id));
    }

    private String joiner(String... string) {
        return LINE_JOINER.join(string);
    }

}
