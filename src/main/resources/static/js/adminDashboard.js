import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

document.addEventListener("DOMContentLoaded", () => {
  // 1. Ініціалізація завантаження лікарів при відкритті сторінки
  loadDoctorCards();

  // 2. Прив'язка події для кнопки "Add Doctor"
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => {
      openModal("addDoctor");
    });
  }

  // 3. Прив'язка подій для пошуку та фільтрації
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);

  // 4. Прив'язка події відправки форми додавання лікаря (якщо форма є в DOM/модальному вікні)
  const addDoctorForm = document.getElementById("addDoctorForm");
  if (addDoctorForm) {
    addDoctorForm.addEventListener("submit", adminAddDoctor);
  }
});

/**
 * Завантажує список усіх лікарів із бекенду та відображає їх
 */
async function loadDoctorCards() {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = ""; // Очищаємо вміст перед завантаженням

  const doctors = await getDoctors();
  renderDoctorCards(doctors);
}

/**
 * Допоміжна функція для рендерингу масиву лікарів
 * @param {Array} doctors - Масив об'єктів лікарів
 */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p class='no-doctors'>No doctors found</p>";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Обробник зміни значення в полях пошуку та фільтрації
 */
async function filterDoctorsOnChange() {
  const nameVal = document.getElementById("searchBar")?.value.trim() || "";
  const timeVal = document.getElementById("filterTime")?.value || "";
  const specialtyVal = document.getElementById("filterSpecialty")?.value || "";

  // Отримуємо відфільтрований список через сервіс
  const filteredList = await filterDoctors(nameVal, timeVal, specialtyVal);
  renderDoctorCards(filteredList);
}

/**
 * Обробляє відправку форми додавання нового лікаря
 * @param {Event} event 
 */
async function adminAddDoctor(event) {
  event.preventDefault();

  // Перевірка наявності токена адміністратора
  const token = localStorage.getItem("token") || sessionStorage.getItem("token");
  if (!token) {
    alert("Authentication token missing. Please log in again.");
    return;
  }

  // Збір значень чекбоксів доступності (якщо вони використовуються)
  const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
  const selectedTimes = Array.from(availabilityCheckboxes).map((cb) => cb.value);

  // Формуємо об'єкт даних лікаря
  const doctorData = {
    name: document.getElementById("docName")?.value || "",
    specialty: document.getElementById("docSpecialty")?.value || "",
    email: document.getElementById("docEmail")?.value || "",
    password: document.getElementById("docPassword")?.value || "",
    mobile: document.getElementById("docMobile")?.value || "",
    availability: selectedTimes.length > 0 ? selectedTimes : (document.getElementById("docAvailability")?.value || ""),
  };

  // Надсилаємо POST-запит через doctorServices.js
  const result = await saveDoctor(doctorData, token);

  if (result.success) {
    alert(result.message || "Doctor added successfully!");
    
    // Закриваємо модальне вікно та очищаємо форму
    const modal = document.getElementById("addDoctorModal");
    if (modal) modal.style.display = "none";
    
    const form = document.getElementById("addDoctorForm");
    if (form) form.reset();

    // Оновлюємо список лікарів на сторінці
    loadDoctorCards();
  } else {
    alert("Error: " + (result.message || "Failed to add doctor"));
  }
}