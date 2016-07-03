package com.kotcrab.vis.editor.test.manual

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import com.kotcrab.vis.plugin.spine.runtime.SpineSupport
import com.kotcrab.vis.plugin.spriter.runtime.SpriterSupport
import com.kotcrab.vis.runtime.RuntimeConfiguration
import com.kotcrab.vis.runtime.font.FreeTypeFontProvider
import com.kotcrab.vis.runtime.scene.Scene
import com.kotcrab.vis.runtime.scene.SceneLoader
import com.kotcrab.vis.runtime.scene.VisAssetManager

/**
 * Simple utility for running VisRuntime application from existing exported project. Used for testing.
 * @author Kotcrab
 */
fun main(args: Array<String>) {
    if (args.size != 2) {
        println("You must specify project root path and scene path")
        return
    }
    var projPath = args[0]
    var scenePath = args[1]
    projPath = projPath.replace("\\", "/")
    scenePath = scenePath.replace("\\", "/")
    if (projPath.endsWith("/") == false) projPath += "/"

    val config = Lwjgl3ApplicationConfiguration()
    config.setWindowedMode(1280, 720)
    config.useVsync(true)
    Lwjgl3Application(ExternalProjectRunner(projPath, scenePath), config)
}

class ExternalProjectRunner(val rootPath: String, val scenePath: String) : ApplicationAdapter() {
    var batch: SpriteBatch? = null
    var manager: VisAssetManager? = null
    var scene: Scene? = null

    override fun create() {
        batch = SpriteBatch()

        val manager = VisAssetManager(FileHandleResolver { filename ->
            if (filename.startsWith(rootPath))
                Gdx.files.absolute(filename)
            else
                Gdx.files.absolute("$rootPath$filename")
        }, batch)
        this.manager = manager
        manager.logger.level = Logger.ERROR
        manager.enableFreeType(FreeTypeFontProvider())
        manager.registerSupport(SpineSupport())
        manager.registerSupport(SpriterSupport())

        val configuration = RuntimeConfiguration()
        manager.sceneLoader.setRuntimeConfig(configuration)
        val parameter = SceneLoader.SceneParameter()
        scene = manager.loadSceneNow(scenePath, parameter)
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        scene!!.render()
    }

    override fun resize(width: Int, height: Int) {
        scene!!.resize(width, height)
    }

    override fun dispose() {
        manager!!.dispose()
    }
}
