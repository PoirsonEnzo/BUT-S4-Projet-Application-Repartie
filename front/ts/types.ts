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