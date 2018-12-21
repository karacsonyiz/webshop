window.onload = function() {
    getCategories();
    updateTable();

    let productForm = document.getElementById("product-form");
    productForm.onsubmit = handleSubmit;
    productForm.onreset = handleReset;
    let imageForm = document.getElementById("picture-form");
    imageForm.onsubmit = uploadImage;
}

function updateTable() {
    fetch("api/products")
             .then(function (response) {
                 return response.json();
             })
             .then(function(jsonData) {
                 fillTable(jsonData);
             });
}

function fillTable(products) {
    let tbody = document.getElementById("products-tbody");
    tbody.innerHTML = "";
    for (let i = 0; i < products.length; i++) {
        let product  = products[i];
        let tr = document.createElement("tr");
        tr["raw-data"] = product;

        let idTd = document.createElement("td");
        idTd.innerHTML = product.id;
        tr.appendChild(idTd);

        let producerTd = document.createElement("td");
        producerTd.innerHTML = product.producer;
        tr.appendChild(producerTd);

        let nameTd = document.createElement("td");
        nameTd.innerHTML = product.name;
        tr.appendChild(nameTd);

        let addressTd = document.createElement("td");
        addressTd.innerHTML = product.address;
        tr.appendChild(addressTd);

        let priceTd = document.createElement("td");
        priceTd.innerHTML = product.currentPrice + " Ft";
        tr.appendChild(priceTd);

        let categoryTd = document.createElement("td");
        categoryTd.innerHTML = product.category.name;
        tr.appendChild(categoryTd);

        let buttonsTd = document.createElement("td");
        let editButton = document.createElement("button");
        let deleteButton = document.createElement("button");
        editButton.setAttribute("class", "btn btn-primary");
        deleteButton.setAttribute("class", "btn btn-danger");
        editButton.innerHTML = "Szerkesztés";
        deleteButton.innerHTML = "Törlés";
        editButton.onclick = handleEditButtonOnClick;
        deleteButton.onclick = handleDeleteButtonOnClick;
        buttonsTd.appendChild(editButton);
        buttonsTd.appendChild(deleteButton);
        tr.appendChild(buttonsTd);

        tbody.appendChild(tr);
    }
}

let editedProduct = null;

function handleSubmit() {

    let idInput = document.getElementById("id-input");
    let producerInput = document.getElementById("producer-input");
    let nameInput = document.getElementById("name-input");
    let addressInput = document.getElementById("address-input");
    let priceInput = document.getElementById("price-input");
    let categorySelect = document.querySelector("#category-select");

    let id = idInput.value;
    let producer = producerInput.value;
    let name = nameInput.value;
    let address = addressInput.value;
    let price = priceInput.value;
    let categoryId = categorySelect.value;
    if (categoryId == 0) {
        categoryId = 1;
    }

    let parsedId = Number(id);
    if (isNaN(parsedId) || !Number.isInteger(parsedId)) {
        alert("Az id megadása kötelező és csak egész szám lehet.");
        return false;
    }
    if (name.length === 0 || producer.length === 0) {
        alert("Minden mező kitöltése kötelező!");
        return false;
    }
    let parsedPrice = Number(price);
    if (isNaN(parsedPrice) || !Number.isInteger(parsedPrice) || parsedPrice <= 0 || parsedPrice > 2000000) {
        alert("Az ár megadása kötelező, csak egész szám lehet és nem haladhatja meg a 2.000.000 Ft-ot.");
        return false;
    }

    let product = {"id": id,
                   "producer": producer,
                   "name": name,
                   "address": address.length > 0 ? address : null,
                   "currentPrice": price,
                   "category": {
                                "id": categoryId
                               },
                   "image": image
                  };

    let url = "api/products";
    if (editedProduct !== null) {
        url += "/" + editedProduct.id;
    }

    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json; charset=utf-8"
                },
        body: JSON.stringify(product)
        }).then(response => response.json())
        .then(function(response) {

            if (editedProduct === null) {
                alert("Hozzáadva.");
            } else {
                alert("Módosítva.")
            }
            updateTable();
            document.getElementById("product-form").reset();
        });

    return false;
}

function handleReset() {
    editedProduct = null;
    /* let idInput = document.getElementById("id-input");
    idInput.value = "";
    let producerInput = document.getElementById("producer-input");
    producerInput.value = "";
    let nameInput = document.getElementById("name-input");
    nameInput.value = "";
    let addressInput = document.getElementById("address-input");
    addressInput.value = "";
    let priceInput = document.getElementById("price-input");
    priceInput.value = "";
    let categorySelect = document.querySelector("#category-select");
    categorySelect.value = 0; */


    let submitButton = document.getElementById("submit-button");
    submitButton.value = "Új termék hozzáadása";
}

function handleEditButtonOnClick() {
    let product = this.parentElement.parentElement["raw-data"];
    editedProduct = product;

    let idInput = document.getElementById("id-input");
    idInput.value = product.id;

    let producerInput = document.getElementById("producer-input");
    producerInput.value = product.producer;

    let nameInput = document.getElementById("name-input");
    nameInput.value = product.name;

    let addressInput = document.getElementById("address-input");
    addressInput.value = product.address;

    let priceInput = document.getElementById("price-input");
    priceInput.value = product.currentPrice;

    let categorySelect = document.querySelector("#category-select");
    categorySelect.value = product.category.id;

    let submitButton = document.getElementById("submit-button");
    submitButton.value = "Mentés";
}

function handleDeleteButtonOnClick() {
    var result = confirm("Biztosan törli a kijelölt terméket?");
    if (result) {
        let product = this.parentElement.parentElement["raw-data"];

        fetch("api/products/" + product.id, {
            method: "DELETE",
        })
        .then(function(response) {
            updateTable();
            document.getElementById("product-form").reset();
        });
    }
}

function getCategories() {
    fetch("/api/categories")
          .then(function (response) {
              return response.json();
          })
          .then(function(categories) {
              fillSelectOptions(categories);
          });
}

function fillSelectOptions(categories) {
    let categorySelect = document.querySelector("#category-select");
    for (let i = 0; i < categories.length; i++) {
        let option = document.createElement("option");
        option["raw-data"] = categories[i];
        option.value = categories[i].id;
        option.innerHTML = categories[i].name;
        categorySelect.appendChild(option);
    }
}

function uploadImage() {
  let idInput = document.getElementById("id-input");
  let id = idInput.value;
  let formData = new FormData();
  let fileInput = document.getElementById('picture-select');
  formData.append('file', fileInput.files[0]);
  let xhr = new XMLHttpRequest();
  let url = '/api/upload/' + id;
  xhr.open('POST', url);
  xhr.onreadystatechange = function () {
    var DONE = 4;
    var OK = 200;
    if (xhr.readyState === DONE) {
      if (xhr.status === OK) {
        updateTable();
      } else {
        console.log('Error: ' + xhr.status);
      }
    }
  };
  xhr.send(formData);
}

