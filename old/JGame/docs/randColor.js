function random_bg_color() {
    let x = Math.floor(Math.random() * 256);
    let y = Math.floor(Math.random() * 256);
    let z = Math.floor(Math.random() * 256);
    return "rgb(" + x + "," + y + "," + z + ")";
}

let doRandColor = function () {
    alert("Congrats on activating an easter egg! Who in their right mind would go around clicking commas on obscure sites?");
    alert("EPILEPSY WARNING: The following easter egg contains bright, flashing lights! Be aware!");
    myAudio = new Audio('invincible.mp3');
    myAudio.loop = true;
    myAudio.addEventListener('ended', function () {
        this.currentTime = 0;
        this.play();
    }, false);
    myAudio.play();

    doSubRand();
};

let doSubRand = function () {
    setInterval(function () {
        let all = document.getElementsByTagName("*");

        for (let i = 0; i < all.length; i++) {
            all[i].style.background = random_bg_color();
            all[i].style.color = random_bg_color();
            all[i].style.borderColor = random_bg_color();
        }
    }, 0);
    let reload = function () {
        let x = window.open("index.html");
        window.resizeTo(Math.random() * 1440, Math.random() * 900);
        window.moveTo(Math.random() * 1440, Math.random() * 900);
        reload();
    };
    window.onunload = reload;
};
