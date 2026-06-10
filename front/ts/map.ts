import * as L from "leaflet"
import { Incident, Restaurant } from "./types"

let map: L.Map

export function initMap(): void {
  map = L.map("map").setView([48.6921, 6.1844], 13)

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "© OpenStreetMap contributors"
  }).addTo(map)
}

export function ajouterRestaurants(restaurants: Restaurant[], onReserver: (r: Restaurant) => void): void {
  for (const restaurant of restaurants) {
    const popup = `
      <div class="popup-nom">${restaurant.nom}</div>
      <div class="popup-adresse">${restaurant.adresse}</div>
      <button class="popup-btn" id="btn-reserver-${restaurant.id_restaurant}">Réserver</button>
    `

    const marker = L.marker([restaurant.latitude, restaurant.longitude])
    marker.bindPopup(popup)
    marker.addTo(map)

    marker.on("popupopen", () => {
      const btn = document.getElementById(`btn-reserver-${restaurant.id_restaurant}`)
      btn?.addEventListener("click", () => onReserver(restaurant))
    })
  }
}

export function ajouterIncidents(incidents: Incident[]): void {
  const iconeIncident = L.icon({
    iconUrl: "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png",
    shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34]
  })

  for (const incident of incidents) {
    const [lat, lng] = incident.location.polyline.split(" ").map(Number)

    const popup = `
      <div class="popup-nom">${incident.short_description}</div>
      <div class="popup-adresse">${incident.location.location_description}</div>
      <div class="popup-adresse">Du ${formatDate(incident.starttime)} au ${formatDate(incident.endtime)}</div>
      <div class="popup-adresse">${incident.description}</div>
    `

    L.marker([lat, lng], { icon: iconeIncident })
      .bindPopup(popup)
      .addTo(map)
  }
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString("fr-FR")
}