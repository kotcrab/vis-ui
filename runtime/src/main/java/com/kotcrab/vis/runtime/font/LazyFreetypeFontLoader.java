package com.kotcrab.vis.runtime.font;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.kotcrab.vis.runtime.util.font.LazyBitmapFont;

public class LazyFreetypeFontLoader extends FreetypeFontLoader {
    public LazyFreetypeFontLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public BitmapFont loadSync(AssetManager manager, String fileName, FileHandle file, FreeTypeFontLoaderParameter parameter) {
        if(parameter == null) throw new RuntimeException("FreetypeFontParameter must be set in AssetManager#load to point at a TTF file!");
        FreeTypeFontGenerator generator = manager.get(parameter.fontFileName + ".gen", FreeTypeFontGenerator.class);
//        BitmapFont font = generator.generateFont(parameter.fontParameters);
        BitmapFont font = new LazyBitmapFont(generator, parameter.fontParameters);
        return font;
    }

//    private FreeTypeFontGenerator.FreeTypeFontParameter getParameterForSize (int size) {
//        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        parameter.size = size;
//        parameter.incremental = true;
//        parameter.minFilter = Texture.TextureFilter.Linear;
//        parameter.magFilter = Texture.TextureFilter.Linear;
//        return parameter;
//    }
}
