package controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.OfferDAO;
import model.entities.Offer;
import model.entities.Product;
import model.entities.User;
import model.service.OfferService;
import model.service.ReputationService;
import model.service.UserService;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/TradeDeliveryController")
public class TradeDeliveryController extends HttpServlet {
    private static EntityManagerFactory entityManagerFactory;
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
        String route = req.getParameter("route");
        if (route==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing route parameter");
            return;
        }
        switch (route) {
            case "confirmDelivery":
                this.confirmDelivery(req, resp);
                break;
            case "rateUser":
                this.rateUser(req, resp);
                break;
            case "rejectOffer":
                this.rejectOffer(req, resp);
                break;
            case "listDeliveries":
                this.listDeliveries(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void rateUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int offerId = Integer.parseInt(req.getParameter("offerId"));
        int score = Integer.parseInt(req.getParameter("score"));

        OfferService offerService = new OfferService();
        Offer offer = offerService.findById(offerId);

        if (offer != null) {
            boolean success = offerService.rateOfferAndMarkDelivered(offer, score);
            if (success) {
                req.setAttribute("successMessage", "✅ Rating submitted successfully!");
            } else {
                req.setAttribute("errorMessage", "⚠️ User who made the offer not found. Please try again.");
            }
        } else {
            req.setAttribute("errorMessage", "⚠️ Offer not found. Please try again.");
        }

        this.listDeliveries(req, resp);
    }



    private void listDeliveries(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User loggedUser = (User) session.getAttribute("user");

        if (loggedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        OfferService offerService = new OfferService();
        List<Offer> pendingDeliveries = offerService.findAcceptedOffersPendingDeliveryByUserId(loggedUser.getIdUser());

        req.setAttribute("pendingDeliveries", pendingDeliveries);
        req.getRequestDispatcher("/jsp/DELIVERY.jsp").forward(req, resp);
    }


    private void confirmDelivery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int offerId = Integer.parseInt(req.getParameter("offerId"));
        int toUserId = Integer.parseInt(req.getParameter("toUserId"));

        OfferService offerService = new OfferService();
        Offer offer = offerService.findById(offerId);

        if (offer != null && offer.getStatus().equals("accepted")) {
            boolean success = offerService.confirmDeliveryAndUpdateOffer(offer);

            if (success) {
                resp.sendRedirect(req.getContextPath() + "/TradeDeliveryController?route=showRatingForm&offerId=" + offerId + "&toUserId=" + toUserId);
            } else {
                req.setAttribute("errorMessage", "⚠️ Error al confirmar la entrega.");
                this.listDeliveries(req, resp);
            }
        } else {
            req.setAttribute("errorMessage", "⚠️ Oferta no encontrada o no aceptada.");
            this.listDeliveries(req, resp);
        }
    }

    private void rejectOffer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int offerId = Integer.parseInt(req.getParameter("offerId"));

        OfferService offerService = new OfferService();
        Offer offer = offerService.findById(offerId);

        if (offer != null && offer.getStatus().equals("accepted")) {
            boolean rejected = offerService.changeOfferStatusToPending(offer);
            if (rejected) {
                req.setAttribute("successMessage", "⚠️ The offer has been rejected and is now pending.");
            } else {
                req.setAttribute("errorMessage", "⚠️ An error occurred while rejecting the offer.");
            }
            this.listDeliveries(req, resp);
        } else {
            req.setAttribute("errorMessage", "⚠️ Offer not found or not accepted.");
            this.listDeliveries(req, resp);  // Mostrar la página con el mensaje de error
        }
    }


}
