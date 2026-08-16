// ===== Config — change these if your setup differs =====
const API_BASE = "http://localhost:8081/api/flights";
const API_KEY = "FLIGHT-SERVICE-SECRET-KEY-2026";

// ===== Elements =====
const form = document.getElementById("flightForm");
const formTitle = document.getElementById("formTitle");
const submitBtn = document.getElementById("submitBtn");
const cancelEditBtn = document.getElementById("cancelEditBtn");
const flightsBody = document.getElementById("flightsBody");
const flightCount = document.getElementById("flightCount");
const statusMessage = document.getElementById("statusMessage");

const searchOrigin = document.getElementById("searchOrigin");
const searchDestination = document.getElementById("searchDestination");
const searchBtn = document.getElementById("searchBtn");
const clearSearchBtn = document.getElementById("clearSearchBtn");

let editingId = null;

// ===== Helpers =====
function headers(extra = {}) {
  return { "X-API-KEY": API_KEY, ...extra };
}

function showMessage(text, type = "") {
  statusMessage.textContent = text;
  statusMessage.className = "status-message " + type;
  if (text) {
    setTimeout(() => { statusMessage.textContent = ""; statusMessage.className = "status-message"; }, 4000);
  }
}

function formatDateTime(iso) {
  if (!iso) return "-";
  const d = new Date(iso);
  if (isNaN(d)) return iso;
  return d.toLocaleString(undefined, { month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" });
}

function toInputDateTime(iso) {
  // convert "2026-09-01T08:00:00" -> value usable by <input type="datetime-local">
  if (!iso) return "";
  return iso.length >= 16 ? iso.substring(0, 16) : iso;
}

// ===== API calls =====
async function fetchFlights() {
  try {
    const res = await fetch(API_BASE, { headers: headers() });
    if (!res.ok) throw new Error("Failed to load flights (" + res.status + ")");
    const data = await res.json();
    renderFlights(data);
  } catch (err) {
    showMessage("Error loading flights: " + err.message, "error");
    renderFlights([]);
  }
}

async function searchFlights() {
  const origin = searchOrigin.value.trim();
  const destination = searchDestination.value.trim();
  if (!origin || !destination) {
    showMessage("Enter both origin and destination to search", "error");
    return;
  }
  try {
    const url = `${API_BASE}/search?origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}`;
    const res = await fetch(url, { headers: headers() });
    if (!res.ok) throw new Error("Search failed (" + res.status + ")");
    const data = await res.json();
    renderFlights(data);
    showMessage(`Found ${data.length} flight(s)`, "success");
  } catch (err) {
    showMessage("Error searching: " + err.message, "error");
  }
}

async function createFlight(payload) {
  const res = await fetch(API_BASE, {
    method: "POST",
    headers: headers({ "Content-Type": "application/json" }),
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error("Create failed (" + res.status + ")");
  return res.json();
}

async function updateFlight(id, payload) {
  const res = await fetch(`${API_BASE}/${id}`, {
    method: "PUT",
    headers: headers({ "Content-Type": "application/json" }),
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error("Update failed (" + res.status + ")");
  return res.json();
}

async function deleteFlight(id) {
  const res = await fetch(`${API_BASE}/${id}`, {
    method: "DELETE",
    headers: headers()
  });
  if (!res.ok) throw new Error("Delete failed (" + res.status + ")");
}

// ===== Rendering =====
function renderFlights(flights) {
  flightCount.textContent = flights.length;
  flightsBody.innerHTML = "";

  if (!flights.length) {
    flightsBody.innerHTML = `<tr class="empty-row"><td colspan="9">No flights found</td></tr>`;
    return;
  }

  flights.forEach(f => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${f.id}</td>
      <td>${f.flightNumber ?? "-"}</td>
      <td>${f.origin ?? "-"} → ${f.destination ?? "-"}</td>
      <td>${formatDateTime(f.departureTime)}</td>
      <td>${formatDateTime(f.arrivalTime)}</td>
      <td><span class="status-pill status-${f.status}">${f.status ?? "-"}</span></td>
      <td>${f.seatsAvailable ?? "-"}</td>
      <td>$${f.price != null ? Number(f.price).toFixed(2) : "-"}</td>
      <td>
        <button class="btn btn-secondary btn-small" data-action="edit" data-id="${f.id}">Edit</button>
        <button class="btn btn-danger btn-small" data-action="delete" data-id="${f.id}">Delete</button>
      </td>
    `;
    flightsBody.appendChild(tr);
  });
}

// ===== Form handling =====
function readForm() {
  return {
    flightNumber: document.getElementById("flightNumber").value.trim(),
    origin: document.getElementById("origin").value.trim().toUpperCase(),
    destination: document.getElementById("destination").value.trim().toUpperCase(),
    departureTime: document.getElementById("departureTime").value,
    arrivalTime: document.getElementById("arrivalTime").value,
    status: document.getElementById("status").value,
    seatsAvailable: Number(document.getElementById("seatsAvailable").value),
    price: Number(document.getElementById("price").value)
  };
}

function resetForm() {
  form.reset();
  document.getElementById("flightId").value = "";
  editingId = null;
  formTitle.textContent = "Add New Flight";
  submitBtn.textContent = "Add Flight";
  cancelEditBtn.style.display = "none";
}

function startEdit(flight) {
  editingId = flight.id;
  document.getElementById("flightId").value = flight.id;
  document.getElementById("flightNumber").value = flight.flightNumber ?? "";
  document.getElementById("origin").value = flight.origin ?? "";
  document.getElementById("destination").value = flight.destination ?? "";
  document.getElementById("departureTime").value = toInputDateTime(flight.departureTime);
  document.getElementById("arrivalTime").value = toInputDateTime(flight.arrivalTime);
  document.getElementById("status").value = flight.status ?? "SCHEDULED";
  document.getElementById("seatsAvailable").value = flight.seatsAvailable ?? "";
  document.getElementById("price").value = flight.price ?? "";

  formTitle.textContent = `Edit Flight #${flight.id}`;
  submitBtn.textContent = "Save Changes";
  cancelEditBtn.style.display = "inline-block";
  window.scrollTo({ top: 0, behavior: "smooth" });
}

// ===== Event listeners =====
form.addEventListener("submit", async (e) => {
  e.preventDefault();
  const payload = readForm();

  try {
    if (editingId) {
      await updateFlight(editingId, payload);
      showMessage("Flight updated successfully", "success");
    } else {
      await createFlight(payload);
      showMessage("Flight added successfully", "success");
    }
    resetForm();
    fetchFlights();
  } catch (err) {
    showMessage("Error: " + err.message, "error");
  }
});

cancelEditBtn.addEventListener("click", resetForm);

flightsBody.addEventListener("click", async (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const id = btn.dataset.id;
  const action = btn.dataset.action;

  if (action === "delete") {
    if (!confirm(`Delete flight #${id}? This cannot be undone.`)) return;
    try {
      await deleteFlight(id);
      showMessage("Flight deleted", "success");
      fetchFlights();
    } catch (err) {
      showMessage("Error deleting: " + err.message, "error");
    }
  }

  if (action === "edit") {
    try {
      const res = await fetch(`${API_BASE}/${id}`, { headers: headers() });
      if (!res.ok) throw new Error("Could not load flight");
      const flight = await res.json();
      startEdit(flight);
    } catch (err) {
      showMessage("Error: " + err.message, "error");
    }
  }
});

searchBtn.addEventListener("click", searchFlights);
clearSearchBtn.addEventListener("click", () => {
  searchOrigin.value = "";
  searchDestination.value = "";
  fetchFlights();
});

// ===== Init =====
fetchFlights();
