function edit_distance(string1, string2) {
    let dp = [];
    for (let i = 0; i <= string1.length; i++) {
        dp.push([]);
        for (let j = 0; j <= string2.length; j++) {
            dp[i].push(0);
        }
    }
    for (let i = 1; i <= string1.length; i++) {
        dp[i][0] = i;
    }
    for (let j = 1; j <= string2.length; j++) {
        dp[0][j] = j;
    }

    for (let i = 1; i <= string1.length; i++) {
        for (let j = 1; j <= string2.length; j++) {
            if (string1[i - 1] === string2[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1;
            }
        }
    }
    console.log(dp);
    return dp[string1.length][string2.length];
}

let t = function(a, b) {
    console.log(edit_distance(a, b, true));
}

t("editreplace", "replace");