package com.misanthropy.primitive_mobs.client.model;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.passive.GroveSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class GroveSpriteModel<T extends GroveSprite> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("grovesprite"), "main");

    private final ModelPart root;
    protected final ModelPart bipedHead;
    protected final ModelPart bipedBody;
    protected final ModelPart bipedHeadwear;
    protected final ModelPart bipedRightArm;
    protected final ModelPart bipedLeftArm;
    protected final ModelPart bipedRightLeg;
    protected final ModelPart bipedLeftLeg;
    protected final ModelPart stem;

    public GroveSpriteModel(ModelPart root) {
        this.root = root;
        this.bipedHead = root.getChild("bipedHead");
        this.bipedBody = root.getChild("bipedBody");
        this.bipedHeadwear = root.getChild("bipedHeadwear");
        this.bipedRightArm = root.getChild("bipedRightArm");
        this.bipedLeftArm = root.getChild("bipedLeftArm");
        this.bipedRightLeg = root.getChild("bipedRightLeg");
        this.bipedLeftLeg = root.getChild("bipedLeftLeg");
        this.stem = root.getChild("stem");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("bipedHead", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6, 6, 6, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedBody", CubeListBuilder.create().texOffs(24, 12).addBox(-2.5F, 0.0F, -2.0F, 5, 6, 4, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -0.9375F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedHeadwear", CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -6.0F, 2.0F, 6, 6, 2, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedRightArm", CubeListBuilder.create().texOffs(12, 12).addBox(-2.0F, -1.0F, -1.5F, 3, 5, 3, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedLeftArm", CubeListBuilder.create().mirror().texOffs(12, 12).addBox(-1.0F, -1.0F, -1.5F, 3, 5, 3, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedRightLeg", CubeListBuilder.create().texOffs(0, 12).addBox(-1.5F, 0.0F, -1.5F, 3, 5, 3, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.5F, 5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bipedLeftLeg", CubeListBuilder.create().mirror().texOffs(0, 12).addBox(-1.5F, 0.0F, -1.5F, 3, 5, 3, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.5F, 5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(24, 8).addBox(-0.5F, -9.0F, 0.0F, 1, 3, 1, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        bipedHead.yRot = netHeadYaw * ((float) Math.PI / 180F);
        bipedHead.xRot = headPitch * ((float) Math.PI / 180F);
        bipedHeadwear.yRot = bipedHead.yRot;
        bipedHeadwear.xRot = bipedHead.xRot;
        stem.yRot = bipedHead.yRot;
        stem.xRot = bipedHead.xRot;
        float swing = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.8F * limbSwingAmount;
        bipedLeftArm.xRot = swing;
        bipedRightArm.xRot = -swing;
        bipedRightLeg.xRot = swing;
        bipedLeftLeg.xRot = -swing;
        if (entity.isBegging()) {
            bipedRightArm.xRot = -1.3F;
        }
    }

    public ModelPart rightArm() {
        return bipedRightArm;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
