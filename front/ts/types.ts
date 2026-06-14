export interface Restaurant {
  id_restaurant: number
  nom: string
  adresse: string
  latitude: number
  longitude: number
}

export interface Reservation {
  idRestau: number
  date: string
  periode: string
  nbrPersonnes: number
  prenom: string
  nom: string
  telephone: string
}

export interface IncidentLocation {
  street: string
  polyline: string
  location_description: string
}

export interface Incident {
  id: string
  type: string
  description: string
  short_description: string
  starttime: string
  endtime: string
  location: IncidentLocation
}

export interface VelibStation {
  station_id: string
  nom: string
  adresse: string
  latitude: number
  longitude: number
  velos_disponibles: number
  places_libres: number
}

export interface VelibInfoStation {
  station_id: string
  name: string
  address: string
  lat: number
  lon: number
}

export interface VelibStatusStation {
  station_id: string
  num_bikes_available: number
  num_docks_available: number
}

export interface VelibInfoResponse {
  data: { stations: VelibInfoStation[] }
}

export interface VelibStatusResponse {
  data: { stations: VelibStatusStation[] }
}