import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

/**
 * Отримує список усіх лікарів із сервера
 * @returns {Promise<Array>} Масив об'єктів лікарів або порожній масив у разі помилки
 */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/**
 * Видаляє лікаря за ID (потрібен токен адміністратора)
 * @param {string|number} id - Унікальний ідентифікатор лікаря
 * @param {string} token - Токен авторизації
 * @returns {Promise<{success: boolean, message: string}>} Статус та повідомлення
 */
export async function deleteDoctor(id, token) {
  try {
    const url = `${DOCTOR_API}/${id}?token=${encodeURIComponent(token || "")}`;
    const response = await fetch(url, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.ok) {
      const data = await response.json().catch(() => ({}));
      return {
        success: true,
        message: data.message || "Doctor deleted successfully",
      };
    } else {
      const errorData = await response.json().catch(() => ({}));
      return {
        success: false,
        message: errorData.message || "Failed to delete doctor",
      };
    }
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: error.message || "Network error occurred",
    };
  }
}

/**
 * Зберігає (додає) нового лікаря в систему
 * @param {Object} doctor - Об'єкт з даними лікаря (name, email, specialization, availability тощо)
 * @param {string} token - Токен авторизації адміністратора
 * @returns {Promise<{success: boolean, message: string, data?: Object}>}
 */
export async function saveDoctor(doctor, token) {
  try {
    const url = `${DOCTOR_API}?token=${encodeURIComponent(token || "")}`;
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(doctor),
    });

    if (response.ok) {
      const data = await response.json().catch(() => ({}));
      return {
        success: true,
        message: "Doctor saved successfully",
        data,
      };
    } else {
      const errorData = await response.json().catch(() => ({}));
      return {
        success: false,
        message: errorData.message || "Failed to save doctor",
      };
    }
  } catch (error) {
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: error.message || "Network error occurred",
    };
  }
}

/**
 * Фільтрує лікарів за параметрами name, time та specialty
 * @param {string} name - Ім'я лікаря для пошуку
 * @param {string} time - Час/день доступності
 * @param {string} specialty - Спеціалізація
 * @returns {Promise<Array>} Відфільтрований масив лікарів або порожній масив
 */
export async function filterDoctors(name, time, specialty) {
  try {
    // Формуємо URL з параметрами маршутизації або Query params залежно від специфікації
    // Якщо параметри передаються через route (наприклад /doctor/filter/Name/Time/Specialty)
    const encodedName = name ? encodeURIComponent(name) : "null";
    const encodedTime = time ? encodeURIComponent(time) : "null";
    const encodedSpecialty = specialty ? encodeURIComponent(specialty) : "null";

    const url = `${DOCTOR_API}/${encodedName}/${encodedTime}/${encodedSpecialty}`;

    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Failed to filter doctors: " + error.message);
    return [];
  }
}