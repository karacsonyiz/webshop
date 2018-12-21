window.onload = function () {
    updateTable();
    getAddressesForUser();
    let resetButton = document.querySelector("#reset-button");
    resetButton.onclick = handleResetButton;
    let orderButton = document.querySelector("#order-button");
    orderButton.onclick = handleOrderButton;
};

function updateTable() {
    fetch("api/basket")
        .then(function (response) {
            return response.json();
        })
        .then(function (products) {
            fillTable(products);
        });
}

function fillTable(products) {
    let totalPrice = 0
    let tbody = document.querySelector("#basket-tbody");
    tbody.innerHTML = "";
    for (let k = 0; k < products.length; k++) {
        let tr = document.createElement("tr");

        let idTd = document.createElement("td");
        idTd.innerHTML = products[k].id;
        tr.appendChild(idTd);

        let nameTd = document.createElement("td");
        nameTd.innerHTML = products[k].name;
        tr.appendChild(nameTd);

        let producerTd = document.createElement("td");
        producerTd.innerHTML = products[k].producer;
        tr.appendChild(producerTd);

        let currentPriceTd = document.createElement("td");
        currentPriceTd.innerHTML = products[k].currentPrice + " Ft";
        tr.appendChild(currentPriceTd);

        let quantityTd = document.createElement("td");
        quantityTd.className = "quantity-field";
        quantityTd.id = "quantityField" + "Row" + k;
        quantityTd.innerHTML = products[k].quantity + " db";
        quantityTd.value = products[k].quantity;
        quantityTd.addEventListener("click", function () {
            modifyQuantity(k, products[k].id, products[k].quantity);
        });
        tr.appendChild(quantityTd);

        let sumButton = document.createElement("button");
        sumButton.className = "sumSubButtons";
        sumButton.id = "sumButton" + "Row" + k;
        sumButton.innerHTML = "+";
        sumButton.addEventListener("click", function () {
            increaseQuantityByOne(k, products[k].id, products[k].quantity);
        });
        tr.appendChild(sumButton);

        let subButton = document.createElement("button");
        subButton.className = "sumSubButtons";
        subButton.id = "subButton" + "Row" + k;
        subButton.innerHTML = "-";
        subButton.addEventListener("click",
            function () {
                decreaseQuantityByOne(k, products[k].id, products[k].quantity);
            });
        tr.appendChild(subButton);

        deleteButton = document.createElement("input");
        deleteButton.setAttribute("type", "reset");
        deleteButton.setAttribute("id", "deleteButton" + "Id" + products[k].id);
        deleteButton.setAttribute("name", "deleteButton" + "Row" + k);
        deleteButton.setAttribute("class", "btn btn-danger resetProductButtons")
        deleteButton.setAttribute("value", "Törlés");
        deleteButton.onclick = clickingOnResetProductButtons;

        totalPrice += products[k].currentPrice * products[k].quantity;
        tr.appendChild(deleteButton);
        tbody.appendChild(tr);
    }
    let sumParagraph = document.querySelector("#totalPrice");
    sumParagraph.innerHTML = "A kosár tartalmának ára összesen : " + totalPrice + " Ft";

    let orderButton = document.querySelector("#order-button");
    orderButton.disabled = products.length === 0;;

    let resetButton = document.querySelector("#reset-button");
    resetButton.disabled = products.length === 0;
}

function clickingOnResetProductButtons(clickEvent) {
    let deleteButtonId = this.getAttribute("id");
    let productId = parseInt(deleteButtonId.substring(deleteButtonId.indexOf("Id") + 2));
    console.log(productId);
    let url = "api/basket/" + productId;
    if (confirm("Biztos szeretné törölni ezt az elemet a kosárból?")) {
        fetch(url, {
            method: "DELETE"
        }).then(function (response) {
            return response.json()
        }).then(responseJson => updateTable())
    }
}

function getAddressesForUser() {
    fetch("/api/orders/addresses")
        .then(function (response) {
            return response.json();
        })
        .then(function (addresses) {
            fillSelectWithAddresses(addresses);
        });
}

function fillSelectWithAddresses(addresses) {
    let select = document.querySelector("#address-selector");
    for (let i = 0; i < addresses.length; i++) {
        let option = document.createElement("option");
        option.innerHTML = addresses[i];
        option.setAttribute("value", addresses[i])
        select.appendChild(option);
    }
    select.addEventListener("change", function () { fillDeliveryAddress(this.value) })
}

