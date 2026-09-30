import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// Глобальні змінні та DOM-елементи
let patientTableBody;
let selectedDate;
let token;
let patientName = "null";

document.addEventListener("DOMContentLoaded", () => {
  // Ініціалізація глобальних змінних
  patientTableBody = document.getElementById("patientTableBody");
  token = localStorage.getItem("token") || sessionStorage.getItem("token");

  // Встановлюємо початкову дату — сьогоднішній день (у форматі YYYY-MM-DD)
  const today = new Date();
  selectedDate = today.toISOString().split("T")[0];

  // Встановлюємо початкове значення для елемента datePicker, якщо він присутній
  const datePicker = document.getElementById("datePicker");
  if (datePicker) {
    datePicker.value = selectedDate;
    datePicker.addEventListener("change", (e) => {
      selectedDate = e.target.value;
      loadAppointments();
    });
  }

  // Налаштування кнопки "Today's Appointments"
  const todayButton = document.getElementById("todayButton");
  if (todayButton) {
    todayButton.addEventListener("click", () => {
      const freshToday = new Date().toISOString().split("T")[0];
      selectedDate = freshToday;
      if (datePicker) {
        datePicker.value = freshToday;
      }
      loadAppointments();
    });
  }

  // Налаштування поля пошуку за ім'ям пацієнта
  const searchBar = document.getElementById("searchBar");
  if (searchBar) {
    searchBar.addEventListener("input", (e) => {
      const value = e.target.value.trim();
      patientName = value === "" ? "null" : value;
      loadAppointments();
    });
  }

  // Первинне завантаження записів при відкритті сторінки
  loadAppointments();
});

/**
 * Завантажує та відображає записи на прийом для лікаря з урахуванням дати та пошукового запиту
 */
async function loadAppointments() {
  if (!patientTableBody) {
    patientTableBody = document.getElementById("patientTableBody");
  }

  if (!patientTableBody) return;

  // Очищаємо таблицю перед завантаженням нових даних
  patientTableBody.innerHTML = "";

  try {
    // Отримуємо записи з бекенду через сервіс
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    // Перевірка на відсутність записів або порожній масив
    if (!appointments || appointments.length === 0) {
      patientTableBody.innerHTML = `
        <tr>
          <td colspan="10" style="text-align: center; padding: 1.5rem;">
            No Appointments found for today
          </td>
        </tr>
      `;
      return;
    }

    // Рендеримо кожний рядок запису
    appointments.forEach((appointment) => {
      const row = createPatientRow(appointment);
      patientTableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments for doctor:", error);

    // Відображення резервного повідомлення про помилку в таблиці
    patientTableBody.innerHTML = `
      <tr>
        <td colspan="10" style="text-align: center; color: red; padding: 1.5rem;">
          Failed to load appointments. Please try again later.
        </td>
      </tr>
    `;
  }
}