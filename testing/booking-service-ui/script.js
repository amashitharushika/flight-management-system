const BASE_URL = "http://localhost:8082/api/bookings";

function getApiKey() {
    return document.getElementById("apiKey").value.trim();
}

function showOutput(content) {
    document.getElementById("output").innerHTML = content;
}

function showError(message) {
    showOutput(`<span class="error-text">Error: ${message}</span>`);
}

function renderBooking(booking) {
    const statusClass = booking.status === "CONFIRMED" ? "status-confirmed" : "status-cancelled";
    return `
        <div class="booking-item" data-id="${booking.id}">
            <strong>Booking #${booking.id}</strong>
            <span class="status ${statusClass}">${booking.status}</span>
            <div>Passenger: ${booking.passengerName}</div>
            <div>Seat: ${booking.seatNumber}</div>
            <div>Flight ID: ${booking.flightId} | User ID: ${booking.userId}</div>
            <div>Booked: ${booking.bookingDate}</div>
            <div class="actions">
                <button onclick="cancelBooking(${booking.id})">Cancel</button>
                <button class="btn-danger" onclick="deleteBooking(${booking.id})">Delete</button>
            </div>
        </div>
    `;
}

// ---- CREATE ----
document.getElementById("createForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        flightId: Number(document.getElementById("flightId").value),
        userId: Number(document.getElementById("userId").value),
        passengerName: document.getElementById("passengerName").value,
        seatNumber: document.getElementById("seatNumber").value
    };

    try {
        const res = await fetch(BASE_URL, {
            method: "POST",
            headers: {
                "X-API-KEY": getApiKey(),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            const errBody = await res.json().catch(() => ({}));
            throw new Error(errBody.error || `HTTP ${res.status}`);
        }

        const booking = await res.json();
        showOutput(`<p>Booking created successfully:</p>` + renderBooking(booking));
        e.target.reset();
    } catch (err) {
        showError(err.message);
    }
});

// ---- GET BY ID ----
document.getElementById("getByIdForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("searchId").value;

    try {
        const res = await fetch(`${BASE_URL}/${id}`, {
            headers: { "X-API-KEY": getApiKey() }
        });

        if (res.status === 404) {
            showOutput("No booking found with that ID.");
            return;
        }
        if (!res.ok) {
            const errBody = await res.json().catch(() => ({}));
            throw new Error(errBody.error || `HTTP ${res.status}`);
        }

        const booking = await res.json();
        showOutput(renderBooking(booking));
    } catch (err) {
        showError(err.message);
    }
});

// ---- GET BY USER ----
document.getElementById("getByUserForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const userId = document.getElementById("searchUserId").value;

    try {
        const res = await fetch(`${BASE_URL}/user/${userId}`, {
            headers: { "X-API-KEY": getApiKey() }
        });

        if (!res.ok) {
            const errBody = await res.json().catch(() => ({}));
            throw new Error(errBody.error || `HTTP ${res.status}`);
        }

        const bookings = await res.json();
        if (bookings.length === 0) {
            showOutput("No bookings found for this user.");
            return;
        }

        showOutput(bookings.map(renderBooking).join(""));
    } catch (err) {
        showError(err.message);
    }
});

// ---- CANCEL ----
async function cancelBooking(id) {
    try {
        const res = await fetch(`${BASE_URL}/${id}/cancel`, {
            method: "PUT",
            headers: { "X-API-KEY": getApiKey() }
        });

        if (!res.ok) {
            const errBody = await res.json().catch(() => ({}));
            throw new Error(errBody.error || `HTTP ${res.status}`);
        }

        const booking = await res.json();
        showOutput(`<p>Booking cancelled:</p>` + renderBooking(booking));
    } catch (err) {
        showError(err.message);
    }
}

// ---- DELETE ----
async function deleteBooking(id) {
    try {
        const res = await fetch(`${BASE_URL}/${id}`, {
            method: "DELETE",
            headers: { "X-API-KEY": getApiKey() }
        });

        if (!res.ok && res.status !== 204) {
            const errBody = await res.json().catch(() => ({}));
            throw new Error(errBody.error || `HTTP ${res.status}`);
        }

        showOutput(`Booking #${id} deleted successfully.`);
    } catch (err) {
        showError(err.message);
    }
}