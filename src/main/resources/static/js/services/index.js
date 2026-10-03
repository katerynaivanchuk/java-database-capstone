import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
import { selectRole } from "../render.js";


console.log("index.js loaded");

const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

// Прив'язуємо кнопки після завантаження сторінки
document.addEventListener("DOMContentLoaded", () => {
  const adminBtn = document.getElementById("role-admin");
  if (adminBtn) {
    adminBtn.addEventListener("click", () => openModal("adminLogin"));
  }

  const doctorBtn = document.getElementById("role-doctor");
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => openModal("doctorLogin"));
  }

  const patientBtn = document.getElementById("role-patient");
  if (patientBtn) {
    patientBtn.addEventListener("click", () => selectRole("patient"));
  }
});

/**
 * Обробник входу для Адміністратора
 */
export async function adminLoginHandler() {
  const username = document.getElementById("adminUsername")?.value || "";
  const password = document.getElementById("adminPassword")?.value || "";

  try {
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      alert("Invalid credentials!");
      return;
    }

    const data = await response.json();
    const token = typeof data === "string" ? data : data.token || data.accessToken;
    if (!token) {
      alert("Server did not return a token");
      return;
    }

    localStorage.setItem("token", token);
    selectRole("admin");
  } catch (error) {
    console.error("Error during admin login:", error);
    alert("Login failed, try again");
  }
}

/**
 * Обробник входу для Лікаря
 */
export async function doctorLoginHandler() {
  const email = document.getElementById("doctorEmail")?.value || "";
  const password = document.getElementById("doctorPassword")?.value || "";

  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ identifier: email, password })
    });

    if (!response.ok) {
      alert("Invalid credentials!");
      return;
    }

    const data = await response.json();
    const token = typeof data === "string" ? data : data.token || data.accessToken;
    if (!token) {
      alert("Server did not return a token");
      return;
    }

    localStorage.setItem("token", token);
    selectRole("doctor");
  } catch (error) {
    console.error("Error during doctor login:", error);
    alert("Login failed, try again");
  }
}

// Робимо функції глобальними, щоб onclick у модалці міг їх викликати
window.adminLoginHandler = adminLoginHandler;
window.doctorLoginHandler = doctorLoginHandler;