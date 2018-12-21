window.onload = function () {
    updateTable();

};

function updateTable() {
    fetch("/dashboard")
        .then(function (response) {
            return response.json();
        })
        .then(function (dashboard) {
            fillTable(dashboard);
            console.log(dashboard);
        });
}

function fillTable(dashboard) {
    let tbody = document.querySelector("#dashboard-tbody");
    let tr = document.createElement("tr");


    let userCount = document.createElement("td");
    userCount.innerHTML = dashboard.userCount;
    tr.appendChild(userCount);

    let activeProductCount = document.createElement("td");
    activeProductCount.innerHTML = dashboard.activeProductCount;
    tr.appendChild(activeProductCount);

    let productCount = document.createElement("td");
    productCount.innerHTML = dashboard.productCount;
    tr.appendChild(productCount);

    let activeOrderCount = document.createElement("td");
    activeOrderCount.innerHTML = dashboard.activeOrderCount;
    tr.appendChild(activeOrderCount);

    let orderCount = document.createElement("td");
    orderCount.innerHTML = dashboard.orderCount;
    tr.appendChild(orderCount);

    tbody.appendChild(tr);
}