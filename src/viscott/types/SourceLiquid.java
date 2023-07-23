package viscott.types;

import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Puddle;
import mindustry.type.CellLiquid;
import mindustry.type.StatusEffect;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Prop;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.liquid.Conduit;
import viscott.content.PvLiquids;

public class SourceLiquid extends CellLiquid {
    public float SourceDamage = -10;
    public StatusEffect SourceFlyingEffect = null;
    public int[][] checks = {
            {-1,0},
            {0,-1},
            {1,0},
            {0,1}
    };
    public SourceLiquid(String name) {
        super(name);
        canStayOn.addAll(Liquids.water);
        spreadTarget = PvLiquids.concentratedVoid;
    }
    @Override
    public void update(Puddle puddle){
        if (SourceFlyingEffect != null)
            Groups.unit.each(u-> Math.abs(u.x - puddle.x) < 8 && Math.abs(u.y - puddle.y) < 8,u-> {
                if (u.hovering || u.isFlying()) {
                    u.apply(SourceFlyingEffect, 30);
                    u.apply(effect, 30);
                }
            });
        Building build = puddle.tile.build;
        if (build != null && build.liquids != null && (build.liquids.currentAmount() == 0 || build.liquids.current() == puddle.liquid)) {
            build.handleLiquid(null,puddle.liquid,puddle.amount);
            puddle.remove();
        }
        for(int rot = 0;rot < checks.length;rot++) {
            int[] i = checks[rot];
            Tile t = puddle.tile.nearby(i[0], i[1]);
            Puddle np = Groups.puddle.find(p->p.tile == t);
            if (np != null) {
                puddle.amount = (np.amount + puddle.amount) / 2;
                np.amount = puddle.amount;
            }
            if ( t == null || t.block() == null ) continue;
            if (t.block() instanceof Prop prop && !(prop instanceof StaticWall)) {
                t.removeNet();
                prop.breakEffect.at(t.x,t.y,0,prop.lightColor);
                puddle.amount += 100;
            }
            if (t.build instanceof Conduit.ConduitBuild cb) {
                if(cb.rotation != rot && (cb.liquids.current() == this || cb.liquids.currentAmount() == 0)) {
                    cb.liquids.add(this,puddle.amount);
                    cb.noSleep();
                    puddle.remove();
                }
            }
            if (t.build == null|| !t.build.isValid() ) continue;
            t.build.damage(SourceDamage/60);
            puddle.amount += SourceDamage/60;
            rot++;
        }
        super.update(puddle);
    }
}
