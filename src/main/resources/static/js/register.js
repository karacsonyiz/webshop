window.onload = function() {
    let registerForm = document.getElementById("register-form");
    registerForm.onsubmit = handleSubmit;
    registerForm.onreset = handleReset;
}

function handleSubmit() {
    let lastNameInput = document.getElementById("last-name");
    let firstNameInput = document.getElementById("first-name");
    let usernameInput = document.getElementById("loginname");
    let passwordInput = document.getElementById("password");
    let passwordRepeatInput = document.getElementById("password-repeat");

    let fullName = lastNameInput.value + " " + firstNameInput.value;
    let username = usernameInput.value;
    let password = passwordInput.value;
    let passwordRepeat = passwordRepeatInput.value;

    if (password !== passwordRepeat) {
        alert("A két jelszó nem egyezik meg.")
    }

    let user = {
                "loginName": username,
                "fullName": fullName,
                "password": password
                }

    fetch("api/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json; charset=utf-8"
                    },
            body: JSON.stringify(user)
    }).then(function(response) {
    if (response.status === 409) {
        alert("A megadott felhasználónév már foglalt.");
    } else if (response.status === 406) {
        alert("A jelszó nem felel meg az elvárt paramétereknek.");
    } else {
        alert("Sikeres regisztráció.");
        handleReset();
        }
    });
    return false;

}

function handleReset() {
    let lastNameInput = document.getElementById("last-name");
    lastNameInput.value = "";

    let firstNameInput = document.getElementById("first-name");
    firstNameInput.value = "";

    let usernameInput = document.getElementById("loginname");
    usernameInput.value = "";

    let passwordInput = document.getElementById("password");
    passwordInput.value = "";

    let passwordRepeatInput = document.getElementById("password-repeat");
    passwordRepeatInput.value = "";
}