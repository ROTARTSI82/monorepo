let doRandColor = function () {
    setInterval(function () {
        let all = document.getElementsByTagName("*");

        for (let i = 0; i < all.length; i++) {
            all[i].style.background = random_bg_color();
            all[i].style.color = random_bg_color();
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

