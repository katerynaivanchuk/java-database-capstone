export async function getAllAppointments(date, patientName, token) {
  const name = patientName ? encodeURIComponent(patientName) : "null";
  const url = `/appointments/${date}/${name}/${encodeURIComponent(token || "")}`;

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const data = await response.json();
  return Array.isArray(data) ? data : data.appointments || [];
}