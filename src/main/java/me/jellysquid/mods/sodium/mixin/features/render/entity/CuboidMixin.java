package me.jellysquid.mods.sodium.mixin.features.render.entity;

import me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor;
import me.jellysquid.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(ModelPart.Cuboid.class)
public class CuboidMixin implements ModelCuboidAccessor {

    @Mutable
    @Shadow
    @Final
    private float minX;

    @Unique
    private ModelCuboid sodium$cuboid;

    // Inject at the start of the function, so we don't capture modified locals
    @Redirect(method = "<init>", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/model/ModelPart$Cuboid;minX:F", ordinal = 0))
    private void onInit(ModelPart.Cuboid instance, float value, int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> renderDirections) {
        this.sodium$cuboid = new ModelCuboid(u, v, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, textureWidth, textureHeight, renderDirections);

        this.minX = value;
    }

    @Override
    public ModelCuboid sodium$copy() {
        return this.sodium$cuboid;
    }
}
