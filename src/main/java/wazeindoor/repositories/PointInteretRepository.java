package wazeindoor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wazeindoor.entity.PointInteret;

import java.util.List;
import java.util.Optional;

public interface PointInteretRepository extends JpaRepository<PointInteret, Long> {
    List<PointInteret> findByEspaceId(Long espaceId);
    Optional<PointInteret> findByEspaceIdAndId(Long espaceId, Long id);
    long countByIdInAndEspaceId(List<Long> id, Long espaceId);
}
