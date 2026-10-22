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

public class DodoModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("dodo"), "main");

    private final ModelPart root;
    protected final ModelPart Body;
    protected final ModelPart Neck;
    protected final ModelPart Beak;
    protected final ModelPart Wing1;
    protected final ModelPart Wing2;
    protected final ModelPart Leg1;
    protected final ModelPart Leg2;
    protected final ModelPart Feet1;
    protected final ModelPart Feet2;
    protected final ModelPart Tail;

    public DodoModel(ModelPart root) {
        this.root = root;
        this.Body = root.getChild("Body");
        this.Neck = root.getChild("Neck");
        this.Beak = root.getChild("Beak");
        this.Wing1 = root.getChild("Wing1");
        this.Wing2 = root.getChild("Wing2");
        this.Leg1 = root.getChild("Leg1");
        this.Leg2 = root.getChild("Leg2");
        this.Feet1 = root.getChild("Feet1");
        this.Feet2 = root.getChild("Feet2");
        this.Tail = root.getChild("Tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 12).addBox(-4.0F, -4.0F, -6.0F, 8, 8, 12),
                PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, -0.1919862F, 0.0F, 0.0F));
        root.addOrReplaceChild("Neck", CubeListBuilder.create().texOffs(29, 0).addBox(-2.0F, -10.0F, -3.0F, 4, 10, 4),
                PartPose.offsetAndRotation(0.0F, 16.0F, -6.0F, -0.2443461F, 0.0F, 0.0F));
        root.addOrReplaceChild("Beak", CubeListBuilder.create().texOffs(14, 0).addBox(-1.0F, -10.0F, -3.0F, 2, 3, 5),
                PartPose.offsetAndRotation(0.0F, 16.0F, -6.0F, 0.2792527F, 0.0F, 0.0F));
        root.addOrReplaceChild("Wing1", CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, 0.0F, 0.0F, 1, 5, 5),
                PartPose.offsetAndRotation(4.0F, 12.0F, -3.0F, -0.1919862F, 0.0F, 0.0F));
        root.addOrReplaceChild("Wing2", CubeListBuilder.create().texOffs(28, 14).addBox(-1.0F, 0.0F, 0.0F, 1, 5, 5),
                PartPose.offsetAndRotation(-4.0F, 12.0F, -3.0F, -0.1919862F, 0.0F, 0.0F));
        root.addOrReplaceChild("Leg1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
                PartPose.offsetAndRotation(2.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Leg2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
                PartPose.offsetAndRotation(-2.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Feet1", CubeListBuilder.create().texOffs(1, 8).addBox(-1.5F, 3.0F, -2.0F, 3, 1, 3),
                PartPose.offsetAndRotation(2.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Feet2", CubeListBuilder.create().texOffs(1, 8).addBox(-1.5F, 3.0F, -2.0F, 3, 1, 3),
                PartPose.offsetAndRotation(-2.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(4, 0).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3),
                PartPose.offsetAndRotation(0.0F, 13.0F, 5.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        Neck.xRot = headPitch / 70.028175F - 0.2443461F;
        Neck.yRot = netHeadYaw / 70.028175F;
        Beak.xRot = headPitch / 70.028175F + 0.2792527F;
        Beak.yRot = netHeadYaw / 70.028175F;
        Leg2.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        Leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        Feet1.xRot = Leg1.xRot;
        Feet2.xRot = Leg2.xRot;
        Wing2.zRot = ageInTicks;
        Wing1.zRot = -ageInTicks;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
