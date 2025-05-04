package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import model.entities.User;
import model.entities.Offer;
import model.service.OfferService;
import model.service.OfferService.ResponseMessage;

import java.io.IOException;

@WebServlet("/RespondOfferController")
public class RespondOfferController extends HttpServlet {

    private final OfferService offerService = new OfferService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String route = req.getParameter("route");
        if (route == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requiere un parámetro 'route'.");
            return;
        }

        switch (route) {
            case "list" -> handleListOffers(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta GET desconocida: " + route);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String route = req.getParameter("route");
        if (route == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requiere un parámetro 'route'.");
            return;
        }

        switch (route) {
            case "respond" -> handleRespondOffer(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta POST desconocida: " + route);
        }
    }

    // Mostrar las ofertas pendientes del usuario
    private void handleListOffers(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoggedUser(req, resp);
        if (user == null) return;

        offerService.loadPendingOffers(user.getIdUser(), req);
        req.getRequestDispatcher("jsp/OFFERS.jsp").forward(req, resp);
    }

    // Responder una oferta: aceptar o rechazar
    private void handleRespondOffer(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoggedUser(req, resp);
        if (user == null) return;

        String status = req.getParameter("status");
        String offerIdStr = req.getParameter("offerId");

        ResponseMessage responseMessage;

        if (status == null || offerIdStr == null) {
            responseMessage = new ResponseMessage("error", "Datos incompletos para procesar la oferta.");
        } else {
            try {
                int offerId = Integer.parseInt(offerIdStr);
                Offer offer = offerService.findById(offerId);

                if (offer == null || offer.getOfferedByUser() == null || offer.getOfferedByUser().getIdUser() != user.getIdUser())
                {
                    responseMessage = new ResponseMessage("error", "Oferta no encontrada o no autorizada.");
                } else {
                    responseMessage = offerService.processOfferStatus(offer, status);
                }

            } catch (NumberFormatException e) {
                responseMessage = new ResponseMessage("error", "ID de oferta inválido.");
            }
        }

        // Mostrar mensaje de respuesta
        req.setAttribute("messageType", responseMessage.type());
        req.setAttribute("message", responseMessage.message());

        // Recargar lista de ofertas pendientes
        offerService.loadPendingOffers(user.getIdUser(), req);
        req.getRequestDispatcher("jsp/OFFERS.jsp").forward(req, resp);
    }

    // Obtener usuario en sesión
    private User getLoggedUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false); // evita crear una nueva sesión si no existe
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect("LoginController?route=enter");
            return null;
        }
        return user;
    }
}
