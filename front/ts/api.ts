import { PROXY_URL } from "./config"
import { Incident, Reservation, Restaurant } from "./types"

export async function getRestaurants(): Promise<Restaurant[]> {
  const response = await fetch(`${PROXY_URL}/restaurants`)
  if (!response.ok) throw new Error("Erreur lors de la récupération des restaurants")
  const data = await response.json()
  return data.restaurants
}

export async function getIncidents(): Promise<Incident[]> {
  const response = await fetch(`${PROXY_URL}/incidents`)
  if (!response.ok) throw new Error("Erreur lors de la récupération des incidents")
  const data = await response.json()
  return data.incidents
}

export async function postReservation(reservation: Reservation): Promise<boolean> {
  const response = await fetch(`${PROXY_URL}/reservation`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(reservation)
  })
  return response.ok
}