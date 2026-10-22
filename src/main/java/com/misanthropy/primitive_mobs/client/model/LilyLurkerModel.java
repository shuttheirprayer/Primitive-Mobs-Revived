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

public class LilyLurkerModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("lilylurker"), "main");

    private final ModelPart root;
    protected final ModelPart Body1;
    protected final ModelPart Fin1;
    protected final ModelPart Fin2;
    protected final ModelPart Body2;
    protected final ModelPart Fin3;
    protected final ModelPart Fin4;
    protected final ModelPart Root1;
    protected final ModelPart Root2;

    public LilyLurkerModel(ModelPart root) {
        this.root = root;
        this.Body1 = root.getChild("Body1");
        this.Fin1 = root.getChild("Fin1");
        this.Fin2 = root.getChild("Fin2");
        this.Body2 = root.getChild("Body2");
        this.Fin3 = root.getChild("Fin3");
        this.Fin4 = root.getChild("Fin4");
        this.Root1 = root.getChild("Root1");
        this.Root2 = root.getChild("Root2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Body1", CubeListBuilder.create().texOffs(28, 0).addBox(-4.0F, 0.0F, -5.0F, 8, 4, 10),
                PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Fin1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -1.5F, 6, 1, 3),
                PartPose.offsetAndRotation(3.0F, 22.0F, -1.0F, 0.0F, -0.6806784F, 0.0F));
        root.addOrReplaceChild("Fin2", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -1.5F, 6, 1, 3),
                PartPose.offsetAndRotation(-3.0F, 22.0F, -1.0F, 0.0F, -2.460914F, 0.0F));
        root.addOrReplaceChild("Body2", CubeListBuilder.create().texOffs(0, 10).addBox(-3.0F, -1.0F, 0.0F, 6, 3, 2),
                PartPose.offsetAndRotation(0.0F, 22.0F, 5.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Fin3", CubeListBuilder.create().texOffs(0, 4).addBox(0.0F, 0.0F, 0.0F, 2, 1, 5),
                PartPose.offsetAndRotation(0.0F, 22.0F, 7.0F, 0.0F, 0.4712389F, 0.0F));
        root.addOrReplaceChild("Fin4", CubeListBuilder.create().texOffs(0, 4).addBox(-2.0F, 0.0F, 0.0F, 2, 1, 5),
                PartPose.offsetAndRotation(0.0F, 22.0F, 7.0F, 0.0F, -0.4712389F, 0.0F));
        root.addOrReplaceChild("Root1", CubeListBuilder.create().texOffs(18, 0).addBox(-0.5F, -5.0F, -0.5F, 1, 5, 1),
                PartPose.offsetAndRotation(0.0F, 21.0F, -2.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Root2", CubeListBuilder.create().texOffs(18, 0).addBox(-0.5F, -4.0F, -0.5F, 1, 5, 1),
                PartPose.offsetAndRotation(0.0F, 16.5F, 1.0F, 0.3316126F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        Fin1.yRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount - 0.6806784F;
        Fin2.yRot = -Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount - 2.460914F;
        Fin3.xRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount;
        Fin4.xRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
