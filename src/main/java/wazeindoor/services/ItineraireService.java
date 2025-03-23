package wazeindoor.services;

import org.springframework.stereotype.Service;
import wazeindoor.entity.Chemin;
import wazeindoor.entity.Espace;
import wazeindoor.entity.PointInteret;
import wazeindoor.repositories.CheminRepository;
import wazeindoor.repositories.EspaceRepository;
import wazeindoor.repositories.PointInteretRepository;

import java.util.*;

@Service
public class ItineraireService {

    private final CheminRepository cheminRepository;
    private final PointInteretRepository pointInteretRepository;
    private final EspaceRepository espaceRepository;

    public ItineraireService(CheminRepository cheminRepository, PointInteretRepository pointInteretRepository, EspaceRepository espaceRepository) {
        this.cheminRepository = cheminRepository;
        this.pointInteretRepository = pointInteretRepository;
        this.espaceRepository = espaceRepository;
    }

    public List<PointInteret> calculerItineraire(Long espaceId, Long poiDepartId, Long poiArriveeId) {
        // Récupération des POIs et des chemins
        List<PointInteret> pois = pointInteretRepository.findByEspaceId(espaceId);
        List<Chemin> chemins = cheminRepository.findByPoiDepart_Espace_Id(espaceId);

        // Création du graphe sous forme de liste d’adjacence
        Map<Long, List<Chemin>> graphe = new HashMap<>();
        for (Chemin chemin : chemins) {
            graphe.computeIfAbsent(chemin.getPoiDepart().getId(), k -> new ArrayList<>()).add(chemin);
        }

        // Initialisation des structures
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> predecesseurs = new HashMap<>();
        PriorityQueue<Long> pq = new PriorityQueue<>(Comparator.comparing(distances::get));

        for (PointInteret poi : pois) {
            distances.put(poi.getId(), Double.MAX_VALUE);
            predecesseurs.put(poi.getId(), null);
        }
        distances.put(poiDepartId, 0.0);
        pq.add(poiDepartId);

        while (!pq.isEmpty()) {
            Long currentId = pq.poll();

            if (currentId.equals(poiArriveeId)) {
                break;
            }

            for (Chemin chemin : graphe.getOrDefault(currentId, new ArrayList<>())) {
                Long voisinId = chemin.getPoiArrivee().getId();
                double nouvelleDistance = distances.get(currentId) + chemin.getDistance();

                if (nouvelleDistance < distances.get(voisinId)) {
                    distances.put(voisinId, nouvelleDistance);
                    predecesseurs.put(voisinId, currentId);
                    pq.add(voisinId);
                }
            }
        }

        // Reconstruction du chemin
        List<PointInteret> cheminFinal = new ArrayList<>();
        Long currentId = poiArriveeId;
        while (currentId != null) {
            PointInteret poi = pointInteretRepository.findById(currentId).orElse(null);
            if (poi != null) {
                cheminFinal.add(poi);
            }
            currentId = predecesseurs.get(currentId);
        }
        Collections.reverse(cheminFinal);

        return cheminFinal;
    }
}
