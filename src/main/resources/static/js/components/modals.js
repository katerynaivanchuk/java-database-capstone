export function openModal(type) {
    const body = document.getElementById("modal-body");
    const modal = document.getElementById("modal");

    if (type === "adminLogin") {
        body.innerHTML = `
            <h3>Admin Login</h3>
            <input id="adminUsername" placeholder="Username">
            <input id="adminPassword" type="password" placeholder="Password">
            <button onclick="adminLoginHandler()">Login</button>`;
    } else if (type === "doctorLogin") {
        body.innerHTML = `
            <h3>Doctor Login</h3>
            <input id="doctorEmail" placeholder="Email">
            <input id="doctorPassword" type="password" placeholder="Password">
            <button onclick="doctorLoginHandler()">Login</button>`;
    } else if (type === "patientLogin") {
        body.innerHTML = `
            <h3>Patient Login</h3>
            <input id="loginEmail" placeholder="Email">
            <input id="loginPassword" type="password" placeholder="Password">
            <button type="button" onclick="loginPatient(event)">Login</button>`;
    } else if (type === "patientSignup") {
        body.innerHTML = `
            <h3>Patient Sign Up</h3>
            <input id="signupName" placeholder="Name">
            <input id="signupEmail" placeholder="Email">
            <input id="signupPassword" type="password" placeholder="Password">
            <input id="signupPhone" placeholder="Phone">
            <input id="signupAddress" placeholder="Address">
            <button type="button" onclick="signupPatient(event)">Sign Up</button>`;
    }  else if (type === "addDoctor") {
        body.innerHTML = `
            <h3>Add Doctor</h3>
            <input id="docName" placeholder="Name (min 3 characters)">
            <input id="docEmail" placeholder="Email">
            <input id="docPassword" type="password" placeholder="Password (min 6)">
            <select id="docGender">
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
            </select>
            <input id="docPhone" placeholder="Phone (10 digits)">
            <input id="docSpecialty" placeholder="Specialty (e.g. Cardiology)">
            <input id="docLicense" placeholder="License number (5-15 chars)">
            <input id="docRoom" placeholder="Room number">
            <label>Working hours:</label>
            <input id="docStart" type="time" value="09:00">
            <input id="docEnd" type="time" value="17:00">
            <div>
                <label><input type="checkbox" name="docDay" value="MONDAY" checked> Mon</label>
                <label><input type="checkbox" name="docDay" value="TUESDAY" checked> Tue</label>
                <label><input type="checkbox" name="docDay" value="WEDNESDAY" checked> Wed</label>
                <label><input type="checkbox" name="docDay" value="THURSDAY" checked> Thu</label>
                <label><input type="checkbox" name="docDay" value="FRIDAY" checked> Fri</label>
            </div>
            <button type="button" onclick="adminAddDoctor()">Save</button>`;
    }

    modal.classList.remove("hidden");
    modal.style.display = "flex";
    document.getElementById("closeModal").onclick = () => {
        modal.classList.add("hidden");
        modal.style.display = "none";
    };
}

window.openModal = openModal;