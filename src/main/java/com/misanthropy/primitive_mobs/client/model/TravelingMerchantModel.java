package com.misanthropy.primitive_mobs.client.model;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
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
import net.minecraft.world.entity.Entity;

public class TravelingMerchantModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("travelingmerchant"), "main");

    private final ModelPart root;
    protected final ModelPart Head;
    protected final ModelPart Body;
    protected final ModelPart LegLeft;
    protected final ModelPart LegRight;
    protected final ModelPart ArmLeftShoulder;
    protected final ModelPart ArmLeftHand;
    protected final ModelPart Burden1;
    protected final ModelPart Burden2;
    protected final ModelPart ArmRightShoulder;
    protected final ModelPart ArmRightHand;
    protected final ModelPart Nose;

    public TravelingMerchantModel(ModelPart root) {
        this.root = root;
        this.Head = root.getChild("Head");
        this.Body = root.getChild("Body");
        this.LegLeft = root.getChild("LegLeft");
        this.LegRight = root.getChild("LegRight");
        this.ArmLeftShoulder = root.getChild("ArmLeftShoulder");
        this.ArmLeftHand = root.getChild("ArmLeftHand");
        this.Burden1 = root.getChild("Burden1");
        this.Burden2 = root.getChild("Burden2");
        this.ArmRightShoulder = root.getChild("ArmRightShoulder");
        this.ArmRightHand = root.getChild("ArmRightHand");
        this.Nose = root.getChild("Nose");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.5F, -5.1F, 8, 10, 8),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 39).addBox(-5.0F, 0.0F, -4.0F, 8, 17, 6),
                PartPose.offsetAndRotation(1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("LegLeft", CubeListBuilder.create().texOffs(0, 22).addBox(-2.1F, 0.0F, -2.0F, 4, 12, 4),
                PartPose.offsetAndRotation(2.0F, 12.0F, -1.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("LegRight", CubeListBuilder.create().texOffs(0, 22).addBox(-1.9F, 0.0F, -2.0F, 4, 12, 4),
                PartPose.offsetAndRotation(-2.0F, 12.0F, -1.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("ArmLeftShoulder", CubeListBuilder.create().texOffs(40, 38).addBox(0.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("ArmLeftHand", CubeListBuilder.create().texOffs(44, 22).addBox(0.0F, -6.0F, -6.0F, 4, 8, 4),
                PartPose.offsetAndRotation(2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Burden1", CubeListBuilder.create().texOffs(66, 26).addBox(-8.0F, 0.0F, 0.0F, 16, 6, 6),
                PartPose.offsetAndRotation(0.0F, -8.0F, 3.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Burden2", CubeListBuilder.create().texOffs(66, 38).addBox(-7.0F, 0.0F, 0.0F, 14, 18, 8),
                PartPose.offsetAndRotation(0.0F, -2.0F, 3.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("ArmRightShoulder", CubeListBuilder.create().texOffs(40, 38).addBox(-4.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(-2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("ArmRightHand", CubeListBuilder.create().texOffs(44, 22).addBox(-4.0F, -6.0F, -6.0F, 4, 8, 4),
                PartPose.offsetAndRotation(-2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -2.5F, -7.0F, 2, 4, 2),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        Head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        Head.xRot = headPitch * (float) (Math.PI / 180.0);
        Nose.xRot = Head.xRot;
        Nose.yRot = Head.yRot;
        LegRight.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        LegLeft.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        LegRight.yRot = 0.0F;
        LegLeft.yRot = 0.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
