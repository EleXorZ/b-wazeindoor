package wazeindoor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wazeindoor.entity.PointInteret;
import wazeindoor.services.ItineraireService;

import java.util.List;

@RestController
@RequestMapping("/espaces/{espaceId}/itineraire")
public class ItineraireController {

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

        List<PointInteret> itineraire = itineraireService.calculerItineraireRapide(espaceId, start, waypoints, end);
        return ResponseEntity.ok(itineraire);
    }
}
