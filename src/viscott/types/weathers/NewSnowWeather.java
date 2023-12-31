package viscott.types.weathers;

import arc.Core;
import arc.Events;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Weathers;
import mindustry.game.EventType;
import mindustry.gen.Sounds;
import mindustry.gen.WeatherState;
import mindustry.graphics.Layer;
import mindustry.type.weather.ParticleWeather;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Attribute;
import viscott.utilitys.PvUtil;
import viscott.world.block.drill.Grinder;

public class NewSnowWeather extends ParticleWeather {
    static TextureRegion[] snowOverlay;
    static TextureRegion[] snowOverlayConveyor;
    float alpha = 0;
    public NewSnowWeather(String name) {
        super(name);
    }

    @Override
    public void load(){
        super.load();
        if(Vars.headless) return;
        snowOverlayConveyor = new TextureRegion[4];
        for(int i = 0;i < 4;i++)
            snowOverlayConveyor[i] = Core.atlas.find(PvUtil.GetName("snow-1-"+i));

        snowOverlay = new TextureRegion[2];
        for(int i = 0;i < 2;i++)
            snowOverlay[i] = Core.atlas.find(PvUtil.GetName("snow-"+(i+1)));
    }

    @Override
    public void init() {
        super.init();
        if(Vars.headless) return;
        Events.run(EventType.Trigger.draw,()->{
            Log.info(alpha);
            if (!isActive())
                alpha = Mathf.lerp(alpha,0,0.01f);
            if (alpha < 0.01) return;
            Draw.z(Layer.blockOver);
            Draw.alpha(alpha);
            Vars.state.teams.getActive().forEach((teamData) -> {
                teamData.buildings.forEach((building) -> {
                    if (building instanceof Conveyor.ConveyorBuild cb ) {
                        for(int i = 0;i < 4;i++)
                            if (cb.rotation != i && (cb.nearby(i) == null || !(cb.nearby(i).block instanceof Conveyor && (cb.nearby(i).rotation+2)%4 == i)))
                                Draw.rect(snowOverlayConveyor[i],building.x,building.y);
                    } else if (building instanceof StackConveyor.StackConveyorBuild cb) {
                        for(int i = 0;i < 4;i++)
                            if ((cb.rotation == i && cb.nearby(i) == null) || (cb.rotation != i && (cb.nearby(i) == null || !(cb.nearby(i).block instanceof StackConveyor && (cb.nearby(i).rotation+2)%4 == i))))
                                Draw.rect(snowOverlayConveyor[i],building.x,building.y);
                    } else {
                        if (snowOverlay.length >= building.block.size)
                            Draw.rect(snowOverlay[building.block.size - 1], building.x, building.y);
                    }
                });
            });
        });
    }

    @Override
    public WeatherState create(float intensity, float duration){
        alpha = 0;
        return super.create(intensity,duration);
    }

    @Override
    public void update(WeatherState state){
        super.update(state);
        alpha = Mathf.lerp(alpha,Mathf.clamp(state.life/60,0,1),0.01f);
    }

    @Override
    public void drawOver(WeatherState state){
        super.drawOver(state);
    }
    @Override
    public void drawUnder(WeatherState state){
        super.drawUnder(state);
    }

}