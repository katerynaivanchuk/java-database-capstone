import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
import { selectRole } from "../render.js"; // допоміжна функція з render.js для збереження ролі та перенаправлення/рендеру

const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

// Налаштовуємо обробники подій після завантаження сторінки
window.onload = function () {
  const adminBtn = document.getElementById("adminLogin");
  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      openModal("adminLogin");
    });
  }

  const doctorBtn = document.getElementById("doctorLogin");
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      openModal("doctorLogin");
    });
  }
};

/**
 * Обробник входу для Адміністратора
 */
export async function adminLoginHandler() {
  const usernameInput = document.getElementById("adminUsername");
  const passwordInput = document.getElementById("adminPassword");

  const username = usernameInput ? usernameInput.value : "";
  const password = passwordInput ? passwordInput.value : "";

  const admin = { username, password };

  try {
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(admin),
    });

    if (response.ok) {
      const data = await response.json();
      // Зберігаємо token (якщо він повертається об'єктом, наприклад data.token або data)
      const token = typeof data === "string" ? data : data.token || data.accessToken;
      if (token) {
        localStorage.setItem("token", token);
      }
      
      // Встановлюємо роль "admin" та викликаємо логіку рендеру
      selectRole("admin");
    } else {
      alert("Invalid credentials!");
    }
  } catch (error) {
    console.error("Error during admin login:", error);
    alert("Invalid credentials!");
  }
}

/**
 * Обробник входу для Лікаря
 */
export async function doctorLoginHandler() {
  const emailInput = document.getElementById("doctorEmail");
  const passwordInput = document.getElementById("doctorPassword");

  const email = emailInput ? emailInput.value : "";
  const password = passwordInput ? passwordInput.value : "";

  const doctor = { email, password };

  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(doctor),
    });

    if (response.ok) {
      const data = await response.json();
      const token = typeof data === "string" ? data : data.token || data.accessToken;
      if (token) {
        localStorage.setItem("token", token);
      }

      // Встановлюємо роль "doctor" та викликаємо логіку рендеру
      selectRole("doctor");
    } else {
      alert("Invalid credentials!");
    }
  } catch (error) {
    console.error("Error during doctor login:", error);
    alert("Invalid credentials!");
  }
}

// Робимо функції глобально доступними у window, 
// щоб HTML-форми або модалки могли легко викликати їх через onclick/onsubmit
window.adminLoginHandler = adminLoginHandler;
window.doctorLoginHandler = doctorLoginHandler;