
package pl.kotcrab.vis.sceneeditor.sample.desktop;

import pl.kotcrab.vis.sceneeditor.sample.Samples;
import pl.kotcrab.vis.sceneeditor.sample.scene2d.SampleScene2d;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {

	@SuppressWarnings("unused")
	public static void main (String[] arg) {

		SampleSelector selector = new SampleSelector(new SampleSelected() {
			@Override
			public void sampleSelected (int sampleId) {
				LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
				config.width = 800;
				config.height = 480;

				switch (sampleId) {
				case 0:
					new LwjglApplication(new Samples(false), config);
					break;
				case 1:
					new LwjglApplication(new Samples(true), config);
					break;
				case 2:
					new LwjglApplication(new SampleScene2d(), config);
					break;
				default:
					break;
				}

			}
		});

	}
}
