export function createPatientRow(appointment) {
  // Якщо бекенд віддає запис із вкладеним пацієнтом, беремо його
  const p = appointment.patient || appointment;

  const fullName =
    [p.firstName, p.lastName].filter(Boolean).join(" ") || p.name || "N/A";

  const row = document.createElement("tr");
  row.innerHTML = `
    <td>${p.id ?? "N/A"}</td>
    <td>${fullName}</td>
    <td>${p.phoneNumber || p.phone || "N/A"}</td>
    <td>${p.email || "N/A"}</td>
    <td></td>`;

  const btn = document.createElement("button");
  btn.textContent = "Prescription";
  btn.addEventListener("click", () => {
    alert("Prescription for " + fullName + " will be connected in the next step.");
  });
  row.lastElementChild.appendChild(btn);

  return row;
}