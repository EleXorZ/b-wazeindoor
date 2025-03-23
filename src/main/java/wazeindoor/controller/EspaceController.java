package wazeindoor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wazeindoor.entity.Espace;
import wazeindoor.repositories.EspaceRepository;

import java.util.List;

@RestController
@RequestMapping("/espace")
public class EspaceController {

    private final EspaceRepository espaceRepository;

    public EspaceController(EspaceRepository espaceRepository) {
        this.espaceRepository = espaceRepository;
    }

    @GetMapping
    public List<Espace> getAllEspace() {
        return espaceRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Espace> getEspaceById(@PathVariable Long id) {
        return espaceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
