<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MAKE AN OFFER</title>
    <!-- Bootstrap CSS -->
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
            rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>

<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark px-4" style="position: sticky; top: 0; z-index: 1030;">
    <div class="container-fluid">
        <!-- Inicio: Sección izquierda -->
        <div class="d-flex">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/ManageProductsController?route=list&view=home">
                        <i class="fas fa-home"></i> Home
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/ManageProductsController?route=list&view=user">
                        <i class="fas fa-box"></i> My Products
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/RespondOfferController?route=list">
                        <i class="fas fa-handshake"></i> Offers
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link"
                       href="${pageContext.request.contextPath}/TradeDeliveryController?route=listDeliveries">
                        <i class="fas fa-truck"></i> My Deliveries
                    </a>
                </li>
            </ul>
        </div>

        <!-- Fin: Sección derecha -->
        <div class="d-flex">
            <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link text-danger" href="${pageContext.request.contextPath}/LoginController?route=logOut">
                        <i class="fas fa-sign-out-alt"></i> Logout
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<!-- Main Content -->
<main class="container my-4">
    <!-- Feedback Message -->
    <c:if test="${not empty sessionScope.message}">
        <div id="notification" class="alert alert-danger"
             style="background-color: #f8d7da; color: #721c24;" role="alert">
                ${sessionScope.message}</div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <div class="row">
        <!-- PRODUCT TO OFFER Section -->
        <div class="col-12">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h1 class="mb-0"> Make An Offer </h1>
            </div>
            <div class="row">
                <c:forEach var="product" items="${products}">
                    <div class="col-md-6 mb-3">
                        <div class="card border-0 rounded-3">
                            <div class="card-body p-4">
                                <h2 class="card-title text-dark fw-bold">${product.title}</h2>
                                <p class="card-text text-secondary small mb-4">
                                    <i class="fa-solid fa-align-left me-2"></i>${product.description}
                                </p>
                                <p class="card-text text-secondary small mb-4">
                                    <i class="fa-solid fa-layer-group me-2"></i>${product.state}
                                </p>
                                <p class="card-text text-secondary small mb-4">
                                    <i class="fa-solid fa-calendar me-2"></i>${product.datePublication}
                                </p>
                                <p class="card-text text-secondary small mb-4">
                                    <c:choose>
                                        <c:when test="${product.isAvailable}">
                                            <i class="fa-solid fa-check me-2"></i>Available
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fa-solid fa-x me-2"></i> Not available
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" type="checkbox"
                               name="selectedProducts" value="${product.idProduct}" id="productCheckbox${product.idProduct}">
                        <label class="form-check-label text-danger fw-bold" for="productCheckbox${product.idProduct}">
                            Offer this one ...
                        </label>
                    </div>

                </c:forEach>

                <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#confirmModal">
                    Confirm Offer
                </button>

            </div>
            <c:if test="${empty products}">
                <div class="alert alert-warning text-center">You have not created any product.</div>
            </c:if>
        </div>
    </div>
</main>

<!-- Modal para mensajes informativos y de error -->
<div class="modal modal-info" id="infoModal" tabindex="-1"
     aria-labelledby="infoModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body ${messageType == 'info' ? 'info' : 'error'}">
                <i
                        class="fas ${messageType == 'info' ? 'fa-info-circle text-info' : 'fa-exclamation-circle text-danger'}"></i>
                <span>${message}</span>
            </div>
        </div>
    </div>
</div>

<!-- Modal de confirmación -->
<div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 rounded-3">
            <div class="modal-header bg-warning text-dark">
                <h5 class="modal-title" id="confirmModalLabel">¿Estás seguro?</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body">
                ¿Quieres completar esta oferta con los productos seleccionados?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
                <button type="button" id="confirmOfferBtn" class="btn btn-danger">Sí</button>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Mostrar modal informativo si hay mensaje
    const message = "${message}";
    if (message !== "") {
        const infoModalElement = document.getElementById("infoModal");
        if (infoModalElement) {
            const infoModal = new bootstrap.Modal(infoModalElement, {
                backdrop: false, // Sin fondo oscuro
                keyboard: false  // Desactiva cerrar con teclado
            });
            infoModal.show();

            // Cerrar automáticamente después de 1 segundo
            setTimeout(() => {
                infoModal.hide();
            }, 1000);
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        const notification = document.getElementById("notification");
        if (notification) {
            // Oculta el mensaje después de 2 segundos
            setTimeout(() => {
                notification.style.transition = "opacity 0.5s";
                notification.style.opacity = "0";
                setTimeout(() => notification.remove(), 1000);
            }, 2000);
        }
    });
</script>
<script>
    document.getElementById("confirmOfferBtn").addEventListener("click", function () {
        // Obtener todos los checkboxes marcados
        const selected = document.querySelectorAll('input[name="selectedProducts"]:checked');
        if (selected.length === 0) {
            alert("No has seleccionado ningún producto.");
            return;
        }

        // Construir array con los IDs
        const ids = Array.from(selected).map(cb => cb.value);
        const selectedIds = ids.join(",");

        // Redirigir a la ruta deseada con los IDs (como query string)
        window.location.href = `MakeOfferController?route=confirm&listOfferedProducts=${selectedIds}`;
    });
</script>

</body>
</html>
