package art.hatchette.antique_ornate.client.model;

import art.hatchette.antique_ornate.block.custom.ConnectionVariant;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

/**
 * Geometry loader for the connective texture system.
 *
 * Example model JSON:
 * {
 *   "loader": "antique_ornate:connective",
 *   "variant": "horizontal",
 *   "textures": {
 *     "connective": "antique_ornate:block/divintine_bricks_top",
 *     "simple": "antique_ornate:block/divintine_bricks",
 *     "particle": "antique_ornate:block/divintine_bricks"
 *   }
 * }
 */
public class ConnectiveModelLoader implements IGeometryLoader<ConnectiveGeometry> {

    @Override
    public ConnectiveGeometry read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        ConnectionVariant variant = ConnectionVariant.ALL;
        if (json.has("variant")) {
            String variantStr = json.get("variant").getAsString().toUpperCase();
            try {
                variant = ConnectionVariant.valueOf(variantStr);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Unknown connective variant: " + variantStr);
            }
        }

        return new ConnectiveGeometry(variant);
    }
}
