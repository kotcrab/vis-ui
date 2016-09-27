VisEditor [![](http://kotcrab.com:8080/buildStatus/icon?job=viseditor-edge)](http://kotcrab.com:8080/job/viseditor-edge/)
=========

[VisEditor](https://vis.kotcrab.com/) is cross platform 2D game level editor, it allows to easily create game scenes using intuitive drag and drop editor. 

VisEditor doesn't force you to use single game framework, scenes are exported in JSON format. Many assets file formats are supported: images, sounds, fonts, particles, GLSL shaders, Spine and Spriter animations with more features coming soon. Box2d physics is supported as well.

See [Quick Start](https://github.com/kotcrab/vis-editor/wiki/Quick-Start) guide to get stared with using VisEditor.

#### Runtime
VisEditor currently have libGDX runtime which uses [artemis-odb](https://github.com/junkdog/artemis-odb) for entity-component-system framework. With that runtime you can load your scene in just few lines of code.

LibGDX runtime source code is a good start if you want to create your own runtime, if you do so don't forget to let me know and I will gladly link it here.

#### Community
You can follow Vis development on my Twitter channels: [@VisEditor](https://twitter.com/VisEditor) and [@kotcrab](https://twitter.com/kotcrab) or read my [blog](https://kotcrab.com/).
You can also join our [forum and IRC channel](https://github.com/kotcrab/vis-editor/wiki/IRC-Channel-and-Forum)!

#### VisUI
[VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI) is a UI library developed alognside VisEditor. It consist of UI skin and many useful widgets, such as file chooser or color picker. More details can be found on [wiki page](https://github.com/kotcrab/vis-editor/wiki/VisUI).

#### Subprojects
* [VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI) - flat design skin and scene2d.ui toolkit for libGDX - [CHANGES](https://github.com/kotcrab/vis-editor/blob/master/UI/CHANGES.md) file - [Documentation](https://github.com/kotcrab/vis-editor/wiki/VisUI)  
* [USL](https://github.com/kotcrab/vis-editor/wiki/USL) - UI Styling Language for scene2d.ui
* [vis-ui-contrib](https://github.com/kotcrab/vis-ui-contrib) - Community driven extension and utilities for VisUI

#### See also
* [gdx-lml-vis](https://github.com/czyzby/gdx-lml/tree/master/lml-vis) - VisUI extension for [gdx-lml](https://github.com/czyzby/gdx-lml/tree/master/lml), LibGDX Markup Language for UI - [web demo](http://czyzby.github.io/gdx-lml/lml-vis/)
* [gdx-setup](https://github.com/czyzby/gdx-setup) -  Alternative gdx-setup application - create your LibGDX projects with ease 
* [ktx](https://github.com/czyzby/ktx) - Kotlin utilities for LibGDX applications
