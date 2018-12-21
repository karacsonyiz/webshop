window.onload = function() {
   createDivs();
}

function createDivs() {
fetch("/api/products/lastsold")
            .then(function (response) {
                return response.json();
            })
            .then(function (jsonData) {
                creatingLastSold(jsonData);
            });
}


function creatingLastSold(productList) {

    let containerDiv = document.querySelector(".container");
    let mainDiv = document.createElement("div");
    mainDiv.setAttribute("id", "main-div");
    for (let i = 0; i < productList.length; i++) {
        let product = productList[i];

        let soldDiv = document.createElement("div");
        soldDiv.setAttribute("id", "div-sold");

        let nameDiv = document.createElement("h6");
        nameDiv.innerHTML = product.name;
        let newLine = document.createElement("br");
        nameDiv.appendChild(newLine);

        let categoryDiv = document.createElement("h6")
        categoryDiv.innerHTML = product.producer;
        nameDiv.appendChild(categoryDiv);

        let imgDiv = document.createElement("img");
        imgDiv.src = "data:image/png;base64, " + product.image;
        categoryDiv.appendChild(imgDiv);

        let priceDiv = document.createElement("h7");
        priceDiv.innerHTML = product.currentPrice + " Ft";

        nameDiv.appendChild(priceDiv);

        soldDiv.appendChild(nameDiv);

        let buttonDiv = document.createElement("div");
        buttonDiv.setAttribute("id", "button-div");
        let linkButton = document.createElement("button");
        linkButton.setAttribute("id", "link-button");
        linkButton.setAttribute("class", "btn-info");
        linkButton.innerHTML = "Megnézem";
        function visitPage(){
                window.location="/product.html?address=" + product.address;
            }
        linkButton.onclick = visitPage;
        buttonDiv.appendChild(linkButton);
        soldDiv.appendChild(buttonDiv);

        mainDiv.appendChild(soldDiv);
        containerDiv.appendChild(mainDiv);
}
}