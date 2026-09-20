
let setTuesThursSchedule = function () {
    now.setSeconds(0);

    now.setHours(8);
    now.setMinutes(45);
    schedule.push([new Date(now.getTime()), "Passing Period (???->4)"]);


    now.setHours(9);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Period 4"]);


    now.setHours(10);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Passing Period (4->5"]);

    now.setHours(10);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), "Period 5"]);

    now.setHours(11);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), "Passing Period (5->6)"]);

    now.setHours(12);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Period 6"]);

    now.setHours(13);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Passing Period (6->7)"]);

    now.setHours(13);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), "Period 7"]);


    now.setHours(14);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), offDuty]);
};

let setMonWedSched = function() {
    now.setSeconds(0);

    now.setHours(8);
    now.setMinutes(45);
    schedule.push([new Date(now.getTime()), "Passing Period (???->Advisory)"]);


    now.setHours(9);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Advisory"]);


    now.setHours(10);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Passing Period (Advisory->1)"]);

    now.setHours(10);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), "Period 1"]);

    now.setHours(11);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), "Passing Period (1->2)"]);

    now.setHours(12);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Period 2"]);

    now.setHours(13);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Passing Period (2->3)"]);

    now.setHours(13);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), "Period 3"]);


    now.setHours(14);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), offDuty]);
};

let setFridaySched = function() {
    now.setSeconds(0);

    now.setHours(8);
    now.setMinutes(45);
    schedule.push([new Date(now.getTime()), "Passing Period (???->Advisory)"]);


    now.setHours(9);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Advisory"]);

    now.setHours(9);
    now.setMinutes(30);
    schedule.push([new Date(now.getTime()), "Period 1"]);

    now.setHours(10);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Passing Period (1->2)"]);

    now.setHours(10);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), "Period 2"]);

    now.setHours(10);
    now.setMinutes(45);
    schedule.push([new Date(now.getTime()), "Period 3"]);

    now.setHours(11);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), "Passing Period (3->4)"]);


    now.setHours(12);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Period 4"]);

    now.setHours(12);
    now.setMinutes(30);
    schedule.push([new Date(now.getTime()), "Period 5"]);

    now.setHours(13);
    now.setMinutes(0);
    schedule.push([new Date(now.getTime()), "Passing Period (5->6)"]);

    now.setHours(13);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), "Period 6"]);

    now.setHours(13);
    now.setMinutes(45);
    schedule.push([new Date(now.getTime()), "Period 7"]);

    now.setHours(14);
    now.setMinutes(15);
    schedule.push([new Date(now.getTime()), offDuty]);
};

let loadSchedule = function () {
    let d = now.getDay();
    d -= holidayTog.checked ? 1 : 0;
    switch (d) {
        case (-1): { // Special case: sunday on holidayTog. No schedules
            break;
        }
        case (0): { // Sunday
            // No schedule here. Special edge case.
            break;
        }
        case (1): { // Monday
            setMonWedSched();

            now.setDate(now.getDate() + 1);
            now.setHours(9);
            now.setMinutes(0);
            schedule.push([new Date(now.getTime()), "Period 4"]);

            break;
        }
        case (2): { // Tuesday
            setTuesThursSchedule();

            now.setDate(now.getDate() + 1);
            now.setHours(9);
            now.setMinutes(0);
            schedule.push([new Date(now.getTime()), "Advisory"]);
            break;
        }
        case (3): { // Wednesday
            setMonWedSched();

            now.setDate(now.getDate() + 1);
            now.setHours(9);
            now.setMinutes(0);
            schedule.push([new Date(now.getTime()), "Period 4"]);
            break;
        }
        case (4): { // Thursday
            setTuesThursSchedule();

            if (holidayTog.checked) {
                now.setDate(now.getDate() + 3);
                now.setHours(9);
                now.setMinutes(0);
                schedule.push([new Date(now.getTime()), "Advisory???"]);
            } else {
                now.setDate(now.getDate() + 1);
                now.setHours(9);
                now.setMinutes(0);
                schedule.push([new Date(now.getTime()), "Advisory"]);
            }
            break;
        }
        case (5): { // Friday
            if (holidayTog.checked) {
                break; // Friday skipped in this case
            }

            setFridaySched();

            now.setDate(now.getDate() + 3);
            now.setHours(9);
            now.setMinutes(0);
            schedule.push([new Date(now.getTime()), "Advisory"]);

            break;
        }
        case (6): { // Saturday
            // No schedule here. Special edge case.
            break;
        }

        default: {
            break;
        }
    }
    now = new Date();
};
