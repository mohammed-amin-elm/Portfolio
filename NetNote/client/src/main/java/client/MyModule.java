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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class MyModule implements Module {

    /**
     * Configures bindings for dependency injection.
     * <p></p>
     * This method is used to define how various classes and interfaces are bound
     * to their implementations in the Guice dependency injection framework.
     * Uncomment and replace **CLASS** with the appropriate class or interface to bind.
     * <p></p>
     * Example:
     * <pre>{@code
     * binder.bind(MyService.class).to(MyServiceImpl.class).in(Scopes.SINGLETON);
     * }</pre>
     *
     * @param binder the Guice {@link Binder} to configure bindings
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(PrimaryCtrl.class).in(Scopes.SINGLETON);
        binder.bind(DefaultCtrl.class).in(Scopes.SINGLETON);
        binder.bind(NoteCreationCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EditSelectCollectionCtrl.class).in(Scopes.SINGLETON);
        binder.bind(DefaultCollectionService.class).in(Scopes.SINGLETON);
        binder.bind(NoteEditCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EditCollectionCtrl.class).in(Scopes.SINGLETON);
    }

}