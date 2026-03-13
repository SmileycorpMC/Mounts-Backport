package net.smileycorp.mounts.client.entity.models;

import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelRenderer;

//yeah this code sucks ass
//but it's the only way I could get this shit rendering
public class ModelScalableHorse extends ModelHorse {

    public ModelScalableHorse(float scale) {
        super();
        //remove all the boxes from the horse model
        for (ModelRenderer renderer : boxList) {
            renderer.cubeList.clear();
            if (renderer.childModels == null) continue;
            for (ModelRenderer child : renderer.childModels) child.cubeList.clear();
        }
        //re-add all the boxes but scaled
        body.addBox(-5, -8, -19, 10, 10, 24, scale);
        tailBase.addBox(-1, -1, 0, 2, 2, 3, scale);
        tailMiddle.addBox(-1.5f, -2, 3, 3, 4, 7, scale);
        tailTip.addBox(-1.5f, -4.5f, 9, 3, 4, 7, scale);
        backLeftLeg.addBox(-2.5f, -2, -2.5f, 4, 9, 5, scale);
        backLeftShin.addBox(-2, 0, -1.5f, 3, 5, 3, scale);
        backLeftHoof.addBox(-2.5f, 5.1f, -2, 4, 3, 4, scale);
        backRightLeg.addBox(-1.5f, -2, -2.5f, 4, 9, 5, scale);
        backRightShin.addBox(-1, 0, -1.5f, 3, 5, 3, scale);
        backRightHoof.addBox(-1.5f, 5.1f, -2, 4, 3, 4, scale);
        frontLeftLeg.addBox(-1.9f, -1, -2.1f, 3, 8, 4, scale);
        frontLeftShin.addBox(-1.9f, 0, -1.6f, 3, 5, 3, scale);
        frontLeftHoof.addBox(-2.4f, 5.1f, -2.1f, 4, 3, 4, scale);
        frontRightLeg.addBox(-1.1f, -1, -2.1f, 3, 8, 4, scale);
        frontRightShin.addBox(-1.1f, 0, -1.6f, 3, 5, 3, scale);
        frontRightHoof.addBox(-1.6f, 5.1f, -2.1f, 4, 3, 4, scale);
        head.addBox(-2.5f, -10, -1.5f, 5, 5, 7, scale);
        upperMouth.addBox(-2, -10, -7, 4, 3, 6, scale);
        lowerMouth.addBox(-2, -7, -6.5f, 4, 2, 5, scale);
        horseLeftEar.addBox(0.45f, -12, 4, 2, 3, 1, scale);
        horseRightEar.addBox(-2.45f, -12, 4, 2, 3, 1, scale);
        muleLeftEar.addBox(-2, -16, 4, 2, 7, 1, scale);
        muleRightEar.addBox(0, -16, 4, 2, 7, 1, scale);
        neck.addBox(-2.05f, -9.8f, -2, 4, 14, 8, scale);
        muleLeftChest.addBox(-3, 0, 0, 8, 8, 3, scale);
        muleRightChest.addBox(-3, 0, 0, 8, 8, 3, scale);
        horseSaddleBottom.addBox(-5, 0, -3, 10, 1, 8, scale);
        horseSaddleFront.addBox(-1.5f, -1, -3, 3, 1, 2, scale);
        horseSaddleBack.addBox(-4, -1, 3, 8, 1, 2, scale);
        horseLeftSaddleMetal.addBox(-0.5f, 6, -1, 1, 2, 2, scale);
        horseLeftSaddleRope.addBox(-0.5f, 0, -0.5f, 1, 6, 1, scale);
        horseRightSaddleMetal.addBox(-0.5f, 6, -1, 1, 2, 2, scale);
        horseRightSaddleRope.addBox(-0.5f, 0, -0.5f, 1, 6, 1, scale);
        horseLeftFaceMetal.addBox(1.5f, -8, -4, 1, 2, 2, scale);
        horseRightFaceMetal.addBox(-2.5f, -8, -4, 1, 2, 2, scale);
        horseLeftRein.addBox(2.6f, -6, -6, 0, 3, 16, scale);
        horseRightRein.addBox(-2.6f, -6, -6, 0, 3, 16, scale);
        mane.addBox(-1, -11.5f, 5, 2, 16, 4, scale);
        horseFaceRopes.addBox(-2.5f, -10.1f, -7, 5, 5, 12, 0.2f * scale);
    }

}
