package Application.DataAccessLayer.Repositories;

import Application.Entities.games.GameMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MapRepository extends JpaRepository<GameMap, UUID> {
    
    @Query("""
            SELECT m
            FROM GameMap m
            WHERE m.name LIKE %:name%
            """)
    Page<GameMap> findByName(@Param("name") String name, Pageable pageable);
    
    @Query("""
            SELECT m
            FROM GameMap m
            """)
    Page<GameMap> findBy(Pageable pageable);
}
