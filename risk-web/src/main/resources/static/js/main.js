var lastClicked = null;

function clicked(event) {
    event = event || window.event; // IE
    var target = event.target || event.srcElement; // IE

    var id = target.id;
    if (id == lastClicked) {
        lastClicked = null;
    } else {
        lastClicked = id;
    }
    var div = document.getElementById(id)
    div.classList.replace("option", "optionSelected");
}
