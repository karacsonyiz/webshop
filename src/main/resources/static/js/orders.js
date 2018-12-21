window.onload = function () {
    updateTable();
    filterActiveButtonClick();
    filterAllButtonClick();
};

function updateTable() {
    fetch("api/orders")
        .then(function (response) {
            return response.json();
        })
        .then(function (jsonData) {
            fillTable(jsonData);
        });
}

function fillTable(orders) {
    let tbody = document.getElementById("orders-tbody");
    tbody.innerHTML = "";
    for (let i = 0; i < orders.length; i++) {
        let order = orders[i];
        console.log(order);
        let tr = document.createElement("tr");
        tr.className = "clickable-row";
        tr["raw-data"] = order;

        let idTd = document.createElement("td");
        idTd.innerHTML = order.id;
        tr.appendChild(idTd);

        let userTd = document.createElement("td");
        userTd.innerHTML = order.userId;
        tr.appendChild(userTd);

        let dateTd = document.createElement("td");
        dateTd.innerHTML = new Date(order.date).toLocaleString();
        tr.appendChild(dateTd);

        let statusTd = document.createElement("td");
        if (order.status === "ACTIVE") {
            statusTd.innerHTML = "aktív";
        } else if (order.status === "DELIVERED") {
            statusTd.innerHTML = "kiszállítva";
        } else if (order.status === "DELETED") {
            statusTd.innerHTML = "törölve";
        }
        tr.appendChild(statusTd);

        let addressTd = document.createElement("td");
        addressTd.innerHTML = order.deliveryAddress;
        tr.appendChild(addressTd);

        let quantityTd = document.createElement("td");
        quantityTd.innerHTML = order.quantity + " db";
        tr.appendChild(quantityTd);

        let priceTd = document.createElement("td");
        priceTd.innerHTML = order.price + " Ft";
        tr.appendChild(priceTd);

        let buttonsTd = document.createElement("td");
        let deleteButton = document.createElement("button");
        deleteButton.innerHTML = "Törlés";
        deleteButton.setAttribute("class", "btn btn-danger");
        deleteButton.setAttribute("id", "delete-button")
        deleteButton.onclick = deleteButtonClick;
        buttonsTd.appendChild(deleteButton);

        let deliveredButton = document.createElement("button");
        deliveredButton.innerHTML = "Kiszállítva";
        deliveredButton.setAttribute("class", "btn btn-success");
        deliveredButton.setAttribute("id", "deliver-button")
        deliveredButton.onclick = deliveredButtonClick;
        buttonsTd.appendChild(deliveredButton);

        tr.appendChild(buttonsTd);

        deliveredButton.disabled = order.status === 'DELETED' || order.status === 'DELIVERED';
        deleteButton.disabled = order.status === 'DELETED' || order.status === 'DELIVERED';

        tr.onclick = function () {
            window.location = "/orderitems.html?order-id=" + order.id;
        };

        tbody.appendChild(tr);
    }
}

function deleteButtonClick(event) {
    var result = confirm("Biztosan törli a kijelölt rendelést?");
    if (result) {
        let order = this.parentElement.parentElement["raw-data"];

        fetch("api/orders/" + order.id, {
            method: "DELETE",
        }).then(function (response) {
            updateTable();
        })
    }
    event.stopPropagation();
}

function deliveredButtonClick(event) {
    let order = this.parentElement.parentElement["raw-data"];

    fetch("api/orders/" + order.id + "/status", {
        method: "POST",
    }).then(function (response) {
        updateTable();
    });
    event.stopPropagation();
}

function activeButtonClick() {
    fetch("api/activeorders/")
        .then(function (response) {
            return response.json();
        })
        .then(function (jsonData) {
            fillTable(jsonData);
        })
}

function filterActiveButtonClick() {
    let theadActiveButton = document.getElementById("orders-filter-active");
    theadActiveButton.setAttribute("class", "btn btn-warning");
    theadActiveButton.onclick = activeButtonClick;
}

function filterAllButtonClick() {
    let theadAllButton = document.getElementById("orders-filter-all");
    theadAllButton.setAttribute("class", "btn btn-info");
    theadAllButton.onclick = updateTable;
}