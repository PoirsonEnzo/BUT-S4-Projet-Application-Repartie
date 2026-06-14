import { getIncidents, getRestaurants, getVelibStations, postReservation } from "./api";
import { initMap, ajouterRestaurants, ajouterIncidents, ajouterVelibs } from "./map";
import { Restaurant, Reservation } from "./types";

let restaurantCourant: Restaurant | null = null;

document.querySelectorAll(".nav-tab").forEach(tab => {
  tab.addEventListener("click", () => {
    const cible = (tab as HTMLElement).dataset.tab;

    document.querySelectorAll(".nav-tab").forEach(t => t.classList.remove("active"));
    document.querySelectorAll(".tab-panel").forEach(p => p.classList.remove("active"));

    tab.classList.add("active");
    document.getElementById(`panel-${cible}`)?.classList.add("active");
  });
});

const overlay = document.getElementById("modal-overlay")!;
const modalNom = document.getElementById("modal-restaurant-nom")!;
const btnAnnuler = document.getElementById("btn-annuler")!;
const btnReserver = document.getElementById("btn-reserver")!;
const modalMsg = document.getElementById("modal-msg")!;

function ouvrirModal(restaurant: Restaurant): void {
  restaurantCourant = restaurant;
  modalNom.textContent = restaurant.nom;
  modalMsg.textContent = "";
  modalMsg.className = "modal-msg";
  overlay.classList.add("open");
}

function fermerModal(): void {
  restaurantCourant = null;
  overlay.classList.remove("open");
}

btnAnnuler.addEventListener("click", fermerModal);
overlay.addEventListener("click", (e) => {
  if (e.target === overlay) {
    fermerModal();
  }
});

btnReserver.addEventListener("click", async () => {
  if (!restaurantCourant) return;

  const nom = (document.getElementById("input-nom") as HTMLInputElement).value.trim();
  const prenom = (document.getElementById("input-prenom") as HTMLInputElement).value.trim();
  const telephone = (document.getElementById("input-tel") as HTMLInputElement).value.trim();
  const nbConvives = parseInt((document.getElementById("input-convives") as HTMLInputElement).value);
  const dateBrute = (document.getElementById("input-date") as HTMLInputElement).value;
  const [annee, mois, jour] = dateBrute.split("-");
  const date = `${jour}-${mois}-${annee}`;
  const periode = (document.getElementById("input-periode") as HTMLSelectElement).value;

  if (!nom || !prenom || !telephone || !nbConvives || !date || !periode) {
    modalMsg.textContent = "Veuillez remplir tous les champs";
    modalMsg.className = "modal-msg error";
    return;
  }

  const reservation: Reservation = {
    idRestau: restaurantCourant.id_restaurant,
    nom,
    prenom,
    telephone,
    nbrPersonnes: nbConvives,
    date,
    periode
  };

  const succes = await postReservation(reservation);

  if (succes) {
    modalMsg.textContent = "Réservation confirmée";
    modalMsg.className = "modal-msg success";
    setTimeout(fermerModal, 2000);
  } else {
    modalMsg.textContent = "Aucune table disponible pour ce créneau";
    modalMsg.className = "modal-msg error";
  }
});

async function init(): Promise<void> {
  initMap();

  try {
    const restaurants = await getRestaurants();
    ajouterRestaurants(restaurants, ouvrirModal);
  } catch (e) {
    console.error("Impossible de charger les restaurants", e);
  }

  try {
    const incidents = await getIncidents();
    ajouterIncidents(incidents);
  } catch (e) {
    console.error("Impossible de charger les incidents", e);
  }

  try {
    const stations = await getVelibStations();
    ajouterVelibs(stations);
  } catch (e) {
    console.error("Impossible de charger les vélibs", e);
  }
}

init();