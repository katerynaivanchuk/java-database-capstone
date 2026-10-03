import { deleteDoctor } from "../services/doctorService.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "./bookingOverlay.js";

export function createDoctorCard(doctor) {
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  const role = localStorage.getItem("userRole");

  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name || "Dr. Unknown";

  const specialization = document.createElement("p");
  specialization.textContent = `Specialty: ${doctor.specialization || doctor.specialty || "N/A"}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email || "N/A"} | Room: ${doctor.roomNumber || "N/A"}`;

  const availability = document.createElement("p");
  const availabilityText = Array.isArray(doctor.availableTimes) && doctor.availableTimes.length
  ? doctor.availableTimes
      .map((a) => `${String(a.dayOfWeek).slice(0, 3)} ${String(a.startTime).slice(0, 5)}-${String(a.endTime).slice(0, 5)}`)
      .join(", ")
  : "Not specified";

  infoDiv.append(name, specialization, email, availability);

  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("delete-btn");

    removeBtn.addEventListener("click", async () => {
      if (!confirm(`Are you sure you want to delete ${doctor.name}?`)) return;

      const token = localStorage.getItem("token");
      const result = await deleteDoctor(doctor.id, token);
      if (result.success) {
        card.remove();
      } else {
        alert("Failed to delete doctor: " + result.message);
      }
    });

    actionsDiv.appendChild(removeBtn);
  } else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");
    bookNow.addEventListener("click", () => {
      alert("Patient needs to login first.");
    });
    actionsDiv.appendChild(bookNow);
  } else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      const patientData = await getPatientData(token);
      if (!patientData) {
        alert("Could not load patient data for booking.");
        return;
      }
      showBookingOverlay(e, doctor, patientData);
    });

    actionsDiv.appendChild(bookNow);
  }

  card.append(infoDiv, actionsDiv);
  return card;
}