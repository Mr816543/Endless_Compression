package ECContents;

import ECConfig.ECData;
import ECConfig.ECTool;
import mindustry.ctype.UnlockableContent;

public class ECTechTrees {
    public static void load(){

        for (UnlockableContent root : ECData.getAllContentKeys()){
            ECTool.loadECTechNode(root);
        }



    }
}
