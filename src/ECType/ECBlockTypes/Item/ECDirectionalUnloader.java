package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.Edges;
import mindustry.world.blocks.distribution.DirectionalUnloader;
import mindustry.world.blocks.distribution.OverflowDuct;
import mindustry.world.blocks.sandbox.ItemVoid;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.content;
import static mindustry.Vars.state;

public class ECDirectionalUnloader extends DirectionalUnloader {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig()
            .linearConfig();
    public DirectionalUnloader root;
    public int level;
    public float outputMultiple;

    public ECDirectionalUnloader(DirectionalUnloader root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);

        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER, level);
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root, this, level);
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        super.init();
    }


    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.speed);
        stats.add(Stat.speed, 60f / speed * outputMultiple, StatUnit.itemsSecond);
    }

    public class ECDirectionalUnloaderBuild extends DirectionalUnloaderBuild {

        @Override
        public void updateTile(){
            if((unloadTimer += edelta()) >= speed){
                Building front = front(), back = back();

                if(front != null && back != null && back.items != null && front.team == team && back.team == team && back.canUnload() && (allowCoreUnload || !(back instanceof CoreBlock.CoreBuild || (back instanceof StorageBlock.StorageBuild sb && sb.linkedCore != null)))){
                    if(unloadItem == null){
                        var itemseq = content.items();
                        int itemc = itemseq.size;
                        for(int i = 0; i < itemc; i++){
                            Item item = itemseq.get((i + offset) % itemc);
                            if(back.items.has(item) && front.acceptItem(this, item)){

                                int moves = front.acceptStack(item,back.items.get(item),this);
                                if (moves>0){
                                    front.handleStack(item,moves,this);
                                    back.items.remove(item, moves);
                                    if ((back instanceof CoreBlock.CoreBuild || (back instanceof StorageBlock.StorageBuild sb && sb.linkedCore != null))){

                                        if(state.isCampaign() && team == state.rules.defaultTeam){
                                            //update item taken amount
                                            state.rules.sector.info.handleCoreItem(item, -moves);
                                        }


                                    }

                                }else {
                                    front.handleItem(this, item);
                                    back.items.remove(item, 1);
                                    back.itemTaken(item);
                                }
                                offset = item.id + 1;
                                break;
                            }
                        }
                    }
                    else if(back.items.has(unloadItem) && front.acceptItem(this, unloadItem)){
                        int moves = front.acceptStack(unloadItem,back.items.get(unloadItem),this);
                        if (moves>0){
                            front.handleStack(unloadItem,moves,this);
                            back.items.remove(unloadItem, moves);
                            if ((back instanceof CoreBlock.CoreBuild || (back instanceof StorageBlock.StorageBuild sb && sb.linkedCore != null))){

                                if(state.isCampaign() && team == state.rules.defaultTeam){
                                    //update item taken amount
                                    state.rules.sector.info.handleCoreItem(unloadItem, -moves);
                                }


                            }

                        }else {
                            front.handleItem(this, unloadItem);
                            back.items.remove(unloadItem, 1);
                            back.itemTaken(unloadItem);
                        }
                    }
                }

                unloadTimer %= speed;
            }
        }
    }
}
