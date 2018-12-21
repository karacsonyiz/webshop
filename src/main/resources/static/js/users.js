window.addEventListener('load', updateTable);

function updateTable() {
    fetch("api/users")
        .then(function (response) {
            return response.json();
        })
        .then(function(jsonData) {
            fillTable(jsonData);
            let userForm = document.getElementById("user-form");
            userForm.onsubmit = modifyUser;
        });
}

function fillTable(users) {
    let tbody = document.getElementById("users-tbody");
    tbody.innerHTML = "";
    for (let i = 0; i < users.length; i++) {
        let user  = users[i];
        let tr = document.createElement("tr");
        tr["raw-data"] = user;

        let idTd = document.createElement("td");
        idTd.innerHTML = user.id;
        tr.appendChild(idTd);

        let nameTd = document.createElement("td");
        nameTd.innerHTML = user.loginName;
        tr.appendChild(nameTd);

        let fullNameTd = document.createElement("td");
        fullNameTd.innerHTML = user.fullName;
        tr.appendChild(fullNameTd);

        let buttonsTd = document.createElement("td");
                let editButton = document.createElement("button");
                let deleteButton = document.createElement("button");
                editButton.setAttribute("class", "btn btn-primary");
                deleteButton.setAttribute("class", "btn btn-danger");
                editButton.innerHTML = "Szerkesztés";
                deleteButton.innerHTML = "Törlés";
                editButton.onclick = editButtonClick;
                deleteButton.onclick = handleDeleteButtonOnClick;
                buttonsTd.appendChild(editButton);
                buttonsTd.appendChild(deleteButton);
                tr.appendChild(buttonsTd);

        tbody.appendChild(tr);
 }
    }

let editedUser = null;

function editButtonClick() {
    let user = this.parentElement.parentElement["raw-data"];
    editedUser = user;

    let idInput = document.getElementById("id-input");
    idInput.value = user.id;

    let nameInput = document.getElementById("fullname-input");
    nameInput.value= user.fullName;
}

function handleReset() {
    editedUser = null;
    let idInput = document.getElementById("id-input");
    idInput.value = "";
    let nameInput = document.getElementById("fullname-input");
    nameInput.value = "";
    let passwordInput = document.getElementById("password-input");
    passwordInput.value = "";
}

function modifyUser() {
    if (editedUser == null) {
        return;
    }
    let idInput = document.getElementById("id-input");
    let nameInput = document.getElementById("fullname-input");
    let passwordInput = document.getElementById("password-input");

    let id = idInput.value;
    let name = nameInput.value;
    let password = passwordInput.value;

    let user = {"id": id,
               };

    if (name.length != 0) {
        user.fullName = name;
    }
    if (password.length != 0) {
        user.password = password;
    }

    fetch("api/users/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json; charset=utf-8"
                },
        body: JSON.stringify(user)
    }).then(function(response) {
        return response.json()
    }).then(function(response) {
        alert(response.message);
        updateTable();
        handleReset();
    });
        return false;
}

function handleDeleteButtonOnClick() {
    var result = confirm("Biztosan törli a kijelölt felhasználót?");
    if (result) {
        let user = this.parentElement.parentElement["raw-data"];

        fetch("api/users/" + user.id, {
            method: "DELETE",
        })
        .then(function(response) {
            updateTable();
        });
    }
}