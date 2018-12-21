window.onload = function() {
    updateTable();

    let categoryForm = document.getElementById("category-form");
    categoryForm.onsubmit = handleSubmit;
    categoryForm.onreset = handleReset;
}

function updateTable() {
    fetch("api/categories")
        .then(function (response) {
            return response.json();
        })
        .then(function(jsonData) {
            fillTable(jsonData);
        });
}

function fillTable(categories) {
    let tbody = document.getElementById("categories-tbody");
    tbody.innerHTML = "";
    for (let i = 0; i < categories.length; i++) {
        let category  = categories[i];
        let tr = document.createElement("tr");
        tr["raw-data"] = category;

        let idTd = document.createElement("td");
        idTd.innerHTML = category.id;
        tr.appendChild(idTd);

        let nameTd = document.createElement("td");
        nameTd.innerHTML = category.name;
        tr.appendChild(nameTd);

        let positionTd = document.createElement("td");
        positionTd.innerHTML = category.positionNumber;
        tr.appendChild(positionTd);

        let buttonsTd = document.createElement("td");
        let editButton = document.createElement("button");
        let deleteButton = document.createElement("button");
        editButton.setAttribute("class", "btn btn-primary");
        deleteButton.setAttribute("class", "btn btn-danger");
        editButton.innerHTML = "Szerkesztés";
        deleteButton.innerHTML = "Törlés";
        editButton.onclick = handleEditButtonOnclick;
        deleteButton.onclick = handleDeleteButtonOnClick;
        buttonsTd.appendChild(editButton);
        buttonsTd.appendChild(deleteButton);
        tr.appendChild(buttonsTd);

        tbody.appendChild(tr);
    }
}

let editedCategory = null;

function handleEditButtonOnclick() {
    let category = this.parentElement.parentElement["raw-data"];
    editedCategory = category;

    let nameInput = document.getElementById("name-input");
    nameInput.value = category.name;

    let positionInput = document.getElementById("position-input");
    positionInput.value = category.positionNumber;

    let submitButton = document.getElementById("submit-button");
    submitButton.value = "Mentés";
}

function handleReset() {
    editedCategory = null;
    let submitButton = document.getElementById("submit-button");
    submitButton.value = "Kategória létrehozása";
}

function handleSubmit() {
    let nameInput = document.getElementById("name-input");
    let positionInput = document.getElementById("position-input");

    let name = nameInput.value;
    let position = positionInput.value;

    let category = {"name": name,
                    "positionNumber": position
                    }

    let url = "api/categories";
    if (editedCategory !== null) {
        category.id = editedCategory.id;
        url += "/update";
    }

    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json; charset=utf-8"
                },
        body: JSON.stringify(category)
    }).then(function(response) {
        return response.json()
    }).then(function(response) {
        alert(response.message);
        updateTable();
        document.getElementById("category-form").reset();

    });
    return false;
}

function handleDeleteButtonOnClick() {
    var result = confirm("Biztosan törli a kijelölt kategóriát?");
    if (result) {
        let category = this.parentElement.parentElement["raw-data"];

        fetch("api/categories/" + category.id, {
            method: "DELETE",
        })
        .then(function(response) {
            updateTable();
            document.getElementById("category-form").reset();
        })
    }
}
