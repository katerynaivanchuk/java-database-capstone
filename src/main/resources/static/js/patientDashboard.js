import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { patientLogin, patientSignup } from "./services/patientServices.js";

document.addEventListener("DOMContentLoaded", () => {
  // 1. Завантаження карток лікарів при відкритті сторінки
  loadDoctorCards();

  // 2. Прив'язка кнопок для відкриття модальних вікон
  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }

  // 3. Прив'язка подій пошуку та фільтрації
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

/**
 * Завантажує всіх лікарів із бекенду та відображає їх у контейнері #content
 */
async function loadDoctorCards() {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = ""; // Очищаємо вміст

  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
    contentDiv.innerHTML = "<p>Failed to load doctors. Please try again later.</p>";
  }
}

/**
 * Утилітарна функція для рендерингу переданого списку лікарів
 * @param {Array} doctors - Масив об'єктів лікарів
 */
export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Обробник подій для пошуку та фільтрації лікарів у реальному часі
 */
async function filterDoctorsOnChange() {
  const nameVal = document.getElementById("searchBar")?.value.trim() || "";
  const timeVal = document.getElementById("filterTime")?.value || "";
  const specialtyVal = document.getElementById("filterSpecialty")?.value || "";

  try {
    const filteredDoctors = await filterDoctors(nameVal, timeVal, specialtyVal);
    renderDoctorCards(filteredDoctors);
  } catch (error) {
    console.error("Error filtering doctors:", error);
    const contentDiv = document.getElementById("content");
    if (contentDiv) {
      contentDiv.innerHTML = "<p>An error occurred while filtering doctors.</p>";
    }
  }
}

/**
 * Глобальна функція реєстрації пацієнта (викликається через onsubmit або форму)
 */
window.signupPatient = async function (event) {
  if (event) event.preventDefault();

  const name = document.getElementById("signupName")?.value || "";
  const email = document.getElementById("signupEmail")?.value || "";
  const password = document.getElementById("signupPassword")?.value || "";
  const phone = document.getElementById("signupPhone")?.value || "";
  const address = document.getElementById("signupAddress")?.value || "";

  const signupData = { name, email, password, phone, address };

  const result = await patientSignup(signupData);

  if (result.success) {
    alert(result.message || "Signup successful!");
    const modal = document.getElementById("patientSignupModal");
    if (modal) modal.style.style = "none";
    window.location.reload();
  } else {
    alert("Signup Failed: " + (result.message || "Please try again."));
  }
};

/**
 * Глобальна функція авторизації пацієнта (викликається через onsubmit або форму)
 */
window.loginPatient = async function (event) {
  if (event) event.preventDefault();

  const email = document.getElementById("loginEmail")?.value || "";
  const password = document.getElementById("loginPassword")?.value || "";

  try {
    const response = await patientLogin({ email, password });

    if (response.ok) {
      const data = await response.json();
      
      // Зберігаємо JWT токен у localStorage
      if (data.token) {
        localStorage.setItem("token", data.token);
      } else if (data.jwt) {
        localStorage.setItem("token", data.jwt);
      }

      // Перенаправляємо в авторизований дашборд
      window.location.href = "loggedPatientDashboard.html";
    } else {
      const errorData = await response.json().catch(() => ({}));
      alert("Login Failed: " + (errorData.message || "Invalid credentials"));
    }
  } catch (error) {
    console.error("Error during patient login:", error);
    alert("An error occurred during login. Please check your network connection.");
  }
};