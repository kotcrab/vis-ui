We are glad that you would like to contribute to the Vis Project. Here are some guidelines that you should follow when making your contributions.

Start by forking this repository, then learn [how to run Vis Projects from source code](https://github.com/kotcrab/vis-editor/wiki/Building-Vis-From-Source).

#### Git commits messages
* Use sentence case (i.e. "Add feature" not "add feature")
* Use imperative, present tense (i.e. "Add", not "Added" or "Adds")
* Don't use dots, exclamation or question marks at the end of commit message

#### Code Formatter
We require you to use code formatter when making pull requests. Code formatter for IntelliJ IDEA can be found in root directory of this repository. If you are using Eclipse then
you must use [libGDX Eclipse formatter](https://github.com/kotcrab/libgdx/blob/master/eclipse-formatter.xml). 

Remember to don't use Eclipse formatter on existing source code because it isn't fully compatible with the IntelliJ IDEA formatter used in this repository. It may change other irrelevant source code and if you decide to make pull request later it will be harder to review.

To install formatter in Eclipse simply import xml file from settings window.

To install formatter in IntelliJ IDEA copy xml to config directory, restart IDE, then select formatter from settings.  
Mac OS X: `~/Library/Preferences/.IdeaIC15/codestyles/`  
Linux: `~/.IdeaIC15/config/codestyles/`  
Windows: `<User home>\.IdeaIC15\config\codeStyles\`

`.IdeaIC15` directory may be named different depending on your IDEA version

#### Code Style
Please follow [libGDX code style](https://github.com/libgdx/libgdx/blob/master/CONTRIBUTING.md#code-style).

Thanks!
