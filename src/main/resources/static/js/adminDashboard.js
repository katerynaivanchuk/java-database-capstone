import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorService.js";
import { createDoctorCard } from "./components/doctorCard.js";

// header.js викликає window.openModal для кнопки Add Doctor
window.openModal = openModal;

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

async function loadDoctorCards() {
  const doctors = await getDoctors();
  renderDoctorCards(doctors);
}

function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";

  const list = Array.isArray(doctors) ? doctors : doctors?.doctors || [];
  if (list.length === 0) {
    contentDiv.innerHTML = "<p class='no-doctors'>No doctors found</p>";
    return;
  }

  list.forEach((doctor) => contentDiv.appendChild(createDoctorCard(doctor)));
}

async function filterDoctorsOnChange() {
  const nameVal = document.getElementById("searchBar")?.value.trim() || "";
  const timeVal = document.getElementById("filterTime")?.value || "";
  const specialtyVal = document.getElementById("filterSpecialty")?.value || "";

  const filtered = await filterDoctors(nameVal, timeVal, specialtyVal);
  renderDoctorCards(filtered);
}

// Викликається кнопкою Save у модальному вікні
window.adminAddDoctor = async function () {
  const token = localStorage.getItem("token");
  if (!token) {
    alert("Authentication token missing. Please log in again.");
    return;
  }

    const start = document.getElementById("docStart")?.value || "09:00";
  const end = document.getElementById("docEnd")?.value || "17:00";
  const days = Array.from(document.querySelectorAll('input[name="docDay"]:checked')).map((cb) => cb.value);

  const doctorData = {
    name: document.getElementById("docName")?.value || "",
    email: document.getElementById("docEmail")?.value || "",
    password: document.getElementById("docPassword")?.value || "",
    gender: document.getElementById("docGender")?.value,
    phone: document.getElementById("docPhone")?.value || "",
    specialty: document.getElementById("docSpecialty")?.value || "",
    licenseNumber: document.getElementById("docLicense")?.value || "",
    roomNumber: document.getElementById("docRoom")?.value || "",
    availableTimes: days.map((day) => ({ dayOfWeek: day, startTime: start, endTime: end })),
  };

  const result = await saveDoctor(doctorData, token);

  if (result.success) {
    alert(result.message || "Doctor added successfully!");
    const modal = document.getElementById("modal");
    modal.classList.add("hidden");
    modal.style.display = "none";
    loadDoctorCards();
  } else {
    alert("Error: " + (result.message || "Failed to add doctor"));
  }
};