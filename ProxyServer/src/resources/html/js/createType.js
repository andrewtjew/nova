var AlertTypeRequest = (function () {
    function AlertTypeRequest() {
        this.application = document.getElementById("application").value;
        this.type = document.getElementById("type").value;
        this.reminderInterval = Number(document.getElementById("reminderInterval").value);
        this.groupingInterval = Number(document.getElementById("groupingInterval").value);
    }
    return AlertTypeRequest;
}());
window.onload = function () {
};
