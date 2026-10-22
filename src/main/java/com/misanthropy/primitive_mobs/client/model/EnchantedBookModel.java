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

public class EnchantedBookModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(PrimitiveMobs.id("enchantedbook"), "main");

    private final ModelPart root;
    protected final ModelPart Book1;
    protected final ModelPart Book2;
    protected final ModelPart Pages1;
    protected final ModelPart Pages2;
    protected final ModelPart Page1;
    protected final ModelPart Page2;

    public EnchantedBookModel(ModelPart root) {
        this.root = root;
        this.Book1 = root.getChild("Book1");
        this.Book2 = root.getChild("Book2");
        this.Pages1 = root.getChild("Pages1");
        this.Pages2 = root.getChild("Pages2");
        this.Page1 = root.getChild("Page1");
        this.Page2 = root.getChild("Page2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("Book1", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0),
                PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 1.570796F, 0.0F, 0.0F));
        root.addOrReplaceChild("Book2", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0),
                PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 1.570796F, 0.0F, 0.0F));
        root.addOrReplaceChild("Pages1", CubeListBuilder.create().texOffs(0, 10).addBox(-5.0F, -4.0F, -1.1F, 5, 8, 1),
                PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 1.570796F, 0.0F, 0.0F));
        root.addOrReplaceChild("Pages2", CubeListBuilder.create().texOffs(12, 10).addBox(0.0F, -4.0F, -1.1F, 5, 8, 1),
                PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 1.570796F, 0.0F, 0.0F));
        root.addOrReplaceChild("Page1", CubeListBuilder.create().texOffs(24, 10).addBox(0.0F, -4.0F, -1.1F, 5, 8, 0),
                PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 1.570796F, 0.0F, 0.0F));
        root.addOrReplaceChild("Page2", CubeListBuilder.create().texOffs(24, 10).addBox(-5.0F, -4.0F, -1.1F, 5, 8, 0),
                PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 1.570796F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        Book1.zRot = ageInTicks;
        Book2.zRot = -ageInTicks;
        Pages1.zRot = ageInTicks;
        Pages2.zRot = -ageInTicks;
        Page1.zRot = ageInTicks + (float) (Math.PI / 6);
        Page2.zRot = -ageInTicks - (float) (Math.PI / 6);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
