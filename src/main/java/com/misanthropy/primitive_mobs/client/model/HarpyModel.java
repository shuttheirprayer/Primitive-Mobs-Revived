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

public class HarpyModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("harpy"), "main");

    private final ModelPart root;
    protected final ModelPart body;
    protected final ModelPart legLeftOverlay;
    protected final ModelPart legRightOverlay;
    protected final ModelPart head;
    protected final ModelPart wingLeft;
    protected final ModelPart wingRight;
    protected final ModelPart legRight;
    protected final ModelPart legLeft;
    protected final ModelPart tail;

    public HarpyModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.legLeftOverlay = root.getChild("legLeftOverlay");
        this.legRightOverlay = root.getChild("legRightOverlay");
        this.head = root.getChild("head");
        this.wingLeft = root.getChild("wingLeft");
        this.wingRight = root.getChild("wingRight");
        this.legRight = root.getChild("legRight");
        this.legLeft = root.getChild("legLeft");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -6.0F, -3.0F, 10, 14, 8),
                PartPose.offsetAndRotation(-1.0F, 10.0F, -2.0F, 0.1745329F, 0.0F, 0.0F));
        root.addOrReplaceChild("legLeftOverlay", CubeListBuilder.create().texOffs(20, 38).addBox(-2.5F, -2.0F, -2.5F, 5, 6, 5),
                PartPose.offsetAndRotation(3.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("legRightOverlay", CubeListBuilder.create().texOffs(0, 38).addBox(-2.5F, -2.0F, -2.5F, 5, 6, 5),
                PartPose.offsetAndRotation(-3.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8),
                PartPose.offsetAndRotation(0.0F, 7.0F, -4.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("wingLeft", CubeListBuilder.create().texOffs(44, 38).addBox(0.0F, 0.0F, -2.0F, 1, 14, 8),
                PartPose.offsetAndRotation(5.0F, 5.0F, -4.0F, 0.2443461F, 0.0F, 0.0F));
        root.addOrReplaceChild("wingRight", CubeListBuilder.create().texOffs(44, 16).addBox(-1.0F, 0.0F, -2.0F, 1, 14, 8),
                PartPose.offsetAndRotation(-5.0F, 5.0F, -4.0F, 0.2443461F, 0.0F, 0.0F));
        root.addOrReplaceChild("legRight", CubeListBuilder.create().texOffs(0, 49).addBox(-2.0F, 1.0F, -2.0F, 4, 5, 4),
                PartPose.offsetAndRotation(-3.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("legLeft", CubeListBuilder.create().texOffs(16, 49).addBox(-2.0F, 1.0F, -2.0F, 4, 5, 4),
                PartPose.offsetAndRotation(3.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.0F, 0.0F, 8, 10, 1),
                PartPose.offsetAndRotation(0.0F, 17.0F, 3.0F, 1.047198F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.xRot = headPitch * ((float) Math.PI / 180F);
        head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        head.zRot = 0.0F;
        legLeft.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        legRight.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        float flap = (Mth.sin(ageInTicks * 0.3F) + 1.0F) * 0.35F;
        wingLeft.zRot = -0.0873F - flap;
        wingRight.zRot = 0.0873F + flap;
        legLeftOverlay.xRot = legLeft.xRot;
        legLeftOverlay.zRot = legLeft.zRot;
        legRightOverlay.xRot = legRight.xRot;
        legRightOverlay.zRot = legRight.zRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
