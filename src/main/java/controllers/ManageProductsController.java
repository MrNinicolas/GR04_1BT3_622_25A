package controllers;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.entities.Product;
import model.entities.User;
import model.service.ProductService;

@WebServlet("/ManageProductsController")
public class ManageProductsController extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ProductService productService = new ProductService();

    private ProductService getProductService() {
        return productService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.router(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.router(req, resp);
    }

    private void router(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Control logic
        String route = (req.getParameter("route") == null) ? "list" : req.getParameter("route");

        switch (route) {
            case "list":
                String viewType = req.getParameter("view");
                if (viewType == null || viewType.isEmpty()) {
                    viewType = "user"; // Valor por defecto si no viene el parámetro "view"
                }
                if ("home".equals(viewType)) {
                    this.viewProducts(req, resp);
                } else if ("user".equals(viewType)) {
                    this.viewMyProducts(req, resp);
                } else {
                    throw new IllegalArgumentException("Unknown view type: " + viewType);
                }
                break;
            case "add":
                this.addProduct(req, resp);
                break;
            case "saveNew":
                this.saveNewProduct(req, resp);
                break;
            case "edit":
                this.editProduct(req, resp);
                break;
            case "saveExisting":
                this.saveExistingProduct(req, resp);
                break;
            case "delete":
                this.deleteProduct(req, resp);
                break;
            case "accept":
                this.confirmRemove(req, resp);
                break;
            default:
                throw new IllegalArgumentException("Unknown route: " + route);
        }
    }

    private void addProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Product> products = getProductService().findProductsByUserId(getUser(req).getIdUser());
        req.setAttribute("products", products);
        req.setAttribute("route", "add");
        req.getRequestDispatcher("jsp/MY_PRODUCT.jsp").forward(req, resp);
    }

    private void confirmRemove(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        int idProduct = Integer.parseInt(req.getParameter("idProduct"));
        if (getProductService().removeProduct(idProduct)) {
            req.setAttribute("messageType", "info");
            req.setAttribute("message", "Product deleted successfully.");
            req.getRequestDispatcher("ManageProductsController?route=list").forward(req, resp);
        } else {
            req.setAttribute("messageType", "error");
            req.setAttribute("message", "Failed to delete Product.");
            req.getRequestDispatcher("ManageProductsController?route=list").forward(req, resp);
        }
    }

    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        int idProduct = Integer.parseInt(req.getParameter("idProduct"));
        HttpSession session = req.getSession();
        Product product = getProductService().findProductById(idProduct);
        List<Product> products = getProductService().findProductsByUserId(getUser(req).getIdUser());

        if (product != null) {
            req.setAttribute("product", product);
            req.setAttribute("products", products);
            req.getRequestDispatcher("jsp/MY_PRODUCT.jsp").forward(req, resp);
        } else {
            session.setAttribute("message", "Product could not be found");
            resp.sendRedirect("ManageProductsController?route=list");
        }
    }

    private void editProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int idProduct = Integer.parseInt(req.getParameter("idProduct"));
        Product product = getProductService().findProductById(idProduct);

        List<Product> products = getProductService().findProductsByUserId(getUser(req).getIdUser());
        req.setAttribute("products", products);

        if (product != null) {
            req.setAttribute("product", product);
            req.getRequestDispatcher("jsp/MY_PRODUCT.jsp").forward(req, resp);
        } else {
            session.setAttribute("message", "Product not found");
            resp.sendRedirect("ManageProductsController?route=list");
        }
    }

    private void saveExistingProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Product product = parseProductFromRequest(req);
        product.setTitle(req.getParameter("txtTitle"));
        product.setDescription(req.getParameter("txtDescription"));
        product.setState(req.getParameter("txtState"));

        processProductSave(req, resp, product, true);
    }

    private void saveNewProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Product product = parseProductFromRequest(req);
        processProductSave(req, resp, product, false);
    }

    private void processProductSave(HttpServletRequest req, HttpServletResponse resp, Product product, boolean isUpdate) throws IOException, ServletException {
        boolean success = isUpdate ? getProductService().updateProduct(product) : getProductService().createProduct(product);

        String messageType = success ? "info" : "error";
        String message = success
                ? (isUpdate ? "Product updated successfully." : "Product created successfully.")
                : (isUpdate ? "Failed to update product." : "Failed to create product.");

        setMessageAndForward(req, resp, messageType, message);
    }

    private void setMessageAndForward(HttpServletRequest req, HttpServletResponse resp, String messageType, String message) throws ServletException, IOException {
        req.setAttribute("messageType", messageType);
        req.setAttribute("message", message);
        req.getRequestDispatcher("ManageProductsController?route=list").forward(req, resp);
    }

    private void viewProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Product> products = getProductService().findAvailableProductsExceptUser(getUser(req).getIdUser());
        req.setAttribute("products", products);
        req.getRequestDispatcher("jsp/HOME.jsp").forward(req, resp);
    }

    private void viewMyProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Product> products = getProductService().findProductsByUserId(getUser(req).getIdUser());
        req.setAttribute("products", products);
        req.getRequestDispatcher("jsp/MY_PRODUCT.jsp").forward(req, resp);
    }

    private Product parseProductFromRequest(HttpServletRequest req) {
        String txtId = req.getParameter("txtIdProduct");
        int idProduct = parseProductId(txtId);
        String title = req.getParameter("txtTitle");
        String description = req.getParameter("txtDescription");
        String state = req.getParameter("txtState");
        return new Product(idProduct, title, description, state, getUser(req));
    }

    private static User getUser(HttpServletRequest req) {
        HttpSession session = req.getSession();
        return (User) session.getAttribute("user");
    }

    private int parseProductId(String idParam) {
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                return Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                System.out.println("Error al convertir el ID: " + e.getMessage());
            }
        }
        return 0;
    }
}