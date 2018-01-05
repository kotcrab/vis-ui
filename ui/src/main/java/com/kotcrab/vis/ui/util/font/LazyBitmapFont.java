package com.kotcrab.vis.ui.util.font;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.lang.reflect.Field;

/**
 * Created by dotw on 2017/11/23.
 */
public class LazyBitmapFont extends BitmapFont {

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeBitmapFontData data;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    public LazyBitmapFont(FreeTypeFontGenerator generator, FreeTypeFontGenerator.FreeTypeFontParameter param) {
        super();
        if (generator == null)
            throw new GdxRuntimeException("lazyBitmapFont global generator must be not null to use this constructor.");
        this.generator = generator;
        this.parameter = param;
        this.data = new LazyBitmapFontData(generator, param.size, this);
        try {
            Field f = getClass().getSuperclass().getDeclaredField("data");
            f.setAccessible(true);
            f.set(this, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        genrateData();
    }

    private void genrateData() {
        FreeType.Face face = null;
        try {
            Field field = generator.getClass().getDeclaredField("face");
            field.setAccessible(true);
            face = (FreeType.Face) field.get(generator);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // set general font data
        FreeType.SizeMetrics fontMetrics = face.getSize().getMetrics();

        // Set space glyph.
        Glyph spaceGlyph = data.getGlyph(' ');
        if (spaceGlyph == null) {
            spaceGlyph = new Glyph();
            spaceGlyph.xadvance = (int) data.spaceWidth;
            spaceGlyph.id = (int) ' ';
            data.setGlyph(' ', spaceGlyph);
        }
        if (spaceGlyph.width == 0)
            spaceGlyph.width = (int) (spaceGlyph.xadvance + data.padRight);

        // set general font data
        data.flipped = parameter.flip;
        data.ascent = FreeType.toInt(fontMetrics.getAscender());
        data.descent = FreeType.toInt(fontMetrics.getDescender());
        data.lineHeight = FreeType.toInt(fontMetrics.getHeight());

        // determine x-height
        for (char xChar : data.xChars) {
            if (!face.loadChar(xChar, FreeType.FT_LOAD_DEFAULT))
                continue;
            data.xHeight = FreeType.toInt(face.getGlyph().getMetrics().getHeight());
            break;
        }
        if (data.xHeight == 0)
            throw new GdxRuntimeException("No x-height character found in font");
        for (char capChar : data.capChars) {
            if (!face.loadChar(capChar, FreeType.FT_LOAD_DEFAULT))
                continue;
            data.capHeight = FreeType.toInt(face.getGlyph().getMetrics().getHeight());
            break;
        }

        // determine cap height
        if (data.capHeight == 1)
            throw new GdxRuntimeException("No cap character found in font");
        data.ascent = data.ascent - data.capHeight;
        data.down = -data.lineHeight;
        if (parameter.flip) {
            data.ascent = -data.ascent;
            data.down = -data.down;
        }

    }

    @Override
    public void dispose() {
        setOwnsTexture(true);
        super.dispose();
        data.dispose();

    }

    public static class LazyBitmapFontData extends FreeTypeFontGenerator.FreeTypeBitmapFontData {

        private FreeTypeFontGenerator generator;
        private int fontSize;
        private LazyBitmapFont font;
        private int page = 1;

        public LazyBitmapFontData(FreeTypeFontGenerator generator, int fontSize, LazyBitmapFont lbf) {
            this.generator = generator;
            this.fontSize = fontSize;
            this.font = lbf;
        }

        public Glyph getGlyph(char ch) {
            Glyph glyph = super.getGlyph(ch);
            if (glyph == null && ch != 0)
                glyph = generateGlyph(ch);
            return glyph;
        }

        protected Glyph generateGlyph(char ch) {
            FreeTypeFontGenerator.GlyphAndBitmap gab = generator.generateGlyphAndBitmap(ch, fontSize, false);
            if (gab == null || gab.bitmap == null)
                return null;

            Pixmap map = gab.bitmap.getPixmap(Pixmap.Format.RGBA8888, Color.WHITE, 1);
            TextureRegion rg = new TextureRegion(new Texture(map));
            map.dispose();

            font.getRegions().add(rg);

            gab.glyph.page = page++;
            super.setGlyph(ch, gab.glyph);
            setGlyphRegion(gab.glyph, rg);

            return gab.glyph;
        }

    }
}
