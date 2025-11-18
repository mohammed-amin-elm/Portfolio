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

package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Main class for initializing and starting the Spring Boot application.
 * It serves as the entry point for the application and contains the main method
 * to bootstrap the application using SpringApplication.
 *
 * The class is annotated with @SpringBootApplication, which marks it as a Spring
 * Boot application and triggers auto-configuration and component scanning.
 *
 * The @EntityScan annotation specifies the packages to scan for JPA entities,
 * allowing the application to recognize and manage entity classes located in
 * specified packages such as "commons" and "server".
 */
@SpringBootApplication
@EntityScan(basePackages = { "commons", "server" })
public class Main {

    /**
     * Starts the application
     * @param args Arguments for the main function
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}