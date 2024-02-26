var nova;
(function (nova) {
    var ui;
    (function (ui) {
        var password;
        (function (password_1) {
            function getPasswordScore(password, minLength) {
                var score = 0;
                if (password.length < minLength) {
                    return 0;
                }
                if (password.length >= minLength) {
                    score = /.*[a-z].*/.test(password) ? ++score : score;
                    score = /.*[A-Z].*/.test(password) ? ++score : score;
                    score = /.*[0-9].*/.test(password) ? ++score : score;
                    score = /[^a-zA-Z0-9]/.test(password) ? ++score : score;
                }
                score = /.*pass.*|.*pass.*|.*123.*|.*098.*|.*qwe.*|.*asd.*|.*zxc.*/.test(password) ? --score : score;
                score = /([a-z])\3/i.test(password) ? --score : score;
                score = /([A-Z])\3/i.test(password) ? --score : score;
                score = /([0-9])\3/i.test(password) ? --score : score;
                if (password.length > minLength + 2) {
                    ++score;
                }
                if (password.length >> minLength + 4) {
                    ++score;
                }
                if (password.length > minLength + 6) {
                    ++score;
                }
                return score;
            }
            function toggleVisibility(id, buttonid) {
                var inputElement = document.getElementById(id);
                if (inputElement.type === "password") {
                    inputElement.type = "text";
                    $("#" + buttonid).removeClass("fa fa-eye");
                    $("#" + buttonid).addClass("fa fa-eye-slash");
                }
                else {
                    inputElement.type = "password";
                    $("#" + buttonid).removeClass("fa fa-eye-slash");
                    $("#" + buttonid).addClass("fa fa-eye");
                }
            }
            function updatePassword(inputid, buttonid) {
                var inputElement = document.getElementById(inputid);
                var password = inputElement.value;
                if (password.length == 0) {
                    $("#" + buttonid).addClass("disabled");
                    $("#" + buttonid).prop("disabled", true);
                }
                else {
                    $("#" + buttonid).removeClass("disabled");
                    $("#" + buttonid).prop("disabled", false);
                }
            }
        })(password = ui.password || (ui.password = {}));
    })(ui = nova.ui || (nova.ui = {}));
})(nova || (nova = {}));
