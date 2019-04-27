var neededPowerPerSecCounter = document.getElementById("neededPowerPerSecond");
var pollutionPerSecondCounter = document.getElementById("pollutionPerSecond");
var temperature = 21;
var temperatureCounter = document.getElementById("temperatureCounter");
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
var money = 10000000;
var moneyPerSec = 0;
var powerPerSec = 0;
var neededPowerPerSec = 0;
var pollution = 0;
var pollutionPerSec = 0;
var birthRate = 0;
var population = 150;
var moneyCounter = document.getElementById("moneyCounter");
var moneyPerSecCounter = document.getElementById("moneyPerSecond");
var powerPerSecondCounter = document.getElementById("powerPerSecond");
var pollutionCounter = document.getElementById("pollutionCounter");
var populationCounter = document.getElementById("populationCounter");
var numsOfEach = document.getElementsByClassName("nums");
var prices = document.getElementsByClassName("price");
var buyButtons = document.getElementsByClassName("buy");
var time = document.getElementById("time");
var daysElapsed = 0;
for (let i = 0; i < buyButtons.length; i++) {
    buyButtons[i].onclick = function () {
        if (money > unshrinkify(prices[i].textContent)) {
            money -= unshrinkify(prices[i].textContent);
            moneyCounter.textContent = shrinkify(money);
            numsOfEach[i].textContent = shrinkify(parseInt(numsOfEach[i].textContent) + 1);
        }
    }
}
var unshrinkify = function (x) {
    x = x + "";
    var c = x.charAt(x.length - 1);
    if (c === "T") {
        return parseFloat(x.substring(0, x.length - 1)) * 1000000000000;
    } else if (c === "B") {
        return parseFloat(x.substring(0, x.length - 1)) * 1000000000;
    } else if (c === "M") {
        return parseFloat(x.substring(0, x.length - 1)) * 1000000;
    } else if (c === "K") {
        return parseFloat(x.substring(0, x.length - 1)) * 1000;
    }
    return parseFloat(x);
};
var shrinkify = function (x) {
    if (Math.abs(x) / 1000000000000 >= 1) {
        return Math.round(x / 1000000000) / 1000 + "B";
    } else if (Math.abs(x) / 1000000000 >= 1) {
        return Math.round(x / 1000000) / 1000 + "B";
    } else if (Math.abs(x) / 1000000 >= 1) {
        return Math.round(x / 1000) / 1000 + "M";
    } else if (Math.abs(x) / 1000 >= 1) {
        return Math.round(x) / 1000 + "K";
    }
    return x;
};
var maintains = document.getElementsByClassName("maintain");
var lels = [];
for (let i = 0; i < maintains.length; i++) {
    lels.push(unshrinkify(maintains[i].textContent));
}
var a = function (x) {
    return parseFloat(numsOfEach[x].textContent)
};
var update = function () {
    if (daysElapsed % 365 === 0) {
        numsOfEach[7].textContent = "" + Math.ceil(a(7) * 1.25);
    }
    moneyPerSec = population * taxes / 10 * 28.73;
    daysElapsed++;
    for (let i = 0; i < lels.length; i++) {
        moneyPerSec -= lels[i] * a(i);
    }
    neededPowerPerSec = 700 * population * Math.min(250 / a(8), 1);
    powerPerSec = a(0) * 24000 + a(1) * 480 + a(2) * 36000 + a(3) * 1160930 + a(4) * 1368000 + a(5) * 1000000 + a(6) * 12870000;
    pollutionPerSec = a(0) * 21600 + a(5) * 320 - a(7) * 0.025 - a(8) * population / 10000;
    temperature = 21 + pollution / 375000000000;
    let x = powerPerSec - neededPowerPerSec;
    if (x > 0) {
        birthRate = Math.floor(x / 5000000 * population + Math.log2(population) / 7 - population * taxes / 750 - (temperature - 21) * 410);
    } else {
        birthRate = Math.floor(x / 500000 * population - population * taxes / 1000 - (temperature - 21) * 410);
    }
    population += birthRate;
    money += moneyPerSec;
    pollution += pollutionPerSec;
    if (pollution < 0) {
        pollution = 0;
    }
    if (money <= 0 || population <= 0) {
        clearInterval(interval);
        window.location.replace("game-over.html");
        return;
    }
    pollutionPerSecondCounter.textContent = shrinkify(pollutionPerSec);
    temperatureCounter.textContent = temperature;
    moneyCounter.textContent = shrinkify(money);
    moneyPerSecCounter.textContent = shrinkify(moneyPerSec);
    powerPerSecondCounter.textContent = shrinkify(powerPerSec);
    neededPowerPerSecCounter.textContent = shrinkify(neededPowerPerSec);
    pollutionCounter.textContent = shrinkify(pollution);
    populationCounter.textContent = shrinkify(population);
    time.textContent = Math.floor(daysElapsed / 365) + " years and " + daysElapsed % 365 + " days have elapsed since the start of your dictatorship!";
    if (fastForward) {
        setTimeout(update, 10);
    }
};
update();
let interval = setInterval(update, 1000);
