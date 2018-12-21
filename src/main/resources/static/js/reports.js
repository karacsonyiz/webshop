window.onload = function() {
    let statusButton = document.querySelector("#bystatus");
    let productButton = document.querySelector("#byproduct");
    statusButton.addEventListener("click",updateTableForFirstReport)
    productButton.addEventListener("click",updateTableForSecondReport)
}

function updateTableForFirstReport() {
    fetch("api/reports/orders")
        .then(function (response) {
            return response.json();
        })
        .then(function (orders) {
            console.log(orders);
            createStatusSelectorForFirstReport(orders);
        });
}

function updateTableForSecondReport(){
        fetch("api/reports/products")
            .then(function (response) {
                return response.json();
            })
            .then(function (orders) {
                console.log(orders);
                createStatusSelectorForSecondReport(orders);
            });
}

function createStatusSelectorForSecondReport(orders){
    let countParagraph = document.querySelector("#countparagraph");
    countParagraph.innerHTML = "";
    let sumParagraph = document.querySelector("#sumparagraph");
    sumParagraph.innerHTML = "";
    let thead = document.querySelector("#orders-thead");
    thead.innerHTML = "";
    let tbody = document.querySelector("#orders-tbody");
    tbody.innerHTML = "";
    let monthList = ["január","február","március","április","május","június","július","augusztus","szeptember",
    "október","november","december"];
    let choiceSelector = document.querySelector("#choice-selector");
    choiceSelector.innerHTML = "";
    createDefaultOption("Hónap");
    let id = 1;
    for(i in monthList){
        let option = document.createElement("option");
        option.innerHTML = monthList[i];
        option.setAttribute("value",id);
        choiceSelector.appendChild(option);
        id += 1;
        }
    choiceSelector.addEventListener("change", function () { updateTableByStatusForSecondReport(orders)})
    createHeadForSecondTable()
    }

function createStatusSelectorForFirstReport(orders){
        let countParagraph = document.querySelector("#countparagraph");
        countParagraph.innerHTML = "";
        let sumParagraph = document.querySelector("#sumparagraph");
        sumParagraph.innerHTML = "";
        let tbody = document.querySelector("#orders-tbody");
        tbody.innerHTML = "";
        let thead = document.querySelector("#orders-thead");
        thead.innerHTML = "";
        let choiceSelector = document.querySelector("#choice-selector");
        choiceSelector.innerHTML = "";
        let defaultOption = createDefaultOption("Státusz");

        choiceSelector.appendChild(defaultOption);

        let activeOption = document.createElement("option");
        activeOption.innerHTML = "ACTIVE";
        choiceSelector.appendChild(activeOption);

        let deliveredOption = document.createElement("option");
        deliveredOption.innerHTML = "DELIVERED";
        choiceSelector.appendChild(deliveredOption);

        let deletedOption = document.createElement("option");
        deletedOption.innerHTML = "DELETED";
        choiceSelector.appendChild(deletedOption);
        choiceSelector.addEventListener("change", function () { updateTableByStatusForFirstReport(orders)})
        createHeadForFirstTable();
}

function updateTableByStatusForSecondReport(orders,id){
    let statusSelector = document.querySelector("#choice-selector");
    let selected = statusSelector.value;
    fillTableByStatusForSecondReport(orders,selected);
}

function updateTableByStatusForFirstReport(orders){
       let statusSelector = document.querySelector("#choice-selector");
       let selected = statusSelector.value;
       fillTableByStatusForFirstReport(orders,selected);
}