function fillDeliveryAddress(address) {

    let splittedAddressArray = address.split(" ");
    let zipCodeValue = splittedAddressArray[0];
    let splittedAddressWithoutZipCode = splittedAddressArray.splice(1).join(" ");
    let splittedAddressCityAndStreet = splittedAddressWithoutZipCode.split(",");
    let cityValue = splittedAddressCityAndStreet[0];
    let streetValue = splittedAddressCityAndStreet[1].trim();

    let zipCode = document.querySelector("#zip-code-field");
    zipCode.value = zipCodeValue;

    let city = document.querySelector("#city-field");
    city.value = cityValue;

    let street = document.querySelector("#street-field");
    street.value = streetValue;
}

function increaseQuantityInSQL(productId, quantity) {
    let url = "api/basket/" + productId + "/" + quantity + "/increase";
    fetch(url, {
        method: "POST"
    }).then(function (response) {
        return response.json();
    }).then(responseJson => updateTable())
}

function decreaseQuantityInSQL(productId, quantity) {
    let url = "api/basket/" + productId + "/" + quantity + "/decrease";
    fetch(url, {
        method: "POST"
    }).then(function (response) {
        return response.json();
    }).then(responseJson => updateTable())
}

function handleResetButton() {
    if (confirm("Biztos hogy üríteni szeretné a kosár tartalmát?")) {
        fetch("api/basket/", {
            method: "DELETE"
        }).then(function (response) {
            return response.json()
        }).then(responseJson => updateTable())
    }
}

function increaseQuantityByOne(rowNumber, productId, quantity) {
    increaseQuantityInSQL(productId, quantity);
}

function decreaseQuantityByOne(rowNumber, productId, quantity) {
    if (quantity - 1 < 1) {
        let inputName = "deleteButtonRow" + rowNumber;
        document.querySelector(`input[name=${inputName}]`).click();
        return;
    } else {
        decreaseQuantityInSQL(productId, quantity);
    }
}


function modifyQuantity(rowNumber, productId, quantity) {
    var newQuantity = prompt("Adja meg a kívánt mennyiséget", quantity);
    if (newQuantity > 0) {
        let url = "api/basket/" + productId + "/" + quantity + "/" + newQuantity;
        fetch(url, {
            method: "POST"
        }).then(function (response) {
            return response.json();
        }).then(responseJson => updateTable())
    }
}

function handleOrderButton() {

    let zipCode = document.querySelector("#zip-code-field").value.trim().replace(/ /g, '');
    let city = document.querySelector("#city-field").value.trim();
    let street = document.querySelector("#street-field").value.trim();
    let address = zipCode + " " + city + ", " + street;

    let zipCodeDiv = document.querySelector(".zip-code-div");
    let zipCodeField = document.querySelector("#zip-code-field");
    if (zipCode === "") {
        zipCodeField.classList.remove("is-valid");
        zipCodeField.classList.add("is-invalid");
        let feedback = document.createElement("div");
        feedback.setAttribute("class", "invalid-feedback");
        feedback.innerHTML = "Az irányítószám megadása kötelező!";
        if (zipCodeDiv.querySelector(".invalid-feedback") == null) {
            zipCodeDiv.appendChild(feedback);
        }
    } else {
        zipCodeField.classList.remove("is-invalid");
        zipCodeField.classList.add("is-valid");
    }

    let cityDiv = document.querySelector(".city-div");
    let cityField = document.querySelector("#city-field");
    if (zipCode === "") {
        cityField.classList.remove("is-valid");
        cityField.classList.add("is-invalid");
        let feedback = document.createElement("div");
        feedback.setAttribute("class", "invalid-feedback");
        feedback.innerHTML = "A város megadása kötelező!";
        if (cityDiv.querySelector(".invalid-feedback") == null) {
            cityDiv.appendChild(feedback);
        }
    } else {
        cityField.classList.remove("is-invalid");
        cityField.classList.add("is-valid");
    }

    let streetDiv = document.querySelector(".street-div");
    let streetField = document.querySelector("#street-field");
    if (street === "") {
        streetField.classList.remove("is-valid");
        streetField.classList.add("is-invalid");
        let feedback = document.createElement("div");
        feedback.setAttribute("class", "invalid-feedback");
        feedback.innerHTML = "Az utca és a házszám megadása kötelező!";
        if (streetDiv.querySelector(".invalid-feedback") == null) {
            streetDiv.appendChild(feedback);
        }
    } else {
        streetField.classList.remove("is-invalid");
        streetField.classList.add("is-valid");
    }

    if (zipCode !== "" && city !== "" && street !== "") {
        if (confirm("Megrendeli a termékeket?")) {
            fetch("/api/myorders", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json; charset=utf-8"
                },
                body: address
            }).then(function (response) {
                window.location = "/myorders.html"
            });
        }
    }
}
