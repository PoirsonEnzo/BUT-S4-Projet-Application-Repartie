import * as L from "leaflet";
import { ICONE_BLEUE, ICONE_OMBRE, ICONE_ROUGE, ICONE_VERTE } from "./config";
import { Incident, Restaurant, VelibStation } from "./types";

let map: L.Map;

export function initMap(): void {
  map = L.map("map").setView([48.6921, 6.1844], 13);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map);
}

export function ajouterRestaurants(restaurants: Restaurant[], onReserver: (r: Restaurant) => void): void {
  const iconeRestaurant = L.icon({
    iconUrl: ICONE_VERTE,
    shadowUrl: ICONE_OMBRE,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34]
  });

  for (const restaurant of restaurants) {
    const popup = `
      <div class="popup-nom">${restaurant.nom}</div>
      <div class="popup-adresse">${restaurant.adresse}</div>
      <button class="popup-btn" id="btn-reserver-${restaurant.id_restaurant}">Réserver</button>
    `;

    const marker = L.marker([restaurant.latitude, restaurant.longitude], { icon: iconeRestaurant });
    marker.bindPopup(popup);
    marker.addTo(map);

    marker.on("popupopen", () => {
      const btn = document.getElementById(`btn-reserver-${restaurant.id_restaurant}`);
      btn?.addEventListener("click", () => onReserver(restaurant));
    });
  }
}

export function ajouterIncidents(incidents: Incident[]): void {
  const iconeIncident = L.icon({
    iconUrl: ICONE_ROUGE,
    shadowUrl: ICONE_OMBRE,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34]
  });

  for (const incident of incidents) {
    const [lat, lng] = incident.location.polyline.split(" ").map(Number);

    const popup = `
      <div class="popup-nom">${incident.short_description}</div>
      <div class="popup-adresse">${incident.location.location_description}</div>
      <div class="popup-adresse">Du ${formatDate(incident.starttime)} au ${formatDate(incident.endtime)}</div>
      <div class="popup-adresse">${incident.description}</div>
    `;

    L.marker([lat, lng], { icon: iconeIncident })
      .bindPopup(popup)
      .addTo(map);
  }
}

export function ajouterVelibs(stations: VelibStation[]): void {
  const iconeVelib = L.icon({
    iconUrl: ICONE_BLEUE,
    shadowUrl: ICONE_OMBRE,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34]
  });

  for (const station of stations) {
    const popup = `
      <div class="popup-nom">${station.nom}</div>
      <div class="popup-adresse">${station.adresse}</div>
      <div class="popup-adresse">Vélos disponibles : ${station.velos_disponibles}</div>
      <div class="popup-adresse">Places libres : ${station.places_libres}</div>
    `;

    L.marker([station.latitude, station.longitude], { icon: iconeVelib })
      .bindPopup(popup)
      .addTo(map);
  }
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString("fr-FR");
}