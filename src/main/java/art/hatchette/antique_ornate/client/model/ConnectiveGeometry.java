package art.hatchette.antique_ornate.client.model;

import art.hatchette.antique_ornate.block.custom.ConnectionVariant;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

/**
 * Unbaked geometry for the connective texture system.
 * Resolves texture references from the model JSON and passes them to the baked model.
 *
 * Texture keys:
 *   #connective - The sprite sheet (32x64 for quarter-chunk, 32x32 for segment)
 *   #simple     - Simple texture for non-CT faces (optional)
 *   #cap        - Cap texture for PILLAR/BEAM (optional)
 *   #particle   - Particle texture
 */
public class ConnectiveGeometry implements IUnbakedGeometry<ConnectiveGeometry> {

    private final ConnectionVariant variant;

    public ConnectiveGeometry(ConnectionVariant variant) {
        this.variant = variant;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                           Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState, ItemOverrides overrides) {

        TextureAtlasSprite connectiveSprite = null;
        if (context.hasMaterial("connective")) {
            connectiveSprite = spriteGetter.apply(context.getMaterial("connective"));
        }

        TextureAtlasSprite simpleSprite = null;
        if (context.hasMaterial("simple")) {
            simpleSprite = spriteGetter.apply(context.getMaterial("simple"));
        }

        TextureAtlasSprite capSprite = null;
        if (context.hasMaterial("cap")) {
            capSprite = spriteGetter.apply(context.getMaterial("cap"));
        }

        TextureAtlasSprite particleSprite = null;
        if (context.hasMaterial("particle")) {
            particleSprite = spriteGetter.apply(context.getMaterial("particle"));
        }

        if (particleSprite == null) {
            particleSprite = connectiveSprite;
        }

        ItemTransforms transforms = context.getTransforms();

        return new ConnectiveBakedModel(variant, connectiveSprite, simpleSprite, capSprite,
                particleSprite, modelState, transforms);
    }
}