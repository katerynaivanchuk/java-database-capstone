import { API_BASE_URL } from "../config/config.js";

// Базовий ендпоінт для всіх запитів, пов'язаних із пацієнтами
const PATIENT_API = API_BASE_URL + "/patient";

/**
 * Реєстрація нового пацієнта в системі
 * @param {Object} data - Об'єкт з даними пацієнта (name, email, password тощо)
 * @returns {Promise<{success: boolean, message: string}>} Результат реєстрації
 */
export async function patientSignup(data) {
  try {
    // Надсилаємо POST-запит із даними пацієнта
    const response = await fetch(`${PATIENT_API}/signup`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

    // Зчитуємо JSON-відповідь від сервера
    const result = await response.json().catch(() => ({}));

    if (response.ok) {
      return {
        success: true,
        message: result.message || "Registration successful",
      };
    } else {
      return {
        success: false,
        message: result.message || "Signup failed. Please try again.",
      };
    }
  } catch (error) {
    console.error("Error during patient signup:", error);
    return {
      success: false,
      message: error.message || "Network error occurred during signup",
    };
  }
}

/**
 * Авторизація пацієнта (вхід у систему)
 * @param {Object} data - Об'єкт з обліковими даними (email, password)
 * @returns {Promise<Response>} Повний Response об'єкт для обробки токена та статусу на фронтенді
 */
export async function patientLogin(data) {
  try {
    // Надсилаємо POST-запит для аутентифікації
    const response = await fetch(`${PATIENT_API}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

    // Повертаємо повну відповідь fetch для перевірки статусу та зчитування токена у дашборді
    return response;
  } catch (error) {
    console.error("Error during patient login:", error);
    throw error;
  }
}

/**
 * Отримання даних авторизованого пацієнта за токеном
 * @param {string} token - Токен авторизації (наприклад, з localStorage)
 * @returns {Promise<Object|null>} Об'єкт пацієнта або null у разі помилки
 */
export async function getPatientData(token) {
  try {
    // Передаємо токен через query параметр або заголовок відповідно до вимог бекенду
    const url = `${PATIENT_API}/getProfile?token=${encodeURIComponent(token || "")}`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to fetch patient data: ${response.status}`);
    }

    const patientData = await response.json();
    return patientData;
  } catch (error) {
    console.error("Error fetching patient profile data:", error);
    return null;
  }
}

/**
 * Отримання списку записів на прийом (Appointments) пацієнта або лікаря
 * @param {string|number} id - Унікальний ідентифікатор користувача
 * @param {string} token - Токен авторизації
 * @param {string} user - Роль/тип запитувача ("patient" або "doctor")
 * @returns {Promise<Array|null>} Масив записів або null у разі помилки
 */
export async function getPatientAppointments(id, token, user) {
  try {
    // Формуємо динамічний URL для універсального використання у дашбордах пацієнта та лікаря
    const url = `${PATIENT_API}/appointments/${id}?token=${encodeURIComponent(token || "")}&user=${encodeURIComponent(user || "patient")}`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to fetch appointments: ${response.status}`);
    }

    const appointments = await response.json();
    return appointments;
  } catch (error) {
    console.error("Error fetching patient appointments:", error);
    return null;
  }
}

/**
 * Фільтрація записів на прийом за станом та ім'ям
 * @param {string} condition - Стан запису ("pending", "consulted" тощо)
 * @param {string} name - Ім'я для пошуку
 * @param {string} token - Токен авторизації
 * @returns {Promise<Array>} Масив відфільтрованих записів або порожній масив []
 */
export async function filterAppointments(condition, name, token) {
  try {
    // Формуємо URL із параметрами фільтрації
    const encodedCondition = condition ? encodeURIComponent(condition) : "null";
    const encodedName = name ? encodeURIComponent(name) : "null";
    const url = `${PATIENT_API}/filterAppointments/${encodedCondition}/${encodedName}?token=${encodeURIComponent(token || "")}`;

    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`Filter request failed with status: ${response.status}`);
    }

    const filteredAppointments = await response.json();
    return filteredAppointments;
  } catch (error) {
    console.error("Error filtering appointments:", error);
    alert("An error occurred while filtering appointments: " + error.message);
    return [];
  }
}