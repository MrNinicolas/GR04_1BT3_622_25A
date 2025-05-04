package model.dao;

import jakarta.persistence.EntityManager;
import model.entities.Product;

import java.util.List;

public class ProductDAO extends GenericDAO<Product> {

    public ProductDAO() {
        super(Product.class);
    }

    public List<Product> findProductsByUserId(int idUser) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT p FROM Product p WHERE p.user.idUser = :idUser";
            return em.createQuery(jpql, Product.class)
                    .setParameter("idUser", idUser)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Product> findAvailableProductsByUserId(int idUser) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT p FROM Product p WHERE p.user.idUser = :idUser AND p.isAvailable = true";
            return em.createQuery(jpql, Product.class)
                    .setParameter("idUser", idUser)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Product> findProductById(int idProduct) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT p FROM Product p WHERE p.idProduct = :idProduct";
            return em.createQuery(jpql, Product.class)
                    .setParameter("idProduct", idProduct)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }


    public boolean updateProductAvailability(List<Product> products, boolean available) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            for (Product product : products) {
                product.setAvailable(available);
                em.merge(product);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> findAvailableProductsExceptUser(int userId) {
        try (EntityManager em = getEntityManager()) {
            String jpql = "SELECT p FROM Product p WHERE p.user.idUser != :userId AND p.isAvailable = true";
            return em.createQuery(jpql, Product.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }
}
