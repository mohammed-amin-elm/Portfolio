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

/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */
package client;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Injector;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javafx.util.Pair;

public class MyFXML {

    private Injector injector;

    /**
     * Constructs a new {@code MyFXML} instance with the provided {@link Injector}.
     *
     * @param injector the Guice injector used to resolve dependencies
     */
    public MyFXML(Injector injector) {
        this.injector = injector;
    }

    /**
     * Loads an FXML file and injects its controller dependencies.
     *
     * @param bundleName the name of resource Bundle
     * @param <T>   the type of the controller
     * @param c     the class type of the controller
     * @param parts the parts of the path to the FXML file
     * @return a {@link Pair} containing the controller and the loaded {@link Parent}
     * @throws RuntimeException if an I/O error occurs while loading the FXML file
     */
    public <T> Pair<T, Parent> load(String bundleName, Class<T> c, String... parts) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName, Locale.ENGLISH);
        try {
            var loader = new FXMLLoader(
                    getLocation(parts),
                    resourceBundle,
                    null,
                    new MyFactory(),
                    StandardCharsets.UTF_8);

            Parent parent = loader.load();
            T ctrl = loader.getController();
            return new Pair<>(ctrl, parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolves the location of an FXML file based on the provided path parts.
     *
     * @param parts the parts of the path to the FXML file
     * @return the {@link URL} of the FXML file
     */
    private URL getLocation(String... parts) {
        var path = Path.of("", parts).toString();
        return MyFXML.class.getClassLoader().getResource(path);
    }

    /**
     * A factory class for creating instances of FXML controllers and
     * other objects, using Guice for dependency injection.
     */
    private class MyFactory implements BuilderFactory, Callback<Class<?>, Object> {

        /**
         * Creates a builder for the specified type.
         *
         * @param type the type of object to build
         * @return a builder that resolves the object using the Guice injector
         */
        @Override
        @SuppressWarnings("rawtypes")
        public Builder<?> getBuilder(Class<?> type) {
            return new Builder() {
                @Override
                public Object build() {
                    return injector.getInstance(type);
                }
            };
        }

        /**
         * Resolves an instance of the specified type using the Guice injector.
         *
         * @param type the type of object to resolve
         * @return an instance of the specified type
         */
        @Override
        public Object call(Class<?> type) {
            return injector.getInstance(type);
        }
    }
}
