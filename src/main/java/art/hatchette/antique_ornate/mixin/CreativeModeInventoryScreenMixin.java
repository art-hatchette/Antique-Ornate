package art.hatchette.antique_ornate.mixin;

import art.hatchette.antique_ornate.AntiqueOrnateMod;
import art.hatchette.antique_ornate.item.ModCreativeModeTabs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.client.gui.CreativeTabsScreenPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {
    @Shadow private static CreativeModeTab selectedTab;

    @Shadow private CreativeTabsScreenPage currentPage;

    private static final ResourceLocation CUSTOM_TOOLTIP = ResourceLocation.fromNamespaceAndPath(AntiqueOrnateMod.MOD_ID, "textures/gui/antique_ornate_tooltip.png");

    @Redirect(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void redirectBlitSprite(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height, GuiGraphics graphics, CreativeModeTab tab) {
        if (tab == ModCreativeModeTabs.ANTIQUE_ORNATE_TAB.get()) {
            instance.blitSprite(ResourceLocation.fromNamespaceAndPath(AntiqueOrnateMod.MOD_ID, sprite.getPath()), x, y, width, height);
        } else {
            instance.blitSprite(sprite, x, y, width, height);
        }
    }

    @Shadow protected abstract boolean checkTabHovering(GuiGraphics guiGraphics, CreativeModeTab tab, int mouseX, int mouseY);

    @Unique
    private boolean antique_ornate$isTabVisible(CreativeModeTab tab) {
        return currentPage != null && currentPage.getVisibleTabs().contains(tab);
    }

    @Inject(method = "checkTabHovering", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;II)V"), cancellable = true)
    private void onCheckTabHovering(GuiGraphics guiGraphics, CreativeModeTab tab, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (tab == ModCreativeModeTabs.ANTIQUE_ORNATE_TAB.get()) {
            if (antique_ornate$isTabVisible(tab)) {
                // We just cancel the vanilla tooltip here.
                // The actual drawing is moved to onRender to ensure it's on top.
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        for (CreativeModeTab creativemodetab : CreativeModeTabs.allTabs()) {
            if (antique_ornate$isTabVisible(creativemodetab) && this.checkTabHovering(guiGraphics, creativemodetab, mouseX, mouseY)) {
                if (creativemodetab == ModCreativeModeTabs.ANTIQUE_ORNATE_TAB.get()) {
                    int x = mouseX + 8;
                    int y = mouseY - 16;
                    // We render at the very end of the render method to ensure it's on top of everything.
                    guiGraphics.blit(CUSTOM_TOOLTIP, x, y, 0, 0, 88, 16, 88, 16);
                    return;
                }
            }
        }
    }
}
