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
import net.minecraft.world.entity.Entity;

public class MimicModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("mimic"), "main");

    private final ModelPart root;
    protected final ModelPart Top;
    protected final ModelPart Lock;
    protected final ModelPart Bottom;
    protected final ModelPart Teeth;

    public MimicModel(ModelPart root) {
        this.root = root;
        this.Top = root.getChild("Top");
        this.Lock = root.getChild("Lock");
        this.Bottom = root.getChild("Bottom");
        this.Teeth = root.getChild("Teeth");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Top", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -5.0F, -14.0F, 14, 5, 14),
                PartPose.offsetAndRotation(0.0F, 15.0F, 7.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Lock", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1),
                PartPose.offsetAndRotation(0.0F, 15.0F, 7.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Bottom", CubeListBuilder.create().texOffs(0, 19).addBox(-7.0F, 0.0F, -7.0F, 14, 10, 14),
                PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("Teeth", CubeListBuilder.create().texOffs(0, 43).addBox(-6.5F, -1.0F, -6.5F, 13, 1, 13),
                PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        Top.xRot = ageInTicks;
        Lock.xRot = ageInTicks;
    }

    public void setTeethVisible(boolean visible) {
        Teeth.visible = visible;
    }

    public void setChestVisible(boolean visible) {
        Top.visible = visible;
        Lock.visible = visible;
        Bottom.visible = visible;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
