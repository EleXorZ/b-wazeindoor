package wazeindoor.controller;

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

    @GetMapping
    public List<PointInteret> calculerItineraire(@PathVariable Long espaceId, @RequestParam Long start, @RequestParam Long end) {
        return itineraireService.calculerItineraire(espaceId, start, end);
    }
}
