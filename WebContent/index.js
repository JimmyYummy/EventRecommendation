(function(){
	var user_id = ""
		var user_name = ""
			var lat = 0.0;
	var lon = 0.0;
	console.log("successfully loaded the js file");

	init();

	function ajax(url, data, call_type, onSuccess, onFail) {
		var xhr = new XMLHttpRequest();
		xhr.open(call_type, url, true);
		xhr.onload = function () {
			if (xhr.status === 200) {
				onSuccess(xhr.responseText);
			} else if (xhr.status === 401) {
				onIncorrect(null);
			} else {
				onFail("the request failed with status code: " + xhr.status);
			}
		};
		xhr.onerror = onFail("the request failed with status code: " + xhr.status);

		if (call_type.toLowerCase() !== "get" && data !== null) {
			xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
			xhr.send(data);
		} else {
			xhr.send();
		}
	}

	function init() {
		// adding the listener of the sign up botton
		document.getElementById("login-btn").addEventListener("click", tryLogin);
        document.getElementById("signup-btn").addEventListener("click", trySignup);
		ajax("./login", null, "get", onCorrect, function(){});

	}

	function tryLogin() {
		var jsonObj = {};
		jsonObj["user_id"] = document.getElementById("username").value;
		jsonObj["password"] = document.getElementById("password").value;
		//console.log(jsonObj);
		ajax("./login", JSON.stringify(jsonObj), "post", onCorrect, onIncorrect);
	}

    function trySignup() {
        var jsonObj = {};
        jsonObj["user_id"] = document.getElementById("username").value;
        jsonObj["password"] = document.getElementById("password").value;
        //console.log(jsonObj);
        ajax("./signup", JSON.stringify(jsonObj), "post", onSignUpSuccess, console.log);
    }

	function tryLogout() {
		ajax("./logout", null, "get", oneSuccessLogOut, console.log);
	}

	function onSignUpSuccess(res) {
        var res = JSON.parse(res);
        var errorText = document.getElementById("login-error");
        if (res.hasOwnProperty("msg")) errorText.innerText = res["msg"];
        else errorText.innerText = "Sign up failed due to unknown error.";
    }

	function oneSuccessLogOut(res) {
		window.location = ".";
	}

	function onCorrect(response) {
		var res = JSON.parse(response);
		user_id = res["user_id"];
		user_name = res["name"];

		var header = document.getElementById("header");
		header.innerHTML = '<a id="logout-link" href="#">Log out</a> \
			<div id="user"></div> \
			<div id="title">Event Recommendation</div>';

		document.getElementById("user").innerText = "Hi, " + user_name;
		var logoutlink = document.getElementById("logout-link");
		logoutlink.addEventListener("click", tryLogout);
		var mainBody = document.getElementById("main-body");
		mainBody.innerHTML = `<nav class="nav-panel">
			<a href="#" id="nearby-btn" class="main-nav-btn"> 
			<i class="fa fa-map-marker"></i> Nearby</a> 
			<a href="#" id="fav-btn" class="main-nav-btn"> 
			<i class="fa fa-heart"></i> My Favorites</a>
			<a href="#" id="recommend-btn" class="main-nav-btn"> 
			<i class="fa fa-thumbs-up"></i> Recommendation</a>
			<div id="search-form" class="main-nav-form"">
			<input id="search" name="search" type="text">
			<button id="search-btn">Search</button>
			</div>
			</nav>
			<ul name="events" id="events">
			</ul>`;

		document.getElementById("nearby-btn").addEventListener("click", onNearbyClicked);
		document.getElementById("fav-btn").addEventListener("click", onFavClicked);
		document.getElementById("recommend-btn").addEventListener("click", onRecClicked);
        document.getElementById("search").addEventListener("keydown", onInputCheck);
        document.getElementById("search").addEventListener("focus", onFocusSearchBar);
        document.getElementById("search").addEventListener("mouseover", onFocusSearchBar);
        document.getElementById("search-btn").addEventListener("click", onSearchClicked);

		onNearbyClicked();

	}

	function onIncorrect(response) {
		var errorText = document.getElementById("login-error");
		if (response === null) {
			errorText.innerText = "Incorrect username or password.";
		} else {
			errorText.innerText = "Network error, please try later.";
		}
	}

	function updatePosition(callback) {
		showText("Getting your position...");
		console.log("getting position");
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(function(position) {
				lat = position.coords.latitude;
				lon = position.coords.longitude;
				console.log("calling back")
				callback();
			}, function() {
				console.warn("failed to get position using navigator.geolocation");
				getPosByIP(callback);
			});
		} else {
			getPosByIP(callback);
		}
	}

	function getPosByIP(callback) {
		console.log("getting position by ip")
		ajax("http://ipinfo.io.json", null, "get", function(res) {
			var json = JSON.parse(res);
			if ("loc" in json) {
				var loc = json["loc"].split(',');
				lat = loc[0];
				lon = loc[1];
			} else {
				console.warn("failed to update position using ip")
			}
			callback();
		}, function() {
			console.warn("failed to update position using ip");
			callback();
		});
	}

	function onNearbyClicked() {
		activeBtn("nearby-btn");
		updatePosition(loadNearbyEvents);
	}

	function onFavClicked() {
		activeBtn("fav-btn");
		loadFavEvents();
	}

	function onRecClicked() {
		activeBtn("recommend-btn");
		updatePosition(loadRecEvents);
	}

	function onFocusSearchBar() {
		document.getElementById("search").select();
    }
    
    function onInputCheck(e) {
		if (e.key === "Enter") onSearchClicked();
    }

	function onSearchClicked() {
		activeBtn("search-form");
		var keyword = document.getElementById("search").value;
		loadNearbyEvents(keyword);
	}

	function activeBtn(btn_id) {
		var allBtns = document.getElementsByClassName("main-nav-btn");
		for (var i = 0; i < allBtns.length; i++) {
			allBtns[i].className = "main-nav-btn";
		}
		var btn = document.getElementById(btn_id);
		btn.className += " active";
	}

	function loadNearbyEvents(keyword = "") {
		showText("Loading nearby events...");
		var url = "./search?user_id=" + user_id + "&lat=" + lat + "&lon=" + lon + "&keyword=" + keyword;
		console.log(url);
		ajax(url, null, "get", showEvents, showError);
	}

	function loadFavEvents() {
		showText("Loading favorite events...");
		var url = "./history?user_id=" + user_id;
		ajax(url, null, "get", showEvents, showError);

	}

	function loadRecEvents() {
		showText("Loading events recommended for you");
		var url = "./recommend?user_id=" + user_id + "&lat=" + lat + "&lon=" + lon;
		ajax(url, null, "get", showEvents, showError)
	}

	function showEvents(res) {
		var arr = JSON.parse(res);
		console.log(arr);
		if (arr === null || arr.length == 0) {
			showText("Ops, seems there is no available event at current position.")
		} else {
			var events = document.getElementById("events");
			events.innerHTML = "";
			for (let i = 0; i < arr.length; i++){
				addEvent(arr[i], events);
			}
		}
	}

	// {
	//     "imgUrl": "xxx",
	//     "eventId": "Z7r9jZ1Ae8AGe",
	//     "address": "3601 South Broad St. Philadelphia Pennsylvania",
	//     "distance": 0,
	//     "eventUrl": "xxx",
	//     "name": "Aubrey & The Three Migos Tour",
	//     "categories": [
	//     "Music"
	// ],
	//     "favorite": false
	// }
	//
	//<li class="item">
	//  <img alt="item image" src="https://s3-media3.fl.yelpcdn.com/bphoto/EmBj4qlyQaGd9Q4oXEhEeQ/ms.jpg" />
	//  <div>
	//      <a class="item-name" href="#">Item</a>
	//      <p class="item-category">Vegetarian</p>
	//  </div>
	//  <p class="item-address">699 Calderon Ave<br/>Mountain View<br/> CA</p>
	//  <div class="fav-link">
	//     <i class="fa fa-heart"></i>
	//  </div>
	//</li>
	function addEvent(event, container) {
		var eventId = event["eventId"];
		var ele = document.createElement("li");
		ele.setAttribute("class", "event");
		ele.setAttribute("id", eventId);

		var section = document.createElement("img");
		section.setAttribute("alt", "event image");
		section.setAttribute("src", event["imgUrl"]);
		ele.appendChild(section);

		section = document.createElement("div");

		var subsection = document.createElement("a");
		subsection.setAttribute("class", "event-name");
		subsection.setAttribute("href", event["eventUrl"]);
		subsection.innerText = event["name"];
		section.appendChild(subsection);

		subsection = document.createElement("p");
		subsection.setAttribute("class", "event-category");
		var cates = event["categories"];
		var str = cates[0];
		for (let i = 1; i < cates.length; i++) {
			str += " | ";
			str += cates[i];
		}
		subsection.innerText = str;
		section.appendChild(subsection);
		ele.appendChild(section);

		section = document.createElement("p");
		section.setAttribute("class", "event-address");
		section.innerHTML = event["address"].replace(/,;,/g, '<br/>').replace(/\"/g, '');
		ele.appendChild(section);

		section = document.createElement("div");
		section.setAttribute("class", "fav-link");
		subsection = document.createElement("i");
		if (event["favorite"]) {
			section.setAttribute("fav", "true");
			subsection.setAttribute("class", "fa fa-heart");
		} else {
			section.setAttribute("fav", "false");
			subsection.setAttribute("class", "fa fa-heart-o");
		}
		section.appendChild(subsection);
		section.addEventListener("click", function(){onFavLinkClicked(eventId);});
		ele.appendChild(section);

		container.appendChild(ele);
	}

	function onFavLinkClicked(eventId) {
		var json = {};
		json["user_id"] = user_id;
		json["favorites"] = [eventId];
		json = JSON.stringify(json);
		var fav = document.getElementById(eventId).getElementsByClassName("fav-link")[0];
		console.log(json);
		if (fav.getAttribute("fav") === "true") {
			ajax("history", json, "delete", function() {
				fav.setAttribute("fav", "false");
				fav.innerHTML = "<i class=\"fa fa-heart-o\"></i>";
			}, console.log);
		} else {
			ajax("history", json, "post", function() {
				fav.setAttribute("fav", "true");
				fav.innerHTML = "<i class=\"fa fa-heart\"></i>";
			}, console.log);
		}
	}

	function showError() {
		showText("Trying to load events.");
	}

	function showText(msg) {
		var events = document.getElementById("events");
		events.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> '
			+ msg + '</p>';
	}
})();