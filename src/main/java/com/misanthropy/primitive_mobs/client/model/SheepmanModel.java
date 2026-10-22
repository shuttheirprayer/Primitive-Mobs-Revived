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

public class SheepmanModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("sheepman"), "main");

    private final ModelPart root;
    protected final ModelPart Head;
    protected final ModelPart HeadWool;
    protected final ModelPart Body;
    protected final ModelPart LegLeft;
    protected final ModelPart LegRight;
    protected final ModelPart ArmLeft;
    protected final ModelPart ArmRight;
    protected final ModelPart BodyWool;
    protected final ModelPart LegLeftWool;
    protected final ModelPart LegRightWool;
    protected final ModelPart FoldedArms;

    public SheepmanModel(ModelPart root) {
        this.root = root;
        this.Head = root.getChild("Head");
        this.HeadWool = root.getChild("HeadWool");
        this.Body = root.getChild("Body");
        this.LegLeft = root.getChild("LegLeft");
        this.LegRight = root.getChild("LegRight");
        this.ArmLeft = root.getChild("ArmLeft");
        this.ArmRight = root.getChild("ArmRight");
        this.BodyWool = root.getChild("BodyWool");
        this.LegLeftWool = root.getChild("LegLeftWool");
        this.LegRightWool = root.getChild("LegRightWool");
        this.FoldedArms = root.getChild("FoldedArms");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -6.0F, 6, 6, 8),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("HeadWool", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -6.0F, -4.0F, 6, 6, 6),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 42).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("LegLeft", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4),
                PartPose.offsetAndRotation(2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("LegRight", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4),
                PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("ArmLeft", CubeListBuilder.create().texOffs(16, 26).addBox(0.0F, 0.0F, -2.0F, 3, 12, 4),
                PartPose.offsetAndRotation(4.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("ArmRight", CubeListBuilder.create().texOffs(16, 26).addBox(-3.0F, 0.0F, -2.0F, 3, 12, 4),
                PartPose.offsetAndRotation(-4.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("BodyWool", CubeListBuilder.create().texOffs(36, 0).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("LegLeftWool", CubeListBuilder.create().texOffs(30, 26).addBox(2.0F, 0.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("LegRightWool", CubeListBuilder.create().texOffs(30, 26).addBox(-2.0F, 0.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("FoldedArms", CubeListBuilder.create().texOffs(20, 52).addBox(-7.0F, 0.0F, -8.0F, 14, 4, 8),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        Head.xRot = headPitch * ((float) Math.PI / 180F);
        HeadWool.yRot = Head.yRot;
        HeadWool.xRot = Head.xRot;
        float swing = Mth.cos(limbSwing * 0.6662F * 2.0F) * 0.6F * limbSwingAmount;
        LegRight.xRot = swing;
        LegLeft.xRot = -swing;
        LegRightWool.xRot = LegRight.xRot;
        LegLeftWool.xRot = LegLeft.xRot;
        ArmRight.xRot = -swing;
        ArmLeft.xRot = swing;
    }

    private void hideAll() {
        Head.visible = false;
        Body.visible = false;
        LegLeft.visible = false;
        LegRight.visible = false;
        ArmLeft.visible = false;
        ArmRight.visible = false;
        FoldedArms.visible = false;
        HeadWool.visible = false;
        BodyWool.visible = false;
        LegLeftWool.visible = false;
        LegRightWool.visible = false;
    }

    public void showBody(boolean sheared) {
        hideAll();
        Head.visible = true;
        Body.visible = true;
        LegLeft.visible = true;
        LegRight.visible = true;
        ArmLeft.visible = sheared;
        ArmRight.visible = sheared;
    }

    public void showFoldedArms() {
        hideAll();
        FoldedArms.visible = true;
    }

    public void showWool() {
        hideAll();
        HeadWool.visible = true;
        BodyWool.visible = true;
        LegLeftWool.visible = true;
        LegRightWool.visible = true;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
