let randColor = function () {
    let ret = "#";
    for (let i = 0; i < 6; i++) {
        ret += [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 'a', 'b', 'c', 'd', 'e', 'f'][Math.floor(Math.random() * 16)]
    }
    return ret;
};

function random_bg_color() {
    let x = Math.floor(Math.random() * 256);
    let y = Math.floor(Math.random() * 256);
    let z = Math.floor(Math.random() * 256);
    return "rgb(" + x + "," + y + "," + z + ")";
}

let spin = function () {
    const numWindows = 1;

    const windows = new Array(numWindows);
    const len = 250;
    const mx = 1440 / 3;
    const my = 900 / 3;

    for (let i = 0; i < numWindows; i++) {
        windows[i] = window.open("spinWindow.html", i, `width=320, height=240`);
    }

    let angle = 0;

    setInterval(function () {
        angle += 0.1;
        if (angle > 360) {
            angle = 0;
        }

        windows.forEach(function (element) {
            //Console.log(randColor());
            //element.body.style.background = random_bg_color();
            document.body.style.background = random_bg_color();
            element.moveTo(mx + Math.cos(angle) * len, my + Math.sin(angle) * len);
        });
    }, 10);
};
