package ECContents;

import ECConfig.ECData;
import ECConfig.ECTool;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;

import static mindustry.content.SectorPresets.craters;

public class ECTechTrees {
    public static void load(){

        for (UnlockableContent root : ECData.getAllContentKeys()){
            ECTool.loadECTechNode(root);
        }

        for (TechTree.TechNode node : Blocks.siliconSmelter.techNodes){
            node.objectives.add(new Objectives.SectorComplete(craters));
        }



    }
}
