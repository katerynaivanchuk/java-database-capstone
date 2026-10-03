import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

let patientTableBody;
let selectedDate;
let patientName = "null";

function todayLocal() {
  const d = new Date();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${mm}-${dd}`;
}

document.addEventListener("DOMContentLoaded", () => {
  patientTableBody = document.getElementById("patientTableBody");
  selectedDate = todayLocal();

  const datePicker = document.getElementById("appointmentDateFilter");
  if (datePicker) {
    datePicker.value = selectedDate;
    datePicker.addEventListener("change", (e) => {
      selectedDate = e.target.value;
      loadAppointments();
    });
  }

  const todayButton = document.getElementById("todayAppointmentsBtn");
  if (todayButton) {
    todayButton.addEventListener("click", () => {
      selectedDate = todayLocal();
      if (datePicker) datePicker.value = selectedDate;
      loadAppointments();
    });
  }

  const searchBar = document.getElementById("searchBar");
  if (searchBar) {
    searchBar.addEventListener("input", (e) => {
      const value = e.target.value.trim();
      patientName = value === "" ? "null" : value;
      loadAppointments();
    });
  }

  loadAppointments();
});

async function loadAppointments() {
  if (!patientTableBody) return;
  patientTableBody.innerHTML = "";

  const token = localStorage.getItem("token");

  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    if (!appointments || appointments.length === 0) {
      patientTableBody.innerHTML = `
        <tr><td colspan="5" style="text-align:center; padding:1.5rem;">
          No appointments found
        </td></tr>`;
      return;
    }

    appointments.forEach((appointment) => {
      patientTableBody.appendChild(createPatientRow(appointment));
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    patientTableBody.innerHTML = `
      <tr><td colspan="5" style="text-align:center; color:red; padding:1.5rem;">
        Failed to load appointments. Please try again later.
      </td></tr>`;
  }
}