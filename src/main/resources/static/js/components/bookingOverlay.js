export function showBookingOverlay(event, doctor, patientData) {
  const body = document.getElementById("modal-body");
  const modal = document.getElementById("modal");
  if (!body || !modal) return;

  body.innerHTML = `
    <h3>Book appointment</h3>
    <p>Doctor: ${doctor.name || "Unknown"}</p>
    <p>Patient: ${patientData?.name || "Unknown"}</p>
    <p>Booking will be connected in the next step.</p>`;

  modal.classList.remove("hidden");
  modal.style.display = "flex";
  document.getElementById("closeModal").onclick = () => {
    modal.classList.add("hidden");
    modal.style.display = "none";
  };
}