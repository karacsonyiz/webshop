window.onload = function () {
    updateTable();
    getCategories();
};

function getCategories(){
    fetch("/api/categories")
        .then(function (response) {
            return response.json();
        })
        .then(function (categories) {
            fillSelectWithCategories(categories);
        });
}

function fillSelectWithCategories(categories){
    let select = document.querySelector("#category-selector");
            let allOption = document.createElement("option");
            allOption.innerHTML = "Összes";
            select.appendChild(allOption);
    for(i in categories){
        let option = document.createElement("option");
        option.innerHTML = categories[i].name;
        option.setAttribute("value",categories[i].id)
        select.appendChild(option);
        }
    select.addEventListener("change", function () { updateTable(this.value) })
}

function updateTable(categoryId) {
    let url = "";
    if(categoryId === undefined || categoryId === "Összes"){
        url = "api/products";
        } else {
        url = "api/products/category/" + categoryId;
    }
    fetch(url)
        .then(function (response) {
            return response.json();
        })
        .then(function (jsonData) {
            fillTable(jsonData);
        });
}

function fillTable(products) {
    let tbody = document.getElementById("products-tbody");
    tbody.innerHTML = "";
    let categorySelector = document.querySelector("#category-selector");
    let value = categorySelector.value;
    for (let i = 0; i < products.length; i++) {
        let product = products[i];
            let tr = document.createElement("tr");
            tr.className = "clickable-row";
            tr["raw-data"] = product;

            let idTd = document.createElement("td");
            idTd.innerHTML = product.id;
            tr.appendChild(idTd);

            let nameTd = document.createElement("td");
            nameTd.innerHTML = product.name;
            tr.appendChild(nameTd);

            let addressTd = document.createElement("td");
            addressTd.innerHTML = product.address;
            tr.appendChild(addressTd);

            let producerTd = document.createElement("td");
            producerTd.innerHTML = product.producer;
            tr.appendChild(producerTd);

            let priceTd = document.createElement("td");
            priceTd.innerHTML = product.currentPrice + " Ft";
            tr.appendChild(priceTd);

            let categoryTd = document.createElement("td");
            categoryTd.innerHTML = product.category.name;
            tr.appendChild(categoryTd);

            tr.onclick = function () {
                window.location = "/product.html?address=" + product.address;
            }
            tbody.appendChild(tr);
    }
}
