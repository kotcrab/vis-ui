VisSceneEditor
==============

VisSceneEditor is a real-time simple 2D scene editor for Libgdx.

![Screenshot](http://dl.kotcrab.pl/github/vissceneeditor.png)

It allows you to easily adjust position, scale and rotation of sprites or any other objects.

##Usage

1.Set path to your Android project assets folder for example:
```java
SceneEditorConfig.assetsFolderPath = "E:/Git/VisSceneEditor/Examples/android/assets/";
```
Path must be ended wtih File.separator

2.Create SceneEditor, register supports and add your objects:
```java
bush1 = new Sprite(bushTexture);
bush2 = new Sprite(bushTexture);
net1 = new Sprite(netTexture);
net2 = new Sprite(netTexture);

sceneEditor = new SceneEditor(Gdx.files.internal("scene.json"), camera, true);
sceneEditor.registerSupport(Sprite.class, new SpriteSupport());
sceneEditor.add(bush1, "bush1").add(bush2, "bush2").add(net1, "net1").add(net2, "net2");
sceneEditor.load();
```

```java
sceneEditor = new SceneEditor(Gdx.files.internal("scene.json"), camera, true);
```
Constructor takes location of a scene file, an OrthograhicCamera, the last argument is a devMode flag. If true it will allow you to edit sprites if false all editing features will be disabled. When you are publishing your game this should be set to false.

If scene files does not exist it will be created.

3.Call sceneEditor.render() in your render() method. <br>
4.Call sceneEditor.dispose() in your dispose() method. <br>
5.Call sceneEditor.resize() in your resize() method. <br>

[Full example](https://github.com/kotcrab/VisSceneEditorExamples/blob/master/core/src/pl/kotcrab/vis/sceneeditor/example/scene/ExampleScene.java) <br>

SceneEditor automatically attaches its InputProcessor, if you change current InputProcessor you will have to call:
```java
sceneEditor.attachInputProcessor();
```
<br>
To enable or disable editing use:
```java
sceneEditor.enable();
sceneEditor.disable();
```
(or you can press F11 to toggle edit mode)

##Edit mode

In edit mode you can adjust position, rotation and scale. To move object just click and drag it, you can hold Left Ctrl to move it precisely. To scale object use small rectangle in the upper right corner, you can hold Left Shift to scale with keeping object ratio. To adjust rotation use circle above the object (you must select it to make it visible)

When you exit edit mode all changes are automatically saved. Next time when you load your game all properties of objects will be restored.

