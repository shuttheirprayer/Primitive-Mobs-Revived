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

public class GroveSpriteLeavesModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("grovesprite_leaves"), "main");
    private static final int LEAVES = 3;

    private final ModelPart root;
    private final ModelPart[] leaves = new ModelPart[LEAVES];

    public GroveSpriteLeavesModel(ModelPart root) {
        this.root = root;
        for (int i = 0; i < LEAVES; i++) {
            leaves[i] = root.getChild("leaf" + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        for (int i = 0; i < LEAVES; i++) {
            root.addOrReplaceChild("leaf" + i,
                    CubeListBuilder.create().texOffs(0, 22).addBox(-1.5F, -9.0F, -5.25F, 3.0F, 1.0F, 5.0F),
                    PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 2.0F * (i + 1.0F), 0.0F));
        }
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
