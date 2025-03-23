package wazeindoor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wazeindoor.entity.Chemin;
import wazeindoor.entity.Espace;

import java.util.List;

public interface CheminRepository extends JpaRepository<Chemin, Long> {

    List<Chemin> findByPoiDepart_Espace_Id(Long espaceId);

}
