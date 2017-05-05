window.onload = function() {
	var query = document.getElementById("search-bar").getAttribute("value");
	var thirdPartyContainer = document
			.getElementById("thirdparty-result-container");
	
	if (document.getElementById("amazon-checkbox").getAttribute("checked") != null) {
		var xhr = new XMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var itemInfos = JSON.parse(this.responseText);
				var resultContainer = document
						.getElementById("thirdparty-result-container");

				for (var i = 0; i < itemInfos.length; i++) {
					var itemInfo = itemInfos[i];
					console.log(itemInfo);
					var itemDiv = document.createElement("div");
					itemDiv.setAttribute("class", "thirdparty-result-item");

					// add image
					var itemImg = document.createElement("img");
					itemImg.setAttribute("src", itemInfo.imgUrl);
					itemDiv.appendChild(itemImg);

					// add title and url
					var h4 = document.createElement("h4");
					var itemUrl = document.createElement("a");
					itemUrl.setAttribute("href", itemInfo.itemUrl);
					itemUrl.textContent = itemInfo.title;
					h4.appendChild(itemUrl);
					itemDiv.appendChild(h4);

					// add price
					var price = document.createElement("p");
					price.textContent = itemInfo.price;
					itemDiv.appendChild(price);

					resultContainer.appendChild(itemDiv);
				}
			}
		}
		var url = location.protocol + '//' + location.host
				+ "/thirdparty?name=amazon&&query=" + query;
		console.log(url);
		xhr.open('GET', url, true);
		xhr.send();
	}

	if (document.getElementById("ebay-checkbox").getAttribute("checked") != null) {

	}

}