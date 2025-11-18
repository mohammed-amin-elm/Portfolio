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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import commons.Note;
import commons.NoteCollection;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;

/**
 * Utility class for server-related operations.
 */
public class ServerUtils {
	private static final String HTTP = "http://";
    private static final String SERVER = "localhost:8080/";

	/**
	 * getter for the server
	 * @return server
	 */
	public String getServer() {
		return SERVER;
	}

	/**
	 * Checks whether the server is available.
	 *
	 * @return {@code true} if the server is reachable, {@code false} otherwise
	 */
	public boolean isServerAvailable() {
		try {
			ClientBuilder.newClient(new ClientConfig()) //
					.target(HTTP + SERVER) //
					.request(APPLICATION_JSON) //
					.get();
		} catch (ProcessingException e) {
			if (e.getCause() instanceof ConnectException) {
				return false;
			}
		}
		return true;
	}




	/**
	 * Get the all the available NoteCollections from the REST endpoint
	 * and returns the data as a List.
	 * @return List of NoteCollections given be the endpoint.
	 */
	public List<NoteCollection> getAllCollections() {
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/collection")
				.request(APPLICATION_JSON)
				.get();

		if (response.getStatus() == 200) {
			return response.readEntity(new GenericType<>() {});
		} else {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}

		return null;
	}

	/**
	 * Get a NoteCollection Object from the REST endpoint and
	 * returns the NoteCollection Object
	 * @param collectionId The id a long of the NoteCollection
	 * @return The NoteCollection Object
	 */
	public NoteCollection getNoteCollectionById(long collectionId) {
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/collection/" + collectionId)
				.request(APPLICATION_JSON)
				.get();

		if(response.getStatus() == 200) {
			return response.readEntity(NoteCollection.class);
		} else {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}
		return null;
	}

	/**
	 * Add a new NoteCollection Object to the database
	 * @param noteCollection The NoteCollection Object to add
	 */
	public void newNoteCollection(NoteCollection noteCollection) {
		var requestBody = Entity.entity(noteCollection, APPLICATION_JSON);
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/collection")
				.request(APPLICATION_JSON)
				.post(requestBody);

		if(response.getStatus() != 200) {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}
	}

	/**
	 * Update a new NoteCollection Object by id in the database
	 * @param collectionId The id of the Object
	 * @param noteCollection The new NoteCollection Object
	 */
	public void updateNoteCollection(long collectionId, NoteCollection noteCollection) {
		var requestBody = Entity.entity(noteCollection, APPLICATION_JSON);
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/collection/" + collectionId)
				.request(APPLICATION_JSON)
				.put(requestBody);

		if(response.getStatus() != 200) {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}
	}

	/**
	 * Delete a NoteCollection Object by id in the database
	 * @param collectionId The id of the Object
	 */
	public void deleteNoteCollection(long collectionId) {
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/collection/" + collectionId)
				.request(APPLICATION_JSON)
				.delete();

		if(response.getStatus() != 200) {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}
	}

	/**
	 * Add a Note to a NoteCollection
	 * @param collectionId id of the NoteCollection
	 * @param note The Note to add
	 */
	public void addNoteToCollection(long collectionId, Note note) {
		NoteCollection noteCollection = getNoteCollectionById(collectionId);
		noteCollection.addNote(note);

		updateNoteCollection(collectionId, noteCollection);
	}

	/**
	 * Get note by id from endpoint
	 * @return The corresponding Note
	 */
	public List<Note> getNotes() {
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/note/")
				.request(APPLICATION_JSON)
				.get();

		if(response.getStatus() == 200) {
			return response.readEntity(new GenericType<>() {});
		} else {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}

		return null;
	}

	/**
	 * Get Note by id
	 * @param noteId id of the Note
	 * @return The Note Object
	 */
	public Note getNote(long noteId) {
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/note/" + noteId)
				.request(APPLICATION_JSON)
				.get();

		if(response.getStatus() == 200) {
			return response.readEntity(Note.class);
		} else {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}

		return null;
	}

	/**
	 * Add a new note to the database
	 * @param note Note Object
	 * @param collectionId id of the NoteCollection
	 */
	public void addNote(Note note, long collectionId) {
		var requestBody = Entity.entity(note, APPLICATION_JSON);
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/note/" + collectionId)
				.request(APPLICATION_JSON)
				.post(requestBody);

		if(response.getStatus() != 200) {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}
	}

