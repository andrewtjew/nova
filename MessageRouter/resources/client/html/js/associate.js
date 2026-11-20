var Associate = (function () {
    function Associate() {
    }
    Associate.checkAddNewAssociateForm = function () {
        var inputFirst = document.getElementById("first");
        var inputLast = document.getElementById("last");
        var inputSignin = document.getElementById("signIn");
        var inputId = document.getElementById("id");
        if ((inputFirst.value.length > 0) && (inputLast.value.length > 0) && (inputSignin.value.length > 0) && (inputId.value.length > 0)) {
            $("#submit").removeClass("disabled");
            $("#submit").prop("disabled", false);
        }
        else {
            $("#submit").addClass("disabled");
            $("#submit").prop("disabled", true);
        }
    };
    return Associate;
}());
