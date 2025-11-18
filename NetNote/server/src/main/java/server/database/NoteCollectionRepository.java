package server.database;

import commons.NoteCollection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteCollectionRepository extends JpaRepository<NoteCollection, Long> {}
