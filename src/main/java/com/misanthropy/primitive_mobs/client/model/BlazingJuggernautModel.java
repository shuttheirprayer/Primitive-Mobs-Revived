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

public class BlazingJuggernautModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("blazingjuggernaut"), "main");

    private static final int RODS = 12;

    private final ModelPart root;
    private final ModelPart[] rods = new ModelPart[RODS];
    protected final ModelPart Head;
    protected final ModelPart RightArm;
    protected final ModelPart Body;
    protected final ModelPart LeftArm;
    protected final ModelPart RightShoulder;
    protected final ModelPart LeftShoulder;

    public BlazingJuggernautModel(ModelPart root) {
        this.root = root;
        this.Head = root.getChild("Head");
        this.RightArm = root.getChild("RightArm");
        this.Body = root.getChild("Body");
        this.LeftArm = root.getChild("LeftArm");
        this.RightShoulder = root.getChild("RightShoulder");
        this.LeftShoulder = root.getChild("LeftShoulder");
        for (int i = 0; i < RODS; i++) {
            rods[i] = root.getChild("rod" + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(8, 17).addBox(-3.0F, -5.0F, -2.5F, 6, 5, 5),
                PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 27).addBox(5.0F, -4.5F, -2.5F, 3, 9, 4),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -0.0698132F, -0.0349066F));
        root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -4.5F, -4.0F, 12, 9, 8),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -4.5F, -2.5F, 3, 9, 4),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.0698132F, 0.0349066F));
        root.addOrReplaceChild("RightShoulder", CubeListBuilder.create().texOffs(30, 17).addBox(3.5F, -7.5F, -4.5F, 5, 5, 7),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -0.1396263F, 0.0F));
        root.addOrReplaceChild("LeftShoulder", CubeListBuilder.create().texOffs(30, 17).addBox(-8.5F, -7.5F, -4.5F, 5, 5, 7),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.1396263F, 0.0F));
        for (int i = 0; i < RODS; i++) {
            root.addOrReplaceChild("rod" + i,
                    CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F), PartPose.ZERO);
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float angle = ageInTicks * (float) Math.PI * -0.1F;
        for (int i = 0; i < 4; i++) {
            rods[i].y = 6.0F + Mth.cos((i * 2 + ageInTicks) * 0.25F);
            rods[i].x = Mth.cos(angle) * 9.0F;
            rods[i].z = Mth.sin(angle) * 9.0F;
            angle++;
        }
        angle = ((float) Math.PI / 4F) + ageInTicks * (float) Math.PI * 0.03F;
        for (int i = 4; i < 8; i++) {
            rods[i].y = 5.0F + Mth.cos((i * 2 + ageInTicks) * 0.25F);
            rods[i].x = Mth.cos(angle) * 11.0F;
            rods[i].z = Mth.sin(angle) * 11.0F;
            angle++;
        }
        angle = 0.47123894F + ageInTicks * (float) Math.PI * -0.05F;
        for (int i = 8; i < 12; i++) {
            rods[i].y = 4.0F + Mth.cos(i * 1.5F + ageInTicks) * 0.5F;
            rods[i].x = Mth.cos(angle) * 13.0F;
            rods[i].z = Mth.sin(angle) * 13.0F;
            angle++;
        }
        Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        Head.xRot = headPitch * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
