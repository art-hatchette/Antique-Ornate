package art.hatchette.antique_ornate.mixin;

import art.hatchette.antique_ornate.block.OrnateBlocks;
import art.hatchette.antique_ornate.item.ModCreativeModeTabs;
import art.hatchette.antique_ornate.item.ModItems;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Shadow private Collection<ItemStack> displayItems;
    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @WrapMethod(method = "buildContents")
    private void antique_ornate$buildContents(CreativeModeTab.ItemDisplayParameters parameters, Operation<Void> original) {
        CreativeModeTab self = (CreativeModeTab) (Object) this;
        
        boolean isMyTab = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(self)
                .map(key -> key.equals(ModCreativeModeTabs.TAB_KEY))
                .orElse(false);

        if (isMyTab) {
            List<ItemStack> items = new ArrayList<>();
            
            // Add actual items
            items.add(ModItems.SOUL.get().getDefaultInstance());
            items.add(new ItemStack(OrnateBlocks.WRAITHWOOD_PLANKS.get()));
            items.add(new ItemStack(OrnateBlocks.DIVINTINE.get()));
            items.add(new ItemStack(OrnateBlocks.DIVINTINE_BRICKS.get()));
            items.add(new ItemStack(OrnateBlocks.ORNATE_GLASS.get()));
            items.add(new ItemStack(OrnateBlocks.WRAITHWOOD_VERSAILLES_TRIMPARQUET.get()));
            items.add(new ItemStack(OrnateBlocks.FRAMED_BLACK_LACQUER.get()));

            this.displayItems = items;
            this.displayItemsSearchTab = new LinkedHashSet<>(items);
            return;
        }
        
        original.call(parameters);
    }
}
