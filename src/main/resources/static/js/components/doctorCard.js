// Імпортуємо необхідні допоміжні сервісні функції та оверлей
import { deleteDoctor } from "./services/doctorServices.js";
import { getPatientData } from "./services/patientServices.js";
import { showBookingOverlay } from "./bookingOverlay.js";

/**
 * Створює картку лікаря
 * @param {Object} doctor - Об'єкт з даними лікаря (id, name, specialization, email, availability)
 * @returns {HTMLElement} - Готовий DOM-елемент картки (.doctor-card)
 */
export function createDoctorCard(doctor) {
  // 1. Створюємо головний контейнер картки
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // 2. Отримуємо роль поточного користувача
  const role = localStorage.getItem("userRole");

  // 3. Створюємо секцію з інформацією про лікаря
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name || "Dr. Unknown";

  const specialization = document.createElement("p");
  specialization.textContent = `Specialty: ${doctor.specialization || "N/A"}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email || "N/A"}`;

  const availability = document.createElement("p");
  // Якщо availability є масивом, об'єднуємо через кому, інакше виводимо як є
  const availabilityText = Array.isArray(doctor.availability)
    ? doctor.availability.join(", ")
    : doctor.availability || "Not specified";
  availability.textContent = `Availability: ${availabilityText}`;

  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // 4. Створюємо контейнер для кнопок дій
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // 5. Умовне додавання кнопок залежно від ролі
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("delete-btn");

    removeBtn.addEventListener("click", async () => {
      const confirmed = confirm(`Are you sure you want to delete ${doctor.name}?`);
      if (!confirmed) return;

      const token = localStorage.getItem("token");
      try {
        await deleteDoctor(doctor.id, token);
        // При успішному видаленні з бекенду видаляємо картку з DOM
        card.remove();
      } catch (error) {
        alert("Failed to delete doctor: " + error.message);
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
      try {
        const patientData = await getPatientData(token);
        showBookingOverlay(e, doctor, patientData);
      } catch (error) {
        alert("Could not load patient data for booking: " + error.message);
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // 6. Фінальна збірка картки
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}