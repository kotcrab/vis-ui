#### Version: 1.5.8-SNAPSHOT (libGDX 1.14.0)
- Updated to libGDX 1.14.0

#### Version: 1.5.7 (libGDX 1.13.5)
- Updated to libGDX 1.13.5

#### Version: 1.5.6 (libGDX 1.13.1)
- Project is now published using Maven Central Portal instead of OSSRH
  - If you're using snapshots add new repository https://central.sonatype.com/repository/maven-snapshots/
- Source compatibility is now Java 1.8
- **Changed**: [#399](https://github.com/kotcrab/vis-ui/issues/399) - Font color markup is now disabled when drawing text field to prevent crashes 

#### Version: 1.5.5 (libGDX 1.13.1)
- Updated to libGDX 1.13.1
- **Improved**: [#396](https://github.com/kotcrab/vis-ui/issues/396) - Added missing Czech diacritics characters
  - This required re-rendering the font, it was last done 8 years ago. I'm pretty sure I used same Hiero version as before,
    but I'm guessing newer JDK has some changes. Font baseline is affected by this, but I think it looks better now.
    Your UI will have slightly different paddings, see the issue for screenshots 
- **Added**: [#394](https://github.com/kotcrab/vis-ui/issues/394) - `VisImageTextButton` now supports focus font colors

#### Version: 1.5.4 (libGDX 1.12.1)
- **Changed**: [#393](https://github.com/kotcrab/vis-ui/pull/393) - Removed use of Apple Java extensions in `FileUtils`

#### Version: 1.5.3 (libGDX 1.12.1)
- Updated to libGDX 1.12.1

#### Version: 1.5.2 (libGDX 1.12.0)
- Updated to libGDX 1.12.0
- **Added**: [#373](https://github.com/kotcrab/vis-ui/issues/373) - `VisImageTextButton` an optional `Orientation` value can be set to change how the button label is positioned relative to the button image
  - Defaults to existing behavior (label to the right of image in the same row), and orientation can be changed via `VisImageTextButton.setOrientation()`

#### Version: 1.5.1 (libGDX 1.11.0)
- Updated to libGDX 1.11.0
- **Added**: [#374](https://github.com/kotcrab/vis-ui/issues/374) - `ToastManager` now supports 'center' horizontal alignment for 'top' and 'bottom' alignments.
  - `ToastManager.updateToastsPositions` and `ToastManager` member variables are now protected, allowing further customization of the toast positions.

#### Version: 1.5.0 (libGDX 1.10.0)
- Updated to libGDX 1.10.0
- **Removed deprecated API**: `VisUI.VERSION` field
- **Changed**: The project now uses Java 1.7 compatibility level, similarly to libGDX 1.10.0
- **Fixed**: [#355](https://github.com/kotcrab/vis-ui/issues/355) - Changing color of `VisImageButton` and `VisImageTextButton` did not work
- **Fixed**: [#357](https://github.com/kotcrab/vis-ui/issues/357) - `CollapsibleWidget` rendering issues when placed inside a scroll pane
- **Fixed**: [#358](https://github.com/kotcrab/vis-ui/issues/358) - `CollapsibleWidget` performance issue

#### Version: 1.4.11 (libGDX 1.9.14)
- Some changes have been made to simplify VisUI release process
   - **API Deprecated**: `VisUI.VERSION`, this field is no longer updated and will be removed in future versions
   - New VisUI USL file (in `usl/styles` directory) will be created only when breaking changes have to be made to the style definitions

#### Version: 1.4.10 (libGDX 1.9.14)
- Updated to libGDX 1.9.14

#### Version: 1.4.9 (libGDX 1.9.12)
- **Changed**: [#350](https://github.com/kotcrab/vis-ui/pull/350) - Improve `CollapseAction` to support custom duration and interpolation
- **Fixed**: `IllegalArgumentException` thrown by `MenuItem#getImageCell` when `MenuItem` was created without image ([reported indirectly here](https://github.com/crashinvaders/gdx-texture-packer-gui/issues/98))

#### Version: 1.4.8 (libGDX 1.9.12)
- Updated to libGDX 1.9.12

#### Version: 1.4.7 (libGDX 1.9.11)
- **Added**: [#335](https://github.com/kotcrab/vis-ui/issues/335) - `VisImageButton (Drawable imageUp, Drawable imageDown, Drawable imageChecked, String styleName)` constructor
- **Added**: [#340](https://github.com/kotcrab/vis-ui/issues/340) - Option to disable color picker preview, added: `BasicColorPicker#setShowColorPreviews(boolean)`, `BasicColorPicker#isShowColorPreviews` 
- **Added**: [#333](https://github.com/kotcrab/vis-ui/issues/333) - `FlowGroup` a generalization of `HorizontalFlowGroup` and `VerticalFlowGroup`
   - `FlowGroup` fixes some layout issues, see linked pull request
   - `HorizontalFlowGroup` and `VerticalFlowGroup` are deprecated
   - To avoid breaking changes `DragPane` and `TabbedPane` implementation was not changed in this version
- **Fixed**: [#331](https://github.com/kotcrab/vis-ui/issues/331)
   - `TabbedPane::removeAll` does not clear the active tab field
   - `TabbedPane::remove(Tab)` does not clear the active tab field when it removes last tab
- **Fixed**: [#336](https://github.com/kotcrab/vis-ui/issues/336) - `VisTextField` crash when text has newlines
- **Fixed**: [#339](https://github.com/kotcrab/vis-ui/issues/339) - `HighlightTextArea` was not disabling soft wrapping when using some constructors
- **Fixed**: [#341](https://github.com/kotcrab/vis-ui/issues/341) - `HighlightTextArea` parent alpha was not used when drawing font 

#### Version: 1.4.6 (libGDX 1.9.11)
- Updated to libGDX 1.9.11

#### Version: 1.4.5 (libGDX 1.9.10)
- **Added**: [#328](https://github.com/kotcrab/vis-ui/issues/328) `BasicColorPicker#focusHexField`
- **Fixed**: [#316](https://github.com/kotcrab/vis-ui/issues/316) `BusyBar` grows but does not shrink
- **Fixed**: [#324](https://github.com/kotcrab/vis-ui/issues/324) `Spinner#getSelectorName` returns wrong field
- **Fixed**: [#326](https://github.com/kotcrab/vis-ui/issues/326) Possible crash in `FocusManager`
- **Changed**: [#315](https://github.com/kotcrab/vis-ui/issues/315) Generify `VisTree` to match libGDX implementation
- **Changed**: [#314](https://github.com/kotcrab/vis-ui/issues/314) List `corner` Drawable wasn't set resulting in blank spot when both scrollbars were visible
- **Changed**: [#325](https://github.com/kotcrab/vis-ui/issues/325) `VisTable#addSeparator(true)` will create `Separator` with `vertical` style
   - **Warning**: This might be a breaking change if you're using a custom skin
- **Changed**: `FileChooser` will auto focus selected file text field when added to stage (use `FileChooser.focusSelectedFileTextFieldOnShow` to override this setting)   
- **Skin changes**:
   - **Added style**: `Separator`: `vertical` style

#### Version: 1.4.4 (libGDX 1.9.10)
- Updated to libGDX 1.9.10

#### Version: 1.4.3 (libGDX 1.9.9)
- **Fixed**: Infinite loop in `PopupMenu` when trying to select next (or previous) `MenuItem` using keyboard and menu has no selectable `MenuItem`s.
- **Fixed**: [#307](https://github.com/kotcrab/vis-ui/issues/307) `ArrayIndexOutOfBounds` exception in `VisTextArea` when trying to select text having empty first line
- **Changed**: `PopupMenu` keyboard events will be now treated as handled by scene2d (they won't be passed to application under the stage)
- **Changed**: [#302](https://github.com/kotcrab/vis-ui/issues/302) `ToastManager` now allows to specify X and Y screen padding separately
   - **Added**: `ToastManager#setScreenPaddingX(int)`, `ToastManager#setScreenPaddingY(int)`, `ToastManager#setScreenPadding(int, int)`, `ToastManager#getScreenPaddingX()`
`ToastManager#getScreenPaddingY()`
   - **Deprecated**: `ToastManager#getScreenPadding()`, use either `ToastManager#getScreenPaddingX()` or `ToastManager#getScreenPaddingY()`. 
Now this method will throw `IllegalStateException` when padding X is different than padding Y. 
This should not cause any breaking changes until new API is used. This method will be removed in future versions.

#### Version: 1.4.2 (libGDX 1.9.9)
- Updated to libGDX 1.9.9
- **Fixed**: `TabbedPane` was not removing `Tab` from internal `ButtonGroup` thus preventing tab instance from being garbage collected 

#### Version: 1.4.1 (libGDX 1.9.8)
- **Fixed**: `FileChooser`'s icon provider not working correctly when chooser's selection checkboxes were enabled 
- **Fixed**: [#292](https://github.com/kotcrab/vis-ui/issues/292) removing `Menu` from `MenuBar` causes `IllegalStateException`

#### Version: 1.4.0 (libGDX 1.9.6)
- **Added**: [#259](https://github.com/kotcrab/vis-ui/issues/259) `VisImageTextButton#setGenerateDisabledImage(boolean)` along with getter, added `VisImageButton#isGenerateDisabledImage`
- **Added**: [#260](https://github.com/kotcrab/vis-ui/issues/260) `FileChooser#setDefaultFileName(String)`
- **Added**: [#279](https://github.com/kotcrab/vis-ui/issues/279) `ToastManager(Group)` constructor
- **Fixed**: [#255](https://github.com/kotcrab/vis-ui/issues/255) Custom cursor handling in `VisSplitPane` and `MultiSplitPane`
- **Fixed**: [#262](https://github.com/kotcrab/vis-ui/issues/262) `FileChooser` was not updating selected file list when text was cut or pasted into file name field 
- **Fixed**: `FileChooser` file name suggestion menu was not updating file name field when navigating suggestion list using arrow keys
- **Fixed**: [#273](https://github.com/kotcrab/vis-ui/issues/273) Fixed rare crash when doing undo in VisTextField can cause `IllegalStateException`
- [#252](https://github.com/kotcrab/vis-ui/issues/252) `Spinner` now implements `Disableable`
- **Improved**: [#264](https://github.com/kotcrab/vis-ui/issues/264) Added Turkish diacritics, added currency symbols: €, ¥
- **Changed**: [#272](https://github.com/kotcrab/vis-ui/pull/272) `VisTextField` and subclasses will now use `style.backgroundOver` when field has keyboard focus or mouse is over it (previously it was only shown on mouse over)

#### Version: 1.3.0 (libGDX 1.9.6)
- **Added**: `VisUI#dispose (boolean disposeSkin)`
- Updated to libGDX 1.9.6
- Excluded `AsyncTask` API from GWT compilation

#### Version: 1.2.5 (libGDX 1.9.4)
- **Added**: `AsyncTask` API and `AsyncTaskProgressDialog`
- **Added**: `PopupMenu.removeEveryMenu(Stage)`
- **Added**: `FileChooser#setShowSelectionCheckboxes`
- **Added**: `FileChooser#getIconProvider`
- **Added**: `Spinner#setDisabled(boolean)`, `Spinner#isDisabled()`
- **Added**: `HorizontalCollapsibleWidget`
- **Fixed**: `MultiSplitPane#setSplit` not affecting split values
- **Fixed**: `MultiSplitPane` and `VisSplitPane` default cursor not restored when mouse exited widget bounds when mouse was still on split handle bar 
- **Changed**: Selection of menu item is removed when mouse pointer leaves popup menu structure
- **I18N Changes**:
   - Added `Common` bundle

#### Version: 1.2.4 (libGDX 1.9.4)
- **Added**: `ListSelection#setListener`, `#setProgrammaticChangeEvents` (with getters)
- **Fixed**: `Spinner.TextFieldEventPolicy` is now public (was package-private)
- **Fixed**: `HighlightTextArea` scroll pane not immediately updated after changing text using `setText()`
- **Improved**: [#220](https://github.com/kotcrab/vis-ui/issues/220) when sub menu can't fit on the right side of parent menu, it will be shown on the side that has more available space (before in such case it was always shown on the left side)
- **Improved**: When mouse is moved from sub-menu to parent menu, selection of menu item in sub-menu will be removed.
- **Improved**: [#222](https://github.com/kotcrab/vis-ui/issues/222) Added clipping to BusyBar
- **Skin changes**:
   - **Added style**: `ListViewStyle` - allows to customize `ListView` scroll pane style
   - **Added new icons**: `icon-maximize`, `icon-minimize`, `icon-restore`, `icon-close-titlebar`
   - **Added style**: `VisImageButtonStyle`: `close-titlebar`

#### Version: 1.2.3 (libGDX 1.9.4)
- **Added**: constructor `LinkLabel (CharSequence text, CharSequence url, LinkLabelStyle style)`
- **Fixed**: Spinner could overflow Table cell bounds by 1 pixel
 - Removed `Sizes.spinnerButtonsWidth` and `Sizes.spinnerFieldRightPadding` (no longer needed)
 - Renamed `Sizes.spinnerButtonSize` to `Sizes.spinnerButtonHeight`
- **Skin changes**:
 - Styles that used to reference other style by name (for example `FileChooserStyle` referencing `PopupMenu` style name) now embeds that style directly
   - Changed `String ToastStyle#closeButtonStyleName` to `VisImageButtonStyle ToastStyle#closeButtonStyle`
   - Changed `String FileChooserStyle#popupMenuStyleName` to `PopupMenuStyle FileChooserStyle#popupMenuStyle`
   - Changed `String MenuStyle#openButtonStyleName` to `VisTextButtonStyle MenuStyle#openButtonStyle`
   - For existing JSON files you only need to remove 'Name' postfix from field name, Skin loading mechanism can automatically resolve such references

#### Version: 1.2.2 (libGDX 1.9.4)
- **Fixed**: [#214](https://github.com/kotcrab/vis-ui/issues/214) minus sign not visible in Spinner when value was changed with text field focus
- **Fixed**: When there was not enough space on the right to fully show sub-menu it was appearing in wrong position on the left side.

#### Version: 1.2.1 (libGDX 1.9.4)
- **Fixed**: When using libGDX 1.9.4 message was printed that libGDX version is incorrect. If your project is using 1.9.4 you could safely ignore it.

#### Version: 1.2.0 (libGDX 1.9.4)
- Updated to libGDX 1.9.4

#### Version: 1.1.6 (libGDX 1.9.3)
- **Added**: `MenuBar#setMenuListener`, `MenuBarListener`
- **Changed**: Spinner by default will fire change event after text field has lost focus, this can be changed. See `Spinner#setTextFieldEventPolicy` and `Spinner#TextFieldEventPolicy`.
   - Use `TextFieldEventPolicy.ON_ENTER_ONLY` to preserve old behaviour
- **Changed**: `FileChooser` will auto focus file list scroll pane when added to stage (use `FileChooser.focusFileScrollPaneOnShow` to override this setting)
- **Fixed**: [#207](https://github.com/kotcrab/vis-ui/issues/207) crash when user has placed text field cursor after last letter (possibly on LWJGL backend only)

#### Version: 1.1.5 (libGDX 1.9.3)
- **API Changed**: `VisTextField#setCurosrAtTextEnd` renamed to `setCursorAtTextEnd` (typo)
- **Added**: `Tooltip#getTarget`
- **Added**: `MenuItem` constructors taking style name
- **Changed**: It's now impossible to create `FileTypeFilter` `Rule` without providing at least one extension
- **Changed**: `FileTypeFilter` select box won't be shown when `FileChooser` `SelectionMode` is set to `DIRECTORIES`
- **Changed**: `FileChooser` now can be closed by pressing enter when file name field has focus
- **Changed**: `Dialogs#showOKDialog` can be closed using enter and escape key
- **Changed**: [#176 (comment)](https://github.com/kotcrab/vis-ui/issues/176#issuecomment-237046516) - `FileChooser` path text field will now show end of the path when it's too long
- **Changed**: `FileChooser` will fallback to default directory when `setDirectory` is called with invalid file handle (either non existing path or pointing to file)
   - Fixes possible crash when current directory is removed while it's open in file chooser
   - Removed protected `handleAsyncError`, no longer needed

#### Version: 1.1.4 (libGDX 1.9.3)
- **Added**: `BusyBar` - used to indicate that background work is going on - see `TestBusyBar`
- **Added**: `MultiSplitPane` - similar to `VisSplitPane` but supports multiple widgets at once
- **Added**: `Tooltip.Builder#width()`, `Tooltip#setText(String)`, `Tooltip#getContentCell()`
- **Changed**: `FileChooser` directory listing is now performed on separate thread to prevent application hanging when accessing unresponsive drive
- **Changed**: When `ColorPicker` is canceled previous color is restored after window fade out have been finished to avoid flickering (listeners are not affected by this change)
- **Fixed**: `PopupMenu` with single item is now accessible using keyboard
- **Fixed**: `TabbedPane` unable to move tab to the last position in pane
- **Skin changes**:
   - **Added**: `TabbedPane` added style: `vertical`

#### Version: 1.1.3 (libGDX 1.9.3)
- **API Changed**: `FileChooser.setSaveLastDirectory` is now static and must be called before creating chooser to properly restore saved directory.
 - Last directory will not be saved when user has canceled selection dialog
- **Changed**: `VisWindow#fadeOut` will reset alpha back to 1f after completing action.

#### Version: 1.1.2 (libGDX 1.9.3)
- **Added**: `CursorManager`
- **Added**: `ScrollableTextArea` and `HighlightTextArea` with `Highlighter` API
- **Added**: `VisTextField#setCurosrAtTextEnd()`, `#getProgrammaticChangeEvents()`
- **Added**: `FileChooser` file sorting options available under right click menu
- **Added**: `FileChooser#setSorting(FileSorting)` and `#setSortingOrderAscending` along with appropriate getters
- **Added**: `FileChooser#setSaveLastDirectory` - allows to automatically remember last directory user browsed between app launches, disabled by default
- **Fixed**: VisSplitPane was not restoring default cursor when user dragged pointer outside od pane area 
- **Fixed**: [#188](https://github.com/kotcrab/vis-ui/issues/188) - same instance of `VisDialog` couldn't be closed for the second time using close button 
- **Fixed**: `FileChooser` NPE when user right clicked last file item after deleting all others files
- **Fixed**: `FileChooser` Duplicated instances of same disk could be visible on list when chooser was displayed right after creating
- **Fixed**: [#196](https://github.com/kotcrab/vis-ui/issues/196) - `ColorPicker` sending old color to listener instead of new
- **Changed**: `FileChooser` in save mode with active file type filter rule will automatically append rule extensions if user haven't typed extension or extension was wrong
- **Changed**: `FileChooser` now shows files modified date when using details view mode
- **Changed**: `FileChooser` will no longer show files when selection mode is `DIRECTORIES` - this behaviour can be changed in `DefaultFileFilter`
- **Changed**: When `VisWindow#fadeOut()` is called then window touchable is set to disabled. Additionally keyboard focus is reset if any window child `Actor` owns keyboard focus.
   - This is done to prevent user input after fade out animation has started.
   - After fade out has finished window touchable property will be restored to previous value which was set before fade out started.
- **Skin changes**:
   - **Added**: `VisTextArea` added style: `textArea` - no background drawable and focus border is disabled
   - **Added**: `FileChooserStyle` added `Drawable`: `contextMenuSelectedItem` - used to mark active item in context menu (by default `vis-radio-tick`)
- **I18N Changes**:
   - **FileChooser**: added keys `contextMenuSortBy`, `sortByName`, `sortByDate`, `sortBySize`, `sortByAscending`, `sortByDescending`
- **Misc**: Disabling Android Lint is no longer necessary

#### Version: 1.1.1 (libGDX 1.9.3)
- **Fixed**: NPE in FileChooser crash when navigating to other directory

#### Version: 1.1.0 (libGDX 1.9.3)
- **API Moved**: `JNAFileDeleter` was moved to [vis-ui-contrib](https://github.com/kotcrab/vis-ui-contrib) project
- **API Deprecated**: `FileChooser.setFavoritesPrefsName()` replaced by `FileChooser.setDefaultPrefsName()`
- **API Changed**: GridGroup is now taking float for item size instead of int.
    - **Warning:** There were two constructors `GridGroup (float spacing)` and `GridGroup (int itemSize)`. Constructor taking float spacing was removed. Constructor taking int item size now takes float. 
- **API Changed**: Refactored `FileChoose.FileIconProvider`, new methods added. `#provideIcon` takes `FileChooser.FileItem`, was `FileHandle`
- **API Changed**: Refactored `VisCheckBox`
    - Style was refactored to separate checkbox background and tick drawable (see below for full skin drawables changes)
    - `VisCheckBoxStyle` now extends `TextButtonStyle`, was `CheckBox` (fields was renamed to properly communicate their functions)
    - `getImage()` removed, use `getBackgroundImage()` or `getTickImage()` 
    - `getImageCell()` removed, use `getImageStackCell()`
    - protected `getCheckboxImage()` removed, override `getCheckboxBgImage()` or `getCheckboxTickImage()`
    - `getStyle()` returns `VisCheckBoxStyle`, was `CheckBoxStyle`
- **Added**: default styles for `ImageButton` and `ImageTextButton`. Note: this is only applies to standard scene2d widgets. VisUI widgets equivalents (`VisImageButton`, `VisImageTextButton`) already had them.
- **Added**: `SimpleFormValidator#validate`
- **Added**: `ToastManager`, `Toast`, `ToastTable`
- **Added**: VisTextField read-only mode (`VisTextField#setReadOnly(boolean)`)
- **Added**: `TabbedPane#getUIOrderedTabs()`
- **Added**: `FileChooser#setFavoriteFolderButtonVisible(true)` - FileChooser now can display 'add folder to favorites' button in the toolbar 
- **Added**: `FileChooser#setPrefsName()` 
- **Added**: `FileTypeFilter`, `FileChooser#setFileTypeFilter(...)` 
- **Added**: `MenuItem#getSubMenuIconCell()` and `MenuItem#getShortcutCell()` 
- **Added**: `VisTextField#setEnterKeyFocusTraversal(boolean)`
- **Added**: `VisTextField#setCursorPercentHeight`
- **Added**: `PopupMenuListener`
- **Added**: `PopupMenu#showMenu (Stage stage, Actor actor)`
- **Added**: `ConstantIfVisibleValue`
- **Added**: `Sizes#borderSize`
- **Added**: `Sizes#fileChooserViewModeBigIconsSize`, `fileChooserViewModeMediumIconsSize`, `fileChooserViewModeSmallIconsSize`, `fileChooserViewModeListWidthSize`
- **Changed**: [#169](https://github.com/kotcrab/vis-ui/issues/169) - `TabbedPane#getTable()` returns `TabbedPaneTable` (holds reference to `TabbedPane` and allow to easily get its cells for customization)
- **Changed**: `FileChooser` now tries to maintain selection while rebuilding file list
- **Changed**: `FileChooser` will now select new folder after creating it 
- **Changed**: `FileChooser` will be automatically refreshed when added to `Stage`
- **Changed**: `FileChooser` when typing file names manually suggestion will be showed
- **Changed**: `TabbedPane`'s Tab now can't be dragged using it's close button  
- **Changed**: Synced `VisTextField` ans `VisTextArea` with equivalents of those classes libGDX
- **Changed**: `PopupMenu` now support menu navigation using arrows keys
- **Changed**: `PopupMenu` now optionally takes `Sizes` instance (added constructor `PopupMenu (Sizes sizes, PopupMenuStyle style)`)
- **Removed deprecated API**: `NumberSelector` - replaced by `Spinner`
- **Removed deprecated API**: `Sizes#numberSelectorButtonSize`, `numberSelectorButtonsWidth`, `numberSelectorFieldSize`, `numberSelectorFieldRightPadding`
- **Fixed**: `Sizes.buttonBarSpacing` was ignored by `ButtonBar`
    - **Added**: constructors `ButtonBar(Sizes sizes, String order)` and `ButtonBar(Sizes sizes)`
- **Fixed**: `TabbedPane` layout when no separator image was used. Fixed misc issue with close button style on touch down. 
- **Fixed**: `FileChooser` NPE when error occurred during directory deleting
- **Fixed**: `FileChooser` non empty directories are now deleted correctly when using default `FileChooser` deleter
- **Fixed**: `FileChooser` crash when user manually entered path to file instead of directory
- **Fixed**: `FocusManager` calling `focusLost()` when the widget that was already focused tried to gain focus again
- **Fixed**: `VisSplitPane` was not implementing `hit(...)` which could result in widget that was underneath split pane's handle get touch events  
- **Fixed**: Now it's not possible to call `VisWindow#fadeOut` multiple times
- **Skin changes**:
    - **Changed**: `FileChooserStyle`: added drawable fields: `iconStar`, `iconStarOutline`, `iconRefresh`, `iconListSettings`, `expandDropdown`
    - **Added**: drawable `window-border-bg.9`, `icon-star`, `icon-star-outline`, `icon-refresh`, `icon-list-settings`
    - **Added**: style `BaseToastStyle`
    - **Added**: VisTextField `label` style - if combined with read-only mode allows to create selectable labels
    - **Updated**: `cursor` drawable (`cursor.9.png`)
    - **Removed**: `check-down-on`, `check-down`, `check-on-disabled`, `check-over-off`, `check-over-on`, `radio-down-on`, `radio-down`, `radio-on-disabled`, `radio-over-off`, `radio-over-on`
    - **Added**: `vis-check`, `vis-check-over`, `vis-check-down`, `vis-check-tick`, `vis-check-tick-disabled`, `vis-radio`, `vis-radio-over`, `vis-radio-down`, `vis-radio-tick`, `vis-radio-tick-disabled`
- **I18N Changes**:
    - **FileChooser**: added keys `contextMenuRefresh`, `fileType`, `allFiles`, `changeViewMode`, `viewModeList`, `viewModeDetails`, `viewModeBigIcons`, `viewModeMediumIcons`, `viewModeSmallIcons`
- **Misc**: Added Gradle tasks to package VisUI skin textures and compile USL into JSON (`gradlew :ui:compileSkin`)

#### Version: 1.0.2 (libGDX 1.9.2)
- **Changed**: [#163](https://github.com/kotcrab/vis-ui/issues/163) - When `VisCheckBox` or `VisTextField` is disabled and is marked as invalid then error border won't be drawn. 
- **Changed**: [#163](https://github.com/kotcrab/vis-ui/issues/163) - Added `SimpleFormValidator#setTreatDisabledFieldsAsValid` (and it's getter) - allow to control whether to mark form as invalid when invalid but disabled field is encountered. If set to true then all disabled fields are treated as valid, regardless of their state.
    - Defaults to true! Set to false to preserve old behaviour.
- **API Changed**: `DragListener`: `Draggable` argument was added to each method
- **API Deprecated**: `Sizes#numberSelectorButtonSize`, `numberSelectorButtonsWidth`, `numberSelectorFieldSize`, `numberSelectorFieldRightPadding` replaced by `spinnerButtonSize`. `spinnerButtonsWidth`, `spinnerFieldSize`, `spinnerFieldRightPadding`
- **API Deprecated**: `NumberSelector` - replaced by `Spinner`, `NumberSelector` will be removed in future version
- **Added**: `VisTextField#isTextSelected()`
- **Added**: `VisTextField#clearText()`
- **Added**: `FloatingGroup`
- **Added**: `VisWindow#isKeepWithinParent` and `VisWindow#setKeepWithinParent` 
- **Added**: constructor `VisImage (String drawableName)`
- **Added**: `VisUI.load(String internalVisSkinPath)`
- **Added**: `VisTextField#setIgnoreEqualsTextChange(...)` - see [#165](https://github.com/kotcrab/vis-ui/issues/165)
- **Fixed**: `OptionDialog#set(...)ButtonText` now updates dialog size
- **Fixed**: [#131](https://github.com/kotcrab/vis-ui/issues/131) - fixed issue when copying numbers between `VisTextField`s with `FloatDigitsOnlyFilter` decimal point was lost
- **Fixed**: `ListView#AbstractListAdapter` error on GWT
- **Fixed**: `VisTextField` was changing system cursor when it was disabled
- **Fixed**: [#165](https://github.com/kotcrab/vis-ui/issues/165) - fixed form not refreshed when text field content was changed to the same as before

#### Version: 1.0.1 (libGDX 1.9.2)
- **Added**: `ListView#getListAdapter()`
- **Added**: `ListView#rebuildView()` and `UpdatePolicy.MANUAL`
- **Added**: `Draggable#setDeadzoneRadius`
- **Fixed**: Not being able to resize window with `TabbedPane`
- **Fixed**: `OptionDialog` not modal by default
- **Fixed**: `SimpleListAdapter` not working on GWT
- **Fixed**: `VisCheckBox` focus border appeared was displayed in wrong place when using `Cell#growX()`
- **Changed**: `DragPane`: `LimitChildren` listener now never rejects own children, even when max children amount is achieved.
- **API Changed**: `ListView#getMainTable()` now returns `ListViewTable<ItemT>` instead of `VisTable`
- **API Changed**: Added `ListAdapter.add(ItemT)`

#### Version: 1.0.0 (libGDX 1.9.2)
- **Changed**: `InputValidator` moved to `com.kotcrab.vis.ui.util` package
- **Changed**: `LesserThanValidator#setEquals(boolean)` renamed to `setUseEquals`
- **Changed**: `GreaterThanValidator#setEquals(boolean)` renamed to `setUseEquals`
- **Changed**: `FormInputValidator#validateInput` is now final and can't be overridden
- **Changed**: `FormInputValidator#getLastResult` is now package-private
- **Changed**: `DialogUtils` renamed to `Dialogs`
    - **Changed**: `DialogUtils.properties` is now `Dialogs.properties`
    - **Changed**: `VisUI#setDialogUtilsBundle(...)` is now `VisUI#setDialogsBundle(...)`
    - **Changed**: `VisUI#getDialogUtilsBundle()` is now `VisUI#getDialogsBundle()`
    - **Added**: `showDetailsDialog (Stage stage, String text, String title, String details)`
    - **Added**: `showDetailsDialog (Stage stage, String text, String title, String details, boolean expandDetails)`
- **Changed**: `ErrorDialog` renamed to `DetailsDialog`
    - **Changed**: Constructor `ErrorDialog (String text, String stacktrace)` changed to `DetailsDialog (String text, String title, String details)`
    - **Added**: `DetailsDialog#setDetailsVisible(...)`
    - **Added**: `DetailsDialog#setCopyDetailsButtonVisible(...)`
- **Changed**: `FileChooserText`, `FilePopupMenu` and `ColorPickerText` moved to `internal` subpackages (were not part of public API)
- **Changed**: `FileChooser#getFileDeleter` removed
- **Changed**: `FileChooserListener` was refactored
    - `FileChooserListener#selected(FileHandle)` removed
    - If user can select single file use `SingleFileChooserListener`
    - If user can select multiple files use `StreamingFileChooserListener` or use `FileChooserListener` directly
- **Changed**: `VisTextField#toString()` now returns field text
- **Changed**: `OptionDialog` now extends `VisWindow` (was extending `VisDialog`)
- **Changed**: `OptionDialog` and `InputDialog` now will show buttons in platform dependant order using `ButtonBar`
- **Removed**: Removed all `Tooltip` constructors except those taking style
    - Use `new Tooltip.Builder(...)` eg. `new Tooltip.Builder("Tooltip Text").target(label).build()`
    - **Changed**: constructor `Tooltip (String text)` is now `Tooltip (String styleName)`
    - **Added**: constructor `Tooltip ()` 
    - **Added**: constructor `Tooltip (TooltipStyle)` 
- **Removed**: `SeparatorStyle#vertical`, was not used
- **Removed**: constructor `Separator (boolean vertical)`
- **Added**: `ListView` and `ItemAdapter` API
- **Added**: constructor `TabbedPane(TabbedPaneStyle style, Sizes sizes)`
- **Added**: constructor `VisWindow(String title, String styleName)`
- **Added**: `PrefWidthIfVisibleValue`
- **Added**: `HorizontalFlowGroup` and `VerticalFlowGroup`
- **Added**: `ButtonBar` - convenient class for creating button panels arranged in platform dependant order.
    - `FileChooser`, `ColorPicker` and `Dialogs` will now show buttons in platform dependant order
- **Added**: `LinkLabel`, `VisTextField`, `VisTextArea` and `VisSplitPane` supports system cursors when using LWJGL3 or GWT backend
- **Fixed**: `TabbedPane`: Tab close button too small when using `SkinScale.X2`
- **Fixed**: `TabbedPane`: In vertical mode, tabs buttons were centered instead of being aligned at the top 
- **Removed deprecated API**: `ColumnGroup` (use libGDX's `VerticalGroup`)
- **Skin**: 
    - **Changed**: Color `menuitem-grey` renamed to `menuitem`
    - **Changed**: `TabbedPaneStyle#bottomBar` renamed to `separatorBar`
    - **Removed**: `FormValidatorStyle#colorTransition`, no longer needed. 
        - If `colorTransitionDuration` is set to 0 then transition will be skipped.
    - **Removed**: `SeparatorStyle#vertical`, no longer needed
    - **Added**: Drawables: `grey`, `vis-blue`, `vis-red`
    - **Added**: New `Window` style: `resizable`
- **I18N**:
    - **Changed** Bundle management moved to `Locales` class. Instead of calling `VisUI.setXYZBundle(...)` call `Locales.setXYZBundle(...)`
    - **Removed**: Dialogs bundle entries: yes, no, cancel, ok. Now handled by `ButtonBar` bundle. 

#### Version: 0.9.5 (libGDX 1.7.1)
- **Added**: constructor `SimpleFormValidator(Disableable)`.
- **Added**: `ActorUtils#keepWithinStage(Actor)`.
- **Deprecated**: `ColumnGroup`. Will be removed in future versions. Use libGDX's `VerticalGroup` which supports more features.
- **Fixed**: `BasicColorPicker` palette color selection were flipped.
- **Fixed**: `BasicColorPicker` removed unnecessary right padding.
- **Fixed**: `MenuItem` sub menu could appear outside screen.
- **Fixed**: `DialogUtils`'s `ConfirmDialog` text label was not centered.

#### Old changelog file:
```
[0.9.4] [libGDX 1.7.1]
-Fixed GWT support

[0.9.3] [libGDX 1.7.1]
-API Change: FocusManager.getFocus(Stage) renamed to resetFocus
-API Change: FocusManager.getFocus(Stage, Focusable) renamed to switchFocus
-API Change: GridGroup#getItemSize() removed, use getItemWidth or getItemHeight
-API Change: Moved FileChooser's FavouritesIO to 'com.kotcrab.vis.ui.widget.file.internal' package (isn't VisUI public API)
-API Addition: FocusManager.resetFocus(Stage, Actor)
-API Addition: FileChooser#getCurrentDirectory()
-API Addition: GridGroup#setItemWidth(int), GridGroup#setItemWidth(int), GridGroup#getItemWidth(), GridGroup#getItemWidth()
-API Addition: GridGroup#getItemSize(int width, int height)
-API Addition: TabbedPane#disableTab(Tab tab, boolean disable), TabbedPane#isTabDisabled(Tab)
-API Addition: IntDigitsOnlyFilter
-API Addition: DragPane
-API Addition: VisValue, VisWidgetValue - standard Table Values can be used as lambadas if you are using Java 1.8
-API Addition: PrefHeightIfVisibleValue
-ColorPicker:
 -API Change: added ColorPickerListener#reset (Color previousColor, Color newColor)
 -Rewritten using shaders, huge performance boost, now usable on gwt and low end devices
 -Internal ColorPicker classes moved to `com.kotcrab.vis.ui.widget.color.internal` package (remember that those classes aren't considered as public api)
 -ColorPicker can now be used as embeddable widget, see ExtendedColorPicker and BasicColorPicker
 -Added ColorPickerWidgetStyle used by ExtendedColorPicker and BasicColorPicker
  -Changed ColorPickerStyle, now uses ColorPickerWidgetStyle as composition
 -I18N: Removed entries: "old", "new" (no longer needed)
 -Style: removed fields: alphaBar10px and alphaBar25px, white (no longer needed)
-NumberSelector:
 -API Addition: setMaxLength(int), getMaxLength()
 -Fixed entering negative integer values
 -Fixed NumberSelector text field focus border when using SkinScale.X2
 -When selector loses focus and entered value is bigger than max it will be set to max value, if it's smaller than min it will be set to min value
  -Previous behaviour was to restore last valid value
-Trying to use PopupMenu.add(Actor) with MenuItem will throw an exception (MenuItems must be added using addItem method)
-ColorPickerStyle now extends WindowStyle
-Fixed GridGroup too high when total items width in single row was equals to group width
-GridGroup now supports setting item width and height separately
-TabbedPane tabs order can be changed by mouse dragging
-Skin changes:
 -Removed drawables: alpha-bar-10px, alpha-bar-25px
-VisUI is now supported by LML (templates for scene2d.ui with HTML-like syntax), https://github.com/czyzby/gdx-lml-vis

[0.9.2] [libGDX 1.7.1]
-API Change: VisValidatableTextField#getValidator() removed, use getValidators() instead
-API Change: Removed constructors FileChooser taking I18NBundle
 -This way inconsistent with other widgets that did not support this, use VisUI class for setting global I18NBundles
-API Change: NumberSelector now supports float values, methods taking and returning integers are now using floats
-API Change: NumberSelectorListener#changed(int) is now NumberSelectorListener#changed(float)
-API Change: VisProgressBar and VisSlider now extends standard scene2d.ui ProgressBar and Slider (should not affect existing code)
 -VisSliderStyle was removed, use SliderStyle which is fully compatible with VisSliderStyle
-API Change: changed ColorPickerListener#canceled () to ColorPickerListener#canceled (Color oldColor)
-API Change: added ColorPickerListener#changed (Color newColor)
 -If you are using ColorPickerAdapter this does not affect you
-API Change: removed constructor LinkLabel (CharSequence text, String fontName, String colorName) because it was misleading
             with LinkLabel (CharSequence text, CharSequence url, String styleName)
-API Addition: NumberSelector#setPrecision
-API Addition: NumberSelector(String name, float initialValue, float min, float max, float step, int precision)
-API Addition: NumberSelector(String styleName, String name, float initialValue, float min, float max, float step, int precision)
-API Addition: NumberSelector(NumberSelectorStyle style, Sizes sizes, String name, float initialValue, float min, float max, float step, int precision)
-API Addition: InputDialog#setText(String text), InputDialog#setText(String text, boolean selectAll)
-API Addition: FileChooser#setSelectedFiles (FileHandle... files)
-API Addition: FloatDigitsOnlyFilter
-API Addition: VisUI.isLoaded()
-API Addition: constructor VisValidatableTextField (String text, String styleName)
-API Addition: constructor VisValidatableTextField (String text, VisTextFieldStyle style)
-API Addition: CollapsibleWidget#setTable()
-API Addition: VisWindow#setCenterOnAdd(boolean)
-API Addition: ColorPicker#setAllowAlphaEdit (boolean allowAlphaEdit)
-API Addition: ColorPicker#isDisposed()
API Addition: VisCheckBox.setStateInvalid(boolean) and VisCheckBox.isStateInvalid()
 -VisCheckBox and VisRadioButton can now be marked as invalid (error border will be drawn around them)
-CollapsibleWidget now supports creation without setting initial table
-Added default style for standard scene2d.ui TextTooltip
-Added VisTextField and TextArea "small" style with smaller font
-LinkLabel now uses LinkLabelStyle
-LinkLabel now has link underline on mouse over
-SimpleFormValidator (note that following also applies to FormValidator):
 -API Addition: constructor SimpleFormValidator (Disableable targetToDisable, Label messageLabel, String styleName)
 -API Addition: constructor SimpleFormValidator (Disableable targetToDisable, Label messageLabel, FormValidatorStyle style)
 -API Addition: SimpleFormValidator#setMessageLabel(), and #setSuccessMessage
 -API Addition: SimpleFormValidator#addDisableTarget(Disableable) and SimpleFormValidator#removeDisableTarget(Disableable)
 -Now using FormValidatorStyle: allows to set color for message label when form is valid or invalid
  -Two built-in styles: 'default' and 'smooth' with smooth transition between colors
 -Any object implementing Disableable interface may be passed to FormInputValidator as target to disable if form is invalid
 -Multiple Disableable targets are supported
 -Added CheckBox support (checking if checkbox is checked/unchecked)
-TextField/VisTextField.setMessageText(String text) now works properly.
-DialogUtils methods now return dialogs objects (was void)
-Focus border can now be disabled on all widgets having it
-Fixed VisTree taking VisTextField focus when it was placed inside tree node
-Fixed invalid key up/down events propagation when using multiple input processors
-Fixed font kerning for character pairs: Ma, Me, Mi
-Fixed TabbedPane listener called multiple times when user was switching current tab
-Fixed TabbedPane close tab buttons styles
-Fixed 'directory' validator in FormValidator
-Added libGDX version check before loading VisUI, in case of version mismatch warning will be printed to console
 -Can be suppressed by VisUI.setSkipGdxVersionCheck(true);

[0.9.1] [libGDX 1.7.0]
-Updated to libGDX 1.7.0

[0.9.0] [libGDX 1.6.5]
-API Change: Renamed VisValidableTextField to VisValidatableTextField (fixes typo in name)
-API Change: MenuItem#getShortcut() returns CharSequence (was String)
-API Addition: ColorPicker#setCloseAfterPickingFinished(boolean)
-Now Tab#onHide() is called before Tab#dispose()
-VisValidatableTextField ChangeEvent is now fired after input validation
 -Fixes bug with input dialog with validator, it was possible to enter invalid value
-Fixed MenuItem not properly updated after changing menu shortcut when item was already added to PopupMenu
-Fixed MenuItem not using bigger icons when using SkinScale.X2
-FileChooser:
 -Added tooltips for back, forward, and parent directory buttons
 -Added "New directory" button next to path field
 -Added popup menu icons
 -DefaultFileFilter class is now public so it's possible to extend it
 -If your project uses JNA library you can enable moving files to trash instead of deleting them permanently (chooser.setFileDeleter(new JNAFileDeleter()))
 -In file view added icons for common file types: text, images, audio and pdf. Custom icons can be supplied by setting FileIconProvider (see chooser.setIconProvider(...))
 -I18N changes: added entries: back, forward, parentDirectory, newDirectory, popupDeleteFileFailed, contextMenuMoveToTrash, contextMenuMoveToTrashWarning
 -Skin changes: added iconFolderNew, iconFolderStar, iconTrash, iconFileText, iconFileImage, iconFilePdf, iconFileAudio

[0.8.2] [libGDX 1.6.4]
-API Addition: VisValidableTextField#restoreLastValidText()
-API Addition: OsUtils.isAndroid(), OsUtils.getAndroidApiLevel(), OsUtils.isIos(), OsUtils.getShortcutFor(int... keycodes)
-API Addition: MenuItem#setShortcut(int... keycodes)
-API Addition: VisSplitPane#getFirstWidgetBounds(), VisSplitPane#getSecondWidgetBounds()
-API Addition: NumberSelector#setProgrammaticChangeEvents(boolean), NumberSelector#setValue(int value, boolean fireEvent)
-API Addition: NumberSelector#removeChangeListener(...)
-API Change: Removed MenuItem#setShortcut(int modifier, int keycode)
-API Change: FileUtils.isMac(), FileUtils.isUnix() and FileUtils.isWindows() moved to OsUtils
-Added ColumnGroup
-Fixed MenuItem shortcut label color when MenuItem is disabled
-If user clicks mouse before Tooltip appears, Tooltip won't be showed
-Fixed issue with GridGroup in ScrollPane - scroll bar appeared too late
-Fixed GWT compilation issues
-PopupMenu is now kept inside stage when displaying it
-FileChooser
 -API Change: Removed FileChooser#setVisble(boolean) (typo in name), use FileChooser#setVisible(boolean)
 -API Change: Renamed: setGroupMultiselectKey to setGroupMultiSelectKey, getGroupMultiselectKey to getGroupMultiSelectKey,
                       setMultiselectKey to setMultiSelectKey, getMultiselectKey to getMultiSelectKey
                       setMultiselectionEnabled to setMultiSelectionEnabled, isMultiselectionEnabled to isMultiSelectionEnabled
 -Fixed issue with very slow chooser creation on computers with floppy disk drivers installed ( https://github.com/kotcrab/vis-ui/issues/11#issuecomment-136892177 )
 -Fixed crash in when user tried to use history buttons for no longer existing directory
 -Fixed multiple selection when selection mode was set to FILES or DIRECTORIES
 -Added support for the back and forward mouse button for navigating in the history
 -I18N
  -added directoryNoLongerExists
  -added missing entries: newDirectoryDialogTitle, newDirectoryDialogText

[0.8.1] [libGDX 1.6.4]
-Updated libGDX to 1.6.4
-Error dialog from DialogUtils with exception will now show stacktrace from nested exceptions
-Skin change: ColorPickerStyle alphaBar25pxShifted removed (no longer necessary)
-Fixed VisSelectBox list elements padding
-Added skin in higher resolution (can be loaded by new method: VisUI.load(SkinScale.X2))
 -SVG file is also available thanks to piotr-j (https://github.com/piotr-j)
-Removed VisUI.getDefaultSpacingTop/Bottom/Right/Left and VisUI.setDefaultSpacingTop/Bottom/Right/Left
 -Replaced by Sizes class ( VisUI.getSizes() )
 -TableUtils.setSpacingDefault now properly uses all spacings if set
-OptionDialog (DialogUtils.showOptionDialog(...)) message label is now by default center aligned.
-Fixed bug in ColorPicker: pasting hex value was changing picker old color

[0.8.0] [libGDX 1.6.3]
-API Addition: Tooltip (Actor target, String text, int textAlign)
-API Addition: Tooltip (String styleName, Actor target, String text, int textAlign)
-API Addition: FormInputValidator#hideErrorOnEmptyInput (can be used with FromValidator to don't display error
               message if field is empty, field will be still marked with red border and accept button will be disabled)
-API Addition: FormValidator#directory(...)
-API Addition: FormValidator#directoryEmpty(...)
-API Addition: FormValidator#directoryNotEmpty(...)
-Optimized FileChooser ( https://github.com/kotcrab/vis-ui/issues/11 )
-Fixed issue when FileChooser confirm button text wasn't updated after changing mode ( https://github.com/kotcrab/vis-ui/pull/14 )
-Fixed issue when FileChooser would crash on file delete dialog
-Added 'blue' button style
-Added New Directory item in FileChooser popup menu ("contextMenuNewDirectory" was added to FileChooser I18N file)
-FileChooser I18N file: added contextMenuNewDirectory, newDirectoryDialogIllegalCharacters, newDirectoryDialogAlreadyExists properties
-Improved small font (some uppercase letters were missing 1px at the top)

[0.7.7] [libGDX 1.6.1]
-uiskin.json is now generated from USL (see USL page on GitHub Wiki, if you are not writing custom VisUI skins this does not affect you)
-API Addition: various getters and setters in NumberSelector
-API Addition: TabbedPane#getActiveTab
-API Change: Menu#selectButton(TextButton) and deselectButton(TextButton) no longer public, they wasn't part of public API
-Added 'navigate to parent directory' button in FileChooser
-FileChooser now displays "Computer" instead of "/" in partitions list (also added 'computer' entry in FileChooser I18N file)
-FileChooser: improved history (back and forward button)
-FileChooser now can will automatically updates drives list after connecting/removing drive, usb stick etc.
-FileChooser now will refresh files list after some files were changed in current directory
-FileChooser I18N: added property: popupSelectedFileDoesNotExist. Removed: popupOk, popupYes, popupNo (replaced by DialogUtils)
-Added some constructors that allows to use widgets without depending on VisUI.getSkin()
-Fixed infinite key repeat bug on Android in VisTextField ( https://github.com/kotcrab/vis-ui/issues/9 )
-Fixed small gap with empty title in NumberSelector
-Fixed issue where FileChooser file list wasn't rebuilt after setting new file filter
-Fixed issue where NumberSelector won't allow to enter value if min is greater than 0 ( related to https://github.com/kotcrab/vis-ui/issues/7 )
-Fixed closeOnEscape() with multiple windows (windows were closed in improper order) ( https://github.com/kotcrab/vis-ui/issues/10 )
-Fixed invalid title align in VisWindow after adding close button when title align is not set to center
-Fixed issue with disappearing MenuItem after opening PopupMenu while holding right mouse button and dragging down ( https://github.com/kotcrab/vis-ui/commit/a17e309b980b5d0db061a315685501e405811ff6 )
-FileChooser, ColorPicker, Tooltip, Menu and MenuBar now can use styles defined in skin file
-ColorPicker now supports I18N (added VisUI.setColorPickerBundle(I18NBundle))

[0.7.6] [libGDX 1.6.0]
-Updating to libGDX 1.6.0

[0.7.5] [libGDX 1.5.6]
-Added VisImageTextButton

[0.7.4] [libGDX 1.5.6]
-API Addition: NumberSelector#setValue(int)
-After adding close button to VisWindow, title label will be automatically centered if noting else was added to title table

[0.7.3] [libGDX 1.5.6] [POM invalid, don't use]
-Updating to libGDX 1.5.6
-Fixed input bug in VisTextField
-Tooltip now can be created without setting target

[0.7.2] [libGDX 1.5.5] [POM invalid, don't use]
-API Addition: NumberSelector (String name, int initialValue, int min, int max)
-API Addition: VisUI.load(Skin)
-API Addition: GridGroup
-API Addition: LinkLabel
-API Addition: VisValidableTextField#setRestoreLastValid(boolean)
-API Addition: VisTextButton (String, ChangeListener)
-API Addition: VisTextButton (String, String, ChangeListener)
-API Addition CollapsibleWidget.setCollapsed (boolean collapse, boolean applyAnimation) to change collapse state without animation
-Fixed menu not closing after clicking it on MenuBar
-Fixed submenu visible for disabled MenuItem
-Fixing some Tooltip problems, Tooltip now won't fade away when user has it mosue over it
-Better padding on VisList/List item (default list style selection drawable now uses 'padded-list-selection.9')
-New default favoritesPrefsName is com.kotcrab.vis.ui.widget.file.filechooser_favorites (was pl.kotcrab.vis.ui.widget.file.filechooser_favorites)
-Now waring will be printed to console if using default favorites preference name (see FileChooser.setFavoritesPrefsName(String))
-Improving text field (faster input while holding key)

[0.7.1] [libGDX 1.5.5]
-Fixing GWT compatibility

[0.7.0] [libGDX 1.5.5]
* Renaming:
   Validators.integers renamed to Validators.INTEGERS
   Validators.floats renamed to Validators.FLOAT
   PopupMenu#displayMenu renamed to PopupMenu#showMenu
   Skin Change: Separator 'height' renamed to 'thickness'
* Moving classes / reorganizing:
   VisTable moved to com.kotcrab.vis.ui.widget package
   DialogUtils moved to com.kotcrab.vis.ui.util.dialog package
   OptionDialogListener and OptionDialogAdapter moved to com.kotcrab.vis.ui.util.dialog package
   InputDialogListener and InputDialogAdapter moved to com.kotcrab.vis.ui.util.dialog package
   FormValidator, SimpleFormValidator, FormInputValidator moved to com.kotcrab.vis.ui.util.form package
   BasicFormValidator renamed to SimpleFormValidator
   TableUtils.setSpaceDefaults renamed to TableUtils.setSpacingDefaults
* Menu system changes:
   Submenus are now supported
   Removed PopupMenu constructors taking boolean, now auto remove is always enabled. Now when user has clicked MenuItem then PopupMenu will be removed from stage
   MenuBar constructor doesn't take Stage argument anymore
   Added: MenuItem#setSubMenu(PopupMenu subMenu)
   Added: MenuBar#addMenu(Menu menu) MenuBar#removeMenu(Menu menu) MenuBar#insertMenu(int index, Menu menu)
   Added: MenuBar#closeMenu()
   Skin Change: MenuItem widget now uses MenuItemStyle (used TextButtonStyle). MenuItemStyle extends TextButtonStyle. MenuItemStyle adds submenu icon.
* Skin changes:
   Separator style now has 'vertical' property
   Added VisSplitPane handleOver property
* Other API changes:
   API Change: Constructor Separator(boolean useMenuStyle) is now Separator(boolean vertical). Use 'new Separator("menu")' for menu styled separator
   API Change: FileChooserLocale removed, now using libGDX's I18NBundle, see FileChooserText class
   API Change: VisWindow#getButtonTable deprecated, instead use VisWindow#getTitleTable
   API Change: VisDialog#getButtonTable deprecated, instead use VisDialog#getButtonsTable
   API Change: Removed FileChooser.getFavoritesPrefsName()
* Other API additions:
   Added New FormValidators: integerNumber, floatNumber, valueLesserThan, valueGreaterThan
   Added VisTable#addSeparator (boolean vertical)
   Added constructor VisLabel (CharSequence text, LabelStyle style)
   Added constructor VisLabel (CharSequence text, int alignment)
   Added TableBuilder and its implementations: StandardTableBuilder, CenteredTableBuilder, GridTableBuilder, OneColumnTableBuilder, OneRowTableBuilder
* Other changes:
   VisTree/Tree now have default mouse over drawable
   Added shift selection for FileChooser (key can be changed by FielChooser#setGroupMultiselectKey(...))
   Tooltip is now kept within Stage border
   Focus border is now optional for every widget that was using it
   Font support for Polish, German, Spanish, French, Greek and Russian characters
   FormInputValidator now uses validate(String) instead of validateInput(String) for input validation, calling setResult is no longer required
    (for examples on how to use it see SimpleFormValidator)
   Added ValidatorWrapper that allows standard validator to be used with (Simple)FormValidator#custom(...)
   Added DialogUtils.showConfirmDialog(...)
   Added TabbedPane

[0.6.1] [libGDX 1.5.4]
-Fixed FileChooser disappearing when removing favourite

[0.6.0] [libGDX 1.5.4]
-API Change: VisUI.skin is not private, use VisUI.getSkin() instead
-API Addition: DialogUtils.showOptionDialog (Stage stage, String title, String text, OptionDialogType type, OptionDialogListener listener)
-API Addition: FormValidator.fileExists (VisValidableTextField field, VisTextField relativeTo, String errorMsg, boolean errorIfRelativeEmpty)
-API Addition: ColorPicker
-API Addition: ColorUtils
-API Addition: VisImage
-API Addition: constructor VisLabel (CharSequence text, Color textColor)
-Changed close button style name to 'close-window' (was 'close')
-Added 'close' button style that matches other normal buttons
-Fixed focus traversing when TAB pressed in VisTextField, doesn't change focus to invisible fields and doesn't leaves modal windows
-Added built-in validators: IntegerValidator, FloatValidator, GreaterThanValidator, LesserThanValidator (see Validators class)
-Added VERSION string constant in VisUI

[0.5.1] [libGDX 1.5.3]
-Added cancelable input dialog in DialogUtils

[0.5.0] [libGDX 1.5.3]
-API Addition: VisValidableTextField.setValidationEnabled(boolean)
-API Addition: VisValidableTextField.isValidationEnabled()
-API Addition: VisValidableTextField.setProgrammaticChangeEvents(boolean)
-API Addition: constructor VisImageButton (String styleName)
-API Addition: constructor VisCheckBox (String text, boolean checked)
-API Addition: VisWindow.addCloseButton()
-API Addition: VisWindow.closeOnEscape()
-API Addition: VisTextField.focusField()
-API Addition: MenuItem.getShortcut()
-API Addition: DialogUtils.showInputDialog (Stage stage, String title, String fieldTitle, InputDialogListener listener)
-API Addition: DialogUtils.showInputDialog (Stage stage, String title, String fieldTitle, InputValidator validator, InputDialogListener listener)
-API Change: VisUI.setDefaultTitleAlign and VisUI.getDefaultTitleAlign (typo fixed)
-API Change: Removed deprecated TableUtils.setColumnsDefaults(Table)
-Separator style "menu" height changed to 3px (was 4px), that means PopupMenu separator height is now 3px as well
-File chooser now have close button in top right corner
-File chooser now closes when escape key has been pressed
-Fixed bug where VisValidableTextField would loss focus if user type something and field don't have ChangeListener attached
-Fixed focusing next field when TAB key is pressed inside VisTextField
-Added Tooltips
-Moved TableUtils to com.kotcrab.vis.util package (sorry!)

[0.4.1] [libGDX 1.5.2]
-Fixed FileChooser padding when scrollbar was showed (because libGDX scrollpane was probably fixed as well) 

[0.4.0] [libGDX 1.5.2] [Important]
-Important: Moving everything to com.kotcrab.vis package, new Gradle definitions:
 in core: com.kotcrab.vis:vis-ui:$visuiVersion
 in html: com.kotcrab.vis:vis-ui:$visuiVersion:sources
 Also don't forget to update your GdxDefinition.gwt.xml and GdxDefinitionSuperdev.gwt.xml files!

[0.3.1] [libGDX 1.5.0]
-Added CollapsibleWidget
-Fixed VisImageButton.setGenerateDisabledImage(boolean)
-Fixed MenuBar not rendering Menu content if added Menu to MenuBar after adding items to it
-VisTable.addSeparator() now sets expandX() and fillX() for separator instead of expand() and fill()
-GWT compatibility for DialogUtils and FormValidator
-Fixed VisCheckBox and VisRadioButton focus border padding

[0.3.0] [libGDX 1.5.0]
-libGDX dependency version changed to 1.5.0
-Font size changed to 15
-Separator width changed to 4px, split pane bar width/height changed to 4px
-Removed markup font
-API Change: Removed deprecated resize() from MenuBar
-API Change: New MenuItem constructors which takes Image instead of Drawable, removed MenuItem (String text, Drawable image, String styleName)
-API Change: FormValidator.fileExist(...) -> FormValidator.fileExists(...)
-API Change: VisTable.addSeparator() returns Cell<Separator> instead of void
-API Addition: FileChooser(FileChooserLocale, Mode)
-API Addition: MenuItem (String, ChangeListener)
-API Addition: VisUI.setDefualtTitleAlign(int align) 
-API Addition: VisUI.getDefualtTitleAlign()
-API Addition: FormValidator.fileExist(VisValidableTextField field, File relavtiveTo, String errorMsg)
-API Addition: FormValidator can also take FileHandle when using file(Not)Exist relativeTo method 
-API Addition: Added VisTextField.isFocusBorderEnabled() and VisTextField.setFocusBorderEnabled(boolean)
-API Addition: Added FormValidator.fileNotExist(...) methods
-API Addition: Added FormValidator.custom (VisValidableTextField field, FormInputValidator customValidator)
-API Addition: VisSplitPane.setWidgets (Actor firstWidget, Actor secondWidget)
-Fixed bug when FormValidator doesn't updated all fields borders after changes in other field
-FileChooser deselects all files when reopened
-FileChooser: When clicked on drive shortcut file scroll pane table will get focus automaticly
-FileChooser: When sorting file list chooser now ignores uppercase/lowercase
-Fixed problem where Separator didn't set color before rendering
-Fixed look of disabled MenuItem, if MenuItem has an image and it is disabled, image color will be set to Color.GRAY. 
 This can be disabled by calling item.setGenerateDisabledImage(false)

[0.2.0] [libGDX 1.4.1]
-API change: removed Stage from VisWindow and VisDialog constructors
-API change: removed VisWindow.setPositonToCenter() replaced with VisWindow.centerWindow()
-API change: VisImageButton.setGeneateDisabledImage() -> VisImageButton.setGenerateDisabledImage() (typo)
-Fixed findNextTextField in VisTextField
-Increased default bottom padding from 6 to 8
-Calling MenuBar.resize() no longer required
-When FileChooser is in SelectionMode.DIRECTORIES, none directory is selected, and finish button was clicked, current directory will be selected
-VisValidableTextField will validate input on setText() and fire ChangeEvent
-VisValidableTextField.validateInput() method is now public
-Added SeparatorStyle class
-Added PopupMenu.addSeparator()
-Added FileUtils.toFileHandle(File file)
-Added FormValidator
-Added VisValidableTextField()
-Added VisValidableTextField(InputValidator validator)
-Added VisValidableTextField(String text)
-Added VisLabel()
-Added VisValidableTextField.getValidator()
-Added VisValidableTextField.getValidators()
-Added MenuItem(String text, Drawable image)
-Added DialogUtils

[0.1.1] [libGDX 1.4.1]
-Updated menu bar look

[0.1.0] [libGDX 1.4.1]
-API change: VisValidableTextField#addValidable -> VisValidableTextField#addValidator
-Added: VisTextField#isEmpty()
-Added: VisTable#addSeparator()
-Added: VisWindow(String title)
-Added: VisTextButton(String text, VisTextButtonStyle buttonStyle)
-Added FileChooser (Desktop only)
-Added fadeOut(), fadeIn() to VisWindow
-Added VisImageButton
-Added VisDialog
-Added PopupMenu
-Disabling button will remove its focus
-VisWindow can be created with border or without it
-MenuItem can be created with icon
-Fixed horizontal scroll pane slider not fully visible
-Fixed fade out animation not worked on VisSplitPane

[0.0.3] [libGDX 1.4.1]
-API change: Renamed 'components' package to 'widget' (sorry!)
-Added VisValidableTextField with InputValidator
-Better CheckBox text padding

[0.0.2] [libGDX 1.4.1]
-Added GWT compatibility

[0.0.1] [libGDX 1.4.1]
-Initial release
```
