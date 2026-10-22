package com.misanthropy.primitive_mobs.client.model;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.passive.FilchLizard;
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

public class FilchLizardModel<T extends FilchLizard> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("filchlizard"), "main");

    private final ModelPart root;
    protected final ModelPart Body;
    protected final ModelPart Tail;
    protected final ModelPart Leg1;
    protected final ModelPart Leg2;
    protected final ModelPart Leg3;
    protected final ModelPart Leg4;
    protected final ModelPart FoldFilch1;
    protected final ModelPart FoldFilch2;
    protected final ModelPart Filch1;
    protected final ModelPart Filch2;
    protected final ModelPart Filch3;
    protected final ModelPart Filch4;
    protected final ModelPart Filch5;
    protected final ModelPart Filch6;
    protected final ModelPart FoldHead;
    protected final ModelPart Head;

    public FilchLizardModel(ModelPart root) {
        this.root = root;
        this.Body = root.getChild("Body");
        this.Tail = root.getChild("Tail");
        this.Leg1 = root.getChild("Leg1");
        this.Leg2 = root.getChild("Leg2");
        this.Leg3 = root.getChild("Leg3");
        this.Leg4 = root.getChild("Leg4");
        this.FoldFilch1 = root.getChild("FoldFilch1");
        this.FoldFilch2 = root.getChild("FoldFilch2");
        this.FoldHead = root.getChild("FoldHead");
        this.Head = root.getChild("Head");
        this.Filch1 = Head.getChild("Filch1");
        this.Filch2 = Head.getChild("Filch2");
        this.Filch3 = Head.getChild("Filch3");
        this.Filch4 = Head.getChild("Filch4");
        this.Filch5 = Head.getChild("Filch5");
        this.Filch6 = Head.getChild("Filch6");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 6).addBox(-2.0F, -1.5F, -6.0F, 4, 3, 12),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(32, 9).addBox(-1.0F, -0.5F, 0.0F, 2, 2, 10),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Leg1", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Leg2", CubeListBuilder.create().texOffs(16, 3).addBox(-4.0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Leg3", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Leg4", CubeListBuilder.create().texOffs(16, 3).addBox(-4.0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("FoldFilch1", CubeListBuilder.create().texOffs(0, 22).addBox(1.0F, -1.5F, 0.0F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 21.0F, -6.0F, 0.0F, 0.0349066F, 0.0F));
        root.addOrReplaceChild("FoldFilch2", CubeListBuilder.create().texOffs(14, 22).addBox(-2.0F, -1.5F, 0.0F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 21.0F, -6.0F, 0.0F, -0.0349066F, 0.0F));
        PartDefinition headPart = root.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.5F, -4.0F, 4, 2, 4),
                PartPose.offsetAndRotation(0.0F, 12.0F, -1.0F, 0.0F, 0.0F, 0.0F));
        headPart.addOrReplaceChild("Filch1", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, -2.5F, 2.5F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3665191F, 1.570796F, -0.296706F));
        headPart.addOrReplaceChild("Filch2", CubeListBuilder.create().texOffs(14, 22).addBox(-1.0F, -2.5F, 2.5F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3665191F, -1.570796F, 0.296706F));
        headPart.addOrReplaceChild("Filch3", CubeListBuilder.create().texOffs(0, 22).addBox(-0.5F, -2.5F, 2.0F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        headPart.addOrReplaceChild("Filch4", CubeListBuilder.create().texOffs(14, 22).addBox(-0.5F, -2.5F, 2.0F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        headPart.addOrReplaceChild("Filch5", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -2.5F, 1.5F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        headPart.addOrReplaceChild("Filch6", CubeListBuilder.create().texOffs(14, 22).addBox(0.0F, -2.5F, 1.5F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("FoldHead", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -0.5F, -4.0F, 4, 2, 4),
                PartPose.offsetAndRotation(0.0F, 21.0F, -6.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean carrying = !entity.getMainHandItem().isEmpty();
        Head.visible = carrying;
        FoldHead.visible = !carrying;
        FoldFilch1.visible = !carrying;
        FoldFilch2.visible = !carrying;

        if (carrying) {
            Leg1.setPos(-2.0F, 13.0F, -1.0F);
            Leg1.setRotation(0.0F, 1.047198F, (float) (Math.PI * 2.0 / 9.0));
            Leg2.setPos(2.0F, 13.0F, -1.0F);
            Leg2.setRotation(0.0F, -1.047198F, (float) (-Math.PI * 2.0 / 9.0));
            Leg3.setPos(2.0F, 20.0F, 5.0F);
            Leg3.setRotation(0.0F, 0.0F, 1.396263F);
            Leg4.setPos(-2.0F, 20.0F, 5.0F);
            Leg4.setRotation(0.0F, 0.0F, -1.396263F);
            Body.setPos(0.0F, 16.0F, 2.0F);
            Body.setRotation(-0.9948377F, 0.0F, 0.0F);
            Tail.setPos(0.0F, 20.0F, 6.0F);
            Tail.setRotation(0.6806784F, 0.0F, 0.0F);
            Head.xRot = headPitch * ((float) Math.PI / 180F);
            Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        } else {
            Leg1.setPos(2.0F, 22.0F, -4.0F);
            Leg1.setRotation(0.0F, 0.0F, 0.3839724F);
            Leg2.setPos(-2.0F, 22.0F, -4.0F);
            Leg2.setRotation(0.0F, 0.0F, -0.3839724F);
            Leg3.setPos(2.0F, 22.0F, 5.0F);
            Leg3.setRotation(0.0F, 0.0F, 0.3839724F);
            Leg4.setPos(-2.0F, 22.0F, 5.0F);
            Leg4.setRotation(0.0F, 0.0F, -0.3839724F);
            Body.setPos(0.0F, 21.0F, 0.0F);
            Body.setRotation(0.0F, 0.0F, 0.0F);
            Tail.setPos(0.0F, 21.0F, 6.0F);
            Tail.setRotation(0.0F, 0.0F, 0.0F);
            Leg1.yRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount;
            Leg2.yRot = -Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount;
            FoldHead.xRot = headPitch * ((float) Math.PI / 180F);
            FoldHead.yRot = netHeadYaw * ((float) Math.PI / 180F);
        }

        Leg3.yRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount;
        Leg4.yRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount;
        Tail.yRot = -Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.2F * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
