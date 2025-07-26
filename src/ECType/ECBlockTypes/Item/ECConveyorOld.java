package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ChainedBuilding;
import mindustry.world.blocks.distribution.Conveyor;

import static mindustry.Vars.*;

public class ECConveyorOld extends Conveyor {

    private static final float itemSpace = 0.4f;
    private static final int capacity = 3;

    public Conveyor root;

    public int level;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .linearConfig("speed", "displayedSpeed");


    public ECConveyorOld(Conveyor root, int level) throws IllegalAccessException {
        super("c"+level+"-"+root.name);

        this.root = root;
        this.level = level;


        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,level);
    }

    @Override
    public void init() {
        super.init();
    }

    public class ECConveyorBuild extends ConveyorBuild implements ChainedBuilding {

        @Override
        public void draw(){
            int frame = enabled && clogHeat <= 0.5f ? (int)(((Time.time * speed * 8f * timeScale * efficiency)) % 4) : 0;

            //draw extra conveyors facing this one for non-square tiling purposes
            Draw.z(Layer.blockUnder);
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = rotation - i;
                    float rot = i == 0 ? rotation * 90 : (dir)*90;

                    Draw.rect(sliced(regions[0][frame], i != 0 ? SliceMode.bottom : SliceMode.top), x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, rot);
                }
            }

            Draw.z(Layer.block - 0.2f);

            Draw.rect(regions[blendbits][frame], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);

            Draw.z(Layer.block - 0.1f);
            float layer = Layer.block - 0.1f, wwidth = world.unitWidth(), wheight = world.unitHeight(), scaling = 0.01f;

            for(int i = 0; i < len; i++){
                Item item = ids[i];
                Tmp.v1.trns(rotation * 90, tilesize, 0);
                Tmp.v2.trns(rotation * 90, -tilesize / 2f, xs[i] * tilesize / 2f);

                float
                        ix = (x + Tmp.v1.x * ys[i] + Tmp.v2.x),
                        iy = (y + Tmp.v1.y * ys[i] + Tmp.v2.y);

                //keep draw position deterministic.
                Draw.z(layer + (ix / wwidth + iy / wheight) * scaling);
                Draw.rect(item.fullIcon, ix, iy, itemSize, itemSize);
            }
        }

        @Override
        public void payloadDraw(){
            Draw.rect(block.fullIcon, x, y);
        }

        @Override
        public void drawCracks(){
            Draw.z(Layer.block - 0.15f);
            super.drawCracks();
        }

        @Override
        public void overwrote(Seq<Building> builds){
            if(builds.first() instanceof Conveyor.ConveyorBuild build){
                ids = build.ids.clone();
                xs = build.xs.clone();
                ys = build.ys.clone();
                len = build.len;
                clogHeat = build.clogHeat;
                lastInserted = build.lastInserted;
                mid = build.mid;
                minitem = build.minitem;
                items.add(build.items);
            }
        }

        @Override
        public boolean shouldAmbientSound(){
            return clogHeat <= 0.5f;
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = bits[0];
            blendsclx = bits[1];
            blendscly = bits[2];
            blending = bits[4];

            next = front();
            nextc = next instanceof Conveyor.ConveyorBuild && next.team == team ? (Conveyor.ConveyorBuild)next : null;

            aligned = nextc != null && rotation == next.rotation   ;
        }

        @Override
        public void unitOn(Unit unit){

            if(!pushUnits || clogHeat > 0.5f || !enabled) return;

            noSleep();

            float mspeed = speed * tilesize * 55f;
            float centerSpeed = 0.1f;
            float centerDstScl = 3f;
            float tx = Geometry.d4x(rotation), ty = Geometry.d4y(rotation);

            float centerx = 0f, centery = 0f;

            if(Math.abs(tx) > Math.abs(ty)){
                centery = Mathf.clamp((y - unit.y()) / centerDstScl, -centerSpeed, centerSpeed);
                if(Math.abs(y - unit.y()) < 1f) centery = 0f;
            }else{
                centerx = Mathf.clamp((x - unit.x()) / centerDstScl, -centerSpeed, centerSpeed);
                if(Math.abs(x - unit.x()) < 1f) centerx = 0f;
            }

            if(len * itemSpace < 0.9f){
                unit.impulse((tx * mspeed + centerx) * delta(), (ty * mspeed + centery) * delta());
            }
        }

        @Override
        public void updateTile(){
            minitem = 1f;
            mid = 0;

            //skip updates if possible
            if(len == 0 && Mathf.equal(timeScale, 1f)){
                clogHeat = 0f;
                sleep();
                return;
            }

            float moved = speed * edelta();





            float nextMax = aligned ? 1f + Math.min( nextc.minitem -itemSpace, 0) : 1f;

            for(int i = len - 1; i >= 0; i--){


                float to = ys[i] + moved;
                if (to<2){
                    slowMove(i, moved, nextMax);
                }else {
                    fastMove(i,moved,nextMax,to);
                }


            }

            if(minitem < itemSpace + (blendbits == 1 ? 0.3f : 0f)){
                clogHeat = Mathf.approachDelta(clogHeat, 1f, 1f / 60f);
            }else{
                clogHeat = 0f;
            }

            noSleep();
        }

        private void slowMove(int i, float moved, float nextMax) {
            float nextpos = (i == len - 1 ? 100f : ys[i + 1]) - itemSpace;
            float maxmove = Mathf.clamp(nextpos - ys[i], 0, moved);

            ys[i] += maxmove;

            if(ys[i] > nextMax) ys[i] = nextMax;

            if(ys[i] > 0.5 && i > 0) mid = i - 1;
            xs[i] = Mathf.approach(xs[i], 0, moved *2);

            if(ys[i] >= 1f && pass(ids[i])){
                //align X position if passing forwards
                if(aligned){
                    nextc.xs[nextc.lastInserted] = xs[i];
                    nextc.ys[nextc.lastInserted] = (ys[i]+moved)%1;
                }
                //remove last item
                items.remove(ids[i], len - i);
                len = Math.min(i, len);
            }else if(ys[i] < minitem){
                minitem = ys[i];
            }
        }
        private void fastMove(int i, float moved, float nextMax,float to) {

            Item item = ids[i];
            int max = maxLength((int)to);

            while (true){

                Building moveTo = getMoveTo(max,item);
                Building lastBuild = getMoveTo(max-1,item);


                if (moveTo == this){
                    slowMove(i,moved,nextMax);
                    return;
                }

                if (moveTo.acceptItem(lastBuild,item)) {
                    if (moveTo instanceof ECConveyorBuild e){
                        e.handleItem(lastBuild,item,to%1);
                    }else {
                        moveTo.handleItem(lastBuild, item);
                    }
                    items.remove(item, len - i);
                    len = Math.min(i, len);
                    return;
                } else {
                    max -= 1 ;

                }


            }

            /*/
            float nextpos = (i == len - 1 ? 100f : ys[i + 1]) - itemSpace;
            float maxmove = Mathf.clamp(nextpos - ys[i], 0, moved);

            ys[i] += maxmove;

            if(ys[i] > nextMax) ys[i] = nextMax;

            if(ys[i] > 0.5 && i > 0) mid = i - 1;
            xs[i] = Mathf.approach(xs[i], 0, moved *2);

            if(ys[i] >= 1f && pass(item)){
                //align X position if passing forwards
                if(aligned){
                    nextc.xs[nextc.lastInserted] = xs[i];
                }

                //remove last item
                items.remove(item, len - i);
                len = Math.min(i, len);



            }else if(ys[i] < minitem){
                minitem = ys[i];
            }
            //*/
        }

        public boolean pass(Item item){
            if(item != null && next != null && next.team == team && next.acceptItem(this, item)){
                next.handleItem(this, item);
                return true;
            }
            return false;
        }

        @Override
        public int removeStack(Item item, int amount){
            noSleep();
            int removed = 0;

            for(int j = 0; j < amount; j++){
                for(int i = 0; i < len; i++){
                    if(ids[i] == item){
                        remove(i);
                        removed ++;
                        break;
                    }
                }
            }

            items.remove(item, removed);
            return removed;
        }

        @Override
        public void getStackOffset(Item item, Vec2 trns){
            trns.trns(rotdeg() + 180f, tilesize / 2f);
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return Math.min((int)(minitem / itemSpace), amount);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            amount = Math.min(amount, capacity - len);

            for(int i = amount - 1; i >= 0; i--){
                add(0);
                xs[0] = 0;
                ys[0] = i * itemSpace;
                ids[0] = item;
                items.add(item, 1);
            }

            noSleep();
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            if(len >= capacity) return false;
            Tile facing = Edges.getFacingEdge(source.tile, tile);
            if(facing == null) return false;
            int direction = Math.abs(facing.relativeTo(tile.x, tile.y) - rotation);
            return (((direction == 0) && minitem >= itemSpace) || ((direction % 2 == 1) && minitem > 0.7f)) && !(source.block.rotate && next == source);
        }

        @Override
        public void handleItem(Building source, Item item){
            if(len >= capacity) return;

            int r = rotation;
            Tile facing = Edges.getFacingEdge(source.tile, tile);
            int ang = ((facing.relativeTo(tile.x, tile.y) - r));
            float x = (ang == -1 || ang == 3) ? 1 : (ang == 1 || ang == -3) ? -1 : 0;

            noSleep();
            items.add(item, 1);

            if(Math.abs(facing.relativeTo(tile.x, tile.y) - r) == 0){ //idx = 0
                add(0);
                xs[0] = x;
                ys[0] = 0;
                ids[0] = item;
            }
            else{ //idx = mid
                add(mid);
                xs[mid] = x;
                ys[mid] = 0.5f;
                ids[mid] = item;
            }



        }

        public void handleItem(Building source, Item item,float y){
            if(len >= capacity) return;
            if (y>1) y=1;

            int r = rotation;
            Tile facing = Edges.getFacingEdge(source.tile, tile);
            int ang = ((facing.relativeTo(tile.x, tile.y) - r));
            float x = (ang == -1 || ang == 3) ? 1 : (ang == 1 || ang == -3) ? -1 : 0;
            noSleep();
            items.add(item, 1);

            if(Math.abs(facing.relativeTo(tile.x, tile.y) - r) == 0){ //idx = 0
                add(0);
                xs[0] = 0;

                if (ids[1] != null){

                    ys[0] = Math.min(y, minitem - itemSpace   );
                    ys[0] = Math.max(ys[0],0);
                }else {
                    ys[0] = y;
                }


                ids[0] = item;
            }else{ //idx = mid
                add(mid);
                xs[mid] = 0;
                ys[mid] = 0.5f;
                ids[mid] = item;
            }
            minitem = ys[0];

        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(len);

            for(int i = 0; i < len; i++){
                write.s(ids[i].id);
                write.b((byte)(xs[i] * 127));
                write.b((byte)(ys[i] * 255 - 128));
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int amount = read.i();
            len = Math.min(amount, capacity);

            for(int i = 0; i < amount; i++){
                short id;
                float x, y;

                if(revision == 0){
                    int val = read.i();
                    id = (short)(((byte)(val >> 24)) & 0xff);
                    x = (float)((byte)(val >> 16)) / 127f;
                    y = ((float)((byte)(val >> 8)) + 128f) / 255f;
                }else{
                    id = read.s();
                    x = (float)read.b() / 127f;
                    y = ((float)read.b() + 128f) / 255f;
                }

                if(i < capacity){
                    ids[i] = content.item(id);
                    xs[i] = x;
                    ys[i] = y;
                }
            }

            //this updates some state
            updateTile();
        }

        @Override
        public Object senseObject(LAccess sensor){
            if(sensor == LAccess.firstItem && len > 0) return ids[len - 1];
            return super.senseObject(sensor);
        }


        @Nullable
        @Override
        public Building next(){
            return next;
        }

        public int maxLength(int max){

            return maxLength(max,0);
        }

        public int maxLength(int max,int trying){

            if (trying >= max) return max;

            if (next()instanceof ECConveyorBuild e) {
               return e.maxLength(max,trying+1);
            } else  return trying + 1;

        }

        public Building getMoveTo( int length,Item item){

            if (length <= 0) return this;

            if (next() == null) return this;
            if (!next().acceptItem(this,item)){
                return this;
            }

            if (next() instanceof ECConveyorBuild e){
                return e.getMoveTo(length - 1,item);
            }


            return next();

        }
        public Building getMoveTo( int length){

            if (length <= 0) return this;

            if (next() == null) return this;

            if (next() instanceof ECConveyorBuild e){
                return e.getMoveTo(length - 1);
            }


            return next();

        }










    }
}