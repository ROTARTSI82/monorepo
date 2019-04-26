var slider = document.getElementById("taxes");
var taxes = 0;
var taxVal = document.getElementById("taxValue");
taxes = slider.value;
slider.oninput = function () {
    taxes = this.value;
    taxVal.textContent = taxes;
};
var fastForward = false;
document.getElementById("ff").onclick = function () {
    fastForward = !fastForward;
    if (this.textContent === "Fast Forward") {
        this.textContent = "Stop";
    } else {
        this.textContent = "Fast Forward";
    }
};
var money = 1000000;
var moneyPerSec = 0;
var powerPerSec = 0;
var neededPowerPerSec = 0;
var pollution = 0;
var pollutionPerSec = 0;
var birthRate = 0;
var population = 100;
var moneyCounter = document.getElementById("moneyCounter");
var moneyPerSecCounter = document.getElementById("moneyPerSecond");
var powerPerSecondCounter = document.getElementById("powerPerSecond");
var pollutionCounter = document.getElementById("pollutionCounter");
var populationCounter = document.getElementById("populationCounter");
var numsOfEach = document.getElementsByClassName("nums");
var prices = document.getElementsByClassName("price");
var buyButtons = document.getElementsByClassName("buy");
for (let i = 0; i < buyButtons.length; i++) {
    buyButtons[i].onclick = function () {
        if (money > unshrinkify(prices[i].textContent)) {
            money -= unshrinkify(prices[i].textContent);
            moneyCounter.textContent = shrinkify(money);
            numsOfEach[i].textContent++;
        }
    }
}
var unshrinkify = function (x) {
    x = x + "";
    var c = x.charAt(x.length - 1);
    if (c === "B") {
        return parseInt(x.substring(0, x.length - 1)) * 1000000000;
    } else if (c === "M") {
        return parseInt(x.substring(0, x.length - 1)) * 1000000;
    } else if (c === "K") {
        return parseInt(x.substring(0, x.length - 1)) * 1000;
    }
    return parseInt(x);
};
var shrinkify = function (x) {
    if (x / 1000000000 >= 1) {
        return Math.round(x / 1000000) / 1000 + "B";
    } else if (x / 1000000 >= 1) {
        return Math.round(x / 1000) / 1000 + "M";
    } else if (x / 1000 >= 1) {
        return Math.round(x) / 1000 + "K";
    }
    return x;
};
var a = function (x) {
    return parseInt(numsOfEach[x].textContent)
};
var update = function () {
    moneyPerSec = population * taxes / 50 * 28.73;
    neededPowerPerSec = 700 * population;
    powerPerSec = a(0) * 24000 + a(1) + a(2) + a(3) + a(4) + a(5) + a(6);
    pollutionPerSec = a(0) * 139505 - a(7) * 0.025;
    let x = powerPerSec - neededPowerPerSec;
    birthRate = Math.ceil(x / 500000000 * population - population * taxes / 1000);
    population += birthRate;
    money += moneyPerSec;
    pollution += pollutionPerSec;
    if (pollution < 0) {
        pollution = 0;
    }
    if (money <= 0 || population <= 0) {
        window.location.replace("game-over.html");
    }
    moneyCounter.textContent = shrinkify(money);
    moneyPerSecCounter.textContent = shrinkify(moneyPerSec);
    powerPerSecondCounter.textContent = shrinkify(powerPerSec);
    pollutionCounter.textContent = shrinkify(pollution);
    populationCounter.textContent = shrinkify(population);
    if (fastForward) {
        setTimeout(update, 1);
    }
};
update();
let interval = setInterval(update, 1000);