function fillTableByStatusForSecondReport(orders,selected){
    let tbody = document.querySelector("#orders-tbody");
    tbody.innerHTML = "";
    let countParagraph = document.querySelector("#countparagraph");
    let sumParagraph = document.querySelector("#sumparagraph");

    console.log(selected);
    let totalPrice = 0;
    let totalCount = 0;
    for(i in orders){
        if(orders[i].month == selected){
            let tr = document.createElement("tr");

                    let prductNameTd = document.createElement("td");
                    prductNameTd.innerHTML = orders[i].productName;
                    tr.appendChild(prductNameTd);

                    let prductPriceTd = document.createElement("td");
                    prductPriceTd.innerHTML = orders[i].productPrice + " Ft";
                    tr.appendChild(prductPriceTd);


                    let productCountTd = document.createElement("td");
                    productCountTd.innerHTML = orders[i].productCount + " db";
                    tr.appendChild(productCountTd);
                    totalCount += orders[i].productCount;

                    let sumTd = document.createElement("td");
                    sumTd.innerHTML = orders[i].totalPrice + " Ft";
                    tr.appendChild(sumTd);
                    totalPrice += orders[i].totalPrice;

                    tbody.appendChild(tr);
            }
        }
        countParagraph.innerHTML = "Összes termék darabszáma : " + totalCount + " db";
        sumParagraph.innerHTML = "Összes termék összértéke : " + totalPrice + " Ft";
}

function fillTableByStatusForFirstReport(orders,status){
        let tbody = document.querySelector("#orders-tbody");
        tbody.innerHTML = "";
        let countParagraph = document.querySelector("#countparagraph");
        let sumParagraph = document.querySelector("#sumparagraph");
        sumParagraph.innerHTML = "";
        let totalPrice = 0;
        let totalCount = 0;
                for(i in orders){
                    if(orders[i].status == status){
                        let tr = document.createElement("tr");
                        let monthTd = document.createElement("td");
                        monthTd.innerHTML = orders[i].date;
                        tr.appendChild(monthTd);
                        let countTd = document.createElement("td");
                        countTd.innerHTML = orders[i].productCount + " db";
                        totalCount += orders[i].productCount;
                        tr.appendChild(countTd);
                        let sumTd = document.createElement("td");
                        sumTd.innerHTML = orders[i].totalPrice + " Ft";
                        totalPrice += orders[i].totalPrice;
                        tr.appendChild(sumTd);
                        tbody.appendChild(tr);
                        tbody.appendChild(tr);
                        }
                }
        countParagraph.innerHTML = "Összes termék darabszáma : " + totalCount + " db";
        sumParagraph.innerHTML = "Összes termék összértéke : " + totalPrice + " Ft";
    }

function createHeadForFirstTable(){
            let thead = document.querySelector("#orders-thead");
            let headTr = document.createElement("tr");

            let monthTh = document.createElement("th");
            monthTh.innerHTML = "Hónap";
            headTr.appendChild(monthTh);

            let countTh = document.createElement("th");
            countTh.innerHTML = "Darabszám";
            headTr.appendChild(countTh);

            let sumTh = document.createElement("th");
            sumTh.innerHTML = "Összeg";
            headTr.appendChild(sumTh);

            thead.appendChild(headTr);
}

function createHeadForSecondTable(){
        let thead = document.querySelector("#orders-thead");
            let headTr = document.createElement("tr");

            let productNameTh = document.createElement("th");
            productNameTh.innerHTML = "Termék neve";
            headTr.appendChild(productNameTh);

            let productPriceTh = document.createElement("th");
            productPriceTh.innerHTML = "Termék egységára";
            headTr.appendChild(productPriceTh);

            let prouctCountTh = document.createElement("th");
            prouctCountTh.innerHTML = "Darabszám";
            headTr.appendChild(prouctCountTh);

            let sumTh = document.createElement("th");
            sumTh.innerHTML = "Termékek összértéke";
            headTr.appendChild(sumTh);



            thead.appendChild(headTr);
    }

function createDefaultOption(type){
            let choiceSelector = document.querySelector("#choice-selector");
            choiceSelector.innerHTML = "";
            let defaultOption = document.createElement("option");
            defaultOption.setAttribute("value","");
            defaultOption.setAttribute("disabled","")
            defaultOption.setAttribute("selected","");
            if(type === "Hónap"){
                defaultOption.innerHTML = "Hónap";
            } else {
                defaultOption.innerHTML = "Státusz";
            }
            choiceSelector.appendChild(defaultOption);
            return defaultOption;
    }


