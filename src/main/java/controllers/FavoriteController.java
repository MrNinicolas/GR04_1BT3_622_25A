package controllers;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
import model.entities.Favorite;
import model.service.FavoriteService;
import model.service.ProductService;
import model.service.UserService;
import model.entities.Product;
import model.entities.User;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet("/FavoriteController")
public class FavoriteController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.route(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.route(req, resp);
    }

    private void route(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("route");

        switch (action) {
            case "add":
                this.addFavorite(req, resp);
                break;

            case "listFavorites":
                this.listFavorites(req, resp);
                break;

            case "removeFavorite":
                this.removeFavorite(req, resp);
                break;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void addFavorite(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int idProduct = Integer.parseInt(req.getParameter("idProduct"));
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        ProductService productService = new ProductService();
        FavoriteService favoriteService = new FavoriteService();
        Product product = productService.findById(idProduct);

        // Comprobamos antes de invocar al servicio
        if (favoriteService.isProductAlreadyFavorite(user, product)) {
            // Mensaje de error/advertencia
            session.setAttribute("message", "This product is already in your favourites");
            session.setAttribute("messageType", "error");
        } else {
            // Agregamos y mensaje de éxito
            favoriteService.addFavorite(user, product);
            session.setAttribute("message", "Product added to favourites");
            session.setAttribute("messageType", "info");
        }

        // Redirigimos siempre al listado de favoritos
        resp.sendRedirect(req.getContextPath() + "/FavoriteController?route=listFavorites");
    }

    private void removeFavorite(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int productId = Integer.parseInt(req.getParameter("productId"));
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            FavoriteService favoriteService = new FavoriteService();
            ProductService productService = new ProductService();

            // Encontrar el producto a eliminar
            Product product = productService.findById(productId);

            // Llamar al servicio para eliminar el favorito
            boolean success = favoriteService.removeFavorite(user, product);

            if (success) {
                resp.sendRedirect(req.getContextPath() + "/FavoriteController?route=listFavorites");
            } else {
                // Si la eliminación falla, redirigir o mostrar un mensaje de error
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "The product could not be removed from favourites.");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/LoginController?route=login");
        }
    }


    private void listFavorites(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        FavoriteService favoriteService = new FavoriteService();

        // Obtener todos los favoritos
        List<Favorite> favorites = favoriteService.getFavoritesByUser(user);

        // Filtrar solo productos disponibles
        List<Favorite> availableFavorites = favorites.stream()
                .filter(fav -> fav.getProduct() != null && fav.getProduct().isAvailable())
                .toList();

        // Eliminar favoritos no disponibles de la base de datos para limpiar
        favorites.stream()
                .filter(fav -> fav.getProduct() != null && !fav.getProduct().isAvailable())
                .forEach(fav -> favoriteService.removeFavorite(user, fav.getProduct()));

        req.setAttribute("favorites", availableFavorites);
        req.getRequestDispatcher("/jsp/FAVORITES.jsp").forward(req, resp);
    }
}