	/**
	 * Update a note in the database
	 * @param noteId id of the note
	 * @param newNote The new Note
	 */
	public void updateNote(long noteId, Note newNote) {
		var requestBody = Entity.entity(newNote, APPLICATION_JSON);
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/note/" + noteId)
				.request(APPLICATION_JSON)
				.put(requestBody);

		if(response.getStatus() != 200) {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}
	}

	/**
	 * Delete a note in the database
	 * @param noteId The id of the Note
	 */
	public void deleteNote(long noteId) {
		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/note/" + noteId)
				.request(APPLICATION_JSON)
				.delete();

		if(response.getStatus() != 200) {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}
	}

	/**
	 * Checks if a note collection with the specified title exists in all note collections.
	 *
	 * @param name the title to check for
	 * @return true if a note collection with the specified title exists, false otherwise
	 */
	public boolean containsTitle(String name) {
		if (name == null) return false;
		List<NoteCollection> allNoteCollections = getAllCollections();
		for (NoteCollection noteCollection : allNoteCollections) {
			if (noteCollection.getTitle() == null){
				continue;
			}
			if (noteCollection.getTitle().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to check if a note title is present on the server
	 * @param title the title
	 * @return true or false, if the title is found or not
	 */
	public boolean containsNoteTitle(String title) {
		if (title == null) return false;
		List<Note> notes = getNotes();
		for (Note note : notes)
			if (note.getTitle().equals(title))
				return true;
		return false;
	}

	/**
	 * Checks if a note collection with the specified URL exists in all note collections.
	 *
	 * @param url the URL to check for
	 * @return true if a note collection with the specified URL exists, false otherwise
	 */
	public boolean containsUrl(String url) {
		if (url == null){
			return false;
		}
		List<NoteCollection> allNoteCollections = getAllCollections();
		for (NoteCollection noteCollection : allNoteCollections) {
			if (noteCollection.getUrl() == null) {
				continue;
			}
			if (noteCollection.getUrl().equals(url)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a note collection with the specified internal
	 * name exists in all note collections.
	 *
	 * @param internalName the internal name to check for
	 * @return true if a note collection with the specified internal name exists,
	 * false otherwise
	 */
	public boolean containsInternalName(String internalName) {
		if (internalName == null) {
			return false;
		}
		List<NoteCollection> allNoteCollections = getAllCollections();
		for (NoteCollection noteCollection : allNoteCollections) {
			if (noteCollection.getInternalName() == null) {
				continue;
			}
			if (noteCollection.getInternalName().equals(internalName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The method returns a list containing all the note and
	 * collection titles.
	 * @return The list containing all note and collection titles
	 */
	public List<String> getAllTitles() {

		List<String> allTitles = new ArrayList<>();
		List<String> allNoteTitles = getNotes().stream().
				map(n -> n.getTitle()).
				toList();
		List<String> allCollectionTitles = getAllCollections().stream()
				.map(c -> c.getTitle())
				.toList();

		allTitles.addAll(allNoteTitles);
		allTitles.addAll(allCollectionTitles);
		return allTitles;
	}

	/**
	 * This method returns an optional of the file content. The file content is a html
	 * image tag if the file is an image.
	 * @param noteId The id of the note
	 * @param fileName The name of file
	 * @return an optional containing the file content or not
	 */
	public Optional<String> getFileContent(long noteId, String fileName) {

		var response = ClientBuilder.newClient()
				.target(HTTP + SERVER).path("api/files/notes/" +
						noteId + "/" + fileName)
				.request(APPLICATION_JSON)
				.get();

		if(response.getStatus() == 200) {
			return Optional.of(response.readEntity(String.class));
		} else {
			System.out.println(response.getStatusInfo().getReasonPhrase());
		}

		return Optional.empty();
	}

	/**
	 * finds the collection of a note
	 * @param note the note
	 */
	public void addCollectionToNote(Note note) {
		note.setNoteCollection(
				getAllCollections().stream()
						.filter(n -> n.getNotes().stream()
								.map(Note::getId)
								.toList()
								.contains(note.getId()))
						.findFirst()
						.orElse(null)
			);
	}
}
