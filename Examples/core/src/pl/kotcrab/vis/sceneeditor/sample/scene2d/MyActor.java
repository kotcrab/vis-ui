package pl.kotcrab.vis.sceneeditor.sample.scene2d;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MyActor extends Actor {
    TextureRegion region;

    public MyActor (Texture texture) {
        region = new TextureRegion(texture);
        setWidth(region.getRegionWidth());
        setHeight(region.getRegionHeight());
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
    
    
}