function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  if (window.location.pathname === "/") {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let headerContent = "";

  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  } else if (role === "doctor") {
    headerContent += `
      <button id="homeBtn">Home</button>
      <a href="#" id="logoutBtn">Logout</a>
    `;
  } else if (role === "patient") {
    headerContent += `
      <button id="loginBtn">Login</button>
      <button id="signupBtn">Sign Up</button>
    `;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="homeBtn">Home</button>
      <button id="appointmentsBtn">Appointments</button>
      <a href="#" id="logoutPatientBtn">Logout</a>
    `;
  }

  headerDiv.innerHTML = headerContent;
  attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => window.openModal && window.openModal("addDoctor"));
  }

  const homeBtn = document.getElementById("homeBtn");
  if (homeBtn) {
    homeBtn.addEventListener("click", () => {
      window.location.href = "/";
    });
  }

  const appointmentsBtn = document.getElementById("appointmentsBtn");
  if (appointmentsBtn) {
    appointmentsBtn.addEventListener("click", () => {
      window.location.href = "/appointments.html";
    });
  }

  const loginBtn = document.getElementById("loginBtn");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => window.openModal("patientLogin"));
  }

  const signupBtn = document.getElementById("signupBtn");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => window.openModal("patientSignup"));
  }

  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", (e) => {
      e.preventDefault();
      logout();
    });
  }

  const logoutPatientBtn = document.getElementById("logoutPatientBtn");
  if (logoutPatientBtn) {
    logoutPatientBtn.addEventListener("click", (e) => {
      e.preventDefault();
      logoutPatient();
    });
  }
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

document.addEventListener("DOMContentLoaded", renderHeader);