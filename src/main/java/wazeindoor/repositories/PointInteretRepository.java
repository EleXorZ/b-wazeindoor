package wazeindoor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wazeindoor.entity.PointInteret;

import java.util.List;

public interface PointInteretRepository extends JpaRepository<PointInteret, Long> {
    List<PointInteret> findByEspaceId(Long espaceId);
}
