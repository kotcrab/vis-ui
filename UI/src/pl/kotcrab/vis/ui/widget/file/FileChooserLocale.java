
package pl.kotcrab.vis.ui.widget.file;

public class FileChooserLocale {
	public String cancel = "Cancel";
	public String fileName = "File name:";
	public String desktop = "Desktop";

	/** Used on confirm button when dialog is in OPEN mode */
	public String open = "Open";
	/** Used on confirm button when dialog is in SAVE mode */
	public String save = "Save";

	public String popupTitle = "Message";
	public String popupChooseFile = "You must choose a file!";
	public String popupOnlyDirectoreis = "Only directories are allowed!";
	public String popupFilenameInvalid = "File name is invalid!";
	public String popupFileExistOverwrite = "This file already exist, do you want to overwrite it?";
	public String popupMutipleFileExistOverwrite = "Those files already exist, do you want to overwrite them?";
	public String popupOK = "OK";
	public String popupYes = "Yes";
	public String popupNo = "No";

	public FileChooserLocale () {
	}

	public FileChooserLocale (String cancel, String open, String save) {
		this.cancel = cancel;
		this.open = open;
		this.save = save;
	}

}
