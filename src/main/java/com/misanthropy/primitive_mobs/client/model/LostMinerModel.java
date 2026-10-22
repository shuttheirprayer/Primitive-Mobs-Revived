package com.misanthropy.primitive_mobs.client.model;

import com.misanthropy.primitive_mobs.core.PrimitiveMobs;
import com.misanthropy.primitive_mobs.entity.passive.LostMiner;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class LostMinerModel<T extends LostMiner> extends VillagerModel<T> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(PrimitiveMobs.id("lost_miner"), "main");
    private static final float PANIC_ARM_ROTATION = 1.396263F;

    private final ModelPart arms;
    private final ModelPart leftShoulder;
    private final ModelPart leftHand;
    private final ModelPart rightShoulder;
    private final ModelPart rightHand;

    public LostMinerModel(ModelPart root) {
        super(root);
        this.arms = root.getChild("arms");
        this.leftShoulder = root.getChild("left_shoulder");
        this.leftHand = root.getChild("left_hand");
        this.rightShoulder = root.getChild("right_shoulder");
        this.rightHand = root.getChild("right_hand");
        ModelPart hat = root.getChild("head").getChild("hat");
        hat.visible = false;
        hat.getChild("hat_rim").visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = VillagerModel.createBodyModel();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");

        head.addOrReplaceChild("miner_hat", CubeListBuilder.create().texOffs(28, 51)
                .addBox(-4.5F, -10.5F, -4.5F, 9.0F, 4.0F, 9.0F), PartPose.ZERO);
        head.addOrReplaceChild("miner_lamp", CubeListBuilder.create().texOffs(28, 46)
                .addBox(-1.0F, -10.0F, -5.6F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);

        root.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(44, 0)
                        .addBox(2.0F, -4.0F, -1.5F, 4.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(2.0F, 3.0F, 1.0F, PANIC_ARM_ROTATION, 0.0F, 0.0F));
        root.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(44, 9)
                        .addBox(2.0F, -8.0F, -5.5F, 4.0F, 8.0F, 4.0F),
                PartPose.offsetAndRotation(2.0F, 3.0F, 1.0F, PANIC_ARM_ROTATION, 0.0F, 0.0F));
        root.addOrReplaceChild("right_shoulder", CubeListBuilder.create().texOffs(44, 0)
                        .addBox(-6.0F, -4.0F, -1.5F, 4.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(-2.0F, 3.0F, 1.0F, PANIC_ARM_ROTATION, 0.0F, 0.0F));
        root.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(44, 9)
                        .addBox(-6.0F, -8.0F, -5.5F, 4.0F, 8.0F, 4.0F),
                PartPose.offsetAndRotation(-2.0F, 3.0F, 1.0F, PANIC_ARM_ROTATION, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public ModelPart getCarryAnchor(boolean rescued) {
        return rescued ? arms : rightHand;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        boolean rescued = entity.isRescued();
        arms.visible = rescued;
        leftShoulder.visible = !rescued;
        leftHand.visible = !rescued;
        rightShoulder.visible = !rescued;
        rightHand.visible = !rescued;

        float swing = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        leftShoulder.xRot = swing + PANIC_ARM_ROTATION;
        leftHand.xRot = leftShoulder.xRot;
        rightShoulder.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F + PANIC_ARM_ROTATION;
        rightHand.xRot = rightShoulder.xRot;
    }
}
