package wazeindoor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wazeindoor.entity.Espace;

public interface EspaceRepository extends JpaRepository<Espace, Long> {}
