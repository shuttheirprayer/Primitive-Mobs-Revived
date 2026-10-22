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

public class GoblinModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("goblin"), "main");

    private final ModelPart root;
    protected final ModelPart bipedHead;
    protected final ModelPart bipedBody;
    protected final ModelPart bipedLeftLeg;
    protected final ModelPart bipedRightLeg;
    protected final ModelPart bipedRightArm;
    protected final ModelPart bipedLeftArm;
    protected final ModelPart Nose;
    protected final ModelPart Ear1;
    protected final ModelPart Ear2;
    protected final ModelPart Helmet;
    protected final ModelPart Armor;
    protected final ModelPart Bag;

    public GoblinModel(ModelPart root) {
        this.root = root;
        this.bipedHead = root.getChild("bipedHead");
        this.Nose = bipedHead.getChild("Nose");
        this.Ear1 = bipedHead.getChild("Ear1");
        this.Ear2 = bipedHead.getChild("Ear2");
        this.Helmet = bipedHead.getChild("Helmet");
        this.bipedBody = root.getChild("bipedBody");
        this.bipedLeftLeg = root.getChild("bipedLeftLeg");
        this.bipedRightLeg = root.getChild("bipedRightLeg");
        this.bipedRightArm = root.getChild("bipedRightArm");
        this.bipedLeftArm = root.getChild("bipedLeftArm");
        this.Armor = root.getChild("Armor");
        this.Bag = root.getChild("Bag");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("bipedHead", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.0F, -5.1F, 8, 10, 8),
                PartPose.offsetAndRotation(0.0F, 7.0F, -3.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedBody", CubeListBuilder.create().texOffs(17, 20).addBox(-5.0F, 0.0F, -4.0F, 10, 10, 6),
                PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedLeftLeg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.1F, 0.0F, -2.0F, 4, 8, 4),
                PartPose.offsetAndRotation(2.0F, 16.0F, -1.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedRightLeg", CubeListBuilder.create().texOffs(0, 22).addBox(-1.9F, 0.0F, -2.0F, 4, 8, 4),
                PartPose.offsetAndRotation(-2.0F, 16.0F, -1.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedRightArm", CubeListBuilder.create().texOffs(48, 22).addBox(-4.0F, -1.0F, -2.0F, 4, 10, 4),
                PartPose.offsetAndRotation(-5.0F, 7.0F, -1.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedLeftArm", CubeListBuilder.create().texOffs(48, 22).addBox(0.0F, -1.0F, -2.0F, 4, 10, 4),
                PartPose.offsetAndRotation(5.0F, 7.0F, -1.0F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("Nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -2.0F, -7.0F, 2, 4, 2),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3490659F, 0.0F, 0.0F));
        head.addOrReplaceChild("Ear1", CubeListBuilder.create().texOffs(33, 0).addBox(0.0F, -9.0F, 0.0F, 1, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4537856F, 0.0F, 0.5759587F));
        head.addOrReplaceChild("Ear2", CubeListBuilder.create().texOffs(46, 0).addBox(-1.0F, -9.0F, 0.0F, 1, 3, 5),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4537856F, 0.0F, -0.5759587F));
        head.addOrReplaceChild("Helmet", CubeListBuilder.create().texOffs(0, 36).addBox(-4.5F, -9.5F, -5.5F, 9, 11, 9),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Armor", CubeListBuilder.create().texOffs(37, 36).addBox(-5.5F, -0.5F, -4.5F, 11, 11, 7),
                PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Bag", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -8.0F, 0.0F, 16, 16, 16),
                PartPose.offsetAndRotation(0.0F, 8.0F, 2.0F, 0.1396263F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        bipedHead.yRot = netHeadYaw * ((float) Math.PI / 180F);
        bipedHead.xRot = headPitch * ((float) Math.PI / 180F);
        bipedRightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        bipedLeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        bipedRightArm.zRot = 0.0F;
        bipedLeftArm.zRot = 0.0F;
        bipedRightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        bipedLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        if (attackTime > 0.0F) {
            float swing = 1.0F - attackTime;
            swing = 1.0F - swing * swing * swing;
            bipedRightArm.xRot -= Mth.sin(swing * (float) Math.PI) * 1.2F
                    + Mth.sin(attackTime * (float) Math.PI) * -(bipedHead.xRot - 0.7F) * 0.75F;
            bipedRightArm.zRot = Mth.sin(attackTime * (float) Math.PI) * -0.4F;
        }

        Bag.xRot = Mth.cos(limbSwing * 0.6662F) * 0.1F * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
