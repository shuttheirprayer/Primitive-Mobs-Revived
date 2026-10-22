package com.misanthropy.primitive_mobs.client.model;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.FlameSpewer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class FlameSpewerModel<T extends FlameSpewer> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(PrimitiveMobs.id("flame_spewer"), "main");
    private static final int TENTACLES = 8;
    private static final float TENTACLE_REST = 0.4537856F;

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart mouth;
    private final ModelPart[] tentacles = new ModelPart[TENTACLES];
    private boolean tentaclesVisible = true;

    public FlameSpewerModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.mouth = root.getChild("mouth");
        for (int i = 0; i < TENTACLES; i++) {
            tentacles[i] = root.getChild("tentacle" + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-7.0F, -16.0F, -7.0F, 14.0F, 16.0F, 14.0F), PartPose.offset(0.0F, 21.0F, 0.0F));
        root.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(43, 0)
                        .addBox(-2.0F, -5.0F, -10.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 21.0F, 0.0F, 0.1570796F, 0.0F, 0.0F));
        for (int i = 0; i < TENTACLES; i++) {
            root.addOrReplaceChild("tentacle" + i, CubeListBuilder.create().texOffs(0, 30)
                            .addBox(-2.0F, 0.0F, -26.0F, 4.0F, 4.0F, 24.0F),
                    PartPose.offsetAndRotation(0.0F, 18.0F, 0.0F, TENTACLE_REST,
                            (float) Math.toRadians(22.5D + 45.0D * i), 0.0F));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    public void setTentaclesVisible(boolean visible) {
        tentaclesVisible = visible;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        body.yRot = netHeadYaw * ((float) Math.PI / 180F);
        mouth.yRot = body.yRot;

        float swell = 1.0F + entity.getAttackSignal();
        body.xScale = swell;
        body.yScale = swell;
        body.zScale = swell;

        float pulse = Mth.sin(entity.tickCount * 0.5F) * 0.03F + 1.0F;
        mouth.xScale = pulse;
        mouth.yScale = pulse;
        mouth.zScale = pulse;

        float sway = entity.isAlarmed() ? 10.0F : 4.0F;
        float speed = entity.isAlarmed() ? 0.75F : 0.15F;
        for (int i = 0; i < TENTACLES; i++) {
            tentacles[i].visible = tentaclesVisible;
            tentacles[i].xRot = TENTACLE_REST
                    + Mth.sin(entity.tickCount * speed + i) * sway * ((float) Math.PI / 180F);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
