function checkPasswordStrength(inputId, output) {
    var element = document.getElementById(inputId);
    var password = element.value;
    if (password.length < 5) {
        return 0;
    }
    var score = 0;
    score = /.*[a-z].*/.test(password) ? ++score : score;
    score = /.*[A-Z].*/.test(password) ? ++score : score;
    score = /.*[0-9].*/.test(password) ? ++score : score;
    score = /[^a-zA-Z0-9]/.test(password) ? ++score : score;
    score = /.*pass.*|.*pass.*|.*123.*|.*098.*|.*qwe.*|.*asd.*|.*zxc.*/.test(password) ? --score : score;
    return score;
}
function getPasswordScore(password) {
    var score = 0;
    if (password.length >= 6) {
        score = /.*[a-z].*/.test(password) ? ++score : score;
        score = /.*[A-Z].*/.test(password) ? ++score : score;
        score = /.*[0-9].*/.test(password) ? ++score : score;
        score = /[^a-zA-Z0-9]/.test(password) ? ++score : score;
    }
    score = /.*pass.*|.*pass.*|.*123.*|.*098.*|.*qwe.*|.*asd.*|.*zxc.*/.test(password) ? --score : score;
    if (password.length > 8) {
        ++score;
    }
    if (password.length > 12) {
        ++score;
    }
    if (password.length > 16) {
        ++score;
    }
    return score;
}
function updatePassword1(inputid1, inputid2, buttonid, submitid, feedbackid, progressid) {
    var inputElement1 = document.getElementById(inputid1);
    var password1 = inputElement1.value;
    var inputElement2 = document.getElementById(inputid2);
    var password2 = inputElement2.value;
    var button = document.getElementById(submitid);
    var score = getPasswordScore(password1);
    if ((password1 == password2) && (score >= 3)) {
        $("#" + submitid).removeClass("disabled");
        $("#" + submitid).prop("disabled", false);
    }
    else {
        $("#" + submitid).addClass("disabled");
        $("#" + submitid).prop("disabled", true);
    }
    if (password1.length == 0) {
        $("#" + buttonid).addClass("disabled");
        $("#" + buttonid).prop("disabled", true);
    }
    else {
        $("#" + buttonid).removeClass("disabled");
        $("#" + buttonid).prop("disabled", false);
    }
    if (progressid != null) {
        var barElement = document.getElementById(progressid);
        var feedbackElement = document.getElementById(feedbackid);
        if (password1.length == 0) {
            $("#" + progressid).attr("style", "width:0%");
        }
        else if (score >= 5) {
            $("#" + progressid).attr("style", "width:100%");
            $("#" + progressid).addClass("bg-success");
            $("#" + progressid).removeClass("bg-warning");
            $("#" + progressid).removeClass("bg-danger");
            $("#" + progressid).addClass("text-light");
            $("#" + progressid).removeClass("text-dark");
            feedbackElement.innerHTML = "Excellent!";
        }
        else if (score >= 3) {
            $("#" + progressid).attr("style", "width:66%");
            $("#" + progressid).removeClass("bg-success");
            $("#" + progressid).addClass("bg-warning");
            $("#" + progressid).removeClass("bg-danger");
            $("#" + progressid).removeClass("text-light");
            $("#" + progressid).addClass("text-dark");
            feedbackElement.innerHTML = "Acceptable, try adding some extra characters.";
        }
        else {
            $("#" + progressid).attr("style", "width:33%");
            $("#" + progressid).removeClass("bg-success");
            $("#" + progressid).removeClass("bg-warning");
            $("#" + progressid).addClass("bg-danger");
            $("#" + progressid).addClass("text-light");
            $("#" + progressid).removeClass("text-dark");
            feedbackElement.innerHTML = "Too short. Add more characters.";
        }
    }
}
function updatePassword2(inputid1, inputid2, buttonid, submitid) {
    var inputElement1 = document.getElementById(inputid1);
    var password1 = inputElement1.value;
    var inputElement2 = document.getElementById(inputid2);
    var password2 = inputElement2.value;
    var button = document.getElementById(submitid);
    var score = getPasswordScore(password2);
    if ((password1 == password2) && (score >= 3)) {
        $("#" + submitid).removeClass("disabled");
        $("#" + submitid).prop("disabled", false);
    }
    else {
        $("#" + submitid).addClass("disabled");
        $("#" + submitid).prop("disabled", true);
    }
    if (password2.length == 0) {
        $("#" + buttonid).addClass("disabled");
        $("#" + buttonid).prop("disabled", true);
    }
    else {
        $("#" + buttonid).removeClass("disabled");
        $("#" + buttonid).prop("disabled", false);
    }
}
function togglePasswordVisibility(id, buttonid) {
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
