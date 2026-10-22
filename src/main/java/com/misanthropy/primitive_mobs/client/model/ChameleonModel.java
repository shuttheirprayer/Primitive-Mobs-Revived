package com.misanthropy.primitive_mobs.client.model;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.passive.Chameleon;
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

public class ChameleonModel<T extends Chameleon> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("chameleon"), "main");

    private final ModelPart root;
    private int tint = 0xFFFFFF;
    protected final ModelPart body1;
    protected final ModelPart body2;
    protected final ModelPart tail;
    protected final ModelPart head;
    protected final ModelPart head2;
    protected final ModelPart eyeleft;
    protected final ModelPart eyeright;
    protected final ModelPart legfrontleft1;
    protected final ModelPart legfrontright1;
    protected final ModelPart legfrontleft2;
    protected final ModelPart legfrontright2;
    protected final ModelPart legbackleft1;
    protected final ModelPart legbackright1;
    protected final ModelPart legbackleft2;
    protected final ModelPart legbackright2;
    protected final ModelPart tongue1;
    protected final ModelPart tongue2;

    public ChameleonModel(ModelPart root) {
        this.root = root;
        this.body1 = root.getChild("body1");
        this.body2 = root.getChild("body2");
        this.tail = root.getChild("tail");
        this.head = root.getChild("head");
        this.head2 = root.getChild("head2");
        this.eyeleft = root.getChild("eyeleft");
        this.eyeright = root.getChild("eyeright");
        this.legfrontleft1 = root.getChild("legfrontleft1");
        this.legfrontright1 = root.getChild("legfrontright1");
        this.legfrontleft2 = root.getChild("legfrontleft2");
        this.legfrontright2 = root.getChild("legfrontright2");
        this.legbackleft1 = root.getChild("legbackleft1");
        this.legbackright1 = root.getChild("legbackright1");
        this.legbackleft2 = root.getChild("legbackleft2");
        this.legbackright2 = root.getChild("legbackright2");
        this.tongue1 = root.getChild("tongue1");
        this.tongue2 = root.getChild("tongue2");
        this.tongue1.visible = false;
        this.tongue2.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, 0.0F, 4, 4, 6),
                PartPose.offsetAndRotation(0.0F, 20.0F, -4.0F, 0.1487144F, 0.0F, 0.0F));
        root.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 11).addBox(-1.5F, -2.0F, 5.5F, 3, 3, 2),
                PartPose.offsetAndRotation(0.0F, 20.0F, -4.0F, -0.0743572F, 0.0F, 0.0F));
        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, -1.0F, -0.2F, 2, 4, 4),
                PartPose.offsetAndRotation(0.0F, 20.0F, 3.0F, -0.4461433F, 0.0F, -0.5948578F));
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(25, 0).addBox(-1.5F, -1.5F, -3.5F, 3, 3, 4),
                PartPose.offsetAndRotation(0.0F, 21.0F, -4.0F, 0.2974289F, 0.0F, 0.0F));
        root.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(25, 8).addBox(-1.0F, -2.0F, -3.0F, 2, 4, 2),
                PartPose.offsetAndRotation(0.0F, 21.0F, -4.0F, -0.7063936F, 0.0F, 0.0F));
        root.addOrReplaceChild("eyeleft", CubeListBuilder.create().texOffs(34, 8).addBox(1.3F, -0.5F, -2.0F, 1, 1, 1),
                PartPose.offsetAndRotation(0.0F, 21.0F, -4.0F, 0.1115358F, 0.2230717F, -0.0743572F));
        root.addOrReplaceChild("eyeright", CubeListBuilder.create().texOffs(34, 11).addBox(-2.3F, -0.5F, -2.0F, 1, 1, 1),
                PartPose.offsetAndRotation(0.0F, 21.0F, -4.0F, 0.1115358F, -0.2230705F, 0.074351F));
        root.addOrReplaceChild("legfrontleft1", CubeListBuilder.create().texOffs(45, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1),
                PartPose.offsetAndRotation(2.0F, 21.0F, -3.5F, 1.115358F, 0.2974367F, 0.1115513F));
        root.addOrReplaceChild("legfrontright1", CubeListBuilder.create().texOffs(50, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1),
                PartPose.offsetAndRotation(-2.0F, 21.0F, -3.5F, 1.115358F, -0.297439F, -0.111544F));
        root.addOrReplaceChild("legfrontleft2", CubeListBuilder.create().texOffs(45, 4).addBox(-0.5F, -0.5F, 2.0F, 1, 3, 1),
                PartPose.offsetAndRotation(2.0F, 21.0F, -3.5F, -0.4635966F, 0.297439F, 0.111544F));
        root.addOrReplaceChild("legfrontright2", CubeListBuilder.create().texOffs(50, 4).addBox(-0.5F, -0.5F, 2.0F, 1, 3, 1),
                PartPose.offsetAndRotation(-2.0F, 21.0F, -3.5F, -0.4635966F, -0.297439F, -0.111544F));
        root.addOrReplaceChild("legbackleft1", CubeListBuilder.create().texOffs(45, 9).addBox(-0.7F, 0.0F, -0.5F, 1, 2, 1),
                PartPose.offsetAndRotation(1.0F, 21.0F, 3.0F, -1.152532F, -0.8179311F, -0.1487144F));
        root.addOrReplaceChild("legbackright1", CubeListBuilder.create().texOffs(50, 9).addBox(-0.3F, 0.0F, -0.5F, 1, 2, 1),
                PartPose.offsetAndRotation(-1.0F, 21.0F, 3.0F, -1.152528F, 0.8179334F, 0.1487195F));
        root.addOrReplaceChild("legbackleft2", CubeListBuilder.create().texOffs(45, 13).addBox(-0.7F, -0.5F, -3.0F, 1, 3, 1),
                PartPose.offsetAndRotation(1.0F, 21.0F, 3.0F, 0.426418F, -0.8179311F, -0.1487195F));
        root.addOrReplaceChild("legbackright2", CubeListBuilder.create().texOffs(50, 13).addBox(-0.3F, -0.5F, -3.0F, 1, 3, 1),
                PartPose.offsetAndRotation(-1.0F, 21.0F, 3.0F, 0.426418F, 0.8179311F, 0.1487195F));
        root.addOrReplaceChild("tongue1", CubeListBuilder.create().texOffs(25, 15).addBox(-0.5F, 0.5F, -7.4F, 1, 0, 4),
                PartPose.offsetAndRotation(0.0F, 21.0F, -4.0F, 0.2974216F, 0.0F, 0.0F));
        root.addOrReplaceChild("tongue2", CubeListBuilder.create().texOffs(25, 15).addBox(-0.5F, 0.0F, -8.4F, 1, 1, 1),
                PartPose.offsetAndRotation(0.0F, 21.0F, -4.0F, 0.2974216F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        tint = entity.getDisplayColor();
        head.xRot = headPitch / (180.0F / (float) Math.PI) + 0.2974289F;
        head.yRot = netHeadYaw / (180.0F / (float) Math.PI);
        head2.xRot = headPitch / (180.0F / (float) Math.PI) - 0.7063936F;
        head2.yRot = netHeadYaw / (180.0F / (float) Math.PI);
        eyeleft.xRot = headPitch / (180.0F / (float) Math.PI) + 0.1396263F;
        eyeleft.yRot = netHeadYaw / (180.0F / (float) Math.PI);
        eyeright.xRot = headPitch / (180.0F / (float) Math.PI) + 0.1396263F;
        eyeright.yRot = netHeadYaw / (180.0F / (float) Math.PI);
        tongue1.xRot = headPitch / (180.0F / (float) Math.PI) + 0.2974216F;
        tongue1.yRot = netHeadYaw / (180.0F / (float) Math.PI);
        tongue2.xRot = headPitch / (180.0F / (float) Math.PI) + 0.2974216F;
        tongue2.yRot = netHeadYaw / (180.0F / (float) Math.PI);
        legfrontleft1.xRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount + 1.115358F;
        legfrontleft2.xRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount - 0.4635966F;
        legfrontright1.xRot = -Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount + 1.115358F;
        legfrontright2.xRot = -Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount - 0.4635966F;
        legbackleft1.xRot = -Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount - 1.152532F;
        legbackleft2.xRot = -Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount + 0.426418F;
        legbackright1.xRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount - 1.152532F;
        legbackright2.xRot = Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.6F * limbSwingAmount + 0.426418F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay,
                red * ((tint >> 16) & 0xFF) / 255.0F,
                green * ((tint >> 8) & 0xFF) / 255.0F,
                blue * (tint & 0xFF) / 255.0F, alpha);
    }
}
