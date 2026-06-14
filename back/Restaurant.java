public class Restaurant{
    private int id_restaurant;
    private String nom;
    private String adresse;
    private double latitude;
    private double longitude;

    public Restaurant(int id_restaurant, String nom, String adresse, double latitude, double longitude) {
        this.id_restaurant = id_restaurant;
        this.nom = nom;
        this.adresse = adresse;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
