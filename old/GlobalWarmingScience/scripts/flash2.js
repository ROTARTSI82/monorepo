function play() {
    let all = document.getElementsByTagName("*");

    let getRandBGCol = function (min, max) {
        let y = Math.floor(Math.random() * max) + min;
        let x = Math.floor(Math.random() * max) + min;
        let z = Math.floor(Math.random() * max) + min;
        return "rgb(" + y + "," + x + "," + z + ")";
    };

    let myAudio = new Audio('assets/rick.mp3');
    myAudio.loop = true;
    myAudio.addEventListener('ended', function () {
        this.currentTime = 0;
        this.play();
    }, false);
    myAudio.play();


    setTimeout(function () {
        setInterval(function () {
            for (let i = 0; i < all.length; i++) {
                all[i].style.backgroundColor = getRandBGCol(0, 256);
            }
        }, 0);
    }, 2250);
}

let chicken = document.getElementById("chickenImg");
chicken.onclick = play;
