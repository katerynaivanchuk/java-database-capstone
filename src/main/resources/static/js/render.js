export function selectRole(role) {
    const token = localStorage.getItem("token");
    localStorage.setItem("userRole", role);
    if (role === "admin") window.location.href = "/adminDashboard/" + token;
    else if (role === "doctor") window.location.href = "/doctorDashboard/" + token;
    else window.location.href = "/pages/patientDashboard.html";
}