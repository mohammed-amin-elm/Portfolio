/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.utils.ServerUtils;
import client.utils.ValidationUtils;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

/**
 * Main class that serves as the entry point for the JavaFX application.
 * It extends the {@link Application} class and initializes required dependencies using Guice.
 */
public class Main extends Application {

	private static final Injector INJECTOR = createInjector(new MyModule());
	private static final MyFXML FXML = new MyFXML(INJECTOR);

	/**
	 * The main method serving as the entry point for the JavaFX application.
	 *
	 * @param args the command-line arguments passed to the application
	 * @throws URISyntaxException if there is an error with the URI syntax
	 * @throws IOException if an I/O error occurs during execution
	 */
	public static void main(String[] args) throws URISyntaxException, IOException {
		launch();
	}

	/**
	 * Starts the JavaFX application by setting up the primary stage.
	 *
	 * @param primaryStage the primary stage for this application
	 * @throws Exception if any error occurs during the start process
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		var serverUtils = INJECTOR.getInstance(ServerUtils.class);
		var validationUtils = INJECTOR.getInstance(ValidationUtils.class);

		if (!serverUtils.isServerAvailable()) {
			String msg = "Server needs to be started before the client," +
					" but it does not seem to be available. Shutting down.";
			System.err.println(msg);
			return;
		}

		var defaultScene = FXML.load
				("messages", DefaultCtrl.class, "client", "defaultScene.fxml");
		var noteCreationScene = FXML.load("messages", NoteCreationCtrl.class,
				"client", "createNoteScene.fxml");
		var collectionScene = FXML.load("messages",
				EditSelectCollectionCtrl.class,
		 "client",
				"editSelectCollectionScene.fxml"
		);
		var noteEditScene = FXML.load("messages",
				NoteEditCtrl.class,
				"client",
				"editNoteScene.fxml"
		);
		var collectionEditScene = FXML.load("messages",
				EditCollectionCtrl.class,
				"client",
				"EditCollection.fxml"
		);
		var fileEditScene = FXML.load("messages",
				FileEditCtrl.class,
				"client",
				"fileEditScene.fxml"
		);
		var pc = INJECTOR.getInstance(PrimaryCtrl.class);

		pc.init(
				primaryStage,
				defaultScene,
				noteCreationScene,
				collectionScene,
				noteEditScene,
				collectionEditScene,
				fileEditScene
		);

		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(450);
	}
}