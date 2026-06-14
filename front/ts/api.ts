import { PROXY_URL, URL_VELIB_INFO, URL_VELIB_STATUS } from "./config";
import { Incident, Reservation, Restaurant, VelibStation, VelibInfoResponse, VelibStatusResponse, VelibStatusStation, VelibInfoStation } from "./types";

export async function getRestaurants(): Promise<Restaurant[]> {
  const response = await fetch(`${PROXY_URL}/restaurants`);
  if (!response.ok) throw new Error("Erreur lors de la récupération des restaurants");
  const data = await response.json() as { restaurants: Restaurant[] };
  return data.restaurants;
}

export async function getIncidents(): Promise<Incident[]> {
  const response = await fetch(`${PROXY_URL}/incidents`);
  if (!response.ok) throw new Error("Erreur lors de la récupération des incidents");
  const data = await response.json() as { incidents: Incident[] };
  return data.incidents;
}

export async function postReservation(reservation: Reservation): Promise<boolean> {
  const response = await fetch(`${PROXY_URL}/reservation`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(reservation)
  });
  return response.ok;
}

export async function getVelibStations(): Promise<VelibStation[]> {
  const [infoResponse, statusResponse] = await Promise.all([
    fetch(URL_VELIB_INFO),
    fetch(URL_VELIB_STATUS)
  ]);

  if (!infoResponse.ok || !statusResponse.ok) throw new Error("Erreur lors de la récupération des vélibs");

  const infoData = await infoResponse.json() as VelibInfoResponse;
  const statusData = await statusResponse.json() as VelibStatusResponse;

  return infoData.data.stations.map((info: VelibInfoStation) => {
    const status = statusData.data.stations.find((s: VelibStatusStation) => s.station_id === info.station_id);
    return {
      station_id: info.station_id,
      nom: info.name,
      adresse: info.address,
      latitude: info.lat,
      longitude: info.lon,
      velos_disponibles: status?.num_bikes_available ?? 0,
      places_libres: status?.num_docks_available ?? 0
    };
  });
}