var administration;
(function (administration) {
    function toggleCalculatePay() {
        var input = document.getElementById("pay");
        var base = document.getElementById("base");
        var perncentage = document.getElementById("percentage");
        base.disabled = !input.checked;
        perncentage.disabled = !input.checked;
    }
    administration.toggleCalculatePay = toggleCalculatePay;
})(administration || (administration = {}));
