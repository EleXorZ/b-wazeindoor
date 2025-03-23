package wazeindoor.services;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wazeindoor.entity.Chemin;
import wazeindoor.entity.PointInteret;
import wazeindoor.repositories.CheminRepository;
import wazeindoor.repositories.PointInteretRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItineraireService {

    private final CheminRepository cheminRepository;
    private final PointInteretRepository pointInteretRepository;

    private static final Logger logger = LoggerFactory.getLogger(ItineraireService.class);

    public ItineraireService(CheminRepository cheminRepository, PointInteretRepository pointInteretRepository) {
        this.cheminRepository = cheminRepository;
        this.pointInteretRepository = pointInteretRepository;
    }

    public List<PointInteret> calculerItineraireByDistance(Long espaceId, Long poiDepartId, Long poiArriveeId) {
        List<Chemin> chemins = cheminRepository.findByPoiDepart_Espace_Id(espaceId);
        Map<Long, List<Chemin>> graphe = chemins.stream()
                .collect(Collectors.groupingBy(chemin -> chemin.getPoiDepart().getId()));

        return dijkstra(poiDepartId, poiArriveeId, graphe);
    }

    public List<PointInteret> calculerItineraireByLocalisation(Long espaceId, Long poiDepartId, Long poiArriveeId) {
        List<PointInteret> pois = pointInteretRepository.findByEspaceId(espaceId);
        Map<Long, List<PointInteret>> graphe = construireGraphe(pois, 3);

        return dijkstra(poiDepartId, poiArriveeId, graphe, pois);
    }

    private List<PointInteret> dijkstra(Long departId, Long arriveeId, Map<Long, List<Chemin>> graphe) {
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> predecesseurs = new HashMap<>();
        PriorityQueue<Long> pq = new PriorityQueue<>(Comparator.comparing(distances::get));

        distances.put(departId, 0.0);
        pq.add(departId);

        while (!pq.isEmpty()) {
            Long currentId = pq.poll();
            if (currentId.equals(arriveeId)) break;

            for (Chemin chemin : graphe.getOrDefault(currentId, Collections.emptyList())) {
                Long voisinId = chemin.getPoiArrivee().getId();
                double nouvelleDistance = distances.getOrDefault(currentId, Double.MAX_VALUE) + chemin.getDistance();

                if (nouvelleDistance < distances.getOrDefault(voisinId, Double.MAX_VALUE)) {
                    distances.put(voisinId, nouvelleDistance);
                    predecesseurs.put(voisinId, currentId);
                    pq.add(voisinId);
                }
            }
        }

        return reconstruireChemin(arriveeId, predecesseurs);
    }

    private List<PointInteret> dijkstra(Long departId, Long arriveeId, Map<Long, List<PointInteret>> graphe, List<PointInteret> pois) {
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> predecesseurs = new HashMap<>();
        PriorityQueue<Long> pq = new PriorityQueue<>(Comparator.comparing(distances::get));

        distances.put(departId, 0.0);
        pq.add(departId);

        while (!pq.isEmpty()) {
            Long currentId = pq.poll();
            PointInteret current = findPoiById(pois, currentId);
            if (current == null || currentId.equals(arriveeId)) break;

            for (PointInteret voisin : graphe.getOrDefault(currentId, Collections.emptyList())) {
                double nouvelleDistance = distances.get(currentId) + calculDistance(current, voisin);
                if (nouvelleDistance < distances.getOrDefault(voisin.getId(), Double.MAX_VALUE)) {
                    distances.put(voisin.getId(), nouvelleDistance);
                    predecesseurs.put(voisin.getId(), currentId);
                    pq.add(voisin.getId());
                }
            }
        }

        return reconstruireChemin(arriveeId, predecesseurs);
    }

    private List<PointInteret> reconstruireChemin(Long arriveeId, Map<Long, Long> predecesseurs) {
        List<PointInteret> chemin = new ArrayList<>();
        Long currentId = arriveeId;

        while (currentId != null) {
            pointInteretRepository.findById(currentId).ifPresent(chemin::add);
            currentId = predecesseurs.get(currentId);
        }

        Collections.reverse(chemin);
        return chemin;
    }

    private double calculDistance(PointInteret a, PointInteret b) {
        return Math.hypot(b.getX() - a.getX(), b.getY() - a.getY());
    }

    private PointInteret findPoiById(List<PointInteret> pois, Long id) {
        return pois.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    private Map<Long, List<PointInteret>> construireGraphe(List<PointInteret> pois, int k) {
        return pois.stream().collect(Collectors.toMap(
                PointInteret::getId,
                poi -> pois.stream()
                        .filter(p -> !p.getId().equals(poi.getId()))
                        .sorted(Comparator.comparingDouble(p -> calculDistance(poi, p)))
                        .limit(k)
                        .collect(Collectors.toList())
        ));
    }

    // Itinéraire avec plusieurs points obligatoires
    public List<PointInteret> calculerItineraireRapide(Long espaceId, Long start, List<Long> waypoints, Long end) {
        List<PointInteret> chemin = new ArrayList<>();

        logger.info("Début du calcul de l'itinéraire depuis le point {} vers le point {} avec les waypoints {}", start, end, waypoints);

        // Vérifier que tous les points sont bien dans l'espace
        try {
            verifierPointsDansEspace(espaceId, start, waypoints, end);
            logger.info("Tous les points sont valides pour l'espace {}", espaceId);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la vérification des points dans l'espace {}: {}", espaceId, e.getMessage());
            throw e;
        }

        List<Long> nonVisites = new ArrayList<>(waypoints);
        Long pointActuel = start;
        chemin.add(trouverPoint(espaceId, pointActuel));

        while (!nonVisites.isEmpty()) {
            Long plusProche = trouverPlusProche(espaceId, pointActuel, nonVisites);
            chemin.add(trouverPoint(espaceId, plusProche));
            nonVisites.remove(plusProche);
            pointActuel = plusProche;
            logger.debug("Point actuel : {}", pointActuel);
        }

        chemin.add(trouverPoint(espaceId, end));
        logger.info("Itinéraire calculé avec succès, fin à {}", end);

        return optimiserItineraire(chemin);
    }

    private void verifierPointsDansEspace(Long espaceId, Long start, List<Long> waypoints, Long end) {
        List<Long> allPoints = new ArrayList<>();
        allPoints.add(start);
        allPoints.addAll(waypoints);
        allPoints.add(end);

        long count = pointInteretRepository.countByIdInAndEspaceId(allPoints, espaceId);

        if (count != allPoints.size()) {
            throw new IllegalArgumentException("Certains points n'appartiennent pas à l'espace " + espaceId);
        }
    }

    private Long trouverPlusProche(Long espaceId, Long origine, List<Long> waypoints) {
        Long plusProche = null;
        double minDistance = Double.MAX_VALUE;

        for (Long point : waypoints) {
            double distance = calculerDistance(trouverPoint(espaceId, origine), trouverPoint(espaceId, point));
            if (distance < minDistance) {
                minDistance = distance;
                plusProche = point;
            }
        }
        return plusProche;
    }

    private double calculerDistance(PointInteret p1, PointInteret p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy); // Formule de Pythagore
    }

    public List<PointInteret> optimiserItineraire(List<PointInteret> chemin) {
        boolean amelioration = true;
        while (amelioration) {
            amelioration = false;
            for (int i = 1; i < chemin.size() - 2; i++) {
                for (int j = i + 1; j < chemin.size() - 1; j++) {
                    if (gainEchange(chemin, i, j)) {
                        Collections.reverse(chemin.subList(i, j + 1));
                        amelioration = true;
                    }
                }
            }
        }
        return chemin;
    }

    private boolean gainEchange(List<PointInteret> chemin, int i, int j) {
        PointInteret A = chemin.get(i - 1);
        PointInteret B = chemin.get(i);
        PointInteret C = chemin.get(j);
        PointInteret D = chemin.get(j + 1);

        double distanceActuelle = calculerDistance(A, B) + calculerDistance(C, D);
        double nouvelleDistance = calculerDistance(A, C) + calculerDistance(B, D);

        return nouvelleDistance < distanceActuelle;
    }

    private PointInteret trouverPoint(Long espaceId, Long id) {
        return pointInteretRepository.findByEspaceIdAndId(espaceId, id)
                .orElseThrow(() -> new EntityNotFoundException("Point d'intérêt non trouvé : " + id));
    }

}
