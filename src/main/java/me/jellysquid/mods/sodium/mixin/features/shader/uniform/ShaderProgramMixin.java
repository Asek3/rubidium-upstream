package me.jellysquid.mods.sodium.mixin.features.shader.uniform;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * On the NVIDIA drivers (and maybe some others), the OpenGL submission thread requires expensive state synchronization
 * to happen when glGetUniformLocation and glGetInteger are called. In our case, this is rather unnecessary, since
 * these uniform locations can be trivially cached.
 */
@Mixin(ShaderProgram.class)
public class ShaderProgramMixin {
    @Shadow
    @Final
    private List<String> samplerNames;

    @Shadow
    @Final
    private int glRef;

    @Unique
    private Object2IntMap<String> uniformCache;

    @Redirect(method = "bind", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/GlUniform;getUniformLocation(ILjava/lang/CharSequence;)I"))
    private int redirectGetUniformLocation(int program, CharSequence name) {
        if(this.uniformCache == null) {
            this.uniformCache = new Object2IntOpenHashMap<>();
            this.uniformCache.defaultReturnValue(-1);

            for (var samplerName : this.samplerNames) {
                var location = GlUniform.getUniformLocation(this.glRef, samplerName);

                if(location != -1)
                    this.uniformCache.put(samplerName, location);
            }
        }
        var location = this.uniformCache.getInt(name);

        if (location == -1) {
            throw new IllegalStateException("Failed to find uniform '%s' during shader bind".formatted(name));
        }

        return location;
    }
}