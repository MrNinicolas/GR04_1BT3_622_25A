package model.entities;

import jakarta.persistence.*;
import model.enums.ProductCategory;
import model.enums.ProductState;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idProduct")
    private int idProduct;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ProductState state;

    @Column(name = "datePublication")
    private Date datePublication;

    @Column(name = "isAvailable")
    private boolean isAvailable;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Column(name = "photo")
    private String photo;

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;

    @ManyToMany(mappedBy = "offeredProducts")
    private List<Offer> offers;

    public Product() {
    }

    public Product(int idProduct, String title, String description, ProductState state, ProductCategory category, String photo, User user) {
        this.idProduct = idProduct;
        this.title = title;
        this.description = description;
        this.state = state;
        this.datePublication = new Date();
        this.isAvailable = true;
        this.category = category;
        this.photo = photo;
        this.user = user;
    }

    public Product(int idProduct, String title, String description, String state, User user) {
        this.idProduct = idProduct;
        this.title = title;
        this.description = description;
        this.state = ProductState.valueOf(state);
        this.datePublication = new Date();
        this.isAvailable = true;
        this.user = user;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductState getState() {
        return state;
    }

    public void setState(ProductState state) {
        this.state = state;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory productCategory) {
        this.category = productCategory;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Profile getOwner() {
        return null;
    }
}
