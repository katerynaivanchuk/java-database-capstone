import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

function extractDoctors(data) {
  return Array.isArray(data) ? data : data?.doctors || [];
}

export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return extractDoctors(await response.json());
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}/${encodeURIComponent(token || "")}`, {
      method: "DELETE",
    });
    const data = await response.json().catch(() => ({}));
    return {
      success: response.ok,
      message: data.message || (response.ok ? "Doctor deleted successfully" : "Failed to delete doctor"),
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return { success: false, message: error.message || "Network error occurred" };
  }
}

export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${encodeURIComponent(token || "")}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });
    const data = await response.json().catch(() => ({}));
    return {
      success: response.ok,
      message: data.message || (response.ok ? "Doctor saved successfully" : "Failed to save doctor"),
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return { success: false, message: error.message || "Network error occurred" };
  }
}

export async function filterDoctors(name, time, specialty) {
  try {
    const n = name ? encodeURIComponent(name) : "null";
    const t = time ? encodeURIComponent(time) : "null";
    const s = specialty ? encodeURIComponent(specialty) : "null";

    const response = await fetch(`${DOCTOR_API}/filter/${n}/${t}/${s}`);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    return extractDoctors(await response.json());
  } catch (error) {
    console.error("Error filtering doctors:", error);
    return [];
  }
}