import { createDoctorCard } from "./components/doctorCard.js";
import { getDoctors, filterDoctors } from "./services/doctorService.js";
import { patientLogin, patientSignup } from "./services/patientServices.js";

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
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";

  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
    contentDiv.innerHTML = "<p>Failed to load doctors. Please try again later.</p>";
  }
}

export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    return;
  }

  doctors.forEach((doctor) => {
    contentDiv.appendChild(createDoctorCard(doctor));
  });
}

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

window.signupPatient = async function (event) {
  if (event) event.preventDefault();

  const name = document.getElementById("signupName")?.value || "";
  const email = document.getElementById("signupEmail")?.value || "";
  const password = document.getElementById("signupPassword")?.value || "";
  const phone = document.getElementById("signupPhone")?.value || "";
  const address = document.getElementById("signupAddress")?.value || "";

  const result = await patientSignup({ name, email, password, phone, address });

  if (result.success) {
    alert(result.message || "Signup successful!");
    const modal = document.getElementById("modal");
    if (modal) {
      modal.classList.add("hidden");
      modal.style.display = "none";
    }
    window.location.reload();
  } else {
    alert("Signup Failed: " + (result.message || "Please try again."));
  }
};

window.loginPatient = async function (event) {
  if (event) event.preventDefault();

  const email = document.getElementById("loginEmail")?.value || "";
  const password = document.getElementById("loginPassword")?.value || "";

  try {
    const response = await patientLogin({ identifier: email, password });

    if (response.ok) {
      const data = await response.json();
      const token = data.token || data.jwt;

      if (!token) {
        alert("Server did not return a token");
        return;
      }

      localStorage.setItem("token", token);
      localStorage.setItem("userRole", "loggedPatient");
      window.location.href = "/pages/patientDashboard.html";
    } else {
      const errorData = await response.json().catch(() => ({}));
      alert("Login Failed: " + (errorData.message || "Invalid credentials"));
    }
  } catch (error) {
    console.error("Error during patient login:", error);
    alert("An error occurred during login. Please check your network connection.");
  }
};