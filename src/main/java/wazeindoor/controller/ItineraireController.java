package wazeindoor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wazeindoor.entity.PointInteret;
import wazeindoor.services.ItineraireService;

import java.util.List;

@RestController
@RequestMapping("/espaces/{espaceId}/itineraire")
public class ItineraireController {

    private static final Logger logger = LoggerFactory.getLogger(ItineraireController.class);
    private final ItineraireService itineraireService;

    public ItineraireController(ItineraireService itineraireService) {
        this.itineraireService = itineraireService;
    }

    @GetMapping("/distance")
    public List<PointInteret> calculerItineraireByDistance(@PathVariable Long espaceId, @RequestParam Long start, @RequestParam Long end) {
        return itineraireService.calculerItineraireByDistance(espaceId, start, end);
    }

    @GetMapping("/localisation")
    public List<PointInteret> calculerItineraireByLocalisation(@PathVariable Long espaceId, @RequestParam Long start, @RequestParam Long end) {
        return itineraireService.calculerItineraireByLocalisation(espaceId, start, end);
    }

    @GetMapping("/way")
    public ResponseEntity<List<PointInteret>> calculerItineraire(
            @PathVariable Long espaceId,
            @RequestParam Long start,
            @RequestParam List<Long> waypoints,
            @RequestParam Long end) {
        logger.info("Requête reçue pour l'itinéraire dans l'espace {}: start={}, waypoints={}, end={}", espaceId, start, waypoints, end);

        try {
            List<PointInteret> itineraire = itineraireService.calculerItineraireRapide(espaceId, start, waypoints, end);
            logger.info("Itinéraire calculé avec succès pour l'espace {}", espaceId);
            return ResponseEntity.ok(itineraire);
        } catch (Exception e) {
            logger.error("Erreur lors du calcul de l'itinéraire dans l'espace {}: {}", espaceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
