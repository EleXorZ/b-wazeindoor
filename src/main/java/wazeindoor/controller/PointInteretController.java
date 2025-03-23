package wazeindoor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wazeindoor.entity.PointInteret;
import wazeindoor.repositories.PointInteretRepository;

import java.util.List;

@RestController
@RequestMapping("/espace/{espaceId}/pois")
public class PointInteretController {

    private final PointInteretRepository pointInteretRepository;

    public PointInteretController(PointInteretRepository pointInteretRepository) {
        this.pointInteretRepository = pointInteretRepository;
    }

    @GetMapping
    public List<PointInteret> getPoisByEspace(@PathVariable Long espaceId) {
        return pointInteretRepository.findByEspaceId(espaceId);
    }
}
