package com.misanthropy.primitive_mobs.client.model;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.monster.Trollager;
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

public class TrollagerModel<T extends Trollager> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("trollager"), "main");

    private static final float ARM_Y = 4.0F;
    private static final float BODY_Y = 6.5F;
    private static final float BODY_PITCH = 0.9599311F;
    private static final float HEAD_Y = 5.0F;
    private static final float HEAD_Z = -10.0F;

    private final ModelPart root;
    protected final ModelPart Head;
    protected final ModelPart Body;
    protected final ModelPart LegLeft;
    protected final ModelPart LegRight;
    protected final ModelPart ArmLeft;
    protected final ModelPart Nose;
    protected final ModelPart ArmRight;
    protected final ModelPart BlockHolder;
    protected final ModelPart Mouth;

    public TrollagerModel(ModelPart root) {
        this.root = root;
        this.Head = root.getChild("Head");
        this.Nose = Head.getChild("Nose");
        this.Mouth = Head.getChild("Mouth");
        this.Body = root.getChild("Body");
        this.LegLeft = root.getChild("LegLeft");
        this.LegRight = root.getChild("LegRight");
        this.ArmLeft = root.getChild("ArmLeft");
        this.ArmRight = root.getChild("ArmRight");
        this.BlockHolder = root.getChild("BlockHolder");
        this.BlockHolder.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition headPart = root.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.5F, -5.1F, 8, 10, 8),
                PartPose.offsetAndRotation(0.0F, 5.0F, -10.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 20).addBox(-8.0F, -10.5F, -6.0F, 16, 21, 12),
                PartPose.offsetAndRotation(0.0F, 6.5F, 0.0F, 0.9599311F, 0.0F, 0.0F));
        root.addOrReplaceChild("LegLeft", CubeListBuilder.create().texOffs(69, 3).addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6),
                PartPose.offsetAndRotation(4.0F, 15.0F, 5.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("LegRight", CubeListBuilder.create().texOffs(69, 3).addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6),
                PartPose.offsetAndRotation(-4.0F, 15.0F, 5.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("ArmLeft", CubeListBuilder.create().texOffs(57, 24).addBox(-6.0F, -3.0F, -3.0F, 6, 23, 6),
                PartPose.offsetAndRotation(8.0F, 4.0F, -6.0F, 0.0F, 3.141593F, 0.0F));
        headPart.addOrReplaceChild("Nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -2.5F, -7.0F, 2, 5, 2),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1570796F, 0.0F, 0.0F));
        root.addOrReplaceChild("ArmRight", CubeListBuilder.create().texOffs(57, 24).addBox(-6.0F, -3.0F, -3.0F, 6, 23, 6),
                PartPose.offsetAndRotation(-8.0F, 4.0F, -6.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("BlockHolder", CubeListBuilder.create().texOffs(57, 24).addBox(-6.0F, -3.0F, -3.0F, 6, 23, 6),
                PartPose.offsetAndRotation(-8.0F, 4.0F, -6.0F, 0.0F, 0.0F, 0.0F));
        headPart.addOrReplaceChild("Mouth", CubeListBuilder.create().texOffs(32, 5).addBox(-4.5F, -2.0F, -5.5F, 9, 4, 9),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        Head.xRot = headPitch * ((float) Math.PI / 180F);
        Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        float stride = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.8F * limbSwingAmount;
        LegRight.xRot = stride;
        LegLeft.xRot = -stride;

        resetPose();
        float progress = entity.getAnimationProgress();
        switch (entity.getAnimationState()) {
            case 0 -> {
                ArmLeft.xRot = stride;
                ArmRight.xRot = stride;
            }
            case 1 -> raiseArms(progress * 4.0F, 0.0F, 3.5F, 0.1F, -0.22F, true);
            case 2 -> {
                raiseArms(progress * 8.0F, 3.5F, 0.0F, -0.22F, 0.1F, true);
                Mouth.xRot = 0.15F;
                Mouth.zRot = 0.0F;
            }
            case 3 -> raiseArms(progress * 3.0F, 0.0F, 3.5F, 0.1F, -0.22F, false);
            case 4 -> {
                float t = progress * 12.0F;
                ArmLeft.xRot = lerp(t, 3.5F, 0.5F);
                ArmRight.xRot = lerp(t, -3.5F, -0.5F);
                float lift = lerp(t, 0.0F, 0.2F) * 16.0F;
                ArmLeft.y = ARM_Y + lift;
                ArmRight.y = ARM_Y + lift;
                Body.xRot = lerp(t, BODY_PITCH, 1.32645F);
                Body.y = BODY_Y + lift;
                Head.y = HEAD_Y + lift;
                Head.z = HEAD_Z + lerp(t, 0.0F, -0.1F) * 16.0F;
                Mouth.xRot = 0.15F;
                Mouth.zRot = 0.0F;
            }
            case 5 -> {
                float t = progress * 10.0F;
                ArmLeft.xRot = lerp(t, 0.5F, 0.0F);
                ArmRight.xRot = lerp(t, -0.5F, 0.0F);
                float lift = lerp(t, 0.2F, 0.0F) * 16.0F;
                ArmLeft.y = ARM_Y + lift;
                ArmRight.y = ARM_Y + lift;
                Body.xRot = lerp(t, 1.32645F, BODY_PITCH);
                Body.y = BODY_Y + lift;
                Head.y = HEAD_Y + lift;
                Head.z = HEAD_Z + lerp(t, -0.1F, 0.0F) * 16.0F;
            }
            case 6 -> {
                float t = progress * 22.0F;
                ArmLeft.xRot = lerp(t, 0.0F, 1.5F);
                ArmRight.xRot = lerp(t, 0.0F, -1.5F);
            }
            case 7 -> {
                float t = progress * 22.0F;
                ArmLeft.xRot = lerp(t, 1.5F, 0.0F);
                ArmRight.xRot = lerp(t, -1.5F, 0.0F);
            }
            default -> {
            }
        }

        int state = entity.getAnimationState();
        if (state != 2 && state != 4 && !entity.isStone()) {
            idleMouth(entity.tickCount * 0.5F);
        }
    }

    private void resetPose() {
        ArmLeft.zRot = 0.0F;
        ArmRight.zRot = 0.0F;
        ArmLeft.y = ARM_Y;
        ArmRight.y = ARM_Y;
        Body.xRot = BODY_PITCH;
        Body.y = BODY_Y;
        Head.y = HEAD_Y;
        Head.z = HEAD_Z;
    }

    private void raiseArms(float t, float fromPitch, float toPitch, float fromRoll, float toRoll, boolean moveHolder) {
        ArmLeft.xRot = lerp(t, fromPitch, toPitch);
        ArmRight.xRot = lerp(t, -fromPitch, -toPitch);
        ArmLeft.zRot = lerp(t, fromRoll, toRoll);
        ArmRight.zRot = lerp(t, -fromRoll, -toRoll);
        if (moveHolder) {
            BlockHolder.xRot = lerp(t, -fromPitch, -toPitch);
        }
    }

    private void idleMouth(float time) {
        Mouth.xRot = Mth.sin(time) * 4.5F * ((float) Math.PI / 180F);
        Mouth.yRot = 0.0F;
        Mouth.zRot = Mth.cos(time) * 2.5F * ((float) Math.PI / 180F);
    }

    private static float lerp(float progress, float from, float to) {
        return (float) Mth.clampedLerp(from, to, progress);
    }

    public ModelPart blockHolder() {
        return BlockHolder;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
