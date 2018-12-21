window.onload = function() {
    updateTable();

}

function updateTable() {
    let orderIdFromUrl = new URL(window.location).searchParams.get("order-id");
    let orderToFetch = "/api/orders/" + orderIdFromUrl;
    let title = document.querySelector("#header");

    fetch(orderToFetch, {
        method: "GET"
    }).then(function(response) {
         return response.json();
    }).then(function(jsonData) {
        fillTable(jsonData);
    }).catch(error => title.innerHTML = "Nincs ilyen rendelés.");
 }

function fillTable(orderItems){
    let tbody = document.querySelector("#orderitem-tbody");
    let totalPrice = 0;
    tbody.innerHTML = "";
    for (let i = 0; i < orderItems.length; i++) {
        let orderitem  = orderItems[i];
        let tr = document.createElement("tr");
        tr.className = "clickable-row";
        tr["raw-data"] = orderitem;

        let productIdTd = document.createElement("td");
        productIdTd.innerHTML = orderitem.productId;
        tr.appendChild(productIdTd);

        let producerTd = document.createElement("td");
        producerTd.innerHTML = orderitem.producer;
        tr.appendChild(producerTd);

        let productNameTd = document.createElement("td");
        productNameTd.innerHTML = orderitem.productName;
        tr.appendChild(productNameTd);

        let quantityTd = document.createElement("td");
        quantityTd.innerHTML = orderitem.quantity + " db";
        tr.appendChild(quantityTd);

        let productPriceTd = document.createElement("td");
        productPriceTd.innerHTML = orderitem.productPrice + " Ft";
        tr.appendChild(productPriceTd);

        let buttonsTd = document.createElement("td");
        let deleteButton = document.createElement("button");
        deleteButton.innerHTML = "Törlés";
        deleteButton.setAttribute("class", "btn btn-danger");
        deleteButton.onclick = deleteButtonClick;
        buttonsTd.appendChild(deleteButton);
        tr.appendChild(buttonsTd);

        tbody.appendChild(tr);

        totalPrice += orderitem.productPrice * orderitem.quantity;
    }
    let pTagForTotalPrice = document.querySelector("#totalPrice");
    pTagForTotalPrice.innerHTML = "A rendelés összértéke: " + totalPrice + " Ft"
}

function deleteButtonClick() {
    let result = confirm("Biztosan törli a kijelölt elemet?");
    if (result) {
        let orderitem = this.parentElement.parentElement["raw-data"];

        fetch("api/orders/" + orderitem.orderId + "/" + orderitem.productAddress, {
            method: "DELETE",
        })
        .then(function(response) {
            updateTable();
        });
    }
}

