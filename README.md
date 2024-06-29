# VisUI

VisUI allows to create nice looking UI in libGDX using scene2d.ui. Library contains scene2d.ui skin, useful widgets like color picker and file chooser, it also contains modified scene2d.ui widgets to provide some extra functionality like focus borders, background change on over and click, etc.

VisUI is licensed under Apache2 license meaning that you can use it for free in both commercial and non-commercial projects.

##### [CHANGES](https://github.com/kotcrab/vis-ui/blob/master/ui/CHANGES.md) file (definitely read before updating!)

**[Web demo!](http://vis.kotcrab.com/demo/ui)** [(source code)](https://github.com/kotcrab/vis-ui/tree/master/ui/src/test/java/com/kotcrab/vis/ui/test/manual)

![VisUI screenshot](http://dl.kotcrab.com/github/vis/visui2.png)
[Bigger screenshot](http://dl.kotcrab.com/github/vis/visui2.png)

## Adding VisUI to your project

[![Maven Central](https://img.shields.io/maven-central/v/com.kotcrab.vis/vis-ui.svg)](https://search.maven.org/artifact/com.kotcrab.vis/vis-ui)

Please refer to [libGDX documentation](https://libgdx.com/wiki/articles/dependency-management-with-gradle) if you don't know how to manage dependencies with Gradle. Alternatively JAR can be downloaded from [Maven repository](http://search.maven.org/#search|gav|1|g%3A%22com.kotcrab.vis%22%20AND%20a%3A%22vis-ui%22). If you are creating new project, you can use gdx-setup to automatically add VisUI for you. (press 'Show Third Party Extension' button)

#### Manual Gradle setup:

Open build.gradle in project root.
In ``ext`` section under ``allprojects`` add:
```groovy
visuiVersion = '1.X.X'
```
Look at [CHANGES](https://github.com/kotcrab/vis-ui/blob/master/ui/CHANGES.md) file to see what version of VisUI you can use
for your version of libGDX. Note that using not matching versions is likely to cause runtime exceptions.

**Core dependency**
```groovy
api "com.kotcrab.vis:vis-ui:$visuiVersion"
```

**HTML dependency** (only if you are using GWT):
```groovy
api "com.kotcrab.vis:vis-ui:$visuiVersion:sources"
```

``GdxDefinition.gwt.xml`` and ``GdxDefinitionSuperdev.gwt.xml``:
```xml
<inherits name='com.kotcrab.vis.vis-ui' />
```

Refresh Gradle dependencies.

## Usage

Using VisUI is pretty simple, to load or unload the skin call:
```java
VisUI.load();
VisUI.dispose();
```

Create your UI like always, for extra skin features you have to use Vis widgets instead of standard scene2d.ui:

| VisUI         | Standard scene2d.ui |
| ------------- | ------------------- |
| VisLabel      | Label               |
| [LinkLabel](https://github.com/kotcrab/vis-ui/wiki/LinkLabel) | -                   |
| VisCheckBox   | CheckBox            |
| VisList       | List                |
| VisProgressBar| ProgressBar         |
| VisRadioButton| -                   |
| VisScrollPane | ScrollPane          |
| VisSelectBox  | SelectBox           |
| VisSlider     | Slider              |
| VisSplitPane  | SplitPane           |
| VisTextArea   | TextArea            |
| VisTextButton | TextButton          |
| VisImageTextButton | ImageTextButton |
| VisImageButton | ImageButton        |
| VisTextField  | TextField           |
| [VisValidatableTextField](https://github.com/kotcrab/vis-ui/wiki/VisValidatableTextField) | -       |
| VisTree       | Tree                |
| VisWindow     | Window              |
| VisTable      | Table               |
| [DragPane](https://github.com/kotcrab/vis-ui/wiki/DragPane)  | -                   |
| [GridGroup](https://github.com/kotcrab/vis-ui/wiki/GridGroup) | -                   |
| [ListView](https://github.com/kotcrab/vis-ui/wiki/ListView)  | -                   |
| [TabbedPane](https://github.com/kotcrab/vis-ui/wiki/TabbedPane)  | -                 |
| [Spinner](https://github.com/kotcrab/vis-ui/wiki/Spinner)  | - |
| [CollapsibleWidget](https://github.com/kotcrab/vis-ui/wiki/CollapsibleWidget) | -           |
| [ButtonBar](https://github.com/kotcrab/vis-ui/wiki/ButtonBar) | -           |
| [FlowGroups](https://github.com/kotcrab/vis-ui/wiki/FlowGroups) | -           |

Using Vis widgets is necessary for proper focus border management. All VisUI widgets constructors do not have Skin argument, they are using VisUI.skin field.

### VisTable

VisTable allows to easily set default spacing for vis components, construct it like this:
```
VisTable table = new VisTable(true);
```

VisTable also allows adding vertical and horizontal separators to table:
```java
table.addSeparator() //horizontal
table.addSeparator(true) //vertical
```

### Using different `SkinScale`s

Default VisUI skin can be too small for high resolution screens or mobile devices, in that case you can load a upscaled skin version simply by calling:
```
VisUI.load(SkinScale.X2);
```

### Internal classes

Classes inside `com.kotcrab.vis.[...].internal` packages are considered private and aren't part of public API. Changes to that classes won't be listed in change log.

### Default title align

Default title align for VisWindow and VisDialog is `Align.left` this can be changed by calling:
```java
VisUI.setDefaultTitleAlign(int align)
```
Calling this method does not affect windows that have been already created.

## Modifying skin

[Raw skin files](https://github.com/kotcrab/vis-ui/tree/master/ui/assets-raw) are available if you would like to modify them. After you pack them using libGDX texture packer, add generated atlas to your project with [uiskin.json, default.fnt and font-small.fnt](https://github.com/kotcrab/vis-ui/tree/master/ui/src/main/resources/com/kotcrab/vis/ui/skin/x1) and load it by calling:
```java
VisUI.load(Gdx.files.internal("path/to/your/modified/files/uiskin.json"))
```
Consider using USL if you want to extend existing VisUI styles. [Read more](https://github.com/kotcrab/vis-ui/wiki/USL)

## See also

* [vis-ui-contrib](https://github.com/kotcrab/vis-ui-contrib) - Community driven extension, utilities and skins for VisUI
* [ktx](https://github.com/czyzby/ktx) - Kotlin utilities for libGDX applications.
  The [ktx-vis](https://github.com/czyzby/ktx/tree/master/vis) and
  [ktx-style-vis](https://github.com/czyzby/ktx/tree/master/vis-style) modules provide Kotlin APIs for VisUI.
